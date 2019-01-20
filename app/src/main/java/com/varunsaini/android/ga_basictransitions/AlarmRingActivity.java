package com.varunsaini.android.ga_basictransitions;

import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import java.util.Calendar;

public class AlarmRingActivity extends AppCompatActivity {

    private AlarmManager alarmMgr;
    private PendingIntent alarmIntent;
    Calendar calendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_ring);

    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void snoozeAlarm(View v){
        alarmMgr = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(AlarmRingActivity.this, AlarmReciever.class);
        intent.putExtra("request_code",AlarmReciever.request_id);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(AlarmRingActivity.this, AlarmReciever.request_id, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmReciever ar = new AlarmReciever();
        if (AlarmReciever.r.isPlaying()) {
            AlarmReciever.r.stop();
        }
        alarmMgr.cancel(alarmIntent);
        finishAndRemoveTask();

        Log.d("HH", "snoozeAlarm: after cancel alarm");
        AlarmManager alarmMgr1 = (AlarmManager)AlarmRingActivity.this.getSystemService(Context.ALARM_SERVICE);

        PendingIntent alarmIntent1 = PendingIntent.getBroadcast(AlarmRingActivity.this, 0, intent, 0);

        alarmMgr1.setExact(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime()+ (1000 * 5), alarmIntent1);

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void dismissAlarm(View view) {
        alarmMgr = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(AlarmRingActivity.this, AlarmReciever.class);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(AlarmRingActivity.this, AlarmReciever.request_id, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        Log.d("dismissAlarm", "dismissAlarm: "+AlarmReciever.request_id);
        AlarmReciever ar = new AlarmReciever();
        if (AlarmReciever.r.isPlaying()) {
            AlarmReciever.r.stop();
        }
        alarmMgr.cancel(alarmIntent);
        finishAndRemoveTask();
    }
}
