package com.utt.potholes;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;


import com.github.ybq.android.spinkit.style.DoubleBounce;
import com.utt.utils.CheckGoogleAccountStatus;

import java.util.Locale;

public class WelcomeActivity extends AppCompatActivity {

    Animation topAnimation, bottomAAnimation;
    ImageView imageLogofl;
    TextView txtTitleMain, txtSlogan;
    ProgressBar progressBar;
    private  SharedPreferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        //loadLanguageSaved();

        //Animations
        topAnimation = AnimationUtils.loadAnimation(this, R.anim.top_animation);
        bottomAAnimation = AnimationUtils.loadAnimation(this, R.anim.bottom_animation);

//        progressBar = (ProgressBar) findViewById(R.id.progress_bar_main);
//        progressBar.setMax(100);
//        progressBar.setVisibility(View.GONE);

        txtTitleMain = (TextView) findViewById(R.id.txtTitleMain);
        txtSlogan = (TextView) findViewById(R.id.txtSlogan);
        imageLogofl = (ImageView) findViewById(R.id.imgLogoMain);
        txtSlogan.setVisibility(View.GONE);

        txtTitleMain.setAnimation(bottomAAnimation);
        txtSlogan.setAnimation(bottomAAnimation);
        imageLogofl.setAnimation(topAnimation);
        progressBar = findViewById(R.id.progress_bar_main);
        progressBar.setIndeterminateDrawable(new DoubleBounce());


        final boolean status = CheckGoogleAccountStatus.getcheckDataAccount(this);
//        startProgcessBar();

        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        if (status){
//                            Intent intent = new Intent(MainActivity.this, HomeActivity.class);
//
//                            Pair[] pairs = new Pair[2];
//                            pairs[0] = new Pair<View,String>(imageLogofl,"trans_logo");
//                            pairs[1] = new Pair<View,String>(imageLogofl,"trans_logo");
//
//                            ActivityOptions activityOptions = ActivityOptions.makeSceneTransitionAnimation(MainActivity.this, pairs);
//                            startActivity(intent, activityOptions.toBundle());
//                            finish();
//                            overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                            Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                            overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);

                        }else{
//                            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
//                            Pair[] pairs = new Pair[2];
//                            pairs[0] = new Pair<View,String>(imageLogofl,"trans_logo");
//                            pairs[1] = new Pair<View,String>(imageLogofl,"trans_logo");
//
//                            ActivityOptions activityOptions = ActivityOptions.makeSceneTransitionAnimation(MainActivity.this, pairs);
//                            startActivity(intent, activityOptions.toBundle());
//                            overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
//                            finish();

                            Intent intent = new Intent(WelcomeActivity.this, LoginActivity.class);
                            startActivity(intent);
                            finish();
                            overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);

                        }
                    }
                }, 3500);
    }
//    @SuppressLint("SetTextI18n")
//    public static void loadLanguageSaved(){
//        pref = getSharedPreferences("Settings", Activity.MODE_PRIVATE);
//        String language = pref.getString("myLanguage","");
//        Log.e("lang", language+"");
//        Locale locale = new Locale(language);
//        Locale.setDefault(locale);
//        Configuration configuration = new Configuration();
//        configuration.locale = locale;
//        getBaseContext().getResources().updateConfiguration(configuration, getBaseContext().getResources().getDisplayMetrics());
//        SharedPreferences.Editor edit = getSharedPreferences("Settings", Context.MODE_PRIVATE).edit();
//        edit.putString("myLanguage", language);
//        edit.apply();
//        edit.commit();
//    }

}
