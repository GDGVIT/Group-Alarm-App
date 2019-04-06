package com.varunsaini.android.ga_basictransitions.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.rm.rmswitch.RMSwitch;
import com.varunsaini.android.ga_basictransitions.models.Group;
import com.varunsaini.android.ga_basictransitions.R;
import com.varunsaini.android.ga_basictransitions.activity.AboutGroupActivity;

import java.util.ArrayList;

public class GroupRecyclerViewAdapter extends RecyclerView.Adapter<GroupRecyclerViewAdapter.GroupRecyclerViewHolder>  {

    ArrayList<Group> s;
    AssetManager assetManager;
    Context context;
    public boolean atLeastOneSelected = false;
    int numberOfGroupsSelected = 0;

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

            }else if(gg==5){

            }else if(gg==6){

            }else if(gg==7){

            }
        }else{
            groupRecyclerViewHolder.groupColor.setBackgroundResource(R.color.selectedGroupGray);
        }



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
                    model.setSelected(true);
                    groupRecyclerViewHolder.groupColor.setBackgroundResource(R.color.selectedGroupGray);
                }else if(atLeastOneSelected && model.isSelected())
                {
                    numberOfGroupsSelected--;
                    model.setSelected(false);
                    if(model.groupColor==1){
                        groupRecyclerViewHolder.groupColor.setBackgroundResource(R.drawable.list_grad_purple);
                    }else if(model.groupColor==2){
                        groupRecyclerViewHolder.groupColor.setBackgroundResource(R.drawable.list_grad_green);
                    }else if(model.groupColor==3){
                        groupRecyclerViewHolder.groupColor.setBackgroundResource(R.drawable.list_grad_pink);
                    }else if(model.groupColor==4){

                    }else if(model.groupColor==5){

                    }else if(model.groupColor==6){

                    }else if(model.groupColor==7){

                    }
                    if(numberOfGroupsSelected==0){
                        atLeastOneSelected = false;
                    }
                }if(!atLeastOneSelected){
                    Intent i = new Intent(context, AboutGroupActivity.class);
                    i.putExtra("groupName", aa);
                    Activity activity = (Activity) context;
                    activity.startActivity(i);
                    activity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                }
                Log.d("sasa", "onClick: single tap "+ numberOfGroupsSelected + "||" + atLeastOneSelected + "//" + model.isSelected);
                Log.d("dfdf", "onClick: single Tap aboutAlarm"+aa+"||"+bb+"||"+cc+"||"+dd+"||"+ff+"||"+model.groupColor+"||"+model.isSelected);
//                if(GroupActivity.isGroupSelected==1){
//                    if(gg==1){
//                        groupRecyclerViewHolder.groupColor.setBackgroundResource(R.drawable.list_grad_purple);
//                    }else if(gg==2){
//                        groupRecyclerViewHolder.groupColor.setBackgroundResource(R.drawable.list_grad_green);
//                    }else if(gg==3){
//                        groupRecyclerViewHolder.groupColor.setBackgroundResource(R.drawable.list_grad_pink);
//                    }else if(gg==4){
//
//                    }else if(gg==5){
//
//                    }else if(gg==6){
//
//                    }else if(gg==7){
//
//                    }
//                    GroupActivity.isGroupSelected = 0;
//                }
//                }else {
//                    Intent i = new Intent(context, AboutGroupActivity.class);
//                    i.putExtra("groupName", aa);
//                    Activity activity = (Activity) context;
//                    activity.startActivity(i);
//                    activity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
//                }
            }
        });
//
        groupRecyclerViewHolder.allCard.setOnLongClickListener(new View.OnLongClickListener() {
            @SuppressLint("RestrictedApi")
            @Override
            public boolean onLongClick(View v) {
                atLeastOneSelected = true;
                model.setSelected(true);
                numberOfGroupsSelected++;
                groupRecyclerViewHolder.groupColor.setBackgroundResource(R.color.selectedGroupGray);


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

        }else if(gg==5){

        }else if(gg==6){

        }else if(gg==7){

        }



    }

    @Override
    public int getItemCount() {
        return s.size();
    }

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


}
