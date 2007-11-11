package com.google.code.jkota.swing;
import java.awt.Component;
import java.io.PrintWriter;
import java.io.StringWriter;

import javax.swing.JOptionPane;

public class SwingUtil {
	public static void error(Component parent,Exception e) {
		StringWriter out=new StringWriter();
		e.printStackTrace(new PrintWriter(out));
		JOptionPane.showMessageDialog(parent, out.getBuffer(), "Hata", JOptionPane.ERROR_MESSAGE);
	}
}
