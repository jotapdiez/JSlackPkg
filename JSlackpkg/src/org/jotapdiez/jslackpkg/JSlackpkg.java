package org.jotapdiez.jslackpkg;

import java.awt.EventQueue;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.jotapdiez.jslackpkg.ui.MainUI;
import org.pushingpixels.substance.api.SubstanceLookAndFeel;

public class JSlackpkg {

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
