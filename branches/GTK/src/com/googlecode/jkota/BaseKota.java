package com.googlecode.jkota;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public abstract class BaseKota extends DownloadNotificationAdapter {
	
	public abstract String promptForMasterKey();
	public abstract void initUI();
	public abstract void showError(Exception e);
	public abstract void firstTime();
	public abstract void updateQuota(String quota);

	public BaseKota() {
		final SettingsManager settings=SettingsManager.getInstance();
		if(settings.isFirstTime())
			firstTime();
		else {
			settings.setMasterKey(promptForMasterKey());
			try {
				settings.readSettings();
			} catch (IOException e) {
				showError(e);
			}
		}
		BaseDownloader.getInstance(settings.getSetting("updater")).addDownloadNotificationListener(this);
		initUI();
		Timer t = new Timer();
		t.schedule(
			new TimerTask() {

				@Override
				public void run() {
					updateQuota("Kota güncelleniyor");
					BaseDownloader downloader=BaseDownloader.getInstance(settings.getSetting("updater"));
					while(true) {
						if (downloader.update())
							break;
					}
					updateQuota(downloader.getLastQuota());
				}
			},
			new Date(),
			60000*settings.getIntSetting("updateinterval",10)
		);
	}
}
