package com.video.downloading.app.downloader.online.app.receiver;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.video.downloading.app.downloader.online.app.fragments.HomeFragment;

import java.util.ArrayList;
import java.util.Objects;

import static com.video.downloading.app.downloader.online.app.utils.Constants.TAGI;


public class DownloadReceiver extends BroadcastReceiver {
    DownloadManager dmo;

    @Override
    public void onReceive(Context context,Intent intent) {
        String action = intent.getAction();
        if ("android.intent.action.DOWNLOAD_COMPLETE".equals(action)) {
            Bundle extras = intent.getExtras();
            dmo = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
            DownloadManager.Query q = new DownloadManager.Query();
            q.setFilterById(Objects.requireNonNull(extras).getLong(DownloadManager.EXTRA_DOWNLOAD_ID));
            Cursor c = dmo.query(q);
            ArrayList<String> xcoords = (new HomeFragment()).vIdeoList();
            if (c.moveToFirst()) {
                int status = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS));
                if (status == DownloadManager.STATUS_SUCCESSFUL) {

                    xcoords.remove(c.getString(c.getColumnIndex(DownloadManager.COLUMN_URI)));
                    Toast.makeText(context,"Download Completed",Toast.LENGTH_LONG).show();

                } else {
                    xcoords.remove(c.getString(c.getColumnIndex(DownloadManager.COLUMN_URI)));
                }
            }
            c.close();
        } else if ("android.intent.action.DOWNLOAD_NOTIFICATION_CLICKED".equals(action)) {
            Log.d(TAGI,"Notification clicked");
        } else {
            assert action != null;
            Log.d(TAGI,action);
        }
    }
}
