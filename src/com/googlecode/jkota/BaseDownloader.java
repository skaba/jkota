package com.googlecode.jkota;

import com.googlecode.jkota.downloader.KabloNetDownloader;
import com.meterware.httpunit.HttpUnitOptions;
import com.meterware.httpunit.WebConversation;

public abstract class BaseDownloader {
	protected String lastQuota="";
	protected QuotaInfo[] quotas;
	protected WebConversation conversation;
	//public abstract boolean isUsingCaptcha();
	public abstract boolean login(String username,String password);
	public abstract boolean downloadQuota();
	
	protected BaseDownloader() {
		quotas=new QuotaInfo[0];
		conversation=new WebConversation();
		conversation.setHeaderField("User-agent","Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1)");
		//logger=BaseKota.getLogger();
		HttpUnitOptions.setScriptingEnabled(false);//Disable Javascript parsing
	}
	
	public String getLastQuota() { return lastQuota; }
	public int getQuotaSize() { return quotas.length; }
	public QuotaInfo getQuota(int i) { return quotas[i]; }
	
	private static BaseDownloader theInstance;
	private static String currentInstanceName="";
	
	public static BaseDownloader getInstance(String name) {
		if(!currentInstanceName.equals(name)) {
			/*
			if("TTNet ADSL".equals(updaterName))
				return new TTNetADSLUpdater(this);
			else if("Kablo Net".equals(updaterName))
				return new KabloNetUpdater(this);
			*/
			if("Kablo Net".equals(name))
				theInstance=new KabloNetDownloader();
		}
		currentInstanceName=name;
		return theInstance;
	}
	
	public synchronized boolean update() {
		SettingsManager settings=SettingsManager.getInstance();
		if (!login(settings.getSetting("username"),settings.getSetting("password")))
			return false;
		if(!downloadQuota())
			return false;
		return true;
	}
	
	public static String[] getDownloaders() {
		return new String[] {"TTNet ADSL","Kablo Net"};
	}
	
}
