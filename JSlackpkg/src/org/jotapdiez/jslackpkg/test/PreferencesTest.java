package org.jotapdiez.jslackpkg.test;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.prefs.BackingStoreException;
import java.util.prefs.InvalidPreferencesFormatException;
import java.util.prefs.Preferences;

import org.jotapdiez.jslackpkg.JSlackpkg;

public class PreferencesTest {

	public enum Sections
	{
		GENERAL("general"),
		INTERFACE("interface"),
		OTHERS("others");
		
		String _name = null;
		private Sections(String name) {
			_name = name;
		}
		public String getName()
		{
			return _name;
		}
		
		public static Sections fromName(String name)
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
	static Class<JSlackpkg> packageForNode = JSlackpkg.class;
	Preferences prefs = null;
	Preferences actualNode = null;
	
	public PreferencesTest() {
		String path = getClass().getResource("/resources/config").getPath();
		load(path);
	}
	
	
	public void load(String parentPath)
	{
		// Create an input stream on a file
		InputStream is = null;
		try {
		    is = new BufferedInputStream(new FileInputStream(parentPath+"/default.xml"));
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
	
	public String[] getKeys(Sections section)
	{
		String oldNodeName = actualNode.name();
		setNode(section);
		String[] result = getKeys();
		setNode(oldNodeName);
		return result;
	}
	
	public String[] getKeys()
	{
		try {
			return actualNode.keys();
		} catch (BackingStoreException e) {
			e.printStackTrace();
		}
		return new String[]{};
	}
	
	public void setNode(Sections section)
	{
		setNode(section.getName());
	}
	
	public void setNode(String sectionName)
	{
		actualNode = prefs.node(sectionName);
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
		
		// Save some values
		Preferences generalPrefs = prefs.node("general");
		generalPrefs.put("languaje", "es_AR");        // String
		generalPrefs.putBoolean("showSplash", false); // boolean
		
		Preferences interfacePrefs = prefs.node("interface");
		interfacePrefs.put("lag", "substance");        // String
		interfacePrefs.putBoolean("useTabs", true);    // boolean
		
		Preferences othersPrefs = prefs.node("others");
		othersPrefs.put("apiToUse", "default");        // String
		othersPrefs.putBoolean("checkUpdates", true);  // boolean
		
		try {
			prefs.exportSubtree(new FileOutputStream(parentPath+"/default.xml"));
		} catch (IOException e) {
			e.printStackTrace();
		} catch (BackingStoreException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		String path = PreferencesTest.class.getResource("/resources/config").getPath();
		System.out.println(path);

		PreferencesTest.saveDefaults(path);
		
		PreferencesTest prefTest = new PreferencesTest();
	    prefTest.setNode(Sections.GENERAL);
	    
		for (String key : prefTest.getKeys())
			System.out.println(key);
	}
}
