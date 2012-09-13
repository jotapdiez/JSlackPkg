package org.jotapdiez.jslackpkg.core.observers;

import org.jotapdiez.jslackpkg.core.entities.Package;
import org.jotapdiez.jslackpkg.core.observers.PackageObservable.MODE;

public interface PackageObserver
{
	public void update(PackageObservable o, Package packageItem, MODE mode);
}
