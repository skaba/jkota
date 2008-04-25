package com.googlecode.jkota.gtk;

import java.io.IOException;

import org.gnome.gtk.AboutDialog;
import org.gnome.gtk.Gtk;
import org.gnome.gtk.Menu;
import org.gnome.gtk.MenuItem;
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
		if(source==logfile)
			viewLogFile();
		if(source==about) {
			AboutDialog about=new AboutDialog();
			about.setComments("İnternet kota kontrol Programı");
			about.setCopyright("© 2007 Serkan Kaba");
			about.setProgramName("JKota");
			about.setAuthors(new String[] {"Serkan Kaba <serkan_kaba@yahoo.com>"});
			about.showAll();
			about.run();
		}
	}

	@Override
	public void onPopupMenu(StatusIcon source, int button, int activateTime) {
		Menu trayMenu=new Menu();
		logfile=new MenuItem("Günlük");
		trayMenu.append(logfile);
		logfile.connect(this);
		about=new MenuItem("Hakkında");
		trayMenu.append(about);
		about.connect(this);
		quit=new MenuItem("Çıkış",this);
		trayMenu.append(quit);
		quit.connect(this);
		trayMenu.showAll();
		trayMenu.popup(source);
	}

}
