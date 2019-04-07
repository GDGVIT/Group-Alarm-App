package com.varunsaini.android.ga_basictransitions.activity;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.rm.rmswitch.RMSwitch;
import com.shawnlin.numberpicker.NumberPicker;
import com.varunsaini.android.ga_basictransitions.Utils;
import com.varunsaini.android.ga_basictransitions.misc.TimeConvertor;
import com.varunsaini.android.ga_basictransitions.models.Alarm;
import com.varunsaini.android.ga_basictransitions.misc.AlarmReciever;
import com.varunsaini.android.ga_basictransitions.models.AllAlarm;
import com.varunsaini.android.ga_basictransitions.misc.DatabaseHandler;
import com.varunsaini.android.ga_basictransitions.misc.NotificationReciever;
import com.varunsaini.android.ga_basictransitions.R;

import java.util.ArrayList;
import java.util.Calendar;

public class EditAlarmActivity extends AppCompatActivity {

    String mRingtoneUri;
    int mVibrate = 0;
    Uri currentRingtoneUri = null;
    TextView alarmTime, groupNameTitle, repeatText, labelText, ringtoneText, vibrateText;
    EditText labelEdittext;
    RMSwitch rmSwitch1;
    CardView buttonMon, buttonTue, buttonWed, buttonThurs, buttonFri, buttonSat, buttonSun, vibrateCard,am_pm_card;
    TextView textMon, textTue, textWed, textThurs, textFri, textSat, textSun;
    int groupColor;
    int groupState;
    String groupName;
    private AlarmManager alarmMgr;
    private PendingIntent[] alarmIntent;
    int mSelectedHour = Calendar.HOUR_OF_DAY;
    int mSelectedMinute = Calendar.MINUTE;
    int alarm_pending_req_code;
    String[] allpreviousAlarmData;
    String nameOfGroup;
    String daystoRing = "";
    String stateMon, stateTue, stateWed, stateThurs, stateFri, stateSat, stateSun;
    String[] stateOfDays;
    String[] days = {"mon", "tue", "wed", "thurs", "fri", "sat", "sun"};
    String[] am_pm = {"AM","PM","AM","PM","AM","PM","AM","PM","AM","PM"};
    NumberPicker min_picker, hour_picker,am_pm_picker;
    TextView titleActionBar;
    ImageView backActionBar;
    NestedScrollView scrollView;
    int alarmPreviousStateInGroup;
    String hourString,minString ;
    NotificationManager nMgr;


    SQLiteDatabase sqLiteDatabase;
    DatabaseHandler db;

    @SuppressLint({"RestrictedApi", "ResourceAsColor"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_alarm);
        alarmTime = findViewById(R.id.alarmTime);
        labelEdittext = findViewById(R.id.labelEdittext);
        groupNameTitle = findViewById(R.id.group_name);
        repeatText = findViewById(R.id.repeatText);
        labelText = findViewById(R.id.labelText);
        ringtoneText = findViewById(R.id.ringtoneText);
        vibrateText = findViewById(R.id.vibrateText);
        vibrateCard = findViewById(R.id.vibrateCard);
        min_picker = findViewById(R.id.min_picker);
        hour_picker = findViewById(R.id.hour_picker);
        am_pm_card = findViewById(R.id.am_pm_card);
        am_pm_picker = findViewById(R.id.am_pm_picker);
        am_pm_picker.setMinValue(1);
        am_pm_picker.setMaxValue(am_pm.length);
        am_pm_picker.setDisplayedValues(am_pm);
        am_pm_picker.setWrapSelectorWheel(true);
        ringtoneText.setSelected(true);

        scrollView = findViewById(R.id.scrollView);
        scrollView.fullScroll(ScrollView.FOCUS_UP);
        scrollView.smoothScrollTo(0, 0);

        Calendar cc = Calendar.getInstance();
//        hourString = String.valueOf(cc.get(Calendar.HOUR_OF_DAY));
//        minString = String.valueOf(cc.get(Calendar.MINUTE));
        if(Utils.getTimeFormat(this)==1){
            hour_picker.setMinValue(1);
            hour_picker.setMaxValue(12);
            hour_picker.setWrapSelectorWheel(true);
            am_pm_card.setVisibility(View.VISIBLE);
        }




        min_picker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                    if (newVal == 0 || newVal == 1 || newVal == 2 || newVal == 3 || newVal == 4 || newVal == 5 || newVal == 6 || newVal == 7 || newVal == 8 || newVal == 9) {
                        minString = "0" + newVal;
                    } else {
                        minString = String.valueOf(newVal);
                    }
                    mSelectedMinute = newVal;
                if (Utils.getTimeFormat(getApplicationContext()) == 1) {
                    alarmTime.setText(hourString + ":" + minString + " " );
                } else {
                    alarmTime.setText(hourString + ":" + minString);
                }
            }
        });

        hour_picker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                if(newVal==0||newVal==1||newVal==2||newVal==3||newVal==4||newVal==5||newVal==6||newVal==7||newVal==8||newVal==9)
                {
                    hourString = "0"+newVal;
                }else{
                    hourString = String.valueOf(newVal);
                }
                mSelectedHour = newVal;
                alarmTime.setText(hourString + ":" + minString);
            }
        });

        am_pm_picker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {

            }
        });
