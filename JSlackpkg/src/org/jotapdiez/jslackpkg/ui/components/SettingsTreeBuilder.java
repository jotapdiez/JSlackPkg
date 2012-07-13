package org.jotapdiez.jslackpkg.ui.components;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import org.jotapdiez.jslackpkg.core.settings.SettingsManager;
import org.jotapdiez.jslackpkg.core.settings.SettingsManager.Section;
import org.jotapdiez.jslackpkg.utils.ResourceMap;

public class SettingsTreeBuilder extends JTree
{
	private final SettingsManager settingsManager = SettingsManager.getInstance();
	private final DefaultMutableTreeNode root = new DefaultMutableTreeNode("Settings"); //TODO: A archivo de lenguajes
	
	public SettingsTreeBuilder() {
		buildSections();
//		setRootVisible(false);
		setModel(new DefaultTreeModel(root));
	}
	
	public void buildSections()
	{
		Section[] sections = settingsManager.getSections();
		for (Section item : sections)
		{
			String langName = ResourceMap.getInstance().getString(item.getName());
			if (langName == null)
				langName = item.getName();
			
			root.add( new DefaultMutableTreeNode(langName) );
//			addChilds(item, manager.getKeys(item));
		}
	}
	
//	private void addChilds(Section item, String[] childs)
//	{
//	}
	
	private static final long serialVersionUID = 5934740791650261278L;
}
