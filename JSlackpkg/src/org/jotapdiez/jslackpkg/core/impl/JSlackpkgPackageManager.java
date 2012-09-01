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

import org.jotapdiez.jslackpkg.core.entities.Package;
import org.jotapdiez.jslackpkg.core.entities.Package.STATE;
import org.jotapdiez.jslackpkg.core.interfaces.PackageManager;
import org.jotapdiez.jslackpkg.core.settings.SettingsManager;
import org.jotapdiez.jslackpkg.ui.components.custom.StatusBar;

public class JSlackpkgPackageManager implements PackageManager
{
	private final String INSTALLED_PACKAGES_PATH = "/var/log/packages/";

	private final SettingsManager settingsManager = SettingsManager.getInstance();
	
	private Map<String, Package> fullPackages = new HashMap<String, Package>(100);

	public Map<String, Package>  installedPackages = new HashMap<String, Package>(100);
	
	private List<Package> newPackages = null;
	private List<Package> upgradedPackages = null;
	private List<Package> removedPackages = null;
	
	char[] changeLog = null;
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
			StatusBar.getInstance().setFocusComponentText("Descargando PACKAGES.TXT"); //TODO: A archivo de lenguajes
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
			
//			while (scannerLinea.hasNext())
//			{
//				Package packageItem = parsePackageInformation("PACKAGE NAME:" + scannerLinea.next());
//				
//				if (packageItem == null)
//					continue;
//				
//				packageItem.setState(Package.STATE.INSTALLED);				
//				fullPackages.put(packageItem.getName(), packageItem);
//			}
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
		//scannerLinea.skip("FILE LIST");
		
		boolean isInDescription = false;
		String description = "";
//		String realName = null;
		
		Package packageItem = null;
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
				}
				
				if (packageItem != null)
					list.add(packageItem);
				packageItem = new Package();
				
				String fileName = itemInfo.replace("NAME:", "").trim(); 
				if (fileName.equals(""))
					continue;
				
				packageItem.setFileName(fileName);
//				System.out.println("NAME: " + itemInfo.replace("NAME:", "").trim());
			}else if (itemInfo.indexOf("LOCATION:") > -1)
				packageItem.setLocation(itemInfo.replace("LOCATION:", "").trim());
			else if (itemInfo.indexOf("SIZE") > -1)
			{
				itemInfo = itemInfo.replace("SIZE", "").replace(":", "").replace("(", "").replace(")", "");
				if (itemInfo.indexOf("uncomp") >-1 || itemInfo.indexOf("UNCOMP") >-1)
					packageItem.setSizeUncompressed(itemInfo.replace("UNCOMPRESSED", "").trim());
				else // if (itemInfo.indexOf("comp") || itemInfo.indexOf("COMP"))
					packageItem.setSizeCompressed(itemInfo.replace("COMPRESSED", "").trim());
			}else if (itemInfo.startsWith("DESCRIPTION") )
			{
				isInDescription = true;
				//description = description.replaceAll("^[^:]*:\\s", "");
				
			}else if (isInDescription && itemInfo.matches("[^:]*:[\\s]?.*"))
			{
//				if (realName == null)
//				{
//					Pattern pattern = Pattern.compile("([^:]*):[\\s]?.*");
//					Matcher matcher = pattern.matcher(itemInfo);
//					if (matcher.find())
//					{
//						realName = matcher.group(1);
//						packageItem.setRealName(realName);
//					}
//				}
//				Pattern pattern = Pattern.compile("[^:]*:[\\s]?");
//				Matcher matcher = pattern.matcher(itemInfo);
//				description += matcher.replaceAll("")+"\n";
				description += itemInfo.replaceFirst("^"+packageItem.getName()+":[ ]?", "")+"\n";
			}
			
