package com.googlecode.jkota.downloader;

import java.io.IOException;
import java.net.MalformedURLException;

import org.xml.sax.SAXException;

import com.googlecode.jkota.BaseDownloader;
import com.googlecode.jkota.LogManager;
import com.googlecode.jkota.QuotaInfo;
import com.googlecode.jkota.Unit;
import com.meterware.httpunit.HttpException;
import com.meterware.httpunit.WebForm;
import com.meterware.httpunit.WebResponse;
import com.meterware.httpunit.WebTable;

public class KabloNetDownloader extends BaseDownloader {

	@Override
	public boolean login(String username, String password) {
		LogManager logManager=LogManager.getInstance();
		try {
			WebResponse response =conversation.getResponse("http://online.turksat.com.tr/fatura/login.php");
			WebForm login=response.getForms()[0];
			login.setParameter("user_name", username);
			login.setParameter("user_pass", password);
			response=login.submit();
			return true;
		} catch (MalformedURLException e) {
			logManager.debug("Login olurken hata",e);
		} catch (IOException e) {
			logManager.debug("Login olurken hata",e);
		} catch (SAXException e) {
			logManager.debug("Login olurken hata",e);
		}
		catch (HttpException e) {
			logManager.debug("Login olurken hata",e);
		}
		logManager.info("Kota isteği: Başarısız (Login hatası)");
		return false;
	}
	
	@Override
	public boolean downloadQuota() {
		LogManager logManager=LogManager.getInstance();
		try {
			WebResponse response =conversation.getResponse("http://online.turksat.com.tr/fatura/kota_takip.php");
			if(response.getText().indexOf("Bu sayfayı kullanabilmek için lüften giriş yapınız")>0) {
				logManager.info("Kota isteği: Başarısız (Login problemi)");
				return false;
			}
			WebTable list=response.getTables()[0];
			list.purgeEmptyCells();
			quotas=new QuotaInfo[list.getRowCount()-1];
			for(int i=0;i<quotas.length;i++) {
				QuotaInfo info=new QuotaInfo
				(
					list.getCellAsText(list.getRowCount()-i-1, 0),
					parseQuotaString(list.getCellAsText(list.getRowCount()-i-1, 2)),
					parseQuotaString(list.getCellAsText(list.getRowCount()-i-1, 1))
				);
				quotas[i]=info;
			}
			lastQuota= "Download: "+list.getCellAsText(1,2)+" Upload: "+list.getCellAsText(1,1);
			
			logManager.info("Kota isteği: Başarılı ("+ lastQuota+")");
			return true;
		} catch (MalformedURLException e) {
			logManager.debug("Kota alınırken hata",e);
		} catch (IOException e) {
			logManager.debug("Kota alınırken hata",e);
		} catch (SAXException e) {
			logManager.debug("Kota alınırken hata",e);
		}
		logManager.info("Kota isteği: Başarısız (Kota alımında hata)");
		return false;
	}
	
	private long parseQuotaString(String quota) {
		double value=Double.parseDouble(quota.substring(0,quota.indexOf(' ')));
		return Math.round(value*Unit.valueOf(quota.substring(quota.length()-2)).getDivider());
	}

}
