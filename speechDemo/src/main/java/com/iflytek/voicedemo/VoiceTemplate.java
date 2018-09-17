package com.iflytek.voicedemo;

import android.content.Context;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jiangrenming on 2018/9/13.
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
