package com.varunsaini.android.ga_basictransitions;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityOptions;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
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
import java.util.Collections;

public class GroupActivity extends AppCompatActivity {

    AlarmManager alarmMgr;
    PendingIntent alarmIntent;
    RecyclerView recyclerView;
    FloatingActionButton fabAddGroup,fabDeleteGroup,fabCancel;
    RecyclerView.LayoutManager layoutManager;
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

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.custom_main_action_bar_layout);
        View view =getSupportActionBar().getCustomView();

        TextView title = view.findViewById(R.id.title);
        title.setTypeface(tf);


        CardView allButton = findViewById(R.id.group_all);
        allButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = (new Intent(GroupActivity.this,AllActivity.class));
//                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });

//        CardView groupCard = findViewById(R.id.group_card);
//        groupCard.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent i = new Intent(GroupActivity.this,AboutGroupActivity.class);
//                startActivity(i);
//            }
//        });
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(new GroupRecyclerViewAdapter(groupArrayList));
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemViewCacheSize(20);
        recyclerView.setFocusable(false);

        final GroupRecyclerViewAdapter groupObject = new GroupRecyclerViewAdapter(groupArrayList);

//        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this,recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
//            @SuppressLint("RestrictedApi")
//            @Override
//            public void onItemClick(View view, int position) {
//                if(!groupObject.atLeastOneSelected){
//                    fabDeleteGroup.setVisibility(View.GONE);
//                    fabCancel.setVisibility(View.GONE);
//                    fabAddGroup.setVisibility(View.VISIBLE);
//                }
//            }
//
//            @SuppressLint({"RestrictedApi", "ResourceAsColor"})
//            @Override
//            public void onLongItemClick(View view, int position) {
//
//                    fabDeleteGroup.setVisibility(View.GONE);
//                    fabCancel.setVisibility(View.GONE);
//                    fabAddGroup.setVisibility(View.VISIBLE);
//
//
//
//            }
//        }));
    }

    public void addGroup(View view) {
        Intent i = new Intent(GroupActivity.this,AboutGroupActivity.class);
        startActivity(i);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

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

                }else if(gg==5){

                }else if(gg==6){

                }else if(gg==7){

                }
            }else{
                groupRecyclerViewHolder.groupColor.setBackgroundResource(R.color.selectedGroupGray);
            }
            
            fabCancel.setOnClickListener(new View.OnClickListener() {
                @SuppressLint("RestrictedApi")
                @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                @Override
                public void onClick(View v) {
                    Toast.makeText(GroupActivity.this, "Cancel Clicked", Toast.LENGTH_SHORT).show();
                        Log.d("hjh", "onClick: "+intString.get(i));
                        Log.d("hjhjjj", "onClick: "+s.get(intString.get(i)).groupColor);
                        numberOfGroupsSelected= 0;
                        finish();
                        startActivity(getIntent());
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                        atLeastOneSelected=false;
                    fabDeleteGroup.setVisibility(View.GONE);
                    fabCancel.setVisibility(View.GONE);
                    fabAddGroup.setVisibility(View.VISIBLE);

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
                            ArrayList<Integer> integerArrayList = db.getThisAlarmIntents(Integer.valueOf(String.valueOf(request_code).substring(3,9)));
                            for(Integer k : integerArrayList) {
                                PendingIntent alarmIntent = PendingIntent.getBroadcast(context, Integer.valueOf(k), intent, PendingIntent.FLAG_UPDATE_CURRENT);
                                alarmMgr.cancel(alarmIntent);
                            }
                            db.removeDaysPendingReq(Integer.valueOf(String.valueOf(request_code).substring(3,9)));
                        }
                        numberOfGroupsSelected--;
                        db.deleteAGroup(s.get(intString.get(i)).groupName);
                        s.remove(intString.get(i).intValue());
                        notifyItemRemoved(intString.get(i));
//                        notifyDataSetChanged();
                    }
                    fabDeleteGroup.setVisibility(View.GONE);
                    fabCancel.setVisibility(View.GONE);
                    fabAddGroup.setVisibility(View.VISIBLE);

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
                        model.setSelected(true);
                        groupRecyclerViewHolder.groupColor.setBackgroundResource(R.color.selectedGroupGray);
                        intString.add(i);
                    }else if(atLeastOneSelected && model.isSelected())
                    {
                        numberOfGroupsSelected--;
                        model.setSelected(false);
                        int pos = intString.indexOf(i);
                        intString.remove(pos);
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
                    }else if(!atLeastOneSelected){
                        fabDeleteGroup.setVisibility(View.GONE);
                        fabCancel.setVisibility(View.GONE);
                        fabAddGroup.setVisibility(View.VISIBLE);
                        Intent i = new Intent(context, AboutGroupActivity.class);
                        i.putExtra("groupName", aa);
                        Activity activity = (Activity) context;
                        activity.startActivity(i);
                        activity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    }if(!atLeastOneSelected){
                        fabDeleteGroup.setVisibility(View.GONE);
                        fabCancel.setVisibility(View.GONE);
                        fabAddGroup.setVisibility(View.VISIBLE);
                        oneTimeLongTapped= false;
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
                    if(!oneTimeLongTapped) {
                        atLeastOneSelected = true;
                        model.setSelected(true);
                        numberOfGroupsSelected++;
                        groupRecyclerViewHolder.groupColor.setBackgroundResource(R.color.selectedGroupGray);
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

            }else if(gg==5){

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
        
        


    }


}
