package com.iflytek.voicedemo;

/**
 *
 * @author jiangrenming
 * @date 2018/9/13
 */

public class AudioParams {

    private int requency;
    private int channelConfig;
    private int audioFormat;

    public int getRequency() {
        return requency;
    }

    public void setRequency(int requency) {
        this.requency = requency;
    }

    public int getChannelConfig() {
        return channelConfig;
    }

    public void setChannelConfig(int channelConfig) {
        this.channelConfig = channelConfig;
    }

    public int getAudioFormat() {
        return audioFormat;
    }

    public void setAudioFormat(int audioFormat) {
        this.audioFormat = audioFormat;
    }
}
