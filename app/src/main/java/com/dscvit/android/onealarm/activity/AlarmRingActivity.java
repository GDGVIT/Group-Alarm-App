package com.dscvit.android.onealarm.activity;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.os.Vibrator;
import android.support.animation.DynamicAnimation;
import android.support.animation.SpringAnimation;
import android.support.animation.SpringForce;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dscvit.android.onealarm.misc.AlarmReciever;
import com.dscvit.android.onealarm.misc.DatabaseHandler;
import com.dscvit.android.onealarm.misc.NotificationReciever;
import com.dscvit.android.onealarm.R;
import com.dscvit.android.onealarm.Utils;
import com.dscvit.android.onealarm.misc.WakeLocker;

import java.util.Calendar;

public class AlarmRingActivity extends AppCompatActivity {

    private static final int NUM_REPEATS = 1000;
    private AlarmManager alarmMgr;
    private PendingIntent alarmIntent;
    Calendar calendar;
    ImageView oneUpArrow,twoUpArrow,threeUpArrow,fourUpArrow,oneDownArrow,twoDownArrow,threeDownArrow,fourDownArrow;
    int request_id;
    private float dY,dAY;
    SpringAnimation springAnimation;
    int backgroundColor;
    int hour,min;
    Vibrator v;
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
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
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
        handler.postDelayed(r,Utils.getAutoSilence(this));

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

        String time = db.getAlarmTimeFromAlarmRequestId(Integer.parseInt(String.valueOf(request_id).substring(3)));
        String[] splittedTime = time.split(":");
        hour = Integer.parseInt(splittedTime[0]);
        min = Integer.parseInt(splittedTime[1]);
        calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY,hour);
        calendar.set(Calendar.MINUTE,min);




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
            relativeLayout.setBackgroundResource(R.drawable.list_grad_brown);
            commonPropertyforAlarmRing();
        }else if(backgroundColor==5){
            relativeLayout.setBackgroundResource(R.drawable.list_grad_violet);
            commonPropertyforAlarmRing();
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
        if(ringtoneVibrateString[1]!=null && (Utils.getAlarmType(getApplicationContext())==0)){
            if(ringtoneVibrateString[1].equals("1")){
                long[] pattern = {0, 100, 1000};
                v.vibrate(pattern, 0);}
        }

//        mGestureDetector = new GestureDetector(this,new MyGestureListener());

        actionButton.setOnTouchListener(new View.OnTouchListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                float x = event.getX();
                float y = event.getY();
                switch (event.getAction()){

                    case MotionEvent.ACTION_DOWN:
                        springAnimation = new SpringAnimation(actionButton,DynamicAnimation.TRANSLATION_Y,0);
                        springAnimation.cancel();
                        dY = actionButton.getY() - event.getRawY();
                        dAY = actionButton.getY();
                        return true;

                    case MotionEvent.ACTION_MOVE:
                        actionButton.animate().y(event.getRawY() + dY).setDuration(0).start();
                        if(((event.getRawY()-dAY +dY)>(cancelText.getY() - dAY-cancelText.getTextSize())) && ((event.getRawY()-dAY +dY)<(cancelText.getY() - dAY + cancelText.getTextSize()))){
                            cancelText.setBackgroundColor(getResources().getColor(R.color.fiftyPercentVisible));
                        }else{
                            cancelText.setBackgroundColor(Color.TRANSPARENT);
                        }

                        if(((event.getRawY()-dAY +dY)>(snooozeText.getY() - dAY-snooozeText.getTextSize())) && ((event.getRawY()-dAY +dY)<(snooozeText.getY() - dAY + snooozeText.getTextSize()))){
                            snooozeText.setBackgroundColor(getResources().getColor(R.color.fiftyPercentVisible));
                        }else{
                            snooozeText.setBackgroundColor(Color.TRANSPARENT);
                        }

                        return true;

                    case MotionEvent.ACTION_UP:

                        springAnimation = new SpringAnimation(actionButton,DynamicAnimation.TRANSLATION_Y,0);

                        if(((event.getRawY()-dAY +dY)>(cancelText.getY() - dAY-cancelText.getTextSize())) && ((event.getRawY()-dAY +dY)<(cancelText.getY() - dAY + cancelText.getTextSize()))){
                            dismissAlarm();
                        }

                        if(((event.getRawY()-dAY +dY)>(snooozeText.getY() - dAY-snooozeText.getTextSize())) && ((event.getRawY()-dAY +dY)<(snooozeText.getY() - dAY + snooozeText.getTextSize()))){
                            snoozeAlarm();
                        }

                        springAnimation.getSpring().setDampingRatio(SpringForce.DAMPING_RATIO_HIGH_BOUNCY).setStiffness(SpringForce.STIFFNESS_MEDIUM);
                        springAnimation.start();
                        return true;

                }
                return false;
            }
        });
    }


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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            finishAndRemoveTask();
        }
//        overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
        Log.d("HH", "snoozeAlarm: after cancel alarm");
        AlarmManager alarmMgr1 = (AlarmManager)AlarmRingActivity.this.getSystemService(Context.ALARM_SERVICE);

        PendingIntent alarmIntent1 = PendingIntent.getBroadcast(AlarmRingActivity.this, request_id, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmMgr1.setExactAndAllowWhileIdle(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime()+ Utils.getSnoozeTime(this), alarmIntent1);
        }else{
            alarmMgr1.setExact(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime()+ Utils.getSnoozeTime(this), alarmIntent1);
        }
        WakeLocker.release();
        isTurnedOff = true;
        AlarmReciever.isplaying = false;


    }

    public void dismissAlarm() {
        alarmMgr = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(AlarmRingActivity.this, AlarmReciever.class);
        intent.putExtra("request_code", request_id);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(AlarmRingActivity.this, request_id, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        Log.d("dismissAlarm", "dismissAlarm: "+request_id);
        AlarmReciever ar = new AlarmReciever();
        if (AlarmReciever.r.isPlaying()) {
            AlarmReciever.r.stop();
        }
        v.cancel();
        alarmMgr.cancel(alarmIntent);
        AlarmManager alarmMgr1 = (AlarmManager)AlarmRingActivity.this.getSystemService(Context.ALARM_SERVICE);
        PendingIntent alarmIntent1 = PendingIntent.getBroadcast(AlarmRingActivity.this, request_id, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmMgr1.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis()+ AlarmManager.INTERVAL_DAY*7, alarmIntent1);
        }else{
            alarmMgr1.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis()+ AlarmManager.INTERVAL_DAY*7, alarmIntent1);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            finishAndRemoveTask();
        }
        //        overridePendingTransition(R.anim.slide_in_down, R.anim.slide_out_down);
        WakeLocker.release();
        isTurnedOff = true;
        AlarmReciever.isplaying = false;

        NotificationManager mNotificationManager = (NotificationManager) this.getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        Intent notifyIntent = new Intent(this, NotificationReciever.class);
        notifyIntent.putExtra("trimmedRequestId",request_id);
        PendingIntent pendingIntent = PendingIntent.getBroadcast
                (this, request_id, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        mNotificationManager.cancel(request_id);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmMgr.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis()+ (7*24*60*60*1000) - Utils.getNotificationDuration(getApplicationContext()), pendingIntent);
        }else{
            alarmMgr.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis()+ (7*24*60*60*1000) - Utils.getNotificationDuration(getApplicationContext()), pendingIntent);
        }
        Log.d("sas", "onReceive: " + request_id);


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
