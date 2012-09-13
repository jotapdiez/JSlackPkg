package org.jotapdiez.jslackpkg.core.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.apache.log4j.Logger;
import org.jotapdiez.jslackpkg.core.entities.Package;
import org.jotapdiez.jslackpkg.core.interfaces.PackageManager;
import org.jotapdiez.jslackpkg.core.observers.PackageObservable;
import org.jotapdiez.jslackpkg.core.settings.SettingsManager;
import org.jotapdiez.jslackpkg.utils.HTTPUtils;

public abstract class PackageManagerImpl extends PackageObservable implements PackageManager
{
	protected final SettingsManager	settingsManager		= SettingsManager.getInstance();

	private Map<String, Package>	fullPackages		= new HashMap<String, Package>(100);
	private Map<String, Package>	installedPackages	= new HashMap<String, Package>(100);

	private List<Package>			newPackages			= null;
	private List<Package>			upgradedPackages	= null;
	private List<Package>			removedPackages		= null;

	protected Logger				logger				= Logger.getLogger(getClass().getCanonicalName());

	char[]							packagesInfo		= null;
	protected File					tmpDir				= new File(System.getProperty("java.io.tmpdir") + File.separator + "jslackpkg");

	public abstract void loadInstalledPackages();

	public PackageManagerImpl()
	{
		if (!tmpDir.exists())
			tmpDir.mkdirs();

//		loadInstalledPackages();
	}

	protected void resetNewUpdateRemovePackages()
	{
		newPackages = new LinkedList<Package>();
		upgradedPackages = new LinkedList<Package>();
		removedPackages = new LinkedList<Package>();
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
	 * Retorna la lista completa de paquetes instalados (previo loadInstalledPackages)
	 */
	public Map<String, Package> getInstalledPackagesMap()
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
	public void fillPackageList()
	{
		if (packagesInfo == null)
		{
			packagesInfo = HTTPUtils.getFileContent(settingsManager.getOption(SettingsManager.Section.REPO, "mirror"), "PACKAGES.TXT").toCharArray();
			// packagesInfo = downloadFile("PACKAGES.TXT").toCharArray();
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
	 * Por medio de un {@link File} genera una lista de {@link Package} con toda la informacion que encuentre
	 * 
	 * @param info
	 *        {@link File} a parsear para obtener la informacion del paquete.
	 */
	protected List<Package> parsePackageInformation(File info)
	{
		try
		{
			Scanner scannerLinea = new Scanner(info);
			return parsePackageInformation(scannerLinea);
		} catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
		return null;
	}

	// /**
	// * Rellena la informacion de packageItem a travez de parsear info ({@link String})
	// * @param packageItem {@link String} donde se guardara la informacion
	// * @param info {@link String} a parsear para obtener la informacion del paquete.
	// */
	// private Package parsePackageInformation(String info)
	// {
	// Scanner scannerLinea = new Scanner(info);
	// return parsePackageInformation(packageItem, scannerLinea);
	// }

	/**
	 * Por medio de un {@link Scanner} genera una lista de {@link Package} con toda la informacion que encuentre
	 * 
	 * @param scannerLinea
	 *        {@link Scanner} a utilizar para obtener la informacion de el/los paquete/s.
	 */
	protected List<Package> parsePackageInformation(Scanner scannerLinea)
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

				// System.out.println("descriptionRegexp: " + descriptionRegexp);
			} else if (itemInfo.indexOf("LOCATION:") > -1)
				packageItem.setLocation(itemInfo.replace("LOCATION:", "").trim());
			else if (itemInfo.indexOf("SIZE") > -1)
			{
				itemInfo = itemInfo.replace("SIZE", "").replace(":", "").replace("(", "").replace(")", "");
				if (itemInfo.indexOf("uncomp") > -1 || itemInfo.indexOf("UNCOMP") > -1)
					packageItem.setUncompressedSize(itemInfo.replace("UNCOMPRESSED", "").trim());
				else
					// if (itemInfo.indexOf("comp") || itemInfo.indexOf("COMP"))
					packageItem.setCompressedSize(itemInfo.replace("COMPRESSED", "").trim());
			} else if (itemInfo.startsWith("DESCRIPTION"))
			{
				isInDescription = true;
			} else if (isInDescription && itemInfo.matches("[^:]*:[\\s]?.*"))
			{
				description += itemInfo.replaceFirst(descriptionRegexp, "") + "\n"; // getName(true):: el true es para retornar el nombre escapeado
			}
		}

		if (packageItem != null && description != null && !description.equals(""))
			packageItem.setDescription(description.trim());

		if (packageItem != null)
			list.add(packageItem);
		return list;
	}

	/**
	 * Chekea si hay alguna version de el paquete instalada en el sistema.
	 * Es para saber si un paquete en upgrade, en nuestra maquina es new (ninguna version instalada) o upgrade (alguna version instalada)
	 * 
	 * @param packageItem
	 *        Paquete con los datos para chekear
	 * @return true si existe alguna version instalada, false en caso contrario
	 */
	protected boolean hasSomeVersionInstalled(Package packageItem)
	{
		Package packageItemTmp = getInstalledPackagesMap().get(packageItem.getName());
		if (packageItemTmp == null)
			return false;
		return true;
	}

	/**
	 * Chekea si el packageItem esta en la lista de los paquete del current (osea, si es la ultima version)
	 * 
	 * @param packageItem
	 * @return
	 */
	protected boolean isSlackwareCurrentPackage(Package packageItem)
	{
		Package packageItemTmp = getPackage(packageItem.getFullName());

		if (packageItemTmp == null || !packageItemTmp.equalsExact(packageItem))
		{
			// System.out.println(packageItem.getFullName() + " no es la ultima version del paquete (Ver PACKAGES.TXT)");
			return false;
		}

		return true;
	}
}
