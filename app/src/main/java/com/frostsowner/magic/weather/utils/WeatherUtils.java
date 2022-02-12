package com.frostsowner.magic.weather.utils;

public class WeatherUtils {

    public static String getAqiValue(String value){
        if(StringUtils.isEmpty(value))return "--";
        if(!StringUtils.isNumber(value))return "--";
        int v = Integer.parseInt(value);
        if(v < 0){
            return "--";
        }
        if(v > 0 && v <= 50){
            return "优";
        }
        if(v > 50 && v <= 100){
            return "良";
        }
        if(v >= 100 && v <= 150){
            return "轻度";
        }
        if(v > 150 && v <= 200){
            return "中度";
        }
        if(v > 200 && v <= 300){
            return "重度";
        }
        if(v > 300){
            return "严重";
        }
        return "--";
    }

    public static String getAqiTips(String value){
        if(StringUtils.isEmpty(value))return "--";
        if(!StringUtils.isNumber(value))return "--";
        int v = Integer.parseInt(value);
        if(v < 0){
            return "--";
        }
        if(v > 0 && v <= 50){
            return "空气质量很好，基本无污染，可以正常外出活动，呼吸新鲜空气。";
        }
        if(v > 50 && v <= 100){
            return "可以正常活动，易敏感人群应减少外出。";
        }
        if(v >= 100 && v <= 150){
            return "儿童，老年人及心脏病呼吸系统疾病患者应减少长时间高强度的户外运动。";
        }
        if(v > 150 && v <= 200){
            return "儿童，老年人及心脏病呼吸系统疾病患者应避免长时间高强度的户外运动，一般人群适量减少户外运动。";
        }
        if(v > 200 && v <= 300){
            return "儿童，老年人及心脏病、肺病患者应停留在室内，停止户外运动，一般人群减少户外运动。";
        }
        if(v > 300){
            return "儿童，老年人和病人应留在室内，避免体力消耗，一般人群应避免户外活动。";
        }
        return "--";
    }

    public static String getWindDir(String dirCode){
        if(dirCode.equals("N"))return "北风";
        if(dirCode.equals("NNE") || dirCode.equals("NE") || dirCode.equals("ENE"))return "东北风";
        if(dirCode.equals("E"))return "东风";
        if(dirCode.equals("ESE") || dirCode.equals("SE") || dirCode.equals("SSE"))return "东南风";
        if(dirCode.equals("S"))return "南风";
        if(dirCode.equals("SSW") || dirCode.equals("SW") || dirCode.equals("WSW"))return "西南风";
        if(dirCode.equals("W"))return "西风";
        if(dirCode.equals("WNW") || dirCode.equals("NW") || dirCode.equals("NNW"))return "西北风";
        if(dirCode.equals("Calm"))return "微风";
        if(dirCode.equals("Whirlwind"))return "旋转风";
        return "无风";
    }

    public static String getStrVIS(String vis){
        if(StringUtils.isEmpty(vis))return "--";
        int index = -1;
        index = vis.lastIndexOf(".");
        if(index != -1){
            vis = vis.substring(0,index);
        }
        long data = Long.parseLong(vis);
        return (data/1000F)+"公里";
    }

    public static String getStrUvi(String uvi){
        if(StringUtils.isEmpty(uvi))return "--";
        int data = Integer.parseInt(uvi);
        if(data <= 3)return "弱("+data+"级)";
        if(data <= 6)return "中等("+data+"级)";
        return "强("+data+"级)";
    }
}
