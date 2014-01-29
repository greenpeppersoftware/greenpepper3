package com.greenpepper.server.license;

import java.io.IOException;
import java.io.OutputStream;
import java.util.prefs.BackingStoreException;
import java.util.prefs.NodeChangeListener;
import java.util.prefs.PreferenceChangeListener;

public class Preferences extends java.util.prefs.Preferences
{
	private static Preferences preferences;
	private byte[] lic;
	
	public static Preferences instance()
	{
		if(preferences == null)
			preferences = new Preferences();
		
		return preferences;
	}
	
    public byte[] getByteArray(String key, byte[] def)
    {
        return lic;
    }
    
    @SuppressWarnings("unchecked")
    public void putByteArray(String key, byte[] lic)
    {
        this.lic = lic;
    }
    
    public void remove(String key)
    {
    	this.lic = null;
    }
    
    public boolean isUserNode()
    {
        return true;
    }

    @Override
    public String absolutePath(){ /* TODO Auto-generated method stub */ return null;}

    @Override
    public void addNodeChangeListener(NodeChangeListener arg0){ /* TODO Auto-generated method stub */ }

    @Override
    public void addPreferenceChangeListener(PreferenceChangeListener arg0){ /* TODO Auto-generated method stub */ }

    @Override
    public String[] childrenNames() throws BackingStoreException{ /* TODO Auto-generated method stub */ return null;}
    
    @Override
    public void clear() throws BackingStoreException{ /* TODO Auto-generated method stub */ }

    @Override
    public void exportNode(OutputStream arg0) throws IOException, BackingStoreException{ /* TODO Auto-generated method stub */ }

    @Override
    public void exportSubtree(OutputStream arg0) throws IOException, BackingStoreException{ /* TODO Auto-generated method stub */ }

    @Override
    public void flush() throws BackingStoreException{ /* TODO Auto-generated method stub */ }

    @Override
    public String get(String arg0, String arg1){ /* TODO Auto-generated method stub */ return null;}    

    @Override
    public boolean getBoolean(String arg0, boolean arg1){ /* TODO Auto-generated method stub */ return false;}

    @Override
    public double getDouble(String arg0, double arg1){ /* TODO Auto-generated method stub */ return 0;}

    @Override
    public float getFloat(String arg0, float arg1){ /* TODO Auto-generated method stub */ return 0;}

    @Override
    public int getInt(String arg0, int arg1){ /* TODO Auto-generated method stub */ return 0;}

    @Override
    public long getLong(String arg0, long arg1){ /* TODO Auto-generated method stub */ return 0;}

    @Override
    public String[] keys() throws BackingStoreException{ /* TODO Auto-generated method stub */ return null;}

    @Override
    public String name(){ /* TODO Auto-generated method stub */ return null;}

    @Override
    public java.util.prefs.Preferences node(String arg0){ /* TODO Auto-generated method stub */ return null;}

    @Override
    public boolean nodeExists(String arg0) throws BackingStoreException{ /* TODO Auto-generated method stub */ return false;}

    @Override
    public java.util.prefs.Preferences parent(){ /* TODO Auto-generated method stub */ return null;}

    @Override
    public void put(String arg0, String arg1){ /* TODO Auto-generated method stub */ }

    @Override
    public void putBoolean(String arg0, boolean arg1){ /* TODO Auto-generated method stub */ }

    @Override
    public void putDouble(String arg0, double arg1){ /* TODO Auto-generated method stub */ }

    @Override
    public void putFloat(String arg0, float arg1){ /* TODO Auto-generated method stub */ }

    @Override
    public void putInt(String arg0, int arg1){ /* TODO Auto-generated method stub */ }

    @Override
    public void putLong(String arg0, long arg1){ /* TODO Auto-generated method stub */ }

    @Override
    public void removeNode() throws BackingStoreException{ /* TODO Auto-generated method stub */ }

    @Override
    public void removeNodeChangeListener(NodeChangeListener arg0){ /* TODO Auto-generated method stub */ }

    @Override
    public void removePreferenceChangeListener(PreferenceChangeListener arg0){ /* TODO Auto-generated method stub */ }

    @Override
    public void sync() throws BackingStoreException{ /* TODO Auto-generated method stub */ }

    @Override
    public String toString(){ /* TODO Auto-generated method stub */ return null;}
}
