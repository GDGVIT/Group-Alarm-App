package com.varunsaini.android.ga_basictransitions;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityOptions;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.AppLaunchChecker;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.transition.Explode;
import android.transition.Fade;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.rm.rmswitch.RMSwitch;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Objects;

public class AllActivity extends AppCompatActivity {

    AlarmManager alarmMgr;
    PendingIntent alarmIntent;
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    SQLiteDatabase sqLiteDatabase;
    DatabaseHandler db;
    TextView allView,groupView;
    NestedScrollView scrollView;
    FloatingActionButton fabAddAlarm,fabDeleteAlarm,fabCancel;
    LinearLayout allColor;



    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all);

        Typeface tf = Typeface.createFromAsset(getAssets(),"fonts/Karla-Bold.ttf");

        allView = findViewById(R.id.all_all_text);
        groupView = findViewById(R.id.all_group_text);
        allView.setTypeface(tf);
        groupView.setTypeface(tf);
        scrollView = findViewById(R.id.scrollView);
        scrollView.fullScroll(ScrollView.FOCUS_UP);
        scrollView.smoothScrollTo(0,0);
        fabAddAlarm = findViewById(R.id.fabAddAlarm);
        fabDeleteAlarm = findViewById(R.id.fabDeleteAlarm);
        fabCancel = findViewById(R.id.fabCancel);
        allColor = findViewById(R.id.allColor);


        db = new DatabaseHandler(this);
        sqLiteDatabase = this.openOrCreateDatabase("Alarmm",MODE_PRIVATE,null);
        db.onCreate(sqLiteDatabase);
        db.getAllDatabaseDataInLogcat();

        ArrayList<AllAlarm> allAlarmArrayList = db.getAllActivtiyAlarmList();
        db.getAllDaysReqCodesInLogcat();



        CardView groupButton = findViewById(R.id.all_group);
        groupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AllActivity.this,GroupActivity.class));
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });


        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.custom_main_action_bar_layout);
        View view =getSupportActionBar().getCustomView();

        TextView title = view.findViewById(R.id.title);

        title.setTypeface(tf);

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


