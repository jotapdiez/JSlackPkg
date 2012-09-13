package org.jotapdiez.jslackpkg.core.observers;

import java.util.LinkedList;
import java.util.List;

import org.jotapdiez.jslackpkg.core.entities.Package;

public class PackageObservable
{
	public static enum MODE
	{
		INSTALLED, UPGRADED, REMOVED;
	}
	
	private List<PackageObserver> observers = null;
	private boolean changed = false;
	

	public PackageObservable()
	{
		observers = new LinkedList<PackageObserver>();
	}
	
	public synchronized void removeObservers()
	{
		observers = new LinkedList<PackageObserver>();
	}
	
	public synchronized void removeObserver(PackageObserver o)
	{
		observers.remove(o);
	}
	
	public synchronized void addObserver(PackageObserver o)
	{
		observers.add(o);
	}
	
	public void notifyObservers(Package packageItem, MODE mode)
	{
		if (!changed)
			return;
		
		for (PackageObserver o : observers)
			o.update(this, packageItem, mode);
		clearChanged();
	}
	
	protected synchronized void setChanged()
	{
		changed = true;
	}
	
	protected synchronized void clearChanged()
	{
		changed = false;
	}
}
