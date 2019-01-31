package com.varunsaini.android.ga_basictransitions;

public class Group {

    public String groupName;
    public String oneAlarm;
    public String twoAlarm;
    public String threeAlarm;
    public String moreAlarm;
    public int isGroupOn;
    public int groupColor;
    public boolean isSelected;

    public Group(String groupName,String oneAlarm,String twoAlarm,String threeAlarm,String moreAlarm,int isGroupOn,int groupColor,boolean isSelected){
        this.groupName = groupName;
        this.oneAlarm = oneAlarm;
        this.twoAlarm = twoAlarm;
        this.threeAlarm = threeAlarm;
        this.moreAlarm = moreAlarm;
        this.isGroupOn = isGroupOn;
        this.groupColor = groupColor;
        this.isSelected = isSelected;
    }

    public void setSelected(boolean selected) {
        this.isSelected = selected;
    }

    public boolean isSelected() {
        return isSelected;
    }
}
