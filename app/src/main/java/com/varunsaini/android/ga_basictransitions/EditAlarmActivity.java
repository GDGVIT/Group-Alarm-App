package com.varunsaini.android.ga_basictransitions;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import com.kevalpatel.ringtonepicker.RingtonePickerDialog;
import com.kevalpatel.ringtonepicker.RingtonePickerListener;
import com.rm.rmswitch.RMSwitch;

import java.util.Calendar;

public class EditAlarmActivity extends AppCompatActivity {

    String mRingtoneUri;
    TextView alarmTime,groupNameTitle;
    EditText labelEdittext;
    RMSwitch rmSwitch1;
    int groupColor,groupState;
    String groupName;
    private AlarmManager alarmMgr;
    private PendingIntent alarmIntent;
    int mSelectedHour = Calendar.HOUR_OF_DAY;
    int mSelectedMinute = Calendar.MINUTE;
    int alarm_pending_req_code;
    String[] allpreviousAlarmData;
    String nameOfGroup;


    SQLiteDatabase sqLiteDatabase;
    DatabaseHandler db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_alarm);
        alarmTime = findViewById(R.id.alarmTime);
        labelEdittext = findViewById(R.id.labelEdittext);
        rmSwitch1 = findViewById(R.id.rm_switch1);
        groupNameTitle = findViewById(R.id.group_name);

        db = new DatabaseHandler(this);
        sqLiteDatabase = this.openOrCreateDatabase("Alarm",MODE_PRIVATE,null);
        db.onCreate(sqLiteDatabase);

        nameOfGroup = getIntent().getStringExtra("nameOfGroup");
        Log.d("asa", "onCreate: Edittext"+nameOfGroup);
        if(nameOfGroup!=null){
            groupNameTitle.setText(nameOfGroup);
            groupName = nameOfGroup;
            // put groupColor and groupState here
        }

        alarm_pending_req_code = getIntent().getIntExtra("alarm_pending_req_code",-1);
        if (alarm_pending_req_code>0){
            allpreviousAlarmData = db.getAllPreviousEditAlarmData(alarm_pending_req_code);
            alarmTime.setText(allpreviousAlarmData[0]+":"+allpreviousAlarmData[1]);
            groupNameTitle.setText(allpreviousAlarmData[4]);
            labelEdittext.setText(allpreviousAlarmData[7]);
            if(allpreviousAlarmData[2].equals("0")){
                rmSwitch1.setChecked(false);
            }
        }

