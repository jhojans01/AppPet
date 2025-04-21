package com.example.apppet.notifications;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.example.apppet.R;
import com.example.apppet.VisitsActivity;

public class VisitReminderReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String motivo = intent.getStringExtra("motivo");
        String fecha = intent.getStringExtra("fecha");

        Intent notiIntent = new Intent(context, VisitsActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context, 0, notiIntent, PendingIntent.FLAG_IMMUTABLE
        );

        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);

        String channelId = "visit_channel";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    channelId, "Recordatorios de Visitas", NotificationManager.IMPORTANCE_HIGH
            );
            notificationManager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.drawable.ic_add) // cambia por tu ícono
                .setContentTitle("¡Recordatorio de Visita!")
                .setContentText("Tienes una visita programada: " + motivo + " el " + fecha)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        notificationManager.notify((int) System.currentTimeMillis(), builder.build());
    }
}
