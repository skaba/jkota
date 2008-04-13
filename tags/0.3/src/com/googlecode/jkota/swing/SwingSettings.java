package com.googlecode.jkota.swing;


import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

import com.googlecode.jkota.BaseKota;

public class SwingSettings extends JDialog implements ActionListener {
	
	private BaseKota kota;
	private JHelpText userName,password,apiKey;
	private JSpinner updateInterval;
	private JComboBox updaterList;
	private JLabel apiKeyLabel;
	
	public SwingSettings(BaseKota adslKota) {
		super((Frame)null,"Yapılandırma",true);
		this.kota=adslKota;
		setLayout(new BorderLayout());
		JPanel settingsPanel=new JPanel(new GridLayout(0,2));
		settingsPanel.add(new JLabel("Servis sağlayıcı:"));
		updaterList = new JComboBox(BaseKota.getUpdaters());
		String updater=kota.getSetting("updater");
		if(updater!=null)
			updaterList.setSelectedItem(updater);
		updaterList.setEditable(false);
		updaterList.setActionCommand("list");
		updaterList.addActionListener(this);
		settingsPanel.add(updaterList);
		settingsPanel.add(new JLabel("Kullanıcı adı:"));
		userName=new JHelpText(adslKota.getSetting("username"),"http://code.google.com/p/jkota/wiki/KullanimDokumani",false);
		settingsPanel.add(userName);
		settingsPanel.add(new JLabel("Şifre:"));
		password=new JHelpText(adslKota.getSetting("password"),"http://code.google.com/p/jkota/wiki/KullanimDokumani",true);
		settingsPanel.add(password);
		apiKeyLabel=new JLabel("API Key:");
		settingsPanel.add(apiKeyLabel);
		apiKey=new JHelpText(adslKota.getSetting("apikey"),"http://code.google.com/p/jkota/wiki/KullanimDokumani",false);
		settingsPanel.add(apiKey);
		settingsPanel.add(new JLabel("Günceleme sıklığı:"));
		updateInterval=new JSpinner(new SpinnerNumberModel(adslKota.getIntSetting("updateinterval",10),10,120,10));
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
			kota.setSetting("username", userName.getText());
			kota.setSetting("password", password.getText());
			kota.setSetting("apikey", apiKey.getText());
			kota.setSetting("updater", (String)updaterList.getSelectedItem());
			kota.setIntSetting("updateinterval", (Integer)updateInterval.getModel().getValue());
			try {
				kota.storeSettings();
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
