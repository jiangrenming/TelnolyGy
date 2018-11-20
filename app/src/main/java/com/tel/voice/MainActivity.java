package com.tel.voice;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.voicelibrary.VoiceSpeaker;
import com.voicelibrary.VoiceType;


/**
 * Created by jiangrenming on 2018/9/12.
 */

public class MainActivity extends Activity {


    private VoiceSpeaker.Builder speaker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.btn_one).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                speaker = new VoiceSpeaker.Builder(MainActivity.this);
                VoiceSpeaker mVoice = speaker.setTransType(VoiceType.VoiceTrans.ALIPAY).setTransPrefix(VoiceType.TransPrefix.RECEIVE).numMoney("123456.01").mVoice;
                try {
                    mVoice.speak();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        findViewById(R.id.btn_two).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                speaker = new VoiceSpeaker.Builder(MainActivity.this);
                VoiceSpeaker cash = speaker.setTransType("cash").numMoney("123.56").mVoice;
                try {
                    cash.speak();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (speaker != null){
            speaker.mVoice.onReleaseVoice();
        }
    }
}
