package com.dscvit.android.onealarm.activity;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
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
import com.dscvit.android.onealarm.models.AllAlarm;
import com.dscvit.android.onealarm.misc.DatabaseHandler;
import com.dscvit.android.onealarm.models.GroupInfo;
import com.dscvit.android.onealarm.misc.NotificationReciever;
import com.dscvit.android.onealarm.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;

public class AllActivity extends AppCompatActivity {

    AlarmManager alarmMgr;
    PendingIntent alarmIntent;
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    SQLiteDatabase sqLiteDatabase;
    DatabaseHandler db;
    TextView allView, groupView, title;
    NestedScrollView scrollView;
    FloatingActionButton fabAddAlarm, fabDeleteAlarm, fabCancel;
    LinearLayout allColor;
    public static final int ANIMATION_DURATION = 500;
    public static final int  EXTRA_TIME_ANIMATION = 50;


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all);

        Typeface tf = Typeface.createFromAsset(getAssets(), "fonts/Karla-Bold.ttf");

        allView = findViewById(R.id.all_all_text);
        groupView = findViewById(R.id.all_group_text);
        allView.setTypeface(tf);
        groupView.setTypeface(tf);
        scrollView = findViewById(R.id.scrollView);
        scrollView.fullScroll(ScrollView.FOCUS_UP);
        scrollView.smoothScrollTo(0, 0);
        fabAddAlarm = findViewById(R.id.fabAddAlarm);
        fabDeleteAlarm = findViewById(R.id.fabDeleteAlarm);
        fabCancel = findViewById(R.id.fabCancel);
        allColor = findViewById(R.id.allColor);
        title = findViewById(R.id.title);
        title.setTypeface(tf);


        db = new DatabaseHandler(this);
        sqLiteDatabase = this.openOrCreateDatabase("Alarmm", MODE_PRIVATE, null);
        db.onCreate(sqLiteDatabase);
        db.getAllDatabaseDataInLogcat();

        ArrayList<AllAlarm> allAlarmArrayList = db.getAllActivtiyAlarmList();
        db.getAllDaysReqCodesInLogcat();


        CardView groupButton = findViewById(R.id.all_group);
        groupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AllActivity.this, GroupActivity.class));
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });


        recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view_all_alarms);
        recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(new AllAlarmRecyclerViewAdapter(allAlarmArrayList));
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemViewCacheSize(20);
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        recyclerView.setFocusable(false);
        recyclerView.setNestedScrollingEnabled(false);

    }


    public void newAlarm(View view) {

        Intent i = new Intent(AllActivity.this, EditAlarmActivity.class);
        i.putExtra("belongs_to_group", 0);
        startActivity(i);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

    }
    public void openSettings(View view){
        Intent i = new Intent(AllActivity.this,SettingsActivity.class);
        startActivity(i);
    }

    public class AllAlarmRecyclerViewAdapter extends RecyclerView.Adapter<AllAlarmRecyclerViewAdapter.AllAlarmRecyclerViewHolder> {

        ArrayList<AllAlarm> s;
        AssetManager assetManager;
        Context context;
        AlarmManager alarmMgr;
        private PendingIntent[] alarmIntent;
        boolean oneTimeLongTapped = false;
        public boolean atLeastOneSelected = false;
        int numberOfGroupsSelected = 0;
        public ArrayList<Integer> intString = new ArrayList<>();
        SQLiteDatabase sqLiteDatabase;
        DatabaseHandler db;
        NotificationManager nMgr;

        public AllAlarmRecyclerViewAdapter(ArrayList<AllAlarm> s) {
            this.s = s;
        }

        @NonNull
        @Override
        public AllAlarmRecyclerViewAdapter.AllAlarmRecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            LayoutInflater layoutInflater = LayoutInflater.from(viewGroup.getContext());
            View view = layoutInflater.inflate(R.layout.all_cards_layout, viewGroup, false);
            assetManager = viewGroup.getContext().getAssets();
            context = viewGroup.getContext();
            return new AllAlarmRecyclerViewAdapter.AllAlarmRecyclerViewHolder(view);
        }

        public void onBindViewHolder(@NonNull final AllAlarmRecyclerViewAdapter.AllAlarmRecyclerViewHolder allAlarmRecyclerViewHolder, final int i) {

            final AllAlarm model = s.get(i);
            final String aa = s.get(i).time;
            final String bb = s.get(i).groupName;
            int cc = s.get(i).isAlarmOn;
            final int dd = s.get(i).alarm_pending_req_code;
            String[] ee = s.get(i).daysToRing.split("#");
            String days = "";
            for (String e : ee) {
                days += e.substring(0, 1).toUpperCase() + e.substring(1) + " ";
            }

            Typeface tf = Typeface.createFromAsset(assetManager, "fonts/Karla-Bold.ttf");
            Typeface tf1 = Typeface.createFromAsset(assetManager, "fonts/Karla.ttf");

            allAlarmRecyclerViewHolder.time.setTypeface(tf);
            allAlarmRecyclerViewHolder.daysToRing.setText(days);
            allAlarmRecyclerViewHolder.daysToRing.setTypeface(tf1);


            if (bb == null) {
                allAlarmRecyclerViewHolder.groupName.setVisibility(View.GONE);
            } else {
                allAlarmRecyclerViewHolder.groupName.setText(bb);
                allAlarmRecyclerViewHolder.groupName.setTypeface(tf1);
            }

            allAlarmRecyclerViewHolder.time.setText(aa);
            if (cc == 0) {
                allAlarmRecyclerViewHolder.rmsSwitch.setChecked(false);
            } else {
                allAlarmRecyclerViewHolder.rmsSwitch.setChecked(true);
            }

            allAlarmRecyclerViewHolder.rmsSwitch.setOnClickListener(new View.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, AlarmReciever.class);
                    alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                    final DatabaseHandler db = new DatabaseHandler(context);
                    if (allAlarmRecyclerViewHolder.rmsSwitch.isChecked()) {
                        allAlarmRecyclerViewHolder.rmsSwitch.toggle();
                        ArrayList<Integer> integerArrayList = db.getThisAlarmIntents(Integer.valueOf(String.valueOf(dd).substring(3,String.valueOf(dd).length()-2)));
                        for (Integer i : integerArrayList) {
                            PendingIntent alarmIntent = PendingIntent.getBroadcast(context, Integer.valueOf(i), intent, PendingIntent.FLAG_UPDATE_CURRENT);
                            alarmMgr.cancel(alarmIntent);
                            nMgr = (NotificationManager)getApplicationContext().getSystemService(NOTIFICATION_SERVICE);
                            nMgr.cancel(i);
                            Log.d("i", "onClick: ii"+i);

                        }
                        Toast.makeText(context, "Alarm Turned Off", Toast.LENGTH_SHORT).show();
                        db.updateAlarmState(dd, 0);
                    } else {
                        if (bb != null && db.getGroupStateByGroupName(bb) == 0) {

                            AlertDialog.Builder builder = new AlertDialog.Builder(AllActivity.this);
                            builder.setMessage("You want to turn on the alarm ? The corressponding group will also be turned on automatically")
                                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            allAlarmRecyclerViewHolder.rmsSwitch.toggle();
                                            setAlarmsOnToggle(db.getDaysToRing(dd), dd);
                                            Toast.makeText(context, "Alarm Turned On", Toast.LENGTH_SHORT).show();
                                            db.updateAlarmState(dd, 1);

                                            db.updateGroupState(bb,1);
                                            ArrayList<GroupInfo> groupInfoArrayList = db.getAllGroupInfo(bb);
                                            for (int i=0;i<groupInfoArrayList.size();i++){
                                                if(groupInfoArrayList.get(i).alarm_previous_state==1) {
                                                    setAlarmsOnToggle(db.getDaysToRing(groupInfoArrayList.get(i).alarm_pending_req_code), groupInfoArrayList.get(i).alarm_pending_req_code);
                                                    db.updateAlarmState(groupInfoArrayList.get(i).alarm_pending_req_code,1);
                                                }
                                                db.changePreviousAlarmState(groupInfoArrayList.get(i).alarm_pending_req_code,-1);
                                            }
                                            Toast.makeText(context, "Group Turned On", Toast.LENGTH_SHORT).show();

                                        }
                                    })
                                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {

                                        }
                                    });
                            builder.create().show();
                        }else{
                            allAlarmRecyclerViewHolder.rmsSwitch.toggle();
                            setAlarmsOnToggle(db.getDaysToRing(dd), dd);
                            Toast.makeText(context, "Alarm Turned On", Toast.LENGTH_SHORT).show();
                            db.updateAlarmState(dd, 1);
                        }
                    }
                }
            });

            fabCancel.setOnClickListener(new View.OnClickListener() {
                @SuppressLint("RestrictedApi")
                @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                @Override
                public void onClick(View v) {
//                    Toast.makeText(AllActivity.this, "Cancel Clicked", Toast.LENGTH_SHORT).show();
                    numberOfGroupsSelected = 0;
                    finish();
                    startActivity(getIntent());
                    overridePendingTransition(0,0);
                    atLeastOneSelected = false;
                    fabDeleteAlarm.animate().alpha(0).setDuration(ANIMATION_DURATION).start();
                    fabCancel.animate().translationX(0).translationY(0).setDuration(ANIMATION_DURATION).rotationBy(-45).start();
                    final Handler handler = new Handler();

                    final Runnable r = new Runnable() {
                        public void run() {
                            fabDeleteAlarm.setVisibility(View.GONE);
                            fabCancel.setVisibility(View.GONE);
                            fabAddAlarm.setVisibility(View.VISIBLE);
                        }
                    };

                    handler.postDelayed(r, ANIMATION_DURATION+EXTRA_TIME_ANIMATION);

                }
            });

            fabDeleteAlarm.setOnClickListener(new View.OnClickListener() {
                @SuppressLint("RestrictedApi")
                @Override
                public void onClick(View v) {
                    DatabaseHandler db = new DatabaseHandler(context);
                    Intent intent = new Intent(context, AlarmReciever.class);
                    alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                    Collections.sort(intString);
                    Log.d("dsd", "onClick: " + intString.size());
                    for (int i = intString.size() - 1; i >= 0; i--) {
                        Log.d("fggf", "onClick: " + intString.get(i) + "||");

                        db.deleteAnAlarm(s.get(intString.get(i)).alarm_pending_req_code);
                        ArrayList<Integer> integerArrayList = db.getThisAlarmIntents(Integer.valueOf(String.valueOf(s.get(intString.get(i)).alarm_pending_req_code).substring(3,String.valueOf(s.get(intString.get(i)).alarm_pending_req_code).length()-2)));
                        for (Integer ii : integerArrayList) {
                            PendingIntent alarmIntent = PendingIntent.getBroadcast(context, Integer.valueOf(ii), intent, PendingIntent.FLAG_UPDATE_CURRENT);
                            alarmMgr.cancel(alarmIntent);
                            nMgr = (NotificationManager)getApplicationContext().getSystemService(NOTIFICATION_SERVICE);
                            Log.d("ii", "onClick: ii"+ii);
                            nMgr.cancel(Integer.valueOf(ii));
                        }
                        db.removeDaysPendingReq(Integer.valueOf(String.valueOf(s.get(intString.get(i)).alarm_pending_req_code).substring(3,String.valueOf(s.get(intString.get(i)).alarm_pending_req_code).length()-2)));
                        s.remove(intString.get(i).intValue());
                        notifyItemRemoved(intString.get(i));
                    }
                    numberOfGroupsSelected = 0;
                    atLeastOneSelected = false;
                    oneTimeLongTapped = false;

                    fabDeleteAlarm.animate().alpha(0).setDuration(ANIMATION_DURATION).start();
                    fabCancel.animate().translationX(0).translationY(0).setDuration(ANIMATION_DURATION).rotationBy(-45).start();
                    final Handler handler = new Handler();

                    final Runnable r = new Runnable() {
                        public void run() {
                            fabDeleteAlarm.setVisibility(View.GONE);
                            fabCancel.setVisibility(View.GONE);
                            fabAddAlarm.setVisibility(View.VISIBLE);
                        }
                    };
                    handler.postDelayed(r, ANIMATION_DURATION+EXTRA_TIME_ANIMATION);

                }
            });

            allAlarmRecyclerViewHolder.allCard.setOnClickListener(new View.OnClickListener() {
                @SuppressLint({"RestrictedApi", "ResourceAsColor"})
                @Override
                public void onClick(View v) {

                    if (!model.isSelected && atLeastOneSelected) {
                        numberOfGroupsSelected++;
                        model.isSelected = (true);
                        animateColorChanging(allAlarmRecyclerViewHolder.daysToRing,Color.GRAY,getResources().getColor(R.color.white),0);
                        animateColorChanging(allAlarmRecyclerViewHolder.groupName,getResources().getColor(R.color.pureBlack),getResources().getColor(R.color.white),0);
                        animateColorChanging(allAlarmRecyclerViewHolder.time,getResources().getColor(R.color.pureBlack),getResources().getColor(R.color.white),0);
                        allAlarmRecyclerViewHolder.rmsSwitch.setVisibility(View.INVISIBLE);
                        animateColorChanging(allAlarmRecyclerViewHolder.allColor,getResources().getColor(R.color.white),getResources().getColor(R.color.selectedGroupGray),0);
                        intString.add(i);
                    } else if (atLeastOneSelected && model.isSelected) {
                        numberOfGroupsSelected--;
                        model.isSelected = (false);
                        int pos = intString.indexOf(i);
                        intString.remove(pos);
                        animateColorChanging(allAlarmRecyclerViewHolder.daysToRing,getResources().getColor(R.color.white),Color.GRAY,0);
                        animateColorChanging(allAlarmRecyclerViewHolder.groupName,getResources().getColor(R.color.white),getResources().getColor(R.color.pureBlack),0);
                        animateColorChanging(allAlarmRecyclerViewHolder.time,getResources().getColor(R.color.white),getResources().getColor(R.color.pureBlack),0);
                        allAlarmRecyclerViewHolder.rmsSwitch.setVisibility(View.VISIBLE);
                        animateColorChanging(allAlarmRecyclerViewHolder.allColor,getResources().getColor(R.color.selectedGroupGray),getResources().getColor(R.color.white),0);

                        if (numberOfGroupsSelected == 0) {
                            atLeastOneSelected = false;
                        }
                    } else if (!atLeastOneSelected) {
                        fabDeleteAlarm.animate().alpha(0).setDuration(ANIMATION_DURATION).start();
                        fabCancel.animate().translationX(0).translationY(0).setDuration(ANIMATION_DURATION).rotationBy(-45).start();
                        final Handler handler = new Handler();

                        final Runnable r = new Runnable() {
                            public void run() {
                                fabDeleteAlarm.setVisibility(View.GONE);
                                fabCancel.setVisibility(View.GONE);
                                fabAddAlarm.setVisibility(View.VISIBLE);
                            }
                        };
                        handler.postDelayed(r, ANIMATION_DURATION+EXTRA_TIME_ANIMATION);
                        Intent i = new Intent(context, EditAlarmActivity.class);
                        i.putExtra("alarm_pending_req_code", dd);
                        i.putExtra("nameOfGroup",bb);
                        if(bb==null){
                            i.putExtra("belongs_to_group", 0);
                        }
                        ActivityOptionsCompat options = ActivityOptionsCompat.
                                makeSceneTransitionAnimation((Activity) context, allAlarmRecyclerViewHolder.time, "time");
                        startActivity(i, options.toBundle());
                    }
                    if (!atLeastOneSelected) {
                        fabDeleteAlarm.animate().alpha(0).setDuration(ANIMATION_DURATION).start();
                        fabCancel.animate().translationX(0).translationY(0).setDuration(ANIMATION_DURATION).rotationBy(-45).start();
                        final Handler handler = new Handler();

                        final Runnable r = new Runnable() {
                            public void run() {
                                fabDeleteAlarm.setVisibility(View.GONE);
                                fabCancel.setVisibility(View.GONE);
                                fabAddAlarm.setVisibility(View.VISIBLE);
                            }
                        };

                        handler.postDelayed(r, ANIMATION_DURATION+EXTRA_TIME_ANIMATION);

                        oneTimeLongTapped = false;
                    }
                }
            });

            allAlarmRecyclerViewHolder.allCard.setOnLongClickListener(new View.OnLongClickListener() {
                @SuppressLint({"RestrictedApi", "ResourceAsColor"})
                @Override
                public boolean onLongClick(View v) {
                    if (!oneTimeLongTapped) {
                        atLeastOneSelected = true;
                        model.isSelected = (true);
                        numberOfGroupsSelected++;
                        animateColorChanging(allAlarmRecyclerViewHolder.daysToRing,Color.GRAY,getResources().getColor(R.color.white),0);
                        animateColorChanging(allAlarmRecyclerViewHolder.groupName,getResources().getColor(R.color.pureBlack),getResources().getColor(R.color.white),0);
                        animateColorChanging(allAlarmRecyclerViewHolder.time,getResources().getColor(R.color.pureBlack),getResources().getColor(R.color.white),0);
                        allAlarmRecyclerViewHolder.rmsSwitch.setVisibility(View.INVISIBLE);
                        animateColorChanging(allAlarmRecyclerViewHolder.allColor,getResources().getColor(R.color.white),getResources().getColor(R.color.selectedGroupGray),0);
                        fabDeleteAlarm.animate().translationX(-100).setDuration(ANIMATION_DURATION).translationY(25).alpha(1).start();
                        fabCancel.animate().translationX(100).translationY(25).alpha(1).setDuration(ANIMATION_DURATION).rotationBy(45).start();
                        fabDeleteAlarm.setVisibility(View.VISIBLE);
                        fabCancel.setVisibility(View.VISIBLE);
                        fabAddAlarm.setVisibility(View.GONE);
                        intString.add(i);
                        oneTimeLongTapped = true;
                    }
                    return true;
                }
            });

        }

        @Override
        public int getItemCount() {
            return s.size();
        }

        public class AllAlarmRecyclerViewHolder extends RecyclerView.ViewHolder {

            TextView time, groupName, daysToRing;
            RMSwitch rmsSwitch;
            CardView allCard;
            LinearLayout allColor;

            public AllAlarmRecyclerViewHolder(@NonNull View itemView) {
                super(itemView);
                time = itemView.findViewById(R.id.time);
                groupName = itemView.findViewById(R.id.group_name);
                rmsSwitch = itemView.findViewById(R.id.rms_switch);
                allCard = itemView.findViewById(R.id.allCard);
                allColor = itemView.findViewById(R.id.allColor);
                daysToRing = itemView.findViewById(R.id.daysToRing);
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
                alarmIntent = new PendingIntent[dayys.length];
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

            Intent intent = new Intent(AllActivity.this, AlarmReciever.class);
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
                Log.d("timmmmm", "else tim: " + _alarm);
                _alarm = calendar.getTimeInMillis();
            }
            alarmIntent[k] = PendingIntent.getBroadcast(AllActivity.this, y, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmMgr.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, _alarm, alarmIntent[k]);
            }else{
                alarmMgr.setExact(AlarmManager.RTC_WAKEUP, _alarm, alarmIntent[k]);
            }

            Intent notifyIntent = new Intent(AllActivity.this, NotificationReciever.class);
            notifyIntent.putExtra("trimmedRequestId",y);
            PendingIntent pendingIntent = PendingIntent.getBroadcast
                    (AllActivity.this, y, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            AlarmManager alarmManager = (AlarmManager) AllActivity.this.getSystemService(Context.ALARM_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,  _alarm - (Utils.getNotificationDuration(getApplicationContext())), pendingIntent);
            }else{
                alarmManager.setExact(AlarmManager.RTC_WAKEUP,  _alarm - (Utils.getNotificationDuration(getApplicationContext())), pendingIntent);
            }

        }
    }

    @Override
    public void onBackPressed() {
        Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(a);
    }

    public void animateColorChanging(final View v, int colorFrom, int colorTo, final int VISIBILITY){

        ValueAnimator colorAnimate = ValueAnimator.ofObject( new ArgbEvaluator(),colorFrom,colorTo);
        colorAnimate.setDuration(250);
        colorAnimate.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {

                if(v instanceof TextView){
                    ((TextView) v).setTextColor((int) animation.getAnimatedValue());
                }else if(v instanceof RMSwitch){
                    v.setVisibility(VISIBILITY);
                }else if(v instanceof LinearLayout){
                    v.setBackgroundColor((int) animation.getAnimatedValue());
                }

            }
        });
        colorAnimate.start();

    }

}
