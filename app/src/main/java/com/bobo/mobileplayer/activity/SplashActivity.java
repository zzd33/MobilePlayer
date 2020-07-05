package com.bobo.mobileplayer.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;

import com.example.mobileplayer.R;

public class SplashActivity extends Activity {
    private static final String TAG = SplashActivity.class.getSimpleName();
    private Handler handler = new Handler();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
            handler.postDelayed(new Runnable() {
            @Override
            public void run() {
               //2秒后才执行到这里,执行在子线程中
               startMainActivity();
                Log.e(TAG,"当前线程的名称："+Thread.currentThread().getName());
            }
        },800);//原来1500
    }

    private boolean isStartMain = false;
    /**跳转到主页面,并且把当前页面关闭掉*/
    public void  startMainActivity(){
        if (!isStartMain){
            isStartMain = true;
            Intent intent = new Intent(this,MainActivity.class);
            startActivity(intent);
            //关闭当前页面
            finish();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.e(TAG,"onTouchEvent==Action"+event.getAction());
        startMainActivity();
        return super.onTouchEvent(event);
    }

    @Override
    protected void onDestroy() {
        //把所有的消息和任务移除
        handler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }
}
