package com.utt.potholes;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import com.airbnb.lottie.LottieAnimationView;
import com.github.ybq.android.spinkit.style.DoubleBounce;
import com.utt.utils.CheckGoogleAccountStatus;

import java.util.Locale;

public class WelcomeActivity extends AppCompatActivity {

    LottieAnimationView lottieAnimationView1,lottieAnimationView2;
    TextView txtNameApp;
    ProgressBar progressBar;
    private  SharedPreferences pref;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        //loadLanguageSaved();

        txtNameApp = (TextView) findViewById(R.id.txtAppName);
        lottieAnimationView1 = findViewById(R.id.logoAnimLogo);
        lottieAnimationView2 = findViewById(R.id.logoAnimLogo);
        txtNameApp.animate().translationX(1400).setDuration(1000).setStartDelay(5000);
        //lottieAnimationView.animate().translationY(-1600).setDuration(1000).setStartDelay(4000);
//        progressBar = findViewById(R.id.progress_bar_main);
//        progressBar.setIndeterminateDrawable(new DoubleBounce());
        checkLogin();

    }
    private void checkLogin(){
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
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                    // TODO: Consider calling
                                    //    Activity#requestPermissions
                                    // here to request the missing permissions, and then overriding
                                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                    //                                          int[] grantResults)
                                    // to handle the case where the user grants the permission. See the documentation
                                    // for Activity#requestPermissions for more details.
                                    return;
                                }
                            }
                            Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                            //overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);

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
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                    // TODO: Consider calling
                                    //    Activity#requestPermissions
                                    // here to request the missing permissions, and then overriding
                                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                    //                                          int[] grantResults)
                                    // to handle the case where the user grants the permission. See the documentation
                                    // for Activity#requestPermissions for more details.
                                    return;
                                }
                            }
                            Intent intent = new Intent(WelcomeActivity.this, LoginActivity.class);
                            startActivity(intent);
                            finish();
                            overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);

                        }
                    }
                }, 5800);
    }


}
