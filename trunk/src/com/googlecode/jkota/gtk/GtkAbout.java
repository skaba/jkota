package com.googlecode.jkota.gtk;

import org.gnome.gtk.AboutDialog;

public class GtkAbout extends AboutDialog {
	public GtkAbout() {
		setComments("İnternet kota kontrol Programı");
		setCopyright("© 2007 Serkan Kaba");
		setProgramName("JKota");
		setAuthors(new String[] {"Serkan Kaba <serkan_kaba@yahoo.com>"});
	}
}
