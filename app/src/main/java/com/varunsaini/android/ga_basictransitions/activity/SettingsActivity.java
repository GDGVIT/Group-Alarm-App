package com.varunsaini.android.ga_basictransitions.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.rm.rmswitch.RMSwitch;
import com.varunsaini.android.ga_basictransitions.R;

public class SettingsActivity extends AppCompatActivity implements View.OnClickListener, RMSwitch.RMSwitchObserver {

    String[] snoozeDurationTime  = {"1 minute" , "3 minute" , "5 minute" , "10 minute" , "15 minute" , "30 minute" , "60 minutes" };
    String[] notificationDurationTime  = {"15 minute" ,"30 minute" , "45 minute" , "1 hour" , "2 hours" };
    String[] alarmypeArray = {"Sound and Vibration","Sound Only"};
    String[] timeFormatArray = {"24 hour","12 hour"};

    SharedPreferences.Editor editor;
    TextView snoozeTime_tv,autoSilence_tv,notificationTime_tv,alarmType_tv,titleActionBar,snoozeTitle_tv,autoSilenceTitle_tv,notificationTitle_tv,alarmTypeTitle_tv
            ,defaultSoundTitle_tv, defaultSound_tv,ascendingTitle_tv,ascendingSubtitle_tv,timeFormatTitle_tv,timeFormat_tv;
    ImageView backActionBar;
    RMSwitch ascending_rms_switch;
    SharedPreferences pref;
    int checkedItemTimeFormat =0;
    int checkedItemSnooze=0;
    int checkedItemAutoSilence=0;
    int checkedItemNotification=0;
    int checkedItemAlarmType=0;
    String checkedItemDefaultSound;
    boolean checkedAscendingVolume ;
    Typeface tf,tf1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        initViews();
        tf = Typeface.createFromAsset(getAssets(), "fonts/Karla-Bold.ttf");
        tf1 = Typeface.createFromAsset(getAssets(), "fonts/Karla.ttf");
        setTypefaces();
        backActionBar.setOnClickListener(this);
        pref = this.getSharedPreferences("MyPref", 0);
        editor = pref.edit();
        checkedItemSnooze = pref.getInt("snooze_duration",0);
        if(checkedItemSnooze<0){ checkedItemSnooze=0;}
        checkedItemAutoSilence = pref.getInt("auto_silence",0);
        if(checkedItemAutoSilence<0){ checkedItemAutoSilence=0;}
        checkedItemNotification = pref.getInt("notification_duration",0);
        if(checkedItemNotification<0){ checkedItemNotification=0;}
        checkedItemAlarmType = pref.getInt("alarm_type",0);
        if(checkedItemAlarmType<0){ checkedItemAlarmType=0;}
        checkedItemDefaultSound = pref.getString("default_sound",null);
        checkedAscendingVolume = pref.getBoolean("ascending_volume",true);
        checkedItemTimeFormat = pref.getInt("time_format",0);
        if(checkedItemTimeFormat<0){ checkedItemTimeFormat=0;}


        timeFormat_tv.setText(timeFormatArray[checkedItemTimeFormat]);
        snoozeTime_tv.setText(snoozeDurationTime[checkedItemSnooze]);
        autoSilence_tv.setText(snoozeDurationTime[checkedItemAutoSilence]);
        notificationTime_tv.setText(notificationDurationTime[checkedItemNotification]);
        alarmType_tv.setText(alarmypeArray[checkedItemAlarmType]);
        if(checkedItemDefaultSound!=null){
            defaultSound_tv.setText(RingtoneManager.getRingtone(this, Uri.parse(checkedItemDefaultSound)).getTitle(this));
        }else{
            defaultSound_tv.setText("Tap to Choose");
        }
        ascending_rms_switch.setChecked(checkedAscendingVolume);

