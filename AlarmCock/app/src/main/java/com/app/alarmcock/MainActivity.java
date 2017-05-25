package com.app.alarmcock;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity implements AlarmListFragment.OnAlarmSelected {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.root_layout, AlarmListFragment.newInstance(), "AlarmList")
                    .commit();
        }
    }

    @Override
    public void onAlarmSelected(int imageResId, String name) {
        final AlarmOptionsFragment optionsFragment =
                AlarmOptionsFragment.newInstance(imageResId, name);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.root_layout, optionsFragment, "alarmDetails")
                .addToBackStack(null)
                .commit();
    }

    public void onAddAlarmClick(View view) {
        Intent intent = new Intent(this, AddAlarm.class);
        startActivity(intent);
    }

    public void enableAlarm(View view) {
        Switch s = (Switch)view;

        String[] minuteArray = s.getText().toString().split(":");

        Calendar calendar = Calendar.getInstance();
        int currentMinute = calendar.get(Calendar.MINUTE);

        int alarmMinute = Integer.parseInt(minuteArray[1]);

        int difference = Math.abs(alarmMinute - currentMinute);
        calendar.add(Calendar.MINUTE, difference);

        Intent intent = new Intent(this, AlarmActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                12345, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager am =
                (AlarmManager)getSystemService(Activity.ALARM_SERVICE);

        am.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);

        if(s.isChecked()){
            am.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        } else{
            am.cancel(pendingIntent);
        }

    }

    public void onDeleteAlarmClick(View view) {
        try{
            SQLiteOpenHelper alarmDatabaseHelper = new AlarmDatabaseHelper(this);
            SQLiteDatabase db = alarmDatabaseHelper.getReadableDatabase();

            db.delete("ALARM", "TIME=?", new String[] {"2:44"});

            db.close();
        }catch(SQLiteException e){}

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}