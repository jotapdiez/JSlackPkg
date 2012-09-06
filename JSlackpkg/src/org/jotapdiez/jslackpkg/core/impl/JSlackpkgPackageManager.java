package org.jotapdiez.jslackpkg.core.impl;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.jotapdiez.jslackpkg.core.blacklist.BlackListManager;
import org.jotapdiez.jslackpkg.core.entities.Package;
import org.jotapdiez.jslackpkg.core.entities.Package.STATE;
import org.jotapdiez.jslackpkg.core.interfaces.PackageManager;
import org.jotapdiez.jslackpkg.core.settings.SettingsManager;
import org.jotapdiez.jslackpkg.ui.components.custom.StatusBar;
import org.jotapdiez.jslackpkg.utils.ResourceMap;

public class JSlackpkgPackageManager implements PackageManager
{
	Logger logger = Logger.getLogger(getClass().getCanonicalName());
	
	private final String INSTALLED_PACKAGES_PATH = "/var/log/packages/";

	private final SettingsManager settingsManager = SettingsManager.getInstance();
	
	private Map<String, Package> fullPackages = new HashMap<String, Package>(100);

	public Map<String, Package>  installedPackages = new HashMap<String, Package>(100);
	
	private List<Package> newPackages = null;
	private List<Package> upgradedPackages = null;
	private List<Package> removedPackages = null;
	
	char[] packagesInfo = null;

	File tmpDir = new File(System.getProperty("java.io.tmpdir")+File.separator+"jslackpkg");

	public JSlackpkgPackageManager()
	{
		if (!tmpDir.exists())
			tmpDir.mkdirs();

		loadInstalledPackages();
	}

	/**
	 * Carga la lista con los paquetes instalados
	 */
	private void loadInstalledPackages()
	{
		File fileDir = new File(INSTALLED_PACKAGES_PATH);
		for (File filePackage : fileDir.listFiles())
		{
			List<Package> list = parsePackageInformation(filePackage);
			if (list.size()>0)
			{
				Package packageItem = list.get(0);
				packageItem.setState(Package.STATE.INSTALLED);
				packageItem.setIsInBlackList( BlackListManager.getInstance().isInBlackList(packageItem) );
				installedPackages.put(packageItem.getName(), packageItem);
			}
		}
	}
	
	/**
	 * Retorna la lista completa de paquetes (instalados, eliminados, agregados y actualizados)
	 */
	public List<Package> getAllPackages()
	{
		List<Package> all = new LinkedList<Package>(fullPackages.values()); 
		return all;
	}
	
	/**
	 * Retorna la lista completa de paquetes instalados (previo loadInstalledPackages)
	 */
	public List<Package> getInstalledPackages()
	{
		return new LinkedList<Package>(installedPackages.values());
	}
	
	/**
	 * Retorna la lista completa de paquetes agregados (previo update)
	 */
	public List<Package> getNewPackages()
	{
		return newPackages;
	}
	
	/**
	 * Retorna la lista completa de paquetes actualizados (previo update)
	 */
	public List<Package> getUpgradedPackages()
	{
		return upgradedPackages;
	}
	
	/**
	 * Retorna la lista completa de paquetes eliminados (previo update)
	 */
	public List<Package> getRemovedPackages()
	{
		return removedPackages;
	}
	
	public Package getPackage(String packageFileName)
	{
		if (packagesInfo == null || fullPackages.isEmpty())
			fillPackageList();
		
		return fullPackages.get(packageFileName);
	}
	
	/**
	 * Descarga y parsea PACKAGES.TXT para obtener la lista completa de paquetes
	 */
	public void fillPackageList()
	{
		if (packagesInfo == null)
		{
			packagesInfo = downloadFile("PACKAGES.TXT").toCharArray();
			fullPackages.clear();
		}
		
		if (fullPackages.isEmpty())
		{
			Scanner scannerLinea = new Scanner(new String(packagesInfo));
			scannerLinea.useDelimiter("PACKAGE NAME:");
				
			List<Package> list = parsePackageInformation(scannerLinea);
			
			Iterator<Package> it = list.iterator();
			while (it.hasNext())
			{
				Package packageItem = it.next();
				if (packageItem == null)
					continue;
				
				packageItem.setState(Package.STATE.INSTALLED);				
				fullPackages.put(packageItem.getFullName(), packageItem);
			}
		}
	}
	
