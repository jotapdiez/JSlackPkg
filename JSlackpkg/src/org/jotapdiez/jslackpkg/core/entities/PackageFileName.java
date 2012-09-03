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
		boolean escaped = false;
		fileName = fileName.replaceAll("\\.t.z", "");
		if (fileName.indexOf('+')>-1)
		{
//			System.out.println(fileName+" - fileName.indexOf('+'): " + fileName.indexOf('+'));
			fileName = escape(fileName);
			escaped = true;
		}
		
		Pattern pattern = Pattern.compile("^(.*)-(.*)-(.*)-(.*)$");
		Matcher matcher = pattern.matcher(fileName);

		if (matcher.find())
		{
			String name = matcher.group(1);
			if (escaped)
				name = unescape(name);
			setName( name.toCharArray() );
			setVersion(matcher.group(2).toCharArray());
			setArch(matcher.group(3).toCharArray());
			setBuild(matcher.group(4).toCharArray());
			 
			_fullName = fileName.toCharArray();
		}
//		else
//			System.out.println("Invalida package filename: " + fileName);
	}
	
	String escape(String name)
	{
//		System.out.println("escape: " + name);
		return name.replaceAll("\\+", "\\\\+");
	}
	
	private String unescape(String name)
	{
//		System.out.println("unescape: " + name);
		return name.replaceAll("\\\\\\+", "#")
				.replaceAll("#", "\\+");
	}
	
	public String getFullName()
	{
		if (_fullName != null)
			return new String(_fullName);
		return "";
	}
	
	public String getBuild()
	{
		if (_build != null)
			return new String(_build);
		return "";
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

