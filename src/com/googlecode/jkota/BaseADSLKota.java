package com.googlecode.jkota;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Date;
import java.util.Properties;
import java.util.Timer;


import net.sourceforge.blowfishj.BlowfishInputStream;
import net.sourceforge.blowfishj.BlowfishOutputStream;

public abstract class BaseADSLKota {
	
	public abstract void updateKota(String kota);
	public abstract void firstTime();
	private Properties settings;
	private byte masterKey[];
	
	public BaseADSLKota() {
		masterKey=new byte[0];
		settings=new Properties();
	}
	
	public final void setMasterKey(String masterKey) {
		this.masterKey = masterKey.getBytes(Charset.forName("UTF-8"));
	}

	public final void startTimer() {
		Timer t = new Timer();
		t.schedule(new Updater(this),new Date(), 60000);
	}
	
	public void storeSettings() throws IOException {
		settings.store(
				new BlowfishOutputStream(
						masterKey,0,masterKey.length,
						new FileOutputStream(System.getProperty("user.home") + File.separator+".jkota")
				),
				""
		);
	}
	
	public void readSettings() throws IOException {
		File settingsFile=new File(System.getProperty("user.home") + File.separator+".jkota");
		if(!settingsFile.exists())
			firstTime();
		settings.load(new BlowfishInputStream(masterKey,0,masterKey.length,new FileInputStream(settingsFile)));
	}
	
	public void setSetting(String key,String value) { settings.setProperty(key, value); }
	public String getSetting(String key) { return settings.getProperty(key); }
}
