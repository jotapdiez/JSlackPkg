package org.jotapdiez.jslackpkg.core.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.jotapdiez.jslackpkg.core.blacklist.BlackListManager;
import org.jotapdiez.jslackpkg.core.entities.Package;
import org.jotapdiez.jslackpkg.core.entities.Package.STATE;
import org.jotapdiez.jslackpkg.core.settings.SettingsManager;
import org.jotapdiez.jslackpkg.ui.components.custom.StatusBar;
import org.jotapdiez.jslackpkg.utils.HTTPUtils;
import org.jotapdiez.jslackpkg.utils.ResourceMap;

public class JSlackpkgPackageManager extends PackageManagerImpl
{
	private Logger			logger					= Logger.getLogger(getClass().getCanonicalName());

	private final String	INSTALLED_PACKAGES_PATH	= "/var/log/packages/";

	/**
	 * Carga la lista con los paquetes instalados
	 */
	public void loadInstalledPackages()
	{
		File fileDir = new File(INSTALLED_PACKAGES_PATH);
		for (File filePackage : fileDir.listFiles())
		{
			List<Package> list = parsePackageInformation(filePackage);
			if (list.size() > 0)
			{
				Package packageItem = list.get(0);
				packageItem.setState(Package.STATE.INSTALLED);
				packageItem.setIsInBlackList(BlackListManager.getInstance().isInBlackList(packageItem));
				getInstalledPackagesMap().put(packageItem.getName(), packageItem);
			}
		}
	}

	/**
	 * Descarga y parsea el ChangeLog.txt del mirror determinado
	 * para rellenar las listas upgradedPackages, newPackages, removedPackages y fullPackages
	 */
	private void parseChangeLog()
	{
		resetNewUpdateRemovePackages();

		String result = HTTPUtils.getFileContent(settingsManager.getOption(SettingsManager.Section.REPO, "mirror"), "ChangeLog.txt");

		Scanner dateScanner = new Scanner(result);
		result = null;
		dateScanner.useDelimiter("\\+--------------------------\\+");

		StatusBar.getInstance().setFocusComponentText(ResourceMap.getInstance().getString("statusbar.info.parsing_file.text").replaceFirst("%FILE%", "ChangeLog.txt"));
		StatusBar.getInstance().resetProgress();

		while (dateScanner.hasNext())
		{
			String date = dateScanner.next();
			/*boolean allPackagesSkipped = */parseChangeLog(date);
//			if (allPackagesSkipped)
//				break; // No continuo porque es una fecha vieja
		}

		String finalProgressMessage = "";
		finalProgressMessage += ResourceMap.getInstance().getString("statusbar.info.update_cant.text").replaceFirst("%CANT%", String.valueOf(getUpgradedPackages().size()));
		finalProgressMessage += (finalProgressMessage.equals("") ? "" : " | ") + ResourceMap.getInstance().getString("statusbar.info.new_cant.text").replaceFirst("%CANT%", String.valueOf(getNewPackages().size()));
		finalProgressMessage += (finalProgressMessage.equals("") ? "" : " | ") + ResourceMap.getInstance().getString("statusbar.info.removed_cant.text").replaceFirst("%CANT%", String.valueOf(getRemovedPackages().size()));

		StatusBar.getInstance().setFocusComponentText(finalProgressMessage);
		StatusBar.getInstance().resetProgress();
	}

