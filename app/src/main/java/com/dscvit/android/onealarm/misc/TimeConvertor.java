package com.dscvit.android.onealarm.misc;

public class TimeConvertor {

    public static String twentyFourToTwelve(int hour,int min){
        String am_pm = "AM";
        String strHour = String.valueOf(hour);
        String strMin = String.valueOf(min);
        if(hour>12){
            strHour = String.valueOf(hour-12);
            am_pm = "PM";
        }
        if(min<10){
             strMin = "0" + min;
        }
        return strHour+":"+ strMin + am_pm;
    }

}