        ascending_rms_switch.addSwitchObserver(this);
    }

    private void setTypefaces() {
        snoozeTime_tv.setTypeface(tf1);
        autoSilence_tv.setTypeface(tf1);
        notificationTime_tv.setTypeface(tf1);
        alarmType_tv.setTypeface(tf1);
        titleActionBar.setTypeface(tf);
        snoozeTitle_tv.setTypeface(tf);
        autoSilenceTitle_tv.setTypeface(tf);
        notificationTitle_tv.setTypeface(tf);
        alarmTypeTitle_tv.setTypeface(tf);
        defaultSoundTitle_tv.setTypeface(tf);
        defaultSound_tv.setTypeface(tf1);
        ascendingTitle_tv.setTypeface(tf);
        ascendingSubtitle_tv.setTypeface(tf1);
        timeFormatTitle_tv.setTypeface(tf);
        timeFormat_tv.setTypeface(tf1);
    }



    private void initViews() {
        snoozeTime_tv = findViewById(R.id.snoozeTime_tv);
        autoSilence_tv = findViewById(R.id.autoSilence_tv);
        notificationTime_tv = findViewById(R.id.notificationTime_tv);
        alarmType_tv = findViewById(R.id.alarmType_tv);
        backActionBar = findViewById(R.id.backActionBar);
        titleActionBar = findViewById(R.id.titleActionBar);
        snoozeTitle_tv = findViewById(R.id.snoozeTitle_tv);
        autoSilenceTitle_tv = findViewById(R.id.autoSilenceTitle_tv);
        notificationTitle_tv = findViewById(R.id.notificationTitle_tv);
        alarmTypeTitle_tv = findViewById(R.id.alarmTypeTitle_tv);
        defaultSoundTitle_tv = findViewById(R.id.defaultSoundTitle_tv);
        defaultSound_tv = findViewById(R.id.defaultSoundTime_tv);
        ascendingTitle_tv = findViewById(R.id.ascendingTitle_tv);
        ascendingSubtitle_tv = findViewById(R.id.ascendingSubtitle_tv);
        ascending_rms_switch = findViewById(R.id.ascending_rms_switch);
        timeFormatTitle_tv = findViewById(R.id.timeFormatTitle_tv);
        timeFormat_tv = findViewById(R.id.timeFormat_tv);
    }

    public void snoozeDuration(View view){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Snooze Duration");
        builder.setItems(snoozeDurationTime, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.d("as", "onSelected: "+which);
                snoozeTime_tv.setText(snoozeDurationTime[which]);
                editor.putInt("snooze_duration",which);
                editor.apply();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }


    public void autoSilence(View view){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Auto Silence");
        builder.setItems(snoozeDurationTime, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.d("as", "onSelected: "+which);
                autoSilence_tv.setText(snoozeDurationTime[which]);
                editor.putInt("auto_silence",which);
                editor.apply();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }


    public void notificationDuration(View view){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Notification Duration");
        builder.setItems(notificationDurationTime, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.d("as", "onSelected: "+which);
                notificationTime_tv.setText(notificationDurationTime[which]);
                editor.putInt("notification_duration",which);
                editor.apply();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void alarmType(View view){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Alarm Type");
        builder.setItems(alarmypeArray, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.d("as", "onSelected: "+which);
                alarmType_tv.setText(alarmypeArray[which]);
                editor.putInt("alarm_type",which);
                editor.apply();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void defaultAlarmSound(View view){
        Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "Select ringtone for alarm:");
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_SILENT, false);
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_DEFAULT, true);
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_NOTIFICATION);
        startActivityForResult(intent, 1);
    }

    @Override
    public void onClick(View v) {
        super.onBackPressed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case 1:
                    Ringtone ringtone = RingtoneManager.getRingtone(this, (Uri) data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI));
                    String title = ringtone.getTitle(this);
                    defaultSound_tv.setText(title);
                    String mRingtoneUri = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI).toString();
                    Log.d("hhhj", "onActivityResult: "+mRingtoneUri);
                    editor.putString("default_sound",mRingtoneUri);
                    editor.apply();
                    break;
                default:
                    break;
            }
        }
    }

    public void timeFormat(View view){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Time Format");
        builder.setItems(timeFormatArray, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.d("as", "onSelected: "+which);
                timeFormat_tv.setText(timeFormatArray[which]);
                editor.putInt("time_format",which);
                editor.apply();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public void onCheckStateChange(RMSwitch switchView, boolean isChecked) {
        editor.putBoolean("ascending_volume",isChecked);
        editor.apply();
    }


}
