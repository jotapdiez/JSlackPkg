package org.jotapdiez.jslackpkg.core.observers.impl;

import org.jotapdiez.jslackpkg.core.entities.Package;
import org.jotapdiez.jslackpkg.core.observers.PackageObservable;
import org.jotapdiez.jslackpkg.core.observers.PackageObserver;
import org.jotapdiez.jslackpkg.core.observers.PackageObservable.MODE;
import org.jotapdiez.jslackpkg.ui.components.PackagesList;

public class NewObserver implements PackageObserver
{
	PackagesList packageList = null;
	
	public NewObserver(PackagesList panel)
	{
		packageList = panel;
	}
	
	@Override
	public void update(PackageObservable o, Package packageItem, MODE mode)
	{
		switch (mode)
		{
			case INSTALLED: 
			case UPGRADED:
				packageList.removePackage(packageItem);
				break;
			case REMOVED:
				packageList.addPackage(packageItem);
				break;
		}		
	}
}
