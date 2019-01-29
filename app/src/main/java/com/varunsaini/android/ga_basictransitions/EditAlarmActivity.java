package com.varunsaini.android.ga_basictransitions;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.kevalpatel.ringtonepicker.RingtonePickerDialog;
import com.kevalpatel.ringtonepicker.RingtonePickerListener;
import com.rm.rmswitch.RMSwitch;

import java.util.ArrayList;
import java.util.Calendar;

public class EditAlarmActivity extends AppCompatActivity {

    String mRingtoneUri;
    Uri currentRingtoneUri = null;
    TextView alarmTime,groupNameTitle;
    EditText labelEdittext;
    RMSwitch rmSwitch1;
    Button buttonMon,buttonTue,buttonWed,buttonThurs,buttonFri,buttonSat,buttonSun;
    int groupColor,groupState;
    String groupName;
    private AlarmManager alarmMgr;
    private PendingIntent[] alarmIntent;
    int mSelectedHour = Calendar.HOUR_OF_DAY;
    int mSelectedMinute = Calendar.MINUTE;
    int alarm_pending_req_code;
    String[] allpreviousAlarmData;
    String nameOfGroup;
    String daystoRing = "";
    String stateMon,stateTue,stateWed,stateThurs,stateFri,stateSat,stateSun;
    String[] stateOfDays ;
    String[] days = {"mon","tue","wed","thurs","fri","sat","sun"};


    SQLiteDatabase sqLiteDatabase;
    DatabaseHandler db;

