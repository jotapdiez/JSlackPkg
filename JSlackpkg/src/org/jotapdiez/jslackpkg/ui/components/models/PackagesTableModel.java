package org.jotapdiez.jslackpkg.ui.components.models;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.jotapdiez.jslackpkg.core.entities.Package;
import org.jotapdiez.jslackpkg.utils.ResourceMap;

import javax.swing.table.DefaultTableModel;

public class PackagesTableModel extends DefaultTableModel
{
	public static String COLUMN_NAME           = ResourceMap.getInstance().getString("packagesList.table.column.name.text");
	public static String COLUMN_INSTALLED_SIZE = ResourceMap.getInstance().getString("packagesList.table.column.installedSize.text");
	public static String COLUMN_VERSION        = ResourceMap.getInstance().getString("packagesList.table.column.version.text");
	public static String COLUMN_BUILD          = ResourceMap.getInstance().getString("packagesList.table.column.build.text");
	public static String COLUMN_LOCATION       = ResourceMap.getInstance().getString("packagesList.table.column.location.text");
	
	String[] _columnNames	= new String[] { COLUMN_NAME, COLUMN_INSTALLED_SIZE, COLUMN_VERSION, COLUMN_BUILD, COLUMN_LOCATION };

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
	
	public PackagesTableModel() {
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

	@Override
	public Class<?> getColumnClass(int columnIndex)
	{
		return getValueAt(0, columnIndex).getClass();
	}

	@Override
	public Object getValueAt(int row, int col)
	{
		if (_data == null)
			return null;

		Item item = _data.get(row);

		Object ret = null;

		if (item == null)
			ret = ResourceMap.getInstance().getString("packagesList.table.column.empty.value");
		else
		{
			switch (col) {
				case 0:
					ret = item.packageItem.getName();
					break;
				case 1:
					ret = Double.parseDouble(item.packageItem.getUncompressedSize());
					break;
				case 2:
					ret = item.packageItem.getVersion();
					break;
				case 3:
					ret = item.packageItem.getBuild();
					break;
				case 4:
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
	
	@Override
	public boolean isCellEditable(int row, int column)
	{
		return (column<=0); // Solo la primera se puedde editar
	}

	private static final long	serialVersionUID	= -502859337667720942L;
}
