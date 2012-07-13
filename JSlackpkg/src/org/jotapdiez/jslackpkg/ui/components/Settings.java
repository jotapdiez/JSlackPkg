package org.jotapdiez.jslackpkg.ui.components;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JPanel;
import javax.swing.JSplitPane;

public class Settings extends JPanel
{
	private final JSplitPane splitPane = new JSplitPane();
	public Settings() {
		setLayout(new BorderLayout(0, 0));
		
		splitPane.setOneTouchExpandable(true);
		add(splitPane, BorderLayout.CENTER);
		
		SettingsTreeBuilder sectionsBuilder = new SettingsTreeBuilder();
		sectionsBuilder.setMinimumSize(new Dimension(200, 0));
		splitPane.setLeftComponent(sectionsBuilder);
		
		JPanel panel = new JPanel();
		splitPane.setRightComponent(panel);
	}

	private static final long serialVersionUID = 3182590899137988025L;
}
