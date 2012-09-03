package org.jotapdiez.jslackpkg.ui.components.models;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.jotapdiez.jslackpkg.core.entities.Package;

import javax.swing.table.DefaultTableModel;

public class PackagesTableModel extends DefaultTableModel
{
	String[]						_columnNames	= new String[] { "S", "Nombre", "Tamano Instalado", "Version", "Build", "Ubicacion" };	// TODO: A archivo de lenguajes

	private Map<Integer, Item>	_data			= null;

	class Item
	{
		public boolean selected = false;
		public Package packageItem = null;
		
		public Item(Package pack)
		{
			this(pack, false);
		}
		public Item(Package pack, boolean selected)
		{
			this.selected = selected;
			packageItem = pack;
		}
	}
	
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
		if (packages == null || packages.size() == 0)
			return;

		_data = new HashMap<Integer, Item>(packages.size());

		Iterator<Package> it = packages.iterator();
		int index = 0;
		while (it.hasNext())
		{
			Package packageItem = it.next();
			if (packageItem == null)
				continue;
			_data.put(index++, new Item(packageItem));
		}
	}

	public List<Package> getSelected()
	{
		List<Package> result = new LinkedList<Package>();
		for (Item item : _data.values())
		{
			if (item.selected)
				result.add(item.packageItem);
		}
		return result;
	}
	
	@Override
	public Object getValueAt(int row, int col)
	{
		if (_data == null)
			return null;

		Item item = _data.get(row);

		Object ret = null;

		if (item == null)
			ret = "Empty column";
		else
		{
			switch (col) {
				case 0:
					ret = item.selected;
					break;
				case 1:
					ret = item.packageItem.getName();
					break;
				case 2:
					ret = item.packageItem.getUncompressedSize();
					break;
				case 3:
					ret = item.packageItem.getVersion();
					break;
				case 4:
					ret = item.packageItem.getBuild();
					break;
				case 5:
					ret = item.packageItem.getLocation();
					break;
			}

			if (ret == null)
				ret = "-";
		}
		return ret;
	}
	
	@Override
	public void setValueAt(Object value, int row, int col) {
		if (value == null || col > 0)
			return;
		
		_data.get(row).selected = (Boolean) value;
	}

	public Package getValueAt(int row)
	{
		if (_data == null)
			return null;

		return _data.get(row).packageItem;
	}
	
//	@Override
//	public boolean isCellEditable(int row, int column)
//	{
//		return false;
//	}

	private static final long	serialVersionUID	= -502859337667720942L;
}
