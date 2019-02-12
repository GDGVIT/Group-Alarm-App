package com.varunsaini.android.ga_basictransitions;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.os.Vibrator;
import android.support.animation.DynamicAnimation;
import android.support.animation.FlingAnimation;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.method.Touch;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;

public class AlarmRingActivity extends AppCompatActivity {

    private static final int NUM_REPEATS = 1000;
    private AlarmManager alarmMgr;
    private PendingIntent alarmIntent;
    Calendar calendar;
    ImageView oneUpArrow,twoUpArrow,threeUpArrow,fourUpArrow,oneDownArrow,twoDownArrow,threeDownArrow,fourDownArrow;
    int request_id;
    int backgroundColor;
    Vibrator v;
    GestureDetector mGestureDetector;
    TextView actionButton,snooozeText,cancelText;
    boolean isTurnedOff = false;
    RectF actionRect,snoozeRect,cancelRect;
    float distanceSnoozeAction,distanceCancelAction;
    RelativeLayout relativeLayout;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_ring);
        this.getWindow().setType(WindowManager.LayoutParams.TYPE_KEYGUARD_DIALOG);

        request_id = getIntent().getIntExtra("recieved_request_code",-1);
        backgroundColor = getIntent().getIntExtra("group_color_for_background",-1);

        oneUpArrow = findViewById(R.id.oneUpArrow);
        twoUpArrow = findViewById(R.id.twoUpArrow);
        threeUpArrow = findViewById(R.id.threeUpArrow);
        fourUpArrow = findViewById(R.id.fourUpArrow);
        oneDownArrow = findViewById(R.id.oneDownArrow);
        twoDownArrow = findViewById(R.id.twoDownArrow);
        threeDownArrow = findViewById(R.id.threeDownArrow);
        fourDownArrow = findViewById(R.id.fourDownArrow);


        Typeface tf = Typeface.createFromAsset(getAssets(), "fonts/Karla-Bold.ttf");

        actionButton = findViewById(R.id.action_button);
        snooozeText = findViewById(R.id.snoozeText);
        cancelText = findViewById(R.id.cancelText);
        relativeLayout = findViewById(R.id.relativeLayout);


        snooozeText.setTypeface(tf);
        cancelText.setTypeface(tf);

        actionRect = calculeRectOnScreen(actionButton);
        snoozeRect = calculeRectOnScreen(snooozeText);
        cancelRect = calculeRectOnScreen(cancelText);

        distanceSnoozeAction = Math.abs(actionRect.bottom - snoozeRect.top);
        distanceCancelAction = Math.abs(cancelRect.bottom - actionRect.top);

        final AlphaAnimation anim1 = new AlphaAnimation(1.0f, 0.4f);
        anim1.setDuration(500);
        anim1.setRepeatCount(NUM_REPEATS);
        anim1.setRepeatMode(Animation.REVERSE);
