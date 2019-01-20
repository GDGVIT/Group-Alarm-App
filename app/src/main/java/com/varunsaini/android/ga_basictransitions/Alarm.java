package com.varunsaini.android.ga_basictransitions;

public class Alarm {

    public int hour;
    public int minutes;
    public int alarm_state;
    public String days;
    public String group_name;
    public int group_color;
    public int group_state;
    public String alarm_label;
    public String ringtone_name;
    public int vibrate;
    public int alarm_pending_req_code;

    public Alarm(int hour,int minutes,int alarm_state,
                 String days,String group_name,int group_color,
                 int group_state,String alarm_label,String ringtone_name,
                 int vibrate,int alarm_pending_req_code)
    {
        this.hour = hour;
        this.minutes =  minutes;
        this.alarm_state = alarm_state;
        this.days = days;
        this.group_name = group_name;
        this.group_color = group_color;
        this.group_state = group_state;
        this.alarm_label = alarm_label;
        this.ringtone_name = ringtone_name;
        this.vibrate = vibrate;
        this.alarm_pending_req_code = alarm_pending_req_code;

    }



}
