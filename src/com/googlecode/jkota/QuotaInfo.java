package com.googlecode.jkota;

public class QuotaInfo {
	private String month="";
	private long downloadedBytes,uploadedBytes;
	
	public QuotaInfo(String month, long downloadedBytes, long uploadedBytes) {
		this.month = month;
		this.downloadedBytes = downloadedBytes;
		this.uploadedBytes = uploadedBytes;
	}
	
	public final long getDownloadedBytes() {
		return downloadedBytes;
	}
	public final String getMonth() {
		return month;
	}
	public final long getUploadedBytes() {
		return uploadedBytes;
	}

	@Override
	public String toString() {
		return "Download:" +downloadedBytes+" Upload: "+uploadedBytes;
	}
}
