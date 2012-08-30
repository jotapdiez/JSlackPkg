package org.jotapdiez.jslackpkg.core.impl;

import java.util.List;

import org.jotapdiez.jslackpkg.core.entities.Package;
import org.jotapdiez.jslackpkg.core.interfaces.PackageManager;

public class SlackpkgPackageManager implements PackageManager
{
	@Override
	public void update() {

	}

	@Override
	public void clean() {

	}

	@Override
	public void search() {

	}

	@Override
	public void info() {

	}

	@Override
	public List<Package> getNewPackages() {
		return null;
	}

	@Override
	public List<Package> getUpgradedPackages() {
		return null;
	}

	@Override
	public List<Package> getRemovedPackages() {
		return null;
	}

	@Override
	public List<Package> getAllPackages() {
		return null;
	}

	@Override
	public List<Package> getInstalledPackages() {
		return null;
	}

	@Override
	public Package getPackage(String packageFileName) {
		return null;
	}

	@Override
	public boolean remove(Package packageItem) {
		return false;
	}

	@Override
	public boolean upgrade(Package packageItem) {
		return false;
	}

	@Override
	public boolean install(Package packageItem) {
		return false;
	}
}