//        actionButton.startAnimation(anim1);

        final AlphaAnimation anim = new AlphaAnimation(1.0f,0.4f);
        anim.setDuration(500);


        Handler handler = new Handler();
        Runnable r  = new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void run() {
                if (!isTurnedOff){
                    dismissAlarm();
                }
            }
        };
        handler.postDelayed(r,57000);

        final Handler handler1 = new Handler();
        Runnable r1 = new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void run() {
                if (!isTurnedOff){
                    fourUpArrow.startAnimation(anim);
                    oneDownArrow.startAnimation(anim);
                    handler1.postDelayed(this,4000);
                }
            }
        };
        handler1.postDelayed(r1,800);

        final Handler handler2 = new Handler();
        Runnable r2  = new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void run() {
                if (!isTurnedOff){
                    threeUpArrow.startAnimation(anim);
                    twoDownArrow.startAnimation(anim);
                    handler2.postDelayed(this,4000);
                }
            }
        };
        handler2.postDelayed(r2,1600);

        final Handler handler3 = new Handler();
        Runnable r3  = new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void run() {
                if (!isTurnedOff){
                    twoUpArrow.startAnimation(anim);
                    threeDownArrow.startAnimation(anim);
                    handler3.postDelayed(this,4000);
                }
            }
        };
        handler3.postDelayed(r3,2400);

        final Handler handler4 = new Handler();
        Runnable r4  = new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void run() {
                if (!isTurnedOff){
                    oneUpArrow.startAnimation(anim);
                    fourDownArrow.startAnimation(anim);
                    handler4.postDelayed(this,4000);
                }
            }
        };
        handler4.postDelayed(r4,3200);



        DatabaseHandler db = new DatabaseHandler(this);
        SQLiteDatabase sqLiteDatabase = this.openOrCreateDatabase("Alarmm",MODE_PRIVATE,null);



        if(backgroundColor==1){
            relativeLayout.setBackgroundResource(R.drawable.list_grad_purple);
            commonPropertyforAlarmRing();
        }else if(backgroundColor==2){
            relativeLayout.setBackgroundResource(R.drawable.list_grad_green);
            commonPropertyforAlarmRing();
        }else if(backgroundColor==3){
            relativeLayout.setBackgroundResource(R.drawable.list_grad_pink);
            commonPropertyforAlarmRing();
        }else if(backgroundColor==4){
        }else if(backgroundColor==5){
        }else if(backgroundColor==6){
        }else if(backgroundColor==7){
        }else{
            relativeLayout.setBackgroundResource(R.color.white);
            oneUpArrow.setImageDrawable(getResources().getDrawable(R.drawable.ic_arrow_drop_up_blue_24dp));
            twoUpArrow.setImageDrawable(getResources().getDrawable(R.drawable.ic_arrow_drop_up_blue_24dp));
            threeUpArrow.setImageDrawable(getResources().getDrawable(R.drawable.ic_arrow_drop_up_blue_24dp));
            fourUpArrow.setImageDrawable(getResources().getDrawable(R.drawable.ic_arrow_drop_up_blue_24dp));
            oneDownArrow.setImageDrawable(getResources().getDrawable(R.drawable.ic_arrow_drop_down_blue_24dp));
            twoDownArrow.setImageDrawable(getResources().getDrawable(R.drawable.ic_arrow_drop_down_blue_24dp));
            threeDownArrow.setImageDrawable(getResources().getDrawable(R.drawable.ic_arrow_drop_down_blue_24dp));
            fourDownArrow.setImageDrawable(getResources().getDrawable(R.drawable.ic_arrow_drop_down_blue_24dp));
            actionButton.setBackgroundResource(R.drawable.circle_blue_action_textview);
            snooozeText.setTextColor(getResources().getColor(R.color.fabBlue));
            cancelText.setTextColor(getResources().getColor(R.color.fabBlue));
        }

        v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        String[] ringtoneVibrateString = db.getRingtoneUriVibrate(request_id);
        if(ringtoneVibrateString[1]!=null){
            if(ringtoneVibrateString[1].equals("1")){
                long[] pattern = {0, 100, 1000};
                v.vibrate(pattern, 0);}
        }

        mGestureDetector = new GestureDetector(this,new MyGestureListener());

        actionButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return mGestureDetector.onTouchEvent(event);
            }
        });
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void snoozeAlarm(){
        alarmMgr = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(AlarmRingActivity.this, AlarmReciever.class);
        intent.putExtra("request_code",request_id);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(AlarmRingActivity.this, request_id, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmReciever ar = new AlarmReciever();
        if (AlarmReciever.r.isPlaying()) {
            AlarmReciever.r.stop();

        }
        v.cancel();
        alarmMgr.cancel(alarmIntent);
        finishAndRemoveTask();
//        overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
        Log.d("HH", "snoozeAlarm: after cancel alarm");
        AlarmManager alarmMgr1 = (AlarmManager)AlarmRingActivity.this.getSystemService(Context.ALARM_SERVICE);

        PendingIntent alarmIntent1 = PendingIntent.getBroadcast(AlarmRingActivity.this, 0, intent, 0);

        alarmMgr1.setExact(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime()+ (1000 * 5), alarmIntent1);
        WakeLocker.release();
        isTurnedOff = true;
        AlarmReciever.isplaying = false;


    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void dismissAlarm() {
        alarmMgr = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(AlarmRingActivity.this, AlarmReciever.class);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(AlarmRingActivity.this, request_id, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        Log.d("dismissAlarm", "dismissAlarm: "+request_id);
        AlarmReciever ar = new AlarmReciever();
        if (AlarmReciever.r.isPlaying()) {
            AlarmReciever.r.stop();
        }
        v.cancel();
        alarmMgr.cancel(alarmIntent);
        AlarmManager alarmMgr1 = (AlarmManager)AlarmRingActivity.this.getSystemService(Context.ALARM_SERVICE);
        PendingIntent alarmIntent1 = PendingIntent.getBroadcast(AlarmRingActivity.this, request_id, intent, 0);
        alarmMgr1.setExact(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime()+ (7*24*60*60*1000), alarmIntent1);
        finishAndRemoveTask();
        //        overridePendingTransition(R.anim.slide_in_down, R.anim.slide_out_down);
        WakeLocker.release();
        isTurnedOff = true;
        AlarmReciever.isplaying = false;


    }

    class MyGestureListener extends GestureDetector.SimpleOnGestureListener {
        private static final float FRICTION = 0.1f;

        @Override
        public boolean onDown(MotionEvent event) {
            Log.d("TAG","onDown: ");
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
            FlingAnimation flingY = new FlingAnimation(actionButton, DynamicAnimation.TRANSLATION_Y);
            flingY.setStartVelocity(velocityY)
                    .setMinValue(-500)  // minimum translationY property
                    .setMaxValue(500) // maximum translationY property
                    .setFriction(FRICTION)
                    .start();
            if(Math.abs(movementX)<Math.abs(movementY)){
                if(Math.abs(movementY)>distanceSnoozeAction){
                    if(movementY>distanceSnoozeAction) {
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

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // Do nothing or catch the keys you want to block
        return false;
    }

    public static RectF calculeRectOnScreen(View view) {
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        return new RectF(location[0], location[1], location[0] + view.getMeasuredWidth(), location[1] + view.getMeasuredHeight());
    }

    public void commonPropertyforAlarmRing(){

        oneUpArrow.setImageDrawable(getResources().getDrawable(R.drawable.baseline_arrow_drop_up_white_18dp));
        twoUpArrow.setImageDrawable(getResources().getDrawable(R.drawable.baseline_arrow_drop_up_white_18dp));
        threeUpArrow.setImageDrawable(getResources().getDrawable(R.drawable.baseline_arrow_drop_up_white_18dp));
        fourUpArrow.setImageDrawable(getResources().getDrawable(R.drawable.baseline_arrow_drop_up_white_18dp));
        oneDownArrow.setImageDrawable(getResources().getDrawable(R.drawable.baseline_arrow_drop_down_white_18dp));
        twoDownArrow.setImageDrawable(getResources().getDrawable(R.drawable.baseline_arrow_drop_down_white_18dp));
        threeDownArrow.setImageDrawable(getResources().getDrawable(R.drawable.baseline_arrow_drop_down_white_18dp));
        fourDownArrow.setImageDrawable(getResources().getDrawable(R.drawable.baseline_arrow_drop_down_white_18dp));
//        actionButton.setBackground(R.drawable.circle_action_textview);
        actionButton.setBackgroundResource(R.drawable.circle_action_textview);
        snooozeText.setTextColor(Color.WHITE);
        cancelText.setTextColor(Color.WHITE);

    }

}
