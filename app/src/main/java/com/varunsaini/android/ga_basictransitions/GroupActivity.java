package com.varunsaini.android.ga_basictransitions;

import android.app.ActivityOptions;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.transition.Explode;
import android.transition.Fade;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import java.util.ArrayList;

public class GroupActivity extends AppCompatActivity {

    AlarmManager alarmMgr;
    PendingIntent alarmIntent;
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    SQLiteDatabase sqLiteDatabase;
    DatabaseHandler db;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);

        db = new DatabaseHandler(this);
        sqLiteDatabase = this.openOrCreateDatabase("Alarmm",MODE_PRIVATE,null);
        db.onCreate(sqLiteDatabase);
        db.getAllDatabaseDataInLogcat();

        ArrayList<Group> groupArrayList = db.getGroupActivityGroupList();

        recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view_group);
        recyclerView.setHasFixedSize(true);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.custom_main_action_bar_layout);
        View view =getSupportActionBar().getCustomView();


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
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
    }

    public void addGroup(View view) {
        Intent i = new Intent(GroupActivity.this,AboutGroupActivity.class);
        startActivity(i);
    }

    @Override
    public void onBackPressed() {
        Intent i = (new Intent(GroupActivity.this,AllActivity.class));
//        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}
