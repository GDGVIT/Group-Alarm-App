package com.varunsaini.android.ga_basictransitions;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityOptions;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Rect;
import android.graphics.Typeface;
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
import android.widget.TextView;

import java.util.ArrayList;

public class GroupActivity extends AppCompatActivity {

    AlarmManager alarmMgr;
    PendingIntent alarmIntent;
    RecyclerView recyclerView;
    FloatingActionButton fabAddGroup,fabDeleteGroup,fabCancel;
    RecyclerView.LayoutManager layoutManager;
    SQLiteDatabase sqLiteDatabase;
    DatabaseHandler db;
    TextView allView,groupView;
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
//        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this,recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
//            @SuppressLint("RestrictedApi")
//            @Override
//            public void onItemClick(View view, int position) {
//                if(isGroupSelected==1){
//                    fabDeleteGroup.setVisibility(View.GONE);
//                    fabCancel.setVisibility(View.GONE);
//                    fabAddGroup.setVisibility(View.VISIBLE);
//                    isGroupSelected = 0;
//                }else {
//                    Intent i = new Intent(GroupActivity.this, AboutGroupActivity.class);
//                    i.putExtra("groupName", groupArrayList.get(position).groupName);
//                    startActivity(i);
//                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
//                }
//            }
//
//            @SuppressLint({"RestrictedApi", "ResourceAsColor"})
//            @Override
//            public void onLongItemClick(View view, int position) {
//                fabDeleteGroup.setVisibility(View.VISIBLE);
//                fabCancel.setVisibility(View.VISIBLE);
//                fabAddGroup.setVisibility(View.GONE);
//                isGroupSelected = 1;
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
//        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}
