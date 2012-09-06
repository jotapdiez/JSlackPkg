package org.jotapdiez.jslackpkg;

import java.awt.EventQueue;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.apache.log4j.PropertyConfigurator;
import org.jotapdiez.jslackpkg.ui.MainUI;
import org.pushingpixels.substance.api.SubstanceLookAndFeel;

public class JSlackpkg {

	@SuppressWarnings("unused")
	private static void initLookAndFeel()
	{
		try {
			UIManager.setLookAndFeel("org.pushingpixels.substance.api.skin.SubstanceMarinerLookAndFeel");
			
			SubstanceLookAndFeel.setSkin(new org.pushingpixels.substance.api.skin.BusinessBlackSteelSkin());
//			SubstanceLookAndFeel.setSkin(new org.pushingpixels.substance.api.skin.NebulaSkin());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
//		initLookAndFeel();
//		org.jotapdiez.jslackpkg.utils.ResourceMap.getInstance("test.properties"); //Setea el lenguaje al instanciarlo por primera vez
		PropertyConfigurator.configure(JSlackpkg.class.getResource("/resources/log4j.properties"));
		
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainUI.getInstance().setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

}
