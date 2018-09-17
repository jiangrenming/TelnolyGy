# TelnolyGy
播放音频（有关金额）
package com.iflytek.voicedemo;

import android.content.Context;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jiangrenming on 2018/9/13.
   分割金额添加读音
 */

public class VoiceTemplate {

    private static final String DOT = ".";

    private String numString;

    private String prefix;

    private String suffix;

    private String voiceType;

    public VoiceTemplate() {

    }

    public static List<String> getDefaultTemplate(String money){
        return new VoiceTemplate()
                .voiceType("支付宝")
                .prefix("到账")
                .numString(money)
                .suffix("元")
                .gen();
    }

    public  static class  Builder{
        VoiceTemplate template;
        public Builder(){
            template = new VoiceTemplate();
        }

        public Builder setPrefix(String prefix) {
            template.prefix = prefix;
            return  this;
        }

        public Builder setVoiceType(String voiceType) {
            template.voiceType = voiceType;
            return  this;
        }
        public Builder setSuffix(String Suffix) {
            template.suffix = Suffix;
            return  this;
        }
    }


    public VoiceTemplate prefix(String prefix) {
        this.prefix = prefix;
        return this;
    }
    public VoiceTemplate voiceType(String voiceType) {
        this.voiceType = voiceType;
        return this;
    }

    public String getSuffix() {
        return suffix;
    }

    public VoiceTemplate suffix(String suffix) {
        this.suffix = suffix;
        return this;
    }


    public String getNumString() {
        return numString;
    }

    public VoiceTemplate numString(String numString) {
        this.numString = numString;
        return this;
    }


    public List<String> gen() {
        return genVoiceList();
    }

    private List<String> createReadableNumList(String numString) {
        List<String> result = new ArrayList<>();
        if (!TextUtils.isEmpty(numString)) {
            int len = numString.length();
            for (int i = 0; i < len; i++) {
                if ('.' == numString.charAt(i)) {
                    result.add("dot");
                } else {
                    result.add(String.valueOf(numString.charAt(i)));
                }
            }
        }
        return result;
    }


    private List<String> genVoiceList() {
        List<String> result = new ArrayList<>();
        if (!TextUtils.isEmpty(voiceType)) {
            result.add(voiceType);
        }
        if (!TextUtils.isEmpty(prefix)) {
            result.add(prefix);
        }
        if (!TextUtils.isEmpty(numString)) {
           // result.addAll(genReadableMoney(numString));
            result.addAll(genFenReadableMoney(numString));
        }

        if (!TextUtils.isEmpty(suffix)) {
            result.add(suffix);
        }
        return result;
    }


    private List<String> genReadableMoney(String numString) {
        List<String> result = new ArrayList<>();
        if (!TextUtils.isEmpty(numString)) {
            if (numString.contains(DOT)) {
                String integerPart = numString.split("\\.")[0];
                String decimalPart = numString.split("\\.")[1];
                List<String> intList = readIntPart(integerPart);
                List<String> decimalList = readDecimalPart(decimalPart);
                result.addAll(intList);
                if (!decimalList.isEmpty()){
                    result.add("点");
                    result.addAll(decimalList);
                }
            }else {
                result.addAll(readIntPart(numString));
            }
        }
        return result;
    }

    private List<String> genFenReadableMoney(String numString) {
        List<String> result = new ArrayList<>();
        if (!TextUtils.isEmpty(numString)) {
            if (numString.contains(DOT)) {
                String integerPart = numString.split("\\.")[0];
                String decimalPart = numString.split("\\.")[1];
                List<String> intList = readIntPart(integerPart);
                List<String> decimalList = readFenDecimalPart(decimalPart);
                result.addAll(intList);
                result.add("元");
                if (!decimalList.isEmpty()){
                    result.add("点");
                    result.addAll(decimalList);
                }
            }else {
                result.addAll(readIntPart(numString));
                result.add("元");
            }
        }
        return result;
    }

