package org.jotapdiez.jslackpkg.core.entities;

import java.util.Arrays;

public class Package
{
	public static enum STATE
	{
		UNKNOWN (0, "Unknown"),
		
		INSTALLED  (1, "Installed"),
		UNINSTALLED(2, "Uninstalled"),
		
		TO_INSTALL(10, "To Install"),
		TO_UPGRADE(11, "To Upgrade"),
		TO_DELETE (12, "To Remove");
		
		char[] _label = null;
		int _id = 0;
		private STATE(int id, String label)
		{
			_id = id;
			_label = label.toCharArray();
		}
		
		@Override
		public String toString()
		{
			return new String(_label);
		}
	}
	
	private PackageFileName _pName            = null;
	
	private char[]			_location		  = null;
	private char[]			_fileName		  = null;
	private double			_sizeUncompressed = 0;
	private double			_sizeCompressed	  = 0;
	private char[]			_description	  = null;
	private boolean         isInBlackList     = false;
	
	private STATE			_state			  = STATE.UNKNOWN;

	public void setState(STATE state)
	{
		_state = state;
	}
	
	public STATE getState()
	{
		return _state;
	}
	
	public void setLocation(String location)
	{
		_location = location.toCharArray();
	}

	public String getLocation()
	{
		if (_location != null)
			return new String(_location);
		return "";
	}

	public void setFileName(String fileName)
	{
		_pName = new PackageFileName(fileName);
		
		int indexOfNewLine = fileName.indexOf("\n");
		if (indexOfNewLine > -1)
			fileName = fileName.substring(0, indexOfNewLine);
		_fileName = fileName.toCharArray();
	}

	public String getFileName()
	{
		if (_fileName != null)
			return new String(_fileName);
		return "";
	}

	public void setUncompressedSize(String size)
	{
		String originalSize = size;

		size = size.replaceAll("[a-zA-Z\\s]*", "");

		_sizeUncompressed = Double.parseDouble(size);

		if (originalSize.trim().endsWith("M"))
			_sizeUncompressed = _sizeUncompressed * 1024;
		else if (originalSize.trim().endsWith("G"))
			_sizeUncompressed = (_sizeUncompressed * 1024) * 1024;
	}

	public void setCompressedSize(String size)
	{
		String originalSize = size;

		size = size.replaceAll("[a-zA-Z\\s]*", "");
		_sizeCompressed = Double.parseDouble(size);

		if (originalSize.trim().endsWith("M"))
			_sizeCompressed = _sizeCompressed * 1024;
		else if (originalSize.trim().endsWith("G"))
			_sizeCompressed = (_sizeCompressed * 1024) * 1024;
	}

	public void setDescription(String description)
	{
		_description = description.toCharArray();
	}

	public String getDescription()
	{
		if (_description != null)
			return new String(_description);
		return "";
	}

	@Override
	public String toString()
	{
		if (_fileName != null)
			return new String(_fileName);
		return "";
	}

	public String getFullName()
	{
		return _pName.getFullName();
	}

	public String getName()
	{
		return getName(false);
	}
	
	public String getName(boolean escaped)
	{
//		System.out.println("escaped: " + escaped);
		String result = _pName.getName();
		if (escaped)
			result = _pName.escape(result);
		return result;
	}

	public String getUncompressedSize()
	{
		return String.valueOf(_sizeUncompressed);
//		return parseSize(_sizeUncompressed);
	}

	public String getCompressedSize()
	{
//		return parseSize(_sizeCompressed);
		return String.valueOf(_sizeCompressed);
	}
	
	public String getVersion() {
		return _pName.getVersion();
	}
	
	public String getBuild()
	{
		return _pName.getBuild();
	}

	public void setIsInBlackList(boolean is)
	{
		isInBlackList = is;
	}
	
	public boolean isInBlackList()
	{
		return isInBlackList;
	}
	
	@Override
	public boolean equals(Object obj)
	{
		Package tmpObj = (Package) obj;
		if (Arrays.equals(tmpObj._pName._name, _pName._name))
			return true;

//		if (!Arrays.equals(tmpObj._pName._name, _pName._name))
//			return false;
//		
//		if (!Arrays.equals(tmpObj._pName._version, _pName._version))
//			return false;
//		
//		if (!Arrays.equals(tmpObj._pName._arch, _pName._arch))
//			return false;
//		
//		if (!Arrays.equals(tmpObj._pName._build, _pName._build))
//			return false;
		
		return false;
	}

	public boolean equalsExact(Object obj)
	{
		Package tmpObj = (Package) obj;

		if (!Arrays.equals(tmpObj._pName._name, _pName._name))
			return false;
		
		if (!Arrays.equals(tmpObj._pName._version, _pName._version))
			return false;
		
		if (!Arrays.equals(tmpObj._pName._arch, _pName._arch))
			return false;
		
		if (!Arrays.equals(tmpObj._pName._build, _pName._build))
			return false;
		
		return true;
	}
}
