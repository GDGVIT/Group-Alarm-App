package com.varunsaini.android.ga_basictransitions;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.rm.rmswitch.RMSwitch;

import java.util.ArrayList;

public class AboutGroupRecyclerViewAdapter extends RecyclerView.Adapter<AboutGroupRecyclerViewAdapter.AboutGroupRecyclerViewHolder> {

    ArrayList<GroupInfo> s;
    AssetManager assetManager;
    Context context;

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

        aboutGroupRecyclerViewHolder.time.setText(aa);

        if (bb==0){
            aboutGroupRecyclerViewHolder.rmsSwitch.setChecked(false);
        }else{
            aboutGroupRecyclerViewHolder.rmsSwitch.setChecked(true);
        }



        aboutGroupRecyclerViewHolder.rmsSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (aboutGroupRecyclerViewHolder.rmsSwitch.isChecked()){
                    aboutGroupRecyclerViewHolder.rmsSwitch.toggle();
                    AlarmManager alarmMgr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
                    Intent intent = new Intent(context, AlarmReciever.class);
                    PendingIntent alarmIntent = PendingIntent.getBroadcast(context, cc, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                    alarmMgr.cancel(alarmIntent);
                    Toast.makeText(context, "Alarm Turned Off", Toast.LENGTH_SHORT).show();
                    DatabaseHandler databaseHandler = new DatabaseHandler(context);
                    databaseHandler.updateAlarmState(cc,0);
                }
            }
        });

        aboutGroupRecyclerViewHolder.allCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context,EditAlarmActivity.class);
                i.putExtra("alarm_pending_req_code",cc);
                context.startActivity(i);
                Toast.makeText(context, "CLicked on "+ cc , Toast.LENGTH_SHORT).show();

            }
        });

        aboutGroupRecyclerViewHolder.allCard.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                DatabaseHandler db = new DatabaseHandler(context);
                db.deleteAnAlarm(cc);

                AlarmManager alarmMgr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
                Intent intent = new Intent(context, AlarmReciever.class);
                PendingIntent alarmIntent = PendingIntent.getBroadcast(context, cc, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                Log.d("dismissAlarm", "dismissAlarm: "+AlarmReciever.request_id);
                alarmMgr.cancel(alarmIntent);

                s.remove(i);
                notifyItemRemoved(i);
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


}