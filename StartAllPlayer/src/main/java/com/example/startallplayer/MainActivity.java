package com.example.startallplayer;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void startAllPlayer(View view){
        Intent intent = new Intent();
        //http://ugcyd.qq.com/r05478ksxc0.p712.1.mp4
        //http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4
        //https://vd3.bdstatic.com/mda-hmqu4kyjejjaznf2/mda-hmqu4kyjejjaznf2.mp4
        intent.setDataAndType(Uri.parse("http://vd3.bdstatic.com/mda-hmqu4kyjejjaznf2/mda-hmqu4kyjejjaznf2.mp4"),"video/*");
        startActivity(intent);
    }
}
