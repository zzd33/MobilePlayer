package com.bobo.mobileplayer.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mobileplayer.R;
import com.bobo.mobileplayer.domain.MediaItem;
import com.bobo.mobileplayer.utils.LogUtil;
import com.bobo.mobileplayer.utils.Utils;
import com.bobo.mobileplayer.view.VideoView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Leon on 2018/1/7.
 * Functions:系统播放器
 */

public class SystemVideoPlayer extends Activity implements View.OnClickListener {

    private boolean isUseSystem = true;//false处理视频卡转圈圈-自己写的 true系统
    /**视频进度的更新*/
    private static final int PROGRESS = 1;
    /**自动隐藏控制面板*/
    private static final int HIDE_MEDIACONTROLLER = 2;
    /**显示网络速度*/
    private static final int SHOW_SPEED = 3;
    /**全屏*/
    private static final int FULL_SCREEN = 1;
    /**默认屏幕*/
    private static final int DEFAULT_SCREEN = 2;
    private VideoView videoview;
    private Uri uri;
    private LinearLayout llTop;
    private TextView tvName;
    private ImageView ivBattery;
    private TextView tvSystemTime;
    private Button btnVoice;
    private SeekBar seekbarVoice;
    private Button btnSwithPlayer;
    private LinearLayout llBottom;
    private RelativeLayout media_controller;
    private TextView tvCurrentTime;
    private SeekBar seekbarVideo;
    private TextView tvDuration;
    private Button btnExit;
    private Button btnVideoPre;
    private Button btnVideoStartPause;
    private Button btnVideoNext;
    private Button btnVideoSiwchScreen;
    private TextView tv_buffer_netspeed;
    private LinearLayout ll_buffer;
    private TextView tv_loading_netspeed;
    private LinearLayout ll_loading;

    private Utils utils;
    /**监听电量变化的广播*/
    private MyReceiver receiver;
    /**传入进来的视频列表*/
    private ArrayList<MediaItem> mediaItems;
    /**要播放的列表中的具体位置*/
    private int position;

    /**手机震动类*/
    private Vibrator vibrator;

    /**1.定义手势识别器*/
    private GestureDetector detector;

    /**是否显示控制面板*/
    private boolean isshowMediaController = false;

    /**是否全屏*/
    private boolean isFullScreen = false;

    /**屏幕的宽*/
    private int screenWidth = 0;

    /**屏幕的高*/
    private int screenHeight = 0;

    /**视频的真实宽*/
    private int videoWidth;

    /**视频的真实高*/
    private int videoHeight;

    /**调节声音*/
    private AudioManager am;

    /**当前的音量*/
    private int currentVoice;

    /**最大的音量 0～15*/
    private int maxVoice;

    /**是否是静音*/
    private boolean isMute = false;

    /**记录用户滑动的起始位置*/
    private float startY;

    private float startX;

    /**屏幕的高*/
    private float touchRang;

    /**当一按下的音量*/
    private int mVol;

    /**是否是网络的uri*/
    private boolean isNetUri;

    /**上一次的播放进度*/
    private int precurrentPosition;