    @SuppressLint({"RestrictedApi", "ResourceAsColor"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_alarm);
        alarmTime = findViewById(R.id.alarmTime);
        labelEdittext = findViewById(R.id.labelEdittext);
        rmSwitch1 = findViewById(R.id.rm_switch1);
        groupNameTitle = findViewById(R.id.group_name);
//        Button[] bt = {buttonMon,buttonTue,buttonWed,buttonThurs,buttonFri,buttonSat,buttonSun};
//        int[] buttonId = {R.id.buttonMon,R.id.buttonTue,R.id.buttonWed,R.id.buttonThurs,R.id.buttonFri,R.id.buttonSat,R.id.buttonSun};
//        String[] stateOfDays = {stateMon,stateTue,stateWed,stateThurs,stateFri,stateSat,stateSun};
//        for(int i=0;i<7;i++){
//            bt[i] = (Button)findViewById(buttonId[i]);
//            Log.d("dss", "onCreate: "+bt[i] + " = " + findViewById(buttonId[i]));
//            stateOfDays[i] = "unchecked";
//        }
        stateMon = "unchecked";;
        stateTue = "unchecked";
        stateWed = "unchecked";
        stateThurs = "unchecked";
        stateFri = "unchecked";
        stateSat = "unchecked";
        stateSun = "unchecked";

        buttonMon = findViewById(R.id.buttonMon);
        buttonTue = findViewById(R.id.buttonTue);
        buttonWed = findViewById(R.id.buttonWed);
        buttonThurs = findViewById(R.id.buttonThurs);
        buttonFri = findViewById(R.id.buttonFri);
        buttonSat = findViewById(R.id.buttonSat);
        buttonSun = findViewById(R.id.buttonSun);

        buttonMon.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View v) {
                if (stateMon.equals("checked")){
                    stateMon = "unchecked";
                    buttonMon.setBackgroundColor(R.color.white);
                }else{
                    stateMon = "checked";
                    buttonMon.setBackgroundColor(R.color.fabBlue);
                }
            }
        });

        buttonTue.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View v) {
                if (stateTue.equals("checked")){
                    stateTue = "unchecked";
                    buttonTue.setBackgroundColor(R.color.white);
                }else{
                    stateTue = "checked";
                    buttonTue.setBackgroundColor(R.color.fabBlue);
                }
            }
        });

        buttonWed.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View v) {
                if (stateWed.equals("checked")){
                    stateWed = "unchecked";
                    buttonWed.setBackgroundColor(R.color.white);
                }else{
                    stateWed = "checked";
                    buttonWed.setBackgroundColor(R.color.fabBlue);

                }
            }
        });

        buttonThurs.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View v) {
                if (stateThurs.equals("checked")){
                    stateThurs = "unchecked";
                    buttonThurs.setBackgroundColor(R.color.white);
                }else{
                    stateThurs = "checked";
                    buttonThurs.setBackgroundColor(R.color.fabBlue);

                }
            }
        });

        buttonFri.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View v) {
                if (stateFri.equals("checked")){
                    stateFri = "unchecked";
                    buttonFri.setBackgroundColor(R.color.white);
                }else{
                    stateFri = "checked";
                    buttonFri.setBackgroundColor(R.color.fabBlue);

                }
            }
        });


        buttonSat.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View v) {
                if (stateSat.equals("checked")){
                    stateSat = "unchecked";
                    buttonSat.setBackgroundColor(R.color.white);
                }else{
                    stateSat = "checked";
                    buttonSat.setBackgroundColor(R.color.fabBlue);

                }
            }
        });

        buttonSun.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View v) {
                if (stateSun.equals("checked")){
                    stateSun = "unchecked";
                    buttonSun.setBackgroundColor(R.color.white);
                }else{
                    stateSun = "checked";
                    buttonSun.setBackgroundColor(R.color.fabBlue);
                }
            }
        });


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
            mSelectedHour = Integer.parseInt(allpreviousAlarmData[0]);
            mSelectedMinute = Integer.parseInt(allpreviousAlarmData[1]);
            groupNameTitle.setText(allpreviousAlarmData[4]);
            labelEdittext.setText(allpreviousAlarmData[7]);
            if(allpreviousAlarmData[8]==null){
                currentRingtoneUri = null;
            }else {
                currentRingtoneUri = Uri.parse(allpreviousAlarmData[8]);
            }
            if(allpreviousAlarmData[2].equals("0")){
                rmSwitch1.setChecked(false);
            }
            if(!allpreviousAlarmData[3].equals("")){
                String savedDays = allpreviousAlarmData[3];
                Log.d("aaa", "onCreate: "+savedDays);
                String[] individualDays = savedDays.split("#");
                for(String d:individualDays){
                    Log.d("aaew", "onCreate: "+d+'\n');
                    if(d.equals("mon")){
                        stateMon = "checked";
                        buttonMon.setBackgroundColor(R.color.fabBlue);
                    }else if(d.equals("tue")){
                        stateTue = "checked";
                        buttonTue.setBackgroundColor(R.color.fabBlue);
                    }else if(d.equals("wed")){
                        stateWed = "checked";
                        buttonWed.setBackgroundColor(R.color.fabBlue);
                    }else if(d.equals("thurs")){
                        stateThurs = "checked";
                        buttonThurs.setBackgroundColor(R.color.fabBlue);
                    }else if(d.equals("fri")){
                        stateFri = "checked";
                        buttonFri.setBackgroundColor(R.color.fabBlue);
                    }else if(d.equals("sat")){
                        stateSat = "checked";
                        buttonSat.setBackgroundColor(R.color.fabBlue);
                    }else if(d.equals("sun")){
                        stateSun = "checked";
                        buttonSun.setBackgroundColor(R.color.fabBlue);
                    }
                }
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
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);


