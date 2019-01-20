package com.varunsaini.android.ga_basictransitions;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
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

public class AllAlarmRecyclerViewAdapter extends RecyclerView.Adapter<AllAlarmRecyclerViewAdapter.AllAlarmRecyclerViewHolder> {

    ArrayList<AllAlarm> s;
    AssetManager assetManager;
    Context context;

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
        allAlarmRecyclerViewHolder.time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "CLicked on "+ aa , Toast.LENGTH_SHORT).show();
            }
        });

        allAlarmRecyclerViewHolder.rmsSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (allAlarmRecyclerViewHolder.rmsSwitch.isChecked()){
                    allAlarmRecyclerViewHolder.rmsSwitch.toggle();
                    AlarmManager alarmMgr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
                    Intent intent = new Intent(context, AlarmReciever.class);
                    PendingIntent alarmIntent = PendingIntent.getBroadcast(context, dd, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                    alarmMgr.cancel(alarmIntent);
                    Toast.makeText(context, "Alarm Turned Off", Toast.LENGTH_SHORT).show();
                    DatabaseHandler databaseHandler = new DatabaseHandler(context);
                    databaseHandler.updateAlarmState(dd,0);
                }
            }
        });

        allAlarmRecyclerViewHolder.allCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context,EditAlarmActivity.class);
                i.putExtra("alarm_pending_req_code",dd);
                context.startActivity(i);
                Toast.makeText(context, "CLicked on "+ dd , Toast.LENGTH_SHORT).show();

            }
        });

        allAlarmRecyclerViewHolder.allCard.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                DatabaseHandler db = new DatabaseHandler(context);
                db.deleteAnAlarm(dd);

                AlarmManager alarmMgr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
                Intent intent = new Intent(context, AlarmReciever.class);
                PendingIntent alarmIntent = PendingIntent.getBroadcast(context, dd, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                Log.d("dismissAlarm", "dismissAlarm: "+AlarmReciever.request_id);
                alarmMgr.cancel(alarmIntent);

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
}
