package com.bobo.mobileplayer.utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import com.example.mobileplayer.R;
import java.util.Random;

/**
 * Created by Leon on 2018/4/6.
 * Functions: 自定义工具类返回一个int类型的 随机图片地址
 */

public class LeTool {

    public static Drawable randomPictureAddress(Context context){

        int array[] = { R.drawable.random1, R.drawable.random2, R.drawable.random3,R.drawable.random4,
                R.drawable.random5,R.drawable.random6, R.drawable.random7, R.drawable.random8,
                R.drawable.random9, R.drawable.random10, R.drawable.random11, R.drawable.random12,
                R.drawable.random13,R.drawable.random14,R.drawable.random15,R.drawable.random16,
                R.drawable.random17,R.drawable.random18,R.drawable.random19,R.drawable.random20,
                R.drawable.random21,R.drawable.random22,R.drawable.random23,R.drawable.random24,
                R.drawable.random25,R.drawable.random26,R.drawable.random27,R.drawable.random28,
                R.drawable.random29,R.drawable.random30,R.drawable.random31,R.drawable.random32,
                R.drawable.random33,R.drawable.random34,R.drawable.random35,R.drawable.random36,
                R.drawable.random37,R.drawable.random38,R.drawable.random39,R.drawable.random40,
                R.drawable.random41,R.drawable.random42,R.drawable.random43,R.drawable.random44,
                R.drawable.random45,R.drawable.random46,R.drawable.random47,R.drawable.random48,
                R.drawable.random49,R.drawable.random50};

        Random rnd = new Random();
        int index = rnd.nextInt(49);


        if (index < 0 || index > 49){
            LogUtil.e(String.valueOf(index));
            LogUtil.e("随机数计算有bug已经处理");
            index = 1;
        }

        Resources resources = context.getResources();
        Drawable cur = resources.getDrawable(array[index]);


        return cur;
    }
}
