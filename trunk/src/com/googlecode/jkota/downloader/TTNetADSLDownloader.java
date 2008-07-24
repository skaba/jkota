package com.googlecode.jkota.downloader;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.StringTokenizer;

import org.xml.sax.SAXException;

import com.googlecode.jkota.BaseDownloader;
import com.googlecode.jkota.LogManager;
import com.googlecode.jkota.QuotaInfo;
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
			quotas=new QuotaInfo[list.getRowCount()-2];
			for(int i=0;i<quotas.length;i++) {
				QuotaInfo info=new QuotaInfo
				(
					list.getCellAsText(i+2, 0)+" "+list.getCellAsText(i+2, 1),
					parseQuotaString(list.getCellAsText(i+2, 3)),
					parseQuotaString(list.getCellAsText(i+2, 2))
				);
				quotas[i]=info;
			}
			lastQuota="Download: "+list.getCellAsText(list.getRowCount()-1,3)+" Upload: "+list.getCellAsText(list.getRowCount()-1,2);
			response =conversation.getResponse("http://adslkota.ttnet.net.tr/adslkota/logout.do");
			logger.info("Kota isteği: Başarılı ("+ lastQuota+")");
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
		String captcha=extractCaptcha("http://adslkota.ttnet.net.tr/adslkota/jcaptcha");
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

	
}
