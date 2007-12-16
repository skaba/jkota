package com.googlecode.jkota.updater;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.logging.Level;

import org.xml.sax.SAXException;

import com.googlecode.jkota.BaseKota;
import com.googlecode.jkota.BaseUpdater;
import com.meterware.httpunit.WebForm;
import com.meterware.httpunit.WebResponse;
import com.meterware.httpunit.WebTable;

public class KabloNetUpdater extends BaseUpdater {

	public KabloNetUpdater(BaseKota kota) {
		super(kota);
	}

	@Override
	public String getQuota() {
		try {
			WebResponse response =conversation.getResponse("http://online.turksat.com.tr/fatura/kota_takip.php");
			if(response.getText().indexOf("Bu sayfayı kullanabilmek için lüften giriş yapınız")>0) {
				logger.warning("Login problemi");
				return null;
			}
			WebTable list=response.getTables()[0];
			list.purgeEmptyCells();
			String s= list.getCellAsText(1,list.getColumnCount()-1);
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
	public boolean isUsingCaptcha() { return false;	}

	@Override
	public boolean login(String username, String password) {
		try {
			WebResponse response =conversation.getResponse("http://online.turksat.com.tr/fatura/login.php");
			WebForm login=response.getForms()[0];
			login.setParameter("user_name", username);
			login.setParameter("user_pass", password);
			response=login.submit();
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

}
