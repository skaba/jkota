package com.googlecode.jkota;

import java.util.EventListener;

public interface DownloadNotificationListener extends EventListener {
	public void notification(DownloadNotificationEvent e);
}
