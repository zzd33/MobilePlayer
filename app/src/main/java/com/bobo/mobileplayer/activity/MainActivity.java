package com.bobo.mobileplayer.activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;

import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.bobo.mobileplayer.base.BasePager;
import com.bobo.mobileplayer.base.MyFragment;
import com.bobo.mobileplayer.pager.AudioPager;
import com.bobo.mobileplayer.pager.NetAudioPager;
import com.bobo.mobileplayer.pager.NetVideoPager;
import com.example.mobileplayer.R;
import com.bobo.mobileplayer.pager.VideoPager;

import java.util.ArrayList;

/**
 * Created by Leon on 2017/12/30.
 * Functions: 主页面
 */

public class MainActivity extends FragmentActivity{

    // 获取读写权限
    private static String[] PERMISSIONS_STORAGE = {
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE};

    // 请求状态码
    private static int REQUEST_PERMISSION_CODE = 101;

    private RadioGroup rg_bottom_tag;
    /**页面的集合*/
    private ArrayList<BasePager> basePagers;
    /**选中的位置*/
    private int position;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 申请读写权限的方法
        readAndWritePermissions();

        setContentView(R.layout.activity_main);
        rg_bottom_tag = (RadioGroup) findViewById(R.id.rg_bottom_tag);

        basePagers = new ArrayList<>();
        basePagers.add(new AudioPager(this));//添加本地音频页面-1 2 2.0 故意去掉本地音乐
        basePagers.add(new NetVideoPager(this));//添加网络视频页面-2 1
        basePagers.add(new VideoPager(this));//添加本地视频页面-0 1 2
        basePagers.add(new NetAudioPager(this));// 添加搞笑视频（网络音乐）0


        //设置RadioGroup的监听
        rg_bottom_tag.setOnCheckedChangeListener(new MyOnCheckedChangeListener());
        rg_bottom_tag.check(R.id.rb_netaudio);//默认选中首页-改网络页rb_netaudio
    }
    class MyOnCheckedChangeListener implements RadioGroup.OnCheckedChangeListener {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            switch (checkedId){
                default://rb_net_video
                    position = 0;
                    break;
                case R.id.rb_net_video://网络视频rb_net_video
                    position = 1;
                    break;
                case R.id.rb_video://本地视频
                    position = 2;
                    break;
                case R.id.rb_netaudio://网络音乐  // Leon暂停开发
                    position = 3;                 // Leon暂停开发
                    break;                        // Leon暂停开发
            }

            setFragment();
        }
    }

    /**把页面添加到Fragment中*/
    private void setFragment() {
        //1.得到FragmentManger
        FragmentManager manager = getSupportFragmentManager();
        //2.开启事务
        FragmentTransaction ft = manager.beginTransaction();

        //3.替换内容 (我修改后的方法)
        BasePager basePager = getBaseView();
        MyFragment myFragment = new MyFragment(basePager);
        ft.replace(R.id.fl_main_content,myFragment);

        //3.替换内容 (源代码写法新版本Android studio报错)
//        ft.replace(R.id.fl_main_content, new Fragment(){
//            @Nullable
//            @Override
//            public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//                BasePager basePager = getBaseView();
//                if (basePager != null){
//                    //各个页面的视图
//                    return basePager.rootview;
//                }
//                return null;
//            }
//        });

        //4.提交事务
        ft.commit();
    }

    /**根据位置得到对应的界面*/
    private BasePager getBaseView() {
        BasePager basePager = basePagers.get(position);
        if (basePager != null && !basePager.isInitData){
            basePager.initData();//联网请求或者绑定数据
            basePager.isInitData = true;
        }
        return basePager;
    }

    /**标记用户是否已经退出*/
    private boolean isExit = false;

    //像其他软件一样连续点击2次退出
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode  == KeyEvent.KEYCODE_BACK){//用户点击了返回键
            if (position != 0){
                position = 0;
                rg_bottom_tag.check(R.id.rb_net_video);//回到网络视频页
                return true;
            }else if (!isExit){
                isExit = true;
                Toast.makeText(MainActivity.this,"再按一次退出",Toast.LENGTH_SHORT).show();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        isExit = false;
                    }
                },2000);
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    // 申请读写权限的方法
    private void  readAndWritePermissions(){
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            if (ActivityCompat.checkSelfPermission(MainActivity.this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(MainActivity.this, PERMISSIONS_STORAGE, REQUEST_PERMISSION_CODE);
            }
        }
    }

    /***
     * 权限请求的回调
     * @param requestCode  请求码
     * @param permissions   权限列表  3个权限
     * @param grantResults  请求结果  3个结果
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == REQUEST_PERMISSION_CODE){

            for(int i = 0;i < grantResults.length; i++){
                if(grantResults[i] != PackageManager.PERMISSION_GRANTED) {//没有授权

                    Toast.makeText(MainActivity.this,"请开启读写权限",
                            Toast.LENGTH_SHORT).show();

                    // 没有读写权限没法用直接退出程序
                    finish();
                }
            }
        }
    }


}
