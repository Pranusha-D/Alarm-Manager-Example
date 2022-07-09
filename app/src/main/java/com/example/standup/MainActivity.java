package com.example.standup;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    private String toastMessage;
    private NotificationManager mNotificationManager;
    private static final int NOTIFICATION_ID = 0;
    private static final String PRIMARY_CHANNEL_ID = "primary_notification_channel";
    private AlarmManager alarmManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ToggleButton alarmToggle = findViewById(R.id.alarmToggle);
        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        // Set up the Notification Broadcast Intent.
        Intent notifyIntent = new Intent(this, AlarmReceiver.class);

        boolean alarmUp = (PendingIntent.getBroadcast(this, NOTIFICATION_ID,
                notifyIntent, PendingIntent.FLAG_NO_CREATE) != null);
        alarmToggle.setChecked(alarmUp);

        final PendingIntent notifyPendingIntent = PendingIntent.getBroadcast
                (this, NOTIFICATION_ID, notifyIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton,
                                         boolean isChecked) {
                if (isChecked) {
                    long repeatInterval = 1 * 60 * 1000;

                    long triggerTime = SystemClock.elapsedRealtime()
                            + repeatInterval;

                    // If the Toggle is turned on, set the repeating alarm with
                    // a 1 minute interval.
                    if (alarmManager != null) {
                        alarmManager.setInexactRepeating
                                (AlarmManager.ELAPSED_REALTIME_WAKEUP,
                                        triggerTime, repeatInterval,
                                        notifyPendingIntent);
                    }
                    // Set the toast message for the "on" case.
                    //deliverNotification(MainActivity.this);
                    //Set the toast message for the "on" case
                    toastMessage = "Stand Up Alarm On!";
                } else {
                    //Cancel notification if the alarm is turned off
                    mNotificationManager.cancelAll();

                    if (alarmManager != null) {
                        alarmManager.cancel(notifyPendingIntent);
                    }
                    // Set the toast message for the "off" case.

                    //Set the toast message for the "off" case
                    toastMessage = "Stand Up Alarm Off!";
                }
                //Show a toast to say the alarm is turned on or off.
                Toast.makeText(MainActivity.this, toastMessage, Toast.LENGTH_SHORT).show();

            }
        });
        createNotificationChannel();
    }

    /**
     * Creates a Notification channel, for OREO and higher.
     */
    public void createNotificationChannel() {

        // Create a notification manager object.
        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        // Notification channels are only available in OREO and higher.
        // So, add a check on SDK version.
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {

            // Create the NotificationChannel with all the parameters.
            NotificationChannel notificationChannel = new NotificationChannel(PRIMARY_CHANNEL_ID, "Stand up notification",
                    NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.enableVibration(true);
            notificationChannel.setDescription
                    ("Notifies every 1 minute to stand up and walk");
            mNotificationManager.createNotificationChannel(notificationChannel);
        }
    }

    public void getAlarmTime(View view) {
        if (alarmManager.getNextAlarmClock() != null) {
            long nextAlarmTime = alarmManager.getNextAlarmClock().getTriggerTime();
            String nextAlarm = new SimpleDateFormat("EEE dd MMM HH:mm").format(new Date(nextAlarmTime));
            Toast.makeText(MainActivity.this, nextAlarm, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(MainActivity.this, "Set alarm first", Toast.LENGTH_SHORT).show();
        }
    }
}