//        am_pm_picker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
//            @Override
//            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
//                Toast.makeText(EditAlarmActivity.this, newVal, Toast.LENGTH_SHORT).show();
//            }
//        });


        Typeface tf = Typeface.createFromAsset(getAssets(), "fonts/Karla.ttf");

        stateMon = "unchecked";
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

        textMon = findViewById(R.id.textMon);
        textTue = findViewById(R.id.textTue);
        textWed = findViewById(R.id.textWed);
        textThurs = findViewById(R.id.textThurs);
        textFri = findViewById(R.id.textFri);
        textSat = findViewById(R.id.textSat);
        textSun = findViewById(R.id.textSun);


        buttonMon.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View v) {
                if (stateMon.equals("checked")) {
                    stateMon = "unchecked";
                    buttonMon.setCardBackgroundColor(Color.WHITE);
                    textMon.setTextColor(Color.BLACK);

                } else {
                    stateMon = "checked";
                    buttonMon.setCardBackgroundColor(Color.rgb(30, 89, 246));
                    textMon.setTextColor(Color.WHITE);
                }
            }
        });

        buttonTue.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View v) {
                if (stateTue.equals("checked")) {
                    stateTue = "unchecked";
                    buttonTue.setCardBackgroundColor(Color.WHITE);
                    textTue.setTextColor(Color.BLACK);
                } else {
                    stateTue = "checked";
                    buttonTue.setCardBackgroundColor(Color.rgb(30, 89, 246));
                    textTue.setTextColor(Color.WHITE);
                }
            }
        });

        buttonWed.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View v) {
                if (stateWed.equals("checked")) {
                    stateWed = "unchecked";
                    buttonWed.setCardBackgroundColor(Color.WHITE);
                    textWed.setTextColor(Color.BLACK);
                } else {
                    stateWed = "checked";
                    buttonWed.setCardBackgroundColor(Color.rgb(30, 89, 246));
                    textWed.setTextColor(Color.WHITE);
                }
            }
        });

        buttonThurs.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View v) {
                if (stateThurs.equals("checked")) {
                    stateThurs = "unchecked";
                    buttonThurs.setCardBackgroundColor(Color.WHITE);
                    textThurs.setTextColor(Color.BLACK);
                } else {
                    stateThurs = "checked";
                    buttonThurs.setCardBackgroundColor(Color.rgb(30, 89, 246));
                    textThurs.setTextColor(Color.WHITE);
                }
            }
        });

        buttonFri.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View v) {
                if (stateFri.equals("checked")) {
                    stateFri = "unchecked";
                    buttonFri.setCardBackgroundColor(Color.WHITE);
                    textFri.setTextColor(Color.BLACK);
                } else {
                    stateFri = "checked";
                    buttonFri.setCardBackgroundColor(Color.rgb(30, 89, 246));
                    textFri.setTextColor(Color.WHITE);
                }
            }
        });


        buttonSat.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View v) {
                if (stateSat.equals("checked")) {
                    stateSat = "unchecked";
                    buttonSat.setCardBackgroundColor(Color.WHITE);
                    textSat.setTextColor(Color.BLACK);
                } else {
                    stateSat = "checked";
                    buttonSat.setCardBackgroundColor(Color.rgb(30, 89, 246));
                    textSat.setTextColor(Color.WHITE);
                }
            }
        });

        buttonSun.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View v) {
                if (stateSun.equals("checked")) {
                    stateSun = "unchecked";
                    buttonSun.setCardBackgroundColor(Color.WHITE);
                    textSun.setTextColor(Color.BLACK);
                } else {
                    stateSun = "checked";
                    buttonSun.setCardBackgroundColor(Color.rgb(30, 89, 246));
                    textSun.setTextColor(Color.WHITE);
                }
            }
        });


        db = new DatabaseHandler(this);
        sqLiteDatabase = this.openOrCreateDatabase("Alarm", MODE_PRIVATE, null);
        db.onCreate(sqLiteDatabase);


        nameOfGroup = getIntent().getStringExtra("nameOfGroup");
        Log.d("asa", "onCreate: Edittext" + nameOfGroup);
        if (nameOfGroup != null) {
            groupNameTitle.setText(nameOfGroup);
            groupName = nameOfGroup;
            groupColor = getIntent().getIntExtra("colorOfGroup", -1);
//            groupState = 1;
            // put groupColor and groupState here
        }

        alarm_pending_req_code = getIntent().getIntExtra("alarm_pending_req_code", -1);
        if (alarm_pending_req_code > 0) {
            allpreviousAlarmData = db.getAllPreviousEditAlarmData(alarm_pending_req_code);
            mSelectedHour = Integer.parseInt(allpreviousAlarmData[0]);
            mSelectedMinute = Integer.parseInt(allpreviousAlarmData[1]);
            if(mSelectedHour==0||mSelectedHour==1||mSelectedHour==2||mSelectedHour==3||mSelectedHour==4||mSelectedHour==5||mSelectedHour==6||mSelectedHour==7||mSelectedHour==8||mSelectedHour==9) {
                hourString = "0"+mSelectedHour;
            }else{
                hourString = String.valueOf(mSelectedHour);
            }

            if(mSelectedMinute==0||mSelectedMinute==1||mSelectedMinute==2||mSelectedMinute==3||mSelectedMinute==4||mSelectedMinute==5||mSelectedMinute==6||mSelectedMinute==7||mSelectedMinute==8||mSelectedMinute==9) {
                minString = "0"+mSelectedMinute;
            }else{
                minString = String.valueOf(mSelectedMinute);
            }
            if(Utils.getTimeFormat(this)==1){
                alarmTime.setText(TimeConvertor.twentyFourToTwelve(mSelectedHour,mSelectedMinute));
            }else {
                alarmTime.setText(hourString + ":" + minString);
            }

            if(allpreviousAlarmData[8]!=null) {
                Uri uri = Uri.parse(allpreviousAlarmData[8]);
                Ringtone ringtone = RingtoneManager.getRingtone(this, uri);
                String title = ringtone.getTitle(this);
                mRingtoneUri = allpreviousAlarmData[8];
                ringtoneText.setText(title);
            }


            if (allpreviousAlarmData[4] != null) {
//                groupNameTitle.setText(allpreviousAlarmData[4]);
            }
            labelEdittext.setText(allpreviousAlarmData[7]);
            min_picker.setValue(Integer.parseInt(allpreviousAlarmData[1]));
            hour_picker.setValue(Integer.parseInt(allpreviousAlarmData[0]));
            if (allpreviousAlarmData[8] == null) {
                currentRingtoneUri = null;
            } else {
                currentRingtoneUri = Uri.parse(allpreviousAlarmData[8]);
            }
            if (allpreviousAlarmData[2].equals("0")) {
//                rmSwitch1.setChecked(false);
            }
            if (!allpreviousAlarmData[3].equals("")) {
                String savedDays = allpreviousAlarmData[3];
                Log.d("aaa", "onCreate: " + savedDays);
                String[] individualDays = savedDays.split("#");
                for (String d : individualDays) {
                    Log.d("aaew", "onCreate: " + d + '\n');
                    if (d.equals("mon")) {
                        stateMon = "checked";
                        buttonMon.setCardBackgroundColor(Color.rgb(30, 89, 246));
                        textMon.setTextColor(Color.WHITE);
                    } else if (d.equals("tue")) {
                        stateTue = "checked";
                        buttonTue.setCardBackgroundColor(Color.rgb(30, 89, 246));
                        textTue.setTextColor(Color.WHITE);
                    } else if (d.equals("wed")) {
                        stateWed = "checked";
                        buttonWed.setCardBackgroundColor(Color.rgb(30, 89, 246));
                        textWed.setTextColor(Color.WHITE);
                    } else if (d.equals("thurs")) {
                        stateThurs = "checked";
                        buttonThurs.setCardBackgroundColor(Color.rgb(30, 89, 246));
                        textThurs.setTextColor(Color.WHITE);
                    } else if (d.equals("fri")) {
                        stateFri = "checked";
                        buttonFri.setCardBackgroundColor(Color.rgb(30, 89, 246));
                        textFri.setTextColor(Color.WHITE);
                    } else if (d.equals("sat")) {
                        stateSat = "checked";
                        buttonSat.setCardBackgroundColor(Color.rgb(30, 89, 246));
                        textSat.setTextColor(Color.WHITE);
                    } else if (d.equals("sun")) {
                        stateSun = "checked";
                        buttonSun.setCardBackgroundColor(Color.rgb(30, 89, 246));
                        textSun.setTextColor(Color.WHITE);
                    }
                }
            }
            if (allpreviousAlarmData[9].equals("1")) {
                vibrateCard.setCardBackgroundColor(Color.rgb(30, 89, 246));
                vibrateText.setTextColor(Color.WHITE);
                mVibrate = 1;
            } else {
                mVibrate = 0;
                //do nothing
            }
        } else {
            mSelectedHour = cc.get(Calendar.HOUR_OF_DAY);
            mSelectedMinute = cc.get(Calendar.MINUTE);
            if(mSelectedHour==0||mSelectedHour==1||mSelectedHour==2||mSelectedHour==3||mSelectedHour==4||mSelectedHour==5||mSelectedHour==6||mSelectedHour==7||mSelectedHour==8||mSelectedHour==9) {
                hourString = "0"+mSelectedHour;
            }else{
                hourString = String.valueOf(mSelectedHour);
            }

            if(mSelectedMinute==0||mSelectedMinute==1||mSelectedMinute==2||mSelectedMinute==3||mSelectedMinute==4||mSelectedMinute==5||mSelectedMinute==6||mSelectedMinute==7||mSelectedMinute==8||mSelectedMinute==9) {
                minString = "0"+mSelectedMinute;
            }else{
                minString = String.valueOf(mSelectedMinute);
            }
            alarmTime.setText(hourString + ":" + minString);
//            alarmTime.setText(mSelectedHour + ":" + mSelectedMinute);
            min_picker.setValue(mSelectedMinute);
            hour_picker.setValue(mSelectedHour);
        }

        vibrateCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mVibrate == 1) {
                    mVibrate = 0;
                    vibrateCard.setCardBackgroundColor(Color.WHITE);
                    vibrateText.setTextColor(Color.BLACK);
                } else {
                    mVibrate = 1;
                    vibrateCard.setCardBackgroundColor(Color.rgb(30, 89, 246));
                    vibrateText.setTextColor(Color.WHITE);
                }
            }
        });

        int belongs_to_group = getIntent().getIntExtra("belongs_to_group", -1);
        if (belongs_to_group == 0 && nameOfGroup == null) {
            Log.d("days", "insided beongToGroup");
            groupName = null;
            groupColor = 0;//0 means does not belong to group
            groupState = -2;

        }


        backActionBar = findViewById(R.id.backActionBar);
        titleActionBar = findViewById(R.id.titleActionBar);

        Typeface tf1 = Typeface.createFromAsset(getAssets(), "fonts/Karla-Bold.ttf");
        Typeface tf2 = Typeface.createFromAsset(getAssets(), "fonts/ostrich-regular.ttf");
        alarmTime.setTypeface(tf1);
        repeatText.setTypeface(tf1);
        labelText.setTypeface(tf1);
        groupNameTitle.setTypeface(tf);
        ringtoneText.setTypeface(tf);
        vibrateText.setTypeface(tf);
        textMon.setTypeface(tf);
        textTue.setTypeface(tf);
        textWed.setTypeface(tf);
        textThurs.setTypeface(tf);
        textFri.setTypeface(tf);
        textSat.setTypeface(tf);
        textSun.setTypeface(tf);
        titleActionBar.setTypeface(tf1);
        min_picker.setTypeface(tf2);
        hour_picker.setTypeface(tf2);
        am_pm_picker.setTypeface(tf2);
        titleActionBar.setText("Set your Alarm");

        backActionBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditAlarmActivity.super.onBackPressed();
            }
        });


    }


    public void ringtoneSelect(View view) {

        //////////////////////////////////////////
        Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "Select ringtone for alarm:");
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_SILENT, false);
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_DEFAULT, true);
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_NOTIFICATION);
        EditAlarmActivity.this.startActivityForResult(intent, 1);
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

        String labelText;
        if (labelEdittext.getText().toString().equals("")) {
            labelText = null;
        } else {
            labelText = labelEdittext.getText().toString();
        }

        ArrayList<AllAlarm> allAlarmArrayList = db.getAllActivtiyAlarmList();
        boolean alarmAlreadyExists = false;
        int alarm_state = 0;
        if (alarm_pending_req_code > 0) {


            if (db.getGroupStateByGroupName(groupName) != 0) {
                alarm_state = 1;
            }
            db.updataAllAlarmData(mSelectedHour, mSelectedMinute, alarm_state, daystoRing, labelText, mRingtoneUri, mVibrate, alarm_pending_req_code, -1);
            db.changeGroupName(allpreviousAlarmData[4],groupName);
            db.changeGroupColor(groupName,groupColor);
            alarmMgr = (AlarmManager) EditAlarmActivity.this.getSystemService(Context.ALARM_SERVICE);


            Log.d("days", "setAlarmOn: " + daystoRing);
            Log.d("ggsbgn", "setAlarmOn: " + db.getGroupStateByGroupName(groupName));
            Log.d("gs", "setAlarmOn: " + groupState);

            if ((!daystoRing.equals("") && db.getGroupStateByGroupName(groupName) != 0) || (!daystoRing.equals("") && groupState == -2)) {
                String[] dayys = daystoRing.split("#");
                alarmIntent = new PendingIntent[dayys.length];
                int[] allRequests = new int[dayys.length];
                int k = 0;
                Intent intent = new Intent(EditAlarmActivity.this, AlarmReciever.class);

                alarmMgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                ArrayList<Integer> integerArrayList = db.getThisAlarmIntents(Integer.valueOf(String.valueOf(alarm_pending_req_code).substring(3)));
                for (Integer i : integerArrayList) {
                    PendingIntent alarmIntent = PendingIntent.getBroadcast(this, Integer.valueOf(i), intent, PendingIntent.FLAG_UPDATE_CURRENT);
                    alarmMgr.cancel(alarmIntent);
                    nMgr = (NotificationManager)getApplicationContext().getSystemService(NOTIFICATION_SERVICE);
                    nMgr.cancel(Integer.valueOf(i));
                }

                String x = String.valueOf(alarm_pending_req_code);

                for (String d : dayys) {
                    if (d.equals("mon")) {
                        int y = Integer.valueOf("111" + x.substring(3));
                        settingAlarmOnDays(y, 2, k);
                        allRequests[k] = y;
                        k++;
                    } else if (d.equals("tue")) {
                        int y = Integer.valueOf("222" + x.substring(3));
                        settingAlarmOnDays(y, 3, k);
                        allRequests[k] = y;
                        k++;
                    } else if (d.equals("wed")) {
                        int y = Integer.valueOf("333" + x.substring(3));
                        settingAlarmOnDays(y, 4, k);
                        allRequests[k] = y;
                        k++;
                    } else if (d.equals("thurs")) {
                        int y = Integer.valueOf("444" + x.substring(3));
                        settingAlarmOnDays(y, 5, k);
                        allRequests[k] = y;
                        k++;
                    } else if (d.equals("fri")) {
                        int y = Integer.valueOf("555" + x.substring(3));
                        settingAlarmOnDays(y, 6, k);
                        allRequests[k] = y;
                        k++;
                    } else if (d.equals("sat")) {
                        int y = Integer.valueOf("666" + x.substring(3));
                        settingAlarmOnDays(y, 7, k);
                        allRequests[k] = y;
                        k++;
                    } else if (d.equals("sun")) {
                        int y = Integer.valueOf("777" + x.substring(3));
                        settingAlarmOnDays(y, 1, k);
                        allRequests[k] = y;
                        k++;
                    }
                }


                db.removeDaysPendingReq(Integer.valueOf(String.valueOf(alarm_pending_req_code).substring(3)));
                for (int i = 0; i < dayys.length; i++) {
                    db.addDaysPendingReq(allRequests[i]);
                }


            }
            if (daystoRing.equals("")) {
                Toast.makeText(this, "Select days", Toast.LENGTH_SHORT).show();
            } else {
                Intent i = new Intent(EditAlarmActivity.this, AllActivity.class);
                startActivity(i);
            }

        } else {

            int currentTimeInMilliSeconds = (int) Calendar.getInstance().getTimeInMillis();
            if (currentTimeInMilliSeconds < 0) {
                currentTimeInMilliSeconds = -1 * currentTimeInMilliSeconds;
            }
            if (db.getGroupStateByGroupName(groupName) != 0) {
                alarm_state = 1;
            }

            Log.d("CMS", "setAlarmOn: " + currentTimeInMilliSeconds);

            alarmMgr = (AlarmManager) EditAlarmActivity.this.getSystemService(Context.ALARM_SERVICE);
            Intent intent = new Intent(EditAlarmActivity.this, AlarmReciever.class);
            String x = String.valueOf(currentTimeInMilliSeconds);
            Log.d("sasa", "setAlarmOn: sa" + x);


            Log.d("days", "setAlarmOn: " + daystoRing);
            Log.d("ggsbgn", "setAlarmOn: " + db.getGroupStateByGroupName(groupName));
            Log.d("gs", "setAlarmOn: " + groupState);

            if ((!daystoRing.equals("") && db.getGroupStateByGroupName(groupName) != 0) || (!daystoRing.equals("") && groupState == -2)) {
                Log.d("11mm", "setAlarmOn: entered in first if");
                if ((db.getGroupStateByGroupName(groupName) != 0) || (groupState == -2)) {
                    Log.d("11mm", "setAlarmOn: entered in second if");
                    String[] dayys = daystoRing.split("#");
                    alarmIntent = new PendingIntent[dayys.length];
                    int k = 0;
                    for (String d : dayys) {
                        if (d.equals("mon")) {
                            int y = Integer.parseInt("111" + x.substring(3));
                            db.addDaysPendingReq(y);
                            settingAlarmOnDays(y, 2, k);
                            k++;
                        } else if (d.equals("tue")) {
                            int y = Integer.parseInt("222" + x.substring(3));
                            db.addDaysPendingReq(y);
                            settingAlarmOnDays(y, 3, k);
                            k++;
                        } else if (d.equals("wed")) {
                            int y = Integer.parseInt("333" + x.substring(3));
                            db.addDaysPendingReq(y);
                            settingAlarmOnDays(y, 4, k);
                            k++;
                        } else if (d.equals("thurs")) {
                            int y = Integer.parseInt("444" + x.substring(3));
                            db.addDaysPendingReq(y);
                            settingAlarmOnDays(y, 5, k);
                            k++;
                        } else if (d.equals("fri")) {
                            int y = Integer.parseInt("555" + x.substring(3));
                            db.addDaysPendingReq(y);
                            settingAlarmOnDays(y, 6, k);
                            k++;
                        } else if (d.equals("sat")) {
                            int y = Integer.parseInt("666" + x.substring(3));
                            db.addDaysPendingReq(y);
                            settingAlarmOnDays(y, 7, k);
                            k++;
                        } else if (d.equals("sun")) {
                            int y = Integer.parseInt("777" + x.substring(3));
                            db.addDaysPendingReq(y);
                            settingAlarmOnDays(y, 1, k);
                            k++;
                        }
                    }

                }
            }
            if (daystoRing.equals("")) {
                Toast.makeText(this, "Select days", Toast.LENGTH_SHORT).show();
            } else {
                groupState = db.getGroupStateByGroupName(groupName);
                if (groupState == -1) {
                    groupState = 1;
                }

                db.changeGroupName(getIntent().getStringExtra("previousGroupName"),groupName);
                Alarm a = new Alarm(mSelectedHour, mSelectedMinute, alarm_state, daystoRing, groupName, groupColor, groupState, labelText, mRingtoneUri, mVibrate, currentTimeInMilliSeconds, -1);
                db.addAlarm(a);
                Intent i = new Intent(EditAlarmActivity.this, AllActivity.class);
                startActivity(i);
            }


        }

    }


    void settingAlarmOnDays(int y, int dayOfWeek, int k) {

        Intent intent = new Intent(EditAlarmActivity.this, AlarmReciever.class);
        Calendar now = Calendar.getInstance();
        long _alarm = 0;

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, mSelectedHour);
        calendar.set(Calendar.MINUTE, mSelectedMinute);
        calendar.set(Calendar.SECOND, 0);
        Log.d("timmmmm", "now tim: " + now.getTimeInMillis());
        calendar.set(Calendar.DAY_OF_WEEK, dayOfWeek);
        intent.putExtra("request_code", y);
        if (calendar.getTimeInMillis() <= now.getTimeInMillis()) {
            _alarm = calendar.getTimeInMillis() + (24 * 60 * 60 * 1000 * 7);
            Log.d("timmmmm", "set tim: " + _alarm);
        } else {
            _alarm = calendar.getTimeInMillis();
            Log.d("timmmmm", "else tim: " + _alarm);
        }
        alarmIntent[k] = PendingIntent.getBroadcast(EditAlarmActivity.this, y, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmMgr.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, _alarm, alarmIntent[k]);
        }else{
            alarmMgr.setExact(AlarmManager.RTC_WAKEUP, _alarm, alarmIntent[k]);
        }

        Intent notifyIntent = new Intent(this, NotificationReciever.class);
        notifyIntent.putExtra("trimmedRequestId",y);
        PendingIntent pendingIntent = PendingIntent.getBroadcast
                (this, y, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,  _alarm - (Utils.getNotificationDuration(getApplicationContext())), pendingIntent);
        }else{
            alarmManager.setExact(AlarmManager.RTC_WAKEUP,  _alarm - (Utils.getNotificationDuration(getApplicationContext())), pendingIntent);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case 1:
                    Ringtone ringtone = RingtoneManager.getRingtone(this, (Uri) data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI));
                    String title = ringtone.getTitle(this);
                    ringtoneText.setText(title);
                    mRingtoneUri = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI).toString();
                    Log.d("hhhj", "onActivityResult: "+mRingtoneUri);
                    break;
                default:
                    break;
            }
        }
    }
}