//			System.out.println(scannerLinea.next());
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
		
		if (changeLog == null)
		{
			String result = downloadFile("ChangeLog.txt");
			changeLog = result.toCharArray();
			
//			System.out.println("============================= CHangeLog.txt:\n"+result);
			Scanner dateScanner = new Scanner(result);
//			Scanner dateScanner = null;
//			try {
//				dateScanner = new Scanner(new File("/home/juanpablo/ChangeLog.txt"));
//			} catch (FileNotFoundException e) {
//				e.printStackTrace();
//			}
			result = null;
			dateScanner.useDelimiter("\\+--------------------------\\+");
			
			StatusBar.getInstance().setFocusComponentText("Interpretando ChangeLog.txt"); //TODO: A archivo de lenguajes
			StatusBar.getInstance().resetProgress();

			while (dateScanner.hasNext())
			{
				String date = dateScanner.next();
//				System.out.println("=====================================================================================Date:\n" + date);
//				parseChangeLogDateScanner(date);
				boolean validDate = parseChangeLogDateRegexp2(date);
//				if (!validDate)
//					break;
			}
			
			String finalProgressMessage = "";
			finalProgressMessage += upgradedPackages.size()+" actualizacion/es"; //TODO: A archivo de lenguajes
			finalProgressMessage += (finalProgressMessage.equals("") ? "" : " | " ) + newPackages.size() + " nuevo/s"; //TODO: A archivo de lenguajes
			finalProgressMessage += (finalProgressMessage.equals("") ? "" : " | " ) + removedPackages.size() + " eliminado/s"; //TODO: A archivo de lenguajes
		 
			StatusBar.getInstance().setFocusComponentText(finalProgressMessage);
			StatusBar.getInstance().resetProgress();
		}
	}
		
	private boolean parseChangeLogDateRegexp2(String changeLog)
	{
		String changeLogParserRegexp = "^([a-z|A-Z]*)\\/([^:]*):\\s(.*)\\.(.*)$"; //"^([^\\/])\\/([^:]*):\\s(.*)\\.(.*)$";
		
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
//				 System.out.println("packageItem invalido: " + matcher.group(2)); // TODO: Ver si no se escapo alguno valido
				 continue;
			 }
			 
			 String actionGroup = matcher.group(3);
			 String extraGroup = matcher.group(4);
			 
			 if (locationGroup != null)
				 locationGroup = locationGroup.trim();
			 if (actionGroup != null)
				 actionGroup = actionGroup.trim();
			 if (extraGroup != null)
				 extraGroup = extraGroup.trim();
			 
			 { //Copio la descripcion y si no esta en esta lista no se muestra nada porque es viejo
				 if (!isSlackwareCurrentPackage(packageItem))
					 continue;
				 packageItem.setDescription( getPackageDescription(packageItem) );
			 }
			 
			 StatusBar.getInstance().increaseProgress();

//			 {
//				 Package packageItemTmp = installedPackages.get(packageItem.getName());
//				 if ( packageItemTmp != null && packageItemTmp.equalsExact(packageItem))
//				 {
//					 System.out.println(packageItem.getFullName() + " ya esta instalado"); //TODO: A archivo de lenguajes
//					 continue;
//				 }
//			 }
//			 
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
//				 System.out.println(packageFileName.toString() + " (in " + locationGroup+") was " + actionGroup); //TODO: A archivo de lenguajes
			 }else
			 {
				 packageItem.setState(Package.STATE.UNKNOWN);
//				 System.out.println(packageFileName.toString() + " (in " + locationGroup+") was " + actionGroup); //TODO: A archivo de lenguajes
			 }
			 
//			 if (extraGroup != null && !extraGroup.equals(""))
//				 System.out.println(packageFileName.toString() + " (in " + locationGroup+") has extra data: " + extraGroup); //TODO: A archivo de lenguajes
			 
//			 fullPackages.put(packageItem.getName(), packageItem);
//			 StatusBar.getInstance().increaseProgress();
		 }
		 
		 return true;
	}
	
	private boolean hasSomeVersionInstalled(Package packageItem)
	{
		Package packageItemTmp = installedPackages.get(packageItem.getName());
		if ( packageItemTmp == null)
			return false;
		return true;
	}
	
	private String getPackageDescription(Package packageItem)
	{
		Package packageItemTmp = getPackage(packageItem.getFullName());
		if (packageItemTmp == null)
			return "";
		return packageItemTmp.getDescription();
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
		
		String packageRemoteFile = settingsManager.getOption(SettingsManager.Section.REPO, "mirror")+"/slackware/"+packageItem.getLocation()+"/"+packageItem.getFileName();
		System.out.println("packageRemoteFile: " + packageRemoteFile);
		
		String packageLocalFile = packageItem.getFileName();
		System.out.println("packageLocalFile: " + packageLocalFile);
		
		//Download package
		packageLocalFile = downloadPackage(packageRemoteFile, packageLocalFile);
		
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
	
	private String downloadPackage(String remoteFile, String fileName)
	{
		try {
			StatusBar.getInstance().setFocusComponentText("Descargando paquete "+fileName); //TODO: A archivo de lenguajes
			String state = "Descargando: ";
//			infoPanel.setState(state);
//			infoPanel.updateProgress(0);
//			logger.debug("downloadFile:"+remoteFile+" to "+localFile);

			URL url = new URL(remoteFile);
			URLConnection conexion = url.openConnection();
			conexion.connect();

			StatusBar.getInstance().setTotal(conexion.getContentLength());
			
			int lenghtOfFile = conexion.getContentLength();
//			infoPanel.setTotalPB(lenghtOfFile);
			InputStream input = new BufferedInputStream(url.openStream());
			
			File local = new File(tmpDir, fileName);
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
//			logger.error("MalformedURLException", e);
		} catch (IOException e) {
			e.printStackTrace();
//			logger.error("IOException", e);
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
	 * Descarga el archivo file del mirror seleccionado en la configuracion
	 * @param file {@link String} con el nombre del archivo a descargar
	 * @return {@link String} con el contenido del archivo descargado
	 */
	public String downloadFile(String file) {
		try {
			StatusBar.getInstance().setFocusComponentText("Descargando "+file); //TODO: A archivo de lenguajes

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
			ex.printStackTrace();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return "";
	}
}
