package com.example.grocerytracker;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class Notification_receiver_exp extends BroadcastReceiver {
    public void onReceive(Context context, Intent intent) {

        //Builds the notification while in sync with the notification channel via the similar channel id
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context,"expiry")
                .setSmallIcon(android.R.drawable.arrow_up_float)
                .setContentTitle("WARNING")
                .setContentText("One of your Grocery has expired ")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        //Redirects to the expired grocery list of the user after the expiry notification message is clicked
        Intent notionce = new Intent(context,listGrocery.class);
        notionce.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        //unique id to link with the channel to determine which notification this is suppose to be
        notificationManager.notify(400,builder.build());
    }
}
