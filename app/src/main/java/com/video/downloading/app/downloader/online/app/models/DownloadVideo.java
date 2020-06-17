
package com.video.downloading.app.downloader.online.app.models;

import java.io.Serializable;

public class DownloadVideo implements Serializable {
    public String size, type, link, name, page, website;
    public boolean chunked;
}
