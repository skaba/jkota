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

import com.meterware.httpunit.HttpUnitOptions;
import com.meterware.httpunit.WebConversation;
import com.meterware.httpunit.WebForm;
import com.meterware.httpunit.WebResponse;


public abstract class BaseUpdater extends TimerTask {
	
	private String captcha;
	protected Logger logger;
	protected WebConversation conversation;
	
	public abstract boolean isUsingCaptcha();
	public abstract boolean login(String username,String password);
	public abstract String getQuota();
	
	private BaseKota adslKota;
	public BaseUpdater(BaseKota kota) {
		this.adslKota=kota;
		conversation=new WebConversation();
		conversation.setHeaderField("User-agent","Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1)");
		logger=BaseKota.getLogger();
		HttpUnitOptions.setScriptingEnabled(false);//Disable Javascript parsing
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
	
	public final String extractCaptcha() {
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
	
	protected String getCaptcha() { return captcha; }
	
	@Override
	public final void run() {
		while(!runNUpdateQuota());

	}
	
	private boolean runNUpdateQuota() {
		if(isUsingCaptcha())
			captcha=extractCaptcha();
		if (!login(adslKota.getSetting("username"),adslKota.getSetting("password")))
			return false;
		String quotaString=getQuota();
		if(quotaString==null || quotaString.equals(""))
			return false;
		adslKota.updateKota(quotaString);
		return true;
	}

}