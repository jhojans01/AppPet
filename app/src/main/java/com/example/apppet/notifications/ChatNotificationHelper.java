package com.example.apppet.notifications;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import androidx.core.app.NotificationCompat;

import com.example.apppet.R;
import com.example.apppet.ChatActivity; // Ajusta si tienes otro nombre

public class ChatNotificationHelper {

    public static void showChatNotification(Context context, String senderName, String message, int chatId) {
        Intent intent = new Intent(context, ChatActivity.class);
        intent.putExtra("chat_id", chatId); // Puedes pasar el ID del chat para abrirlo
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, chatId, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "chat_messages")
                .setSmallIcon(R.drawable.chat) // Usa tu propio ícono de notificación
                .setContentTitle("Nuevo mensaje de " + senderName)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(chatId, builder.build());
    }
}
