package com.googlecode.jkota;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Properties;

import net.sourceforge.blowfishj.BlowfishInputStream;
import net.sourceforge.blowfishj.BlowfishOutputStream;

public class SettingsManager {
	private Properties settings;
	private byte masterKey[];
	protected static String SETTINGS_FILE=System.getProperty("user.home")+ File.separator+".jkota";
	
	private static SettingsManager theInstance=new SettingsManager();
	
	private SettingsManager() {
		masterKey=new byte[0];
		settings=new Properties();
	}
	
	public void storeSettings() throws IOException {
		BlowfishOutputStream out=new BlowfishOutputStream(
				masterKey,0,masterKey.length,
				new FileOutputStream(SETTINGS_FILE)
		);
		settings.store(out,"");
		out.flush();
		out.close();
	}
	
	public void readSettings() throws IOException {
		File settingsFile=new File(SETTINGS_FILE);
		settings.load(new BlowfishInputStream(masterKey,0,masterKey.length,new FileInputStream(settingsFile)));
	}
	
	public boolean isFirstTime() {
		File settingsFile=new File(SETTINGS_FILE);
		return !settingsFile.exists();
	}
	
	public final void setMasterKey(String masterKey) {
		this.masterKey = masterKey.getBytes(Charset.forName("UTF-8"));
	}
	
	public void setSetting(String key,String value) { settings.setProperty(key, value); }
	public String getSetting(String key) { return settings.getProperty(key); }
	public void setIntSetting(String key,int value) { settings.setProperty(key, Integer.toString(value)); }
	public int getIntSetting(String key,int defaultSetting) {
		String valueString=settings.getProperty(key);
		if(valueString==null)
			return defaultSetting;
		else
			return Integer.parseInt(valueString); 
	}
	
	public static SettingsManager getInstance() { return theInstance; }
}
