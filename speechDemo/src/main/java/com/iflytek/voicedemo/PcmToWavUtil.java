package com.iflytek.voicedemo;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioFormat;
import android.media.AudioTrack;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.List;
import java.util.concurrent.Executors;

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



    public static String getAssetsPath(String filename) {
        return "file:///android_asset/" + filename;
    }

    public static InputStream getInputStreamFromAssets(String filename,Context context) throws IOException {
        AssetManager manager = context.getAssets();
        return manager.open(filename);
    }

    public static String[] listFilesFromPath(String path,Context context) throws IOException {
        AssetManager manager = context.getAssets();
        return manager.list(path);
    }


    public static AssetFileDescriptor getAssetFileDescription(String filename,Context context) throws IOException {
        AssetManager manager = context.getResources().getAssets();
        return manager.openFd(filename);
    }


    public static void writeStringToFile(String string, File file, boolean isAppending) {
        FileWriter writer = null;
        BufferedWriter bufferedWriter = null;
        try {
            writer = new FileWriter(file);
            bufferedWriter = new BufferedWriter(writer);
            if (isAppending) {
                bufferedWriter.append(string);
            } else {
                bufferedWriter.write(string);
            }
            bufferedWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (writer != null) {
                    writer.close();
                }
                if (bufferedWriter != null) {
                    bufferedWriter.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public static void writeStringToFile(String string, String filePath, boolean isAppending) {
        writeStringToFile(string, new File(filePath), isAppending);
    }


    public static Bitmap getBitmapFromAssets(String filename,Context context) throws IOException {
        AssetManager manager = context.getAssets();
        InputStream inputStream = manager.open(filename);
        return BitmapFactory.decodeStream(inputStream);
    }


    public static void copyAssetsToFile(final String assetFilename, final String dstName,final Context context) {
        Executors.newCachedThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                FileOutputStream fos = null;
                try {
                    File dstFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "ktools", dstName);
                    fos = new FileOutputStream(dstFile);
                    InputStream fileInputStream = getInputStreamFromAssets(assetFilename,context);
                    byte[] buffer = new byte[1024 * 2];
                    int byteCount;
                    while ((byteCount = fileInputStream.read(buffer)) != -1) {
                        fos.write(buffer, 0, byteCount);
                    }
                    fos.flush();
                } catch (IOException e) {
                } finally {
                    if (fos != null) {
                        try {
                            fos.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });

    }


    /**
     * @param filename  filename you will create
     * @param directory directory where the file exists
     * @return true if the file created successfully, or return false
     */
    public static boolean createFile(String filename, String directory) {
        boolean isSuccess = false;
        File file = new File(directory, filename);
        if (!file.exists()) {
            try {
                isSuccess = file.createNewFile();
            } catch (IOException e) {
            }
        } else {
            file.delete();
            try {
                isSuccess = file.createNewFile();
            } catch (IOException e) {
            }
        }
        return isSuccess;
    }


    public static boolean hideFile(String directory, String filename) {
        boolean isSuccess;
        File file = new File(directory, filename);
        isSuccess = file.renameTo(new File(directory, ".".concat(filename)));
        if (isSuccess) {
            file.delete();
        }
        return isSuccess;
    }


    public static long getFolderSize(final String folderPath) {
        long size = 0;
        File directory = new File(folderPath);
        if (directory.exists() && directory.isDirectory()) {
            for (File file : directory.listFiles()) {
                if (file.isDirectory()) {
                    size += getFolderSize(file.getAbsolutePath());
                } else {
                    size += file.length();
                }
            }
        }
        return size;
    }


    public static String readFromFiles(String filename,Context context) {
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(getAssetFileDescription(filename,context).getFileDescriptor()));
            StringBuilder stringBuilder = new StringBuilder();
            String content;
            while ((content = bufferedReader.readLine()) != null) {
                stringBuilder.append(content);
            }
            return stringBuilder.toString();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


    /**
     * 合并wav音频文件
     * @param inputs
     * @param output
     * @throws IOException
     */

    public static void mergeWav(List<File> inputs, File output) throws IOException {
        if (inputs.size() < 1) {
            return;
        }
        FileInputStream fis = new FileInputStream(inputs.get(0));
        FileOutputStream fos = new FileOutputStream(output);
        byte[] buffer = new byte[2048];
        int total = 0;
        int count;
        while ((count = fis.read(buffer)) > -1) {
            fos.write(buffer, 0, count);
            total += count;
        }
        fis.close();
        for (int i = 1; i < inputs.size(); i++) {
            File file = inputs.get(i);
            Header header = resolveHeader(file);
            FileInputStream dataInputStream = header.dataInputStream;
            while ((count = dataInputStream.read(buffer)) > -1) {
                fos.write(buffer, 0, count);
                total += count;
            }
            dataInputStream.close();
        }
        fos.flush();
        fos.close();
        Header outputHeader = resolveHeader(output);
        outputHeader.dataInputStream.close();
        RandomAccessFile res = new RandomAccessFile(output, "rw");
        res.seek(4);
        byte[] fileLen = intToByteArray(total + outputHeader.dataOffset - 8);
        res.write(fileLen, 0, 4);
        res.seek(outputHeader.dataSizeOffset);
        byte[] dataLen = intToByteArray(total);
        res.write(dataLen, 0, 4);
        res.close();
    }

    /**
     * 解析头部，并获得文件指针指向数据开始位置的InputStreram，记得使用后需要关闭
     */
    private static Header resolveHeader(File wavFile) throws IOException {
        FileInputStream fis = new FileInputStream(wavFile);
        byte[] byte4 = new byte[4];
        byte[] buffer = new byte[2048];
        int readCount = 0;
        Header header = new Header();
        fis.read(byte4);//RIFF
        fis.read(byte4);
        readCount += 8;
        header.fileSizeOffset = 4;
        header.fileSize = byteArrayToInt(byte4);
        fis.read(byte4);//WAVE
        fis.read(byte4);//fmt
        fis.read(byte4);
        readCount += 12;
        int fmtLen = byteArrayToInt(byte4);
        fis.read(buffer, 0, fmtLen);
        readCount += fmtLen;
        fis.read(byte4);//data or fact
        readCount += 4;
        if (isFmt(byte4, 0)) {//包含fmt段
            fis.read(byte4);
            int factLen = byteArrayToInt(byte4);
            fis.read(buffer, 0, factLen);
            fis.read(byte4);//data
            readCount += 8 + factLen;
        }
        fis.read(byte4);// data size
        int dataLen = byteArrayToInt(byte4);
        header.dataSize = dataLen;
        header.dataSizeOffset = readCount;
        readCount += 4;
        header.dataOffset = readCount;
        header.dataInputStream = fis;
        return header;
    }

    private static boolean isRiff(byte[] bytes, int start) {
        if (bytes[start + 0] == 'R' && bytes[start + 1] == 'I' && bytes[start + 2] == 'F' && bytes[start + 3] == 'F') {
            return true;
        } else {
            return false;
        }
    }

    private static boolean isFmt(byte[] bytes, int start) {
        if (bytes[start + 0] == 'f' && bytes[start + 1] == 'm' && bytes[start + 2] == 't' && bytes[start + 3] == ' ') {
            return true;
        } else {
            return false;
        }
    }

    private static boolean isData(byte[] bytes, int start) {
        if (bytes[start + 0] == 'd' && bytes[start + 1] == 'a' && bytes[start + 2] == 't' && bytes[start + 3] == 'a') {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 将int转化为byte[]
     */
    private static byte[] intToByteArray(int data) {
        return ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(data).array();
    }

    /**
     * 将short转化为byte[]
     */
    private static byte[] shortToByteArray(short data) {
        return ByteBuffer.allocate(2).order(ByteOrder.LITTLE_ENDIAN).putShort(data).array();
    }

    /**
     * 将byte[]转化为short
     */
    private static short byteArrayToShort(byte[] b) {
        return ByteBuffer.wrap(b).order(ByteOrder.LITTLE_ENDIAN).getShort();
    }

    /**
     * 将byte[]转化为int
     */
    private static int byteArrayToInt(byte[] b) {
        return ByteBuffer.wrap(b).order(ByteOrder.LITTLE_ENDIAN).getInt();
    }

    /**
     * 头部部分信息
     */
    static class Header {
        public int fileSize;
        public int fileSizeOffset;
        public int dataSize;
        public int dataSizeOffset;
        public int dataOffset;
        public FileInputStream dataInputStream;
    }

}
