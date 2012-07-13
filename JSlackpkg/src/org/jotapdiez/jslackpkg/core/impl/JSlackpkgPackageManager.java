package org.jotapdiez.jslackpkg.core.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jotapdiez.jslackpkg.core.entities.Package;
import org.jotapdiez.jslackpkg.core.interfaces.PackageManager;
import org.jotapdiez.jslackpkg.core.settings.SettingsManager;
import org.jotapdiez.jslackpkg.ui.components.custom.StatusBar;

public class JSlackpkgPackageManager implements PackageManager
{
	private final SettingsManager settingsManager = SettingsManager.getInstance();
	
	private Map<String, Package> fullPackages = new HashMap<String, Package>(100);

	public List<Package> installedPackages = new LinkedList<Package>();
	
	private List<Package> newPackages = new LinkedList<Package>();
	private List<Package> upgradedPackages = new LinkedList<Package>();
	private List<Package> removedPackages = new LinkedList<Package>();
	
	char[] changeLog = null;
	char[] packagesInfo = null;

	/**
	 * Class para encapsular el parseo del nombre del paquete
	 * @author juanpablo
	 */
	public class PackageFileName
	{
		char[] _name = null;
		char[] _verion = null;
		char[] _arch = null;
		char[] _build = null;
		String _nameCache = null;
		
		public PackageFileName(String fileName)
		{
			fileName = fileName.replaceAll("\\.t.z", "");
			Pattern pattern = Pattern.compile("^(.*)-(.*)-(.*)-(.*)$");
			Matcher matcher = pattern.matcher(fileName);

			if (matcher.find())
			{
				 _name = matcher.group(1).toCharArray();
				 _verion = matcher.group(2).toCharArray();
				 _arch = matcher.group(3).toCharArray();
				 _build = matcher.group(4).toCharArray();
			}			
		}
		
		@Override
		public String toString() {
			if (_nameCache == null && _name != null)
				_nameCache = new String(_name);
			return _nameCache;
		}
	}
	public JSlackpkgPackageManager() {
		loadInstalledPackages();
	}

	/**
	 * Carga la lista con los paquetes instalados
	 */
	private void loadInstalledPackages()
	{
		String dir = "/var/log/packages/";
		
		File fileDir = new File(dir);
		for (File filePackage : fileDir.listFiles())
		{
			Package packageItem = new Package();
			packageItem.setState(Package.STATE.INSTALLED);
			parsePackageInformation(packageItem, filePackage);
			installedPackages.add(packageItem);
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
		return installedPackages;
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
	private void fillPackageList()
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
				
			while (scannerLinea.hasNext())
			{
				Package packageItem = new Package();
				
				parsePackageInformation(packageItem, "PACKAGE NAME:" + scannerLinea.next());
				packageItem.setState(Package.STATE.INSTALLED);				
				fullPackages.put(packageItem.getRealName(), packageItem);
			}
		}
	}
	
