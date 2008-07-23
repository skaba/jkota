package com.googlecode.jkota.downloader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.util.StringTokenizer;

import org.xml.sax.SAXException;

import com.googlecode.jkota.BaseDownloader;
import com.googlecode.jkota.LogManager;
import com.googlecode.jkota.QuotaInfo;
import com.googlecode.jkota.SettingsManager;
import com.meterware.httpunit.HTMLElement;
import com.meterware.httpunit.HTMLElementPredicate;
import com.meterware.httpunit.WebForm;
import com.meterware.httpunit.WebResponse;
import com.meterware.httpunit.WebTable;

public class TTNetADSLDownloader extends BaseDownloader {

	@Override
	public boolean downloadQuota() {
		LogManager logger=LogManager.getInstance();
		try {
			WebResponse response =conversation.getResponse("http://adslkota.ttnet.net.tr/adslkota/viewTransfer.do?dispatch=entry");
			String responseText=response.getText();
			if(responseText.indexOf("Sistem Hatası")>=0) {
				logger.debug("Sistem Hatası");
				return false;
			}
			if(responseText.indexOf("Oturum sonlandığından dolayı tekrar giriş yapmanız gerekmektedir.")>=0) {
				logger.debug("Oturum sonlanmış");
				return false;
			}
			WebTable list=response.getFirstMatchingTable(
					new HTMLElementPredicate() {

						public boolean matchesCriteria(Object arg0, Object arg1) {
							HTMLElement element = (HTMLElement)arg0;
							if (element.getClassName().equals("tblList"))
								return true;
							else
								return false;
						}
					}, null);
			list.purgeEmptyCells();
			//String s= list.getCellAsText(list.getRowCount()-1,list.getColumnCount()-1);
			quotas=new QuotaInfo[list.getRowCount()-2];
			for(int i=0;i<quotas.length;i++) {
				QuotaInfo info=new QuotaInfo
				(
					list.getCellAsText(i+2, 1),
					parseQuotaString(list.getCellAsText(i+2, 3)),
					parseQuotaString(list.getCellAsText(i+2, 2))
				);
				quotas[i]=info;
			}
			lastQuota="Download: "+list.getCellAsText(list.getRowCount()-1,3)+" Upload: "+list.getCellAsText(list.getRowCount()-1,2);
			response =conversation.getResponse("http://adslkota.ttnet.net.tr/adslkota/logout.do");
			logger.info("Kota alındı");
			return true;
		} catch (MalformedURLException e) {
			logger.debug("Kota alınırken hata",e);
		} catch (IOException e) {
			logger.debug("Kota alınırken hata",e);
		} catch (SAXException e) {
			logger.debug("Kota alınırken hata",e);
		}
		return false;
	
	}

	private long parseQuotaString(String quota) {
		StringTokenizer tokenizer=new StringTokenizer(quota);
		String token=tokenizer.nextToken();
		String quotaString=token.replaceAll("\\D", "");
		return Long.parseLong(quotaString);
	}

	@Override
	public boolean login(String username, String password) {
		String captcha=extractCaptcha();
		if(!"".equals(captcha)) {
			LogManager logManager=LogManager.getInstance();
			try {

				WebResponse response =conversation.getResponse("http://adslkota.ttnet.net.tr/adslkota/login_tr.jsp");
				WebForm login=response.getForms()[0];
				login.setParameter("userName", username);
				login.setParameter("password", password);
				login.setParameter("captchaResponse", captcha);
				response=login.submit();
				String submitText=response.getText();
				if(submitText.indexOf("İşlem hatası")>=0) {
					logManager.debug("Login problemi");
					return false;
				}
				conversation.getResponse("http://adslkota.ttnet.net.tr/adslkota/confirmAgreement.do?dispatch=agree");
				logManager.info("Login olundu");
				return true;
			} catch (MalformedURLException e) {
				logManager.debug("Login olurken hata",e);
			} catch (IOException e) {
				logManager.debug("Login olurken hata",e);
			} catch (SAXException e) {
				logManager.debug("Login olurken hata",e);
			}
		}
		return false;
	}

	private String extractCaptcha() {
		if (downloadCaptcha()) {
			LogManager logger=LogManager.getInstance();
			try {
				SettingsManager settings=SettingsManager.getInstance();
				WebResponse response=conversation.getResponse("http://jkota.googlecode.com/svn/trunk/uploadform.html");
				WebForm upload_captcha=response.getForms()[0];
				upload_captcha.setParameter("api_key", settings.getSetting("apikey"));
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
	
	private boolean downloadCaptcha() {
		LogManager logger=LogManager.getInstance();
		try {
			WebResponse response = conversation.getResponse("http://adslkota.ttnet.net.tr/adslkota/jcaptcha");
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
