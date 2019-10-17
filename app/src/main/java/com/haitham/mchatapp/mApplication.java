package com.haitham.mchatapp;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

public class mApplication extends Application {

    public final static String CHANNEL_ID = "Channel_ID";
    NotificationChannel notificationChannel;

    @Override
    public void onCreate() {
        super.onCreate();

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {

            notificationChannel = new NotificationChannel(CHANNEL_ID, "mchannel", NotificationManager.IMPORTANCE_DEFAULT);
            notificationChannel.setDescription("mChatChannel");
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(notificationChannel);

        }
    }
}
