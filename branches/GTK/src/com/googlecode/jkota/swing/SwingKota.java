package com.googlecode.jkota.swing;

import java.awt.AWTException;
import java.awt.Desktop;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import com.googlecode.jkota.BaseDownloader;
import com.googlecode.jkota.BaseKota;
import com.googlecode.jkota.DownloadNotificationEvent;
import com.googlecode.jkota.DownloadNotificationListener;
import com.googlecode.jkota.DownloadNotificationType;
import com.googlecode.jkota.LogManager;
import com.googlecode.jkota.SettingsManager;

public class SwingKota extends BaseKota implements ActionListener, ClipboardOwner,DownloadNotificationListener {

	private MenuItem quit,settings,about,logfile,clipboard,statistics;
	private TrayIcon icon;

	public static void main(String[] args) {
		if(!SystemTray.isSupported()) {
			JOptionPane.showMessageDialog(null,"Sistem tepsisi desteklenmiyor.\nJKota kapatılacak", "HATA", JOptionPane.ERROR_MESSAGE);
			System.exit(1);
		}

		SwingUtilities.invokeLater
		(
				new Thread() {
					@Override
					public void run() {
						new SwingKota();
					}
				}
		);
	}

	public void viewLogFile() {
		Desktop desktop = Desktop.getDesktop();
		try {
			desktop.open(new File(LogManager.LOG_FILE));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void actionPerformed(ActionEvent e) {
		if(e.getSource()==quit) {
			preQuit();
			System.exit(0);
		}
		if(e.getSource()==settings)
			new SwingSettings();
		if(e.getSource()==about)
			new SwingAbout();
		if(e.getSource()==logfile) {
			viewLogFile();
		}
		if(e.getSource()==clipboard) {
			StringSelection stringSelection = new StringSelection(icon.getToolTip());
			Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
			clipboard.setContents( stringSelection, this );
		}
		if(e.getSource()==statistics) {
			BaseDownloader downloader=BaseDownloader.getInstance(SettingsManager.getInstance().getSetting("updater"));
			if(downloader.getQuotaSize()==0)
				SwingUtil.error(null, "Kota bilgileri alınmadan istatistikleri göremezsiniz", "HATA");
			else
				new SwingStats();
		}
	}

	@Override
	public void updateQuota(String quota) {
		icon.setToolTip(quota);
	}

	@Override
	public void firstTime() {
		new SwingSettings();
	}

	public void lostOwnership(Clipboard clipboard, Transferable contents) {}

	@Override
	public void initUI() {
		SystemTray tray =SystemTray.getSystemTray();
		PopupMenu trayMenu=new PopupMenu();
		settings=createMenuItem("Ayarlar", trayMenu);
		logfile=createMenuItem("Günlük", trayMenu);
		clipboard=createMenuItem("Panoya kopyala", trayMenu);
		statistics=createMenuItem("İstatistikler", trayMenu);
		about=createMenuItem("Hakkında", trayMenu);
		quit=createMenuItem("Çıkış", trayMenu);
		ClassLoader loader=getClass().getClassLoader();
		URL fileLocation=loader.getResource("favicon.png");
		icon = new TrayIcon(Toolkit.getDefaultToolkit().getImage(fileLocation),null,trayMenu);
		icon.setImageAutoSize(true);
		try {
			tray.add(icon);
		} catch (AWTException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	private MenuItem createMenuItem(String label,PopupMenu trayMenu) {
		MenuItem item=new MenuItem(label);
		item.addActionListener(this);
		trayMenu.add(item);
		return item;
	}

	@Override
	public String promptForMasterKey() {
		String masterKey=null;
		while(masterKey==null)
			masterKey=JOptionPane.showInputDialog(null, "Ana şifre:");
		return masterKey;
	}

	@Override
	public void showError(Exception e) {
		SwingUtil.error(null, e,"HATA");
	}

	@Override
	public void notification(DownloadNotificationEvent e) {
		if(e.getType()==DownloadNotificationType.WARNING)
			icon.displayMessage("UYARI", e.getMessage(),TrayIcon.MessageType.WARNING);
		if(e.getType()==DownloadNotificationType.INFO)
			icon.displayMessage("BİLGİ", e.getMessage(),TrayIcon.MessageType.INFO);
	}
}