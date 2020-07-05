package com.bobo.mobileplayer.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.drawable.AnimationDrawable;
import android.media.audiofx.Visualizer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bobo.mobileplayer.domain.MediaItem;
import com.bobo.mobileplayer.utils.LeTool;
import com.bobo.mobileplayer.utils.LyricUtils;
import com.bobo.mobileplayer.utils.Utils;
import com.bobo.mobileplayer.view.ShowLyricView;
import com.example.mobileplayer.IMusicPlayerService;
import com.example.mobileplayer.R;
import com.bobo.mobileplayer.service.MusicPlayerService;
import com.bobo.mobileplayer.view.BaseVisualizerView;

//import org.greenrobot.eventbus.EventBus;
//import org.greenrobot.eventbus.Subscribe;
//import org.greenrobot.eventbus.ThreadMode;

import java.io.File;

import de.greenrobot.event.EventBus;
import de.greenrobot.event.ThreadMode;


/**
 * Created by Leon on 2018/3/25.
 * Functions: 音频播放器
 */
public class AudioPlayerActivity extends Activity implements View.OnClickListener {

    /**进度更新*/
    private static final int PROGRESS = 1;
    /**显示歌词*/
    private static final int SHOW_LYRIC = 2;
    //校验播放-暂停的状态没0.5秒发一次
    private static final int CHECK_STATUS = 3;

    private int position;
    /**true：来自状态栏，不需要重新播放 false：从播放列表进入*/
    private boolean notification;
    private IMusicPlayerService service;//服务的代理类，通过它可以调用服务的方法
    private RelativeLayout relativeLeon;//Leon
    private ImageView ivIcon;
    private TextView tvArtist;
    private TextView tvName;
    private TextView tvTime;
    private SeekBar seekbarAudio;
    private Button btnAudioPalymode;
    private Button btnAudioPre;
    private Button btnAudioStartPause;
    private Button btnAudioNext;
    private Button btnLyrc;
    private ShowLyricView showLyricView;
    private BaseVisualizerView baseVisualizerView;

    private MyReceiver receiver;
    private Utils utils;



