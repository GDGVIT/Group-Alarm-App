package com.dscvit.android.onealarm.models;

public class GroupInfo  {

    public String time;
    public int alarm_state;
    public int alarm_pending_req_code;
    public int alarm_previous_state;
    public boolean isSelected;

    public GroupInfo(String time,int alarm_state,int alarm_pending_req_code,int alarm_previous_state,boolean isSelected){
        this.time = time;
        this.alarm_state = alarm_state;
        this.alarm_pending_req_code = alarm_pending_req_code;
        this.alarm_previous_state = alarm_previous_state;
        this.isSelected = isSelected;
    }

}
