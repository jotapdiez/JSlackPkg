package org.jotapdiez.jslackpkg.ui.components.models;

import java.awt.Component;
import java.util.List;

import javax.swing.JTable;
import javax.swing.RowFilter;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.DefaultTableModel;
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
		setDefaultEditor(String.class, new PackagesCellEditor());
		setDefaultRenderer(String.class, new PackagesListCellRender());
		setCellSelectionEnabled(false);
		setColumnSelectionAllowed(false);
		setDragEnabled(false);
		
		getTableHeader().setReorderingAllowed(false);
		
		sorter.setSortsOnUpdates(true);
		setRowSorter(sorter);
	}
	
	public void setData(List<Package> data)
	{
//		if (model == null)
			model = new PackagesTableModel();
		
		model.setData(data);
		sorter.setModel(model);
		setModel(model);
		setAutoCreateColumnsFromModel(true);

		packColumns();
		
//		getTableHeader().getColumnModel().getColumn(1).getCellEditor();
	}

//	public List<Package> getData()
//	{
//		return model.getData();
//	}
	
	public void cleanData()
	{
		model = new PackagesTableModel();
		setModel(new DefaultTableModel());
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
	
//	TODO: Implementar
//	private void configureRowSorter()
//	{
//		final String regexpRemoveNonNumbers = "[^0-9]*";
//		
//		sorter.setSortsOnUpdates(true);
//		//Harcoding columna Estado
//		sorter.setComparator(2, new Comparator<STATE>()
//		{
//			public int compare(STATE o1, STATE o2)
//			{
//				return o1.compareTo(o2);
//			}
//		});
//		
//		//Harcoding columna version
//		sorter.setComparator(3, new Comparator<String>()
//		{
//			public int compare(String o1, String o2)
//			{
//				Long o1l = Long.valueOf(o1.replaceAll(regexpRemoveNonNumbers, ""));
//				Long o2l = Long.valueOf(o2.replaceAll(regexpRemoveNonNumbers, ""));
//				return o1l.compareTo(o2l);
//			}
//		});
//		//Harcoding columna build
//		sorter.setComparator(3, new Comparator<String>()
//		{
//			public int compare(String o1, String o2)
//			{
//				Long o1l = Long.valueOf(o1.replaceAll(regexpRemoveNonNumbers, ""));
//				Long o2l = Long.valueOf(o2.replaceAll(regexpRemoveNonNumbers, ""));
//				return o1l.compareTo(o2l);
//			}
//		});
//	}

	private static final long	serialVersionUID	= 1285320561554247588L;
}
