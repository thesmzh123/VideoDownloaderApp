package com.video.downloading.app.downloader.online.app.models;

public class DailymotionLink {
    private static DailymotionLink instance = new DailymotionLink();
    private String downloadUrl;

    public static DailymotionLink getInstance() {
        return instance;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }
}
