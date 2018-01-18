package com.alarmclock.administrator.alarmclock;

import android.animation.Animator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.transition.ChangeBounds;
import android.transition.Scene;
import android.transition.TransitionManager;

import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;


import com.alarmclock.administrator.alarmclock.Views.LoginLoadingView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private FrameLayout mFrtContent;

    private Scene mSceneSignUp;
    private Scene mSceneLogging;
    private Scene mSceneMain;

    private int mTvSighUpWidth, mTvSighUpHeight;
    private int mDuration;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference myRef;
    private static  String email = null, password = null,name = null;
    private  static boolean resultat = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        mFrtContent = (FrameLayout) findViewById(R.id.frt_content);

        mDuration = getResources().getInteger(R.integer.duration);

        mSceneSignUp = Scene.getSceneForLayout(mFrtContent, R.layout.scene_login, this);
        mSceneSignUp.setEnterAction(new Runnable() {
            @Override
            public void run() {
                final LoginLoadingView loginView = (LoginLoadingView) mFrtContent.findViewById(R.id.login_view);
                ViewTreeObserver vto = loginView.getViewTreeObserver();
                vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        setSize(loginView.getMeasuredWidth(), loginView.getMeasuredHeight());
                    }
                });
                loginView.setOnClickListener(MainActivity.this);
            }
        });


        mSceneLogging = Scene.getSceneForLayout(mFrtContent, R.layout.scene_logging, this);
        mSceneLogging.setEnterAction(new Runnable() {
            @Override
            public void run() {
                final LoginLoadingView loginView = (LoginLoadingView) mFrtContent.findViewById(R.id.login_view);
                loginView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        loginView.setStatus(LoginLoadingView.STATUS_LOGGING);
                    }
                }, mDuration);
                loginView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        loginView.setStatus(LoginLoadingView.STATUS_LOGIN_SUCCESS);
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

    private void setSize(int width, int height) {
        mTvSighUpWidth = width;
        mTvSighUpHeight = height;
    }

    public void goToSignUp(View view) {
        Intent intent = new Intent(this,SignUpActicity.class);
        startActivity(intent);

    }


    @Override
    public void onClick(final View view) {

        float finalRadius = (float) Math.hypot(mFrtContent.getWidth(), mFrtContent.getHeight());

        int[] location = new int[2];
        view.getLocationOnScreen(location);
        int x = location[0];
        int y = location[1];

        Animator anim = ViewAnimationUtils.createCircularReveal(mFrtContent, x + mTvSighUpWidth / 2, y - mTvSighUpHeight / 2, 100, finalRadius);
        mFrtContent.setBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.colorBg));
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
        checkIfRefIsAbsent();


    }
    public void goDrawer() {
        Intent intent = new Intent(this, Nav_drawer.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(intent);

    }
    public void checkIfRefIsAbsent() {

        EditText useremail = (EditText) findViewById(R.id.email_login);
        EditText pass = (EditText) findViewById(R.id.password_login);

        email = useremail.getText().toString();
        password = pass.getText().toString();
        myRef = database.getReference("user");
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot userSnapshot: snapshot.getChildren()) {
                    System.out.println(userSnapshot.child("email").getValue().toString()+userSnapshot.child("password").getValue().toString());
                    if (userSnapshot.child("email").getValue().toString().equals(email) && userSnapshot.child("password").getValue().toString().equals(password)) {
                        resultat = true;
                        name = userSnapshot.child("name").getValue().toString();
                        break;
                    }
                }
                if(!resultat) {
                    TextView erro = (TextView) findViewById(R.id.erro_login);
                    erro.setText("email or password is not correct");
                }else {
                    SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putString("email", email);  // Saving string
                    editor.putString("password", password);
                    editor.putString("name", name);
                    editor.putString("currentEmail", email);
                    editor.commit(); // commit changes
                    TransitionManager.go(mSceneLogging, new ChangeBounds().setDuration(mDuration).setInterpolator(new DecelerateInterpolator()));
                }
                resultat = false;
            }

            @Override
            public void onCancelled(DatabaseError firebaseError) { }
        });

    }


}
