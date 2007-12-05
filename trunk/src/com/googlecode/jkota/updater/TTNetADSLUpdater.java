package com.googlecode.jkota.updater;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.logging.Level;

import org.xml.sax.SAXException;

import com.googlecode.jkota.BaseKota;
import com.googlecode.jkota.BaseUpdater;
import com.meterware.httpunit.HTMLElement;
import com.meterware.httpunit.HTMLElementPredicate;
import com.meterware.httpunit.WebForm;
import com.meterware.httpunit.WebResponse;
import com.meterware.httpunit.WebTable;

public class TTNetADSLUpdater extends BaseUpdater {
	
	public TTNetADSLUpdater(BaseKota kota) {
		super(kota);
	}

	@Override
	public boolean login(String username,String password) {
		try {
			WebResponse response =conversation.getResponse("http://adslkota.ttnet.net.tr/adslkota/loginSelf.do");
			WebForm login=response.getForms()[0];
			login.setParameter("userName", username);
			login.setParameter("password", password);
			login.setParameter("captchaResponse", getCaptcha());
			response=login.submit();
			String submitText=response.getText();
			if(submitText.indexOf("İşlem hatası")>=0) {
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
	
	public String getQuota() {
		try {
			WebResponse response =conversation.getResponse("http://adslkota.ttnet.net.tr/adslkota/viewTransfer.do?dispatch=entry");
			String responseText=response.getText();
			if(responseText.indexOf("Sistem Hatası")>=0) {
				logger.warning("Sistem Hatası");
				return null;
			}
			if(responseText.indexOf("Oturum sonlandığından dolayı tekrar giriş yapmanız gerekmektedir.")>=0) {
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
	
	@Override
	public boolean isUsingCaptcha() { return true; }
}