    /**
     * Find the Views in the layout<br />
     * <br />
     * Auto-created on 2018-01-14 20:51:35 by Android Layout Finder
     * (http://www.buzzingandroid.com/tools/android-layout-finder)
     */
    private void findViews() {
        setContentView(R.layout.activity_system_video_player);
        llTop = (LinearLayout)findViewById( R.id.ll_top );
        tvName = (TextView)findViewById( R.id.tv_name );
        ivBattery = (ImageView)findViewById( R.id.iv_battery );
        tvSystemTime = (TextView)findViewById( R.id.tv_system_time );
        btnVoice = (Button)findViewById( R.id.btn_voice );
        seekbarVoice = (SeekBar)findViewById( R.id.seekbar_voice );
        btnSwithPlayer = (Button)findViewById( R.id.btn_swith_player );
        llBottom = (LinearLayout)findViewById( R.id.ll_bottom );
        tvCurrentTime = (TextView)findViewById( R.id.tv_current_time );
        seekbarVideo = (SeekBar)findViewById( R.id.seekbar_video );
        tvDuration = (TextView)findViewById( R.id.tv_duration );
        btnExit = (Button)findViewById( R.id.btn_exit );
        btnVideoPre = (Button)findViewById( R.id.btn_video_pre );
        btnVideoStartPause = (Button)findViewById( R.id.btn_video_start_pause );
        btnVideoNext = (Button)findViewById( R.id.btn_video_next );
        btnVideoSiwchScreen = (Button)findViewById( R.id.btn_video_siwch_screen );
        videoview = (VideoView)findViewById(R.id.videoview);
        media_controller = (RelativeLayout) findViewById(R.id.media_controller);
        tv_buffer_netspeed = (TextView)findViewById(R.id.tv_buffer_netspeed);
        ll_buffer = (LinearLayout)findViewById(R.id.ll_buffer);
        tv_loading_netspeed = (TextView)findViewById(R.id.tv_loading_netspeed);
        ll_loading = (LinearLayout)findViewById(R.id.ll_loading);

        btnVoice.setOnClickListener( this );
        btnSwithPlayer.setOnClickListener( this );
        btnExit.setOnClickListener( this );
        btnVideoPre.setOnClickListener( this );
        btnVideoStartPause.setOnClickListener( this );
        btnVideoNext.setOnClickListener( this );
        btnVideoSiwchScreen.setOnClickListener( this );

        //最大音量和seekBar关联
        seekbarVoice.setMax(maxVoice);
        //设置seekBar当前的进度-当前音量
        seekbarVoice.setProgress(currentVoice);

        //开始更新网络速度
        handler.sendEmptyMessage(SHOW_SPEED);
    }

    /**
     * Handle button click events<br />
     * <br />
     * Auto-created on 2018-01-14 20:51:35 by Android Layout Finder
     * (http://www.buzzingandroid.com/tools/android-layout-finder)
     */
    @Override
    public void onClick(View v) {
        if ( v == btnVoice ) {
            // 点击了静音喇叭
            isMute = !isMute;
            updataVoice(currentVoice,isMute);
        } else if ( v == btnSwithPlayer ) {
            // 点击了切换到万能播放器的按钮
            showSwichPlayerDialog();
        } else if ( v == btnExit ) {
            // 退出
            finish();
        } else if ( v == btnVideoPre ) {
            // 点击了上一个视频的按钮
            playPreVideo();
        } else if ( v == btnVideoStartPause ) {
            startAndPause();
        } else if ( v == btnVideoNext ) {
            // 点击了下一个视频的 按钮
            playNextVideo();
        } else if ( v == btnVideoSiwchScreen ) {
            // 点击了最佳宽高比 和全屏 切换按钮
            setFullScreenAndDefault();
        }

        //一旦用户点击了某个按钮 自动隐藏控制面板的消息重新发送一次
        handler.removeMessages(HIDE_MEDIACONTROLLER);
        handler.sendEmptyMessageDelayed(HIDE_MEDIACONTROLLER,2600);
    }

