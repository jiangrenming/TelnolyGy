package com.iflytek.voicedemo;

/**
 *
 * @author jiangrenming
 * @date 2018/9/25
 * 播报类型
 */

public class VoiceType {


    /**
     * 前缀播报类型(目前只有 :星POS)
     */
    public  static  final String STAR_POS = "star_pos";

    /**
     * 中间部分（交易类型）
     */
    public static  class VoiceTrans{
        //支付宝
        public static final String ALIPAY = "alipay";
        //微信
        public static final String WECHAT = "wechat";
        //银行卡
        public static final String BANK = "bank";
        //银联二维码
        public static final String BANK_CODE = "bankcode";
        //扫码退货
        public static final String SCAN = "scan_return";
        //银行卡退货
        public static final String BANK_RETURN = "bank_return";
        //银行卡消费撤销
        public static final String BANK_CONSUME_REVERSE = "bank_consume_reverse";
        //银行卡预授权
        public static final String BANK_PRELICENS = "bank_prelicens";
        //银行卡预授权完成
        public static final String BANK_PRELICENS_COMPLETE = "bank_prelicens_complete";
        //银行卡预授权撤销
        public static final String BANK_PRELICENS_REVERSE = "bank_reverse";
        //银行卡预授权完成撤销
        public static final String BANK_PRELICENS_COMPLETE_REVERSE = "bank_complete_reverse";
        //现金记账
        public static  final String CASH ="cash";
        //代表“收款”
        public static  final String RECEIVE ="receive";

    }

    /**
     * 尾部(交易行为)
     */
    public static  class TransPrefix{
        //撤销
        public static  final String REVERSE ="reverse";
        //完成
        public static  final String COMPLETE ="complete";
        //完成撤销
        public static  final String COMPLETE_REVERSE ="complete_reverse";
        //退货
        public static  final String RETURN ="return";
    }


}
