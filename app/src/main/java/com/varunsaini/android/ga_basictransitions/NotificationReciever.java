package com.varunsaini.android.ga_basictransitions;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;

import java.util.Calendar;

public class NotificationReciever extends BroadcastReceiver {

    private static final long NOTIFICATION_ALERT_TIME = Utils.NOTIFICATION_TIME;

    public NotificationReciever() {
        super();
    }

    int trimmedRequestId;
    int hour,min;
    Calendar calendar;

    @Override
    public void onReceive(Context context, Intent intent) {

        String action = intent.getAction();
        trimmedRequestId = intent.getIntExtra("trimmedRequestId", -1);

        DatabaseHandler db = new DatabaseHandler(context);
        String time = db.getAlarmTimeFromAlarmRequestId(Integer.parseInt(String.valueOf(trimmedRequestId).substring(3,9)));
        String[] splittedTime = time.split(":");
        hour = Integer.parseInt(splittedTime[0]);
        min = Integer.parseInt(splittedTime[1]);
        calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY,hour);
        calendar.set(Calendar.MINUTE,min);
        AlarmManager alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        NotificationManager mNotificationManager = (NotificationManager) context.getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

        if ("CANCEL_ACTION".equals(action)) {

            Toast.makeText(context, "Turn Off Clicked", Toast.LENGTH_SHORT).show();
            Intent intent2 = new Intent(context, AlarmReciever.class);
            intent2.putExtra("request_code",trimmedRequestId);
            PendingIntent alarmIntent = PendingIntent.getBroadcast(context, trimmedRequestId, intent2, PendingIntent.FLAG_UPDATE_CURRENT);
            alarmMgr.cancel(alarmIntent);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmMgr.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis()+ (7*24*60*60*1000 ), alarmIntent);
            }else{
                alarmMgr.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis()+ (7*24*60*60*1000 ), alarmIntent);
            }

            Intent notifyIntent = new Intent(context, NotificationReciever.class);
            notifyIntent.putExtra("trimmedRequestId",trimmedRequestId);
            PendingIntent pendingIntent = PendingIntent.getBroadcast
                    (context, trimmedRequestId, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            mNotificationManager.cancel(trimmedRequestId);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmMgr.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis()+ (7*24*60*60*1000 ) - NOTIFICATION_ALERT_TIME, pendingIntent);
            }else{
                alarmMgr.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis()+ (7*24*60*60*1000 ) - NOTIFICATION_ALERT_TIME, pendingIntent);
            }
            Log.d("sas", "onReceive: " + trimmedRequestId);

        }else if("SWIPE_NOTIFICATION_ACTION".equals(action)){
            Toast.makeText(context, "Swiped", Toast.LENGTH_SHORT).show();

            Intent notifyIntent = new Intent(context, NotificationReciever.class);
            notifyIntent.putExtra("trimmedRequestId",trimmedRequestId);
            PendingIntent pendingIntent = PendingIntent.getBroadcast
                    (context, trimmedRequestId, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            mNotificationManager.cancel(trimmedRequestId);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmMgr.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis()+ (7*24*60*60*1000 ) - NOTIFICATION_ALERT_TIME, pendingIntent);
            }else{
                alarmMgr.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis()+ (7*24*60*60*1000 ) - NOTIFICATION_ALERT_TIME, pendingIntent);
            }
            Log.d("sas", "onReceive: " + trimmedRequestId);

        } else {
            Intent intent1 = new Intent(context, NotificationIntentService.class);
            intent1.putExtra("trimmedRequestId", trimmedRequestId);
            context.startService(intent1);
        }
    }

}
