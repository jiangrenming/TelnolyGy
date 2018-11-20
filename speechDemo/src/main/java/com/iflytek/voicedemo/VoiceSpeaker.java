package com.iflytek.voicedemo;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by jiangrenming on 2018/9/18.
 */

public class VoiceSpeaker {

    private static VoiceSpeaker sInstance;

    private ExecutorService service;
    private static Context mContext;

    //不同应用头部
    private String mAppType;
    //正交易类型
    private String mTransType;
    //交易尾语
    private String mTransPrefix;


    private String money;
    private boolean isInterrupt = false;



    private CompleteVoiceCallBack mCompleteVoice;

    public interface CompleteVoiceCallBack {
        void OnCompleteVoice(int end);

        void onErrorVoice(String error);
    }


    private VoiceSpeaker() {
        service = Executors.newCachedThreadPool();

    }


    public static synchronized VoiceSpeaker getInstance() {
        if (sInstance == null) {
            sInstance = new VoiceSpeaker();
        }
        return sInstance;
    }


    public static class Builder {
        public VoiceSpeaker mVoice;

        public Builder(Context context) {
            mContext = context;
            mVoice = VoiceSpeaker.getInstance();
        }

        /**
         * 交易金额
         */
        public Builder numMoney(String numMoney) {
            mVoice.money = numMoney;
            return this;
        }
        /**
         *不同应用播放头部
         */
        public Builder setAppType(String appType) {
            mVoice.mAppType = appType;
            return this;
        }

        /**
         * 正交易类型
         *
         * @param transType
         */
        public Builder setTransType(String transType) {
            mVoice.mTransType = transType;
            return this;
        }
        /**
         *交易尾语
         */
        public Builder setTransPrefix(String prefix) {
            mVoice.mTransPrefix = prefix;
            return this;
        }

    }

    public void speak() throws Exception{
        this.speak(null);
    }

    public void speak(final CompleteVoiceCallBack mCompleteVoice) throws Exception{

        //获取金额语音字符串
        VoiceTemplate voiceTemplate = new VoiceTemplate();
        if (!TextUtils.isEmpty(mAppType)){
            voiceTemplate.voiceHeader(mAppType);
        }
        if (!TextUtils.isEmpty(mTransType)){
            voiceTemplate.transType(mTransType);
        }
        if (!TextUtils.isEmpty(mTransPrefix)){
            voiceTemplate.prefix(mTransPrefix);
        }
        final  List<String> gen = voiceTemplate.numString(money).suffix("yuan").gen();
        if (service != null) {
            service.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        start(gen, mCompleteVoice);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    private   MediaPlayer player = null;
    private void start(final List<String> list, final CompleteVoiceCallBack mCompleteVoice) throws Exception {
        // synchronized (this) {   //当一下很多语音播报时，想要都播放的时候，则加锁按照顺序播放音频文件
        final CountDownLatch latch = new CountDownLatch(1);
        if (isInterrupt){
            player.reset();
            player.release();
            latch.countDown();
        }
        player = new MediaPlayer();
        if (list != null && list.size() > 0) {
            final int[] counter = {0};
            final String path = String.format("sound/tts_%s.wav", list.get(counter[0]));
            AssetFileDescriptor fd = null;
            try {
               fd = PcmToWavUtil.getAssetFileDescription(path, mContext);
                player.setDataSource(fd.getFileDescriptor(), fd.getStartOffset(),
                        fd.getLength());
                player.prepareAsync();
                player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        mp.start();
                        isInterrupt=true;
                        mAppType = null;
                        mTransPrefix = null;
                        mTransType = null;
                    }
                });
                player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        mp.reset();
                        counter[0]++;
                        if (counter[0] < list.size()) {
                            try {
                                AssetFileDescriptor fileDescriptor = PcmToWavUtil.getAssetFileDescription(String.format("sound/tts_%s.wav", list.get(counter[0])), mContext);
                                mp.setDataSource(fileDescriptor.getFileDescriptor(), fileDescriptor.getStartOffset(), fileDescriptor.getLength());
                                mp.prepare();
                            } catch (IOException e) {
                                e.printStackTrace();
                                latch.countDown();
                                if (mCompleteVoice != null) {
                                    mCompleteVoice.onErrorVoice(e.getMessage());
                                }
                            }
                        } else {
                            mp.release();
                            latch.countDown();
                            isInterrupt =false;
                            if (mCompleteVoice != null) {
                                mCompleteVoice.OnCompleteVoice(1); //成功播放完成
                            }
                        }
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
                latch.countDown();
                isInterrupt =false;
                if (mCompleteVoice != null) {
                    mCompleteVoice.onErrorVoice(e.getMessage());
                }
            } finally {
                if (fd != null) {
                    try {
                        fd.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                        isInterrupt =false;
                        if (mCompleteVoice != null) {
                            mCompleteVoice.onErrorVoice(e.getMessage());
                        }
                    }
                }
            }
        }

            /*try {
                latch.await();
                this.notifyAll();               //唤醒锁
            } catch (InterruptedException e) {
                e.printStackTrace();
                if (mCompleteVoice != null) {
                    mCompleteVoice.onErrorVoice(e.getMessage());
                }
            }*/
    }
    //  }


    /**
     * 退出当前页面停止语音播报
     */
    public  void onReleaseVoice(){
        if (player != null){
            player.release();
            player = null;
        }
    }
}
