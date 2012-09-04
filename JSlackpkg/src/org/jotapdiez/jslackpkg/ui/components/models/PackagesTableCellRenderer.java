package org.jotapdiez.jslackpkg.ui.components.models;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import org.jotapdiez.jslackpkg.utils.Conversions;

public class PackagesTableCellRenderer implements TableCellRenderer
{
	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
	{
		if (value instanceof Double)
		{
			double val = Double.parseDouble(value.toString());
			value = Conversions.parseSize(val);
		}
		
		return new JLabel(value.toString());
	}
}