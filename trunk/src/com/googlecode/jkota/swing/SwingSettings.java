package com.googlecode.jkota.swing;


import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;

import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

import com.googlecode.jkota.BaseADSLKota;

public class SwingSettings extends JDialog implements ActionListener {
	
	private BaseADSLKota adslKota;
	private JHelpText userName,password,apiKey;
	private JSpinner updateInterval;
	
	public SwingSettings(BaseADSLKota adslKota) {
		super((Frame)null,"Yapılandırma",true);
		this.adslKota=adslKota;
		setLayout(new BorderLayout());
		JPanel settingsPanel=new JPanel(new GridLayout(0,2));
		settingsPanel.add(new JLabel("Kullanıcı adı:"));
		userName=new JHelpText(adslKota.getSetting("username"),"http://code.google.com/p/jkota/wiki/KullanimDokumani",false);
		settingsPanel.add(userName);
		settingsPanel.add(new JLabel("Şifre:"));
		password=new JHelpText(adslKota.getSetting("password"),"http://code.google.com/p/jkota/wiki/KullanimDokumani",true);
		settingsPanel.add(password);
		settingsPanel.add(new JLabel("API Key:"));
		apiKey=new JHelpText(adslKota.getSetting("apikey"),"http://code.google.com/p/jkota/wiki/KullanimDokumani",false);
		settingsPanel.add(apiKey);
		settingsPanel.add(new JLabel("Günceleme sıklığı:"));
		updateInterval=new JSpinner(new SpinnerNumberModel(adslKota.getIntSetting("updateinterval"),10,120,10));
		JPanel updateIntervalPanel=new JPanel(new FlowLayout());
		updateIntervalPanel.add(updateInterval);
		updateIntervalPanel.add(new JLabel("dk."));
		settingsPanel.add(updateIntervalPanel);
		getContentPane().add(settingsPanel,BorderLayout.CENTER);
		JButton ok=new JButton("Tamam");
		ok.addActionListener(this);
		getContentPane().add(ok,BorderLayout.SOUTH);
		setSize(300,200);
		setResizable(false);
		SwingUtil.center(this);
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		setVisible(true);
	}

	public void windowActivated(WindowEvent e) {}
	public void windowClosed(WindowEvent e) {}
	public void windowDeactivated(WindowEvent e) {}
	public void windowDeiconified(WindowEvent e) {}
	public void windowIconified(WindowEvent e) {}
	public void windowOpened(WindowEvent e) {}
	
	public void windowClosing(WindowEvent e) {
		
	}

	public void actionPerformed(ActionEvent e) {
		adslKota.setSetting("username", userName.getText());
		adslKota.setSetting("password", password.getText());
		adslKota.setSetting("apikey", apiKey.getText());
		adslKota.setIntSetting("updateinterval", (Integer)updateInterval.getModel().getValue());
		try {
			adslKota.storeSettings();
		} catch (IOException e1) {
			SwingUtil.error(this, e1,"Ayarlar kaydedilirken hata");
		}
		dispose();
	}	
}
