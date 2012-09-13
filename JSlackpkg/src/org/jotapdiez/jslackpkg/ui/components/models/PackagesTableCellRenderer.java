package org.jotapdiez.jslackpkg.ui.components.models;

import java.awt.Color;
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
		JLabel label = new JLabel();
		if (value instanceof Double)
		{
			double val = Double.parseDouble(value.toString());
			value = Conversions.parseSize(val);
		}
		
		label.setText(value.toString());
		
		if (isSelected)
		{
			// Para que el JLabel haga caso al color de fondo, tiene que ser opaco. 
			label.setOpaque(true);
			label.setBackground(Color.LIGHT_GRAY);
		}
		
		return label;
	}
}