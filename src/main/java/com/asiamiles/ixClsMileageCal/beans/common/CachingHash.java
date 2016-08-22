/*
 * Created on Oct 27, 2008
 *
 * 
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.asiamiles.ixClsMileageCal.beans.common;

import java.util.Hashtable;

/**
 * @author CPPALAC
 *
 * 
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class CachingHash {
	
	public static CachingHash getInstance() 
	{
		if (instance == null) {
			instance = new CachingHash();

			mileageCalHash = new Hashtable();
		}
		return instance;
	}
	
	public static void clearCachingHash()
	{
		mileageCalHash = new Hashtable();
		Log.writeInfoLog("CachingHash>clear caching hash done" );
	}
	
	public static Hashtable getMileageCalHash()
	{
		return mileageCalHash;
	}
	
	public static void addMileageCalHash(String inKey, Object inValue)
	{
		mileageCalHash.put(inKey, inValue);
	}
	
	private static Hashtable mileageCalHash;
	
	private static CachingHash instance;

}
