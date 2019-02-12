package com.varunsaini.android.ga_basictransitions;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AlertDialogLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.rm.rmswitch.RMSwitch;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;

public class AboutGroupActivity extends AppCompatActivity {
    //    tools:showIn="@layout/activity_about_group"
    AlarmManager alarmMgr;
    PendingIntent alarmIntent;

    int mGroupColor = 1;
    RecyclerView recyclerView;
    FloatingActionButton  fabDeleteAlarm, fabCancel;
    com.github.clans.fab.FloatingActionButton fabAddAlarm;
    RecyclerView.LayoutManager layoutManager;
    SQLiteDatabase sqLiteDatabase;
    DatabaseHandler db;
    EditText groupNameEditext;
    static String groupName;
    String groupNameOnGroupOpen = "";
    CardView colorPurple, colorGreen, colorPink;
    LinearLayout fullCardLinLayout;
    TextView titleActionBar;
    ImageView backActionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_group);

        colorPurple = findViewById(R.id.colorPurple);
        colorGreen = findViewById(R.id.colorGreen);
        colorPink = findViewById(R.id.colorPink);
        fullCardLinLayout = findViewById(R.id.full_card_lin_layout);

        fabAddAlarm = findViewById(R.id.fabAddAlarm);
        fabDeleteAlarm = findViewById(R.id.fabDeleteAlarm);
        fabCancel = findViewById(R.id.fabCancel);

        colorPurple.setPreventCornerOverlap(false);
        colorGreen.setPreventCornerOverlap(false);
        colorPink.setPreventCornerOverlap(false);

        colorPurple.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setCardBackgroundGradient(1);
                mGroupColor = 1;
            }
        });

        colorGreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setCardBackgroundGradient(2);
                mGroupColor = 2;
            }
        });

        colorPink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setCardBackgroundGradient(3);
                mGroupColor = 3;
            }
        });


        db = new DatabaseHandler(this);
        sqLiteDatabase = this.openOrCreateDatabase("Alarmm", MODE_PRIVATE, null);
        db.onCreate(sqLiteDatabase);
        db.getAllDatabaseDataInLogcat();

