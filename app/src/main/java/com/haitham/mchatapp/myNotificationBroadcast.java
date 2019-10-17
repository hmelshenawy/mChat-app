package com.haitham.mchatapp;

import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import static com.haitham.mchatapp.mApplication.CHANNEL_ID;

public class myNotificationBroadcast extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        String msg_TXT = intent.getStringExtra("MSG_TXT");
        String msg_Sender = intent.getStringExtra("MSG_SENDER");

        NotificationManagerCompat notiCompat = NotificationManagerCompat.from(context);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID);
        builder.setContentTitle(msg_Sender);
        builder.setContentText(msg_TXT);
        builder.setPriority(NotificationCompat.PRIORITY_HIGH);
        builder.setCategory(NotificationCompat.CATEGORY_ALARM);
        builder.setSmallIcon(R.drawable.sendsmall);
        builder.build();

        Notification notification = builder.build();

        notiCompat.notify(1, notification);

        Toast.makeText(context, "new message", Toast.LENGTH_SHORT).show();

    }
}
