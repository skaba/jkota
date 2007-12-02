package com.googlecode.jkota;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Date;
import java.util.Properties;
import java.util.Timer;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

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
		t.schedule(new Updater(this),new Date(), 60000*getIntSetting("updateinterval",10));
	}
	
	public void storeSettings() throws IOException {
		BlowfishOutputStream out=new BlowfishOutputStream(
				masterKey,0,masterKey.length,
				new FileOutputStream(System.getProperty("user.home") + File.separator+".jkota")
		);
		settings.store(out,"");
		out.flush();
		out.close();
	}
	
	public void readSettings() throws IOException {
		File settingsFile=new File(System.getProperty("user.home") + File.separator+".jkota");
		if(!settingsFile.exists())
			firstTime();
		else
			settings.load(new BlowfishInputStream(masterKey,0,masterKey.length,new FileInputStream(settingsFile)));
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
	
	public static Logger getLogger() {
		Logger logger= Logger.getLogger("JKota");
		try {
			FileHandler fileHandler =new FileHandler(System.getProperty("user.home")+"/jkota.log",true);
			fileHandler.setFormatter(new SimpleFormatter());
			fileHandler.setLevel(Level.ALL);
			logger.addHandler(fileHandler);
			logger.setLevel(Level.ALL);
			return logger;
			
		} catch (SecurityException e) {
			logger.log(Level.SEVERE,"Kayıtçı alınamadı",e);
			System.exit(-1);
		} catch (IOException e) {
			logger.log(Level.SEVERE,"Kayıtçı alınamadı",e);
			System.exit(-1);
		}
		return null;
	}
}