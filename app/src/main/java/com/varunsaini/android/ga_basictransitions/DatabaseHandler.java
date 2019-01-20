package com.varunsaini.android.ga_basictransitions;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.util.Log;
import android.view.MenuInflater;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
                KEY_ALARM_PENDING_REQ_CODE + " INTEGER PRIMARY KEY )";
        db.execSQL(CREATE_ALARM_TABLE);
        Log.d("create", "onCreate: "+CREATE_ALARM_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
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
                +","+ KEY_ALARM_STATE +"," + KEY_ALARM_PENDING_REQ_CODE + " FROM " + TABLE_NAME;

//        String selectQuery = "SELECT hour,minute,group_name,alarm_state from alarm";
        ArrayList<AllAlarm> allAlarmArrayList = new ArrayList<>();

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c = db.rawQuery(selectQuery,null);

        int hoursIndex = c.getColumnIndex(KEY_HOUR);
        int minutesIndex = c.getColumnIndex(KEY_MINUTE);
        int groupName = c.getColumnIndex(KEY_GROUP_NAME);
        int alarmState = c.getColumnIndex(KEY_ALARM_STATE);
        int alarmPendingReqCode = c.getColumnIndex(KEY_ALARM_PENDING_REQ_CODE);

        if (c != null && c.moveToFirst()){
            do {
                Log.d("asa", "onCreate: "+c.getInt(hoursIndex)+":"+c.getInt(minutesIndex)+":"+c.getString(groupName)+":"+c.getInt(alarmState));
                String time = c.getInt(hoursIndex)+":"+c.getInt(minutesIndex);
                String groupNamee = c.getString(groupName);
                int alarmStatee = c.getInt(alarmState);
                int alarmPendingReqCodee = c.getInt(alarmPendingReqCode);
                allAlarmArrayList.add(new AllAlarm(time,groupNamee,alarmStatee,alarmPendingReqCodee));
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

    public String getRingtoneUri(int request_id){

        String requestQuery = "SELECT "+ KEY_RINGTONE_NAME + " FROM " +
                TABLE_NAME + " WHERE " + KEY_ALARM_PENDING_REQ_CODE + "="+request_id;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c = db.rawQuery(requestQuery, null);
        Log.d("sas", "getRingtoneUri: "+requestQuery);
        c.moveToFirst();
        String x = c.getString(0);
        return (x);

    }

    public void updateAlarmState(int request_id,int newState){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(KEY_ALARM_STATE,newState); //These Fields should be your String values of actual column names
        db.update(TABLE_NAME, cv, KEY_ALARM_PENDING_REQ_CODE+"="+request_id, null);
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
        String selectQuery = "SELECT DISTINCT " + KEY_GROUP_NAME + " FROM " + TABLE_NAME +
                                " WHERE " + KEY_GROUP_NAME + " NOT NULL " ;
        ArrayList<Group> groupArrayList = new ArrayList<>();

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c = db.rawQuery(selectQuery,null);

//        int hoursIndex = c.getColumnIndex(KEY_HOUR);
//        int minutesIndex = c.getColumnIndex(KEY_MINUTE);
        int groupName = c.getColumnIndex(KEY_GROUP_NAME);


        if (c != null && c.moveToFirst()) {
            do {
                int isGroupOn = 1; //1 means group is toggle on
                String[] s = new String[5];
                Arrays.fill(s,null);
                Log.d("asas", "getGroupActivityGroupList: "+c.getString(groupName));
                String getGroupName = c.getString(groupName);
                String selectQuery1 = "SELECT " + KEY_HOUR + " , " + KEY_MINUTE +
                        " FROM " + TABLE_NAME +
                        " WHERE " + KEY_GROUP_NAME + " LIKE " + "'" + getGroupName + "'";
                Cursor c1 = db.rawQuery(selectQuery1,null);
                int i=0;
                if(c1 != null && c1.moveToFirst()){
                    do{
                        s[i] = c1.getString(0) + ":" + c1.getString(1);
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
                groupArrayList.add(new Group(c.getString(groupName),s[0],s[1],s[2],s[3],1));//1 is used as placeholder
            } while (c.moveToNext());
        }

        return groupArrayList;
    }

    public void deleteAnAlarm(int request_code){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME,KEY_ALARM_PENDING_REQ_CODE + "=" + request_code,null);
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

    public void updataAllAlarmData(int mSelectedHour,int mSelectedMinute,int alarm_state,String days,String labelText,String mRingtoneUri,int vibrate,int alarm_pending_req_code) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(KEY_HOUR,mSelectedHour);
        cv.put(KEY_MINUTE,mSelectedMinute);
        cv.put(KEY_ALARM_STATE,alarm_state);
        cv.put(KEY_DAYS,days);
        cv.put(KEY_ALARM_LABEL,labelText);
        cv.put(KEY_RINGTONE_NAME,mRingtoneUri);
        cv.put(KEY_VIBRATE,vibrate);
        db.update(TABLE_NAME, cv, KEY_ALARM_PENDING_REQ_CODE+"="+alarm_pending_req_code, null);


    }

    public ArrayList<GroupInfo> getAllGroupInfo(String groupName) {

        ArrayList<GroupInfo> groupInfoArrayList = new ArrayList<>();
        String selectQuery = "SELECT " + KEY_HOUR + "," + KEY_MINUTE + "," + KEY_ALARM_STATE + "," + KEY_ALARM_PENDING_REQ_CODE
                + " FROM " + TABLE_NAME +
                " WHERE " + KEY_GROUP_NAME + " = " + "'" + groupName + "'" ;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c = db.rawQuery(selectQuery,null);

        int hoursIndex = c.getColumnIndex(KEY_HOUR);
        int minIndex = c.getColumnIndex(KEY_MINUTE);
        int alarmStateIndex = c.getColumnIndex(KEY_ALARM_STATE);
        int reqCodeIndex = c.getColumnIndex(KEY_ALARM_PENDING_REQ_CODE);

        if (c != null && c.moveToFirst()) {
            do {

                String time = c.getInt(hoursIndex) + ":" + c.getInt(minIndex);int alarmState = c.getInt(alarmStateIndex);
                int requestCode = c.getInt(reqCodeIndex);

                groupInfoArrayList.add(new GroupInfo(time,alarmState,requestCode));

            } while (c.moveToNext());
        }

        return groupInfoArrayList;
    }
}