////        sqLiteDatabase.execSQL("drop table if exists alarmss");
//        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS alarmss(hours int(2),minutes int(2),alarmState int(1),group_name VARCHAR(50),days VARCHAR(50),group_color int(20),group_state boolean,alarm_label text,ringtone_uri VARCHAR(256),vibrate boolean,alarm_pending_req_code int primary key)");

        int belongs_to_group = getIntent().getIntExtra("belongs_to_group",-1);
        if(belongs_to_group==-1 && nameOfGroup==null){
            groupName=null;
            groupColor=0;//0 means does not belong to group
            groupState=0;  //0 means does not belong to group

        }

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.custom_main_action_bar_layout);
        View view =getSupportActionBar().getCustomView();

    }

    public void ringtoneSelect(View view){
        RingtonePickerDialog.Builder ringtonePickerBuilder = new RingtonePickerDialog
                .Builder(EditAlarmActivity.this, getSupportFragmentManager())

                //Set title of the dialog.
                //If set null, no title will be displayed.
                .setTitle("Select ringtone")

                //set the currently selected uri, to mark that ringtone as checked by default.
                //If no ringtone is currently selected, pass null.
//                .setCurrentRingtoneUri(/* Prevously selected ringtone Uri */)

                //Set true to allow allow user to select default ringtone set in phone settings.
                .displayDefaultRingtone(true)

                //Set true to allow user to select silent (i.e. No ringtone.).
                .displaySilentRingtone(true)

                //set the text to display of the positive (ok) button.
                //If not set OK will be the default text.
                .setPositiveButtonText("SET RINGTONE")

                //set text to display as negative button.
                //If set null, negative button will not be displayed.
                .setCancelButtonText("CANCEL")

                //Set flag true if you want to play the sample of the clicked tone.
                .setPlaySampleWhileSelection(true)

                //Set the callback listener.
                .setListener(new RingtonePickerListener() {
                    @Override
                    public void OnRingtoneSelected(@NonNull String ringtoneName, Uri ringtoneUri) {
                        //Do someting with selected uri...
                        mRingtoneUri = ringtoneUri.toString();
                    }
                });

//Add the desirable ringtone types.
//        ringtonePickerBuilder.addRingtoneType(RingtonePickerDialog.Builder.TYPE_MUSIC);
        ringtonePickerBuilder.addRingtoneType(RingtonePickerDialog.Builder.TYPE_NOTIFICATION);
        ringtonePickerBuilder.addRingtoneType(RingtonePickerDialog.Builder.TYPE_RINGTONE);
        ringtonePickerBuilder.addRingtoneType(RingtonePickerDialog.Builder.TYPE_ALARM);

//Display the dialog.
        ringtonePickerBuilder.show();
    }

    public void setAlarmTime(View view) {

        Calendar mcurrentTime = Calendar.getInstance();
        int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
        int minute = mcurrentTime.get(Calendar.MINUTE);
        TimePickerDialog mTimePicker;
        mTimePicker = new TimePickerDialog(EditAlarmActivity.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                mSelectedHour = selectedHour;
                mSelectedMinute = selectedMinute;
                alarmTime.setText( selectedHour + ":" + selectedMinute);
            }
        }, hour, minute, true);//Yes 24 hour time
        mTimePicker.setTitle("Select Time");
        mTimePicker.show();

    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void setAlarmOn(View v){

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, mSelectedHour);
        calendar.set(Calendar.MINUTE, mSelectedMinute);

        String labelText;
        if (labelEdittext.getText().toString().equals("")) {
            labelText = null;
        } else {
            labelText = labelEdittext.getText().toString();
        }

        if(alarm_pending_req_code>0){
            db.updataAllAlarmData(mSelectedHour,mSelectedMinute,1,null,labelText,mRingtoneUri,0,alarm_pending_req_code);

            Log.d("CMS", "setAlarmOn: "+alarm_pending_req_code);
            alarmMgr = (AlarmManager)EditAlarmActivity.this.getSystemService(Context.ALARM_SERVICE);
            Intent intent = new Intent(EditAlarmActivity.this, AlarmReciever.class);
            intent.putExtra("request_code",alarm_pending_req_code);
            alarmIntent = PendingIntent.getBroadcast(EditAlarmActivity.this, alarm_pending_req_code, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        }else{
            int currentTimeInMilliSeconds = (int) calendar.getTimeInMillis();

            Alarm a = new Alarm(mSelectedHour, mSelectedMinute, 1, null, groupName, groupColor, groupState, labelText, mRingtoneUri, 0, currentTimeInMilliSeconds);
            db.addAlarm(a);
            Log.d("CMS", "setAlarmOn: "+currentTimeInMilliSeconds);
            alarmMgr = (AlarmManager)EditAlarmActivity.this.getSystemService(Context.ALARM_SERVICE);
            Intent intent = new Intent(EditAlarmActivity.this, AlarmReciever.class);
            intent.putExtra("request_code",(int)currentTimeInMilliSeconds);
            alarmIntent = PendingIntent.getBroadcast(EditAlarmActivity.this, (int) currentTimeInMilliSeconds, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        }

        alarmMgr.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), alarmIntent);
        Intent i = new Intent(EditAlarmActivity.this,AllActivity.class);
        startActivity(i);
    }

}
