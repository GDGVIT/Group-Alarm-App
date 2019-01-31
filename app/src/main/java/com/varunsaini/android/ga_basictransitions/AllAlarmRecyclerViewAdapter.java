package com.varunsaini.android.ga_basictransitions;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.rm.rmswitch.RMSwitch;

import java.util.ArrayList;
import java.util.Calendar;

import static android.content.Context.MODE_PRIVATE;

public class AllAlarmRecyclerViewAdapter extends RecyclerView.Adapter<AllAlarmRecyclerViewAdapter.AllAlarmRecyclerViewHolder> {

    ArrayList<AllAlarm> s;
    AssetManager assetManager;
    Context context;
    AlarmManager alarmMgr;
    private PendingIntent[] alarmIntent;

    SQLiteDatabase sqLiteDatabase;
    DatabaseHandler db;

    public AllAlarmRecyclerViewAdapter(ArrayList<AllAlarm> s){
        this.s = s;
    }

    @NonNull
    @Override
    public AllAlarmRecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater layoutInflater = LayoutInflater.from(viewGroup.getContext());
        View view = layoutInflater.inflate(R.layout.all_cards_layout,viewGroup,false);
        assetManager = viewGroup.getContext().getAssets();
        context = viewGroup.getContext();

        return new AllAlarmRecyclerViewHolder(view);
    }

    public void onBindViewHolder(@NonNull final AllAlarmRecyclerViewHolder allAlarmRecyclerViewHolder, final int i) {

        final String aa = s.get(i).time;
        final String bb = s.get(i).groupName;
        int cc = s.get(i).isAlarmOn;
        final int dd = s.get(i).alarm_pending_req_code;


        Typeface tf = Typeface.createFromAsset(assetManager,"fonts/Karla-Bold.ttf");
        allAlarmRecyclerViewHolder.time.setTypeface(tf);

        if(bb==null){
            allAlarmRecyclerViewHolder.groupName.setVisibility(View.GONE);
        }else{
            allAlarmRecyclerViewHolder.groupName.setText(bb);
            Typeface tf1 = Typeface.createFromAsset(assetManager,"fonts/Karla.ttf");
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
                    ArrayList<Integer> integerArrayList = db.getThisAlarmIntents(Integer.valueOf(String.valueOf(dd).substring(0,7)));
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

        allAlarmRecyclerViewHolder.allCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context,EditAlarmActivity.class);
                i.putExtra("alarm_pending_req_code",dd);
                Activity activity = (Activity)context;
                activity.startActivity(i);
                activity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                Toast.makeText(context, "CLicked on "+ dd , Toast.LENGTH_SHORT).show();

            }
        });

        allAlarmRecyclerViewHolder.allCard.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                DatabaseHandler db = new DatabaseHandler(context);
                Intent intent = new Intent(context, AlarmReciever.class);
                alarmMgr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
                db.deleteAnAlarm(dd);
                ArrayList<Integer> integerArrayList = db.getThisAlarmIntents(Integer.valueOf(String.valueOf(dd).substring(0,7)));
                for(Integer i : integerArrayList) {
                    PendingIntent alarmIntent = PendingIntent.getBroadcast(context, Integer.valueOf(i), intent, PendingIntent.FLAG_UPDATE_CURRENT);
                    alarmMgr.cancel(alarmIntent);
                }
                db.removeDaysPendingReq(Integer.valueOf(String.valueOf(dd).substring(0,7)));
//                AlarmManager alarmMgr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
//                PendingIntent alarmIntent = PendingIntent.getBroadcast(context, dd, intent, PendingIntent.FLAG_UPDATE_CURRENT);
//                Log.d("dismissAlarm", "dismissAlarm: "+AlarmReciever.request_id);
//                alarmMgr.cancel(alarmIntent);

                s.remove(allAlarmRecyclerViewHolder.getAdapterPosition());
                notifyItemRemoved(allAlarmRecyclerViewHolder.getAdapterPosition());
                return true;

            }
        });

    }

    @Override
    public int getItemCount() {
        return s.size();
    }

    public class AllAlarmRecyclerViewHolder extends RecyclerView.ViewHolder {

        TextView time,groupName;
        RMSwitch rmsSwitch;
        CardView allCard;

        public AllAlarmRecyclerViewHolder(@NonNull View itemView) {
            super(itemView);
            time = itemView.findViewById(R.id.time);
            groupName = itemView.findViewById(R.id.group_name);
            rmsSwitch = itemView.findViewById(R.id.rms_switch);
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
            ArrayList<Integer> integerArrayList = db.getThisAlarmIntents(Integer.valueOf(String.valueOf(alarm_pending_req_code).substring(0, 7)));
            for (Integer i : integerArrayList) {
                PendingIntent alarmIntent = PendingIntent.getBroadcast(context, Integer.valueOf(i), intent, PendingIntent.FLAG_UPDATE_CURRENT);
                alarmMgr.cancel(alarmIntent);
            }

            for (String d : dayys) {
                if (d.equals("mon")) {
                    String x = String.valueOf(alarm_pending_req_code);
                    int y = Integer.valueOf(x.substring(0, 7) + "111");
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
                    int y = Integer.valueOf(x.substring(0, 7) + "222");
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
                    int y = Integer.valueOf(x.substring(0, 7) + "333");
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
                    int y = Integer.valueOf(x.substring(0, 7) + "444");
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
                    int y = Integer.valueOf(x.substring(0, 7) + "555");
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
                    int y = Integer.valueOf(x.substring(0, 7) + "666");
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
                    int y = Integer.valueOf(x.substring(0, 7) + "777");
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