//
//        CardView groupButton = findViewById(R.id.all_group);
//        groupButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(AllActivity.this,GroupActivity.class));
//                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
//            }
//        });
//
//        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(AllActivity.this, recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
//            @Override
//            public void onItemClick(View view, int position) {
//
//                Intent i = new Intent(AllActivity.this,EditAlarmActivity.class);
//                view.findViewById(R.id.rms_switch).setOnClickListener();
//            }
//
//            @Override
//            public void onLongItemClick(View view, int position) {
//
//            }
//        }));

    }


    public void newAlarm(View view) {

        Intent i = new Intent(AllActivity.this,EditAlarmActivity.class);
        i.putExtra("belongs_to_group",0);
        startActivity(i);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

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

        public AllAlarmRecyclerViewAdapter(ArrayList<AllAlarm> s){
            this.s = s;
        }

        @NonNull
        @Override
        public AllAlarmRecyclerViewAdapter.AllAlarmRecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            LayoutInflater layoutInflater = LayoutInflater.from(viewGroup.getContext());
            View view = layoutInflater.inflate(R.layout.all_cards_layout,viewGroup,false);
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
            for (String e : ee){
                days += e.substring(0,1).toUpperCase()  + e.substring(1) + " ";
            }
                //            String ee = (s.get(i).daysToRing).replace("#",",").substring(0,s.get(i).daysToRing.length()-1);



            Typeface tf = Typeface.createFromAsset(assetManager,"fonts/Karla-Bold.ttf");
            Typeface tf1 = Typeface.createFromAsset(assetManager,"fonts/Karla.ttf");

            allAlarmRecyclerViewHolder.time.setTypeface(tf);
            allAlarmRecyclerViewHolder.daysToRing.setText(days);
            allAlarmRecyclerViewHolder.daysToRing.setTypeface(tf1);


            if(bb==null){
                allAlarmRecyclerViewHolder.groupName.setVisibility(View.GONE);
            }else{
                allAlarmRecyclerViewHolder.groupName.setText(bb);
                allAlarmRecyclerViewHolder.groupName.setTypeface(tf1);
            }

            allAlarmRecyclerViewHolder.time.setText(aa);
            if (cc==0){
                allAlarmRecyclerViewHolder.rmsSwitch.setChecked(false);
            }else{
                allAlarmRecyclerViewHolder.rmsSwitch.setChecked(true);
            }
//        allAlarmRecyclerViewHolder.time.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(context, "CLicked on "+ aa , Toast.LENGTH_SHORT).show();
//            }
//        });

            allAlarmRecyclerViewHolder.rmsSwitch.setOnClickListener(new View.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, AlarmReciever.class);
                    alarmMgr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
                    DatabaseHandler db = new DatabaseHandler(context);
                    if (allAlarmRecyclerViewHolder.rmsSwitch.isChecked()){
                        allAlarmRecyclerViewHolder.rmsSwitch.toggle();
                        ArrayList<Integer> integerArrayList = db.getThisAlarmIntents(Integer.valueOf(String.valueOf(dd).substring(3,9)));
                        for(Integer i : integerArrayList) {
                            PendingIntent alarmIntent = PendingIntent.getBroadcast(context, Integer.valueOf(i), intent, PendingIntent.FLAG_UPDATE_CURRENT);
                            alarmMgr.cancel(alarmIntent);

                        }
                        Toast.makeText(context, "Alarm Turned Off", Toast.LENGTH_SHORT).show();
                        db.updateAlarmState(dd,0);
                    }else{
                        allAlarmRecyclerViewHolder.rmsSwitch.toggle();
                        setAlarmsOnToggle(db.getDaysToRing(dd),dd);
                        Toast.makeText(context, "Alarm Turned On", Toast.LENGTH_SHORT).show();
                        db.updateAlarmState(dd,1);
                    }
                }
            });

            fabCancel.setOnClickListener(new View.OnClickListener() {
                @SuppressLint("RestrictedApi")
                @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                @Override
                public void onClick(View v) {
                    Toast.makeText(AllActivity.this, "Cancel Clicked", Toast.LENGTH_SHORT).show();
                        numberOfGroupsSelected=0;
                        finish();
                        startActivity(getIntent());
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                        atLeastOneSelected=false;
                    fabDeleteAlarm.setVisibility(View.GONE);
                    fabCancel.setVisibility(View.GONE);
                    fabAddAlarm.setVisibility(View.VISIBLE);

                }
            });

            fabDeleteAlarm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DatabaseHandler db = new DatabaseHandler(context);
                    Intent intent = new Intent(context, AlarmReciever.class);
                    alarmMgr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
                    Collections.sort(intString);
                    Log.d("dsd", "onClick: "+intString.size());
                    for(int i=intString.size()-1;i>=0;i--){
                        Log.d("fggf", "onClick: "+intString.get(i) + "||");

                    db.deleteAnAlarm(s.get(i).alarm_pending_req_code);
                    ArrayList<Integer> integerArrayList = db.getThisAlarmIntents(Integer.valueOf(String.valueOf(s.get(i).alarm_pending_req_code).substring(3,9)));
                    for(Integer ii : integerArrayList) {
                        PendingIntent alarmIntent = PendingIntent.getBroadcast(context, Integer.valueOf(ii), intent, PendingIntent.FLAG_UPDATE_CURRENT);
                        alarmMgr.cancel(alarmIntent);
                    }
                    db.removeDaysPendingReq(Integer.valueOf(String.valueOf(s.get(i).alarm_pending_req_code).substring(3,9)));
                        numberOfGroupsSelected--;
                        s.remove(intString.get(i).intValue());
                        notifyItemRemoved(intString.get(i));
                    }

                }
            });

            allAlarmRecyclerViewHolder.allCard.setOnClickListener(new View.OnClickListener() {
                @SuppressLint({"RestrictedApi", "ResourceAsColor"})
                @Override
                public void onClick(View v) {

                    if(!model.isSelected && atLeastOneSelected){
                        numberOfGroupsSelected++;
                        model.isSelected = (true);
                        allAlarmRecyclerViewHolder.allColor.setBackgroundResource(R.color.selectedGroupGray);
                        intString.add(i);
                    }else if(atLeastOneSelected && model.isSelected)
                    {
                        numberOfGroupsSelected--;
                        model.isSelected = (false);
                        int pos = intString.indexOf(i);
                        intString.remove(pos);
                        allAlarmRecyclerViewHolder.allColor.setBackgroundResource(R.color.white);
                        if(numberOfGroupsSelected==0){
                            atLeastOneSelected = false;
                        }
                    }else if(!atLeastOneSelected){
                        fabDeleteAlarm.setVisibility(View.GONE);
                        fabCancel.setVisibility(View.GONE);
                        fabAddAlarm.setVisibility(View.VISIBLE);
                    Intent i = new Intent(context,EditAlarmActivity.class);
                    i.putExtra("alarm_pending_req_code",dd);
                    Activity activity = (Activity)context;
                    activity.startActivity(i);
                    activity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    Toast.makeText(context, "CLicked on "+ dd , Toast.LENGTH_SHORT).show();
                    }if(!atLeastOneSelected){
                        fabDeleteAlarm.setVisibility(View.GONE);
                        fabCancel.setVisibility(View.GONE);
                        fabAddAlarm.setVisibility(View.VISIBLE);
                        oneTimeLongTapped= false;
                    }
//                    Log.d("sasa", "onClick: single tap "+ numberOfGroupsSelected + "||" + atLeastOneSelected + "//" + model.isSelected);
//                    Log.d("dfdf", "onClick: single Tap aboutAlarm"+aa+"||"+bb+"||"+cc+"||"+dd+"||"+ff+"||"+model.groupColor+"||"+model.isSelected);
                }
            });

