package com.googlecode.jkota.gtk;

import org.gnome.gtk.MenuItem;
import java.io.IOException;

import org.gnome.gtk.AboutDialog;
import org.gnome.gtk.Gtk;
import org.gnome.gtk.Menu;
import org.gnome.gtk.StatusIcon;
import org.gnome.gtk.MenuItem.ACTIVATE;
import org.gnome.gtk.StatusIcon.POPUP_MENU;

import com.googlecode.jkota.BaseKota;

public class GtkKota extends BaseKota implements ACTIVATE,POPUP_MENU {
	
	private MenuItem quit,settings,about,logfile,clipboard;
	private StatusIcon icon;
	public GtkKota() {
		String masterKey=null;
		while(masterKey==null) {
			InputDialog dialog=new InputDialog("Ana şifre",null,true);
			dialog.run();
			masterKey=dialog.getText();
			dialog.hide();
		}
		setMasterKey(masterKey);
		try {
			readSettings();
		} catch (IOException e) {
			GtkUtil.error(null, e,"Ayarlar okunurken hata");
		}
		icon= new StatusIcon("favicon.png");
		icon.connect((POPUP_MENU)this);
		startTimer();
	}

	@Override
	public void firstTime() {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateKota(String kota) {
		icon.setTooltip(kota);
	}

	public static void main(String[] args) {
		Gtk.init(args);
		new GtkKota();
		Gtk.main();
	}

	@Override
	public void onActivate(MenuItem source) {
		if(source==quit)
			Gtk.mainQuit();
	}

	@Override
	public void onPopupMenu(StatusIcon source, int button, int activateTime) {
		Menu trayMenu=new Menu();
		quit=new MenuItem("Çıkış",this);
		trayMenu.append(quit);
		trayMenu.showAll();
		trayMenu.popup(source);
	}

}
