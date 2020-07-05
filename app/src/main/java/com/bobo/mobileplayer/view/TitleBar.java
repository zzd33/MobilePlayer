package com.bobo.mobileplayer.view;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;
import com.example.mobileplayer.R;
import com.bobo.mobileplayer.activity.SearchActivity;

/**
 * Created by Leon on 2018/1/6.
 * Functions:自定义标题栏
 */

public class TitleBar extends LinearLayout implements View.OnClickListener {

    private View tv_search;
    private View rl_game;
    private View iv_record;
    private Context context;

    /**在代码中实例化该类的时候使用这个方法*/
    public TitleBar(Context context) {
        this(context,null);
    }
    /**在布局文件使用该类的时候，Android系统通过这个构造方法实例化该类*/
    public TitleBar(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }
    /**当需要设置样式的时候，可以使用该方法*/
    public TitleBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
    }

    /**当布局文件加载完成的时候回掉这个方法*/
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        //得到子控件实例
        tv_search = getChildAt(1);
        rl_game = getChildAt(2);
        iv_record = getChildAt(3);

        //设置点击事件
        tv_search.setOnClickListener(this);
        rl_game.setOnClickListener(this);
        iv_record.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_search://搜索
                //Toast.makeText(context,"搜索",Toast.LENGTH_LONG).show();
                Intent intent  = new Intent(context,SearchActivity.class);
                context.startActivity(intent);

                break;
            case R.id.rl_game://游戏
                Toast.makeText(context,"游戏",Toast.LENGTH_LONG).show();
                break;
            case R.id.iv_record://播放历史
                Toast.makeText(context,"播放历史",Toast.LENGTH_LONG).show();
                break;
        }
    }
}
