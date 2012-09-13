package org.jotapdiez.jslackpkg.core.observers.impl;

import org.jotapdiez.jslackpkg.core.entities.Package;
import org.jotapdiez.jslackpkg.core.observers.PackageObservable;
import org.jotapdiez.jslackpkg.core.observers.PackageObservable.MODE;
import org.jotapdiez.jslackpkg.core.observers.PackageObserver;
import org.jotapdiez.jslackpkg.ui.components.PackagesList;

public class InstalledObserver implements PackageObserver
{
	PackagesList packageList = null;
	
	public InstalledObserver(PackagesList panel)
	{
		packageList = panel;
	}
	
	@Override
	public void update(PackageObservable o, Package packageItem, MODE mode)
	{
		switch (mode)
		{
			case INSTALLED:
				packageList.addPackage(packageItem);
				break;
			case REMOVED:
				packageList.removePackage(packageItem);
				break;
			case UPGRADED:
				packageList.updatePackage(packageItem);
				break;
		}		
	}
}
