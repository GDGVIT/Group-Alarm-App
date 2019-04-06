package com.varunsaini.android.ga_basictransitions;

import android.content.Context;
import android.content.SharedPreferences;

public class Utils {

    public static final int NOTIFICATION_TIME = 1000*60*60;
    public static final int AUTO_SILENCE = 57*1000;
    public static final int ONE_MINUTE = 60*1000;

    public static int getSnoozeTime(Context context){
        SharedPreferences pref = context.getSharedPreferences("MyPref", 0);
        int duration = pref.getInt("snooze_duration",0);
        if(duration<0){ duration=0;}
        switch (duration){
            case 0:
                return ONE_MINUTE;
            case 1:
                return 3*ONE_MINUTE;
            case 2:
                return 5*ONE_MINUTE;
            case 3:
                return 10*ONE_MINUTE;
            case 4:
                return 15*ONE_MINUTE;
            case 5:
                return 30*ONE_MINUTE;
            case 6:
                return 60*ONE_MINUTE;
            default:
                return 0 ;
        }
    }

    public static int getAutoSilence(Context context){
        SharedPreferences pref = context.getSharedPreferences("MyPref", 0);
        int duration = pref.getInt("auto_silence",0);
        if(duration<0){ duration=0;}
        switch (duration){
            case 0:
                return ONE_MINUTE;
            case 1:
                return 3*ONE_MINUTE;
            case 2:
                return 5*ONE_MINUTE;
            case 3:
                return 10*ONE_MINUTE;
            case 4:
                return 15*ONE_MINUTE;
            case 5:
                return 30*ONE_MINUTE;
            case 6:
                return 60*ONE_MINUTE;
            default:
                return 0 ;
        }
    }

    public static int getNotificationDuration(Context context){
        SharedPreferences pref = context.getSharedPreferences("MyPref", 0);
        int duration = pref.getInt("notification_duration",0);
        if(duration<0){ duration=0;}
        switch (duration){
            case 0:
                return 15*ONE_MINUTE;
            case 1:
                return 30*ONE_MINUTE;
            case 2:
                return 45*ONE_MINUTE;
            case 3:
                return 60*ONE_MINUTE;
            case 4:
                return 120*ONE_MINUTE;
            default:
                return 0 ;
        }
    }

    public static int getAlarmType(Context context){
        SharedPreferences pref = context.getSharedPreferences("MyPref", 0);
        int duration = pref.getInt("alarm_type",0);
        if(duration<0){ duration=0;}
        switch (duration){
            case 0:
                return 0;
            case 1:
                return 1;
            default:
                return 0 ;
        }
    }

    public static String getDefaultAlarmSound(Context context){
        SharedPreferences pref = context.getSharedPreferences("MyPref", 0);
        String mRingtoneUri = pref.getString("default_sound",null);
        return mRingtoneUri;
    }

    public static boolean getAscendingVolume(Context context){
        SharedPreferences pref = context.getSharedPreferences("MyPref", 0);
        boolean isEnabled = pref.getBoolean("ascending_volume",true);
        return isEnabled;
    }

}
