package com.varunsaini.android.ga_basictransitions;

public class GroupInfo  {

    public String time;
    public int alarm_state;
    public int alarm_pending_req_code;

    public GroupInfo(String time,int alarm_state,int alarm_pending_req_code){
        this.time = time;
        this.alarm_state = alarm_state;
        this.alarm_pending_req_code = alarm_pending_req_code;
    }

}
