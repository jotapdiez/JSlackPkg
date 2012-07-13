package org.jotapdiez.jslackpkg.core.entities;

import java.text.DecimalFormat;
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
	private char[]			_location			= null;
	private char[]			_fileName			= null;
	private char[]			_name				= null;
	private double			_sizeUncompressed	= 0;
	private double			_sizeCompressed		= 0;
	private char[]			_description		= null;

	private char[]			_version			= null;
	private char[]			_arch    			= null;
	private char[]			_buildNumber		= null;
	private STATE			_state				= STATE.UNKNOWN;

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

	public void setSizeUncompressed(String size)
	{
		String originalSize = size;

		size = size.replaceAll("[a-zA-Z\\s]*", "");

		_sizeUncompressed = Double.parseDouble(size);

		if (originalSize.trim().endsWith("M"))
			_sizeUncompressed = _sizeUncompressed * 1024;
		else if (originalSize.trim().endsWith("G"))
			_sizeUncompressed = (_sizeUncompressed * 1024) * 1024;
	}

	public void setSizeCompressed(String size)
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

	@Deprecated
	public void setRealName(String realName)
	{
		setRealName(realName.toCharArray());
	}

	public void setRealName(char[] realName)
	{
		_name = realName;
	}

	public String getRealName()
	{
		if (_name != null)
			return new String(_name);
		return "";
	}

	public void setVersion(char[] version)
	{
		_version = version;
	}

	public String getVersion()
	{
		if (_version != null)
			return new String(_version);
		return "";
	}

	public String getUncompressedSize()
	{
		return parseSize(_sizeUncompressed);
	}

	public String getCompressedSize()
	{
		return parseSize(_sizeCompressed);
	}
	
	private String parseSize(double sizeToParse)
	{
		char sizeType = 'K';
		double size = sizeToParse;
		if (size > 1024)
		{
			size = size / 1024;
			sizeType = 'M';
			if (size > 1024)
			{
				size = size / 1024;
				sizeType = 'G';
			}
		}
		DecimalFormat twoDForm = new DecimalFormat("#.##");
		return String.valueOf(twoDForm.format(size)) + String.valueOf(sizeType);	}
	
	public void setArch(char[] arch)
	{
		_arch = arch;
	}

	public void setBuildNumber(char[] build)
	{
		_buildNumber = build;
	}

	public String getBuild()
	{
		return new String(_buildNumber);
	}
	
	@Override
	public boolean equals(Object obj)
	{
		Package tmpObj = (Package) obj;
		if (!Arrays.equals(tmpObj._name, _name))
			return false;
		
		if (!Arrays.equals(tmpObj._version,_version))
			return false;
		
		if (!Arrays.equals(tmpObj._arch,_arch))
			return false;
		
		if (!Arrays.equals(tmpObj._buildNumber,_buildNumber))
			return false;
		
		return true;
	}
}