    private List<String> readDecimalPart(String decimalPart) {  //05
        List<String> result = new ArrayList<>();
        if (!"00".equals(decimalPart)){
            char[] chars = decimalPart.toCharArray();
            for (int i = 0; i <chars.length ; i++) {
                if (chars[i] == '0'){
                    result.add("零");
                }else {
                    result.add(String.valueOf(chars[i]));
                }
            }
        }
        return result;
    }

    private List<String> readFenDecimalPart(String decimalPart) {  //05
        List<String> result = new ArrayList<>();
        if (!"00".equals(decimalPart)){
            if (decimalPart.length() >2){   //金额小数点保留长度>2 ，去除2位之后的数据
                decimalPart = decimalPart.substring(0, 2);
            }else if (decimalPart.length() < 2){
                decimalPart = decimalPart + "0";  //金额小数点保留长度 <2 ,后面+0，补充为2位
            }
            char[] chars = decimalPart.toCharArray();
            result.add(0,chars[0]+"角");
            result.add(1,chars[1]+"分");
        }
        return result;
    }


    private List<String> readIntPart(String integerPart) {  //10010
        List<String> result = new ArrayList<>();
        String intString = readInt(Integer.parseInt(integerPart));
        int len = intString.length();
        for (int i =0; i < len;i++){
            char current = intString.charAt(i);
            if (current == '拾'){
            //    result.add("ten");
                result.add("拾");
            }else if (current == '佰'){
           //     result.add("hundred");
                result.add("佰");
            }else if (current == '仟'){
            //    result.add("thousand");
                result.add("仟");
            }else if (current == '万'){
             //   result.add("ten_thousand");
                result.add("万");
            }else if (current == '亿'){
            //    result.add("ten_million");
                result.add("亿");
            }else {
                result.add(String.valueOf(current));
            }
        }
        return result;
    }



    private static final char [] NUM ={'0','1','2','3','4','5','6','7','8','9'};
    private static final char [] CHINESE_UNIT = {'元','拾','佰','仟','万','拾','佰','仟','亿','拾','佰','仟'};

    /**
     * 返回关于钱的中文式大写数字,支仅持到亿
     * */
    public static String readInt(int moneyNum){ //10010
        String res="";
        int i=0;
        if(moneyNum==0) {
            return "0";
        }

        if (moneyNum == 10){
            return "拾";
        }

        if (moneyNum > 10 && moneyNum < 20) {
            return "拾" + moneyNum % 10;
        }

        while(moneyNum>0){   //10010
            res=CHINESE_UNIT[i++]+res;  //元,'拾'，'佰'
            res=NUM[moneyNum%10]+res;   //0元，1'拾'，0
            moneyNum/=10;               //1001 ，100
        }
        return res.replaceAll("0[拾佰仟]", "0")
                .replaceAll("0+亿", "亿").replaceAll("0+万", "万")
                .replaceAll("0+元", "元").replaceAll("0+", "0")
                .replace("元","");
    }

}

/*
 进行android 系自带音频播放工具的封装
*/
package com.iflytek.voicedemo;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.AudioAttributes;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileDescriptor;
import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static android.content.Context.AUDIO_SERVICE;

/**
 *
 * @author jiangrenming
 * @date 2018/9/13
 */

public class SpeakerVoice {


    private static SpeakerVoice sInstance;
    private ExecutorService service;
    private AudioParams mParams;
    private  String TAG = this.getClass().getSimpleName();
    private int mPrimePlaySize = 0;								// 较优播放块大小
    private int mPlayOffset = 0;
    private static  Context mContext;
    private  int voiceType = 0;
    private  int mResId =0;
    private  String outPath;
    private  String inPath;
    private byte[] mData;
    private  int mLoop = 0;



    private SpeakerVoice() {
        service = Executors.newCachedThreadPool();

    }

    private static synchronized SpeakerVoice getInstance() {
        if (sInstance == null) {
            sInstance = new SpeakerVoice();
        }
        return sInstance;
    }


    public  static class  Builder{
        SpeakerVoice mVoice ;

        public Builder(Context context){
            mContext=  context;
            mVoice = SpeakerVoice.getInstance();
        }
        /**
         * 设置音频的格式配置
         * @param audioParams
         */
        public Builder setAudioParams(AudioParams audioParams){
            mVoice.mParams = audioParams;
            return  this;
        }

