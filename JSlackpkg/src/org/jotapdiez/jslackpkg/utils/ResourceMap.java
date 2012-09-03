package org.jotapdiez.jslackpkg.utils;

import java.awt.Color;
import java.awt.Font;
import java.io.InputStream;
import java.util.Properties;

import javax.swing.ImageIcon;

public class ResourceMap
{
	public static String		DEFAULT_LANGUAJE	= "es_AR.properties";

//	public static URL			noPhoto				= ResourceMap.class.getResource("/resources/noPhoto.jpg");

//	public static ImageIcon		mainIcon			= new ImageIcon(ResourceMap.class.getResource("/resources/images/icon-mainProgram.png"));

//	public static ImageIcon		arrowLeftIcon		= new ImageIcon(ResourceMap.class.getResource("/resources/icon-arrowLeft.png"));
//	public static ImageIcon		arrowRightIcon		= new ImageIcon(ResourceMap.class.getResource("/resources/icon-arrowRight.png"));
//	public static ImageIcon		addIcon				= new ImageIcon(ResourceMap.class.getResource("/resources/icon-add.png"));
//	public static ImageIcon		okIcon				= new ImageIcon(ResourceMap.class.getResource("/resources/icon-ok.png"));
//	public static ImageIcon		searchIcon			= new ImageIcon(ResourceMap.class.getResource("/resources/icon-search.png"));
//	public static ImageIcon		cleanIcon			= new ImageIcon(ResourceMap.class.getResource("/resources/images/icon-clean.png"));
//	public static ImageIcon		deleteIcon			= new ImageIcon(ResourceMap.class.getResource("/resources/icon-delete.png"));
	public static ImageIcon		closeIcon			= new ImageIcon(ResourceMap.class.getResource("/resources/images/ico-close.png"));
	public static ImageIcon		updateIcon			= new ImageIcon(ResourceMap.class.getResource("/resources/images/ico-update.png"));
	public static ImageIcon		upgradeIcon			= new ImageIcon(ResourceMap.class.getResource("/resources/images/ico-upgrade.png"));
	public static ImageIcon		installIcon			= new ImageIcon(ResourceMap.class.getResource("/resources/images/ico-install.png"));
	public static ImageIcon		installedIcon		= new ImageIcon(ResourceMap.class.getResource("/resources/images/ico-installed.png"));
	public static ImageIcon		cleanIcon			= new ImageIcon(ResourceMap.class.getResource("/resources/images/ico-clean.png"));
	public static ImageIcon		settingsIcon		= new ImageIcon(ResourceMap.class.getResource("/resources/images/ico-settings.png"));

	public static ImageIcon		addIcon				= new ImageIcon(ResourceMap.class.getResource("/resources/images/ico-add.png"));
	public static ImageIcon		removeIcon			= new ImageIcon(ResourceMap.class.getResource("/resources/images/ico-remove.png"));

	public static ImageIcon		searchAddIcon		= new ImageIcon(ResourceMap.class.getResource("/resources/images/ico-search-add.png"));
	public static ImageIcon		searchRemoveIcon	= new ImageIcon(ResourceMap.class.getResource("/resources/images/ico-search-remove.png"));

	private static ResourceMap	instance			= null;

	public static ResourceMap getInstance()
	{
		if (instance == null)
			instance = new ResourceMap();
		return instance;
	}

	Properties	_p	= null;

	public ResourceMap()
	{
		try
		{
			InputStream is = loadFile(DEFAULT_LANGUAJE);

			_p = new Properties();
			_p.loadFromXML(is);
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public ResourceMap(String fileName)
	{
		try
		{
			InputStream is = loadFile(fileName);

			_p = new Properties();
			_p.loadFromXML(is);
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public void changeLanguaje(String fileName)
	{
		InputStream is;
		try
		{
			is = loadFile(fileName);
			_p = new Properties();
			_p.loadFromXML(is);
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	private InputStream loadFile(String fileName) throws Exception
	{
		InputStream is = getClass().getResourceAsStream("/lang/" + fileName);

		if (is == null)
			throw new Exception("El archivo " + fileName + " no existe.");

		return is;
	}

	/**
	 * ********************************************************************
	 * METODOS PARA EL USO MISMO DE ResourceMap
	 ** ********************************************************************/

	/**
	 * 
	 * @param key
	 * @return
	 */
	public String getString(String key)
	{
		if (_p == null)
			return null;
		return _p.getProperty(key);
	}

	public Color getColor(String key)
	{
		if (_p == null)
			return null;
		String[] color_split = getString(key).split(",");
		if (color_split.length <= 2)
			return null;
		int r = Integer.parseInt(color_split[0].trim());
		int g = Integer.parseInt(color_split[1].trim());
		int b = Integer.parseInt(color_split[2].trim());

		return new Color(r, g, b);
	}

	public Font getFont(String key)
	{
		if (_p == null)
			return null;
		String font = getString(key).trim();
		return new Font(font, 0, 0);
	}
}
