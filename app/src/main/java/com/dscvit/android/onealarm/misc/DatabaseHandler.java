package com.dscvit.android.onealarm.misc;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.dscvit.android.onealarm.models.Alarm;
import com.dscvit.android.onealarm.models.AllAlarm;
import com.dscvit.android.onealarm.models.Group;
import com.dscvit.android.onealarm.models.GroupInfo;

import java.util.ArrayList;
import java.util.Arrays;

public class DatabaseHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "Alarm";
    private static final String TABLE_NAME = "alarm";
    private static final String KEY_HOUR = "hour";
    private static final String KEY_MINUTE = "minute";
    private static final String KEY_ALARM_STATE = "alarm_state";
    private static final String KEY_DAYS = "days";
    private static final String KEY_GROUP_NAME = "group_name";
    private static final String KEY_GROUP_COLOR = "group_color";
    private static final String KEY_GROUP_STATE = "group_state";
    private static final String KEY_ALARM_LABEL = "alarm_label";
    private static final String KEY_RINGTONE_NAME = "ringtone_name";
    private static final String KEY_VIBRATE = "vibrate";
    private static final String KEY_ALARM_PENDING_REQ_CODE = "alarm_pending_req_code";
    private static final String DAYS_TABLE_NAME = "days_alarm";
    private static final String KEY_DAYS_REQUEST_CODE = "days_req_code";
    private static final String KEY_PREVIOUS_STATE = "previous_alarm_state";


    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        //3rd argument to be passed is CursorFactory instance
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_ALARM_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
                KEY_HOUR + " INTEGER,"+
                KEY_MINUTE + " INTEGER,"+
                KEY_ALARM_STATE + " INTEGER," +
                KEY_DAYS +" TEXT,"+
                KEY_GROUP_NAME + " TEXT,"+
                KEY_GROUP_COLOR + " INTEGER,"+
                KEY_GROUP_STATE + " INTEGER,"+
                KEY_ALARM_LABEL + " TEXT,"+
                KEY_RINGTONE_NAME + " TEXT,"+
                KEY_VIBRATE + " INTEGER,"+
                KEY_ALARM_PENDING_REQ_CODE + " INTEGER PRIMARY KEY  ,"
                +KEY_PREVIOUS_STATE + " INTEGER )";
        db.execSQL(CREATE_ALARM_TABLE);
        Log.d("create", "onCreate: "+CREATE_ALARM_TABLE);

        String CREATE_DAYS_REQ_TABLE = "CREATE TABLE IF NOT EXISTS "+ DAYS_TABLE_NAME + " (" +
                KEY_DAYS_REQUEST_CODE + " INTEGER PRIMARY KEY )";
        db.execSQL(CREATE_DAYS_REQ_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + DAYS_TABLE_NAME);
        onCreate(db);
        Log.d("upgrade", "onUpgrade: passed onupgrade");
    }

    //code to add new alarm
    public void addAlarm(Alarm alarm){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_HOUR,alarm.hour);
        values.put(KEY_MINUTE,alarm.minutes);
        values.put(KEY_ALARM_STATE,alarm.alarm_state);
        values.put(KEY_DAYS,alarm.days);
        values.put(KEY_GROUP_NAME,alarm.group_name);
        values.put(KEY_GROUP_COLOR,alarm.group_color);
        values.put(KEY_GROUP_STATE,alarm.group_state);
        values.put(KEY_ALARM_LABEL,alarm.alarm_label);
        values.put(KEY_RINGTONE_NAME,alarm.ringtone_name);
        values.put(KEY_VIBRATE,alarm.vibrate);
        values.put(KEY_ALARM_PENDING_REQ_CODE,alarm.alarm_pending_req_code);

        db.insert(TABLE_NAME,null,values);
        db.close();
    }

    public ArrayList<AllAlarm> getAllActivtiyAlarmList(){
        String selectQuery = "SELECT " + KEY_HOUR +","+ KEY_MINUTE +","+ KEY_GROUP_NAME
                +","+ KEY_ALARM_STATE +"," + KEY_ALARM_PENDING_REQ_CODE + "," + KEY_GROUP_COLOR + "," + KEY_DAYS + " FROM " + TABLE_NAME + " ORDER BY " + KEY_HOUR +","+ KEY_MINUTE ;

//        String selectQuery = "SELECT hour,minute,group_name,alarm_state from alarm";
        ArrayList<AllAlarm> allAlarmArrayList = new ArrayList<>();

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c = db.rawQuery(selectQuery,null);

        int hoursIndex = c.getColumnIndex(KEY_HOUR);
        int minutesIndex = c.getColumnIndex(KEY_MINUTE);
        int groupName = c.getColumnIndex(KEY_GROUP_NAME);
        int alarmState = c.getColumnIndex(KEY_ALARM_STATE);
        int alarmPendingReqCode = c.getColumnIndex(KEY_ALARM_PENDING_REQ_CODE);
        int days = c.getColumnIndex(KEY_DAYS);

        if (c != null && c.moveToFirst()){
            do {
                Log.d("asa", "onCreate: "+c.getInt(hoursIndex)+":"+c.getInt(minutesIndex)+":"+c.getString(groupName)+":"+c.getInt(alarmState));
                int hour = c.getInt(hoursIndex);
                int min = c.getInt(minutesIndex);
                String hourString;
                String minString;
                if(hour==0||hour==1||hour==2||hour==3||hour==4||hour==5||hour==6||hour==7||hour==8||hour==9){
                    hourString = "0" + hour;
                }else{
                    hourString = String.valueOf(hour);
                }

                if(min==0||min==1||min==2||min==3||min==4||min==5||min==6||min==7||min==8||min==9){
                    minString = "0" + min;
                }else{
                    minString = String.valueOf(min);
                }

                String time = hourString + ":" + minString;
                String groupNamee = c.getString(groupName);
                int alarmStatee = c.getInt(alarmState);
                int alarmPendingReqCodee = c.getInt(alarmPendingReqCode);
                String dayss = c.getString(days);
                allAlarmArrayList.add(new AllAlarm(time,groupNamee,alarmStatee,alarmPendingReqCodee,false,dayss));
            } while (c.moveToNext());
        }
        return allAlarmArrayList;
    }

    public void getAllDatabaseDataInLogcat() {
        String selectQuery = "SELECT * FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        if (c != null && c.moveToFirst()) {
            do {
                String data = "";
                for (int i = 0; i < 11; i++) {
                    data = data + c.getString(i) + "||";
                }
                Log.d("asa", data);
            } while (c.moveToNext());
        }
    }

    public String[] getRingtoneUriVibrate(int request_id){

        String[] array = new String[2];

        Integer idd = Integer.parseInt(String.valueOf(request_id).substring(3));
        Log.d("jk", "getRingtoneUri: "+idd);

        String requestQuery = "SELECT "+ KEY_RINGTONE_NAME + " , " + KEY_VIBRATE + " FROM " +
                TABLE_NAME + " WHERE " + KEY_ALARM_PENDING_REQ_CODE + " LIKE '%"+idd + "%'";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c = db.rawQuery(requestQuery, null);
        Log.d("sas", "getRingtoneUri: "+requestQuery);
        c.moveToFirst();
        String ringtone,vibrate;
        if (c.getString(0)==null){
            ringtone = null;
        }else{
            ringtone = c.getString(0);
        }

        if(c.getString(1)==null){
            vibrate = null;
        }else{
            vibrate = c.getString(1);
        }
        array[0] = ringtone;
        array[1] = vibrate;
        return array;
    }

    public void updateAlarmState(int request_id,int newState){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(KEY_ALARM_STATE,newState); //These Fields should be your String values of actual column names
        db.update(TABLE_NAME, cv, KEY_ALARM_PENDING_REQ_CODE+"="+request_id, null);
    }


    public void updateGroupState(String groupName,int newState) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(KEY_GROUP_STATE,newState); //These Fields should be your String values of actual column names
        db.update(TABLE_NAME, cv, KEY_GROUP_NAME+" = '"+ groupName + "'" , null);
    }

    public String[] getAllPreviousEditAlarmData(int request_id){
        String requestQuery = "SELECT * "  + " FROM " +
                TABLE_NAME + " WHERE " + KEY_ALARM_PENDING_REQ_CODE + "="+request_id;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c = db.rawQuery(requestQuery, null);
        Log.d("sas", "getRingtoneUri: "+requestQuery);
        c.moveToFirst();
        String[] allDataAboutParticularAlarm = new String[11];
        for(int i=0;i<11;i++){
            allDataAboutParticularAlarm[i] = c.getString(i);
        }
        return allDataAboutParticularAlarm;


    }

    public ArrayList<Group> getGroupActivityGroupList(){
        String selectQuery = "SELECT DISTINCT " + KEY_GROUP_NAME + " , " + KEY_GROUP_COLOR + ","  + KEY_GROUP_STATE +   " FROM " + TABLE_NAME +
                                " WHERE " + KEY_GROUP_NAME + " NOT NULL " +" ORDER BY " + KEY_GROUP_NAME ;
        ArrayList<Group> groupArrayList = new ArrayList<>();

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c = db.rawQuery(selectQuery,null);

//        int hoursIndex = c.getColumnIndex(KEY_HOUR);
//        int minutesIndex = c.getColumnIndex(KEY_MINUTE);
        int groupName = c.getColumnIndex(KEY_GROUP_NAME);
        int groupColor = c.getColumnIndex(KEY_GROUP_COLOR);
        int groupState = c.getColumnIndex(KEY_GROUP_STATE);


        if (c != null && c.moveToFirst()) {
            do {
                int isGroupOn = 1; //1 means group is toggle on
                String[] s = new String[5];
                Arrays.fill(s,null);
                Log.d("asas", "getGroupActivityGroupList: "+c.getString(groupName));
                String getGroupName = c.getString(groupName);
                String selectQuery1 = "SELECT " + KEY_HOUR + " , " + KEY_MINUTE +
                        " FROM " + TABLE_NAME +
                        " WHERE " + KEY_GROUP_NAME + " LIKE " + "'" + getGroupName + "'"  +  " ORDER BY " + KEY_HOUR + " , " + KEY_MINUTE  ;
                Cursor c1 = db.rawQuery(selectQuery1,null);
                int i=0;
                if(c1 != null && c1.moveToFirst()){
                    do{
                        int hour = c1.getInt(0);
                        int min = c1.getInt(1);
                        String hourString;
                        String minString;
                        if(hour==0||hour==1||hour==2||hour==3||hour==4||hour==5||hour==6||hour==7||hour==8||hour==9){
                            hourString = "0" + hour;
                        }else{
                            hourString = String.valueOf(hour);
                        }

                        if(min==0||min==1||min==2||min==3||min==4||min==5||min==6||min==7||min==8||min==9){
                            minString = "0" + min;
                        }else{
                            minString = String.valueOf(min);
                        }

                        s[i] = hourString + ":" + minString;
                        i++;
                        if(i==3){
                            int j=-1;
                            if(c1!=null){
                                do{
                                    j=j+1;

                                }while(c1.moveToNext());
                            }
                            if(j==0){
                                break;
                            }else{
                            s[i] = j + "more";
                            break;}
                        }

                    }while (c1.moveToNext());
                }
                groupArrayList.add(new Group(c.getString(groupName),s[0],s[1],s[2],s[3],c.getInt(groupState),c.getInt(groupColor),false));//1 is used as placeholder
            } while (c.moveToNext());
        }

        return groupArrayList;
    }

    public void deleteAnAlarm(int request_code){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME,KEY_ALARM_PENDING_REQ_CODE + "=" + request_code,null);
    }

    public void deleteAGroup(String groupName){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME,KEY_GROUP_NAME + "= '" + groupName + "'",null);
    }

    public ArrayList<String> getAllGroupNames(){
        String selectQuery = "SELECT DISTINCT " + KEY_GROUP_NAME + " FROM " + TABLE_NAME +
                " WHERE " + KEY_GROUP_NAME + " NOT NULL " ;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);
        ArrayList<String> allGroupName = new ArrayList<>();
        if (c != null && c.moveToFirst()) {
            do {
                allGroupName.add(c.getString(0));
            } while (c.moveToNext());
        }

        return allGroupName;
    }

    public void changeGroupName(String previousGroupName,String newGroupName){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(KEY_GROUP_NAME,newGroupName);
        db.update(TABLE_NAME, cv, KEY_GROUP_NAME + "= '" + previousGroupName + "'", null);
    }

    public void changeGroupColor(String groupName,int groupColor){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(KEY_GROUP_COLOR,groupColor);
        db.update(TABLE_NAME, cv, KEY_GROUP_NAME + "= '" + groupName + "'", null);
    }

    public void changePreviousAlarmState(int request_code,int whatToPutInPreviousState){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(KEY_PREVIOUS_STATE,whatToPutInPreviousState);
        db.update(TABLE_NAME,cv,KEY_ALARM_PENDING_REQ_CODE  +  " = " + request_code,null);
    }

    public void updataAllAlarmData(int mSelectedHour,int mSelectedMinute,int alarm_state,String days,String labelText,String mRingtoneUri,int vibrate,int alarm_pending_req_code,int previous_alarm_state_in_group) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(KEY_HOUR,mSelectedHour);
        cv.put(KEY_MINUTE,mSelectedMinute);
        cv.put(KEY_ALARM_STATE,alarm_state);
        cv.put(KEY_DAYS,days);
        cv.put(KEY_ALARM_LABEL,labelText);
        cv.put(KEY_RINGTONE_NAME,mRingtoneUri);
        cv.put(KEY_VIBRATE,vibrate);
        cv.put(KEY_PREVIOUS_STATE,previous_alarm_state_in_group);
        db.update(TABLE_NAME, cv, KEY_ALARM_PENDING_REQ_CODE+"="+alarm_pending_req_code, null);


    }

    public ArrayList<GroupInfo> getAllGroupInfo(String groupName) {

        ArrayList<GroupInfo> groupInfoArrayList = new ArrayList<>();
        String selectQuery = "SELECT " + KEY_HOUR + "," + KEY_MINUTE + "," + KEY_ALARM_STATE + "," + KEY_ALARM_PENDING_REQ_CODE + "," + KEY_PREVIOUS_STATE
                + " FROM " + TABLE_NAME +
                " WHERE " + KEY_GROUP_NAME + " = " + "'" + groupName + "'" + " ORDER BY " +  KEY_HOUR + "," + KEY_MINUTE ;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c = db.rawQuery(selectQuery,null);

        int hoursIndex = c.getColumnIndex(KEY_HOUR);
        int minIndex = c.getColumnIndex(KEY_MINUTE);
        int alarmStateIndex = c.getColumnIndex(KEY_ALARM_STATE);
        int reqCodeIndex = c.getColumnIndex(KEY_ALARM_PENDING_REQ_CODE);
        int previousState = c.getColumnIndex(KEY_PREVIOUS_STATE);

        if (c != null && c.moveToFirst()) {
            do {
                int hour = c.getInt(hoursIndex);
                int min = c.getInt(minIndex);
                String hourString;
                String minString;
                if(hour==0||hour==1||hour==2||hour==3||hour==4||hour==5||hour==6||hour==7||hour==8||hour==9){
                    hourString = "0" + hour;
                }else{
                    hourString = String.valueOf(hour);
                }

                if(min==0||min==1||min==2||min==3||min==4||min==5||min==6||min==7||min==8||min==9){
                    minString = "0" + min;
                }else{
                    minString = String.valueOf(min);
                }

                String time = hourString + ":" + minString;
                int alarmState = c.getInt(alarmStateIndex);
                int requestCode = c.getInt(reqCodeIndex);

                groupInfoArrayList.add(new GroupInfo(time,alarmState,requestCode,c.getInt(previousState),false));

            } while (c.moveToNext());
        }

        return groupInfoArrayList;
    }

    public int[] getHoursMin(int request_code){
        int[] hourMin = new int[2];
        String requestQuery = "SELECT " + KEY_HOUR + " , " + KEY_MINUTE + " FROM " +
                TABLE_NAME + " WHERE " + KEY_ALARM_PENDING_REQ_CODE + "="+request_code;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c = db.rawQuery(requestQuery,null);
        c.moveToFirst();
        hourMin[0] = c.getInt(0);
        hourMin[1] = c.getInt(1);

        return hourMin;
    }


    public String getDaysToRing(int request_code) {
        String requestQuery = "SELECT " + KEY_DAYS + " FROM " +
                TABLE_NAME + " WHERE " + KEY_ALARM_PENDING_REQ_CODE + "="+request_code;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c = db.rawQuery(requestQuery,null);
        c.moveToFirst();
        return(c.getString(0));
    }

    public int getGroupColor(String groupName) {

        String selectQuery = "SELECT " + KEY_GROUP_COLOR
                + " FROM " + TABLE_NAME +
                " WHERE " + KEY_GROUP_NAME + " = " + "'" + groupName + "'" ;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c = db.rawQuery(selectQuery,null);
        c.moveToFirst();
        return (c.getInt(0));

    }


    public int getGroupStateByGroupName(String groupName) {

        String selectQuery = "SELECT " + KEY_GROUP_STATE
                + " FROM " + TABLE_NAME +
                " WHERE " + KEY_GROUP_NAME + " = " + "'" + groupName + "'" ;

        SQLiteDatabase db = this.getWritableDatabase();
        try {
            Cursor c = db.rawQuery(selectQuery, null);
            c.moveToFirst();
            return (c.getInt(0));
        }catch (Exception e){
            return -1;       }

    }

    public String getAlarmTimeFromAlarmRequestId(int trimmed_request_id){
        String requestQuery = "SELECT " + KEY_HOUR+ "," + KEY_MINUTE + " FROM " +
                TABLE_NAME + " WHERE " + KEY_ALARM_PENDING_REQ_CODE + " LIKE '%" + trimmed_request_id + "%'";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c = db.rawQuery(requestQuery,null);
        c.moveToFirst();
        return  c.getInt(0)+ ":" + c.getInt(1);

    }

    public String getGroupNameByRequestId(int request_id){
        String requestQuery = "SELECT " + KEY_GROUP_NAME + " FROM " +
                TABLE_NAME + " WHERE " + KEY_ALARM_PENDING_REQ_CODE + " = " +request_id;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c = db.rawQuery(requestQuery,null);
        c.moveToFirst();
        return c.getString(0);
    }

    public int getGroupColorByTrimmedRequestId(int trimmed_request_id){
        String requestQuery = "SELECT " + KEY_GROUP_COLOR + " FROM " +
                TABLE_NAME + " WHERE " + KEY_ALARM_PENDING_REQ_CODE + " LIKE '%" + trimmed_request_id + "%'";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c = db.rawQuery(requestQuery,null);
        c.moveToFirst();
        return  c.getInt(0);
    }

    /////////////////////////////////////////////////////////////////////////
    public void addDaysPendingReq(int request_code){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_DAYS_REQUEST_CODE,request_code);
        db.insert(DAYS_TABLE_NAME,null,values);
        db.close();
    }

    public void removeDaysPendingReq(int request_code){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(DAYS_TABLE_NAME,KEY_DAYS_REQUEST_CODE+ " like " + "'%" + request_code + "%'",null);
    }

    public ArrayList<Integer> getAllDaysIntents(){

        ArrayList<Integer> array = new ArrayList<>();
        String selectQuery = "SELECT *" + " FROM " + DAYS_TABLE_NAME;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c = db.rawQuery(selectQuery,null);
        if(c!=null && c.moveToFirst()){
            do{
                array.add(c.getInt(0));

            }while (c.moveToNext());
        }

        return array;
    }

    public ArrayList<Integer> getThisAlarmIntents(int request_code){

        ArrayList<Integer> array = new ArrayList<>();
        String selectQuery = "SELECT * " + " FROM " + DAYS_TABLE_NAME + " WHERE "+ KEY_DAYS_REQUEST_CODE+ " LIKE " + "'%" + request_code + "%'";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c = db.rawQuery(selectQuery,null);
        if(c!=null && c.moveToFirst()){
            do{
                array.add(c.getInt(0));

            }while (c.moveToNext());
        }

        return array;
    }

    public void getAllDaysReqCodesInLogcat(){

        String selectQuery = "SELECT * " + " FROM " + DAYS_TABLE_NAME;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c = db.rawQuery(selectQuery,null);
        if(c!=null && c.moveToFirst()){
            do{
                Log.d("alldaysalarm", "getAllDaysReqCodes: " + (c.getInt(0) + "\n"));

            }while (c.moveToNext());
        }
    }


}
