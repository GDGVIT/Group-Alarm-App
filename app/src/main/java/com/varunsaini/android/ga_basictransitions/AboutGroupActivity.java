package com.varunsaini.android.ga_basictransitions;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.os.Bundle;
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
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class AboutGroupActivity extends AppCompatActivity {
//    tools:showIn="@layout/activity_about_group"
    AlarmManager alarmMgr;
    PendingIntent alarmIntent;

    int mGroupColor = 1;
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    SQLiteDatabase sqLiteDatabase;
    DatabaseHandler db;
    EditText groupNameEditext;
    static String groupName;
    String groupNameOnGroupOpen = "";
    CardView colorPurple,colorGreen,colorPink;
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
        sqLiteDatabase = this.openOrCreateDatabase("Alarmm",MODE_PRIVATE,null);
        db.onCreate(sqLiteDatabase);
        db.getAllDatabaseDataInLogcat();

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.custom_2_action_bar_layout);
        View view = getSupportActionBar().getCustomView();

        backActionBar = view.findViewById(R.id.backActionBar);
        titleActionBar = view.findViewById(R.id.titleActionBar);

        Typeface tf1 = Typeface.createFromAsset(getAssets(),"fonts/Karla-Bold.ttf");
        titleActionBar.setTypeface(tf1);
        titleActionBar.setText("Manage Group");

        backActionBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AboutGroupActivity.super.onBackPressed();
            }
        });


        groupNameEditext = findViewById(R.id.group_name);
        groupName = getIntent().getStringExtra("groupName");
        if (groupName!=null){
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

    public void editAlarm(View v){
        db.changeGroupColor(groupName,mGroupColor);
        Intent i= new Intent(AboutGroupActivity.this, EditAlarmActivity.class);
        startActivity(i);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    public void newAlarm(View view) {
        db.changeGroupColor(groupName,mGroupColor);
        ArrayList<String> allGroupNames = db.getAllGroupNames();
        allGroupNames.remove(groupName);
        String editText = groupNameEditext.getText().toString();
        int groupNameFoundInDatabase = 0;
        for(int i=0;i<allGroupNames.size();i++){
            if(editText.equals(allGroupNames.get(i))){
                groupNameFoundInDatabase = 1;
            }
        }
        if(!editText.equals("") && groupNameFoundInDatabase==0 && (!editText.contains("'")||!editText.contains("'")||!editText.contains(""))){
            Intent i= new Intent(AboutGroupActivity.this, EditAlarmActivity.class);
            i.putExtra("nameOfGroup",groupNameEditext.getText().toString());
            i.putExtra("colorOfGroup",mGroupColor);
            startActivity(i);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        }else if(editText.equals("")){
            Toast.makeText(this, "Give a Group Name first", Toast.LENGTH_SHORT).show();
        }else if(groupNameFoundInDatabase==1){
            Toast.makeText(this, "Group Name Already Exists", Toast.LENGTH_SHORT).show();
        }else if(editText.contains("'")||editText.contains("'")||editText.contains("")){
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
        for(int i=0;i<allGroupNames.size();i++){
            if(editText.equals(allGroupNames.get(i))){
                groupNameFoundInDatabase = 1;
            }
        }
        if(!editText.equals("") && groupNameFoundInDatabase == 0 && (!editText.contains("'")||!editText.contains(""))) {
            db.changeGroupColor(groupName,mGroupColor);
            db.changeGroupName(groupName, editText);
            startActivity(new Intent(this, GroupActivity.class));
        }else if(editText.equals("") && !groupNameOnGroupOpen.equals("")){
            Toast.makeText(this, "Give a Group Name first", Toast.LENGTH_SHORT).show();
        }else if(groupNameFoundInDatabase==1){
            Toast.makeText(this, "Group Name Already Exists", Toast.LENGTH_SHORT).show();
        }else if(groupNameOnGroupOpen.equals("")){
            startActivity(new Intent(this, GroupActivity.class));
        }else if((editText.contains("'")||editText.contains(""))){
            Toast.makeText(this, "Group name can't contain Special Characters", Toast.LENGTH_SHORT).show();
        }
    }

    void setCardBackgroundGradient(int groupColor){
        if(groupColor==1){
            fullCardLinLayout.setBackgroundResource(R.drawable.list_grad_purple);
        }else if(groupColor==2){
            fullCardLinLayout.setBackgroundResource(R.drawable.list_grad_green);
        }else if(groupColor==3){
            fullCardLinLayout.setBackgroundResource(R.drawable.list_grad_pink);
        }else if(groupColor==4){

        }else if(groupColor==5){

        }else if(groupColor==6){

        }else if(groupColor==7){

        }
    }

}
