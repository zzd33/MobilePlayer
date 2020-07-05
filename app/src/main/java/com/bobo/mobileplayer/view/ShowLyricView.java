package com.bobo.mobileplayer.view;


import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.TextView;

import com.bobo.mobileplayer.domain.Lyric;
import com.bobo.mobileplayer.utils.DensityUtil;
import com.bobo.mobileplayer.utils.LogUtil;

import java.util.ArrayList;

/**
 * Created by Leon on 2018/4/5.
 * Functions: 自定义歌词显示控件  ShowLyricView 我加:android:textSize="20dp"
 */

@SuppressLint("AppCompatCustomView")
public class ShowLyricView extends TextView {

    /**歌词列表*/
    private ArrayList<Lyric> lyrics;
    private Paint paint;
    private Paint whitepaint;

    private int width;
    private int height;

    /**歌词列表中的索引，是第几句歌词*/
    private int index;
    /**每行歌词的高度*/
    private float textHeight;
    /**当前播放进度*/
    private float currentPosition;
    /**高亮显示的时间或者休眠的时间*/
    private float sleepTime;
    /**自定义时间戳，什么时刻到高亮哪句歌词*/
    private float timePoint;

    /**设置歌词列表*/
    public void setLyrics(ArrayList<Lyric> lyrics) {
        this.lyrics = lyrics;
    }

    public ShowLyricView(Context context) {
        this(context,null);
    }

    public ShowLyricView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public ShowLyricView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width = w;
        height = h;
    }

    private void initView(Context context) {

        textHeight = DensityUtil.dip2px(context,18);//对应的像素
        LogUtil.e("textHeight=="+String.valueOf(textHeight));

        //创建画笔
        paint = new Paint();
        paint.setColor(Color.GREEN);
        paint.setTextSize(DensityUtil.dip2px(context,16));
        paint.setAntiAlias(true);
        //设置居中对齐
        paint.setTextAlign(Paint.Align.CENTER);

        whitepaint = new Paint();
        whitepaint.setColor(Color.WHITE);
        whitepaint.setTextSize(DensityUtil.dip2px(context,16));
        whitepaint.setAntiAlias(true);
        //设置居中对齐
        whitepaint.setTextAlign(Paint.Align.CENTER);

//        lyrics = new ArrayList<>();  测试的时候的for循环做的假歌词
//
//        Lyric lyric = new Lyric();
//        for (int i = 0;i < 10000;i++){
//            lyric.setTimePoint(1000 * i);
//            lyric.setSleepTime(1500 + i);
//            lyric.setContent(i+"这是测试的歌词"+i);
//            //把歌词添加到集合中
//            lyrics.add(lyric);
//            lyric = new Lyric();
//        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (lyrics != null && lyrics.size() > 0){

            //往上平稳推移 - 注意要把所有参与进来的数据都改成float类型！
            float plush = 0;
            if (sleepTime == 0){
                plush = 0;
            }else {
                /**平移 一句歌词所花的时间 ：休眠的时间 = 移动的距离 ：总距离（行高）
                 * 移动距离 = （一句歌词花的时间 ： 休眠时间）* 总距离（行高）
                 * 屏幕上的坐标 = 行高 + 移动距离
                 */
                //float dalta = ((currentPosition - timePoint) / sleepTime) * textHeight;
                //屏幕上的坐标 = 行高 + 移动的距离
                plush = textHeight + ((currentPosition - timePoint) / sleepTime) * textHeight;
            }

            canvas.translate(0,-plush);


            //绘制歌词:绘制当前句
            String currentText = lyrics.get(index).getContent();
            canvas.drawText(currentText,width / 2,height / 2,paint);

            //绘制前面部分
            float tempY = height / 2;//Y轴中间坐标
            for (int i = index - 1;i >= 0;i--){
                //每一句歌词
                String preContent = lyrics.get(i).getContent();
                tempY = tempY - textHeight;
                if (tempY < 0){
                    break;
                }

                canvas.drawText(preContent,width / 2,tempY,whitepaint);
            }

            // 绘制后面部分
            tempY = height / 2;//Y轴中间坐标
            for (int i = index + 1;i < lyrics.size();i++){
                //每一句歌词
                String nextContent = lyrics.get(i).getContent();
                tempY = tempY + textHeight;
                if (tempY > height){
                    break;
                }

                canvas.drawText(nextContent,width / 2,tempY,whitepaint);
            }


        }else {
            //没有歌词
            canvas.drawText("没有找到对应的歌词",width / 2,height / 2,paint);
        }
    }

    /**根据当前播放的位置，找出该高亮显示哪句歌词*/
    public void setshowNextLyric(int currentPosition) {

        this.currentPosition = currentPosition;
        if (lyrics  == null || lyrics.size()  == 0){
            return;
        }

        for (int i = 1;i < lyrics.size();i++){
            if (currentPosition < lyrics.get(i).getTimePoint()){

                int tempIndex = i - 1;

                if (currentPosition >= lyrics.get(tempIndex).getTimePoint()) {
                    //当前正在播放的歌词
                    index = tempIndex;
                   // LogUtil.e("第几句歌词："+String.valueOf(index));
                    sleepTime = lyrics.get(index).getSleepTime();
                    timePoint = lyrics.get(index).getTimePoint();
                }
            }
        }

        //重新绘制
        invalidate();//在主线程中

        //子线程
        //postInvalidate();
    }
}
