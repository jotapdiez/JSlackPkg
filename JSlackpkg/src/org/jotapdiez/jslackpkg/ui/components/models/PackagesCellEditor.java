package org.jotapdiez.jslackpkg.ui.components.models;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractCellEditor;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;

public class PackagesCellEditor extends AbstractCellEditor implements TableCellEditor, ActionListener
{
	private Component comp = null;
	
	@Override
	public Object getCellEditorValue()
	{
		if (comp instanceof JCheckBox)
			return (((JCheckBox)comp).isSelected() ? true : false);
		else
			return ((JLabel)comp).getText();
	}

	@Override
	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column)
	{
		if (value instanceof Boolean)
		{
			JCheckBox check = new JCheckBox("", (Boolean) value);
			check.addActionListener(this);
			comp = check;
		}else
		{
			comp = new JLabel(value.toString());
		}
		
		return comp;
	}
	
	public void actionPerformed(ActionEvent e)
	{
//		System.err.println("ActionListener:ActionCommand: "+e.getActionCommand());
		fireEditingStopped();
	}
	
	private static final long serialVersionUID = 4346422845510702274L;
}