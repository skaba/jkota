package com.googlecode.jkota.swing;


import java.awt.AWTException;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URL;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import com.googlecode.jkota.BaseADSLKota;



public class SwingADSLKota extends BaseADSLKota implements ActionListener {

	private MenuItem quit,settings;
	private TrayIcon icon;
	
	public SwingADSLKota() {
		SystemTray tray =SystemTray.getSystemTray();
		PopupMenu trayMenu=new PopupMenu();
		settings=new MenuItem("Ayarlar");
		settings.addActionListener(this);
		trayMenu.add(settings);
		quit=new MenuItem("Çıkış");
		quit.addActionListener(this);
		trayMenu.add(quit);
		ClassLoader loader=getClass().getClassLoader();
		URL fileLocation=loader.getResource("favicon.gif");
		icon = new TrayIcon(Toolkit.getDefaultToolkit().getImage(fileLocation),null,trayMenu);
		try {
			tray.add(icon);
		} catch (AWTException e) {
			e.printStackTrace();
			System.exit(1);
		}
		String masterKey=null;
		while(masterKey==null)
			masterKey=JOptionPane.showInputDialog(null, "Ana şifre:");
		setMasterKey(masterKey);
		try {
			readSettings();
		} catch (IOException e) {
			SwingUtil.error(null, e,"Ayarlar okunurken hata");
		}
		startTimer();
	}
	
	public static void main(String[] args) {
		SwingUtilities.invokeLater
		(
				new Thread() {
					public void run() {
						new SwingADSLKota();
					}
				}
		);
	}

	public void actionPerformed(ActionEvent e) {
		if(e.getSource()==quit)
			System.exit(0);
		if(e.getSource()==settings)
			new SwingSettings(this);
	}

	@Override
	public void updateKota(String kota) {
		icon.setToolTip(kota);
	}

	@Override
	public void firstTime() {
		new SwingSettings(this);
	}
}
