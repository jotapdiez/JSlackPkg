package org.jotapdiez.jslackpkg.ui.components.models;

import java.awt.Component;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JTable;
import javax.swing.RowFilter;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableRowSorter;

import org.jotapdiez.jslackpkg.core.entities.Package;

public class PackagesTable extends JTable
{
	private PackagesTableModel model  = new PackagesTableModel();
	private transient final TableRowSorter<PackagesTableModel>	sorter	= new TableRowSorter<PackagesTableModel>();
	
	public PackagesTable()
	{
		setAutoCreateColumnsFromModel(true);
		
//		setDefaultRenderer(Boolean.class, new PackagesTableCellRenderer());
//		setDefaultRenderer(Double.class, new PackagesTableCellRenderer());
//		setDefaultRenderer(String.class, new PackagesTableCellRenderer());
//		setCellEditor(new PackagesCellEditor());
		
		setCellSelectionEnabled(true);
		setColumnSelectionAllowed(false);
		setDragEnabled(false);
		
		getTableHeader().setReorderingAllowed(false);
		
		setRowSorter(sorter);
	}
	
	public void setData(List<Package> data)
	{
		model = new PackagesTableModel();
		
		model.setData(data);
		sorter.setModel(model);
		setModel(model);

		packColumns();
	}

	public void packColumns()
	{
		for (int c = 0; c < this.getColumnCount(); c++)
		{
			packColumn(c, 0);
		}
	}

	// Sets the preferred width of the visible column specified by vColIndex. The column
	// will be just wide enough to show the column head and the widest cell in the column.
	// margin pixels are added to the left and right
	// (resulting in an additional width of 2*margin pixels).
	public void packColumn(int vColIndex, int margin)
	{
		// TableModel model = getModel();
		DefaultTableColumnModel colModel = (DefaultTableColumnModel) getColumnModel();
		TableColumn col = colModel.getColumn(vColIndex);
		int width = 0;

		// Get width of column header
		TableCellRenderer renderer = col.getHeaderRenderer();
		if (renderer == null)
		{
			renderer = getTableHeader().getDefaultRenderer();
		}
		Component comp = renderer.getTableCellRendererComponent(this, col.getHeaderValue(), false, false, 0, 0);
		width = comp.getPreferredSize().width;

		// Get maximum width of column data
		for (int r = 0; r < getRowCount(); r++)
		{
			renderer = getCellRenderer(r, vColIndex);
			comp = renderer.getTableCellRendererComponent(this, getValueAt(r, vColIndex), false, false, r, vColIndex);
			width = Math.max(width, comp.getPreferredSize().width);
		}

		// Add margin
		width += 2 * margin;

		// Set the width
		col.setPreferredWidth(width);
	}
	
	public void filter(String text)
	{
//		System.out.println("doFilter(String text): " + text + " | text.length(): " + text.length());
		RowFilter<Object, Object> filter = null;
		
		if (text.length() > 0)
			filter = RowFilter.regexFilter(text);

		sorter.setRowFilter(filter);
	}
	
	public List<Package> getSelectedPackages() {
		List<Package> list = new LinkedList<Package>();
		int[] indexSelectedRow = getSelectedRows();
		
		for (int index : indexSelectedRow)
		{
			index = convertRowIndexToModel(index);
			Package selectedPackage = model.getValueAt(index);
			list.add(selectedPackage);
		}
		
		return list;
	}
	
	private static final long	serialVersionUID	= 1285320561554247588L;
}
