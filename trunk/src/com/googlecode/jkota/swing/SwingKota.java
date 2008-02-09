package com.googlecode.jkota.swing;

import java.awt.AWTException;
import java.awt.Image;
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
import java.io.IOException;
import java.net.URL;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import com.googlecode.jkota.BaseKota;



public class SwingKota extends BaseKota implements ActionListener, ClipboardOwner {

	private MenuItem quit,settings,about,logfile,clipboard;
	private TrayIcon icon;
	
	public SwingKota() {
		String masterKey=null;
		while(masterKey==null)
			masterKey=JOptionPane.showInputDialog(null, "Ana şifre:");
		setMasterKey(masterKey);
		try {
			readSettings();
		} catch (IOException e) {
			SwingUtil.error(null, e,"Ayarlar okunurken hata");
		}
		SystemTray tray =SystemTray.getSystemTray();
		PopupMenu trayMenu=new PopupMenu();
		settings=new MenuItem("Ayarlar");
		settings.addActionListener(this);
		trayMenu.add(settings);
		logfile=new MenuItem("Günlük");
		logfile.addActionListener(this);
		trayMenu.add(logfile);
		clipboard=new MenuItem("Panoya kopyala");
		clipboard.addActionListener(this);
		trayMenu.add(clipboard);
		about=new MenuItem("Hakkında");
		about.addActionListener(this);
		trayMenu.add(about);
		quit=new MenuItem("Çıkış");
		quit.addActionListener(this);
		trayMenu.add(quit);
		ClassLoader loader=getClass().getClassLoader();
		URL fileLocation=loader.getResource("favicon.png");
		int width=tray.getTrayIconSize().width;
		int height=tray.getTrayIconSize().height;
		Image orj = Toolkit.getDefaultToolkit().getImage(fileLocation);
		icon = new TrayIcon(orj.getScaledInstance(width, height, Image.SCALE_SMOOTH),null,trayMenu);
		try {
			tray.add(icon);
		} catch (AWTException e) {
			e.printStackTrace();
			System.exit(1);
		}
		startTimer();
	}
	
	public static void main(String[] args) {
		SwingUtilities.invokeLater
		(
				new Thread() {
					public void run() {
						new SwingKota();
					}
				}
		);
	}

	public void actionPerformed(ActionEvent e) {
		if(e.getSource()==quit)
			System.exit(0);
		if(e.getSource()==settings)
			new SwingSettings(this);
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
	}

	@Override
	public void updateKota(String kota) {
		icon.setToolTip(kota);
	}

	@Override
	public void firstTime() {
		new SwingSettings(this);
	}

	public void lostOwnership(Clipboard clipboard, Transferable contents) {}
}