    /**
     * Find the Views in the layout<br />
     * <br />
     * Auto-created on 2018-03-31 16:13:52 by Android Layout Finder
     * (http://www.buzzingandroid.com/tools/android-layout-finder)
     */

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)//Leon
    private void findViews() {
        setContentView(R.layout.activity_audioplayer);

        //-----------leon设置随机的播放背景图片----↓--------------------
        relativeLeon = (RelativeLayout)findViewById(R.id.relativeLeon);
        relativeLeon.setBackground(LeTool.randomPictureAddress(this));
        //-----------leon设置随机的播放背景图片---↑---------------------

        ivIcon = (ImageView)findViewById( R.id.iv_icon );
        ivIcon.setBackgroundResource(R.drawable.animation_list);
        AnimationDrawable rocketAnimation = (AnimationDrawable)ivIcon.getBackground();
        rocketAnimation.start();
        tvArtist = (TextView)findViewById( R.id.tv_artist );
        tvName = (TextView)findViewById( R.id.tv_name );
        tvTime = (TextView)findViewById( R.id.tv_time );
        seekbarAudio = (SeekBar)findViewById( R.id.seekbar_audio );
        btnAudioPalymode = (Button)findViewById( R.id.btn_audio_palymode );
        btnAudioPre = (Button)findViewById( R.id.btn_audio_pre );
        btnAudioStartPause = (Button)findViewById( R.id.btn_audio_start_pause );
        btnAudioNext = (Button)findViewById( R.id.btn_audio_next );
        btnLyrc = (Button)findViewById( R.id.btn_lyrc );
        showLyricView = (ShowLyricView)findViewById(R.id.showLyricView);
        baseVisualizerView = (BaseVisualizerView)findViewById(R.id.baseVisualizerView);

        btnAudioPalymode.setOnClickListener( this );
        btnAudioPre.setOnClickListener( this );
        btnAudioStartPause.setOnClickListener( this );
        btnAudioNext.setOnClickListener( this );
        btnLyrc.setOnClickListener( this );

        //设置音频的拖动
        seekbarAudio.setOnSeekBarChangeListener(new MyOnSeekBarChangeListener());
    }

    class MyOnSeekBarChangeListener implements SeekBar.OnSeekBarChangeListener{

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (fromUser){
                //用户拖动了进度
                try {
                    service.seekTo(progress);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    }

    /**
     * Handle button click events<br />
     * <br />
     * Auto-created on 2018-03-31 16:13:52 by Android Layout Finder
     * (http://www.buzzingandroid.com/tools/android-layout-finder)
     */
    @Override
    public void onClick(View v) {
        if ( v == btnAudioPalymode ) {
            //用户点击了播放模式 顺序播放 - 单曲循环 - 全部循环
            setPlaymode();
        } else if ( v == btnAudioPre ) {
            // 用户点击了上一曲
            if (service != null){
                try {
                    service.pre();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        } else if ( v == btnAudioStartPause ) {
            // 用户点击了播放或暂停
            if (service != null){
                try {
                    if (service.isPlaying()){//播放状态 切换到暂停状态
                        //暂停
                        service.pause();
                        //按钮状态切换到播放
                        btnAudioStartPause.setBackgroundResource(R.drawable.btn_audio_start_selector);
                    }else {//暂停状态 切换到播放状态
                        //播放
                        service.start();
                        //按钮状态设为暂停
                        btnAudioStartPause.setBackgroundResource(R.drawable.btn_audio_pause_selector);
                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        } else if ( v == btnAudioNext ) {
            // 用户点击了下一曲
            if (service != null){
                try {
                    service.next();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        } else if ( v == btnLyrc ) {
            // Handle clicks for btnLyrc
        }
    }

    private void setPlaymode() {
        try {
            int playmode = service.getPlayMode();
            if (playmode == MusicPlayerService.REPEAT_NORMAL){
                playmode = MusicPlayerService.REPEAT_SINGLE;
            }else if (playmode == MusicPlayerService.REPEAT_SINGLE){
                playmode = MusicPlayerService.REPEAT_ALL;
            }else if (playmode == MusicPlayerService.REPEAT_ALL){
                playmode = MusicPlayerService.REPEAT_NORMAL;
            }else {
                playmode = MusicPlayerService.REPEAT_NORMAL;
            }

            //保存
            service.setPlayMode(playmode);

            //设置图片
            showPlaymode();

        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void showPlaymode() {

        try {
            int playmode = service.getPlayMode();

            if (playmode == MusicPlayerService.REPEAT_NORMAL){
                btnAudioPalymode.setBackgroundResource(R.drawable.btn_audio_palymode_normal_selector);
                Toast.makeText(AudioPlayerActivity.this,"顺序播放",Toast.LENGTH_SHORT).show();
            }else if (playmode == MusicPlayerService.REPEAT_SINGLE){
                btnAudioPalymode.setBackgroundResource(R.drawable.btn_audio_palymode_single_selector);
                Toast.makeText(AudioPlayerActivity.this,"单曲循环",Toast.LENGTH_SHORT).show();
            }else if (playmode == MusicPlayerService.REPEAT_ALL){
                btnAudioPalymode.setBackgroundResource(R.drawable.btn_audio_palymode_all_selector);
                Toast.makeText(AudioPlayerActivity.this,"全部循环",Toast.LENGTH_SHORT).show();
            }else {
                btnAudioPalymode.setBackgroundResource(R.drawable.btn_audio_palymode_normal_selector);
                Toast.makeText(AudioPlayerActivity.this,"顺序播放",Toast.LENGTH_SHORT).show();
            }

        } catch (RemoteException e) {
            e.printStackTrace();
        }

    }

    /**校验状态*/
    private void checkPlaymode() {

        try {
            int playmode = service.getPlayMode();

            if (playmode == MusicPlayerService.REPEAT_NORMAL){
                btnAudioPalymode.setBackgroundResource(R.drawable.btn_audio_palymode_normal_selector);
            }else if (playmode == MusicPlayerService.REPEAT_SINGLE){
                btnAudioPalymode.setBackgroundResource(R.drawable.btn_audio_palymode_single_selector);
            }else if (playmode == MusicPlayerService.REPEAT_ALL){
                btnAudioPalymode.setBackgroundResource(R.drawable.btn_audio_palymode_all_selector);
            }else {
                btnAudioPalymode.setBackgroundResource(R.drawable.btn_audio_palymode_normal_selector);
            }

            //校验播放和暂停的按钮
            if (service.isPlaying()){//播放状态显示播放按钮
                btnAudioStartPause.setBackgroundResource(R.drawable.btn_audio_pause_selector);
            }else{//暂停状态显示暂停的按钮
                btnAudioStartPause.setBackgroundResource(R.drawable.btn_audio_start_selector);

            }

        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private Handler handler = new Handler(){

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case SHOW_LYRIC://显示歌词

                    //1.得到当前进度
                    try {
                        int currentPosition = service.getCurrentPosition();

                        //2.把进度传入ShowLyricView,并且计算该高亮哪一句
                        showLyricView.setshowNextLyric(currentPosition);

                        //3.实时的发消息
                        handler.removeMessages(SHOW_LYRIC);
                        handler.sendEmptyMessage(SHOW_LYRIC);

                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    break;
                case PROGRESS:

                    try {
                        //1.得到当前进度
                        int currentPosition = service.getCurrentPosition();

                        //2.设置seekbar.setProgress(进度)
                        seekbarAudio.setProgress(currentPosition);

                        //3.时间进度更新
                        tvTime.setText(utils.stringForTime(currentPosition)+"/"+utils.stringForTime(service.getDuration()));

                        //4.每秒更新一次
                        handler.removeMessages(PROGRESS);
                        handler.sendEmptyMessageDelayed(PROGRESS,1000);

                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    break;
                case CHECK_STATUS:
                    checkButton();//leon增加到这里每秒校验2次
                    //4.每秒更新一次
                    handler.removeMessages(CHECK_STATUS);
                    handler.sendEmptyMessageDelayed(CHECK_STATUS,380);
                    break;

            }
        }
    };


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initData();
        findViews();
        getData();
        bindAndStartService();
    }

    private ServiceConnection con = new ServiceConnection() {

        /**当连接成功的时候回调这个方法*/
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            service = IMusicPlayerService.Stub.asInterface(iBinder);

            if (service != null){
                try {
                    if (!notification){//从列表中进来
                        service.openAudio(position);
                    }else {
                        //从状态栏进来
                        showViewData();
                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }

        /**当断开连接的时候回调这个方法*/
        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            try {
                if (service != null) {
                    service.stop();
                    service = null;
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    };

    private void initData() {
        utils = new Utils();
        //注册广播
//        receiver = new MyReceiver();
//        IntentFilter intentFilter = new IntentFilter();
//        intentFilter.addAction(MusicPlayerService.OPENAUDIO);
//        registerReceiver(receiver, intentFilter);

        //1.EventBus注册
        EventBus.getDefault().register(this);
    }

    class MyReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {

            showData(null);
        }
    }

    //3.订阅方法-订阅方法不能私有@Subscribe(threadMode = ThreadMode.MAIN,sticky = false,priority = 0)
    public void showData(MediaItem mediaItem) {
        //发消息开始歌词同步
        showLyric();
        showViewData();
        checkPlaymode();
        setupVisualizerFxAndUi();
    }

    public void onEventMainThread(MediaItem mediaItem){
        //发消息开始歌词同步
        showLyric();
        showViewData();
        checkPlaymode();
        setupVisualizerFxAndUi();
    }


    private Visualizer  mVisualizer;
    /**
     * 生成一个VisualizerView对象，使音频频谱的波段能够反映到 VisualizerView上
     */
    private void setupVisualizerFxAndUi()
    {
        try {
            int audioSessionid = service.getAudioSessionId();
            System.out.println("audioSessionid=="+audioSessionid);
            mVisualizer = new Visualizer(audioSessionid);
            // 参数内必须是2的位数
            mVisualizer.setCaptureSize(Visualizer.getCaptureSizeRange()[1]);
            // 设置允许波形表示，并且捕获它
            baseVisualizerView.setVisualizer(mVisualizer);
            mVisualizer.setEnabled(true);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void showLyric()  {
        //解析歌词
        LyricUtils lyricUtils = new LyricUtils();

        try {
            String path = service.getAudioPath();

            //传歌词文件
            path = path.substring(0,path.lastIndexOf("."));
            File file = new File(path+".lrc");
            if (!file.exists()){
                file = new File(path+".txt");
            }
            lyricUtils.readLyricFile(file);//解析歌词

            showLyricView.setLyrics(lyricUtils.getLyrics());

        } catch (RemoteException e) {
            e.printStackTrace();
        }

        if (lyricUtils.isExistsLyric()) {
            handler.sendEmptyMessage(SHOW_LYRIC);
        }
    }

    private void showViewData() {
        try {
            tvArtist.setText(service.getArtist());
            tvName.setText(service.getName());
            //设置进度条的最大值
            seekbarAudio.setMax(service.getDuration());

            //发消息
            handler.sendEmptyMessage(PROGRESS);
            handler.sendEmptyMessage(CHECK_STATUS);//Leon新增加

        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void bindAndStartService() {
        Intent intent = new Intent(this, MusicPlayerService.class);
        intent.setAction("com.example.mobileplayer_OPENAUDIO");
        bindService(intent,con, Context.BIND_AUTO_CREATE);
        startService(intent);//不至于实例化多个服务
    }

    /**得到数据*/
    private void getData() {
        notification = getIntent().getBooleanExtra("Notification",false);
        if (!notification){
            position = getIntent().getIntExtra("position",0);
        }
    }

    @Override
    protected void onDestroy() {
        handler.removeCallbacksAndMessages(null);
        //取消广播
//        if (receiver != null){
//            unregisterReceiver(receiver);
//            receiver = null;
//        }

        //2.EventBus取消注册
        EventBus.getDefault().unregister(this);

        //解绑服务
        if (con != null){
            unbindService(con);
            con = null;
        }
        super.onDestroy();

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mVisualizer != null){
            mVisualizer.release();
        }
    }

    /**校验按钮380毫秒一次 解决全部播放最后一首歌曲播放按钮 状态不对的bug*/
    private void checkButton(){
        try {
            //LogUtil.e("校验播放和暂停的按钮"+service.isPlaying());
            //校验播放和暂停的按钮
            if (service.isPlaying()){//播放状态显示播放按钮
                btnAudioStartPause.setBackgroundResource(R.drawable.btn_audio_pause_selector);
            }else{//暂停状态显示暂停的按钮
                btnAudioStartPause.setBackgroundResource(R.drawable.btn_audio_start_selector);

            }

        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

}