	private boolean parseChangeLog(String changeLog)
	{
		String changeLogParserRegexp = "^([a-z|A-Z]*)\\/(.*)$"; // "^([a-z|A-Z]*)\\/([^:]*):\\s(.*)\\.(.*)$";

		Pattern pattern = Pattern.compile(changeLogParserRegexp, Pattern.MULTILINE);
		Matcher matcher = pattern.matcher(changeLog);

		int skipped = 0;
		int error = 0;
		
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

			// logger.debug("* ===================================== *");
			// logger.debug("locationGroup: " + locationGroup);
			// logger.debug("matcher.group(2): " + matcher.group(2));

			String[] split = matcher.group(2).split(":");

			packageItem.setFileName(split[0]);
			// logger.debug("split[0]: " + split[0]);

			if (packageItem.getFullName().equals(""))
			{
				logger.error("PackageItem invalido: " + split[0]);
				++error;
				continue;
			}

			{ // Copio la descripcion y si no esta en esta lista no se muestra nada porque es viejo
				Package packageItemTmp = getPackage(packageItem.getFullName());
				// logger.debug("packageItem.getFullName(): " + packageItem.getFullName());
				if (packageItemTmp == null || !packageItemTmp.equalsExact(packageItem))
				{
					logger.debug("continue - FullName: " + packageItem.getFullName());
					++skipped;
					continue;
				}

				packageItem.setDescription(packageItemTmp.getDescription());
				packageItem.setCompressedSize(packageItemTmp.getCompressedSize());
				packageItem.setUncompressedSize(packageItemTmp.getUncompressedSize());
			}

			String actionGroup = null;
			String extraGroup = null;

			if (split.length > 1)
			{
				actionGroup = split[1].trim();

				if (actionGroup.split(".").length > 1)
				{
					String[] extraSplit = actionGroup.split(".");
					actionGroup = extraSplit[0].trim();
					extraGroup = extraSplit[1].trim();
				} else if (actionGroup.endsWith("."))
					actionGroup = actionGroup.replaceAll("\\.", "");
			}
			StatusBar.getInstance().increaseProgress();

			packageItem.setLocation(locationGroup);

			if (actionGroup == null)
			{
				logger.debug("actionGroup == null - FullName: " + packageItem.getFullName());
				continue;
			}

			if (actionGroup.equals("Upgraded"))
			{
				if (!addUpgradePackage(packageItem))
					continue;
			} else if (actionGroup.equals("Added"))
			{
				if (!addNewPackage(packageItem))
					continue;
			} else if (actionGroup.equals("Removed"))
			{
				if (!addRemovePackage(packageItem))
					continue;
			} else if (actionGroup.equals("Rebuilt"))
			{
				if (getInstalledPackagesMap().containsValue(packageItem)) // SOLO chekea el nombre
				{
					if (!addUpgradePackage(packageItem))
						continue;
				} else
				{
					if (!addNewPackage(packageItem))
						continue;
				}
			} else if (actionGroup.equals("Reverted"))
			{
				packageItem.setState(Package.STATE.UNKNOWN);
				// TODO: Hacer que?
				logger.info("ActionGroup Reverted (Sin uso) - Paquete: " + packageItem.getFileName());
			} else
			{
				packageItem.setState(Package.STATE.UNKNOWN);
				logger.info("ActionGroup desconocido: " + actionGroup + " | Paquete: " + packageItem.getFileName()); // TODO: A archivo de lenguajes
			}

			if (extraGroup != null && !extraGroup.equals(""))
				logger.info(extraGroup + "(extraGroup) sin uso. " + actionGroup + " | Paquete: " + packageItem.getFileName()); // TODO: A archivo de
																																// lenguajes
		}