//        stateOfDays= {stateMon, stateTue, stateWed, stateThurs, stateFri, stateSat, stateSun};

    }

    public void ringtoneSelect(View view){
        RingtonePickerDialog.Builder ringtonePickerBuilder = new RingtonePickerDialog
                .Builder(EditAlarmActivity.this, getSupportFragmentManager())

                .setTitle("Select ringtone")
                .setCurrentRingtoneUri((currentRingtoneUri))
                .displayDefaultRingtone(true)

                //Set true to allow user to select silent (i.e. No ringtone.).
                .displaySilentRingtone(true)
                .setPositiveButtonText("SET RINGTONE")
                .setCancelButtonText("CANCEL")
                .setPlaySampleWhileSelection(true)
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
    public void setAlarmOn(View v) {

        stateOfDays = new String[7];
        stateOfDays[0] = stateMon;
        stateOfDays[1] = stateTue;
        stateOfDays[2] = stateWed;
        stateOfDays[3] = stateThurs;
        stateOfDays[4] = stateFri;
        stateOfDays[5] = stateSat;
        stateOfDays[6] = stateSun;

        for (int i = 0; i < 7; i++) {
            if (stateOfDays[i].equals("checked")) {
                daystoRing += days[i] + "#";
            }
        }

        Calendar now = Calendar.getInstance();
        long _alarm = 0;

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, mSelectedHour);
        calendar.set(Calendar.MINUTE, mSelectedMinute);
        calendar.set(Calendar.SECOND,0);

        String labelText;
        if (labelEdittext.getText().toString().equals("")) {
            labelText = null;
        } else {
            labelText = labelEdittext.getText().toString();
        }

//        if(calendar.getTimeInMillis() <= now.getTimeInMillis())
//            _alarm = calendar.getTimeInMillis() + (AlarmManager.INTERVAL_DAY+7);
//        else
//            _alarm = calendar.getTimeInMillis();


        if (alarm_pending_req_code > 0) {

//            if(calendar.getTimeInMillis() <= now.getTimeInMillis())
//                _alarm = calendar.getTimeInMillis() + (AlarmManager.INTERVAL_DAY+7);
//            else
//                _alarm = calendar.getTimeInMillis();
            db.updataAllAlarmData(mSelectedHour, mSelectedMinute, 1, daystoRing, labelText, mRingtoneUri, 0, alarm_pending_req_code);

            Log.d("CMS", "setAlarmOn: " + alarm_pending_req_code);
            alarmMgr = (AlarmManager) EditAlarmActivity.this.getSystemService(Context.ALARM_SERVICE);
            Intent intent = new Intent(EditAlarmActivity.this, AlarmReciever.class);
//            intent.putExtra("request_code", alarm_pending_req_code);


            if (!daystoRing.equals("")) {
                String[] dayys = daystoRing.split("#");
                alarmIntent = new PendingIntent[dayys.length];
                int[] allRequests = new int[dayys.length];
                int k = 0;

                alarmMgr = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
                ArrayList<Integer> integerArrayList = db.getThisAlarmIntents(Integer.valueOf(String.valueOf(alarm_pending_req_code).substring(0,7)));
                for(Integer i : integerArrayList) {
                    PendingIntent alarmIntent = PendingIntent.getBroadcast(this, Integer.valueOf(i), intent, PendingIntent.FLAG_UPDATE_CURRENT);
                    alarmMgr.cancel(alarmIntent);
                }

                for (String d : dayys) {
                    if (d.equals("mon")) {
                        String x = String.valueOf(alarm_pending_req_code);
                        int y = Integer.valueOf(x.substring(0,7) + "111");
                        calendar.set(Calendar.DAY_OF_WEEK, 2);
                        Log.d("xzx", "setAlarmOn: "+y);
                        intent.putExtra("request_code", y);
                        if(calendar.getTimeInMillis() <= now.getTimeInMillis())
                        {Log.d("if", "setAlarmOn: "+"inside if");
                            _alarm = calendar.getTimeInMillis() + (24*60*60*1000*7);}
                        else{
                            Log.d("else", "setAlarmOn: "+"inside else");
                            _alarm = calendar.getTimeInMillis();}
                        alarmIntent[k] = PendingIntent.getBroadcast(EditAlarmActivity.this, y, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                        alarmMgr.setExact(AlarmManager.RTC_WAKEUP, _alarm, alarmIntent[k]);
                        allRequests[k] = y;
                        k++;
                    } else if (d.equals("tue")) {
                        String x = String.valueOf(alarm_pending_req_code);
                        int y = Integer.valueOf(x.substring(0,7) + "222");
                        calendar.set(Calendar.DAY_OF_WEEK, 3);
                        Log.d("xzx", "setAlarmOn: "+y);
                        intent.putExtra("request_code", y);
                        if(calendar.getTimeInMillis() <= now.getTimeInMillis())
                        {Log.d("if", "setAlarmOn: "+"inside if");
                            _alarm = calendar.getTimeInMillis() + (24*60*60*1000*7);}
                        else{
                            Log.d("else", "setAlarmOn: "+"inside else");
                            _alarm = calendar.getTimeInMillis();}
                            alarmIntent[k] = PendingIntent.getBroadcast(EditAlarmActivity.this, y, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                        alarmMgr.setExact(AlarmManager.RTC_WAKEUP, _alarm, alarmIntent[k]);
                        allRequests[k] = y;
                        k++;
                    } else if (d.equals("wed")) {
                        String x = String.valueOf(alarm_pending_req_code);
                        int y = Integer.valueOf(x.substring(0,7) + "333");
                        calendar.set(Calendar.DAY_OF_WEEK, 4);
                        Log.d("xzx", "setAlarmOn: "+y);
                        intent.putExtra("request_code", y);
                        if(calendar.getTimeInMillis() <= now.getTimeInMillis())
                            _alarm = calendar.getTimeInMillis() + (24*60*60*1000*7);
                        else
                            _alarm = calendar.getTimeInMillis();
                        alarmIntent[k] = PendingIntent.getBroadcast(EditAlarmActivity.this, y, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                        alarmMgr.setExact(AlarmManager.RTC_WAKEUP, _alarm, alarmIntent[k]);
                        allRequests[k] = y;
                        k++;
                    } else if (d.equals("thurs")) {
                        String x = String.valueOf(alarm_pending_req_code);
                        int y = Integer.valueOf(x.substring(0,7) + "444");
                        calendar.set(Calendar.DAY_OF_WEEK, 5);
                        Log.d("xzx", "setAlarmOn: "+y);
                        intent.putExtra("request_code", y);
                        if(calendar.getTimeInMillis() <= now.getTimeInMillis())
                        {Log.d("if", "setAlarmOn: "+"inside if");
                            _alarm = calendar.getTimeInMillis() + (24*60*60*1000*7);}
                        else{
                            Log.d("else", "setAlarmOn: "+"inside else");
                            _alarm = calendar.getTimeInMillis();}
                            alarmIntent[k] = PendingIntent.getBroadcast(EditAlarmActivity.this, y, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                        alarmMgr.setExact(AlarmManager.RTC_WAKEUP, _alarm, alarmIntent[k]);
                        allRequests[k] = y;
                        k++;
                    } else if (d.equals("fri")) {
                        String x = String.valueOf(alarm_pending_req_code);
                        int y = Integer.valueOf(x.substring(0,7) + "555");
                        calendar.set(Calendar.DAY_OF_WEEK, 6);
                        Log.d("xzx", "setAlarmOn: "+y);
                        intent.putExtra("request_code", y);
                        if(calendar.getTimeInMillis() <= now.getTimeInMillis())
                            _alarm = calendar.getTimeInMillis() + (24*60*60*1000*7);
                        else
                            _alarm = calendar.getTimeInMillis();
                        alarmIntent[k] = PendingIntent.getBroadcast(EditAlarmActivity.this, y, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                        alarmMgr.setExact(AlarmManager.RTC_WAKEUP, _alarm, alarmIntent[k]);
                        allRequests[k] = y;
                        k++;
                    } else if (d.equals("sat")) {
                        String x = String.valueOf(alarm_pending_req_code);
                        int y = Integer.valueOf(x.substring(0,7) + "666");
                        calendar.set(Calendar.DAY_OF_WEEK, 7);
                        Log.d("xzx", "setAlarmOn: "+y);
                        intent.putExtra("request_code", y);
                        if(calendar.getTimeInMillis() <= now.getTimeInMillis())
                            _alarm = calendar.getTimeInMillis() + (24*60*60*1000*7);
                        else
                            _alarm = calendar.getTimeInMillis();
                        alarmIntent[k] = PendingIntent.getBroadcast(EditAlarmActivity.this, y, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                        alarmMgr.setExact(AlarmManager.RTC_WAKEUP, _alarm, alarmIntent[k]);
                        allRequests[k] = y;
                        k++;
                    } else if (d.equals("sun")) {
                        String x = String.valueOf(alarm_pending_req_code);
                        int y = Integer.valueOf(x.substring(0,7) + "777");
                        calendar.set(Calendar.DAY_OF_WEEK, 1);
                        Log.d("xzx", "setAlarmOn: "+y);
                        intent.putExtra("request_code", y);
                        Log.d("calendar.getTimeInMil", "setAlarmOn: calendar.getTimeInMillis()" + calendar.getTimeInMillis());
                        Log.d("now.getTimeInMill", "setAlarmOn: now.getTimeInMillis()" + now.getTimeInMillis());
                        if(calendar.getTimeInMillis() <= now.getTimeInMillis())
                        {Log.d("if", "setAlarmOn: "+"inside if");
                            _alarm = calendar.getTimeInMillis() + (24*60*60*1000*7);}
                        else{
                            Log.d("else", "setAlarmOn: "+"inside else");
                            _alarm = calendar.getTimeInMillis();}
                        alarmIntent[k] = PendingIntent.getBroadcast(EditAlarmActivity.this, y, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                        alarmMgr.setExact(AlarmManager.RTC_WAKEUP, _alarm, alarmIntent[k]);
                        allRequests[k] = y;
                        k++;
                    }
                }

                //remove pending intents
//                alarmMgr = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
//                ArrayList<Integer> integerArrayList = db.getThisAlarmIntents(Integer.valueOf(String.valueOf(alarm_pending_req_code).substring(0,7)));
//                for(Integer i : integerArrayList) {
//                    PendingIntent alarmIntent = PendingIntent.getBroadcast(this, Integer.valueOf(i), intent, PendingIntent.FLAG_UPDATE_CURRENT);
//                    alarmMgr.cancel(alarmIntent);
//                }
                db.removeDaysPendingReq(Integer.valueOf(String.valueOf(alarm_pending_req_code).substring(0,7)));
                for(int i=0;i<dayys.length;i++){
                    db.addDaysPendingReq(allRequests[i]);
                }


            }
            if(alarmIntent==null||alarmIntent.length<1 ){
                Toast.makeText(this, "Select days", Toast.LENGTH_SHORT).show();
            }else {
//                for (int i = 0; i < alarmIntent.length; i++) {
//                    Log.d("sI", "settingIntents "+alarmIntent[i]);
////                    alarmMgr.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), alarmIntent[i]);
////                    alarmMgr.setExact(AlarmManager.RTC_WAKEUP, _alarm, alarmIntent[i]);
//                }

                Intent i = new Intent(EditAlarmActivity.this, AllActivity.class);
                startActivity(i);
            }

//            alarmIntent = PendingIntent.getBroadcast(EditAlarmActivity.this, alarm_pending_req_code, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        } else {

            int currentTimeInMilliSeconds = (int) calendar.getTimeInMillis();
            if (currentTimeInMilliSeconds < 0) {
                currentTimeInMilliSeconds = -1 * currentTimeInMilliSeconds;
            }

            Alarm a = new Alarm(mSelectedHour, mSelectedMinute, 1, daystoRing, groupName, groupColor, groupState, labelText, mRingtoneUri, 0, currentTimeInMilliSeconds);
//            db.addAlarm(a);
            Log.d("CMS", "setAlarmOn: " + currentTimeInMilliSeconds);

            alarmMgr = (AlarmManager) EditAlarmActivity.this.getSystemService(Context.ALARM_SERVICE);
            Intent intent = new Intent(EditAlarmActivity.this, AlarmReciever.class);
//            intent.putExtra("request_code", (int) currentTimeInMilliSeconds);


            if (!daystoRing.equals("")) {
                String[] dayys = daystoRing.split("#");
                alarmIntent = new PendingIntent[dayys.length];
                int k = 0;
                for (String d : dayys) {
                    if (d.equals("mon")) {
                        String x = String.valueOf(currentTimeInMilliSeconds);
                        int y = Integer.valueOf(x.substring(0,7) + "111");
                        db.addDaysPendingReq(y);
                        calendar.set(Calendar.DAY_OF_WEEK, 2);
                        Log.d("xzx", "setAlarmOn: "+y);
                        intent.putExtra("request_code", y);
                        Log.d("cal", "setAlarmOn: calMilli:"+calendar.getTimeInMillis());
                        Log.d("now", "setAlarmOn: nowMilli:"+now.getTimeInMillis());
                        if(calendar.getTimeInMillis() <= now.getTimeInMillis())
                        {Log.d("if", "setAlarmOn: "+"inside if");
                            _alarm = calendar.getTimeInMillis() + (24*60*60*1000*7);}
                        else{
                            Log.d("else", "setAlarmOn: "+"inside else");
                            _alarm = calendar.getTimeInMillis();}
                        alarmIntent[k] = PendingIntent.getBroadcast(EditAlarmActivity.this, y, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                        alarmMgr.setExact(AlarmManager.RTC_WAKEUP, _alarm, alarmIntent[k]);
                        k++;
                        Log.d("kkl", "setAlarmOn: "+"Alarm Mon End");
                    } else if (d.equals("tue")) {
                        String x = String.valueOf(currentTimeInMilliSeconds);
                        int y = Integer.valueOf(x.substring(0,7) + "222");
                        db.addDaysPendingReq(y);
                        calendar.set(Calendar.DAY_OF_WEEK, 3);
                        Log.d("xzx", "setAlarmOn: "+y);
                        intent.putExtra("request_code", y);
                        Log.d("cal", "setAlarmOn: calMilli:"+calendar.getTimeInMillis());
                        Log.d("now", "setAlarmOn: nowMilli:"+now.getTimeInMillis());
                        if(calendar.getTimeInMillis() <= now.getTimeInMillis())
                        {Log.d("if", "setAlarmOn: "+"inside if");
                            _alarm = calendar.getTimeInMillis() + (24*60*60*1000*7);}
                        else{
                            Log.d("else", "setAlarmOn: "+"inside else");
                            _alarm = calendar.getTimeInMillis();}
                            alarmIntent[k] = PendingIntent.getBroadcast(EditAlarmActivity.this, y, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                        alarmMgr.setExact(AlarmManager.RTC_WAKEUP, _alarm, alarmIntent[k]);

                        k++;
                    } else if (d.equals("wed")) {
                        String x = String.valueOf(currentTimeInMilliSeconds);
                        int y = Integer.valueOf(x.substring(0,7) + "333");
                        db.addDaysPendingReq(y);
                        calendar.set(Calendar.DAY_OF_WEEK, 4);
                        Log.d("xzx", "setAlarmOn: "+y);
                        intent.putExtra("request_code", y);
                        if(calendar.getTimeInMillis() <= now.getTimeInMillis())
                            _alarm = calendar.getTimeInMillis() + (24*60*60*1000*7);
                        else
                            _alarm = calendar.getTimeInMillis();
                        alarmIntent[k] = PendingIntent.getBroadcast(EditAlarmActivity.this, y, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                        alarmMgr.setExact(AlarmManager.RTC_WAKEUP, _alarm, alarmIntent[k]);
                        k++;
                    } else if (d.equals("thurs")) {
                        String x = String.valueOf(currentTimeInMilliSeconds);
                        int y = Integer.valueOf(x.substring(0,7) + "444");
                        db.addDaysPendingReq(y);
                        calendar.set(Calendar.DAY_OF_WEEK, 5);
                        Log.d("xzx", "setAlarmOn: "+y);
                        intent.putExtra("request_code", y);
                        if(calendar.getTimeInMillis() <= now.getTimeInMillis())
                        {Log.d("if", "setAlarmOn: "+"inside if");
                            _alarm = calendar.getTimeInMillis() + (24*60*60*1000*7);}
                        else{
                            Log.d("else", "setAlarmOn: "+"inside else");
                            _alarm = calendar.getTimeInMillis();}
                            alarmIntent[k] = PendingIntent.getBroadcast(EditAlarmActivity.this, y, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                        alarmMgr.setExact(AlarmManager.RTC_WAKEUP, _alarm, alarmIntent[k]);
                        k++;
                    } else if (d.equals("fri")) {
                        String x = String.valueOf(currentTimeInMilliSeconds);
                        int y = Integer.valueOf(x.substring(0,7) + "555");
                        db.addDaysPendingReq(y);
                        calendar.set(Calendar.DAY_OF_WEEK, 6);
                        Log.d("xzx", "setAlarmOn: "+y);
                        intent.putExtra("request_code", y);
                        if(calendar.getTimeInMillis() <= now.getTimeInMillis())
                            _alarm = calendar.getTimeInMillis() + (24*60*60*1000*7);
                        else
                            _alarm = calendar.getTimeInMillis();
                        alarmIntent[k] = PendingIntent.getBroadcast(EditAlarmActivity.this, y, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                        alarmMgr.setExact(AlarmManager.RTC_WAKEUP, _alarm, alarmIntent[k]);
                        k++;
                    } else if (d.equals("sat")) {
                        String x = String.valueOf(currentTimeInMilliSeconds);
                        int y = Integer.valueOf(x.substring(0,7) + "666");
                        db.addDaysPendingReq(y);
                        calendar.set(Calendar.DAY_OF_WEEK, 7);
                        Log.d("xzx", "setAlarmOn: "+y);
                        intent.putExtra("request_code", y);
                        if(calendar.getTimeInMillis() <= now.getTimeInMillis())
                            _alarm = calendar.getTimeInMillis() + ((24*60*60*1000*7));
                        else
                            _alarm = calendar.getTimeInMillis();
                        alarmIntent[k] = PendingIntent.getBroadcast(EditAlarmActivity.this, y, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                        alarmMgr.setExact(AlarmManager.RTC_WAKEUP, _alarm, alarmIntent[k]);
                        k++;
                    } else if (d.equals("sun")) {
                        String x = String.valueOf(currentTimeInMilliSeconds);
                        int y = Integer.valueOf(x.substring(0,7) + "777");
                        db.addDaysPendingReq(y);
                        calendar.set(Calendar.DAY_OF_WEEK, 1);
                        Log.d("xzx", "setAlarmOn: "+y);
                        intent.putExtra("request_code", y);
                        if(calendar.getTimeInMillis() <= now.getTimeInMillis())
                            _alarm = calendar.getTimeInMillis() + (24*60*60*1000*7);
                        else
                            _alarm = calendar.getTimeInMillis();
                        alarmIntent[k] = PendingIntent.getBroadcast(EditAlarmActivity.this, y, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                        alarmMgr.setExact(AlarmManager.RTC_WAKEUP, _alarm, alarmIntent[k]);
                        k++;
                    }

                }
//            alarmIntent = PendingIntent.getBroadcast(EditAlarmActivity.this, (int) currentTimeInMilliSeconds, intent, 0);

            }
//            if(alarmIntent==null||alarmIntent.length<1 ){
//                Toast.makeText(this, "Select days", Toast.LENGTH_SHORT).show();
//            }else {
//                for (int i = 0; i < alarmIntent.length; i++) {
//                    alarmMgr.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), alarmIntent[i]); }
//                db.addAlarm(a);
//                Intent i = new Intent(EditAlarmActivity.this, AllActivity.class);
//                startActivity(i);
//            }

            if(alarmIntent==null||alarmIntent.length<1 ){
                Toast.makeText(this, "Select days", Toast.LENGTH_SHORT).show();
            }else {
//                for (int i = 0; i < alarmIntent.length; i++) {
//                    Log.d("sI", "settingIntents "+alarmIntent[i]);
////                    alarmMgr.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), alarmIntent[i]);
//                    alarmMgr.setExact(AlarmManager.RTC_WAKEUP, _alarm, alarmIntent[i]);
//                    }
                    db.addAlarm(a);


                Intent i = new Intent(EditAlarmActivity.this, AllActivity.class);
                startActivity(i);
            }

//        alarmMgr.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
//                SystemClock.elapsedRealtime(), 5 * 1000, alarmIntent);
//        alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, SystemClock.elapsedRealtime(),5*1000,alarmIntent);


        }


    }

}
