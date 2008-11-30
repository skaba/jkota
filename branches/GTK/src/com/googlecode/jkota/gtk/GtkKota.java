package com.googlecode.jkota.gtk;

import java.net.URI;
import java.net.URISyntaxException;

import org.gnome.gtk.AboutDialog;
import org.gnome.gtk.Clipboard;
import org.gnome.gtk.Gtk;
import org.gnome.gtk.Menu;
import org.gnome.gtk.MenuItem;
import org.gnome.gtk.StatusIcon;

import com.googlecode.jkota.BaseKota;
import com.googlecode.jkota.LogManager;

public class GtkKota extends BaseKota {

	private MenuItem quit,settings,about,logfile,clipboard,statistics;
	private StatusIcon icon;
	private String quotaText;

	@Override
	public void firstTime() {
		// TODO Auto-generated method stub

	}

	@Override
	public void initUI() {
		icon= new StatusIcon("favicon.png");
		icon.connect(
				new org.gnome.gtk.StatusIcon.PopupMenu() {
					@Override
					public void onPopupMenu(StatusIcon source, int arg1, int arg2) {
						TrayMenuHandler handler=new TrayMenuHandler();
						Menu trayMenu=new Menu();
						logfile=creteMenuItem("Günlük", trayMenu, handler);
						about=creteMenuItem("Hakkında", trayMenu, handler);
						quit=creteMenuItem("Çıkış", trayMenu, handler);
						clipboard=creteMenuItem("Panoya kopyala", trayMenu, handler);
						trayMenu.showAll();
						trayMenu.popup(source);
					}

					private MenuItem creteMenuItem(String label,Menu trayMenu, TrayMenuHandler handler) {
						MenuItem item=new MenuItem(label);
						trayMenu.add(item);
						item.connect(handler);
						return item;
					}
				}
		);
	}

	@Override
	public String promptForMasterKey() {
		String masterKey=null;
		while(masterKey==null) {
			InputDialog dialog=new InputDialog("Ana şifre",null,true);
			dialog.run();
			masterKey=dialog.getText();
			dialog.hide();
		}
		return masterKey;
	}

	@Override
	public void showError(Exception e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateQuota(String quota) {
		icon.setTooltip(quota);
		quotaText=quota;
	}

	public static void main(String[] args) {
		Gtk.init(args);
		new GtkKota();
		Gtk.main();
	}

	public class TrayMenuHandler implements org.gnome.gtk.MenuItem.Activate {
		@Override
		public void onActivate(MenuItem source) {
			if(source==quit) {
				preQuit();
				Gtk.mainQuit();
			}
			if(source==logfile)
				try {
					Gtk.showURI(new URI("file://"+LogManager.LOG_FILE));
				} catch (URISyntaxException e) { }
				if(source==about) {
					AboutDialog about=new AboutDialog();
					about.setComments("İnternet kota kontrol Programı");
					about.setCopyright("© 2007 Serkan Kaba");
					about.setProgramName("JKota");
					about.setAuthors(new String[] {"Serkan Kaba <serkan_kaba@yahoo.com>"});
					about.showAll();
					about.run();
				}
				if(source==clipboard) {
					Clipboard clipboard=Clipboard.getDefault();
					clipboard.setText(quotaText);
				}
		}
	}

}
