package com.alarmclock.administrator.alarmclock;




import android.media.MediaPlayer;
import android.os.Handler;
import android.support.v4.app.DialogFragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;

import com.alarmclock.administrator.alarmclock.Adapter.CommonAdapter;
import com.alarmclock.administrator.alarmclock.Adapter.common;
import com.alarmclock.administrator.alarmclock.Class.Alarm;
import com.alarmclock.administrator.alarmclock.Class.Person;
import com.alarmclock.administrator.alarmclock.Views.TimePickerFragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Administrator on 2017/1/5.
 */

public class Nav_drawer extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference myRef;
    private  static List<String> email = new ArrayList<String>();
    private  NavigationView navigationView;
    private SharedPreferences pref;
    private String currentUser;
    private  Hashtable<String,MediaPlayer> mps = new Hashtable<String,MediaPlayer>();
    private Hashtable<String,Handler> handlers= new Hashtable<String,Handler>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nav_drawer_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        currentUser = pref.getString("currentEmail",null);
        getAlarmView(currentUser);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        TextView nav_name = (TextView) navigationView.getHeaderView(0).findViewById(R.id.nav_name);
        TextView nav_email = (TextView) navigationView.getHeaderView(0).findViewById(R.id.nav_email);

        nav_name.setText(pref.getString("name", null).toString());
        nav_email.setText(pref.getString("email", null).toString());
        listOfFriends();
        addAlarmPlay();
        navigationView.setNavigationItemSelectedListener(this);



    }

    @Override
    public void onBackPressed() {

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if(id == R.id.action_invite) {
            Intent intent = new Intent(this, searchActivity.class);
            startActivity(intent);
        }else if(id == R.id.action_signout) {
            pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
            SharedPreferences.Editor editor = pref.edit();
            editor.clear();
            editor.commit();
            finish();
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        item.setChecked(true);
        System.out.println("ssdfq");
        String itemTitle = (String) item.getTitle();
        currentUser = itemTitle;
        getAlarmView(itemTitle);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    public void listOfFriends() {
        navigationView.getMenu().clear();
        myRef = database.getReference("controler"); //todo:friends
        pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
        String path = common.EncodeString(pref.getString("email", null));
        myRef.child(path).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                Menu m = navigationView.getMenu().addSubMenu("who you can control");
                email.clear();
                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    String value = userSnapshot.child("email").getValue().toString();
                    if (value != null) {
                        email.add(value);
                    }
                }
                Iterator<String> emailIterator = email.iterator();
                while (emailIterator.hasNext()) {
                    m.add(emailIterator.next());
                    m.getItem(m.size()-1).setEnabled(true);
                }
            }

            @Override
            public void onCancelled(DatabaseError firebaseError) { }
        });

        myRef = database.getReference("controled");

        myRef.child(path).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                Menu menuGroup = navigationView.getMenu().addSubMenu("who can control you");
                email.clear();
                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    String value = userSnapshot.child("email").getValue().toString();
                    if (value != null) {
                        email.add(value);
                    }
                }
                Iterator<String> emailIterator = email.iterator();
                while (emailIterator.hasNext()) {
                    menuGroup.add(emailIterator.next());
                    menuGroup.getItem(menuGroup.size()-1).setEnabled(false);
                }
            }

                @Override
                public void onCancelled(DatabaseError firebaseError) { }
            });

    }
    public void addAlarmPlay() {
        pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
        myRef = database.getReference("alarm");
        myRef.child(common.EncodeString(pref.getString("email",null))).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (Handler handler:handlers.values()) {
                    handler.removeCallbacksAndMessages(null);
                }
                for (MediaPlayer mp:mps.values()) {
                    mp.stop();
                    mp.release();
                }
                handlers.clear();
                mps.clear();
                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    boolean everyMonday = Boolean.valueOf(userSnapshot.child("monday").getValue().toString());
                    boolean everyTuesday = Boolean.valueOf(userSnapshot.child("tuesday").getValue().toString());
                    boolean everyWednesday = Boolean.valueOf(userSnapshot.child("wednesday").getValue().toString());
                    boolean everyThursday = Boolean.valueOf(userSnapshot.child("thursday").getValue().toString());
                    boolean everyFriday = Boolean.valueOf(userSnapshot.child("friday").getValue().toString());
                    boolean everySaturday = Boolean.valueOf(userSnapshot.child("saturday").getValue().toString());
                    boolean everySunday = Boolean.valueOf(userSnapshot.child("sunday").getValue().toString());
                    boolean repeat = everyMonday || everyTuesday || everyWednesday || everyThursday || everyFriday || everySaturday || everySunday;
                    if (Boolean.valueOf(userSnapshot.child("onOff").getValue().toString())) {

                        java.util.Date date1 = null;
                        java.util.Date date2 = new Date();
                        java.util.Date date3 = null;

                        String[] weeks = {"monday","tuesday","wednesday","thursday","friday","saturday","sunday"};
                        java.text.DateFormat df = new java.text.SimpleDateFormat("hh:mm:ss");
                        df.setTimeZone(TimeZone.getTimeZone("GMT"));

                        String hour = userSnapshot.child("hour").getValue().toString();
                        String minute = userSnapshot.child("minute").getValue().toString();
                        int day = date2.getDay()-1;
                        System.out.println(  date2.getDay() + "  " +date2.getDate());
                        if (!repeat) {
                            try {
                                date1 = df.parse(hour+":"+minute+":00");
                                date3 = df.parse(date2.getHours()+":"+date2.getMinutes()+":00");
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            long diff = (long) date1.getTime() - (long) date3.getTime();
                            deplayPlay(diff,  userSnapshot ,repeat);
                            System.out.println(date1.getTime() +"  " + (long) date3.getTime()+ " " +date2.getHours() +" "+ userSnapshot.child(weeks[day]).getValue().toString() );
                        }else if(Boolean.valueOf(userSnapshot.child(weeks[day]).getValue().toString())){
                            try {
                                date1 = df.parse(hour+":"+minute+":00");
                                date3 = df.parse(date2.getHours()+":"+date2.getMinutes()+":00");
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            long diff = (long) date1.getTime()  - (long) date3.getTime();
                            deplayPlay(diff,  userSnapshot ,repeat);
                            System.out.println(date1.getTime() +"  " + (long) date3.getTime() + " "  +date2.getHours() +" "+userSnapshot.child(weeks[day]).getValue().toString() );
                        }else {

                        }

                    }else {

                    }


                }


            }

            @Override
            public void onCancelled(DatabaseError firebaseError) { }
        });



    }
    public void getAlarmView(final String itemTitle){
        System.out.println(itemTitle);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(itemTitle);
        myRef = database.getReference("alarm");
        myRef.child(common.EncodeString(itemTitle)).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                final RecyclerView recyclerView = (RecyclerView) findViewById(R.id.rv_common);
                CommonAdapter adapter = new CommonAdapter(getApplicationContext(), itemTitle, snapshot.getChildrenCount(), snapshot.getChildren());
                recyclerView.setLayoutManager(new LinearLayoutManager(Nav_drawer.this));
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) { }
        });
    }

    public void gotoAlarm(View view) {
        DialogFragment newFragment = new TimePickerFragment(currentUser);
        newFragment.show(getSupportFragmentManager(), "timePicker");

    }

    public void deplayPlay(long time, final DataSnapshot userSnapshot, final boolean repeat){
        final MediaPlayer mp = MediaPlayer.create(this, R.raw.alarm);
        mps.put(userSnapshot.getKey(),mp);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                MediaPlayer mp =  mps.get(userSnapshot.getKey());
                mp.start();
                if (!repeat) {
                    Timer ti = new Timer();
                    ti.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            userSnapshot.child("onOff").getRef().setValue(false);
                        }
                    }, 10000);

                }
                handlers.remove(userSnapshot.getKey());

            }
        }, time);
        handlers.put(userSnapshot.getKey(),handler);


    }
}

