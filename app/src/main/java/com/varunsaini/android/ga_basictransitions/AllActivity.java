package com.varunsaini.android.ga_basictransitions;

import android.app.ActivityOptions;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.AppLaunchChecker;
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
import android.view.View;
import android.view.Window;
import android.widget.Button;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;

public class AllActivity extends AppCompatActivity {

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
        setContentView(R.layout.activity_all);


        db = new DatabaseHandler(this);
        sqLiteDatabase = this.openOrCreateDatabase("Alarmm",MODE_PRIVATE,null);
        db.onCreate(sqLiteDatabase);
        db.getAllDatabaseDataInLogcat();

        ArrayList<AllAlarm> allAlarmArrayList = db.getAllActivtiyAlarmList();



        CardView groupButton = findViewById(R.id.all_group);
        groupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AllActivity.this,GroupActivity.class));
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });


        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.custom_main_action_bar_layout);
        View view =getSupportActionBar().getCustomView();

        recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view_all_alarms);
        recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(new AllAlarmRecyclerViewAdapter(allAlarmArrayList));
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemViewCacheSize(20);
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);

//
//        CardView groupButton = findViewById(R.id.all_group);
//        groupButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(AllActivity.this,GroupActivity.class));
//                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
//            }
//        });
//
//        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(AllActivity.this, recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
//            @Override
//            public void onItemClick(View view, int position) {
//
//                Intent i = new Intent(AllActivity.this,EditAlarmActivity.class);
//                view.findViewById(R.id.rms_switch).setOnClickListener();
//            }
//
//            @Override
//            public void onLongItemClick(View view, int position) {
//
//            }
//        }));

    }


    public void newAlarm(View view) {

        Intent i = new Intent(AllActivity.this,EditAlarmActivity.class);
        i.putExtra("belongs_to_group",0);
        startActivity(i);

    }

}
