package com.googlecode.jkota.swing;

import java.io.IOException;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

import com.googlecode.jkota.BaseDownloader;
import com.googlecode.jkota.SettingsManager;

public class SwingSettings extends JDialog implements ActionListener {
	
	private JHelpText userName,password,apiKey;
	private JSpinner updateInterval;
	private JComboBox updaterList;
	private JLabel apiKeyLabel;
	private SettingsManager settings;
	
	public SwingSettings() {
		super((Frame)null,"Yapılandırma",true);
		settings=SettingsManager.getInstance();
		setLayout(new BorderLayout());
		JPanel settingsPanel=new JPanel(new GridLayout(0,2));
		settingsPanel.add(new JLabel("Servis sağlayıcı:"));
		updaterList = new JComboBox(BaseDownloader.getDownloaders());
		String updater=settings.getSetting("updater");
		if(updater!=null)
			updaterList.setSelectedItem(updater);
		updaterList.setEditable(false);
		updaterList.setActionCommand("list");
		updaterList.addActionListener(this);
		settingsPanel.add(updaterList);
		settingsPanel.add(new JLabel("Kullanıcı adı:"));
		userName=new JHelpText(settings.getSetting("username"),"http://code.google.com/p/jkota/wiki/KullanimDokumani",false);
		settingsPanel.add(userName);
		settingsPanel.add(new JLabel("Şifre:"));
		password=new JHelpText(settings.getSetting("password"),"http://code.google.com/p/jkota/wiki/KullanimDokumani",true);
		settingsPanel.add(password);
		apiKeyLabel=new JLabel("API Key:");
		settingsPanel.add(apiKeyLabel);
		apiKey=new JHelpText(settings.getSetting("apikey"),"http://code.google.com/p/jkota/wiki/KullanimDokumani",false);
		settingsPanel.add(apiKey);
		settingsPanel.add(new JLabel("Günceleme sıklığı:"));
		updateInterval=new JSpinner(new SpinnerNumberModel(settings.getIntSetting("updateinterval",10),10,120,10));
		JPanel updateIntervalPanel=new JPanel(new FlowLayout());
		updateIntervalPanel.add(updateInterval);
		updateIntervalPanel.add(new JLabel("dk."));
		settingsPanel.add(updateIntervalPanel);
		getContentPane().add(settingsPanel,BorderLayout.CENTER);
		JButton ok=new JButton("Tamam");
		ok.setActionCommand("ok");
		ok.addActionListener(this);
		getContentPane().add(ok,BorderLayout.SOUTH);
		setSize(300,200);
		setResizable(false);
		SwingUtil.center(this);
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		hideShowAPIKey();
		setVisible(true);
	}

	public void actionPerformed(ActionEvent e) {
		if("ok".equals(e.getActionCommand())) {
			settings.setSetting("username", userName.getText());
			settings.setSetting("password", password.getText());
			settings.setSetting("apikey", apiKey.getText());
			settings.setSetting("updater", (String)updaterList.getSelectedItem());
			settings.setIntSetting("updateinterval", (Integer)updateInterval.getModel().getValue());
			try {
				settings.storeSettings();
			} catch (IOException e1) {
				SwingUtil.error(this, e1,"Ayarlar kaydedilirken hata");
			}
			dispose();
		}
		else if("list".equals(e.getActionCommand()))
			hideShowAPIKey();
	}
	
	private void hideShowAPIKey() {
		if(updaterList.getSelectedItem().equals("TTNet ADSL")) {
			apiKeyLabel.setVisible(true);
			apiKey.setVisible(true);
		}
		else {
			apiKeyLabel.setVisible(false);
			apiKey.setVisible(false);
		}
	}
}