//        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
//        getSupportActionBar().setDisplayShowCustomEnabled(true);
//        getSupportActionBar().setCustomView(R.layout.custom_2_action_bar_layout);
//        View view = getSupportActionBar().getCustomView();

        backActionBar = findViewById(R.id.backActionBar);
        titleActionBar = findViewById(R.id.titleActionBar);

        Typeface tf1 = Typeface.createFromAsset(getAssets(), "fonts/Karla-Bold.ttf");
        titleActionBar.setTypeface(tf1);
        titleActionBar.setText("Manage Group");

        backActionBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AboutGroupActivity.super.onBackPressed();
            }
        });


        groupNameEditext = findViewById(R.id.group_name);
        groupNameEditext.setTypeface(tf1);
        groupName = getIntent().getStringExtra("groupName");
        if (groupName != null) {
            groupNameEditext.setText(groupName);
            groupNameOnGroupOpen = groupName;
            mGroupColor = db.getGroupColor(groupName);
            int groupColor = db.getGroupColor(groupName);
            setCardBackgroundGradient(groupColor);
            ArrayList<GroupInfo> groupInfoArrayList = db.getAllGroupInfo(groupName);
            recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view_all_alarms_in_a_group);
            recyclerView.setHasFixedSize(true);

            layoutManager = new LinearLayoutManager(this);
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.setAdapter(new AboutGroupRecyclerViewAdapter(groupInfoArrayList));
            recyclerView.setHasFixedSize(true);
            recyclerView.setItemViewCacheSize(20);

        }

    }

    public void editAlarm(View v) {
        db.changeGroupColor(groupName, mGroupColor);
        Intent i = new Intent(AboutGroupActivity.this, EditAlarmActivity.class);
        startActivity(i);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    public void newAlarm(View view) {
        db.changeGroupColor(groupName, mGroupColor);
        ArrayList<String> allGroupNames = db.getAllGroupNames();
        allGroupNames.remove(groupName);
        String editText = groupNameEditext.getText().toString();
        int groupNameFoundInDatabase = 0;
        for (int i = 0; i < allGroupNames.size(); i++) {
            if (editText.equals(allGroupNames.get(i))) {
                groupNameFoundInDatabase = 1;
            }
        }
        if (!editText.equals("") && groupNameFoundInDatabase == 0 && (!editText.contains("'") || !editText.contains("'") || !editText.contains(""))) {
            Intent i = new Intent(AboutGroupActivity.this, EditAlarmActivity.class);
            i.putExtra("nameOfGroup", groupNameEditext.getText().toString());
            i.putExtra("colorOfGroup", mGroupColor);

            startActivity(i);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        } else if (editText.equals("")) {
            Toast.makeText(this, "Give a Group Name first", Toast.LENGTH_SHORT).show();
        } else if (groupNameFoundInDatabase == 1) {
            Toast.makeText(this, "Group Name Already Exists", Toast.LENGTH_SHORT).show();
        } else if (editText.contains("'") || editText.contains("'") || editText.contains("")) {
            Toast.makeText(this, "Group name can't contain Special Characters", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        int groupNameFoundInDatabase = 0;
        String editText = groupNameEditext.getText().toString();
        ArrayList<String> allGroupNames = db.getAllGroupNames();
        allGroupNames.remove(groupName);
        for (int i = 0; i < allGroupNames.size(); i++) {
            if (editText.equals(allGroupNames.get(i))) {
                groupNameFoundInDatabase = 1;
            }
        }
        if (!editText.equals("") && groupNameFoundInDatabase == 0 && (!editText.contains("'") || !editText.contains(""))) {
            db.changeGroupColor(groupName, mGroupColor);
            db.changeGroupName(groupName, editText);
            startActivity(new Intent(this, GroupActivity.class));
        } else if (editText.equals("") && !groupNameOnGroupOpen.equals("")) {
            Toast.makeText(this, "Give a Group Name first", Toast.LENGTH_SHORT).show();
        } else if (groupNameFoundInDatabase == 1) {
            Toast.makeText(this, "Group Name Already Exists", Toast.LENGTH_SHORT).show();
        } else if (groupNameOnGroupOpen.equals("")) {
            startActivity(new Intent(this, GroupActivity.class));
        } else if ((editText.contains("'") || editText.contains(""))) {
            Toast.makeText(this, "Group name can't contain Special Characters", Toast.LENGTH_SHORT).show();
        }
    }

    void setCardBackgroundGradient(int groupColor) {
        if (groupColor == 1) {
            fullCardLinLayout.setBackgroundResource(R.drawable.list_grad_purple);
        } else if (groupColor == 2) {
            fullCardLinLayout.setBackgroundResource(R.drawable.list_grad_green);
        } else if (groupColor == 3) {
            fullCardLinLayout.setBackgroundResource(R.drawable.list_grad_pink);
        } else if (groupColor == 4) {

        } else if (groupColor == 5) {

        } else if (groupColor == 6) {

        } else if (groupColor == 7) {

        }
    }

    public class AboutGroupRecyclerViewAdapter extends RecyclerView.Adapter<AboutGroupRecyclerViewAdapter.AboutGroupRecyclerViewHolder> {

        ArrayList<GroupInfo> s;
        AssetManager assetManager;
        Context context;
        SQLiteDatabase sqLiteDatabase;
        AlarmManager alarmMgr;
        private PendingIntent[] alarmIntent;
        DatabaseHandler db;
        int numberOfGroupsSelected = 0;
        public boolean atLeastOneSelected = false;
        public ArrayList<Integer> intString = new ArrayList<>();
        boolean oneTimeLongTapped = false;


        public AboutGroupRecyclerViewAdapter(ArrayList<GroupInfo> s) {
            this.s = s;
        }

        @NonNull
        @Override
        public AboutGroupRecyclerViewAdapter.AboutGroupRecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            LayoutInflater layoutInflater = LayoutInflater.from(viewGroup.getContext());
            View view = layoutInflater.inflate(R.layout.group_info_card_layout, viewGroup, false);
            assetManager = viewGroup.getContext().getAssets();
            context = viewGroup.getContext();


            return new AboutGroupRecyclerViewAdapter.AboutGroupRecyclerViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull final AboutGroupRecyclerViewAdapter.AboutGroupRecyclerViewHolder aboutGroupRecyclerViewHolder, final int i) {

            final String aa = s.get(i).time;
            int bb = s.get(i).alarm_state;
            final int cc = s.get(i).alarm_pending_req_code;

            db = new DatabaseHandler(context);
            sqLiteDatabase = context.openOrCreateDatabase("Alarmm", MODE_PRIVATE, null);

            aboutGroupRecyclerViewHolder.time.setText(aa);
            Typeface tf = Typeface.createFromAsset(assetManager, "fonts/Karla-Bold.ttf");
            aboutGroupRecyclerViewHolder.time.setTypeface(tf);

            if (bb == 0) {
                aboutGroupRecyclerViewHolder.rmsSwitch.setChecked(false);
            } else {
                aboutGroupRecyclerViewHolder.rmsSwitch.setChecked(true);
            }


            aboutGroupRecyclerViewHolder.rmsSwitch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, AlarmReciever.class);
                    alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                    final DatabaseHandler db = new DatabaseHandler(context);
                    if (aboutGroupRecyclerViewHolder.rmsSwitch.isChecked()) {
                        aboutGroupRecyclerViewHolder.rmsSwitch.toggle();
                        ArrayList<Integer> integerArrayList = db.getThisAlarmIntents(Integer.valueOf(String.valueOf(cc).substring(3, 9)));
                        for (Integer i : integerArrayList) {
                            PendingIntent alarmIntent = PendingIntent.getBroadcast(context, Integer.valueOf(i), intent, PendingIntent.FLAG_UPDATE_CURRENT);
                            alarmMgr.cancel(alarmIntent);

                        }
                        Toast.makeText(context, "Alarm Turned Off", Toast.LENGTH_SHORT).show();
                        db.updateAlarmState(cc, 0);
                    } else {
                        if (db.getGroupNameByRequestId(cc) != null && db.getGroupStateByGroupName(db.getGroupNameByRequestId(cc)) == 0) {

                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            builder.setMessage("You want to turn on the alarm ? The corressponding group will also be turned on automatically")
                                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            aboutGroupRecyclerViewHolder.rmsSwitch.toggle();
                                            setAlarmsOnToggle(db.getDaysToRing(cc), cc);
                                            Toast.makeText(context, "Alarm Turned On", Toast.LENGTH_SHORT).show();
                                            db.updateAlarmState(cc, 1);

                                            db.updateGroupState(db.getGroupNameByRequestId(cc), 1);
                                            ArrayList<GroupInfo> groupInfoArrayList = db.getAllGroupInfo(db.getGroupNameByRequestId(cc));
                                            for (int i = 0; i < groupInfoArrayList.size(); i++) {
                                                if (groupInfoArrayList.get(i).alarm_previous_state == 1) {
                                                    setAlarmsOnToggle(db.getDaysToRing(groupInfoArrayList.get(i).alarm_pending_req_code), groupInfoArrayList.get(i).alarm_pending_req_code);
                                                    db.updateAlarmState(groupInfoArrayList.get(i).alarm_pending_req_code, 1);
                                                }
                                                db.changePreviousAlarmState(groupInfoArrayList.get(i).alarm_pending_req_code, -1);
                                            }
                                            Toast.makeText(context, "Group Turned On", Toast.LENGTH_SHORT).show();

                                        }
                                    })
                                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {

                                        }
                                    });
                            builder.create().show();
