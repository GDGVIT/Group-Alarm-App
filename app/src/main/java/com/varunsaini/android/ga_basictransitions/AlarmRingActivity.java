package com.varunsaini.android.ga_basictransitions;

import android.annotation.SuppressLint;
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
import android.text.method.Touch;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;

public class AlarmRingActivity extends AppCompatActivity {

    private AlarmManager alarmMgr;
    private PendingIntent alarmIntent;
    Calendar calendar;
    GestureDetector mGestureDetector;
    TextView actionButton,snooozeText,cancelText;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_ring);

        actionButton = findViewById(R.id.action_button);
        snooozeText = findViewById(R.id.snoozeText);
        cancelText = findViewById(R.id.cancelText);

        mGestureDetector = new GestureDetector(this,new MyGestureListener());

        actionButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
//                if(event.getAction()==MotionEvent.ACTION_DOWN){
//                    View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(actionButton);
//                    // Start the drag of the shadow
//                    actionButton.startDrag(null, shadowBuilder, actionButton, 0);
//                    // Hide the actual view as shadow is being dragged
//                    actionButton.setVisibility(View.INVISIBLE);
//
//                }
                return mGestureDetector.onTouchEvent(event);
            }
        });
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void snoozeAlarm(){
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
    public void dismissAlarm() {
        alarmMgr = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(AlarmRingActivity.this, AlarmReciever.class);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(AlarmRingActivity.this, AlarmReciever.request_id, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        Log.d("dismissAlarm", "dismissAlarm: "+AlarmReciever.request_id);
        AlarmReciever ar = new AlarmReciever();
        if (AlarmReciever.r.isPlaying()) {
            AlarmReciever.r.stop();
        }
        alarmMgr.cancel(alarmIntent);
        AlarmManager alarmMgr1 = (AlarmManager)AlarmRingActivity.this.getSystemService(Context.ALARM_SERVICE);

        PendingIntent alarmIntent1 = PendingIntent.getBroadcast(AlarmRingActivity.this, AlarmReciever.request_id, intent, 0);

        alarmMgr1.setExact(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime()+ (7*24*60*60*1000), alarmIntent1);
        finishAndRemoveTask();
    }

    class MyGestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onDown(MotionEvent event) {
            Log.d("TAG","onDown: ");
//            Toast.makeText(AlarmRingActivity.this, "onDown", Toast.LENGTH_SHORT).show();
            // don't return false here or else none of the other
            // gestures will work
            return true;
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            Log.i("TAG", "onSingleTapConfirmed: ");
//            Toast.makeText(AlarmRingActivity.this, "onSingleTap", Toast.LENGTH_SHORT).show();
            return true;
        }

        @Override
        public void onLongPress(MotionEvent e) {
            Log.i("TAG", "onLongPress: ");
//            Toast.makeText(AlarmRingActivity.this, "onLongPress", Toast.LENGTH_SHORT).show();
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            Log.i("TAG", "onDoubleTap: ");
            return true;
        }

        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2,
                                float distanceX, float distanceY) {
//            Log.i("TAG", "onScroll: ");
//            float moveMent = e1.getY()-e2.getY();
            float distSnooze = actionButton.getY() - snooozeText.getY();
            float distCancel = actionButton.getY() - cancelText.getY();
//            if(moveMent>0) {
////                snoozeAlarm();
//                Toast.makeText(AlarmRingActivity.this, "Alarm Snoozed", Toast.LENGTH_SHORT).show();
//            }else{
////                dismissAlarm();
//                Toast.makeText(AlarmRingActivity.this, "Alarm Dismissed", Toast.LENGTH_SHORT).show();
//            }


//            Toast.makeText(AlarmRingActivity.this, "onScroll", Toast.LENGTH_SHORT).show();
            return true;
        }

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public boolean onFling(MotionEvent event1, MotionEvent event2,
                               float velocityX, float velocityY) {
            Log.d("TAG", "onFling: ");
            float distSnooze = actionButton.getY() - snooozeText.getY();
            float distCancel = actionButton.getY() - cancelText.getY();
            float movementY = event1.getY()-event2.getY();
            float movementX = event1.getX() - event2.getX();
            if(Math.abs(movementX)<Math.abs(movementY)){
                if(Math.abs(movementY)>200){
                    if(movementY>0) {
                        snoozeAlarm();
                        Toast.makeText(AlarmRingActivity.this, "Alarm Snoozed", Toast.LENGTH_SHORT).show();
                    }else{
                        dismissAlarm();
                        Toast.makeText(AlarmRingActivity.this, "Alarm Dismissed", Toast.LENGTH_SHORT).show();
                    }
                    }
            }
//            Toast.makeText(AlarmRingActivity.this, "onFling", Toast.LENGTH_SHORT).show();

            return true;
        }
    }
}
