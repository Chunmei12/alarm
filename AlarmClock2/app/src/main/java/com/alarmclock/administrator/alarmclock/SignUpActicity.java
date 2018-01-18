package com.alarmclock.administrator.alarmclock;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.transition.ChangeBounds;
import android.transition.Scene;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alarmclock.administrator.alarmclock.Adapter.CommonAdapter;
import com.alarmclock.administrator.alarmclock.Class.Alarm;
import com.alarmclock.administrator.alarmclock.Class.Person;
import com.alarmclock.administrator.alarmclock.Views.SignUpLoading;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.alarmclock.administrator.alarmclock.Adapter.common;
/**
 * Created by Administrator on 2017/1/4.
 */

public class SignUpActicity extends AppCompatActivity implements View.OnClickListener{
    private FrameLayout mFrtContent;

    private Scene mSceneSignUp;
    private Scene mSceneLogging;
    private Scene mSceneMain;

    private int mTvSighUpWidth, mTvSighUpHeight;
    private int mDuration;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference myRef;
    private  static   boolean  resulte = false;
    private  static  String email=null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mFrtContent = (FrameLayout) findViewById(R.id.frt_content);

        mDuration = getResources().getInteger(R.integer.duration);

        mSceneSignUp = Scene.getSceneForLayout(mFrtContent, R.layout.scene_sign_up, this);
        mSceneSignUp.setEnterAction(new Runnable() {
            @Override
            public void run() {
                final SignUpLoading loginView = (SignUpLoading) mFrtContent.findViewById(R.id.SignUp_view);
                ViewTreeObserver vto = loginView.getViewTreeObserver();
                vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        setSize(loginView.getMeasuredWidth(), loginView.getMeasuredHeight());
                    }
                });
                loginView.setOnClickListener(SignUpActicity.this);
            }
        });


        mSceneLogging = Scene.getSceneForLayout(mFrtContent, R.layout.scene_signuping, this);
        mSceneLogging.setEnterAction(new Runnable() {
            @Override
            public void run() {
                final SignUpLoading loginView = (SignUpLoading) mFrtContent.findViewById(R.id.signUpIng_view);
                loginView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        loginView.setStatus(SignUpLoading.STATUS_LOGGING);
                    }
                }, mDuration);
                loginView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        loginView.setStatus(SignUpLoading.STATUS_LOGIN_SUCCESS);
                    }
                }, 4000);

                loginView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        goDrawer();
                    }
                }, 6000);
            }
        });


        TransitionManager.go(mSceneSignUp);
    }


    @Override
    public void onClick(final View view) {

        float finalRadius = (float) Math.hypot(mFrtContent.getWidth(), mFrtContent.getHeight());

        String password=null;
        String name=null;

        int[] location = new int[2];
        view.getLocationOnScreen(location);
        int x = location[0];
        int y = location[1];

        Animator anim = ViewAnimationUtils.createCircularReveal(mFrtContent, x + mTvSighUpWidth / 2, y - mTvSighUpHeight / 2, 100, finalRadius);
        mFrtContent.setBackgroundColor(ContextCompat.getColor(SignUpActicity.this, R.color.colorBg));
        anim.setDuration(mDuration);
        anim.setInterpolator(new AccelerateDecelerateInterpolator());
        anim.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mFrtContent.setBackgroundColor(Color.TRANSPARENT);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        anim.start();
        EditText useremail = (EditText) findViewById(R.id.useremail);
        EditText username = (EditText) findViewById(R.id.username);
        EditText pass = (EditText) findViewById(R.id.password);
        EditText passConfir = (EditText) findViewById(R.id.passconfir);
        TextView erro = (TextView) findViewById(R.id.errotext);
        email = useremail.getText().toString();
        name = username.getText().toString();
        password = pass.getText().toString();
        String emailregex = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$";
        Boolean b = email.matches(emailregex);

        if(b){
            if ( !password.equals("") && password.equals(passConfir.getText().toString())) {
                saveIfRefIsAbsent();
                if(resulte) {
                    erro.setText("the email is exited");
                }else {
                    addUser(password, name);
                    TransitionManager.go(mSceneLogging, new ChangeBounds().setDuration(mDuration).setInterpolator(new DecelerateInterpolator()));
                }
            }else{
                erro.setText("the seconde password is not same as the first");
            }
        }else {
            erro.setText("the email format id not correct");
        }

        resulte = false;


    }
    private void setSize(int width, int height) {
        mTvSighUpWidth = width;
        mTvSighUpHeight = height;
    }

    public void addUser(String password, String name) {
        // Write a message to the database


        Person person = new Person(email, password, name);
        //Adding valuesm
        myRef = database.getReference("user");
        DatabaseReference newRef = myRef.push();
        newRef.setValue(person);

        myRef = database.getReference("controled");
        myRef.child(common.EncodeString(person.getEmail())).push().setValue(person);

        myRef = database.getReference("controler");
        myRef.child(common.EncodeString(person.getEmail())).push().setValue(person);

        myRef = database.getReference("alarm");
        Alarm alarm = new Alarm("08","00",false,false,false,false,false,false,false,false);
        myRef.child(common.EncodeString(person.getEmail())).push().setValue(alarm);


        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("email", person.getEmail());  // Saving string
        editor.putString("password", person.getPassword());
        editor.putString("name", person.getName());
        editor.putString("currentEmail",  person.getEmail());
        editor.commit(); // commit changes
    }
    public void goDrawer() {
        Intent intent = new Intent(this, Nav_drawer.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(intent);
    }
    public void saveIfRefIsAbsent() {
        myRef = database.getReference("user");
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                //resulte = snapshot.exists();
                for (DataSnapshot userSnapshot: snapshot.getChildren()) {

                  if (userSnapshot.child("email").getValue() == email) {
                      resulte = true;
                  }
                }
            }

            @Override
            public void onCancelled(DatabaseError firebaseError) { }
        });
    }
}
