package com.googlecode.jkota.swing;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.Frame;
import java.io.IOException;
import java.net.URISyntaxException;

import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JTabbedPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

public class SwingAbout extends JDialog implements HyperlinkListener {

	public SwingAbout() {
		super((Frame)null,"JKota Hakkında",true);
		JTabbedPane tabs=new JTabbedPane();
		JEditorPane aboutTab=new JEditorPane();
		ClassLoader loader=getClass().getClassLoader();
		try {
			aboutTab.setPage(loader.getResource("about.html"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		aboutTab.addHyperlinkListener(this);
		aboutTab.setEditable(false);
		tabs.add("Hakkında",aboutTab);
		JEditorPane thxTab=new JEditorPane();
		try {
			thxTab.setPage(loader.getResource("thx.html"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		thxTab.addHyperlinkListener(this);
		tabs.add("Teşekkürler",thxTab);
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(tabs,BorderLayout.CENTER);
		setSize(400,200);
		setResizable(false);
		SwingUtil.center(this);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setVisible(true);
	}

	public void hyperlinkUpdate(HyperlinkEvent e) {
		if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
			Desktop desktop = Desktop.getDesktop();
			try {
				desktop.browse(e.getURL().toURI());
			} catch (IOException e1) {
				e1.printStackTrace();
			} catch (URISyntaxException e1) {
				e1.printStackTrace();
			}
		}
	}
}
