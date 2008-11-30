package com.googlecode.jkota;

import java.util.EventObject;

public class DownloadNotificationEvent extends EventObject {
	private String message="";
	private DownloadNotificationType type;
	public DownloadNotificationEvent(Object source,String message,DownloadNotificationType type) {
		super(source);
		this.message=message;
		this.type=type;
	}
	public String getMessage() {
		return message;
	}
	public DownloadNotificationType getType() {
		return type;
	}
}