//
//                            allAlarmRecyclerViewHolder.rmsSwitch.toggle();
//                            setAlarmsOnToggle(db.getDaysToRing(dd), dd);
//                            Toast.makeText(context, "Alarm Turned On", Toast.LENGTH_SHORT).show();
//                            db.updateAlarmState(dd, 1);
                        } else {
                            aboutGroupRecyclerViewHolder.rmsSwitch.toggle();
                            setAlarmsOnToggle(db.getDaysToRing(cc), cc);
                            Toast.makeText(context, "Alarm Turned On", Toast.LENGTH_SHORT).show();
                            db.updateAlarmState(cc, 1);
                        }
                    }
                }
            });

            ///////////////////////////////
            fabCancel.setOnClickListener(new View.OnClickListener() {
                @SuppressLint("RestrictedApi")
                @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                @Override
                public void onClick(View v) {
                    Toast.makeText(AboutGroupActivity.this, "Cancel Clicked", Toast.LENGTH_SHORT).show();
                    numberOfGroupsSelected = 0;
                    finish();
                    startActivity(getIntent());
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    atLeastOneSelected = false;
                    fabDeleteAlarm.setVisibility(View.GONE);
                    fabCancel.setVisibility(View.GONE);
                    fabAddAlarm.setVisibility(View.VISIBLE);
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
                        ArrayList<Integer> integerArrayList = db.getThisAlarmIntents(Integer.valueOf(String.valueOf(s.get(intString.get(i)).alarm_pending_req_code).substring(3, 9)));
                        for (Integer ii : integerArrayList) {
                            PendingIntent alarmIntent = PendingIntent.getBroadcast(context, Integer.valueOf(ii), intent, PendingIntent.FLAG_UPDATE_CURRENT);
                            alarmMgr.cancel(alarmIntent);
                        }
                        db.removeDaysPendingReq(Integer.valueOf(String.valueOf(s.get(intString.get(i)).alarm_pending_req_code).substring(3, 9)));
                        s.remove(intString.get(i).intValue());
                        notifyItemRemoved(intString.get(i));
                    }
                    numberOfGroupsSelected = 0;
                    atLeastOneSelected = false;
                    fabDeleteAlarm.setVisibility(View.GONE);
                    fabCancel.setVisibility(View.GONE);
                    fabAddAlarm.setVisibility(View.VISIBLE);

                }
            });

            aboutGroupRecyclerViewHolder.allCard.setOnClickListener(new View.OnClickListener() {
                @SuppressLint({"RestrictedApi", "ResourceAsColor"})
                @Override
                public void onClick(View v) {

                    if ( atLeastOneSelected && !s.get(i).isSelected) {
                        numberOfGroupsSelected++;
                        s.get(i).isSelected = (true);
                        aboutGroupRecyclerViewHolder.allColor.setBackgroundResource(R.color.selectedGroupGray);
                        intString.add(i);
                    } else if (atLeastOneSelected && s.get(i).isSelected) {
                        numberOfGroupsSelected--;
                        s.get(i).isSelected = (false);
                        int pos = intString.indexOf(i);
                        intString.remove(pos);
                        aboutGroupRecyclerViewHolder.allColor.setBackgroundResource(R.color.white);
                        if (numberOfGroupsSelected == 0) {
                            atLeastOneSelected = false;
                        }
                    } else if (!atLeastOneSelected) {
                        fabDeleteAlarm.setVisibility(View.GONE);
                        fabCancel.setVisibility(View.GONE);
                        fabAddAlarm.setVisibility(View.VISIBLE);
                        Intent i = new Intent(context, EditAlarmActivity.class);
                        i.putExtra("alarm_pending_req_code", cc);
//                        if(bb==null){
//                            i.putExtra("belongs_to_group", 0);
//                        }
                        Activity activity = (Activity) context;
                        activity.startActivity(i);
                        activity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                        Toast.makeText(context, "CLicked on " + cc, Toast.LENGTH_SHORT).show();
                    }
                    if (!atLeastOneSelected) {
                        fabDeleteAlarm.setVisibility(View.GONE);
                        fabCancel.setVisibility(View.GONE);
                        fabAddAlarm.setVisibility(View.VISIBLE);
                        oneTimeLongTapped = false;
                    }
                }
            });

            aboutGroupRecyclerViewHolder.allCard.setOnLongClickListener(new View.OnLongClickListener() {
                @SuppressLint({"RestrictedApi", "ResourceAsColor"})
                @Override
                public boolean onLongClick(View v) {
                    if (!oneTimeLongTapped) {
                        atLeastOneSelected = true;
                        s.get(i).isSelected = (true);
                        numberOfGroupsSelected++;
                        aboutGroupRecyclerViewHolder.allColor.setBackgroundResource(R.color.selectedGroupGray);
                        fabDeleteAlarm.setVisibility(View.VISIBLE);
                        fabCancel.setVisibility(View.VISIBLE);
                        fabAddAlarm.setVisibility(View.GONE);
                        intString.add(i);
                        oneTimeLongTapped = true;
                    }
                    return true;
                }
            });

            ////////////////////////////////////

