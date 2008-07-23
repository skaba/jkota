package com.googlecode.jkota;

public final class QuotaInfo {
	private String month="";
	private long downloadedBytes,uploadedBytes;
	
	public QuotaInfo(String month, long downloadedBytes, long uploadedBytes) {
		this.month = month;
		this.downloadedBytes = downloadedBytes;
		this.uploadedBytes = uploadedBytes;
	}
	
	public long getDownloadedBytes() {
		return downloadedBytes;
	}
	public String getMonth() {
		return month;
	}
	public long getUploadedBytes() {
		return uploadedBytes;
	}

	@Override
	public String toString() {
		return "Download:" +downloadedBytes+" Upload: "+uploadedBytes;
	}
}
