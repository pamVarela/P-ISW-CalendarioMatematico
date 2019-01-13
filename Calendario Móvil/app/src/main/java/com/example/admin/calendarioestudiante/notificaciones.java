package com.example.admin.calendarioestudiante;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;
import java.util.Calendar;


public class notificaciones extends AppCompatActivity {

    Switch sw;
    private final int MY_PERMISSION_TO_WRITE_CALENDAR = 123;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notificar_activity);

        sw = findViewById(R.id.switch2);


        sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {

                if (isChecked) {
                    agregarEventoCalendario();
                    Toast.makeText(getApplicationContext(), "Notificaciones activadas", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Notificaciones desactivadas", Toast.LENGTH_SHORT).show();
                    desactivarNotificaciones();
                }
            }
        });


    }
    //se agregan los eventos en el calendario
    public void agregarEventoCalendario() {

        long calID = 2;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_CALENDAR}, MY_PERMISSION_TO_WRITE_CALENDAR);
        }

        Calendar beginTime = Calendar.getInstance();
        beginTime.set(beginTime.get(Calendar.YEAR),beginTime.get(Calendar.MONTH),  beginTime.get(Calendar.DAY_OF_MONTH), 14, 5);
        Calendar endTime = Calendar.getInstance();
        endTime.set(beginTime.get(Calendar.YEAR),beginTime.get(Calendar.MONTH),  beginTime.get(Calendar.DAY_OF_MONTH), 15, 5);

        ContentResolver cr = getContentResolver();

        ContentValues values = new ContentValues();
        values.put(CalendarContract.Events.DTSTART, beginTime.getTimeInMillis());
        values.put(CalendarContract.Events.DTEND, endTime.getTimeInMillis());
        values.put(CalendarContract.Events.TITLE, "Calendario Matemático CIEMAC");
        values.put(CalendarContract.Events.DESCRIPTION, "Ejercicio del día");
        values.put(CalendarContract.Events.RRULE, "FREQ=DAILY");
        values.put(CalendarContract.Events.CALENDAR_ID, calID);
        values.put(CalendarContract.Events.HAS_ALARM, 1);
        values.put(CalendarContract.Events.EVENT_LOCATION, "Costa Rica");
        values.put(CalendarContract.Events.EVENT_TIMEZONE, "America/Los_Angeles");

        Uri uri = cr.insert(CalendarContract.Events.CONTENT_URI, values);
        long eventID = Long.parseLong(uri.getLastPathSegment());

        ContentValues reminders = new ContentValues();
        reminders.put(CalendarContract.Reminders.MINUTES, 1);
        reminders.put(CalendarContract.Reminders.EVENT_ID, eventID);
        reminders.put(CalendarContract.Reminders.METHOD, CalendarContract.Reminders.METHOD_ALERT);
        cr.insert(CalendarContract.Reminders.CONTENT_URI, reminders);

        Log.d("CALENDAR ", "calendar entry inserted");
    }


    public void desactivarNotificaciones(){

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_CALENDAR}, MY_PERMISSION_TO_WRITE_CALENDAR);
        }

        Uri uri = CalendarContract.Events.CONTENT_URI;

        String mSelectionClause = CalendarContract.Events.TITLE+ " = ?";
        String[] mSelectionArgs = {"Calendario Matemático CIEMAC"};

        int updCount = getContentResolver().delete(uri,mSelectionClause,mSelectionArgs);
    }
}






