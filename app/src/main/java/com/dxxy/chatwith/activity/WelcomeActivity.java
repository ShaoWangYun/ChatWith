package com.dxxy.chatwith.activity;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;

import com.dxxy.code.chatwith.R;

/**
 *  欢迎界面
 */
public class WelcomeActivity extends AppCompatActivity {

    private Handler mHandler = new Handler();
    private ImageView img_welcome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        img_welcome = findViewById(R.id.img_welcome);
        startAlphaAnimation();
        mHandler.postDelayed(r, 3000);
    }

    public void startAlphaAnimation(){
        AlphaAnimation alphaAnimation = new AlphaAnimation(0.1f, 1.0f);
        alphaAnimation.setDuration(3000);
        img_welcome.startAnimation(alphaAnimation);

    }

    Runnable r = new Runnable() {
        @Override
        public void run() {
            Intent intent = new Intent();
            intent.setClass(WelcomeActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
    };

    @Override
    protected void onDestroy() {
        if(mHandler!=null){
            mHandler.removeCallbacks(r);
            mHandler = null;
        }
        super.onDestroy();
    }
}