    /**系统播放器切换到万能播放器*/
    private void showSwichPlayerDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("系统播放器提醒您");
        builder.setMessage("当您播放视频，有声音没有画面的时候，请切换到万能播放器。");
        builder.setPositiveButton("确定",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                statVitamioPlayer();
            }
        });
        builder.setNegativeButton("取消",null);
        builder.show();
    }

    /**播放和暂停点击事件的处理*/
    private void startAndPause() {
        // video的播放和暂停的切换
        if (videoview.isPlaying()){
            //视频在播放-设置为暂停
            videoview.pause();
            //按钮状态要设置为播放状态
            btnVideoStartPause.setBackgroundResource(R.drawable.btn_video_start_selector);
        }else {
            //视频暂停-设置为视频播放
            videoview.start();
            //按钮的状态设置为暂停
            btnVideoStartPause.setBackgroundResource(R.drawable.btn_video_pause_selector);
        }
    }

    /**播放上一个视频*/
    private void playPreVideo() {
        if (mediaItems != null && mediaItems.size() >0){
            //播放上一个
            position--;
            if (position >= 0){
                ll_loading.setVisibility(View.VISIBLE);//加载蒙版
                MediaItem mediaItem = mediaItems.get(position);
                tvName.setText(mediaItem.getName());
                isNetUri = utils.isNetUri(mediaItem.getData());
                videoview.setVideoPath(mediaItem.getData());

                //设置按钮状态
                setButtonState();
            }
        }else if (uri != null){
            //设置按钮状态 上一个和下一个按钮设置成灰色并且不可以点击
            setButtonState();
        }
    }
    /**播放下一个视频*/
    private void playNextVideo() {
        if (mediaItems != null && mediaItems.size() >0){
            //播放下一个
            position++;
            if (position < mediaItems.size()){
                ll_loading.setVisibility(View.VISIBLE);//加载蒙版
                MediaItem mediaItem = mediaItems.get(position);
                tvName.setText(mediaItem.getName());
                isNetUri = utils.isNetUri(mediaItem.getData());
                videoview.setVideoPath(mediaItem.getData());

                //设置按钮状态
                setButtonState();
            }
        }else if (uri != null){
            //设置按钮状态 上一个和下一个按钮设置成灰色并且不可以点击
            setButtonState();
        }
    }

    private void setButtonState() {
        if (mediaItems != null && mediaItems.size() > 0){
            if (mediaItems.size() ==  1){//只有1个视频的时候
                setEnable(false);
            } else if (mediaItems.size() == 2) {//只有2个视频的时候
                if (position == 0) {//上一个不可点击 下一个可以点击
                    btnVideoPre.setBackgroundResource(R.drawable.btn_pre_gray);
                    btnVideoPre.setEnabled(false);
                    btnVideoNext.setBackgroundResource(R.drawable.btn_video_next_selector);
                    btnVideoNext.setEnabled(true);
                } else if (position == mediaItems.size() - 1) {//下一个不可点击 上一个可以点击
                    btnVideoNext.setBackgroundResource(R.drawable.btn_next_gray);
                    btnVideoNext.setEnabled(false);
                    btnVideoPre.setBackgroundResource(R.drawable.btn_video_pre_selector);
                    btnVideoPre.setEnabled(true);
                }
            } else {//3个视频以上的时候
                if (position == 0) {//上一个不可点击
                    btnVideoPre.setBackgroundResource(R.drawable.btn_pre_gray);
                    btnVideoPre.setEnabled(false);
                } else if (position == mediaItems.size() - 1) {//下一个不可点击
                    btnVideoNext.setBackgroundResource(R.drawable.btn_next_gray);
                    btnVideoNext.setEnabled(false);
                } else {//上一个 下一个 按钮都可以点击
                    setEnable(true);
                }
            }
        }else if (uri != null){
            //两个按钮都设置成灰色 且 不能点击
            setEnable(false);
        }
    }

    private void setEnable(boolean isEnable) {
        //两个按钮都设置成灰色 且 不能点击
        if (isEnable){
            btnVideoPre.setBackgroundResource(R.drawable.btn_video_pre_selector);
            btnVideoPre.setEnabled(true);
            btnVideoNext.setBackgroundResource(R.drawable.btn_video_next_selector);
            btnVideoNext.setEnabled(true);
        }else {
            btnVideoPre.setBackgroundResource(R.drawable.btn_pre_gray);
            btnVideoPre.setEnabled(false);
            btnVideoNext.setBackgroundResource(R.drawable.btn_next_gray);
            btnVideoNext.setEnabled(false);
        }

    }

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case SHOW_SPEED://显示网速
                    //得到网路速度
                    String netSpeed = utils.getNetSpeed(SystemVideoPlayer.this);

                    //显示网络速度
                    tv_loading_netspeed.setText("波波加载中..."+netSpeed);
                    tv_buffer_netspeed.setText("波波缓存中.."+netSpeed);

                    //2.每两秒中调用一次
                    handler.removeMessages(SHOW_SPEED);
                    handler.sendEmptyMessageDelayed(SHOW_SPEED,2000);

                    break;
                case HIDE_MEDIACONTROLLER://隐藏控制面板
                    hideMediaController();
                    break;
                case PROGRESS:
                    //1.得到当前的视频播放进程
                    int currentPosition = videoview.getCurrentPosition();
                    //2.seekbar.setProgress(当前进度);
                    seekbarVideo.setProgress(currentPosition);
                    //更新文本播放进度
                    tvCurrentTime.setText(utils.stringForTime(currentPosition));

                    //设置系统时间
                    tvSystemTime.setText(getSysteTime());

                    //缓存进度更新
                    if (isNetUri){
                        //只有网络资源才有缓存效果
                        int buffer = videoview.getBufferPercentage();
                        int totalBuffer = buffer * seekbarVideo.getMax();
                        int secondaryProgress = totalBuffer / 100;
                        seekbarVideo.setSecondaryProgress(secondaryProgress);
                    }else {
                        //本地的视频没有缓冲效果
                        seekbarVideo.setSecondaryProgress(0);
                    }

                    //监听卡-（自己写的方法）
                    if (!isUseSystem){//&& videoview.isPlaying()

                        if (videoview.isPlaying()) {
                            int buffer = currentPosition - precurrentPosition;
                            if (buffer < 500) {
                                //视频卡了
                                ll_buffer.setVisibility(View.VISIBLE);
                            } else {
                                //视频不卡了
                                ll_buffer.setVisibility(View.GONE);
                            }
                        }else {
                            ll_buffer.setVisibility(View.GONE);
                        }
                    }

                    precurrentPosition = currentPosition;

                    //3.每秒更新一次
                    handler.removeMessages(PROGRESS);
                    handler.sendEmptyMessageDelayed(PROGRESS,1000);

                    break;
            }
        }
    };

    /**得到系统时间*/
    public String getSysteTime() {
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
        return format.format(new Date());
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);//初始化父类
        LogUtil.e("onCreate--");

        initData();

        findViews();

        setListener();

        getDate();

        setData();
        //设置控制面板
       // videoview.setMediaController(new MediaController(this));
    }

    private void setData() {
        if (mediaItems != null && mediaItems.size() > 0){
            MediaItem mediaItem = mediaItems.get(position);
            tvName.setText(mediaItem.getName());//设置视频的名称
            isNetUri = utils.isNetUri(mediaItem.getData());
            videoview.setVideoPath(mediaItem.getData());
        }else if (uri != null){
            tvName.setText(uri.toString());//设置视频的名称
            isNetUri = utils.isNetUri(uri.toString());
            videoview.setVideoURI(uri);
        }else {
            Toast.makeText(SystemVideoPlayer.this,"没有资源或资源损坏",Toast.LENGTH_SHORT).show();
        }
        setButtonState();
    }

    private void getDate() {
        //得到播放地址
        uri = getIntent().getData();//文件夹 ES文件浏览器，QQ空间
        mediaItems = (ArrayList<MediaItem>) getIntent().getSerializableExtra("videolist");
        position = getIntent().getIntExtra("postion",0);
    }

    private void initData() {
        utils = new Utils();
        //注册电量广播
        receiver = new MyReceiver();
        IntentFilter intentFilter = new IntentFilter();
        //当电量变化的时候发这个广播
        intentFilter.addAction(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(receiver, intentFilter);

        //2.实例化手势识别器，并且重写 双击，单击，长按。
        detector = new GestureDetector(this,new MySimpleOnGestureListener());

        //得到屏幕的宽和高
        // 过时的方法
        //screenWidth = getWindowManager().getDefaultDisplay().getWidth();
        //screenHeight = getWindowManager().getDefaultDisplay().getHeight();

        //得到屏幕宽高的最新的方式
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        screenWidth = displayMetrics.widthPixels;
        screenHeight = displayMetrics.heightPixels;

        //得到音量
        am = (AudioManager) getSystemService(AUDIO_SERVICE);
        currentVoice = am.getStreamVolume(AudioManager.STREAM_MUSIC);
        maxVoice = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
    }

    class MySimpleOnGestureListener extends GestureDetector.SimpleOnGestureListener{
        @Override
        public void onLongPress(MotionEvent e) {
            super.onLongPress(e);
            //Toast.makeText(SystemVideoPlayer.this,"我被长按了",Toast.LENGTH_SHORT).show();
            startAndPause();
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            // Toast.makeText(SystemVideoPlayer.this,"我被双击了",Toast.LENGTH_SHORT).show();
            setFullScreenAndDefault();
            return super.onDoubleTap(e);
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            // Toast.makeText(SystemVideoPlayer.this,"我被单机了",Toast.LENGTH_SHORT).show();
            if (isshowMediaController){
                //隐藏
                hideMediaController();
                //把隐藏消息移除
                handler.removeMessages(HIDE_MEDIACONTROLLER);
            }else {
                //显示
                showMediaController();
                //发消息隐藏
                handler.sendEmptyMessageDelayed(HIDE_MEDIACONTROLLER,2600);
            }
            return super.onSingleTapConfirmed(e);
        }
    }

    private void setFullScreenAndDefault() {
        if (isFullScreen){
            //默认
            setVideoType(DEFAULT_SCREEN);
        }else {
            //全屏
            setVideoType(FULL_SCREEN);
        }
    }

    private void setVideoType(int defaultScreen) {
        switch (defaultScreen){
            case FULL_SCREEN ://全屏
                //1.设置视频画面的大小-屏幕有多大视频就用多大
                videoview.setVideoSize(screenWidth,screenHeight);
                //2.设置按钮的状态-默认
                btnVideoSiwchScreen.setBackgroundResource(R.drawable.btn_video_siwch_screen_default_selector);
                //3.修改状态
                isFullScreen = true;
                break;
            case DEFAULT_SCREEN://默认最佳宽高
                //1.设置视频画面的大小
                //视频真实的宽和高
                int mVideoWidth = videoWidth;
                int mVideoHeight = videoHeight;

                //屏幕的宽和高
                int width = screenWidth;
                int height = screenHeight;

                if (mVideoWidth * height < width * mVideoHeight){
                    width = height * mVideoWidth / mVideoHeight;
                }else if (mVideoWidth * height > width * mVideoHeight){
                    height = width * mVideoHeight / mVideoWidth;
                }
                videoview.setVideoSize(width,height);
                //2.设置按钮的状态-全屏
                btnVideoSiwchScreen.setBackgroundResource(R.drawable.btn_video_siwch_screen_full_selector);
                //3.修改状态
                isFullScreen = false;
                break;
        }
    }

    class MyReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            int level = intent.getIntExtra("level",0);//0~100
            setBattery(level);
        }
    }

    private void setBattery(int level) {
        if (level <= 0 ){
            ivBattery.setImageResource(R.drawable.ic_battery_0);
        }else if (level <= 10){
            ivBattery.setImageResource(R.drawable.ic_battery_10);
        }else if (level <= 20){
            ivBattery.setImageResource(R.drawable.ic_battery_20);
        }else if (level <= 40){
            ivBattery.setImageResource(R.drawable.ic_battery_40);
        }else if (level <= 60){
            ivBattery.setImageResource(R.drawable.ic_battery_60);
        }else if (level <= 80){
            ivBattery.setImageResource(R.drawable.ic_battery_80);
        }else if (level <= 100){
            ivBattery.setImageResource(R.drawable.ic_battery_100);
        }else {
            ivBattery.setImageResource(R.drawable.ic_battery_100);
        }
    }

    private void setListener() {
        //准备好的监听
        videoview.setOnPreparedListener(new MyOnPreparedListener());

        //播放出错的监听
        videoview.setOnErrorListener(new MyOnErrorListener());

        //播放完成了的监听
        videoview.setOnCompletionListener(new MyOnCompletionListener());

        //设置视频seekbar状态变化的监听
        seekbarVideo.setOnSeekBarChangeListener(new VideoOnSeekBarChangeListener());

        //设置音频seekbar状态变化的监听
        seekbarVoice.setOnSeekBarChangeListener(new VoiceOnSeekBarChangeListener());

        if (isUseSystem){
            //监听视频播放卡-系统的api
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                videoview.setOnInfoListener(new MyOnInfoListener());
            }
        }

    }

    class MyOnInfoListener implements MediaPlayer.OnInfoListener{

        @Override
        public boolean onInfo(MediaPlayer mp, int what, int extra) {

            switch (what){
                case MediaPlayer.MEDIA_INFO_BUFFERING_START://视频开始卡了，拖动卡
                   // Toast.makeText(SystemVideoPlayer.this,"开始卡了",Toast.LENGTH_SHORT).show();
                    ll_buffer.setVisibility(View.VISIBLE);
                    break;
                case MediaPlayer.MEDIA_INFO_BUFFERING_END://视频卡结束了,拖动卡结束了
                    //Toast.makeText(SystemVideoPlayer.this,"不卡了",Toast.LENGTH_SHORT).show();
                    ll_buffer.setVisibility(View.GONE);
                    break;
            }
            return true;
        }
    }

    class VoiceOnSeekBarChangeListener implements SeekBar.OnSeekBarChangeListener{

        /**
         * 当手指滑动的时候，会引起seekbar进度变化，会回掉这个方法
         * @param fromUser 如果是用户引起的为true，不是用户引起的false
         */
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (fromUser){
                if (progress > 0){
                    isMute = false;
                }else {
                    isMute = true;
                }
                updataVoice(progress,isMute);
            }
        }
        //当手指触碰的时候回掉这个方法
        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            handler.removeMessages(HIDE_MEDIACONTROLLER);
        }
        //当手指离开的时候回掉这个方法
        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            handler.sendEmptyMessageDelayed(HIDE_MEDIACONTROLLER,2600);
        }
    }

    /**设置音量的大小*/
    private void updataVoice(int progress,boolean isMute) {
        if (isMute){//静音
            am.setStreamVolume(AudioManager.STREAM_MUSIC,0,0);
            seekbarVoice.setProgress(0);
        }else {
            am.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 1);
            seekbarVoice.setProgress(progress);
            currentVoice = progress;
        }
    }

    class VideoOnSeekBarChangeListener implements SeekBar.OnSeekBarChangeListener{
        /**
         * 当手指滑动的时候，会引起seekbar进度变化，会回掉这个方法
         * @param fromUser 如果是用户引起的为true，不是用户引起的false
         */
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (fromUser){
                videoview.seekTo(progress);
            }
        }
        //当手指触碰的时候回掉这个方法
        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            handler.removeMessages(HIDE_MEDIACONTROLLER);
        }
        //当手指离开的时候回掉这个方法
        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            handler.sendEmptyMessageDelayed(HIDE_MEDIACONTROLLER,2600);
        }
    }

    class MyOnPreparedListener implements MediaPlayer.OnPreparedListener {
        //当底层解码准备好的时候
        @Override
        public void onPrepared(MediaPlayer mp) {
            videoWidth = mp.getVideoWidth();
            videoHeight = mp.getVideoHeight();

            videoview.start();//视频已经准备就绪start开始播放
            //1.视频的总时长，关联总长度
            int duration =  mp.getDuration();
            seekbarVideo.setMax(duration);
            tvDuration.setText(utils.stringForTime(duration));

            hideMediaController();//默认是隐藏控制面板
            //2.发消息
            handler.sendEmptyMessage(PROGRESS);

            //videoview.setVideoSize(200,200);
            //videoview.setVideoSize(mp.getVideoWidth(),mp.getVideoHeight());

            //屏幕的默认播放
            setVideoType(DEFAULT_SCREEN);

            //把加载页面消失掉
            ll_loading.setVisibility(View.GONE);

//            mp.setOnSeekCompleteListener(new MediaPlayer.OnSeekCompleteListener() {//监听用户拖动
//                @Override
//                public void onSeekComplete(MediaPlayer mp) {
//                    Toast.makeText(SystemVideoPlayer.this,"拖动完成",Toast.LENGTH_SHORT).show();
//                }
//            });
        }
    }

    class MyOnErrorListener implements MediaPlayer.OnErrorListener {

        @Override
        public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
//            Toast.makeText(SystemVideoPlayer.this,"文件已经不存在或格式不正确...",Toast.LENGTH_SHORT).show();
            //1.播放视频格式不支持--跳转到万能播放器继续播放
            statVitamioPlayer();
            //2.播放网络视频的时候网络中断--1.网络确实断了，可以提示用户网络断了;2.网络断断续续的，重新播放

            //3.播放视频的时候本地文件中间有空白--下载做完成
            return true;
        }
    }
    /** a,把数据按照原样传入VitamioVideoPlayer
     *  b,关闭系统播放器
     **/
    private void statVitamioPlayer() {

        if (videoview != null){
            videoview.stopPlayback();
        }

        Intent intent = new Intent(this,VitamioVideoPlayer.class);

        if (mediaItems != null && mediaItems.size() > 0){
            Bundle bundle = new Bundle();
            bundle.putSerializable("videolist",mediaItems);
            intent.putExtras(bundle);
            intent.putExtra("postion",position);

        }else if (uri != null){
            intent.setData(uri);
        }
        startActivity(intent);

        finish();//关闭页面
    }

    class MyOnCompletionListener implements MediaPlayer.OnCompletionListener{

        @Override
        public void onCompletion(MediaPlayer mp) {
            MediaItem mediaItem = mediaItems.get(position);//我自己加的+mediaItem.getName()
            Toast.makeText(SystemVideoPlayer.this,"播放完成："+mediaItem.getName(),Toast.LENGTH_SHORT).show();
            // Toast.makeText(SystemVideoPlayer.this,"播放完成了="+uri,Toast.LENGTH_SHORT).show();
            playNextVideo();//自动播放下一个视频
        }
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        LogUtil.e("onRestart--");
    }

    @Override
    protected void onStart() {
        super.onStart();
        LogUtil.e("onStart--");
    }

    @Override
    protected void onResume() {
        super.onResume();
        LogUtil.e("onResume--");
    }

    @Override
    protected void onPause() {
        super.onPause();
        LogUtil.e("onPause--");
    }

    @Override
    protected void onStop() {
        super.onStop();
        LogUtil.e("onStop--");
    }

    @Override
    protected void onDestroy() {

        // 移除所有的消息
        handler.removeCallbacksAndMessages(null);

        //释放资源的时候，先释放子类，在释放父类
        if (receiver != null) {
            unregisterReceiver(receiver);
            receiver = null;
        }
        LogUtil.e("onDestroy--");
        super.onDestroy();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //3.把事件传递给手势识别器
        detector.onTouchEvent(event);
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN://手指按下
                //1.按下记录值
                startY = event.getY();
                startX = event.getX();
                mVol = am.getStreamVolume(AudioManager.STREAM_MUSIC);
                touchRang = Math.min(screenHeight,screenWidth);//screenHeight
                handler.removeMessages(HIDE_MEDIACONTROLLER);
                break;
            case MotionEvent.ACTION_MOVE://手指移动
                //2.移动的记录相关的值
                float endY = event.getY();
                float endX= event.getX();
                float distanceY = startY - endY;

                if (endX < screenWidth / 2){
                    //左边（上边）屏幕-调节亮度
                    final double FLING_MIN_DISTANCE = 0.5;
                    final double FLING_MIN_VELOCITY = 0.5;
                    if (distanceY > FLING_MIN_DISTANCE && Math.abs(distanceY) > FLING_MIN_VELOCITY){
                        setBrightness(20);//20
                    }
                    if (distanceY < FLING_MIN_DISTANCE && Math.abs(distanceY) > FLING_MIN_VELOCITY){
                     setBrightness(-20);//20
                    }
                }else {//右边（下面）屏幕-调节声音
                    //改变声音=（滑动屏幕的距离：总距离）* 音量最大值
                    float delta = (distanceY / touchRang) * maxVoice;
                    //最终声音 = 原来的 + 改变声音
                    int voice = (int) Math.min(Math.max(mVol + delta,0),maxVoice);
                    if (delta != 0){
                        isMute = false;
                        updataVoice(voice,false);
                    }
                }
                break;
            case MotionEvent.ACTION_UP://手指离开
                handler.sendEmptyMessageDelayed(HIDE_MEDIACONTROLLER,2600);
                break;
        }
        return super.onTouchEvent(event);
    }


    /**设置屏幕的亮度 lp = 0全暗 , lp = -1 根据系统设置，lp = 1 最亮*/
    @SuppressLint("MissingPermission")
    public void setBrightness(float brightness){
        WindowManager.LayoutParams lp = getWindow().getAttributes();

        lp.screenBrightness = lp.screenBrightness + brightness / 255.0f;

        if (lp.screenBrightness > 1){
            lp.screenBrightness = 1;
            vibrator = (Vibrator)getSystemService(VIBRATOR_SERVICE);
            long[] pattern = {10,200};//震动 OFF/ON/OFF/ON
            if (vibrator != null){
                vibrator.vibrate(pattern,-1);
            }
        }else if (lp.screenBrightness < 0.2){
            lp.screenBrightness = (float)0.2;
            vibrator = (Vibrator)getSystemService(VIBRATOR_SERVICE);
            long[] pattern = {10,200};//震动 OFF/ON/OFF/ON
            if (vibrator != null){
                vibrator.vibrate(pattern,-1);
            }
        }

        LogUtil.e("lp.screenBrightness == "+lp.screenBrightness );
        getWindow().setAttributes(lp);
    }

    /**显示控制面板*/
    private void showMediaController(){
        media_controller.setVisibility(View.VISIBLE);
        isshowMediaController = true;
    }

    /**隐藏控制面板*/
    private void hideMediaController(){
        media_controller.setVisibility(View.GONE);
        isshowMediaController = false;
    }

    /**监听物理键，实现声音的调节大小*/
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN){
            currentVoice--;
            //-----------leon-----------------
            if (currentVoice > 0){
                isMute = false;
                updataVoice(currentVoice,false);
            }
            //-----------leon-----------------
           // updataVoice(currentVoice,false);原来
            handler.removeMessages(HIDE_MEDIACONTROLLER);
            handler.sendEmptyMessageDelayed(HIDE_MEDIACONTROLLER,2600);
            return true;
        }else if (keyCode == KeyEvent.KEYCODE_VOLUME_UP){
            currentVoice++;
            //-----------leon-----------------
            if (currentVoice > 0){
                isMute = false;
                updataVoice(currentVoice,false);
            }
            //-----------leon-----------------
            //updataVoice(currentVoice,false);原来
            handler.removeMessages(HIDE_MEDIACONTROLLER);
            handler.sendEmptyMessageDelayed(HIDE_MEDIACONTROLLER,2600);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}

//        之前写这个方法是跳转到 TestB 的测试方法。public boolean onTouchEvent(MotionEvent event)中
//        if (event.getAction() == MotionEvent.ACTION_DOWN){
//            Intent intent = new Intent(this,TestB.class);
//            startActivity(intent);
//            return true;
//        }


