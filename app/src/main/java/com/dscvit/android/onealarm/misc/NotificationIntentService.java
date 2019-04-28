package com.dscvit.android.onealarm.misc;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.dscvit.android.onealarm.R;
import com.dscvit.android.onealarm.activity.AllActivity;

import static android.media.RingtoneManager.getDefaultUri;

@RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
public class NotificationIntentService extends IntentService {

    public NotificationIntentService(String name) {
        super(name);
    }

    public NotificationIntentService() {
        super("dsd");
    }

    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Log.d("sas", "onReceive: "+"reached notificationintentservice");
        Uri alarmSound = getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        int trimmedRequestId = intent.getIntExtra("trimmedRequestId",-1);

        //////////////
        DatabaseHandler db = new DatabaseHandler(getApplicationContext());
        NotificationManager mNotificationManager;
        mNotificationManager =
                (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(getApplicationContext(), "notify_001");

        Intent notifyIntent = new Intent(this, AllActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, trimmedRequestId, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent yesReceive = new Intent(this, NotificationReciever.class);
        yesReceive.putExtra("trimmedRequestId",trimmedRequestId);
        yesReceive.setAction("CANCEL_ACTION");
        PendingIntent pendingIntentYes = PendingIntent.getBroadcast(this, trimmedRequestId, yesReceive, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent swipeIntent = new Intent(this,NotificationReciever.class);
        swipeIntent.putExtra("trimmedRequestId",trimmedRequestId);
        swipeIntent.setAction("SWIPE_NOTIFICATION_ACTION");
        PendingIntent pendingIntentSwipe = PendingIntent.getBroadcast(this, trimmedRequestId, swipeIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmMgr1 = (AlarmManager)this.getSystemService(Context.ALARM_SERVICE);
        PendingIntent alarmIntent1 = PendingIntent.getBroadcast(this, trimmedRequestId, intent, 0);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmMgr1.setExactAndAllowWhileIdle(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime()+ (7*24*60*60*1000), alarmIntent1);
        }else{
            alarmMgr1.setExact(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime()+ (7*24*60*60*1000), alarmIntent1);
        }

        Uri path = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.BigTextStyle bigText = new NotificationCompat.BigTextStyle();
        bigText.bigText("Upcoming Alarm at " + db.getAlarmTimeFromAlarmRequestId(Integer.parseInt(String.valueOf(trimmedRequestId).substring(3))));
        bigText.setBigContentTitle("Upcoming Alarm");
        mBuilder.setSmallIcon(R.drawable.baseline_alarm_on_white_18dp);
        mBuilder.setContentTitle("Upcoming Alarm");
        mBuilder.setContentText("Upcoming Alarm at " + db.getAlarmTimeFromAlarmRequestId(Integer.parseInt(String.valueOf(trimmedRequestId).substring(3))));
        mBuilder.setContentIntent(pendingIntent);
        mBuilder.setPriority(Notification.PRIORITY_MAX);
        mBuilder.setStyle(bigText);
        mBuilder.setSound(path);
        mBuilder.addAction(R.drawable.cross,"Turn Off",pendingIntentYes);
        mBuilder.setDeleteIntent(pendingIntentSwipe);




        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelId = "YOUR_CHANNEL_ID";
            NotificationChannel channel = new NotificationChannel(channelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);
            mNotificationManager.createNotificationChannel(channel);
            mBuilder.setChannelId(channelId);
        }

        mNotificationManager.notify(trimmedRequestId, mBuilder.build());
    }
}
