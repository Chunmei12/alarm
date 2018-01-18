package com.alarmclock.administrator.alarmclock;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Switch;

import com.alarmclock.administrator.alarmclock.Adapter.common;
import com.alarmclock.administrator.alarmclock.Class.Person;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/1/9.
 */

public class repeatAlarm extends AppCompatActivity implements Serializable, View.OnClickListener{
    private String path;
    private String key;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference myRef = database.getReference("alarm");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.repeat_alarm);
        path = getIntent().getExtras().getSerializable("email").toString();
        key = getIntent().getExtras().getSerializable("key").toString();

        Switch monday = (Switch) findViewById(R.id.mySwitch_Monday);
        Switch tuesday = (Switch) findViewById(R.id.mySwitch_Tuesday);
        Switch wendsday = (Switch) findViewById(R.id.mySwitch_Wendesday);
        Switch thursday = (Switch) findViewById(R.id.mySwitch_Thursday);
        Switch friday = (Switch) findViewById(R.id.mySwitch_Friday);
        Switch saturday = (Switch) findViewById(R.id.mySwitch_Sarturday);
        Switch sunday = (Switch) findViewById(R.id.mySwitch_Sunday);


        initialCheck(monday, tuesday, wendsday, thursday, friday, saturday, sunday);
        monday.setOnClickListener(this);
        tuesday.setOnClickListener(this);
        wendsday.setOnClickListener(this);
        thursday.setOnClickListener(this);
        friday.setOnClickListener(this);
        saturday.setOnClickListener(this);
        sunday.setOnClickListener(this);



    }

    @Override
    public void onClick(View view){
        // So we will make
        Switch s;
        boolean value;

        switch (view.getId() /*to get clicked view id**/) {
            case R.id.mySwitch_Monday:
                s = (Switch) findViewById(R.id.mySwitch_Monday);
                value = s.isChecked();
                s.setChecked(value);
                myRef.child(path).child(key).child("monday").setValue(value);


                break;
            case R.id.mySwitch_Tuesday:
                s = (Switch) findViewById(R.id.mySwitch_Tuesday);
                value = s.isChecked();
                s.setChecked(value);
                myRef.child(path).child(key).child("tuesday").setValue(value);

                break;
            case R.id.mySwitch_Wendesday:

                s = (Switch) findViewById(R.id.mySwitch_Wendesday);
                value = s.isChecked();
                s.setChecked(value);
                myRef.child(path).child(key).child("wednesday").setValue(value);

                break;
            case R.id.mySwitch_Thursday:

                s = (Switch) findViewById(R.id.mySwitch_Thursday);
                value = s.isChecked();
                s.setChecked(value);
                myRef.child(path).child(key).child("thursday").setValue(value);
                break;
            case R.id.mySwitch_Friday:

                s = (Switch) findViewById(R.id.mySwitch_Friday);
                value = s.isChecked();
                s.setChecked(value);
                myRef.child(path).child(key).child("friday").setValue(value);

                break;
            case R.id.mySwitch_Sarturday:

                s = (Switch) findViewById(R.id.mySwitch_Sarturday);
                value = s.isChecked();
                s.setChecked(value);
                myRef.child(path).child(key).child("saturday").setValue(value);


                break;
            case R.id.mySwitch_Sunday:

                s = (Switch) findViewById(R.id.mySwitch_Sunday);
                value = s.isChecked();
                s.setChecked(value);
                myRef.child(path).child(key).child("sunday").setValue(value);

                break;
            default:
                break;
        }
    }
    @Override
    public void onBackPressed() {
        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("currentEmail", common.DecodeString(this.path));
        editor.commit();
        Intent intent = new Intent(this,Nav_drawer.class);
        this.startActivity(intent);
    }
    public void initialCheck(final Switch monday,final Switch tuesday,final Switch wendsday, final Switch thursday, final Switch friday, final Switch saturday, final Switch sunday) {



        myRef.child(path).child(key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                   monday.setChecked(Boolean.valueOf(snapshot.child("monday").getValue().toString()));
                   tuesday.setChecked(Boolean.valueOf(snapshot.child("tuesday").getValue().toString()));
                   wendsday.setChecked(Boolean.valueOf(snapshot.child("wednesday").getValue().toString()));
                   thursday.setChecked(Boolean.valueOf(snapshot.child("thursday").getValue().toString()));
                   friday.setChecked(Boolean.valueOf(snapshot.child("friday").getValue().toString()));
                   saturday.setChecked(Boolean.valueOf(snapshot.child("saturday").getValue().toString()));
                   sunday.setChecked(Boolean.valueOf(snapshot.child("sunday").getValue().toString()));

            }

            @Override
            public void onCancelled(DatabaseError firebaseError) { }
        });
    }
}
