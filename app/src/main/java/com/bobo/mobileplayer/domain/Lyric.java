package com.bobo.mobileplayer.domain;

/**
 * Created by Leon on 2018/4/5.
 * Functions: 歌词类
 */

public class Lyric {

    /**歌词内容*/
    private String content;

    /**时间戳*/
    private long timePoint;

    /**休眠时间或者高亮显示的时间*/
    private long sleepTime;

    public void setContent(String content) {
        this.content = content;
    }

    public void setTimePoint(long timePoint) {
        this.timePoint = timePoint;
    }

    public void setSleepTime(long sleepTime) {
        this.sleepTime = sleepTime;
    }

    public String getContent() {

        return content;
    }

    public long getTimePoint() {
        return timePoint;
    }

    public long getSleepTime() {
        return sleepTime;
    }

    @Override
    public String toString() {
        return "Lyric{" +
                "content='" + content + '\'' +
                ", timePoint=" + timePoint +
                ", sleepTime=" + sleepTime +
                '}';
    }
}
