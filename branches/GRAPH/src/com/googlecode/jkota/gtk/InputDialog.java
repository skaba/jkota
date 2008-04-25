package com.googlecode.jkota.gtk;

import org.gnome.gtk.Dialog;
import org.gnome.gtk.Entry;
import org.gnome.gtk.ResponseType;
import org.gnome.gtk.Stock;
import org.gnome.gtk.Window;

public class InputDialog extends Dialog {
	private Entry text;
	public InputDialog(java.lang.String title, Window parent, boolean modal) {
		super(title, parent, modal);
		text=new Entry();
		text.setVisibleChars(false);
		add(text);
		addButton(Stock.OK, ResponseType.OK);
		showAll();
	}
	public String getText() {
		return text.getText();
	}
}
