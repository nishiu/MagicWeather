package com.frostsowner.magic.weather.utils;

import android.text.TextUtils;

import com.fm.commons.widget.SimpleNotice;

/**
 * Created by yulijun on 2017/12/12.
 */

public class VerifyUtils {


    public static boolean aliPayVerify(String resultStatus){
        if(TextUtils.equals(resultStatus,"4000")){
            SimpleNotice.show("订单支付失败");
            return false;
        }
        if(TextUtils.equals(resultStatus,"8000")){
            SimpleNotice.show("正在处理中，支付结果未知");
            return false;
        }
        if(TextUtils.equals(resultStatus,"5000")){
            SimpleNotice.show("重复请求");
            return false;
        }
        if(TextUtils.equals(resultStatus,"6001")){
            SimpleNotice.show("支付取消");
            return false;
        }
        if(TextUtils.equals(resultStatus,"6002")){
            SimpleNotice.show("网络连接出错");
            return false;
        }
        if(TextUtils.equals(resultStatus,"6004")){
            SimpleNotice.show("支付结果未知");
            return false;
        }
        if(TextUtils.equals(resultStatus,"9000")) {
            SimpleNotice.show("支付成功");
            return true;
        }
        SimpleNotice.show("支付失败");
        return false;
    }
}
