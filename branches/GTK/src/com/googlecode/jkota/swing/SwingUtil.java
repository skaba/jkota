package com.googlecode.jkota.swing;
import java.awt.Component;
import java.io.PrintWriter;
import java.io.StringWriter;

import javax.swing.JOptionPane;

public class SwingUtil {
	public static void error(Component parent,String message,String title) {
		JOptionPane.showMessageDialog(parent, message, title, JOptionPane.ERROR_MESSAGE);
	}

	public static void error(Component parent,Exception e,String title) {
		StringWriter out=new StringWriter();
		e.printStackTrace(new PrintWriter(out));
		out.flush();
		error(parent,out.getBuffer().toString(),title);
	}
}
