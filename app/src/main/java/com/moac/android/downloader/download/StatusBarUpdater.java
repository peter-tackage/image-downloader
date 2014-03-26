package com.moac.android.downloader.download;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

import com.moac.android.downloader.R;

import java.io.File;
import java.util.concurrent.atomic.AtomicInteger;

public class StatusBarUpdater {

    // Used to generate sequential ids for tracking status bar notifications
    private final AtomicInteger mSequenceGenerator = new AtomicInteger();

    private final Context mContext;
    private final NotificationManager mNotificationManager;

    public StatusBarUpdater(Context context, NotificationManager notificationManager) {
        mContext = context;
        mNotificationManager = notificationManager;
    }

    // TODO Would be nice if we could batch these together for multiple files
    public void sendStatusBarNotification(Request request) {

        // Make this behaviour optional
        if (mNotificationManager == null)
            return;

        // If we are cancelling the download then cancel any existing notification
        if (request.getStatus() == Status.CANCELLED) {
            if (request.getNotificationId() != Request.UNSET_NOTIFICATION_ID) {
                mNotificationManager.cancel(request.getNotificationId());
            }
            return;
        }

        // Otherwise conditionally create a new notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext);
        Notification notification;

        // Don't send status bar notifications for each state transition as it is distracting to the user
        switch (request.getStatus()) {
            case PENDING:
                notification = builder
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setContentTitle(mContext.getString(R.string.notification_title_status_pending))
                        .setContentText(request.getDisplayName())
                        .setProgress(0, 0, true)
                        .build();
                break;
            case FAILED:
                notification = builder
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setTicker(mContext.getString(R.string.notification_ticker_status_failed))
                        .setContentTitle(mContext.getString(R.string.notification_title_status_failed))
                        .setContentText(request.getDisplayName())
                        .build();
                break;
            case SUCCESSFUL:
                notification = builder
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setContentTitle(mContext.getString(R.string.notification_title_successful))
                        .setContentText(request.getDisplayName())
                        .build();
                initiateMediaScan(request);
                break;
            default:
                notification = null;
                break;
        }
        if (notification != null) {
            if (request.getNotificationId() == Request.UNSET_NOTIFICATION_ID) {
                request.setNotificationId(mSequenceGenerator.getAndIncrement());
            }
            mNotificationManager.notify(request.getNotificationId(), notification);
        }
    }

    private void initiateMediaScan(Request request) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(request.getDestination());
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        mContext.sendBroadcast(mediaScanIntent);
        // TODO Update the notification with an intent to open the image
    }

}
