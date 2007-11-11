package com.googlecode.jkota.swing;


import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import com.googlecode.jkota.BaseADSLKota;




public class SwingSettings extends JDialog implements /*WindowListener,*/ ActionListener {
	
	private BaseADSLKota adslKota;
	private JHelpText userName,password,apiKey;
	
	public SwingSettings(BaseADSLKota adslKota) {
		super((Frame)null,"ADSL Kota",true);
		this.adslKota=adslKota;
		setLayout(new BorderLayout());
		JPanel settingsPanel=new JPanel(new GridLayout(3,3));
		settingsPanel.add(new JLabel("Kullanıcı adı:"));
		userName=new JHelpText(adslKota.getSetting("username"),"http://code.google.com/p/jkota/",false);
		settingsPanel.add(userName);
		settingsPanel.add(new JLabel("Şifre:"));
		password=new JHelpText(adslKota.getSetting("password"),"http://code.google.com/p/jkota/",true);
		settingsPanel.add(password);
		settingsPanel.add(new JLabel("API Key:"));
		apiKey=new JHelpText(adslKota.getSetting("apikey"),"http://code.google.com/p/jkota/",false);
		settingsPanel.add(apiKey);
		getContentPane().add(settingsPanel,BorderLayout.CENTER);
		JButton ok=new JButton("Tamam");
		ok.addActionListener(this);
		getContentPane().add(ok,BorderLayout.SOUTH);
		setSize(300,200);
		setResizable(false);
		center();
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		setVisible(true);
	}
	
	public void center() {
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		Dimension screenSize = toolkit.getScreenSize();
		int x = (screenSize.width - getWidth()) / 2;
		int y = (screenSize.height - getHeight()) / 2;
		setLocation(x, y);
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
		System.out.println("saving sets");
		adslKota.setSetting("username", userName.getText());
		adslKota.setSetting("password", password.getText());
		adslKota.setSetting("apikey", apiKey.getText());
		try {
			adslKota.storeSettings();
		} catch (IOException e1) {
			SwingUtil.error(this, e1);
		}
		dispose();
	}	
}