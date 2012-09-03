package org.jotapdiez.jslackpkg.ui.components.models;

import java.awt.Component;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

public class PackagesListCellRender implements TableCellRenderer
	{
		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
		{
			Component comp = null;
			if (value instanceof Boolean)
			{
				comp = new JCheckBox("", (Boolean) value);
			}else
			{
				comp = new JLabel(value.toString());
			}
			
			return comp;
		}
	}