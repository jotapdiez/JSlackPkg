package org.jotapdiez.jslackpkg.ui.components.models;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.jotapdiez.jslackpkg.core.entities.Package;

import javax.swing.table.DefaultTableModel;

public class TableModel extends DefaultTableModel
{
	String[]						_columnNames	= new String[] { "Nombre", "Estado", "Version", "Build", "Ubicacion" };	// TODO: A archivo de lenguajes

	private Map<Integer, Package>	_data			= null;

	@Override
	public int getColumnCount()
	{
		return _columnNames.length;
	}

	@Override
	public String getColumnName(int columnIndex)
	{
		return _columnNames[columnIndex];
	}

	@Override
	public int getRowCount()
	{
		if (_data == null)
			return 0;
		
		return _data.size();
	}

	@Override
	public Class<?> getColumnClass(int columnIndex)
	{
		return String.class;
	}

	public void setData(List<Package> packages)
	{
		if (packages.size() == 0)
			return;

		_data = new HashMap<Integer, Package>(packages.size());

		Iterator<Package> it = packages.iterator();
		int index = 0;
		while (it.hasNext())
		{
			Package packageItem = it.next();
			if (packageItem == null)
				continue;

			_data.put(index++, packageItem);
		}
	}

	@Override
	public Object getValueAt(int row, int col)
	{
		if (_data == null)
			return null;

		Package packageItem = _data.get(row);

		Object ret = null;

		if (packageItem == null)
			ret = "Empty column";
		else
		{
			switch (col) {
			case 0:
				ret = packageItem.getName() + " (" + packageItem.getFileName() + ")";
				break;
			case 1:
				ret = packageItem.getState();
				break;
			case 2:
				ret = packageItem.getVersion();
				break;
			case 3:
				ret = packageItem.getBuild();
				break;
			case 4:
				ret = packageItem.getLocation();
				break;
			}

			if (ret == null)
				ret = "-";
		}
		return ret;
	}

	public Package getValueAt(int row)
	{
		if (_data == null)
			return null;

		return _data.get(row);
	}
	
	@Override
	public boolean isCellEditable(int row, int column)
	{
		return false;
	}

	private static final long	serialVersionUID	= -502859337667720942L;
}