        /**
         * Atrack播放最优缓存区大小
         * @param mPrimePlaySize
         * @return
         */
        public Builder setmPrimePlaySize(int mPrimePlaySize) {
            mVoice.mPrimePlaySize = mPrimePlaySize;
            return  this;
        }

        /**
         * 选择音频播放的类型
         * @param type
         * @return
         */
        public  Builder setVoiceType(int type){
            mVoice.voiceType = type;
            return  this;
        }

        /**
         * SoundPool播放资源文件音频id
         * @param resId
         * @return
         */
        public  Builder setResId(int resId){
            mVoice.mResId = resId;
            return  this;
        }

        public  Builder  setInPath(String inPath){
            mVoice.inPath = inPath;
            return  this;
        }
        public  Builder  setOutPath(String outPath){
            mVoice.outPath = outPath;
            return  this;
        }
        /**
         * 是否循环播放
         * @param loop
         * @return
         */
        public Builder setLoop(int loop){
            mVoice.mLoop = loop;
            return  this;
        }

    }

    /**
     * 开始播放
     */
    public  void startSpeak(){
        mPlayOffset = 0;
        if (service != null){
            service.execute(new Runnable() {
                @Override
                public void run() {
                    synchronized (this){
                        try {
                            if (voiceType == 0){            //AudioTrack类实现读取语言
                                if (!isPCM(inPath)){
                                    Log.i("读取的音频格式","不是PCM音频格式");
                                    return;
                                }
                                mData = PcmToWavUtil.readFromFile(inPath);
                                if (mData == null || mParams == null){
                                    return;
                                }
                                AudioTrack audioTrack = createAudioTrack();
                                playerAudio(audioTrack);
                            }else if (voiceType == 1){    //soundPool实现语言播放
                                SoundPool soundPool = createSoundPool();
                                if (mResId != 0){        //assets或raw中的音频文件
                                    int load = soundPool.load(mContext , mResId, 1);
                                }else {                 //文件中的音频文件
                                    if (isPCM(inPath)){   //如果是pcm格式就将其转换成wav格式
                                       outPath = inPath.replace(".pcm", ".wav");
                                        int pcmConvertToWAV = pcmConvertToWAV();
                                        Log.i("是否转换成功",pcmConvertToWAV+"");
                                    }else {
                                        outPath = inPath;
                                    }
                                    int load = soundPool.load(outPath, 1);
                                }
                                soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
                                    @Override
                                    public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                                        Log.i("加载完成","sampleId="+sampleId+"status="+status+"soundPool="+soundPool.toString());
                                        soundPool.play(sampleId,1,1,1,mLoop,1.0f);
                                    }
                                });
                            }else {   //MedioPlayer实现语音播放
                                MediaPlayer mediaPlayer = new MediaPlayer();
                                AssetFileDescriptor af = null;
                                if (mResId != 0){
                                    try{
                                        af = PcmToWavUtil.getAssetFileDescription(mContext,inPath);
                                        mediaPlayer.setDataSource(af.getFileDescriptor(), af.getStartOffset(), af.getLength());
                                    }catch(Exception e){
                                        e.printStackTrace();
                                    }finally {
                                        if (af != null){
                                            try {
                                                af.close();
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }
                                }else {
                                    if (isPCM(inPath)){   //如果是pcm格式就将其转换成wav格式
                                        outPath = inPath.replace(".pcm", ".wav");
                                        int pcmConvertToWAV = pcmConvertToWAV();
                                        Log.i("是否转换成功",pcmConvertToWAV+"");
                                    }else {
                                        outPath = inPath;
                                    }
                                    mediaPlayer.setDataSource(outPath);
                                }
                                mediaPlayer.prepareAsync();
                                mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                                    @Override
                                    public void onPrepared(MediaPlayer mp) {
                                        mp.start();
                                    }
                                });
                                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                    @Override
                                    public void onCompletion(MediaPlayer mp) {
                                        mp.reset();
                                        if (!mp.isPlaying()){
                                            mp.release();
                                        }else {
                                            try {
                                                AssetFileDescriptor fileDescriptor = PcmToWavUtil.getAssetFileDescription(mContext,inPath);
                                                mp.setDataSource(fileDescriptor.getFileDescriptor(), fileDescriptor.getStartOffset(), fileDescriptor.getLength());
                                                mp.prepare();
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }
                                });
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            return ;
                        }
                    }
                }
            });
        }
    }


    private void playerAudio( AudioTrack audioTrack){
        try{
            audioTrack.play();
            while(true) {
                try {
                    int size = audioTrack.write(mData, mPlayOffset, mData.length);
                    mPlayOffset += mPrimePlaySize;
                } catch (Exception e) {
                    e.printStackTrace();
                    break;
                }
                if (mPlayOffset >= mData.length) {
                    break;
                }
            }
            audioTrack.stop();
            Log.d(TAG, "PlayAudioThread complete...");
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 构建AudioTrack类
     * @return
     */
    private AudioTrack createAudioTrack() throws Exception{
        // 获得构建对象的最小缓冲区大小
        int minBufSize = AudioTrack.getMinBufferSize(mParams.getRequency(),
                mParams.getChannelConfig(),
                mParams.getAudioFormat());
          mPrimePlaySize = minBufSize*5;
        Log.d(TAG, "mPrimePlaySize = " + mPrimePlaySize);
//		         STREAM_ALARM：警告声
//		         STREAM_MUSCI：音乐声，例如music等
//		         STREAM_RING：铃声
//		         STREAM_SYSTEM：系统声音
//		         STREAM_VOCIE_CALL：电话声音
        AudioTrack  mAudioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,
                mParams.getRequency(),
                mParams.getChannelConfig(),
                mParams.getAudioFormat(),
                mPrimePlaySize,
                AudioTrack.MODE_STREAM);
//				AudioTrack中有MODE_STATIC和MODE_STREAM两种分类。
//      		STREAM的意思是由用户在应用程序通过write方式把数据一次一次得写到audiotrack中。
//				这个和我们在socket中发送数据一样，应用层从某个地方获取数据，例如通过编解码得到PCM数据，然后write到audiotrack。
//				这种方式的坏处就是总是在JAVA层和Native层交互，效率损失较大。
//				而STATIC的意思是一开始创建的时候，就把音频数据放到一个固定的buffer，然后直接传给audiotrack，
//				后续就不用一次次得write了。AudioTrack会自己播放这个buffer中的数据。
//				这种方法对于铃声等内存占用较小，延时要求较高的声音来说很适用。

        return  mAudioTrack;

    }


    private SoundPool createSoundPool(){
        SoundPool mSoundPool;
        if(Build.VERSION.SDK_INT > 21){
            SoundPool.Builder builder = new SoundPool.Builder();
            //传入音频数量
            builder.setMaxStreams(5);
            //AudioAttributes是一个封装音频各种属性的方法
            AudioAttributes attrBuilder =  new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setLegacyStreamType(AudioManager.STREAM_MUSIC)
                    .build();
            //加载一个AudioAttributes
            builder.setAudioAttributes(attrBuilder).setMaxStreams(5);
            mSoundPool = builder.build();
        }else{
            mSoundPool = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
        }
        return  mSoundPool;
    }

    /**
     *判断是否为PCM格式
     */
    private  boolean isPCM(String inPath){
        if (inPath.endsWith(".pcm")){
            return  true;
        }
        return  false;
    }

    /**
     * 格式转换
     */
    private  int pcmConvertToWAV(){
        int pcmToWav =0;
        if (mParams != null){
             pcmToWav = new PcmToWavUtil(mParams.getRequency(), mParams.getChannelConfig(),mParams.getAudioFormat()).pcmToWav(inPath, outPath);
        }else {
            pcmToWav = new PcmToWavUtil(8000, AudioFormat.CHANNEL_OUT_MONO,AudioFormat.ENCODING_PCM_16BIT).pcmToWav(inPath, outPath);
        }
        return  pcmToWav;
    }



}


