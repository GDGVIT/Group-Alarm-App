package com.varunsaini.android.ga_basictransitions;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.rm.rmswitch.RMSwitch;

import java.util.ArrayList;

public class GroupRecyclerViewAdapter extends RecyclerView.Adapter<GroupRecyclerViewAdapter.GroupRecyclerViewHolder>  {

    ArrayList<Group> s;
    AssetManager assetManager;
    Context context;

    public GroupRecyclerViewAdapter(ArrayList<Group> s){
        this.s = s;
    }

    @NonNull
    @Override
    public GroupRecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater layoutInflater = LayoutInflater.from(viewGroup.getContext());
        View view = layoutInflater.inflate(R.layout.group_cards_layout,viewGroup,false);
        assetManager = viewGroup.getContext().getAssets();
        context = viewGroup.getContext();

        return new GroupRecyclerViewHolder(view);
    }

    public void onBindViewHolder(@NonNull final GroupRecyclerViewAdapter.GroupRecyclerViewHolder groupRecyclerViewHolder, int i) {

        final String aa = s.get(i).groupName;
        String bb = s.get(i).oneAlarm;
        String cc = s.get(i).twoAlarm;
        String dd = s.get(i).threeAlarm;
        String ee = s.get(i).moreAlarm;
        int ff = s.get(i).isGroupOn;

        Typeface tf = Typeface.createFromAsset(assetManager,"fonts/Karla-Bold.ttf");
        groupRecyclerViewHolder.groupName.setTypeface(tf);

        groupRecyclerViewHolder.groupName.setText(aa);

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
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context,AboutGroupActivity.class);
                i.putExtra("groupName",aa);
                context.startActivity(i);
            }
        });

    }

    @Override
    public int getItemCount() {
        return s.size();
    }

    public class GroupRecyclerViewHolder extends RecyclerView.ViewHolder {

        TextView groupName,firstAlarm,secondAlarm,thirdAlarm,moreAlarm;
        RMSwitch rmsSwitch;
        CardView allCard;

        public GroupRecyclerViewHolder(@NonNull View itemView) {
            super(itemView);
            groupName = itemView.findViewById(R.id.groupName);
            firstAlarm = itemView.findViewById(R.id.firstAlarm);
            secondAlarm = itemView.findViewById(R.id.secondAlarm);
            thirdAlarm = itemView.findViewById(R.id.thirdAlarm);
            moreAlarm = itemView.findViewById(R.id.moreAlarm);
            rmsSwitch = itemView.findViewById(R.id.all_switch);
            allCard = itemView.findViewById(R.id.allCard);

        }
    }

}
