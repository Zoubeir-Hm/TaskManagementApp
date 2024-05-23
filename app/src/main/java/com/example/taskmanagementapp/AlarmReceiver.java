// AlarmReceiver.java
package com.example.taskmanagementapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Vibrator;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String eventName = intent.getStringExtra("eventName");

        // Vibration
        Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        if (vibrator != null) {
            vibrator.vibrate(1000); // Vibrate for 1 second
        }

        // Play default alarm sound
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        RingtoneManager.getRingtone(context, alarmSound).play();

        // Show notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "event_channel")
                .setSmallIcon(R.drawable.ic_event) // Assurez-vous que cette icône existe
                .setContentTitle("Event Reminder")
                .setContentText("Event: " + eventName)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setSound(alarmSound);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling ActivityCompat#requestPermissions
            // to request the missing permissions
            return;
        }
        notificationManager.notify(0, builder.build());
    }
}
