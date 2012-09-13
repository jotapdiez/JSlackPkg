package org.jotapdiez.jslackpkg.core.interfaces;

import java.util.List;

import org.jotapdiez.jslackpkg.core.entities.Package;

public interface PackageManager
{
	public void update();
	
	public boolean upgrade(Package packageItem);
	public boolean upgrade(List<Package> list);
	
	public boolean install(Package packageItem);
	public boolean install(List<Package> list);
	
	public boolean remove(Package packageItem);
	public boolean remove(List<Package> list);
	
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

