package com.dscvit.android.onealarm.adapters;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.rm.rmswitch.RMSwitch;
import com.dscvit.android.onealarm.misc.AlarmReciever;
import com.dscvit.android.onealarm.misc.DatabaseHandler;
import com.dscvit.android.onealarm.models.GroupInfo;
import com.dscvit.android.onealarm.R;
import com.dscvit.android.onealarm.activity.AboutGroupActivity;
import com.dscvit.android.onealarm.activity.EditAlarmActivity;

import java.util.ArrayList;
import java.util.Calendar;

import static android.content.Context.MODE_PRIVATE;

public class AboutGroupRecyclerViewAdapter extends RecyclerView.Adapter<AboutGroupRecyclerViewAdapter.AboutGroupRecyclerViewHolder> {

    ArrayList<GroupInfo> s;
    AssetManager assetManager;
    Context context;
    SQLiteDatabase sqLiteDatabase;
    AlarmManager alarmMgr;
    private PendingIntent[] alarmIntent;
    DatabaseHandler db;

    public AboutGroupRecyclerViewAdapter(ArrayList<GroupInfo> s){
        this.s = s;
    }

    @NonNull
    @Override
    public AboutGroupRecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater layoutInflater = LayoutInflater.from(viewGroup.getContext());
        View view = layoutInflater.inflate(R.layout.group_info_card_layout,viewGroup,false);
        assetManager = viewGroup.getContext().getAssets();
        context = viewGroup.getContext();


        return new AboutGroupRecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final AboutGroupRecyclerViewHolder aboutGroupRecyclerViewHolder, final int i) {

        final String aa = s.get(i).time;
        int bb = s.get(i).alarm_state;
        final int cc = s.get(i).alarm_pending_req_code;

        db = new DatabaseHandler(context);
        sqLiteDatabase = context.openOrCreateDatabase("Alarmm",MODE_PRIVATE,null);

        aboutGroupRecyclerViewHolder.time.setText(aa);
        Typeface tf = Typeface.createFromAsset(assetManager,"fonts/Karla-Bold.ttf");
        aboutGroupRecyclerViewHolder.time.setTypeface(tf);

        if (bb==0){
            aboutGroupRecyclerViewHolder.rmsSwitch.setChecked(false);
        }else{
            aboutGroupRecyclerViewHolder.rmsSwitch.setChecked(true);
        }



        aboutGroupRecyclerViewHolder.rmsSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (aboutGroupRecyclerViewHolder.rmsSwitch.isChecked()){
//                    aboutGroupRecyclerViewHolder.rmsSwitch.toggle();
//                    AlarmManager alarmMgr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
//                    Intent intent = new Intent(context, AlarmReciever.class);
//                    PendingIntent alarmIntent = PendingIntent.getBroadcast(context, cc, intent, PendingIntent.FLAG_UPDATE_CURRENT);
//                    alarmMgr.cancel(alarmIntent);
//                    Toast.makeText(context, "Alarm Turned Off", Toast.LENGTH_SHORT).show();
//                    DatabaseHandler databaseHandler = new DatabaseHandler(context);
//                    databaseHandler.updateAlarmState(cc,0);
//                }else{
//                    aboutGroupRecyclerViewHolder.rmsSwitch.toggle();
//                    setAlarmsOnToggle(db.getDaysToRing(cc),cc);
//                    Toast.makeText(context, "Alarm Turned On", Toast.LENGTH_SHORT).show();
//                    db.updateAlarmState(cc,1);
//                }
                Intent intent = new Intent(context, AlarmReciever.class);
                alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                final DatabaseHandler db = new DatabaseHandler(context);
                if (aboutGroupRecyclerViewHolder.rmsSwitch.isChecked()) {
                    aboutGroupRecyclerViewHolder.rmsSwitch.toggle();
                    ArrayList<Integer> integerArrayList = db.getThisAlarmIntents(Integer.valueOf(String.valueOf(cc).substring(3)));
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

                                        db.updateGroupState(db.getGroupNameByRequestId(cc),1);
                                        ArrayList<GroupInfo> groupInfoArrayList = db.getAllGroupInfo(db.getGroupNameByRequestId(cc));
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
//
//                            allAlarmRecyclerViewHolder.rmsSwitch.toggle();
//                            setAlarmsOnToggle(db.getDaysToRing(dd), dd);
//                            Toast.makeText(context, "Alarm Turned On", Toast.LENGTH_SHORT).show();
//                            db.updateAlarmState(dd, 1);
                    }else {
                        aboutGroupRecyclerViewHolder.rmsSwitch.toggle();
                        setAlarmsOnToggle(db.getDaysToRing(cc), cc);
                        Toast.makeText(context, "Alarm Turned On", Toast.LENGTH_SHORT).show();
                        db.updateAlarmState(cc, 1);
                    }
                }
            }
        });

        aboutGroupRecyclerViewHolder.allCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, EditAlarmActivity.class);
                i.putExtra("alarm_pending_req_code",cc);
                Activity activity = (Activity) context;
                db.changeGroupColor(AboutGroupActivity.groupName,db.getGroupColor(AboutGroupActivity.groupName));
                activity.startActivity(i);
                activity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                Toast.makeText(context, "CLicked on "+ cc , Toast.LENGTH_SHORT).show();

            }
        });

        aboutGroupRecyclerViewHolder.allCard.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                DatabaseHandler db = new DatabaseHandler(context);
                Intent intent = new Intent(context, AlarmReciever.class);
                AlarmManager alarmMgr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
                db.deleteAnAlarm(cc);
                ArrayList<Integer> integerArrayList = db.getThisAlarmIntents(Integer.valueOf(String.valueOf(cc).substring(3)));
                for(Integer i : integerArrayList) {
                    PendingIntent alarmIntent = PendingIntent.getBroadcast(context, Integer.valueOf(i), intent, PendingIntent.FLAG_UPDATE_CURRENT);
                    alarmMgr.cancel(alarmIntent);
                }
                db.removeDaysPendingReq(Integer.valueOf(String.valueOf(cc).substring(3,9)));
                s.remove(aboutGroupRecyclerViewHolder.getAdapterPosition());
                notifyItemRemoved(aboutGroupRecyclerViewHolder.getAdapterPosition());
                return true;

            }
        });

    }


    @Override
    public int getItemCount() {
        return s.size();
    }

    public class AboutGroupRecyclerViewHolder extends RecyclerView.ViewHolder {

        TextView time;
        RMSwitch rmsSwitch;
        CardView allCard;

        public AboutGroupRecyclerViewHolder(@NonNull View itemView) {
            super(itemView);
            time = itemView.findViewById(R.id.time);
            rmsSwitch = itemView.findViewById(R.id.rmsSwitch);
            allCard = itemView.findViewById(R.id.allCard);

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

