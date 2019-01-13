package com.example.admin.calendarioestudiante;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

public class broadcastReceiver extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {

        final int NOTIFY_ID = 0; // ID of notification
        NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);

        Intent intent1 = new Intent(context, adaptadorCursosE.class);
        String nom = intent.getStringExtra("curso");
        intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent1, 0);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    "com.example.admin.calendarioestudiante",
                    "Calendario Matemático CIEMAC",
                    NotificationManager.IMPORTANCE_DEFAULT);

            channel.setDescription("Calendario Matemático CIEMAC");
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                .setChannelId("com.example.admin.calendarioestudiante")
                .setSmallIcon(R.drawable.event)
                .setContentTitle("Calendario Matemático CIEMAC")
                .setContentText("Ejercicio disponible de " + nom)
                .setWhen(System.currentTimeMillis())
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent);


        Notification notification = mBuilder.build();
        notificationManager.notify(NOTIFY_ID, notification);


    }
}