//            allAlarmRecyclerViewHolder.allCard.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Intent i = new Intent(context,EditAlarmActivity.class);
//                    i.putExtra("alarm_pending_req_code",dd);
//                    Activity activity = (Activity)context;
//                    activity.startActivity(i);
//                    activity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
//                    Toast.makeText(context, "CLicked on "+ dd , Toast.LENGTH_SHORT).show();
//
//                }
//            });

//            allAlarmRecyclerViewHolder.allCard.setOnLongClickListener(new View.OnLongClickListener() {
//                @Override
//                public boolean onLongClick(View v) {
//                    DatabaseHandler db = new DatabaseHandler(context);
//                    Intent intent = new Intent(context, AlarmReciever.class);
//                    alarmMgr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
//                    db.deleteAnAlarm(dd);
//                    ArrayList<Integer> integerArrayList = db.getThisAlarmIntents(Integer.valueOf(String.valueOf(dd).substring(3,9)));
//                    for(Integer i : integerArrayList) {
//                        PendingIntent alarmIntent = PendingIntent.getBroadcast(context, Integer.valueOf(i), intent, PendingIntent.FLAG_UPDATE_CURRENT);
//                        alarmMgr.cancel(alarmIntent);
//                    }
//                    db.removeDaysPendingReq(Integer.valueOf(String.valueOf(dd).substring(3,9)));
////                AlarmManager alarmMgr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
////                PendingIntent alarmIntent = PendingIntent.getBroadcast(context, dd, intent, PendingIntent.FLAG_UPDATE_CURRENT);
////                Log.d("dismissAlarm", "dismissAlarm: "+AlarmReciever.request_id);
////                alarmMgr.cancel(alarmIntent);
//
//                    s.remove(allAlarmRecyclerViewHolder.getAdapterPosition());
//                    notifyItemRemoved(allAlarmRecyclerViewHolder.getAdapterPosition());
//                    return true;
//
//                }
//            });

            allAlarmRecyclerViewHolder.allCard.setOnLongClickListener(new View.OnLongClickListener() {
                @SuppressLint({"RestrictedApi", "ResourceAsColor"})
                @Override
                public boolean onLongClick(View v) {
                    if(!oneTimeLongTapped) {
                        atLeastOneSelected = true;
                        model.isSelected = (true);
                        numberOfGroupsSelected++;
                allAlarmRecyclerViewHolder.allColor.setBackgroundResource(R.color.selectedGroupGray);
                        fabDeleteAlarm.setVisibility(View.VISIBLE);
                        fabCancel.setVisibility(View.VISIBLE);
                        fabAddAlarm.setVisibility(View.GONE);
                        intString.add(i);
                        oneTimeLongTapped = true;
                    }

//                groupRecyclerViewHolder.groupColor.setBackgroundResource(R.color.selectedGroupGray);
                    return true;
                }
            });

        }

        @Override
        public int getItemCount() {
            return s.size();
        }

        public class AllAlarmRecyclerViewHolder extends RecyclerView.ViewHolder {

            TextView time,groupName,daysToRing;
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
                ArrayList<Integer> integerArrayList = db.getThisAlarmIntents(Integer.valueOf(String.valueOf(alarm_pending_req_code).substring(3,9)));
                for (Integer i : integerArrayList) {
                    PendingIntent alarmIntent = PendingIntent.getBroadcast(context, Integer.valueOf(i), intent, PendingIntent.FLAG_UPDATE_CURRENT);
                    alarmMgr.cancel(alarmIntent);
                }

                for (String d : dayys) {
                    if (d.equals("mon")) {
                        String x = String.valueOf(alarm_pending_req_code);
                        int y = Integer.valueOf("111"+x.substring(3,9) );
                        calendar.set(Calendar.DAY_OF_WEEK, 2);
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
                    } else if (d.equals("tue")) {
                        String x = String.valueOf(alarm_pending_req_code);
                        int y = Integer.valueOf("222"+x.substring(3,9));
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
                        int y = Integer.valueOf("333"+x.substring(3,9));
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
                        int y = Integer.valueOf("444"+x.substring(3,9));
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
                        int y = Integer.valueOf("555"+x.substring(3,9));
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
                        int y = Integer.valueOf("666"+x.substring(3,9));
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
                        int y = Integer.valueOf("777"+x.substring(3,9));
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

    @Override
    public void onBackPressed() {
        Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(a);
    }
}
