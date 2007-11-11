package com.googlecode.jkota;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.util.TimerTask;

import org.xml.sax.SAXException;

import com.meterware.httpunit.HTMLElement;
import com.meterware.httpunit.HTMLElementPredicate;
import com.meterware.httpunit.WebConversation;
import com.meterware.httpunit.WebForm;
import com.meterware.httpunit.WebResponse;
import com.meterware.httpunit.WebTable;

public class Updater extends TimerTask {
	private WebConversation conversation;
	private BaseADSLKota adslKota;
	
	public Updater(BaseADSLKota adslKota) {
		this.adslKota=adslKota;
		conversation=new WebConversation();
		conversation.setHeaderField("User-agent","Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1)");
	}
	
	public String ExtractCaptcha() {
		try {
			WebResponse response=conversation.getResponse("http://adslkota.ttnet.net.tr/adslkota/jcaptcha");
			copyStream(response.getInputStream(), new FileOutputStream(System.getProperty("java.io.tmpdir")+"/captcha"));
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
			return captcha;
			
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}
	
	public void Auth(String username,String pass,String captcha) {
		try {
			WebResponse response =conversation.getResponse("http://adslkota.ttnet.net.tr/adslkota/loginSelf.do");
			WebForm login=response.getForms()[0];
			login.setParameter("userName", username);
			login.setParameter("password", pass);
			login.setParameter("captchaResponse", captcha);
			response=login.submit();
			response.getText();
			response =conversation.getResponse("http://adslkota.ttnet.net.tr/adslkota/confirmAgreement.do?dispatch=agree");
			response.getText();
			
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		}
	}
	
	public String Quota() {
		try {
			WebResponse response =conversation.getResponse("http://adslkota.ttnet.net.tr/adslkota/viewTransfer.do?dispatch=entry");
			String responseText=response.getText();
			if(responseText.indexOf("Sistem Hatası")>=0) {
				System.err.println("Sistem Hatası");
				return null;
			}
			if(responseText.indexOf("Oturum sonlandığından dolayı tekrar giriş yapmanız gerekmektedir.")>=0) {
				System.err.println("Güvenlik kodu doğru çözülemedi");
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
			return s;
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static void copyStream(InputStream in,OutputStream out) throws IOException {
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
			Auth(adslKota.getSetting("username"),adslKota.getSetting("password"),captcha);
			String quotaString=Quota();
			if(quotaString==null || quotaString.equals(""))
				continue;
			adslKota.updateKota(quotaString);
			break;
		}
	}
}