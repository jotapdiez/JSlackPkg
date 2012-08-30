package org.jotapdiez.jslackpkg.core.entities;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Class para encapsular el parseo del nombre del paquete
 * @author juanpablo
 */
class PackageFileName
{
	char[] _name = null;
	char[] _fullName = null;
	char[] _version = null;
	char[] _arch = null;
	char[] _build = null;
	
	String _nameCache = null;
	
	public PackageFileName(String fileName)
	{
		fileName = fileName.replaceAll("\\.t.z", "");
		Pattern pattern = Pattern.compile("^(.*)-(.*)-(.*)-(.*)$");
		Matcher matcher = pattern.matcher(fileName);

		if (matcher.find())
		{
			 setName(matcher.group(1).toCharArray());
			 setVersion(matcher.group(2).toCharArray());
			 setArch(matcher.group(3).toCharArray());
			 setBuild(matcher.group(4).toCharArray());
			 
			 _fullName = fileName.toCharArray();
		}			
	}
	
	public String getFullName()
	{
		if (_fullName != null)
			return new String(_fullName);
		return "";
	}
	
	public String getBuild()
	{
		return new String(_build);
	}
	
	public void setBuild(char[] _build) {
		this._build = _build;
	}

	public String getArch() {
		return new String(_arch);
	}

	public void setArch(char[] _arch) {
		this._arch = _arch;
	}

	public String getVersion()
	{
		if (_version != null)
			return new String(_version);
		return "";
	}

	public void setVersion(char[] _version) {
		this._version = _version;
	}

	public String getName() {
		if (_name != null)
			return new String(_name);
		return null;
	}

	public void setName(char[] name) {
		this._name = name;
	}

	@Override
	public String toString() {
		if (_nameCache == null && getName() != null)
			_nameCache = new String(getName());
		return _nameCache;
	}
}

