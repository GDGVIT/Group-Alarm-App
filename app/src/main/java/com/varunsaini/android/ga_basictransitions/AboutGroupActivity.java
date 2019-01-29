package com.varunsaini.android.ga_basictransitions;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AlertDialogLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;

public class AboutGroupActivity extends AppCompatActivity {
//    tools:showIn="@layout/activity_about_group"
    AlarmManager alarmMgr;
    PendingIntent alarmIntent;
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    SQLiteDatabase sqLiteDatabase;
    DatabaseHandler db;
    EditText groupNameEditext;
    String groupName;
    String groupNameOnGroupOpen = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_group);


        db = new DatabaseHandler(this);
        sqLiteDatabase = this.openOrCreateDatabase("Alarmm",MODE_PRIVATE,null);
        db.onCreate(sqLiteDatabase);
        db.getAllDatabaseDataInLogcat();

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.custom_main_action_bar_layout);
        View view = getSupportActionBar().getCustomView();

        groupNameEditext = findViewById(R.id.group_name);
        groupName = getIntent().getStringExtra("groupName");
        if (groupName!=null){
            groupNameEditext.setText(groupName);
            groupNameOnGroupOpen = groupName;
            ArrayList<GroupInfo> groupInfoArrayList = db.getAllGroupInfo(groupName);
            recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view_all_alarms_in_a_group);
            recyclerView.setHasFixedSize(true);

            layoutManager = new LinearLayoutManager(this);
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.setAdapter(new AboutGroupRecyclerViewAdapter(groupInfoArrayList));
            recyclerView.setHasFixedSize(true);
            recyclerView.setItemViewCacheSize(20);
            recyclerView.setDrawingCacheEnabled(true);
            recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);

        }

    }

    public void editAlarm(View v){
        Intent i= new Intent(AboutGroupActivity.this, EditAlarmActivity.class);
        startActivity(i);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    public void newAlarm(View view) {
        ArrayList<String> allGroupNames = db.getAllGroupNames();
        allGroupNames.remove(groupName);
        String editText = groupNameEditext.getText().toString();
        int groupNameFoundInDatabase = 0;
        for(int i=0;i<allGroupNames.size();i++){
            if(editText.equals(allGroupNames.get(i))){
                groupNameFoundInDatabase = 1;
            }
        }
        if(!editText.equals("") && groupNameFoundInDatabase==0){
            Intent i= new Intent(AboutGroupActivity.this, EditAlarmActivity.class);
            i.putExtra("nameOfGroup",groupNameEditext.getText().toString());
            startActivity(i);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        }else if(editText.equals("")){
            Toast.makeText(this, "Give a Group Name first", Toast.LENGTH_SHORT).show();
        }else if(groupNameFoundInDatabase==1){
            Toast.makeText(this, "Group Name Already Exists", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onBackPressed() {
        int groupNameFoundInDatabase = 0;
        String editText = groupNameEditext.getText().toString();
        ArrayList<String> allGroupNames = db.getAllGroupNames();
        allGroupNames.remove(groupName);
        for(int i=0;i<allGroupNames.size();i++){
            if(editText.equals(allGroupNames.get(i))){
                groupNameFoundInDatabase = 1;
            }
        }
        if(!editText.equals("") && groupNameFoundInDatabase == 0) {
            db.changeGroupName(groupName, editText);
            startActivity(new Intent(this, GroupActivity.class));
        }else if(editText.equals("") && !groupNameOnGroupOpen.equals("")){
            Toast.makeText(this, "Give a Group Name first", Toast.LENGTH_SHORT).show();
        }else if(groupNameFoundInDatabase==1){
            Toast.makeText(this, "Group Name Already Exists", Toast.LENGTH_SHORT).show();
        }else if(groupNameOnGroupOpen.equals("")){
            startActivity(new Intent(this, GroupActivity.class));
        }
    }

}