		logger.debug("Total: "+total+" | Skipped: "+skipped+" | Error: "+error);
		return (total == skipped);
	}

	/**
	 * Agrega un paquete a la lista de paquetes a actualizar.(paquetes en modo upgrade)
	 * 
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
		if (getUpgradedPackages().contains(packageItem))
		{
			System.out.println("upgradedPackages ya tenia " + packageItem.getFullName());
			return false;
			// return false; // Si upgrade ya tenia un paquete con el mismo nombre (NOMBRE) retorno false para que NO siga con el resto de los dias
		}

		getUpgradedPackages().add(packageItem);
		return true;
	}

	/**
	 * Agrega un paquete a la lista de paquetes a instalar (Paquetes nuevos o nunca instalados).
	 * 
	 * @param packageItem
	 * @return
	 */
	private boolean addNewPackage(Package packageItem)
	{
		if (hasSomeVersionInstalled(packageItem)) // Si hay alguna version instalada, por mas que sea un added se debe actualizar
			return addUpgradePackage(packageItem);

		packageItem.setState(Package.STATE.TO_INSTALL);
		if (getNewPackages().contains(packageItem))
		{
			System.out.println("newPackages ya tenia " + packageItem.getFullName());
			return false;
		}

		getNewPackages().add(packageItem);
		return true;
	}

	/**
	 * Agrega un paquete a la lista de paquetes a eliminar.
	 * 
	 * @param packageItem
	 * @return
	 */
	private boolean addRemovePackage(Package packageItem)
	{
		if (!hasSomeVersionInstalled(packageItem)) // Si no hay ninguna version instalada, por mas que sea un remove no hay nada que borrar
			return true;

		// TODO: Si la version instalada NO es la misma que packageItem tengo que poner para borrar cual?
		packageItem.setState(Package.STATE.TO_DELETE);
		if (getRemovedPackages().contains(packageItem))
		{
			logger.debug("removedPackages ya tenia " + packageItem.getFullName());
			return false;
		}

		getRemovedPackages().add(packageItem);
		return true;
	}

	@Override
	public void update()
	{
		parseChangeLog();
	}

	@Override
	public boolean upgrade(Package packageItem)
	{
		logger.debug("Upgrading package: " + packageItem.getFullName());

		String packageLocalFile = HTTPUtils.downloadFile(settingsManager.getOption(SettingsManager.Section.REPO, "mirror") + "/slackware/" + packageItem.getLocation() + "/", packageItem.getFileName());

		if (packageLocalFile == null || packageLocalFile.equals(""))
			return false;
		
		StatusBar.getInstance().setFocusComponentText("Actualizando "+packageItem.getName() + " a "+packageItem.getFullName()); //TODO: Al archivo de lenguajes
		StatusBar.getInstance().startIndeterminated();
		boolean succesfull = runPackageCommand("upgradepkg", packageLocalFile);
		
		if (!succesfull)
			return false; // No fue actualizado
		
		boolean isInstalled = isPackageInstalled(packageItem.getFileName());
		if (isInstalled)
		{
			packageItem.setState(STATE.INSTALLED);
			StatusBar.getInstance().setFocusComponentText(packageItem.getFullName() + " eliminado correctamente"); //TODO: Al archivo de lenguajes
		}else
			StatusBar.getInstance().setFocusComponentText("Error al instalar "+packageItem.getFullName()); //TODO: Al archivo de lenguajes
		StatusBar.getInstance().stopIndeterminated();
		
		if (getInstalledPackagesMap().containsValue(packageItem))
		{
			getInstalledPackagesMap().remove(packageItem.getName());
			getInstalledPackagesMap().put(packageItem.getName(), packageItem);
		}
		
		if (getUpgradedPackages().contains(packageItem))
			getUpgradedPackages().remove(packageItem);
		
		setChanged();
		notifyObservers(packageItem, MODE.UPGRADED);
		
		return false;
	}

	@Override
	public boolean upgrade(List<Package> list)
	{
		//TODO: Implementar
		return false;
	}
	
	@Override
	public boolean remove(Package packageItem)
	{
		logger.debug("Removing package: " + packageItem.getName());

		String packageLocalFile = packageItem.getName();
		StatusBar.getInstance().setFocusComponentText("Eliminando "+packageItem.getFullName()); //TODO: Al archivo de lenguajes
		StatusBar.getInstance().startIndeterminated();

		boolean succesfull = runPackageCommand("removepkg", packageLocalFile);

		if (!succesfull)
			return false; // No fue eliminado
		
		boolean isInstalled = isPackageInstalled(packageItem.getFileName());
		if (!isInstalled)
			packageItem.setState(STATE.UNINSTALLED);

		if (getInstalledPackagesMap().containsValue(packageItem))
			getInstalledPackagesMap().remove(packageItem.getName());
		
		//TODO: Logica para que pase a la lista de new la ultima version (si estaba en upgrade, y que se borre de upgrade)
		
		setChanged();
		notifyObservers(packageItem, MODE.REMOVED);
		
		if (!isInstalled)
		{
			StatusBar.getInstance().setFocusComponentText(packageItem.getFullName() + " eliminado correctamente"); //TODO: Al archivo de lenguajes
		}else
			StatusBar.getInstance().setFocusComponentText("Error al eliminar "+packageItem.getFullName()); //TODO: Al archivo de lenguajes
		StatusBar.getInstance().stopIndeterminated();
		
		return !isInstalled;
	}
	
	@Override
	public boolean remove(List<Package> list)
	{
		logger.debug("Removing packages: " + list.toString());
		
		String packageLocalFile = "";
		for (Package item : list)
		{
			packageLocalFile += (packageLocalFile.equals("") ? "" : " ") + item.getFullName(); 
		}
		
		boolean succesfull = runPackageCommand("removepkg", packageLocalFile);

		if (!succesfull)
			return false; // No fue eliminado
		
		boolean isInstalled = false;
		
		for (Package item : list)
		{
			isInstalled = isPackageInstalled(item.getFileName());
			if (!isInstalled)
				item.setState(STATE.UNINSTALLED);

			if (getInstalledPackagesMap().containsValue(item))
			{
				getInstalledPackagesMap().remove(item.getName());
				
				//TODO: Logica para que pase a la lista de new la ultima version (si estaba en upgrade, y que se borre de upgrade)
			}
			
			setChanged();
			notifyObservers(item, MODE.REMOVED);
		}
		
		return !isInstalled;
	}

	@Override
	public boolean install(List<Package> list)
	{
		//TODO: Implementar
		
		return false;
	}
	
	@Override
	public boolean install(Package packageItem)
	{
		logger.debug("Installing package: " + packageItem.getFullName());

		// Download package
		String packageLocalFile = HTTPUtils.downloadFile(settingsManager.getOption(SettingsManager.Section.REPO, "mirror") + "/slackware/" + packageItem.getLocation() + "/", packageItem.getFileName());

		logger.debug(packageLocalFile);

		StatusBar.getInstance().setFocusComponentText("Instalando "+packageItem.getFullName()); //TODO: Al archivo de lenguajes
		StatusBar.getInstance().startIndeterminated();
		boolean succesfull = runPackageCommand("installpkg", packageLocalFile);

		if (!succesfull)
			return false; // No fue instalado
		
		boolean isInstalled = isPackageInstalled(packageItem.getFileName());
		if (isInstalled)
		{
			packageItem.setState(STATE.INSTALLED);
		}

		new File(packageLocalFile).delete();
		if (!getInstalledPackagesMap().containsValue(packageItem))
			getInstalledPackagesMap().put(packageItem.getName(), packageItem);

		if (!getUpgradedPackages().contains(packageItem))
			getUpgradedPackages().remove(packageItem);
		else if (!getNewPackages().contains(packageItem))
			getNewPackages().remove(packageItem);

		setChanged();
		notifyObservers(packageItem, MODE.INSTALLED);
		
		if (isInstalled)
		{
			StatusBar.getInstance().setFocusComponentText(packageItem.getFullName() + " instalado correctamente"); //TODO: Al archivo de lenguajes
		}else
			StatusBar.getInstance().setFocusComponentText("Error al instalar "+packageItem.getFullName()); //TODO: Al archivo de lenguajes
		StatusBar.getInstance().stopIndeterminated();
		return isInstalled;
	}

	private boolean runPackageCommand(String proccess, String packageName)
	{
		final List<String> procArgs = new LinkedList<String>();
		procArgs.add("kdesu");
		procArgs.add("-t");
		procArgs.add(proccess);
		procArgs.add(packageName);

		// procArgs.add("-warn"); //dry-run. PARA DEBUG
		logger.debug("Command: " + procArgs.toString());
		
		final List<String> resultInputStream = new LinkedList<String>();
		final List<String> resultErrorStream = new LinkedList<String>();
		
		Runnable t = new Runnable()
		{
			@Override
			public void run()
			{
				ProcessBuilder pb = new ProcessBuilder(procArgs);
				pb.redirectErrorStream();
				try
				{
					Process proc = pb.start();

					BufferedReader in = new BufferedReader(new InputStreamReader(proc.getInputStream()));

					String inputLine;
					while ((inputLine = in.readLine()) != null)
					{
						resultInputStream.add(inputLine);
						logger.debug("in:: " + inputLine); // result.append(inputLine);
					}
					in.close();

					in = new BufferedReader(new InputStreamReader(proc.getErrorStream()));
					while ((inputLine = in.readLine()) != null)
					{
						resultErrorStream.add(inputLine);
						logger.debug("err:: " + inputLine); // result.append(inputLine);
					}
					in.close();

				} catch (IOException e)
				{
					e.printStackTrace();
				}
			}
		};

		Thread tt = new Thread(t);
		tt.start();
		while (tt.getState() != Thread.State.TERMINATED)
		{
			try
			{
				Thread.sleep(50);
			} catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
		
		//TODO: Implementar la comprobacion de si termino bien o no
		for (String line : resultInputStream)
		{
			if (line.indexOf("No such package")>0 && line.indexOf("Can't remove.")>0)
				return false;
		}
		
		return true;
	}
	
	private boolean isPackageInstalled(String packageFileName)
	{
		File installedPackage = new File(INSTALLED_PACKAGES_PATH, packageFileName);
		boolean isInstalled = installedPackage.exists();

		// System.out.println(installedPackage.getAbsolutePath());
		// System.out.println("isPackageInstalled: " + isInstalled);

		return isInstalled;
	}

	@Override
	public void clean()
	{

	}

	@Override
	public void search()
	{

	}

	@Override
	public void info()
	{

	}
}
