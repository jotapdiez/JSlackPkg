package org.jotapdiez.jslackpkg.core.settings;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.prefs.BackingStoreException;
import java.util.prefs.InvalidPreferencesFormatException;
import java.util.prefs.Preferences;

import org.jotapdiez.jslackpkg.JSlackpkg;

import sun.misc.IOUtils;

public class SettingsManager
{
	public enum Section
	{
		GENERAL   ("general"  ),
		INTERFACE ("interface"),
		REPO	  ("repo"     ),
		OTHERS    ("others"   );
		
		private String _name = null;
		
		private Section(String name) {
			_name = name;
		}
		
		public String getName()
		{
			return _name;
		}
		
		public static Section fromName(String name)
		{
			if (name.equals(GENERAL))
				return GENERAL;
			else if (name.equals(INTERFACE))
				return INTERFACE;
			else if (name.equals(OTHERS))
				return OTHERS;
			
			return null;
		}
	}
	
	private static Class<JSlackpkg> packageForNode = JSlackpkg.class;
	private Preferences prefs = null;
	private Preferences actualNode = null;
	private static SettingsManager _instance = null;
	
	public static SettingsManager getInstance()
	{
		if (_instance == null)
			_instance = new SettingsManager();
		return _instance;
	}
	
	private SettingsManager() {
		String homeUserPath = System.getProperty("user.home");
		
		File jslackpkgDir = new File(homeUserPath, ".jslackpkg");
		if (!jslackpkgDir.exists())
			jslackpkgDir.mkdir();
		
		System.out.println("jslackpkgDir: " + jslackpkgDir.getAbsolutePath());
		
		File userConfigFile = new File(jslackpkgDir, "config.xml");
		if (!userConfigFile.exists())
		{
			System.out.println("Escribiendo archivo de configuracion en el usuario.");
			saveDefaults(jslackpkgDir.getAbsolutePath());
		}
		System.out.println("userConfigFile: " + userConfigFile.getAbsolutePath());
		
//		String path = getClass().getResource("/resources/config").getPath();
		load(jslackpkgDir.getAbsolutePath());
	}
	
	public void load(String parentPath)
	{
		// Create an input stream on a file
		InputStream is = null;
		try {
		    is = new BufferedInputStream(new FileInputStream(parentPath+"/config.xml"));
		} catch (FileNotFoundException e) {
		}

		// Import preference data
		try {
		    Preferences.importPreferences(is);
			prefs = Preferences.userNodeForPackage(packageForNode);
		} catch (InvalidPreferencesFormatException e) {
		} catch (IOException e) {
		}
	}
	
	public String[] getKeys(Section section)
	{
		String oldNodeName = null;
		if (getNode() != null)
			oldNodeName = getNode().name();
		
		setNode(section);
		String[] result = getKeys();
		if (oldNodeName != null)
			setNode(oldNodeName);
		return result;
	}
	
	public String[] getKeys()
	{
		try {
			return getNode().keys();
		} catch (BackingStoreException e) {
			e.printStackTrace();
		}
		return new String[]{};
	}
	
	public String getOption(Section section, String key)
	{
		String result = null;
		
		if (getNode() == null)
		{
			setNode(section);
			result = getOption(key);
		}else if(!getNode().name().equals(section.getName()))
		{
			Preferences prevNode = getNode();
			setNode(section);
			result = getOption(key);
			setNode(prevNode);
		}else
		{
			result = getOption(key);
		}
		
		return result;
	}
	
	public String getOption(String key)
	{
		return getNode().get(key, "");
	}
	
	public void setNode(Section section)
	{
		setNode(section.getName());
	}
	
	public void setNode(String sectionName)
	{
		setNode(prefs.node(sectionName));
	}

	public void setNode(Preferences node)
	{
		actualNode = node;
	}
	
	private Preferences getNode() {
		return actualNode;
	}

	public Section[] getSections()
	{
		return Section.values();
	}

	public static void saveDefaults(String parentPath)
	{
      // Retrieve the user preference node for the package java.lang
		Preferences prefs = Preferences.userNodeForPackage(packageForNode);
		try {
			prefs.clear();
		} catch (BackingStoreException e1) {
			e1.printStackTrace();
		}
		
		try {
			// Save some values
			Preferences generalPrefs = prefs.node(Section.GENERAL.getName());
			generalPrefs.clear();
			generalPrefs.put("languaje", "es_AR");        // String
			generalPrefs.putBoolean("showSplash", false); // boolean
		
			Preferences interfacePrefs = prefs.node(Section.INTERFACE.getName());
			interfacePrefs.clear();
			interfacePrefs.put("lag", "substance");        // String
			interfacePrefs.putBoolean("useTabs", true);    // boolean
		
			Preferences repoPrefs = prefs.node(Section.REPO.getName());
			repoPrefs.clear();
			repoPrefs.put("mirror", "http://slackware.mirrors.tds.net/pub/slackware/slackware-current/");        // String
			
			Preferences othersPrefs = prefs.node(Section.OTHERS.getName());
			othersPrefs.clear();
			othersPrefs.put("apiToUse", "default");        // String
			othersPrefs.putBoolean("checkUpdates", true);  // boolean
		} catch (BackingStoreException e1) {
			e1.printStackTrace();
		}
		
		try {
			System.out.println(new File(parentPath+"/config.xml").getAbsolutePath());
			prefs.exportSubtree(new FileOutputStream(parentPath+"/config.xml"));
		} catch (IOException e) {
			e.printStackTrace();
		} catch (BackingStoreException e) {
			e.printStackTrace();
		}
	}
}

