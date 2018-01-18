package com.alarmclock.administrator.alarmclock.Views;

import android.app.Dialog;
import android.support.v4.app.DialogFragment;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.widget.TimePicker;

import com.alarmclock.administrator.alarmclock.Adapter.common;
import com.alarmclock.administrator.alarmclock.Class.Alarm;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

/**
 * Created by Administrator on 2017/1/9.
 */

public  class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference myRef = database.getReference("alarm");
    private String email;

    public TimePickerFragment(String email) {
        this.email = email;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current time as the default values for the picker
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        // Create a new instance of TimePickerDialog and return it
        return new TimePickerDialog(getActivity(), this, hour, minute,
                DateFormat.is24HourFormat(getActivity()));
    }

    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        // Do something with the time chosen by the user
        String hour = String.valueOf(hourOfDay);
        String minu = String.valueOf(minute);
        //add ararm
        Alarm alarm = new Alarm(hour,minu,false,false,false,false,false,false,false,false);
        myRef.child(common.EncodeString(email)).push().setValue(alarm);
    }




}