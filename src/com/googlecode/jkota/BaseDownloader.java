package com.googlecode.jkota;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;

import org.xml.sax.SAXException;

import com.googlecode.jkota.downloader.KabloNetDownloader;
import com.googlecode.jkota.downloader.TTNetADSLDownloader;
import com.meterware.httpunit.HttpUnitOptions;
import com.meterware.httpunit.WebConversation;
import com.meterware.httpunit.WebForm;
import com.meterware.httpunit.WebResponse;

public abstract class BaseDownloader {
	protected String lastQuota="";
	protected QuotaInfo[] quotas;
	protected WebConversation conversation;
	private Unit viewUnit;
	public abstract boolean login(String username,String password);
	public abstract boolean downloadQuota();

	protected BaseDownloader() {
		quotas=new QuotaInfo[0];
		conversation=new WebConversation();
		conversation.setHeaderField("User-agent","Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1)");
		HttpUnitOptions.setScriptingEnabled(false);//Disable Javascript parsing
	}

	public String getLastQuota() { return lastQuota; }
	public int getQuotaSize() { return quotas.length; }
	public QuotaInfo getQuota(int i) { return quotas[i]; }
	public Unit getViewUnit() { return viewUnit; }

	private static BaseDownloader theInstance;
	private static String currentInstanceName;

	public static synchronized BaseDownloader getInstance(String name) {
		if(theInstance==null || !currentInstanceName.equals(name)) {
			if("TTNet ADSL".equals(name))
				theInstance= new TTNetADSLDownloader();
			else if("Kablo Net".equals(name))
				theInstance= new KabloNetDownloader();
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
		viewUnit=calculateUnit();
		return true;
	}

	public static String[] getDownloaders() {
		return new String[] {"TTNet ADSL","Kablo Net"};
	}

	private Unit calculateUnit() {
		long max=0L;
		for(int i=0;i<quotas.length;i++) {
			QuotaInfo info = quotas[i];
			if(info.getDownloadedBytes()>max)
				max=info.getDownloadedBytes();
			if(info.getUploadedBytes()>max)
				max=info.getUploadedBytes();
		}
		Unit units[]=Unit.values();
		for(int i=units.length-1;i>=0;i--) {
			if(max>units[i].getDivider())
				return units[i];
		}
		return Unit.BYTE;
	}

	private boolean downloadCaptcha(String url) {
		LogManager logger=LogManager.getInstance();
		try {
			WebResponse response = conversation.getResponse(url);
			copyStream(response.getInputStream(), new FileOutputStream(System.getProperty("java.io.tmpdir")+"/captcha"));
			logger.info("Captcha alındı");
			return true;
		} catch (MalformedURLException e) {
			logger.debug("Güvenlik kodu indirilirken hata",e);
		} catch (IOException e) {
			logger.debug("Güvenlik kodu indirilirken hata",e);
		} catch (SAXException e) {
			logger.debug("Güvenlik kodu indirilirken hata",e);
		}
		return false;
	}

	protected String extractCaptcha(String url) {
		if (downloadCaptcha(url)) {
			LogManager logger=LogManager.getInstance();
			try {
				SettingsManager settings=SettingsManager.getInstance();
				WebResponse response=conversation.getResponse("http://jkota.googlecode.com/svn/trunk/uploadform.html");
				WebForm upload_captcha=response.getForms()[0];
				upload_captcha.setParameter("api_key", settings.getSetting("apikey"));
				upload_captcha.setParameter("captcha_url",url);
				upload_captcha.setParameter("file", new File(System.getProperty("java.io.tmpdir")+"/captcha"));
				String guid="";
				while(guid.equals("")) {
					response=upload_captcha.submit();
					String responseText=response.getText();
					if(responseText.startsWith("SUCCESS: captcha_id="))
						guid=responseText.substring(20);
					else {
						logger.debug
						(
								"Güvenlik kodu gönderilirken hata: "+
								responseText.substring(9)
						);
						return "";
					}
				}
				logger.info("Captcha gönderildi");
				guid=guid.substring(0,36);
				response=conversation.getResponse("http://jkota.googlecode.com/svn/trunk/resultform.html");
				WebForm get_result=response.getForms()[0];
				get_result.setParameter("api_key", settings.getSetting("apikey"));
				get_result.setParameter("captcha_id", guid);
				String captcha="";
				while(captcha.equals("")) {
					response=get_result.submit();
					String responseText=response.getText();
					if(responseText.startsWith("SUCCESS: captcha_result=")) {
						int start=responseText.indexOf("\"")+1;
						int end=responseText.indexOf("\"", start);
						captcha=responseText.substring(start,end).toLowerCase();
					}
					else if(responseText.startsWith("WAIT")) {
						Thread.sleep(10000);
					}
					else if(responseText.startsWith("FAILURE")) {
						logger.debug
						(
								"Güvenlik kodu çözülürken hata: "+
								responseText.substring(9)
						);
						break;
					}
				}
				(new File(System.getProperty("java.io.tmpdir")+"/captcha")).delete();
				if (!"".equals(captcha))
					logger.info("Captcha çözüldü: "+captcha);
				return captcha;
			} catch (MalformedURLException e) {
				logger.debug("Güvenlik kodu çözülürken hata",e);
			} catch (FileNotFoundException e) {
				logger.debug("Güvenlik kodu çözülürken hata",e);
			} catch (IOException e) {
				logger.debug("Güvenlik kodu çözülürken hata",e);
			} catch (SAXException e) {
				logger.debug("Güvenlik kodu çözülürken hata",e);
			} catch (InterruptedException e) {
				logger.debug("Güvenlik kodu çözülürken hata",e);
			}
			(new File(System.getProperty("java.io.tmpdir")+"/captcha")).delete();
			return "";
		}
		return "";
	}

	private void copyStream(InputStream in,OutputStream out) throws IOException {
		byte buf[]=new byte[8*1024];
		while(true) {
			int read=in.read(buf);
			if(read==-1)
				break;
			out.write(buf, 0, read);
		}
		out.flush();
	}
}