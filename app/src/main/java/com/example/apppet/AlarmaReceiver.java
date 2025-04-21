package com.example.apppet;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.app.NotificationManager;
import android.app.NotificationChannel;
import androidx.core.app.NotificationCompat;

public class AlarmaReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String medicamento = intent.getStringExtra("medicamento");
        String enfermedad = intent.getStringExtra("enfermedad");

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "medicamento_channel")
                .setSmallIcon(R.drawable.desc_vaccine_icon) // ícono en drawable
                .setContentTitle("Hora de Medicamento")
                .setContentText("Toma de " + medicamento + " para " + enfermedad)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify((int) System.currentTimeMillis(), builder.build());
    }
}
