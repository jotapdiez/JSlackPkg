package org.jotapdiez.jslackpkg.core.interfaces;

import java.util.List;

import org.jotapdiez.jslackpkg.core.entities.Package;

public interface PackageManager
{
	public void update();
	
	public void upgrade();
	public void install();
	
	public void clean();

	public void search();
	public void info();

	public Package getPackage(String packageFileName);
	
	public List<Package> getAllPackages();
	public List<Package> getInstalledPackages();
	public List<Package> getNewPackages();
	public List<Package> getUpgradedPackages();
	public List<Package> getRemovedPackages();
	
}

