package org.jotapdiez.jslackpkg.core.blacklist;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.jotapdiez.jslackpkg.core.entities.Package;

import org.apache.log4j.Logger;
import org.jotapdiez.jslackpkg.core.settings.SettingsManager;

public class BlackListManager
{
	private static final int BL_TYPE_REGEX   = 0;
	private static final int BL_TYPE_PACKAGE = 1;
	
	private Logger logger = Logger.getLogger(getClass().getCanonicalName());

	private Map<Integer, List<Object>> _blacklistCache = new HashMap<Integer, List<Object>>(5);
	private FileWriter _blFileWriter = null;
	
	private File userBlacklistFile = null;
	
	private static BlackListManager _instance = null;
	public static BlackListManager getInstance()
	{
		if (_instance == null)
			_instance = new BlackListManager();
		return _instance;
	}
	
	public BlackListManager()
	{
		userBlacklistFile = new File(SettingsManager.getInstance().getWorkingDir(), "blacklist");
		if (!userBlacklistFile.exists())
		{
			try {
				userBlacklistFile.createNewFile();
				logger.info("Creando archivo de blacklist del usuario en " + userBlacklistFile.getAbsolutePath());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		loadBlackList();
	}
	
	private FileWriter getFileWriter()
	{
		try {
//			if (_blFileWriter == null)
				_blFileWriter = new FileWriter(userBlacklistFile, false);
			return _blFileWriter;
		} catch (IOException e) {
			logger.error("getFileWriter", e);
		}
		return null;		
	}
	
	private void loadBlackList()
	{
		try {
			Scanner blScanner = new Scanner(userBlacklistFile);
			blScanner.useDelimiter("\n");
			
			int type = BL_TYPE_REGEX;
			logger.debug("loadBlackList: " + userBlacklistFile.getAbsolutePath());
			while ( blScanner.hasNext())
			{
				String line = blScanner.nextLine();
				if (line.indexOf("BL_TYPE_REGEX")>0)
					type = BL_TYPE_REGEX;
				else if(line.indexOf("BL_TYPE_PACKAGE")>0)
					type = BL_TYPE_PACKAGE;
				else
				{
					Object value = line;
					
					if (type == BL_TYPE_PACKAGE) // Genero un Package para meter en la lista
					{
						Package pack = new Package();
						String fileName = value.toString().substring(value.toString().indexOf('#')+1).trim();
						pack.setFileName(fileName);
						value = pack;
					}

					logger.debug("Cargando blacklist::item BL_TYPE_"+(type == BL_TYPE_PACKAGE ? "PACKAGE" : "REGEX")+": "+value);
					getList(type).add(value);
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	private List<Object> getList(int type)
	{
		if (_blacklistCache.get(type) == null)
			_blacklistCache.put(type, new LinkedList<Object>());
		return _blacklistCache.get(type);
	}
	
	public void add(Package packageItem)
	{
		getList(BL_TYPE_PACKAGE).add(packageItem);
		flush();
	}
	
	public void add(String value)
	{
		getList(BL_TYPE_REGEX).add(value);
		flush();
	}
	
	public void remove(Package packageItem)
	{
		getList(BL_TYPE_PACKAGE).remove(packageItem); //TODO: Ver si se le pasa una version diferente al que esta guardado
		flush();
	}
	
	private synchronized void flush()
	{
		try {
			FileWriter fw = getFileWriter();
			fw.write("# BL_TYPE_REGEX"+"\n");
			for (Object regex : getList(BL_TYPE_REGEX))
			{
				fw.write(regex.toString()+"\n");
				logger.debug("Escribiendo Blacklist (BL_TYPE_REGEX) " + regex.toString());
			}
			
			fw.write("# BL_TYPE_PACKAGE"+"\n");
			for (Object packageItem : getList(BL_TYPE_PACKAGE))
			{
				Package item = (Package) packageItem;
				fw.write(item.getName() + " # "+item.getFileName()+"\n");
				logger.debug("Escribiendo Blacklist (BL_TYPE_PACKAGE) " + item.getName() + " # "+item.getFileName());
			}
			
			fw.flush();
		} catch (IOException e) {
			logger.error("flush", e);
		}
	}
	
	/**
	 * Chekea si el paquete esta en blacklist o  no
	 * @param packageItem
	 * @return true si el paquete esta en la blacklist
	 */
	public boolean isInBlackList(Package packageItem) {
		if (getList(BL_TYPE_PACKAGE).contains(packageItem))
			return true;
		
		for (Object regex : getList(BL_TYPE_REGEX))
		{
			if (packageItem.getFullName().matches(regex.toString()))
				return true;
		}
		
		return false;
	}
}
