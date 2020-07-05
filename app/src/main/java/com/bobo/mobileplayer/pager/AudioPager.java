package com.bobo.mobileplayer.pager;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Message;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bobo.mobileplayer.adapter.VideoPagerAdapter;
import com.example.mobileplayer.R;
import com.bobo.mobileplayer.activity.AudioPlayerActivity;
import com.bobo.mobileplayer.base.BasePager;
import com.bobo.mobileplayer.domain.MediaItem;
import com.bobo.mobileplayer.utils.LogUtil;

import java.util.ArrayList;

/**
 * Created by Leon on 2017/12/31.
 * Functions:本地音频页面
 */
public class AudioPager extends BasePager {
    private ListView listview;
    private TextView tv_nomedia;
    private ProgressBar pb_loading;

    private VideoPagerAdapter videoPagerAdapter;
    /**装数据集合*/
    private ArrayList<MediaItem> mediaItems;

    public AudioPager(Context context) {
        super(context);
    }

    private android.os.Handler handler = new android.os.Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (mediaItems != null && mediaItems.size() > 0){
                //有数据 设置适配器
                videoPagerAdapter = new VideoPagerAdapter(context,mediaItems,false);
                listview.setAdapter(videoPagerAdapter);
                //文本就隐藏
                tv_nomedia.setVisibility(View.GONE);
            }else {
                //没有数据 文本就要显示了
                tv_nomedia.setVisibility(View.VISIBLE);
                tv_nomedia.setText("您本地没有音频");

            }
            //progressBar都隐藏
            pb_loading.setVisibility(View.GONE);
        }
    };

    @Override
    public View initView() {
        View view = View.inflate(context, R.layout.video_pager,null);
        listview = (ListView) view.findViewById(R.id.listview);
        tv_nomedia = (TextView)view.findViewById(R.id.tv_nomedia);
        pb_loading = (ProgressBar)view.findViewById(R.id.pb_loading);
        //设置listview的item的点击事件
        listview.setOnItemClickListener(new MyOnItemClickListener());
        return view;
    }

    class MyOnItemClickListener implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            Intent intent = new Intent(context,AudioPlayerActivity.class);
            intent.putExtra("position",position);//在AudioPlayerActivity中有接收
            context.startActivity(intent);

        }
    }

    @Override
    public void initData() {
        super.initData();
        LogUtil.e("本地视频页面数据初始化");
        //加载本地视频数据
        getDataFromLocal();

    }

    /**从本地的SDcard得到数据
     * 方法一：便利SDcard，后缀名
     * 方法二：从内容提供者里面获取
     * 如果是6.0的系统就需要添加权限 动态读取SDcard的权限
     **/
    private void getDataFromLocal() {

        //开辟一条子线程在子线程中获取视频
        new Thread(){
            @Override
            public void run() {
                super.run();
                isGrantExternalRW((Activity)context);
                //SystemClock.sleep(1000);
                mediaItems = new ArrayList<>();
                ContentResolver resolver = context.getContentResolver();
                Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                String[] objs = {
                        MediaStore.Audio.Media.DISPLAY_NAME,//视频文件在本地的名称
                        MediaStore.Audio.Media.DURATION,//视频总时长
                        MediaStore.Audio.Media.SIZE,//视频文件的大小
                        MediaStore.Audio.Media.DATA,//视频的绝对地址
                        MediaStore.Audio.Media.ARTIST,//歌曲的演唱者（视频不一定有）
                };
                Cursor cursor = resolver.query(uri,objs,null,null,null);
                if (cursor != null){
                    while (cursor.moveToNext()){
                        MediaItem mediaItem = new MediaItem();
                        mediaItems.add(mediaItem);//写在上面
                        String name = cursor.getString(0);//视频的名称
                        mediaItem.setName(name);
                        long duration = cursor.getLong(1);//视频的时长
                        mediaItem.setDuration(duration);
                        long size = cursor.getLong(2);//视频的文件大小
                        mediaItem.setSize(size);
                        String data = cursor.getString(3);//视频的播放地址
                        mediaItem.setData(data);
                        String artist = cursor.getString(4);//艺术家
                        mediaItem.setArtist(artist);
                    }
                    cursor.close();
                }
                //Handle发消息
                handler.sendEmptyMessage(10);
            }
        }.start();
    }

    /**6.0 以上版本需要添加的权限 */
    public static boolean isGrantExternalRW(Activity activity){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && activity.checkSelfPermission(
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            activity.requestPermissions(new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
            },1);
            return false;
        }
        return true;
    }
}
