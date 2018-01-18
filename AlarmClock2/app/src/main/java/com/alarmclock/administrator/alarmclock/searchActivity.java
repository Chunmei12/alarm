package com.alarmclock.administrator.alarmclock;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.alarmclock.administrator.alarmclock.Class.Person;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import android.widget.AdapterView.OnItemClickListener;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import com.alarmclock.administrator.alarmclock.Adapter.common;
/**
 * Created by Administrator on 2017/1/8.
 */

public class searchActivity extends AppCompatActivity implements View.OnClickListener{
    SearchView searchView;
    private ListView mListView;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference myRef;
    private  static List<String> email = new ArrayList<String>();
    private  static List<Person> userlist = new ArrayList<Person>();
    private  static boolean resultat = true;
    private static String itemSelected;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.searchview);

        searchView=(SearchView) findViewById(R.id.searchView);
        searchView.setQueryHint("Search View");

        listOfUser();
        mListView = (ListView) findViewById(R.id.list_view);

        mListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter, View v, int position, long arg3) {
                final String value = (String) adapter.getItemAtPosition(position);
                itemSelected = value;
                SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
                final Person myself = new Person(pref.getString("email", null), pref.getString("password", null), pref.getString("name", null));
                myRef = database.getReference("controled");
                myRef.child(common.EncodeString(myself.getEmail())).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                            if (userSnapshot.child("email").getValue().toString().equals(itemSelected)){
                                resultat = false; //table of controled existed the user
                                break;
                            }
                        }
                        addFriends(itemSelected,myself);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) { }
                });


            }
        });
        mListView.setAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1,
                email));
        mListView.setTextFilterEnabled(true);
        setupSearchView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                if (TextUtils.isEmpty(query)) {
                    mListView.clearTextFilter();
                } else {
                    mListView.setFilterText(query.toString());
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });


       // mListView.setOnClickListener(searchActivity.this);
    }

    public void addFriends(String value, Person myself){
        if(resultat) {
            //add controeld by ohther person
            Iterator<Person> userlistIterator = userlist.iterator();

            while (userlistIterator.hasNext()){
                Person person = userlistIterator.next();
                if (person.getEmail().equals(value)) {
                    myRef.child(common.EncodeString(myself.getEmail())).push().setValue(person);
                    Toast.makeText(getBaseContext(), "add frineds successe who can control you", Toast.LENGTH_LONG).show();
                    break;
                }
            }
            //add other controler
            Iterator<Person> userlistIterator2 = userlist.iterator();
            myRef = database.getReference("controler");
            while (userlistIterator2.hasNext()){
                Person person = userlistIterator2.next();
                if (person.getEmail().equals(value)) {
                    myRef.child(common.EncodeString(person.getEmail())).push().setValue(myself);
                    goBackToNav();
                    break;
                }
            }
        }
        else {
            Toast.makeText(getBaseContext(), "this frined who can control you has existed", Toast.LENGTH_LONG).show();
        }
        resultat = true;

    }
    private void setupSearchView() {
        searchView.setIconifiedByDefault(false);
        searchView.setSubmitButtonEnabled(true);
        searchView.setQueryHint("Search Here");
    }

    public void listOfUser() {

        myRef = database.getReference("user");

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                email.clear();
                userlist.clear();
                for (DataSnapshot userSnapshot: snapshot.getChildren()) {
                    String  value = userSnapshot.child("email").getValue().toString();
                    if (value != null) {
                        email.add(value);
                        Person person = new Person(userSnapshot.child("email").getValue().toString(), userSnapshot.child("password").getValue().toString(), userSnapshot.child("name").getValue().toString());
                        userlist.add(person);
                    }
                }

            }

            @Override
            public void onCancelled(DatabaseError firebaseError) { }
        });
    }


    @Override
    public void onClick(final View view) {


    }
    public void goBackToNav() {
        Intent intent = new Intent(this,Nav_drawer.class);
        startActivity(intent);
    }


    }