//            aboutGroupRecyclerViewHolder.allCard.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Intent i = new Intent(context, EditAlarmActivity.class);
//                    i.putExtra("alarm_pending_req_code", cc);
//                    Activity activity = (Activity) context;
//                    db.changeGroupColor(AboutGroupActivity.groupName, db.getGroupColor(AboutGroupActivity.groupName));
//                    activity.startActivity(i);
//                    activity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
//                    Toast.makeText(context, "CLicked on " + cc, Toast.LENGTH_SHORT).show();
//
//                }
//            });
//
//            aboutGroupRecyclerViewHolder.allCard.setOnLongClickListener(new View.OnLongClickListener() {
//                @Override
//                public boolean onLongClick(View v) {
//
//                    DatabaseHandler db = new DatabaseHandler(context);
//                    Intent intent = new Intent(context, AlarmReciever.class);
//                    AlarmManager alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
//                    db.deleteAnAlarm(cc);
//                    ArrayList<Integer> integerArrayList = db.getThisAlarmIntents(Integer.valueOf(String.valueOf(cc).substring(3, 9)));
//                    for (Integer i : integerArrayList) {
//                        PendingIntent alarmIntent = PendingIntent.getBroadcast(context, Integer.valueOf(i), intent, PendingIntent.FLAG_UPDATE_CURRENT);
//                        alarmMgr.cancel(alarmIntent);
//                    }
//                    db.removeDaysPendingReq(Integer.valueOf(String.valueOf(cc).substring(3, 9)));
//                    s.remove(aboutGroupRecyclerViewHolder.getAdapterPosition());
//                    notifyItemRemoved(aboutGroupRecyclerViewHolder.getAdapterPosition());
//                    return true;
//
//                }
//            });

        }


        @Override
        public int getItemCount() {
            return s.size();
        }

        public class AboutGroupRecyclerViewHolder extends RecyclerView.ViewHolder {

            TextView time;
            RMSwitch rmsSwitch;
            CardView allCard;
            LinearLayout allColor;

            public AboutGroupRecyclerViewHolder(@NonNull View itemView) {
                super(itemView);
                time = itemView.findViewById(R.id.time);
                rmsSwitch = itemView.findViewById(R.id.rmsSwitch);
                allCard = itemView.findViewById(R.id.allCard);
                allColor = itemView.findViewById(R.id.allColor);

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
                ArrayList<Integer> integerArrayList = db.getThisAlarmIntents(Integer.valueOf(String.valueOf(alarm_pending_req_code).substring(3, 9)));
                for (Integer i : integerArrayList) {
                    PendingIntent alarmIntent = PendingIntent.getBroadcast(context, Integer.valueOf(i), intent, PendingIntent.FLAG_UPDATE_CURRENT);
                    alarmMgr.cancel(alarmIntent);
                }

                for (String d : dayys) {
                    if (d.equals("mon")) {
                        String x = String.valueOf(alarm_pending_req_code);
                        int y = Integer.valueOf("111" + x.substring(3, 9));
                        calendar.set(Calendar.DAY_OF_WEEK, 2);
                        Log.d("xzx", "setAlarmOn: " + y);
                        intent.putExtra("request_code", y);
                        if (calendar.getTimeInMillis() <= now.getTimeInMillis()) {
                            _alarm = calendar.getTimeInMillis() + (24 * 60 * 60 * 1000 * 7);
                        } else {
                            _alarm = calendar.getTimeInMillis();
                        }
                        alarmIntent[k] = PendingIntent.getBroadcast(context, y, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                        alarmMgr.setExact(AlarmManager.RTC_WAKEUP, _alarm, alarmIntent[k]);
                        allRequests[k] = y;
                        k++;
                    } else if (d.equals("tue")) {
                        String x = String.valueOf(alarm_pending_req_code);
                        int y = Integer.valueOf("222" + x.substring(3, 9));
                        calendar.set(Calendar.DAY_OF_WEEK, 3);
                        Log.d("xzx", "setAlarmOn: " + y);
                        intent.putExtra("request_code", y);
                        if (calendar.getTimeInMillis() <= now.getTimeInMillis()) {
                            Log.d("if", "setAlarmOn: " + "inside if");
                            _alarm = calendar.getTimeInMillis() + (24 * 60 * 60 * 1000 * 7);
                        } else {
                            Log.d("else", "setAlarmOn: " + "inside else");
                            _alarm = calendar.getTimeInMillis();
                        }
                        alarmIntent[k] = PendingIntent.getBroadcast(context, y, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                        alarmMgr.setExact(AlarmManager.RTC_WAKEUP, _alarm, alarmIntent[k]);
                        allRequests[k] = y;
                        k++;
                    } else if (d.equals("wed")) {
                        String x = String.valueOf(alarm_pending_req_code);
                        int y = Integer.valueOf("333" + x.substring(3, 9));
                        calendar.set(Calendar.DAY_OF_WEEK, 4);
                        Log.d("xzx", "setAlarmOn: " + y);
                        intent.putExtra("request_code", y);
                        if (calendar.getTimeInMillis() <= now.getTimeInMillis())
                            _alarm = calendar.getTimeInMillis() + (24 * 60 * 60 * 1000 * 7);
                        else
                            _alarm = calendar.getTimeInMillis();
                        alarmIntent[k] = PendingIntent.getBroadcast(context, y, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                        alarmMgr.setExact(AlarmManager.RTC_WAKEUP, _alarm, alarmIntent[k]);
                        allRequests[k] = y;
                        k++;
                    } else if (d.equals("thurs")) {
                        String x = String.valueOf(alarm_pending_req_code);
                        int y = Integer.valueOf("444" + x.substring(3, 9));
                        calendar.set(Calendar.DAY_OF_WEEK, 5);
                        Log.d("xzx", "setAlarmOn: " + y);
                        intent.putExtra("request_code", y);
                        if (calendar.getTimeInMillis() <= now.getTimeInMillis()) {
                            Log.d("if", "setAlarmOn: " + "inside if");
                            _alarm = calendar.getTimeInMillis() + (24 * 60 * 60 * 1000 * 7);
                        } else {
                            Log.d("else", "setAlarmOn: " + "inside else");
                            _alarm = calendar.getTimeInMillis();
                        }
                        alarmIntent[k] = PendingIntent.getBroadcast(context, y, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                        alarmMgr.setExact(AlarmManager.RTC_WAKEUP, _alarm, alarmIntent[k]);
                        allRequests[k] = y;
                        k++;
                    } else if (d.equals("fri")) {
                        String x = String.valueOf(alarm_pending_req_code);
                        int y = Integer.valueOf("555" + x.substring(3, 9));
                        calendar.set(Calendar.DAY_OF_WEEK, 6);
                        Log.d("xzx", "setAlarmOn: " + y);
                        intent.putExtra("request_code", y);
                        if (calendar.getTimeInMillis() <= now.getTimeInMillis())
                            _alarm = calendar.getTimeInMillis() + (24 * 60 * 60 * 1000 * 7);
                        else
                            _alarm = calendar.getTimeInMillis();
                        alarmIntent[k] = PendingIntent.getBroadcast(context, y, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                        alarmMgr.setExact(AlarmManager.RTC_WAKEUP, _alarm, alarmIntent[k]);
                        allRequests[k] = y;
                        k++;
                    } else if (d.equals("sat")) {
                        String x = String.valueOf(alarm_pending_req_code);
                        int y = Integer.valueOf("666" + x.substring(3, 9));
                        calendar.set(Calendar.DAY_OF_WEEK, 7);
                        Log.d("xzx", "setAlarmOn: " + y);
                        intent.putExtra("request_code", y);
                        if (calendar.getTimeInMillis() <= now.getTimeInMillis())
                            _alarm = calendar.getTimeInMillis() + (24 * 60 * 60 * 1000 * 7);
                        else
                            _alarm = calendar.getTimeInMillis();
                        alarmIntent[k] = PendingIntent.getBroadcast(context, y, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                        alarmMgr.setExact(AlarmManager.RTC_WAKEUP, _alarm, alarmIntent[k]);
                        allRequests[k] = y;
                        k++;
                    } else if (d.equals("sun")) {
                        String x = String.valueOf(alarm_pending_req_code);
                        int y = Integer.valueOf("777" + x.substring(3, 9));
                        calendar.set(Calendar.DAY_OF_WEEK, 1);
                        Log.d("xzx", "setAlarmOn: " + y);
                        intent.putExtra("request_code", y);
                        Log.d("calendar.getTimeInMil", "setAlarmOn: calendar.getTimeInMillis()" + calendar.getTimeInMillis());
                        Log.d("now.getTimeInMill", "setAlarmOn: now.getTimeInMillis()" + now.getTimeInMillis());
                        if (calendar.getTimeInMillis() <= now.getTimeInMillis()) {
                            Log.d("if", "setAlarmOn: " + "inside if");
                            _alarm = calendar.getTimeInMillis() + (24 * 60 * 60 * 1000 * 7);
                        } else {
                            Log.d("else", "setAlarmOn: " + "inside else");
                            _alarm = calendar.getTimeInMillis();
                        }
                        alarmIntent[k] = PendingIntent.getBroadcast(context, y, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                        alarmMgr.setExact(AlarmManager.RTC_WAKEUP, _alarm, alarmIntent[k]);
                        allRequests[k] = y;
                        k++;
                    }
                }

            }
        }
    }


}
