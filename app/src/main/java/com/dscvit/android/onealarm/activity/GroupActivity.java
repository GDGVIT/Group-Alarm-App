package com.dscvit.android.onealarm.activity;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.rm.rmswitch.RMSwitch;
import com.dscvit.android.onealarm.Utils;
import com.dscvit.android.onealarm.misc.AlarmReciever;
import com.dscvit.android.onealarm.misc.DatabaseHandler;
import com.dscvit.android.onealarm.models.Group;
import com.dscvit.android.onealarm.models.GroupInfo;
import com.dscvit.android.onealarm.misc.NotificationReciever;
import com.dscvit.android.onealarm.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;

import static com.dscvit.android.onealarm.activity.AllActivity.ANIMATION_DURATION;
import static com.dscvit.android.onealarm.activity.AllActivity.EXTRA_TIME_ANIMATION;

public class GroupActivity extends AppCompatActivity {

    AlarmManager alarmMgr;
    PendingIntent alarmIntent;
    RecyclerView recyclerView;
    FloatingActionButton fabAddGroup,fabDeleteGroup,fabCancel;
    RecyclerView.LayoutManager layoutManager;
    private PendingIntent[] alarmIntent1;
    SQLiteDatabase sqLiteDatabase;
    DatabaseHandler db;
    NestedScrollView scrollView;
    TextView allView,groupView;
    Bundle savedInstanceState1;
    static int isGroupSelected = 0;  //0 means group not selected // 1 means group selected

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);

        Typeface tf = Typeface.createFromAsset(getAssets(),"fonts/Karla-Bold.ttf");

        allView = findViewById(R.id.group_all_text);
        groupView = findViewById(R.id.group_group_text);
        allView.setTypeface(tf);
        groupView.setTypeface(tf);

        fabAddGroup = findViewById(R.id.fabAddGroup);
        fabDeleteGroup = findViewById(R.id.fabDeleteGroup);
        fabCancel = findViewById(R.id.fabCancel);
        scrollView = findViewById(R.id.scrollView);
        scrollView.fullScroll(ScrollView.FOCUS_UP);
        scrollView.smoothScrollTo(0,0);


        db = new DatabaseHandler(this);
        sqLiteDatabase = this.openOrCreateDatabase("Alarmm",MODE_PRIVATE,null);
        db.onCreate(sqLiteDatabase);
        db.getAllDatabaseDataInLogcat();

        final ArrayList<Group> groupArrayList = db.getGroupActivityGroupList();

        recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view_group);
        recyclerView.setHasFixedSize(true);

        TextView title = findViewById(R.id.title);
        title.setTypeface(tf);


        CardView allButton = findViewById(R.id.group_all);
        allButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = (new Intent(GroupActivity.this, AllActivity.class));
                startActivity(i);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(new GroupRecyclerViewAdapter(groupArrayList));
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemViewCacheSize(20);
        recyclerView.setFocusable(false);
        recyclerView.setNestedScrollingEnabled(false);

        final GroupRecyclerViewAdapter groupObject = new GroupRecyclerViewAdapter(groupArrayList);

    }

    public void addGroup(View view) {
        Intent i = new Intent(GroupActivity.this, AboutGroupActivity.class);
        startActivity(i);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    @RequiresApi(api = Build.VERSION_CODES.ECLAIR)
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i = (new Intent(GroupActivity.this,AllActivity.class));
        startActivity(i);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
    

    public class GroupRecyclerViewAdapter extends RecyclerView.Adapter<GroupRecyclerViewAdapter.GroupRecyclerViewHolder>  {

        ArrayList<Group> s;
        AssetManager assetManager;
        Context context;
        boolean oneTimeLongTapped = false;
        public boolean atLeastOneSelected = false;
        int numberOfGroupsSelected = 0;
        NotificationManager nMgr;
        public ArrayList<Integer> intString = new ArrayList<>();

        public GroupRecyclerViewAdapter(ArrayList<Group> s){
            this.s = s;
        }

        @NonNull
        @Override
        public GroupRecyclerViewAdapter.GroupRecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            LayoutInflater layoutInflater = LayoutInflater.from(viewGroup.getContext());
            View view = layoutInflater.inflate(R.layout.group_cards_layout,viewGroup,false);

            assetManager = viewGroup.getContext().getAssets();
            context = viewGroup.getContext();

            return  new GroupRecyclerViewHolder(view);
        }

        public void onBindViewHolder(@NonNull final GroupRecyclerViewAdapter.GroupRecyclerViewHolder groupRecyclerViewHolder, final int i) {


            final Group model = s.get(i);
            final String aa = s.get(i).groupName;
            final String bb = s.get(i).oneAlarm;
            final String cc = s.get(i).twoAlarm;
            final String dd = s.get(i).threeAlarm;
            String ee = s.get(i).moreAlarm;
            final int ff = s.get(i).isGroupOn;
            int gg = s.get(i).groupColor;
            final boolean hh = s.get(i).isSelected;

            Typeface tf = Typeface.createFromAsset(assetManager,"fonts/Karla-Bold.ttf");
            groupRecyclerViewHolder.groupName.setTypeface(tf);

            groupRecyclerViewHolder.groupName.setText(aa);
            if(!model.isSelected){
                if(gg==1){
                    groupRecyclerViewHolder.groupColor.setBackgroundResource(R.drawable.list_grad_purple);
                }else if(gg==2){
                    groupRecyclerViewHolder.groupColor.setBackgroundResource(R.drawable.list_grad_green);
                }else if(gg==3){
                    groupRecyclerViewHolder.groupColor.setBackgroundResource(R.drawable.list_grad_pink);
                }else if(gg==4){
                    groupRecyclerViewHolder.groupColor.setBackgroundResource(R.drawable.list_grad_brown);
                }else if(gg==5){
                    groupRecyclerViewHolder.groupColor.setBackgroundResource(R.drawable.list_grad_violet);
                }else if(gg==6){

                }else if(gg==7){

                }
            }else{
                groupRecyclerViewHolder.groupColor.setBackgroundResource(R.color.selectedGroupGray);
            }

            ///////starting from here
            if(ff==1){
                groupRecyclerViewHolder.rmsSwitch.setChecked(true);
            }else{
                groupRecyclerViewHolder.rmsSwitch.setChecked(false);
            }

            groupRecyclerViewHolder.rmsSwitch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, AlarmReciever.class);
                    alarmMgr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
                    ArrayList<GroupInfo> groupInfoArrayList = db.getAllGroupInfo(aa);
                    if(groupRecyclerViewHolder.rmsSwitch.isChecked()){
                        groupRecyclerViewHolder.rmsSwitch.setChecked(false);
                        for(int i=0;i<groupInfoArrayList.size();i++){
                            ArrayList<Integer> integerArrayList = db.getThisAlarmIntents(Integer.valueOf(String.valueOf(groupInfoArrayList.get(i).alarm_pending_req_code).substring(3,String.valueOf(groupInfoArrayList.get(i).alarm_pending_req_code).length()-2)));
                            for(Integer ii : integerArrayList) {
                                PendingIntent alarmIntent = PendingIntent.getBroadcast(context, Integer.valueOf(ii), intent, PendingIntent.FLAG_UPDATE_CURRENT);
                                alarmMgr.cancel(alarmIntent);
                                nMgr = (NotificationManager)getApplicationContext().getSystemService(NOTIFICATION_SERVICE);
                                nMgr.cancel(Integer.valueOf(ii));
                            }
                            db.changePreviousAlarmState(groupInfoArrayList.get(i).alarm_pending_req_code,groupInfoArrayList.get(i).alarm_state);
                            db.updateAlarmState(groupInfoArrayList.get(i).alarm_pending_req_code,0);
                        }
                        db.updateGroupState(aa,0);
                        Toast.makeText(context, "Group Turned Off", Toast.LENGTH_SHORT).show();
                    }else{
                        groupRecyclerViewHolder.rmsSwitch.toggle();
                        for (int i=0;i<groupInfoArrayList.size();i++){
                            if(groupInfoArrayList.get(i).alarm_previous_state==1) {
                                setAlarmsOnToggle(db.getDaysToRing(groupInfoArrayList.get(i).alarm_pending_req_code), groupInfoArrayList.get(i).alarm_pending_req_code);
                                db.updateAlarmState(groupInfoArrayList.get(i).alarm_pending_req_code,1);
                            }
                            db.changePreviousAlarmState(groupInfoArrayList.get(i).alarm_pending_req_code,-1);
                        }
                        groupRecyclerViewHolder.rmsSwitch.setChecked(true);
                        db.updateGroupState(aa,1);
                        Toast.makeText(context, "Group Turned On", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            
            fabCancel.setOnClickListener(new View.OnClickListener() {
                @SuppressLint("RestrictedApi")
                @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                @Override
                public void onClick(View v) {
//                    Toast.makeText(GroupActivity.this, "Cancel Clicked", Toast.LENGTH_SHORT).show();
                        numberOfGroupsSelected= 0;
                        finish();
                        startActivity(getIntent());
                        overridePendingTransition(0,0);
                        atLeastOneSelected=false;
                    fabDeleteGroup.animate().alpha(0).setDuration(ANIMATION_DURATION).start();
                    fabCancel.animate().translationX(0).translationY(0).setDuration(ANIMATION_DURATION).rotationBy(-45).start();
                    final Handler handler = new Handler();

                    final Runnable r = new Runnable() {
                        public void run() {
                            fabDeleteGroup.setVisibility(View.GONE);
                            fabCancel.setVisibility(View.GONE);
                            fabAddGroup.setVisibility(View.VISIBLE);
                        }
                    };

                    handler.postDelayed(r, ANIMATION_DURATION+EXTRA_TIME_ANIMATION);


                }
            });

            fabDeleteGroup.setOnClickListener(new View.OnClickListener() {
                @SuppressLint("RestrictedApi")
                @Override
                public void onClick(View v) {
                    DatabaseHandler db = new DatabaseHandler(context);
                    Intent intent = new Intent(context, AlarmReciever.class);
                    alarmMgr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
                    Collections.sort(intString);
                    Log.d("dsd", "onClick: "+intString.size());
                    for(int i=intString.size()-1;i>=0;i--){
                        Log.d("fggf", "onClick: "+intString.get(i) + "||");
                        ArrayList<GroupInfo> arrayList = db.getAllGroupInfo(s.get(intString.get(i)).groupName);
                        for(int j=0;j<arrayList.size();j++){
                            int request_code = arrayList.get(j).alarm_pending_req_code;
                            ArrayList<Integer> integerArrayList = db.getThisAlarmIntents(Integer.valueOf(String.valueOf(request_code).substring(3,String.valueOf(request_code).length()-2)));
                            for(Integer k : integerArrayList) {
                                PendingIntent alarmIntent = PendingIntent.getBroadcast(context, Integer.valueOf(k), intent, PendingIntent.FLAG_UPDATE_CURRENT);
                                alarmMgr.cancel(alarmIntent);
                                nMgr = (NotificationManager)getApplicationContext().getSystemService(NOTIFICATION_SERVICE);
                                nMgr.cancel(Integer.valueOf(k));
                            }
                            db.removeDaysPendingReq(Integer.valueOf(String.valueOf(request_code).substring(3,String.valueOf(request_code).length()-2)));
                        }
                        db.deleteAGroup(s.get(intString.get(i)).groupName);
                        s.remove(intString.get(i).intValue());
                        notifyItemRemoved(intString.get(i));
                    }
                    numberOfGroupsSelected = 0;
                    atLeastOneSelected = false;
                    oneTimeLongTapped = false;
                    fabDeleteGroup.animate().alpha(0).setDuration(ANIMATION_DURATION).start();
                    fabCancel.animate().translationX(0).translationY(0).setDuration(ANIMATION_DURATION).rotationBy(-45).start();
                    final Handler handler = new Handler();

                    final Runnable r = new Runnable() {
                        public void run() {
                            fabDeleteGroup.setVisibility(View.GONE);
                            fabCancel.setVisibility(View.GONE);
                            fabAddGroup.setVisibility(View.VISIBLE);
                        }
                    };
                    handler.postDelayed(r, ANIMATION_DURATION+EXTRA_TIME_ANIMATION);


                }
            });

            if(bb==null){
                groupRecyclerViewHolder.firstAlarm.setVisibility(View.GONE);
            }else{
                groupRecyclerViewHolder.firstAlarm.setText(bb);
                groupRecyclerViewHolder.firstAlarm.setTypeface(tf);
            }

            if(cc==null){
                groupRecyclerViewHolder.secondAlarm.setVisibility(View.GONE);
            }else{
                groupRecyclerViewHolder.secondAlarm.setText(cc);
                groupRecyclerViewHolder.secondAlarm.setTypeface(tf);
            }

            if(dd==null){
                groupRecyclerViewHolder.thirdAlarm.setVisibility(View.GONE);
            }else{
                groupRecyclerViewHolder.thirdAlarm.setText(dd);
                groupRecyclerViewHolder.thirdAlarm.setTypeface(tf);
            }

            if(ee==null){
                groupRecyclerViewHolder.moreAlarm.setVisibility(View.GONE);
            }else{
                groupRecyclerViewHolder.moreAlarm.setText(ee);
                groupRecyclerViewHolder.moreAlarm.setTypeface(tf);
            }

            if (ff==0){
                groupRecyclerViewHolder.rmsSwitch.setChecked(false);
            }else{
                groupRecyclerViewHolder.rmsSwitch.setChecked(true);
            }

            groupRecyclerViewHolder.allCard.setOnClickListener(new View.OnClickListener() {
                @SuppressLint("RestrictedApi")
                @Override
                public void onClick(View v) {

                    if(!model.isSelected && atLeastOneSelected){
                        numberOfGroupsSelected++;
                        groupRecyclerViewHolder.rmsSwitch.setVisibility(View.INVISIBLE);
                        model.setSelected(true);
                        groupRecyclerViewHolder.groupColor.setBackgroundResource(R.color.selectedGroupGray);
                        intString.add(i);
                    }else if(atLeastOneSelected && model.isSelected())
                    {
                        numberOfGroupsSelected--;
                        model.setSelected(false);
                        int pos = intString.indexOf(i);
                        groupRecyclerViewHolder.rmsSwitch.setVisibility(View.VISIBLE);
                        intString.remove(pos);
                        if(model.groupColor==1){
                            animateColorChanging(groupRecyclerViewHolder.groupColor, Color.GRAY,R.color.purple_grad_start,0,model.groupColor);
                        }else if(model.groupColor==2){
                            animateColorChanging(groupRecyclerViewHolder.groupColor, Color.GRAY,R.color.green_grad_start,0,model.groupColor);
                        }else if(model.groupColor==3){
                            animateColorChanging(groupRecyclerViewHolder.groupColor, Color.GRAY,R.color.pink_grad_start,0,model.groupColor);
                        }else if(model.groupColor==4){
                            animateColorChanging(groupRecyclerViewHolder.groupColor, Color.GRAY,R.color.brown_grad_start,0,model.groupColor);
                        }else if(model.groupColor==5){
                            animateColorChanging(groupRecyclerViewHolder.groupColor, Color.GRAY,R.color.pink_grad_start,0,model.groupColor);
                        }else if(model.groupColor==6){

                        }else if(model.groupColor==7){

                        }
                        if(numberOfGroupsSelected==0){
                            atLeastOneSelected = false;
                        }
                    }else if(!atLeastOneSelected){
                        fabDeleteGroup.animate().alpha(0).setDuration(ANIMATION_DURATION).start();
                        fabCancel.animate().translationX(0).translationY(0).setDuration(ANIMATION_DURATION).rotationBy(-45).start();
                        final Handler handler = new Handler();

                        final Runnable r = new Runnable() {
                            public void run() {
                                fabDeleteGroup.setVisibility(View.GONE);
                                fabCancel.setVisibility(View.GONE);
                                fabAddGroup.setVisibility(View.VISIBLE);
                            }
                        };
                        handler.postDelayed(r, ANIMATION_DURATION+EXTRA_TIME_ANIMATION);
                        Intent i = new Intent(context, AboutGroupActivity.class);
                        i.putExtra("groupName", aa);
                        ActivityOptionsCompat options = ActivityOptionsCompat.
                                makeSceneTransitionAnimation((Activity) context, groupRecyclerViewHolder.allCard, "groupName");
                        startActivity(i, options.toBundle());
                    }if(!atLeastOneSelected){
                        fabDeleteGroup.animate().alpha(0).setDuration(ANIMATION_DURATION).start();
                        fabCancel.animate().translationX(0).translationY(0).setDuration(ANIMATION_DURATION).rotationBy(-45).start();
                        final Handler handler = new Handler();

                        final Runnable r = new Runnable() {
                            public void run() {
                                fabDeleteGroup.setVisibility(View.GONE);
                                fabCancel.setVisibility(View.GONE);
                                fabAddGroup.setVisibility(View.VISIBLE);
                            }
                        };

                        handler.postDelayed(r, ANIMATION_DURATION+EXTRA_TIME_ANIMATION);

                        oneTimeLongTapped= false;
                    }
                    Log.d("sasa", "onClick: single tap "+ numberOfGroupsSelected + "||" + atLeastOneSelected + "//" + model.isSelected);
                    Log.d("dfdf", "onClick: single Tap aboutAlarm"+aa+"||"+bb+"||"+cc+"||"+dd+"||"+ff+"||"+model.groupColor+"||"+model.isSelected);
                }
            });
//
            groupRecyclerViewHolder.allCard.setOnLongClickListener(new View.OnLongClickListener() {
                @SuppressLint("RestrictedApi")
                @Override
                public boolean onLongClick(View v) {
                    if(!oneTimeLongTapped) {
                        groupRecyclerViewHolder.rmsSwitch.setVisibility(View.INVISIBLE);
                        atLeastOneSelected = true;
                        model.setSelected(true);
                        numberOfGroupsSelected++;
                        groupRecyclerViewHolder.groupColor.setBackgroundResource(R.color.selectedGroupGray);
                        fabDeleteGroup.setVisibility(View.VISIBLE);
                        fabCancel.setVisibility(View.VISIBLE);
                        fabAddGroup.setVisibility(View.GONE);
                        fabDeleteGroup.animate().translationX(-100).setDuration(ANIMATION_DURATION).translationY(25).alpha(1).start();
                        fabCancel.animate().translationX(100).translationY(25).alpha(1).setDuration(ANIMATION_DURATION).rotationBy(45).start();
                        fabDeleteGroup.setVisibility(View.VISIBLE);
                        fabCancel.setVisibility(View.VISIBLE);
                        fabAddGroup.setVisibility(View.GONE);
                        intString.add(i);
                        oneTimeLongTapped = true;
                    }

//                groupRecyclerViewHolder.groupColor.setBackgroundResource(R.color.selectedGroupGray);
                    return true;
                }
            });

            if(gg==1){
                groupRecyclerViewHolder.groupColor.setBackgroundResource(R.drawable.list_grad_purple);
            }else if(gg==2){
                groupRecyclerViewHolder.groupColor.setBackgroundResource(R.drawable.list_grad_green);
            }else if(gg==3){
                groupRecyclerViewHolder.groupColor.setBackgroundResource(R.drawable.list_grad_pink);
            }else if(gg==4){
                groupRecyclerViewHolder.groupColor.setBackgroundResource(R.drawable.list_grad_brown);
            }else if(gg==5){
                groupRecyclerViewHolder.groupColor.setBackgroundResource(R.drawable.list_grad_violet);
            }else if(gg==6){

            }else if(gg==7){

            }



        }

        @Override
        public int getItemCount() { return s.size(); }

        public class GroupRecyclerViewHolder extends RecyclerView.ViewHolder {

            TextView groupName,firstAlarm,secondAlarm,thirdAlarm,moreAlarm;
            RMSwitch rmsSwitch;
            CardView allCard;
            LinearLayout groupColor;

            public GroupRecyclerViewHolder(@NonNull View itemView) {
                super(itemView);
                groupName = itemView.findViewById(R.id.groupName);
                firstAlarm = itemView.findViewById(R.id.firstAlarm);
                secondAlarm = itemView.findViewById(R.id.secondAlarm);
                thirdAlarm = itemView.findViewById(R.id.thirdAlarm);
                moreAlarm = itemView.findViewById(R.id.moreAlarm);
                rmsSwitch = itemView.findViewById(R.id.all_switch);
                allCard = itemView.findViewById(R.id.allCard);
                groupColor = itemView.findViewById(R.id.groupColor);

            }
            
        }

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        public void setAlarmsOnToggle(String daystoRing, int alarm_pending_req_code) {
            db = new DatabaseHandler(context);
            sqLiteDatabase = context.openOrCreateDatabase("Alarm", MODE_PRIVATE, null);
            db.onCreate(sqLiteDatabase);
            alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            Intent intent = new Intent(context, AlarmReciever.class);

            int[] hourMin = db.getHoursMin(alarm_pending_req_code);
            Calendar now = Calendar.getInstance();
            long _alarm = 0;

            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, hourMin[0]);
            calendar.set(Calendar.MINUTE, hourMin[1]);
            calendar.set(Calendar.SECOND, 0);

            if (!daystoRing.equals("")) {
                String[] dayys = daystoRing.split("#");
                alarmIntent1 = new PendingIntent[dayys.length];
                int[] allRequests = new int[dayys.length];
                int k = 0;

                alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                ArrayList<Integer> integerArrayList = db.getThisAlarmIntents(Integer.valueOf(String.valueOf(alarm_pending_req_code).substring(3,String.valueOf(alarm_pending_req_code).length()-2)));
                for (Integer i : integerArrayList) {
                    PendingIntent alarmIntent = PendingIntent.getBroadcast(context, Integer.valueOf(i), intent, PendingIntent.FLAG_UPDATE_CURRENT);
                    alarmMgr.cancel(alarmIntent);
                    nMgr = (NotificationManager)getApplicationContext().getSystemService(NOTIFICATION_SERVICE);
                    nMgr.cancel(Integer.valueOf(i));
                }

                String x = String.valueOf(alarm_pending_req_code);

                for (String d : dayys) {
                    if (d.equals("mon")) {
                        int y = Integer.valueOf("111" + x.substring(3,x.length()-2));
                        settingAlarmOnDays(y, 2, k,hourMin[0],hourMin[1]);
                        allRequests[k] = y;
                        k++;
                    } else if (d.equals("tue")) {
                        int y = Integer.valueOf("222" + x.substring(3,x.length()-2));
                        settingAlarmOnDays(y, 3, k,hourMin[0],hourMin[1]);
                        allRequests[k] = y;
                        k++;
                    } else if (d.equals("wed")) {
                        int y = Integer.valueOf("333" + x.substring(3,x.length()-2));
                        settingAlarmOnDays(y, 4, k,hourMin[0],hourMin[1]);
                        allRequests[k] = y;
                        k++;
                    } else if (d.equals("thurs")) {
                        int y = Integer.valueOf("444" + x.substring(3,x.length()-2));
                        settingAlarmOnDays(y, 5, k,hourMin[0],hourMin[1]);
                        allRequests[k] = y;
                        k++;
                    } else if (d.equals("fri")) {
                        int y = Integer.valueOf("555" + x.substring(3,x.length()-2));
                        settingAlarmOnDays(y, 6, k,hourMin[0],hourMin[1]);
                        allRequests[k] = y;
                        k++;
                    } else if (d.equals("sat")) {
                        int y = Integer.valueOf("666" + x.substring(3,x.length()-2));
                        settingAlarmOnDays(y, 7, k,hourMin[0],hourMin[1]);
                        allRequests[k] = y;
                        k++;
                    } else if (d.equals("sun")) {
                        int y = Integer.valueOf("777" + x.substring(3,x.length()-2));
                        settingAlarmOnDays(y, 1, k,hourMin[0],hourMin[1]);
                        allRequests[k] = y;
                        k++;
                    }
                }

            }
        }

        void settingAlarmOnDays(int y, int dayOfWeek, int k,int mSelectedHour,int mSelectedMinute) {

            Intent intent = new Intent(GroupActivity.this, AlarmReciever.class);
            Calendar now = Calendar.getInstance();
            long _alarm = 0;

            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
            calendar.set(Calendar.HOUR_OF_DAY, mSelectedHour);
            calendar.set(Calendar.MINUTE, mSelectedMinute);
            calendar.set(Calendar.SECOND, 0);
            Log.d("timmmmm", "now tim: " + now.getTimeInMillis());
            calendar.set(Calendar.DAY_OF_WEEK, dayOfWeek);
            intent.putExtra("request_code", y);
            if (calendar.getTimeInMillis() <= now.getTimeInMillis()) {
                _alarm = calendar.getTimeInMillis() + (24 * 60 * 60 * 1000 * 7);
                Log.d("timmmmm", "set tim: " + _alarm);
            } else {
                _alarm = calendar.getTimeInMillis();
            }
            alarmIntent1[k] = PendingIntent.getBroadcast(GroupActivity.this, y, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmMgr.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, _alarm, alarmIntent1[k]);
            }else{
                alarmMgr.setExact(AlarmManager.RTC_WAKEUP, _alarm, alarmIntent1[k]);
            }

            Intent notifyIntent = new Intent(GroupActivity.this, NotificationReciever.class);
            notifyIntent.putExtra("trimmedRequestId",y);
            PendingIntent pendingIntent = PendingIntent.getBroadcast
                    (GroupActivity.this, y, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            AlarmManager alarmManager = (AlarmManager) GroupActivity.this.getSystemService(Context.ALARM_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,  _alarm - (Utils.getNotificationDuration(getApplicationContext())), pendingIntent);
            }else{
                alarmManager.setExact(AlarmManager.RTC_WAKEUP,  _alarm - (Utils.getNotificationDuration(getApplicationContext())), pendingIntent);
            }

        }

    }

    public void animateColorChanging(final View v, int colorFrom, int colorTo, final int VISIBILITY, final int background){

        ValueAnimator colorAnimate = ValueAnimator.ofObject( new ArgbEvaluator(),colorFrom,colorTo);
        colorAnimate.setDuration(250);
        colorAnimate.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {


                if(v instanceof LinearLayout){
                    v.setBackgroundColor((int) animation.getAnimatedValue());
                    if(background==1){
                        v.setBackgroundResource(R.drawable.list_grad_purple);
                    }else if(background==2){
                        v.setBackgroundResource(R.drawable.list_grad_green);
                    }else if(background==3){
                        v.setBackgroundResource(R.drawable.list_grad_pink);
                    }else if(background==4){
                        v.setBackgroundResource(R.drawable.list_grad_brown);
                    }else if(background==5){
                        v.setBackgroundResource(R.drawable.list_grad_violet);
                    }
                }

            }
        });
        colorAnimate.start();

    }




}
