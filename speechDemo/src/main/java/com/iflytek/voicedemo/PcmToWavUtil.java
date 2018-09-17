package com.iflytek.voicedemo;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.AudioFormat;
import android.media.AudioTrack;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import it.sauronsoftware.jave.AudioAttributes;
import it.sauronsoftware.jave.Encoder;
import it.sauronsoftware.jave.EncodingAttributes;

import static android.os.Build.VERSION.SDK;

/**
 * Created by jiangrenming on 2018/9/13.
 */

public class PcmToWavUtil {

    /**
     * 缓存的音频大小
     */
    private int mBufferSize;
    /**
     * 采样率
     */
    private int mSampleRate;
    /**
     * 声道数
     */
    private int mChannel;


    /**
     * @param sampleRate sample rate、采样率
     * @param channel channel、声道
     * @param encoding Audio data format、音频格式
     */
    PcmToWavUtil(int sampleRate, int channel, int encoding) {
        this.mSampleRate = sampleRate;
        this.mChannel = channel;
        this.mBufferSize = AudioTrack.getMinBufferSize(mSampleRate, mChannel, encoding);
    }


    /**
     * pcm文件转wav文件
     * @param inFilename 源文件路径
     * @param outFilename 目标文件路径
     */
    public int pcmToWav(String inFilename, String outFilename) {
        FileInputStream in;
        FileOutputStream out;
        long totalAudioLen;
        long totalDataLen;
        long longSampleRate = mSampleRate;
        int channels = mChannel == AudioFormat.CHANNEL_IN_MONO ? 1 : 2;
        long byteRate = 16 * mSampleRate * channels / 8;
        byte[] data = new byte[mBufferSize];
        try {
            in = new FileInputStream(inFilename);
            out = new FileOutputStream(outFilename);
            totalAudioLen = in.getChannel().size();
            totalDataLen = totalAudioLen + 36;

            writeWaveFileHeader(out, totalAudioLen, totalDataLen,
                    longSampleRate, channels, byteRate);
            while (in.read(data) != -1) {
                out.write(data);
            }
            in.close();
            out.close();
            return 1;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return  -1;
    }


    /**
     * 加入wav文件头
     */
    private void writeWaveFileHeader(FileOutputStream out, long totalAudioLen,
                                     long totalDataLen, long longSampleRate, int channels, long byteRate)
            throws IOException {
        byte[] header = new byte[44];
        // RIFF/WAVE header
        header[0] = 'R';
        header[1] = 'I';
        header[2] = 'F';
        header[3] = 'F';
        header[4] = (byte) (totalDataLen & 0xff);
        header[5] = (byte) ((totalDataLen >> 8) & 0xff);
        header[6] = (byte) ((totalDataLen >> 16) & 0xff);
        header[7] = (byte) ((totalDataLen >> 24) & 0xff);
        //WAVE
        header[8] = 'W';
        header[9] = 'A';
        header[10] = 'V';
        header[11] = 'E';
        // 'fmt ' chunk
        header[12] = 'f';
        header[13] = 'm';
        header[14] = 't';
        header[15] = ' ';
        // 4 bytes: size of 'fmt ' chunk
        header[16] = 16;
        header[17] = 0;
        header[18] = 0;
        header[19] = 0;
        // format = 1
        header[20] = 1;
        header[21] = 0;
        header[22] = (byte) channels;
        header[23] = 0;
        header[24] = (byte) (longSampleRate & 0xff);
        header[25] = (byte) ((longSampleRate >> 8) & 0xff);
        header[26] = (byte) ((longSampleRate >> 16) & 0xff);
        header[27] = (byte) ((longSampleRate >> 24) & 0xff);
        header[28] = (byte) (byteRate & 0xff);
        header[29] = (byte) ((byteRate >> 8) & 0xff);
        header[30] = (byte) ((byteRate >> 16) & 0xff);
        header[31] = (byte) ((byteRate >> 24) & 0xff);
        // block align
        header[32] = (byte) (2 * 16 / 8);
        header[33] = 0;
        // bits per sample
        header[34] = 16;
        header[35] = 0;
        //data
        header[36] = 'd';
        header[37] = 'a';
        header[38] = 't';
        header[39] = 'a';
        header[40] = (byte) (totalAudioLen & 0xff);
        header[41] = (byte) ((totalAudioLen >> 8) & 0xff);
        header[42] = (byte) ((totalAudioLen >> 16) & 0xff);
        header[43] = (byte) ((totalAudioLen >> 24) & 0xff);
        out.write(header, 0, 44);
    }

    /**
     * wav格式转换成map3格式
     * @param inPath
     * @param outFile
     * @return
     */
    public static boolean wavTomp3(String inPath,String outFile){
        boolean status=false;
        File file=new File(inPath);
        try {
            execute(file,outFile);
            status=true;
        } catch (Exception e) {
            status=false;
            e.printStackTrace();
        }
        return status;
    }

    /**
     * 执行转化
     * @param source 输入文件
     * @param desFileName  目标文件名
     * @return  转换之后文件
     */
    public static File execute(File source, String desFileName) {
        try{
            File target = new File(desFileName);
            AudioAttributes audio = new AudioAttributes();
            audio.setCodec("libmp3lame");
            audio.setBitRate(new Integer(36000));
            audio.setChannels(new Integer(2));
            audio.setSamplingRate(new Integer(44100));
            audio.setVolume(new Integer(256));
            EncodingAttributes attrs = new EncodingAttributes();
            attrs.setFormat("mp3");
            attrs.setAudioAttributes(audio);
            Encoder encoder = new Encoder();
            encoder.encode(source, target, attrs);
            return target;
        }catch (Exception e){
            e.printStackTrace();
        }
       return  null;
    }

    /**
     * 读取文件字节数组
     * @param path
     * @return
     */
    public static byte[] readFromFile(String path) {
        try {
            File file = new File(path);
            if (!file.exists()) {
                Log.i("文件是否存在", false + "");
                return null;
            }
            FileInputStream is = new FileInputStream(file);
            byte[] datas = new byte[(int) ((file.length()) / 2)];
            int i = 0;
            while (is.available() > 0) {
                int read = is.read(datas);
                Log.i("读取的长度:",read+"");
            }
            is.close();
            return datas;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取AssetsFileDescript
     */
    public static AssetFileDescriptor getAssetFileDescription(Context mContext,String filename) throws IOException {
        AssetManager manager =mContext.getAssets();
        return manager.openFd(filename);
    }


}
