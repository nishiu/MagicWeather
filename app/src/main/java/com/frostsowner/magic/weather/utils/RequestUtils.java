package com.frostsowner.magic.weather.utils;

import com.fm.commons.widget.SimpleNotice;

public class RequestUtils {

    public static boolean signResult(int code){
        switch (code){
            case 1:
                SimpleNotice.show("签到成功");
                return true;
            case 10:
                SimpleNotice.show("用户不存在");
                return false;
            case 1010001:
                return false;
        }
        return false;
    }
}
