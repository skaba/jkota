package com.googlecode.jkota.gtk;

import static com.googlecode.jkota.Util.errorMessage;

import org.gnome.gtk.ErrorMessageDialog;
import org.gnome.gtk.Window;

public abstract class GtkUtil {
	public static void error(Window parent,String message,String title) {
		ErrorMessageDialog dialog= new ErrorMessageDialog(parent,title,message);
		dialog.run();
	}

	public static void error(Window parent,Exception e,String title) {
		error(parent,errorMessage(e),title);
	}
}
