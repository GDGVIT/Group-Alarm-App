package com.varunsaini.android.ga_basictransitions;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.provider.ContactsContract;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import static android.content.Context.MODE_PRIVATE;

public class AlarmReciever extends BroadcastReceiver {

    private boolean isplaying;
    public static Ringtone r;
    public static int request_id,batch_request_id;
    private static String ringtoneString;
    private static Uri ringtoneUri;
    private static String mVibrate;
    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    public void onReceive(Context context, Intent intent) {
        DatabaseHandler db = new DatabaseHandler(context);
        SQLiteDatabase sqLiteDatabase = context.openOrCreateDatabase("Alarmm",MODE_PRIVATE,null);
//        batch_request_id = intent.getIntExtra("batch_request_code",-1);
        request_id = intent.getIntExtra("request_code",-1);
//        ringtoneString = intent.getStringExtra("ringtone");

        String[] ringtoneVibrateString = db.getRingtoneUriVibrate(request_id);
        if(ringtoneVibrateString[0]!=null){
            ringtoneUri = Uri.parse(ringtoneVibrateString[0]);
        }



        Log.d("cx", "onReceive: "+request_id);
        WakeLocker.acquire(context);


        if(ringtoneUri == null){
            ringtoneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
            if(ringtoneUri == null) {
                ringtoneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            }
        }
        r = RingtoneManager.getRingtone(context, ringtoneUri);
        r.play();
        r.setLooping(true);
        isplaying=true;
        Intent myIntent = new Intent(context, AlarmRingActivity.class);
        myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(myIntent);
        Toast.makeText(context, "OnReceive alarm test", Toast.LENGTH_SHORT).show();

    }

    public void stopAlarmRingtone(){
//        if (isplaying) {
//            r.stop();
//            Log.d("as", "stopAlarmRingtone: Stop Ringtone");
//        }
    }

}
