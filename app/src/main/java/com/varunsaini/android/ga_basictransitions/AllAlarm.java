package com.varunsaini.android.ga_basictransitions;

import android.widget.ToggleButton;

public class AllAlarm {

    public String time;
    public String groupName;
    public String daysToRing;
    public int isAlarmOn;
    public int alarm_pending_req_code;
    public boolean isSelected;


    public AllAlarm(String time,String groupName,int isAlarmOn,int alarm_pending_req_code,boolean isSelected,String daysToRing ){
        this.groupName = groupName;
        this.time = time;
        this.isAlarmOn = isAlarmOn;
        this.alarm_pending_req_code = alarm_pending_req_code;
        this.isSelected = isSelected;
        this.daysToRing = daysToRing;

    }
}