	/**
	 * Rellena la informacion de packageItem a travez de parsear info
	 * @param packageItem {@link Package} donde se guardara la informacion
	 * @param info {@link File} a parsear para obtener la informacion del paquete.
	 */
	private List<Package> parsePackageInformation(File info)
	{
		try {
			Scanner scannerLinea = new Scanner(info);
			return parsePackageInformation(scannerLinea);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}
	
//	/**
//	 * Rellena la informacion de packageItem a travez de parsear info ({@link String})
//	 * @param packageItem {@link String} donde se guardara la informacion
//	 * @param info {@link String} a parsear para obtener la informacion del paquete.
//	 */
//	private Package parsePackageInformation(String info)
//	{
//		Scanner scannerLinea = new Scanner(info);
//		return parsePackageInformation(packageItem, scannerLinea);
//	}
		
	/**
	 * Rellena la informacion de packageItem a travez de un {@link Scanner}
	 * @param packageItem {@link String} donde se guardara la informacion
	 * @param scannerLinea {@link Scanner} a utilizar para obtener la informacion del paquete.
	 */
	private List<Package> parsePackageInformation(Scanner scannerLinea)
	{
		List<Package> list = new LinkedList<Package>();
		scannerLinea.useDelimiter("\n");
		
		boolean isInDescription = false;
		String description = "";
		
		Package packageItem = null;
		String descriptionRegexp = "^#NAME#:[ ]?";
		while (scannerLinea.hasNext())
		{
			String itemInfo = scannerLinea.next().replace("PACKAGE", "").trim();
			if (itemInfo.indexOf("FILE LIST:") > -1)
				break;
			
			if (itemInfo.startsWith("NAME:"))
			{
				if (description != null && !description.equals(""))
				{
					packageItem.setDescription(description.trim());
					description = "";
					descriptionRegexp = "^#NAME#:[ ]?";
				}
				
				if (packageItem != null)
					list.add(packageItem);
				packageItem = new Package();
				
				String fileName = itemInfo.replace("NAME:", "").trim(); 
				if (fileName.equals(""))
				{
					logger.debug("parsePackageInformation no package FileName" + itemInfo);
					packageItem = null;
					continue;
				}
				
				packageItem.setFileName(fileName);
				if (packageItem.getName() == null || packageItem.getName().equals(""))
				{
					logger.debug("parsePackageInformation invalid packageItem" + fileName);
					packageItem = null;
					continue;
				}
				descriptionRegexp = descriptionRegexp.replace("#NAME#", packageItem.getName(true));
				
//				System.out.println("descriptionRegexp: " + descriptionRegexp);
			}else if (itemInfo.indexOf("LOCATION:") > -1)
				packageItem.setLocation(itemInfo.replace("LOCATION:", "").trim());
			else if (itemInfo.indexOf("SIZE") > -1)
			{
				itemInfo = itemInfo.replace("SIZE", "").replace(":", "").replace("(", "").replace(")", "");
				if (itemInfo.indexOf("uncomp") >-1 || itemInfo.indexOf("UNCOMP") >-1)
					packageItem.setUncompressedSize(itemInfo.replace("UNCOMPRESSED", "").trim());
				else // if (itemInfo.indexOf("comp") || itemInfo.indexOf("COMP"))
					packageItem.setCompressedSize(itemInfo.replace("COMPRESSED", "").trim());
			}else if (itemInfo.startsWith("DESCRIPTION") )
			{
				isInDescription = true;
			}else if (isInDescription && itemInfo.matches("[^:]*:[\\s]?.*"))
			{
				description += itemInfo.replaceFirst(descriptionRegexp, "")+"\n"; // getName(true):: el true es para retornar el nombre escapeado
			}
		}
		
		if (packageItem != null && description != null && !description.equals(""))
			packageItem.setDescription(description.trim());
		
		if (packageItem != null)
			list.add(packageItem);
		return list;
	}

	/**
	 * Descarga y parsea el ChangeLog.txt del mirror determinado
	 * para rellenar las listas upgradedPackages, newPackages, removedPackages y fullPackages
	 */
	private void parseChangeLog()
	{
		newPackages = new LinkedList<Package>();
		upgradedPackages = new LinkedList<Package>();
		removedPackages = new LinkedList<Package>();
		
		String result = downloadFile("ChangeLog.txt");
		
		Scanner dateScanner = new Scanner(result);
		result = null;
		dateScanner.useDelimiter("\\+--------------------------\\+");
		
		StatusBar.getInstance().setFocusComponentText(ResourceMap.getInstance().getString("statusbar.info.parsing_file.text").replaceFirst("%FILE%", "ChangeLog.txt"));
		StatusBar.getInstance().resetProgress();

		while (dateScanner.hasNext())
		{
			String date = dateScanner.next();
			parseChangeLog(date);
		}
		
		String finalProgressMessage = "";
		finalProgressMessage += ResourceMap.getInstance().getString("statusbar.info.update_cant.text").replaceFirst("%CANT%", String.valueOf(upgradedPackages.size()));
		finalProgressMessage += (finalProgressMessage.equals("") ? "" : " | " ) + ResourceMap.getInstance().getString("statusbar.info.new_cant.text").replaceFirst("%CANT%", String.valueOf(newPackages.size()));
		finalProgressMessage += (finalProgressMessage.equals("") ? "" : " | " ) + ResourceMap.getInstance().getString("statusbar.info.removed_cant.text").replaceFirst("%CANT%", String.valueOf(removedPackages.size()));
	 
		StatusBar.getInstance().setFocusComponentText(finalProgressMessage);
		StatusBar.getInstance().resetProgress();
	}
		
	private boolean parseChangeLog(String changeLog)
	{
		String changeLogParserRegexp = "^([a-z|A-Z]*)\\/(.*)$"; //"^([a-z|A-Z]*)\\/([^:]*):\\s(.*)\\.(.*)$";
		
		 Pattern pattern = Pattern.compile(changeLogParserRegexp, Pattern.MULTILINE);
		 Matcher matcher = pattern.matcher(changeLog);

		 int total = 0;
		 while (matcher.find())
		 {
			 ++total;
		 }
		 
		 StatusBar.getInstance().setTotal(total);
		 
		 matcher = pattern.matcher(changeLog);
		 while (matcher.find())
		 {
			 String locationGroup = matcher.group(1).trim();
			 Package packageItem = new Package();
			 
//			 logger.debug("* ===================================== *");
//			 logger.debug("locationGroup: " + locationGroup);
//			 logger.debug("matcher.group(2): " + matcher.group(2));
			 
			 String[] split = matcher.group(2).split(":");
			 
			 packageItem.setFileName(split[0]);
//			 logger.debug("split[0]: " + split[0]);

			 if (packageItem.getFullName().equals(""))
			 {
				 logger.error("PackageItem invalido: " + split[0]);
				 continue;
			 }
			 
			 { //Copio la descripcion y si no esta en esta lista no se muestra nada porque es viejo
				 Package packageItemTmp = getPackage(packageItem.getFullName());
//				 logger.debug("packageItem.getFullName(): " + packageItem.getFullName());
				 if (packageItemTmp == null || !packageItemTmp.equalsExact(packageItem))
				 {
					 logger.debug("continue - FullName: " + packageItem.getFullName());
					 continue;
				 }
				 
				 packageItem.setDescription( packageItemTmp.getDescription() );
				 packageItem.setCompressedSize(packageItemTmp.getCompressedSize());
				 packageItem.setUncompressedSize(packageItemTmp.getUncompressedSize());
			 }
			 
			 String actionGroup = null;
			 String extraGroup = null;
			 
			 if (split.length>1)
			 {
				 actionGroup = split[1].trim();
				 
				 if (actionGroup.split(".").length>1)
				 {
					 String[] extraSplit = actionGroup.split("."); 
					 actionGroup = extraSplit[0].trim();
					 extraGroup = extraSplit[1].trim();
				 }else if (actionGroup.endsWith("."))
					 actionGroup = actionGroup.replaceAll("\\.", "");
			 }
			 StatusBar.getInstance().increaseProgress();

			 packageItem.setLocation(locationGroup);
			 
			 if (actionGroup == null)
				 continue;
			 
			 if (actionGroup.equals("Upgraded"))
			 {
				 if (!addUpgradePackage(packageItem))
					 continue;
			 }else if (actionGroup.equals("Added"))
			 {
				 if (!addNewPackage(packageItem))
					 continue;
			 }else if (actionGroup.equals("Removed"))
			 {
				 if (!addRemovePackage(packageItem))
					 continue;
			 }else if (actionGroup.equals("Rebuilt"))
			 {
				 if (installedPackages.containsValue(packageItem)) // SOLO chekea el nombre
				 {
					 if (!addUpgradePackage(packageItem))
						 continue;
				}else
				{
					if (!addNewPackage(packageItem))
						 continue;
				}
			 }else if (actionGroup.equals("Reverted"))
			 {
				 packageItem.setState(Package.STATE.UNKNOWN);
				 //TODO: Hacer que?
				 logger.info("ActionGroup Reverted (Sin uso) - Paquete: " + packageItem.getFileName());
			 }else
			 {
				 packageItem.setState(Package.STATE.UNKNOWN);
				 logger.info("ActionGroup desconocido: " + actionGroup + " | Paquete: " + packageItem.getFileName()); //TODO: A archivo de lenguajes
			 }
			 
			 if (extraGroup != null && !extraGroup.equals(""))
				 logger.info(extraGroup + "(extraGroup) sin uso. " + actionGroup + " | Paquete: " + packageItem.getFileName()); //TODO: A archivo de lenguajes
		 }
		 
		 return true;
	}
	private boolean parseChangeLogOld(String changeLog)
	{
		String changeLogParserRegexp = "^([a-z|A-Z]*)\\/([^:]*):\\s([^\\.]*)\\.(.*)$"; //"^([a-z|A-Z]*)\\/([^:]*):\\s(.*)\\.(.*)$";
		
		 Pattern pattern = Pattern.compile(changeLogParserRegexp, Pattern.MULTILINE);
		 Matcher matcher = pattern.matcher(changeLog);

		 int total = 0;
		 while (matcher.find())
		 {
			 ++total;
		 }
		 
		 StatusBar.getInstance().setTotal(total);
		 
		 matcher = pattern.matcher(changeLog);
		 while (matcher.find())
		 {
			 String locationGroup = matcher.group(1);
			 Package packageItem = new Package();
			 packageItem.setFileName(matcher.group(2));
			 if (packageItem.getFullName().equals(""))
			 {
				 logger.debug("PackageItem invalido: " + matcher.group(2));
				 continue;
			 }
			 
			 { //Copio la descripcion y si no esta en esta lista no se muestra nada porque es viejo
				 Package packageItemTmp = getPackage(packageItem.getFullName());
				 logger.debug("packageItem.getFullName(): " + packageItem.getFullName());
				 if (packageItemTmp == null || !packageItemTmp.equalsExact(packageItem))
					 continue;
				 
				 packageItem.setDescription( packageItemTmp.getDescription() );
				 packageItem.setCompressedSize(packageItemTmp.getCompressedSize());
				 packageItem.setUncompressedSize(packageItemTmp.getUncompressedSize());
			 }
			 
			 String actionGroup = matcher.group(3);
			 String extraGroup = matcher.group(4);
			 
			 if (locationGroup != null)
				 locationGroup = locationGroup.trim();
			 if (actionGroup != null)
				 actionGroup = actionGroup.trim();
			 if (extraGroup != null)
				 extraGroup = extraGroup.trim();
			 
			 StatusBar.getInstance().increaseProgress();

			 packageItem.setLocation(locationGroup);
			 
			 if (actionGroup.equals("Upgraded"))
			 {
				 if (!addUpgradePackage(packageItem))
					 continue;
			 }else if (actionGroup.equals("Added"))
			 {
				 if (!addNewPackage(packageItem))
					 continue;
			 }else if (actionGroup.equals("Removed"))
			 {
				 if (!addRemovePackage(packageItem))
					 continue;
			 }else if (actionGroup.equals("Rebuilt"))
			 {
				 if (installedPackages.containsValue(packageItem)) // SOLO chekea el nombre
				 {
					 if (!addUpgradePackage(packageItem))
						 continue;
				}else
				{
					if (!addNewPackage(packageItem))
						 continue;
				}
			 }else if (actionGroup.equals("Reverted"))
			 {
				 packageItem.setState(Package.STATE.UNKNOWN);
				 //TODO: Hacer que?
				 logger.debug("ActionGroup Reverted (Sin uso) - Paquete: " + packageItem.getFileName());
			 }else
			 {
				 packageItem.setState(Package.STATE.UNKNOWN);
				 logger.debug("ActionGroup desconocido: " + actionGroup + " | Paquete: " + packageItem.getFileName()); //TODO: A archivo de lenguajes
			 }
			 
			 if (extraGroup != null && !extraGroup.equals(""))
				 logger.debug(extraGroup + "(extraGroup) sin uso. " + actionGroup + " | Paquete: " + packageItem.getFileName()); //TODO: A archivo de lenguajes
		 }
		 
		 return true;
	}

	/**
	 * Chekea si hay alguna version de el paquete instalada en el sistema.
	 * Es para saber si un paquete en upgrade, en nuestra maquina es new (ninguna version instalada) o upgrade (alguna version instalada)
	 * @param packageItem Paquete con los datos para chekear
	 * @return true si existe alguna version instalada, false en caso contrario
	 */
	private boolean hasSomeVersionInstalled(Package packageItem)
	{
		Package packageItemTmp = installedPackages.get(packageItem.getName());
		if ( packageItemTmp == null)
			return false;
		return true;
	}
	
	/**
	 * Chekea si el packageItem esta en la lista de los paquete del current (osea, si es la ultima version)
	 * @param packageItem
	 * @return
	 */
	public boolean isSlackwareCurrentPackage(Package packageItem)
	{
		 Package packageItemTmp = getPackage(packageItem.getFullName());
		 
		 if (packageItemTmp == null || !packageItemTmp.equalsExact(packageItem))
		 {
//			 System.out.println(packageItem.getFullName() + " no es la ultima version del paquete (Ver PACKAGES.TXT)");
			 return false;
		 }
		 
		 return true;
	}
	
	/**
	 * Agrega un paquete a la lista de paquetes a actualizar.(paquetes en modo upgrade)
	 * @param packageItem
	 * @return
	 */
	private boolean addUpgradePackage(Package packageItem)
	{
		if (!hasSomeVersionInstalled(packageItem)) // Si no hay ninguna version instalada, por mas que sea un upgrade se debe instalar como nuevo
			return addNewPackage(packageItem);
		
		if (isPackageInstalled(packageItem.getFullName()))
			return false;
		
		packageItem.setState(Package.STATE.TO_UPGRADE);
		if (upgradedPackages.contains(packageItem))
		 {
			 System.out.println("upgradedPackages ya tenia " +packageItem.getFullName());
			 return false;
//			 return false; // Si upgrade ya tenia un paquete con el mismo nombre (NOMBRE) retorno false para que NO siga con el resto de los dias
		 }
		 
		upgradedPackages.add(packageItem);
		return true;
	}
	
	/**
	 * Agrega un paquete a la lista de paquetes a instalar  (Paquetes nuevos o nunca instalados).
	 * @param packageItem
	 * @return
	 */
	private boolean addNewPackage(Package packageItem)
	{
		if (hasSomeVersionInstalled(packageItem)) // Si hay alguna version instalada, por mas que sea un added se debe actualizar
			return addUpgradePackage(packageItem);
		
		packageItem.setState(Package.STATE.TO_INSTALL);
		if (newPackages.contains(packageItem))
		{
			System.out.println("newPackages ya tenia " +packageItem.getFullName());
			return false;
		}
		 
		newPackages.add(packageItem);
		return true;
	}
	
	/**
	 * Agrega un paquete a la lista de paquetes a eliminar.
	 * @param packageItem
	 * @return
	 */
	private boolean addRemovePackage(Package packageItem)
	{
		if (!hasSomeVersionInstalled(packageItem)) // Si no hay ninguna version instalada, por mas que sea un remove no hay nada que borrar
			return true;
		
		//TODO: Si la version instalada NO es la misma que packageItem tengo que poner para borrar cual?
		packageItem.setState(Package.STATE.TO_DELETE);
		if (removedPackages.contains(packageItem))
		{
			System.out.println("removedPackages ya tenia " +packageItem.getFullName());
			return false;
		}
		 
		removedPackages.add(packageItem);
		return true;
	}
	
	@Override
	public void update() {
		parseChangeLog();
	}

	@Override
	public boolean upgrade(Package packageItem) {
		return false;
	}

	@Override
	public boolean remove(Package packageItem) {
		final List<String> procArgs = new LinkedList<String>();
		procArgs.add("kdesu");
		procArgs.add("removepkg");
		
		String packageLocalFile = packageItem.getFileName();
		procArgs.add(packageLocalFile);
		
		System.out.println("procArgs: " + procArgs.toString());

		Runnable t = new Runnable() {
			@Override
			public void run() {
				ProcessBuilder pb = new ProcessBuilder(procArgs);
				pb.redirectErrorStream();
				try {
			
					Process proc = pb.start();
					
					BufferedReader in = new BufferedReader(new InputStreamReader(proc.getInputStream()));
					
			        String inputLine;
			        while ((inputLine = in.readLine()) != null) 
			        	System.out.println("in:: " + inputLine); //result.append(inputLine);
			        in.close();
			        
			        in = new BufferedReader(new InputStreamReader(proc.getErrorStream()));
			        while ((inputLine = in.readLine()) != null) 
			        	System.out.println("err:: " + inputLine); //result.append(inputLine);
			        in.close();
			        
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		};
		
		Thread tt = new Thread(t);
	    tt.start();
	    while (tt.getState()!=Thread.State.TERMINATED)
	    {
	    	try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
	    }
	    
	    boolean isInstalled = isPackageInstalled(packageItem.getFileName());
	    if (!isInstalled)
	    	packageItem.setState(STATE.UNINSTALLED);
	    
	    if (installedPackages.containsValue(packageItem))
	    	installedPackages.remove(packageItem.getName());
		return !isInstalled;
	}

	@Override
	public boolean install(Package packageItem) {
		final List<String> procArgs = new LinkedList<String>();
		procArgs.add("kdesu");
		procArgs.add("installpkg");
		
		System.out.println("Install");
		
		//Download package
		String packageLocalFile = downloadPackage(packageItem);
		
//		procArgs.add("-warn"); //dry-run. PARA DEBUG
		
		procArgs.add(packageLocalFile);
		
		System.out.println("procArgs: " + procArgs.toString());
		
		Runnable t = new Runnable() {
			
			@Override
			public void run() {
				ProcessBuilder pb = new ProcessBuilder(procArgs);
				pb.redirectErrorStream();
				try {
			
					Process proc = pb.start();
					
					BufferedReader in = new BufferedReader(new InputStreamReader(proc.getInputStream()));
					
			        String inputLine;
			        while ((inputLine = in.readLine()) != null) 
			        	System.out.println("in:: " + inputLine); //result.append(inputLine);
			        in.close();
			        
			        in = new BufferedReader(new InputStreamReader(proc.getErrorStream()));
			        while ((inputLine = in.readLine()) != null) 
			        	System.out.println("err:: " + inputLine); //result.append(inputLine);
			        in.close();
			        
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		};
		
		Thread tt = new Thread(t);
	    tt.start();
	    while (tt.getState()!=Thread.State.TERMINATED)
	    {
	    	try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
	    }
	    
	    boolean isInstalled = isPackageInstalled(packageItem.getFileName());
	    if (isInstalled)
	    	packageItem.setState(STATE.INSTALLED);
	    
	    if (!installedPackages.containsValue(packageItem))
	    	installedPackages.put(packageItem.getName(), packageItem);

	    if (!upgradedPackages.contains(packageItem))
	    	upgradedPackages.remove(packageItem);
	    else if (!newPackages.contains(packageItem))
	    	newPackages.remove(packageItem);

		return isInstalled;
	}

	private boolean isPackageInstalled(String packageFileName)
	{
		File installedPackage = new File(INSTALLED_PACKAGES_PATH, packageFileName);
		boolean isInstalled = installedPackage.exists();
		
//		System.out.println(installedPackage.getAbsolutePath());
//		System.out.println("isPackageInstalled: " + isInstalled);
		
		return isInstalled;
	}
	
	/**
	 * Descarga un paquete del mirror seleccionado en la configuracion
	 * @param remoteFile {@link Package} a descargar
	 * @param fileName 
	 * @return el path absoluto donde se descargo el paquete
	 */
	private String downloadPackage(Package packageItem)
	{
		try {
			StatusBar.getInstance().setFocusComponentText(ResourceMap.getInstance().getString("statusbar.info.downloading.text").replaceFirst("%FILE%", packageItem.getFileName()));
//			String state = "Descargando: ";
//			infoPanel.setState(state);
//			infoPanel.updateProgress(0);
//			logger.debug("downloadFile:"+remoteFile+" to "+localFile);

			URL url = new URL(settingsManager.getOption(SettingsManager.Section.REPO, "mirror")+"/slackware/"+packageItem.getLocation()+"/"+packageItem.getFileName());
			URLConnection conexion = url.openConnection();
			conexion.connect();

			StatusBar.getInstance().setTotal(conexion.getContentLength());
			
//			int lenghtOfFile = conexion.getContentLength();
//			infoPanel.setTotalPB(lenghtOfFile);
			InputStream input = new BufferedInputStream(url.openStream());
			
			File local = new File(settingsManager.getWorkingDir(), packageItem.getFileName());
			if (!local.exists())
				local.createNewFile();
			
			OutputStream output = new FileOutputStream(local);

			byte data[] = new byte[8192];

			long total = 0;
			
			int count;
			while ((count = input.read(data)) != -1) {
				total += count;
				StatusBar.getInstance().increaseProgress((int) total);
//				infoPanel.updateProgress((int) total);
//				logger.debug(total+"/"+lenghtOfFile);
				output.write(data, 0, count);
			}

			StatusBar.getInstance().resetProgress();
			StatusBar.getInstance().resetText();
			output.flush();
			output.close();
			input.close();
			
			return local.getAbsolutePath();
		} catch (MalformedURLException e) {
			e.printStackTrace();
			logger.error("MalformedURLException", e);
		} catch (IOException e) {
			e.printStackTrace();
			logger.error("IOException", e);
		}
		return null;
	}
	
	@Override
	public void clean() {

	}

	@Override
	public void search() {

	}

	@Override
	public void info() {

	}

	/**
	 * Descarga el archivo del mirror seleccionado en la configuracion
	 * @param file {@link String} con el nombre del archivo a descargar
	 * @return {@link String} con el contenido del archivo descargado
	 */
	public String downloadFile(String file) {
		try {
			StatusBar.getInstance().setFocusComponentText(ResourceMap.getInstance().getString("statusbar.info.downloading.text").replaceFirst("%FILE%", file));

			URL mirrorContext = new URL(settingsManager.getOption(SettingsManager.Section.REPO, "mirror"));
			URL mirrorFile = new URL(mirrorContext, file);
	        URLConnection mirror = mirrorFile.openConnection();
	        
	        StatusBar.getInstance().setTotal(mirror.getContentLength());
	        
	        BufferedReader in = new BufferedReader(new InputStreamReader(mirror.getInputStream()));
	        
	        StringBuffer result = new StringBuffer();
	        String inputLine;
	        int total = 0;
	        while ((inputLine = in.readLine()) != null) 
	        {
	        	result.append(inputLine+"\n");
	        	total += inputLine.length();
	        	StatusBar.getInstance().increaseProgress(total);
	        }
	        in.close();
	        StatusBar.getInstance().resetProgress();
	        StatusBar.getInstance().resetText();
	        return result.toString();
		} catch (MalformedURLException ex) {
			logger.error("downloadFile", ex);
		} catch (IOException ex) {
			logger.error("downloadFile", ex);
		}
		return "";
	}
}
