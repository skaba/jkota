package com.googlecode.jkota.swing;
import static com.googlecode.jkota.Util.errorMessage;

import java.awt.Component;

import javax.swing.JOptionPane;

public abstract class SwingUtil {
	public static void error(Component parent,String message,String title) {
		JOptionPane.showMessageDialog(parent, message, title, JOptionPane.ERROR_MESSAGE);
	}

	public static void error(Component parent,Exception e,String title) {
		error(parent,errorMessage(e),title);
	}
}