	/**
	 * Rellena la informacion de packageItem a travez de parsear info
	 * @param packageItem {@link Package} donde se guardara la informacion
	 * @param info {@link File} a parsear para obtener la informacion del paquete.
	 */
	private void parsePackageInformation(Package packageItem, File info)
	{
		try {
			Scanner scannerLinea = new Scanner(info);
			parsePackageInformation(packageItem, scannerLinea);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Rellena la informacion de packageItem a travez de parsear info ({@link String})
	 * @param packageItem {@link String} donde se guardara la informacion
	 * @param info {@link String} a parsear para obtener la informacion del paquete.
	 */
	private void parsePackageInformation(Package packageItem, String info)
	{
		Scanner scannerLinea = new Scanner(info);
		parsePackageInformation(packageItem, scannerLinea);
	}
		
	/**
	 * Rellena la informacion de packageItem a travez de un {@link Scanner}
	 * @param packageItem {@link String} donde se guardara la informacion
	 * @param scannerLinea {@link Scanner} a utilizar para obtener la informacion del paquete.
	 */
	private void parsePackageInformation(Package packageItem, Scanner scannerLinea)
	{
		scannerLinea.useDelimiter("\n");
		//scannerLinea.skip("FILE LIST");
		
		boolean isInDescription = false;
		String description = "";
//		String realName = null;
		while (scannerLinea.hasNext())
		{
			String itemInfo = scannerLinea.next().replace("PACKAGE", "").trim();
			if (itemInfo.indexOf("FILE LIST:") > -1)
				break;
			
			if (itemInfo.startsWith("NAME:"))
			{
				String fileName = itemInfo.replace("NAME:", "").trim(); 
				PackageFileName tmpFileNameResolver = new PackageFileName(fileName);
				
				packageItem.setFileName(fileName);
				packageItem.setRealName(tmpFileNameResolver._name);
				packageItem.setVersion(tmpFileNameResolver._verion);
				packageItem.setArch(tmpFileNameResolver._arch);
				packageItem.setBuildNumber(tmpFileNameResolver._build);
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
				description += itemInfo.replaceFirst("^"+packageItem.getRealName()+":[ ]?", "")+"\n";
			}
			
//			System.out.println(scannerLinea.next());
		}
		
		if (description != null && !description.equals(""))
			packageItem.setDescription(description.trim());
	}

	/**
	 * Descarga y parsea el ChangeLog.txt del mirror determinado
	 * para rellenar las listas upgradedPackages, newPackages, removedPackages y fullPackages
	 */
	private void parseChangeLog()
	{
		String changeLogParserRegexp = "^([^\\/])\\/([^:]*):\\s(.*)\\.(.*)$";
//		changeLogParserRegexp = "^([^\\/])\\/.*$";
		
		if (changeLog == null)
		{
			StatusBar.getInstance().setFocusComponentText("Descargando ChangeLog.txt"); //TODO: A archivo de lenguajes
			changeLog = downloadFile("ChangeLog.txt").toCharArray();
		}
		
		 Pattern pattern = Pattern.compile(changeLogParserRegexp, Pattern.MULTILINE);
		 Matcher matcher = pattern.matcher(new String(changeLog));

		 StatusBar.getInstance().setFocusComponentText("Interpretando ChangeLog.txt"); //TODO: A archivo de lenguajes
		 StatusBar.getInstance().resetProgress();
		 StatusBar.getInstance().startIndeterminated();
		 while (matcher.find())
		 {
			 String locationGroup = matcher.group(1);
			 PackageFileName packageFileName = new PackageFileName(matcher.group(2));
			 String actionGroup = matcher.group(3);
			 String extraGroup = matcher.group(4);
			 
			 if (locationGroup != null)
				 locationGroup = locationGroup.trim();
			 if (actionGroup != null)
				 actionGroup = actionGroup.trim();
			 if (extraGroup != null)
				 extraGroup = extraGroup.trim();
			 
//			 System.out.println("packageNameGroup: " + packageFileName.toString()); // DEBUG:
			 Package packageItem = getPackage(packageFileName.toString());
			 if (packageItem == null)
			 {
				 StatusBar.getInstance().reduceTotal();
				 continue;
			 }
			 
			 if (installedPackages.contains(packageItem))
//			 {
//				 System.out.println(packageFileName.toString() + " ya esta instalado"); //TODO: A archivo de lenguajes
				 continue;
//			 }
			 
			 System.out.println("packageFileName: " + packageFileName.toString() + " | packageItem: " + packageItem.getFileName() + " | actionGroup: " + actionGroup); //TODO: A archivo de lenguajes
			 if (actionGroup.equals("Upgraded") || actionGroup.equals("Rebuilt"))
			 {
				 packageItem.setState(Package.STATE.TO_UPGRADE);
				 upgradedPackages.add(packageItem);
			 }else if (actionGroup.equals("Added"))
			 {
				 packageItem.setState(Package.STATE.TO_INSTALL);
				 newPackages.add(packageItem);
			 }else if (actionGroup.equals("Removed"))
			 {
				 packageItem.setState(Package.STATE.TO_DELETE);
				 removedPackages.add(packageItem);
			 }else if (actionGroup.equals("Reverted"))
			 {
				 packageItem.setState(Package.STATE.UNKNOWN);
				 //TODO: Hacer que?
				 System.out.println(packageFileName.toString() + " (in " + locationGroup+") was " + actionGroup); //TODO: A archivo de lenguajes
			 }else
			 {
				 packageItem.setState(Package.STATE.UNKNOWN);
				 System.out.println(packageFileName.toString() + " (in " + locationGroup+") was " + actionGroup); //TODO: A archivo de lenguajes
			 }
			 
			 if (extraGroup != null && !extraGroup.equals(""))
				 System.out.println(packageFileName.toString() + " (in " + locationGroup+") has extra data: " + extraGroup); //TODO: A archivo de lenguajes
			 
			 fullPackages.put(packageItem.getRealName(), packageItem);
			 StatusBar.getInstance().increaseProgress();
			 try {
				Thread.sleep(0, 100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		 }
		 
		 String finalProgressMessage = "";
//		 if (upgradedPackages.size()>0)
			 finalProgressMessage += upgradedPackages.size()+" actualizacion/es"; //TODO: A archivo de lenguajes
//		 if (newPackages.size()>0)
			 finalProgressMessage += (finalProgressMessage.equals("") ? "" : " | " ) + newPackages.size() + " nuevo/s"; //TODO: A archivo de lenguajes
//		 if (removedPackages.size()>0)
			 finalProgressMessage += (finalProgressMessage.equals("") ? "" : " | " ) + removedPackages.size() + " eliminado/s"; //TODO: A archivo de lenguajes
		 
//		 if (!finalProgressMessage.equals(""))
//			 finalProgressMessage = "Hay "+ finalProgressMessage;
		 
		 StatusBar.getInstance().setFocusComponentText(finalProgressMessage);
		 StatusBar.getInstance().stopIndeterminated();
	}
	
	@Override
	public void update() {
		parseChangeLog();
	}

	@Override
	public void upgrade() {

	}

	@Override
	public void install() {

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
			URL mirrorContext = new URL(settingsManager.getOption(SettingsManager.Section.REPO, "mirror"));
			URL mirrorFile = new URL(mirrorContext, file);
	        URLConnection mirror = mirrorFile.openConnection();
	        
	        BufferedReader in = new BufferedReader(new InputStreamReader(mirror.getInputStream()));
	        
	        StringBuffer result = new StringBuffer();
	        String inputLine;
	        while ((inputLine = in.readLine()) != null) 
	        	result.append(inputLine+"\n");
	        in.close();
	        
	        return result.toString();
		} catch (MalformedURLException ex) {
			ex.printStackTrace();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return "";
	}
}
