package com.googlecode.jkota;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.xml.sax.SAXException;

import com.meterware.httpunit.HTMLElement;
import com.meterware.httpunit.HTMLElementPredicate;
import com.meterware.httpunit.HttpUnitOptions;
import com.meterware.httpunit.WebConversation;
import com.meterware.httpunit.WebForm;
import com.meterware.httpunit.WebResponse;
import com.meterware.httpunit.WebTable;

public class Updater extends TimerTask {
	private WebConversation conversation;
	private BaseADSLKota adslKota;
	private Logger logger;
	//private static Logger logger= Logger.getLogger("com.googlecode.jkota.Updater");
	public Updater(BaseADSLKota adslKota) {
		this.adslKota=adslKota;
		conversation=new WebConversation();
		conversation.setHeaderField("User-agent","Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1)");
		logger=BaseADSLKota.getLogger();
		HttpUnitOptions.setScriptingEnabled(false);//Disable Javascript parsing
	}
	
	public String ExtractCaptcha() {
		try {
			WebResponse response=conversation.getResponse("http://adslkota.ttnet.net.tr/adslkota/jcaptcha");
			copyStream(response.getInputStream(), new FileOutputStream(System.getProperty("java.io.tmpdir")+"/captcha"));
			logger.info("Captcha alındı");
			response=conversation.getResponse("http://jkota.googlecode.com/svn/trunk/uploadform.html");
			WebForm upload_captcha=response.getForms()[0];
			upload_captcha.setParameter("api_key", adslKota.getSetting("apikey"));
			upload_captcha.setParameter("file", new File(System.getProperty("java.io.tmpdir")+"/captcha"));
			String guid="";
			while(guid.equals("")) {
				response=upload_captcha.submit();
				String responseText=response.getText();
				if(responseText.startsWith("SUCCESS: captcha_id="))
					guid=responseText.substring(20);
					
			}
			logger.info("Captcha gönderildi");
			guid=guid.substring(0,36);
			response=conversation.getResponse("http://jkota.googlecode.com/svn/trunk/resultform.html");
			WebForm get_result=response.getForms()[0];
			get_result.setParameter("api_key", adslKota.getSetting("apikey"));
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
			}
			(new File(System.getProperty("java.io.tmpdir")+"/captcha")).delete();
			logger.info("Captcha çözüldü: "+captcha);
			return captcha;
		} catch (MalformedURLException e) {
			logger.log(Level.WARNING,"Güvenlik kodu çözülürken hata",e);
		} catch (FileNotFoundException e) {
			logger.log(Level.WARNING,"Güvenlik kodu çözülürken hata",e);
		} catch (IOException e) {
			logger.log(Level.WARNING,"Güvenlik kodu çözülürken hata",e);
		} catch (SAXException e) {
			logger.log(Level.WARNING,"Güvenlik kodu çözülürken hata",e);
		} catch (InterruptedException e) {
			logger.log(Level.WARNING,"Güvenlik kodu çözülürken hata",e);
		}
		(new File(System.getProperty("java.io.tmpdir")+"/captcha")).delete();
		return "";
	}
	
	public boolean Auth(String username,String pass,String captcha) {
		try {
			WebResponse response =conversation.getResponse("http://adslkota.ttnet.net.tr/adslkota/loginSelf.do");
			WebForm login=response.getForms()[0];
			login.setParameter("userName", username);
			login.setParameter("password", pass);
			login.setParameter("captchaResponse", captcha);
			response=login.submit();
			String submitText=response.getText();
			if(submitText.indexOf("İşlem hatası")>=0) {
				System.out.println("Login problemi");
				logger.warning("Login problemi");
				return false;
			}
			conversation.getResponse("http://adslkota.ttnet.net.tr/adslkota/confirmAgreement.do?dispatch=agree");
			logger.info("Login olundu");
			return true;
		} catch (MalformedURLException e) {
			logger.log(Level.WARNING,"Login olurken hata",e);
		} catch (IOException e) {
			logger.log(Level.WARNING,"Login olurken hata",e);
		} catch (SAXException e) {
			logger.log(Level.WARNING,"Login olurken hata",e);
		}
		return false;
	}
	
	public String Quota() {
		try {
			WebResponse response =conversation.getResponse("http://adslkota.ttnet.net.tr/adslkota/viewTransfer.do?dispatch=entry");
			String responseText=response.getText();
			if(responseText.indexOf("Sistem Hatası")>=0) {
				System.err.println("Sistem Hatası");
				logger.warning("Sistem Hatası");
				return null;
			}
			if(responseText.indexOf("Oturum sonlandığından dolayı tekrar giriş yapmanız gerekmektedir.")>=0) {
				System.err.println("Oturum sonlanmış");
				logger.warning("Oturum sonlanmış");
				return null;
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
			String s= list.getCellAsText(list.getRowCount()-1,list.getColumnCount()-1);
			response =conversation.getResponse("http://adslkota.ttnet.net.tr/adslkota/logout.do");
			logger.info("Kota alındı");
			return s;
		} catch (MalformedURLException e) {
			logger.log(Level.WARNING,"Kota alınırken hata",e);
		} catch (IOException e) {
			logger.log(Level.WARNING,"Kota alınırken hata",e);
		} catch (SAXException e) {
			logger.log(Level.WARNING,"Kota alınırken hata",e);
		}
		return null;
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

	public void run() {
		while(true) {
			String captcha=ExtractCaptcha();
			if(!Auth(adslKota.getSetting("username"),adslKota.getSetting("password"),captcha))
				continue;
			String quotaString=Quota();
			if(quotaString==null || quotaString.equals(""))
				continue;
			adslKota.updateKota(quotaString);
			break;
		}
		System.gc();//Garbage collect at the end of update
	}
}
