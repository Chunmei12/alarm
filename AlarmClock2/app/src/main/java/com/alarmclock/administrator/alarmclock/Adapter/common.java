package com.alarmclock.administrator.alarmclock.Adapter;

import android.content.SharedPreferences;
import android.support.v4.app.FragmentActivity;

import com.alarmclock.administrator.alarmclock.Class.Person;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Administrator on 2017/1/8.
 */

public final class common extends FragmentActivity {


    public static String EncodeString(String string) {
        return string.replace(".", ",");
    }

    public static String DecodeString(String string) {
        return string.replace(",", ".");
    }

}
