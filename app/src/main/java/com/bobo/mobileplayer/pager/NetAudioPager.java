package com.bobo.mobileplayer.pager;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bobo.mobileplayer.adapter.NetAudioPagerAdapter;
import com.bobo.mobileplayer.base.BasePager;
import com.bobo.mobileplayer.domain.NetAudioPagerData;
import com.bobo.mobileplayer.utils.CacheUtils;
import com.bobo.mobileplayer.utils.Constants;
import com.bobo.mobileplayer.utils.LogUtil;
import com.example.mobileplayer.R;
import com.google.gson.Gson;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.List;

/**
 * Created by Leon on 2017/12/31.
 * Functions:网络音乐页面
 */

public class NetAudioPager extends BasePager {

    @ViewInject(R.id.listview)
    private ListView mListView;

    @ViewInject(R.id.tv_nonet)
    private TextView tv_nonet;

    @ViewInject(R.id.pb_loading)
    private ProgressBar pb_loading;

    /**页面的数据*/
    private List<NetAudioPagerData.ListBean> datas;

    private NetAudioPagerAdapter adapter;

    public NetAudioPager(Context context) {
        super(context);
    }

    /**
     *初始化当前页面控件，由父类调用
     */
    @Override
    public View initView() {
        View view = View.inflate(context, R.layout.netaudio_pager,null);
        x.view().inject(this,view);

        return view;
    }

    @Override
    public void initData() {
        super.initData();
        LogUtil.e("网络音乐页面被初始化了initData()");
        String savaJson = CacheUtils.getString(context,Constants.ALL_RES_URL);
        if (!TextUtils.isEmpty(savaJson)){
            //解析数据
            processedData(savaJson);
        }
        //联网
        getDataFromNet();

    }

    /**联网*/
    private void getDataFromNet() {
        RequestParams params = new RequestParams(Constants.ALL_RES_URL) ;
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                LogUtil.e("请求数据成功 == "+result);
                //保存数据
                CacheUtils.putString(context,Constants.ALL_RES_URL,result);
                processedData(result);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                LogUtil.e("请求失败 == "+ex.getMessage());
            }

            @Override
            public void onCancelled(CancelledException cex) {
                LogUtil.e("onCancelled == "+cex.getMessage());
            }

            @Override
            public void onFinished() {
                LogUtil.e("onFinished 完成 ");
            }
        });
    }

    /**解析json数据和显示适配器
     * 解析数据：1.GsonFormat生产bean对象
     * 2.用Gson解析数据
     */
    private void processedData(String json) {
        //解析数据
        NetAudioPagerData data = parsedJson(json);
        datas = data.getList();

        if (datas != null && datas.size() > 0){
            //有数据
            tv_nonet.setVisibility(View.GONE);
            //设置适配器
            adapter = new NetAudioPagerAdapter(context,datas);
            mListView.setAdapter(adapter);
        }else {
            tv_nonet.setText("没有对应的数据...");
            //没有数据
            tv_nonet.setVisibility(View.VISIBLE);
        }

        pb_loading.setVisibility(View.GONE);
    }

    /**Gson解析数据*/
    private NetAudioPagerData parsedJson(String json) {
        return new Gson().fromJson(json,NetAudioPagerData.class);
    }
}
