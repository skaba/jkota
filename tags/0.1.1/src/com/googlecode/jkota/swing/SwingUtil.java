package com.googlecode.jkota.swing;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.PrintWriter;
import java.io.StringWriter;

import javax.swing.JOptionPane;

public class SwingUtil {
	public static void error(Component parent,Exception e,String title) {
		StringWriter out=new StringWriter();
		e.printStackTrace(new PrintWriter(out));
		out.flush();
		JOptionPane.showMessageDialog(parent, out.getBuffer(), title, JOptionPane.ERROR_MESSAGE);
	}
	
	public static void center(Component component) {
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		Dimension screenSize = toolkit.getScreenSize();
		int x = (screenSize.width - component.getWidth()) / 2;
		int y = (screenSize.height - component.getHeight()) / 2;
		component.setLocation(x, y);
	}
}
