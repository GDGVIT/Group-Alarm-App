package com.varunsaini.android.ga_basictransitions;

public class Group {

    public String groupName;
    public String oneAlarm;
    public String twoAlarm;
    public String threeAlarm;
    public String moreAlarm;
    public int isGroupOn;

    public Group(String groupName,String oneAlarm,String twoAlarm,String threeAlarm,String moreAlarm,int isGroupOn){
        this.groupName = groupName;
        this.oneAlarm = oneAlarm;
        this.twoAlarm = twoAlarm;
        this.threeAlarm = threeAlarm;
        this.moreAlarm = moreAlarm;
        this.isGroupOn = isGroupOn;
    }


}
