package com.googlecode.jkota.gtk;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.gnome.gtk.ErrorMessageDialog;
import org.gnome.gtk.Window;

public class GtkUtil {
	public static void error(Window parent,Exception e,String title) {
		StringWriter out=new StringWriter();
		e.printStackTrace(new PrintWriter(out));
		out.flush();
		ErrorMessageDialog dialog = new ErrorMessageDialog(parent,title,out.getBuffer().toString());
		dialog.run();
		dialog.hide();
	}
}
