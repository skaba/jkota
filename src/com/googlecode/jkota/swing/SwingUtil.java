package com.googlecode.jkota.swing;
import java.awt.Component;
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
}
