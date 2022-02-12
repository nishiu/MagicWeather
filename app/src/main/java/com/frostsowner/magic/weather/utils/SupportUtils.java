package com.frostsowner.magic.weather.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
import android.view.View;

import com.fm.commons.util.PixelUtils;
import com.fm.commons.util.StringUtils;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by yulijun on 2017/6/22.
 */

public class SupportUtils {

    public static final String[] AUDIO_SUPPORT = new String[]{".mp3",".pcm",".aac",".m4a"};
    public static final String[] VIDEO_SUPPORT = new String[]{".mp4"};

    public static boolean isAudioSupport(String suffix){
        for(String s : AUDIO_SUPPORT){
            if(s.equals(suffix)){
                return true;
            }
        }
        return false;
    }

    public static boolean isVideoSupport(String suffix){
        for(String s : VIDEO_SUPPORT){
            if(s.equals(suffix)){
                return true;
            }
        }
        return false;
    }

    /**
     * 秒转时间(String)
     * @param duration
     * @return
     */
    public static String transformTime(int duration){
        int min = duration/60;
        int second = duration - min*60;
        String minStr = min < 10 ? "0"+min : ""+min;
        String secondStr = second < 10 ? "0"+second : ""+second;
        return minStr+":"+secondStr;
    }

    /**
     * bit转mb(String)
     * @param size
     * @return
     */
    public static String transformSize(long size){
        float mb = size*1.0f/(1024*1024);
        return String.format(Locale.getDefault(),"%.1fMB",mb);
    }

    /**
     * 修正某些字符串与后端sql字段字符集不兼容的问题，
     * 比如emoji表情符号和颜文字等字符串在mysql5.5以前插入字段异常等，5.5以后可以通过修改mysql配置来适配
     * 参考使用
     * @param str
     * @return
     */
    public static String stringTransform(String str){
        if(!StringUtils.isEmpty(str))return str.replaceAll("[\\x{10000}-\\x{10FFFF}]","");
        return str;
    }

    /**
     * float zhuan int 并且进行四舍五入
     * @param value
     * @return
     */
    public static int float2IntTransform(float value){
        return float2IntTransform(value,true);
    }

    /**
     * float 换算取整 int,并且进行四舍五入
     * @param value 需要换算的值
     * @param isRounding 是否需要四舍五入
     * @return
     */
    public static int float2IntTransform(float value, boolean isRounding){
        int i = (int)value;
        if(!isRounding)return i;

        //判断正数
        if(value >= 0.00000001){
            // simple: 12.53、0.53
            if((int)((value*10 - 5)/10) == i && value >= 0.5f){
                return i+1;
            }else{
                return i;
            }
        }
        //判断负数
        else if(value <= 0.0000001){
            // simple: -12.53、-0.53
            if((int)((value*10 + 5)/10) == i && value <= -0.5f){
                return i-1;
            }else{
                return i;
            }
        }
        else{
            return 0;
        }
    }

    /**
     * 小数点自动补正 例: 1.3333后是2
     * @param value
     * @return
     */
    public static int float2IntTransform2(float value){
        int i = (int)value;
        //判断正数
        if(value >= 0.00000001){
            // simple: 12.13、0.53
            if(value - i > 0.000001f){
                return i+1;
            }else{
                return i;
            }
        }
        //判断负数
        else if(value <= 0.0000001){
            // simple: -12.53、-0.53
            if(Math.abs(value - i) > 0.000001f){
                return i-1;
            }else{
                return i;
            }
        }
        else{
            return 0;
        }
    }

    /**
     * 计算打折率
     * @param oprice 原价
     * @param nprice 现价
     * @return
     */
    public static String discountString(float oprice,float nprice){
        float discount = nprice/oprice;
        return String.format(Locale.getDefault(),"%.1f折",discount*10);
    }

    /**
     * 计算两个日期相差几天
     * @param startDay
     * @param endDay
     * @return
     */
    public static long getSubDay(long startDay,long endDay){
        if(endDay - startDay <= 0)return 0;
        return (endDay - startDay)/(60*1000*60*24);
    }

    public static long getSubDay(String startDay,String endDay){
        long start = StringUtils.isEmpty(startDay)?0:Long.parseLong(startDay);
        long end = StringUtils.isEmpty(endDay)?0:Long.parseLong(endDay);
        return getSubDay(start,end);
    }

    public static int getWeek(int year,int month,int day){
        Calendar date = Calendar.getInstance();
        date.set(year, month - 1, day);
        return date.get(Calendar.DAY_OF_WEEK) - 1;
    }

    public static String weekNameTransform(int week){
        if (week == 1) {
            return "周一";
        }
        if (week == 2) {
            return "周二";
        }
        if (week == 3) {
            return "周三";
        }
        if (week == 4) {
            return "周四";
        }
        if (week == 5) {
            return "周五";
        }
        if (week == 6) {
            return "周六";
        }
        return "周日";
    }

    public static String getCalendarTimestamp(int year,int month,int day){
        StringBuilder builder = new StringBuilder();
        builder.append(year);
        builder.append(month<10?"0"+month:month);
        builder.append(day < 10?"0"+day:day);
        builder.append("000000");
        return builder.toString();
    }

    public static long getHours(long time){
        return time/1000/60/60;
    }

    public static long getDay(long time){
        return time/1000/60/60/24;
    }


    /**
     * 判断当前view是否在屏幕可见
     */
    public static boolean getLocalVisibleRect(Context context, View view) {
        Point p = new Point();
        ((Activity) context).getWindowManager().getDefaultDisplay().getSize(p);
        int screenWidth = p.x;
        int screenHeight = p.y - PixelUtils.dp2px(context,60);
        Rect rect = new Rect(0, 0, screenWidth, screenHeight);
        int[] location = new int[2];
        view.getLocationInWindow(location);
        location[1] = location[1] - PixelUtils.dp2px(context, 60);
//        Log.e("ijimu","screenHeight : "+screenHeight+" , location1 : "+location[1]);
        view.setTag(location[1]);//存储y方向的位置
        if (view.getLocalVisibleRect(rect)){
            return true;
        } else {
            return false;
        }
    }

    /**
     * 获取两个日期之间的间隔天数
     * @return
     */
    public static int getGapCount(Date startDate, Date endDate){
        Calendar fromCalendar = Calendar.getInstance();
        fromCalendar.setTime(startDate);
        fromCalendar.set(Calendar.HOUR_OF_DAY, 0);
        fromCalendar.set(Calendar.MINUTE, 0);
        fromCalendar.set(Calendar.SECOND, 0);
        fromCalendar.set(Calendar.MILLISECOND, 0);

        Calendar toCalendar = Calendar.getInstance();
        toCalendar.setTime(endDate);
        toCalendar.set(Calendar.HOUR_OF_DAY, 0);
        toCalendar.set(Calendar.MINUTE, 0);
        toCalendar.set(Calendar.SECOND, 0);
        toCalendar.set(Calendar.MILLISECOND, 0);
        return (int) ((toCalendar.getTime().getTime() - fromCalendar.getTime().getTime()) / (1000 * 60 * 60 * 24));
    }

        /**
    //     甲子日
    //     甲子（金匮 吉） 乙丑（天德贵人 吉） 丙寅（白虎 凶） 丁卯（玉堂天开吉神 吉）
    //     戊辰（天牢 凶） 己巳（元武黑道 凶） 庚午（司令金星 吉） 辛未（勾陈 凶）
    //     壬申（青龙 吉） 癸酉（明堂 吉） 甲戌（天刑孤辰 凶） 乙亥（朱雀天讼 凶）

    //     甲寅日
    //     甲子（青龙 吉） 乙丑（贵人明堂 吉） 丙寅（天刑 凶） 丁卯（元武 凶）
    //     戊辰（金匮 吉） 己巳（天德 吉） 庚午（白虎 凶） 辛未（玉堂 吉）
    //     壬申（空亡 凶） 癸酉（截路 凶） 甲戌（司命贵人 吉） 乙亥（勾陈 凶）

    //     甲辰日
    //     甲子（元武天牢 凶） 乙丑（截路空亡 凶） 丙寅（三合黄道 吉） 丁卯（勾陈 凶）
    //     戊辰（青龙 吉） 己巳（黄道明堂 吉） 庚午（天刑五鬼 凶） 辛未（朱雀 凶）
    //     壬申（金匮 吉） 癸酉（天德黄道 吉） 甲戌（白虎 凶） 乙亥（玉堂天开吉神 吉）

    //     甲午日
    //     甲子（金匮 吉） 乙丑（天乙贵人 吉） 丙寅（白虎 凶） 丁卯（玉堂 吉）
    //     戊辰（天牢 凶） 己巳（元武 凶） 庚午（司命 吉） 辛未（勾陈 凶）
    //     壬申（青龙 吉） 癸酉（明堂 吉） 甲戌（天刑 凶） 乙亥（朱雀 凶）

    //     甲申日
    //     甲子（青龙 吉） 乙丑（明堂 吉） 丙寅（天牢 凶） 丁卯（朱雀 凶）
    //     戊辰（金匮 吉） 己巳（天德 吉） 庚午（白虎 凶） 辛未（玉堂 吉）
    //     壬申（截路 凶） 癸酉（截路 凶） 甲戌（司命 吉） 乙亥（勾陈 凶）

    //     甲戌日
    //     甲子（元武天刑 凶） 乙丑（截路空亡 凶） 丙寅（三合黄道 吉） 丁卯（勾陈 凶）
    //     戊辰（青龙 吉） 己巳（黄道明堂 吉） 庚午（天牢五鬼 凶） 辛未（玉堂 吉）
    //     壬申（金匮 吉） 癸酉（天德黄道 吉） 甲戌（白虎 凶） 乙亥（天开玉堂 吉）

         --------------------------------------------------------------------------
    //     乙丑日
    //     丙子（天刑 凶） 丁丑（朱雀 凶） 戊寅（金匮 吉） 己卯（天德 吉）
    //     庚辰（白虎 凶） 辛巳（玉堂 吉） 壬午（天牢 凶） 癸未（元武 凶）
    //     甲申（贵人司命 吉） 乙酉（勾陈 凶） 丙戌（青龙 吉） 丁亥（明堂 吉）

    //     乙卯日
    //     丙子（司命贵人 吉） 丁丑（勾陈 凶） 戊寅（青龙 吉） 己卯（明堂 吉）
    //     庚辰（天刑 凶） 辛巳（朱雀 凶） 壬午（金匮 吉） 癸未（天德 吉）
    //     甲申（白虎 凶） 乙酉（玉堂 吉） 丙戌（天牢 凶） 丁亥（元武 凶）

    //     乙巳日
    //     丙子（白虎 凶） 丁丑（天德 吉） 戊寅（天牢 凶） 己卯（寡宿五鬼 凶）
    //     庚辰（司命黄道 吉） 辛巳（勾陈 凶） 壬午（青龙明堂 吉） 癸未（勾陈 凶）
    //     甲申（朱雀 凶） 乙酉（天刑 凶） 丙戌（金匮 吉） 丁亥（天德 吉）

    //     乙未日
    //     丙子（天刑 凶） 丁丑（朱雀 凶） 戊寅（金匮 吉） 己卯（天德 吉）
    //     庚辰（白虎 凶） 辛巳（玉堂 吉） 壬午（截路空亡 凶） 癸未（截路 凶）
    //     甲申（司命 吉） 乙酉（勾陈 凶） 丙戌（青龙 吉） 丁亥（明堂 吉）

    //     乙酉日
    //     丙子（天乙贵人 吉） 丁丑（勾陈 凶） 戊寅（青龙 吉） 己卯（明堂 吉）
    //     庚辰（天牢 凶） 辛巳（朱雀 凶） 壬午（天贵黄道 吉） 癸未（天德 吉）
    //     甲申（白虎 凶） 乙酉（玉堂 吉） 丙戌（天牢 凶） 丁亥（元武 凶）

    //     乙亥日
    //     丙子（白虎 凶） 丁丑（天德 吉） 戊寅（天牢 凶） 己卯（寡宿五鬼 凶）
    //     庚辰（司命黄道 吉） 辛巳（勾陈 凶） 壬午（青龙明堂 吉） 癸未（勾陈 凶）
    //     甲申（朱雀 凶） 乙酉（天刑 凶） 丙戌（金匮 吉） 丁亥（天德 吉）

         --------------------------------------------------------------------------
    //     丙子日
    //     戊子（金匮 吉） 己丑（天德 吉） 庚寅（白虎 凶） 辛卯（玉堂 吉）
    //     壬辰（截路 凶） 癸巳（截路 凶） 甲午（司命 吉） 乙未（勾陈 凶）
    //     丙申（青龙 吉） 丁酉（明堂贵人 吉） 戊戌（天刑 凶） 己亥（朱雀 凶）

    //     丙寅日
    //     戊子（青龙 吉） 己丑（明堂 吉） 庚寅（天刑 凶） 辛卯（朱雀 凶）
    //     壬辰（金匮 吉） 癸巳（天德 吉） 甲午（白虎 凶） 乙未（明堂 吉）
    //     丙申（天牢 凶） 丁酉（元武 凶） 戊戌（司命太阴 吉） 己亥（勾陈 凶）

    //     丙辰日
    //     戊子（天刑 凶） 己丑（元武 凶） 庚寅（司命 吉） 辛卯（勾陈 凶）
    //     壬辰（青龙 吉） 癸巳（天乙黄道 吉） 甲午（天牢 凶） 乙未（朱雀 凶）
    //     丙申（金匮 吉） 丁酉（天德贵人 吉） 戊戌（白虎 凶） 己亥（玉堂贵人 吉）

    //     丙午日
    //     戊子（金匮 吉） 己丑（天德 吉） 庚寅（白虎 凶） 辛卯（玉堂 吉）
    //     壬辰（截路 凶） 癸巳（截路 凶） 甲午（司命 吉） 乙未（勾陈 凶）
    //     丙申（青龙 吉） 丁酉（明堂 凶） 戊戌（天牢 凶） 己亥（朱雀 凶）

    //     丙申日
    //     戊子（青龙 吉） 己丑（明堂 吉） 庚寅（天刑 凶） 辛卯（朱雀 凶）
    //     壬辰（金匮 吉） 癸巳（天德 吉） 甲午（白虎 凶） 乙未（玉堂 吉）
    //     丙申（天牢 凶） 丁酉（元武 凶） 戊戌（司命 吉） 己亥（勾陈 凶）

    //     丙戌日
    //     戊子（天牢 凶） 己丑（元武 凶） 庚寅（司命 吉） 辛卯（勾陈 凶）
    //     壬辰（天乙贵人 吉） 癸巳（贵人明堂 吉） 甲午（天刑 凶） 乙未（朱雀 凶）
    //     丙申（金匮 吉） 丁酉（天德 吉） 戊戌（白虎 凶） 己亥（玉堂 吉）

         --------------------------------------------------------------------------
    //     丁丑日
    //     庚子（天刑 凶） 辛丑（朱雀 凶） 壬寅（金匮 吉） 癸卯（天德 吉）
    //     甲辰（白虎 凶） 乙巳（玉堂 吉） 丙午（天牢 凶） 丁未（元武 凶）
    //     戊申（司命 吉） 己酉（勾陈 凶） 庚戌（青龙 吉） 辛亥（明堂贵人 吉）

    //     丁卯日
    //     庚子（司命 吉） 辛丑（勾陈 凶） 壬寅（青龙天贵 吉） 癸卯（明堂 吉）
    //     甲辰（天刑 凶） 乙巳（朱雀 凶） 丙午（金匮 吉） 丁未（天德贵人 吉）
    //     戊申（白虎 凶） 己酉（玉堂贵人 吉） 庚戌（天牢 凶） 辛亥（元武 凶）

    //     丁巳日
    //     庚子（白虎 凶） 辛丑（玉堂 吉） 壬寅（截路 凶） 癸卯（截路 凶）
    //     甲辰（司命玉堂 吉） 乙巳（勾陈 凶） 丙午（青龙 吉） 丁未（明堂 吉）
    //     戊申（天刑 凶） 己酉（朱雀 凶） 庚戌（金匮 吉） 辛亥（天德贵人 吉）

    //     丁未日
    //     庚子（天刑 凶） 辛丑（朱雀 凶） 壬寅（金匮 吉） 癸卯（天德 吉）
    //     甲辰（白虎 凶） 乙巳（玉堂 吉） 丙午（天牢 凶） 丁未（元武 凶）
    //     戊申（司命 吉） 己酉（勾陈 凶） 庚戌（青龙 吉） 辛亥（明堂贵人 吉）

    //     丁酉日
    //     庚子（司命黄道 吉） 辛丑（勾陈 凶） 壬寅（天贵青龙 吉） 癸卯（天乙明堂 吉）
    //     甲辰（天刑 凶） 乙巳（朱雀 凶） 丙午（金匮 吉） 丁未（天德 吉）
    //     戊申（白虎 凶） 己酉（玉堂贵人 吉） 庚戌（天牢 凶） 辛亥（元武 凶）

    //     丁亥日
    //     庚子（白虎 凶） 辛丑（玉堂 吉） 壬寅（截路 凶） 癸卯（截路空亡 凶）
    //     甲辰（司命黄道 吉） 乙巳（勾陈 凶） 丙午（青龙 吉） 丁未（明堂 吉）
    //     戊申（天刑 凶） 己酉（朱雀 凶） 庚戌（金匮 吉） 辛亥（天德 吉）

         --------------------------------------------------------------------------
    //     戊子日
    //     壬子（金匮 吉） 癸丑（天德 吉） 甲寅（白虎 凶） 乙卯（玉堂 吉）
    //     丙辰（天牢 凶） 丁巳（元武 凶） 戊午（司命 吉） 己未（勾陈 凶）
    //     庚申（青龙 吉） 辛酉（明堂 吉） 壬戌（天刑 凶） 癸亥（朱雀 凶）

    //     戊寅日
    //     庚子（青龙 吉） 辛丑（明堂 吉） 壬寅（天刑 凶） 癸卯（朱雀 凶）
    //     甲辰（金匮 吉） 乙巳（天德 吉） 丙午（白虎 凶） 丁未（玉堂 吉）
    //     戊申（天牢 凶） 己酉（朱雀 凶） 庚戌（司命 吉） 辛亥（勾陈 凶）

    //     戊辰日
    //     庚子（天牢 凶） 辛丑（元武 凶） 壬寅（司命 吉） 癸卯（勾陈 凶）
    //     甲辰（青龙 吉） 乙巳（明堂 吉） 丙午（天刑 凶） 丁未（朱雀 凶）
    //     戊申（金匮 吉） 己酉（天德六合 吉） 庚戌（白虎 凶） 辛亥（玉堂 吉）

    //     戊午日
    //     壬子（金匮 吉） 癸丑（天德 吉） 甲寅（白虎 凶） 乙卯（玉堂 吉）
    //     丙辰（天牢 凶） 丁巳（元武 凶） 戊午（司命 吉） 己未（勾陈 凶）
    //     庚申（青龙 吉） 辛酉（明堂 吉） 壬戌（天刑 凶） 癸亥（朱雀 凶）

    //     戊申日
    //     庚子（青龙 吉） 辛丑（天官 吉） 壬寅（天刑 凶） 癸卯（朱雀 凶）
    //     甲辰（金匮 吉） 乙巳（天德 吉） 丙午（天牢 凶） 丁未（天乙贵人 吉）
    //     戊申（天牢 凶） 己酉（勾陈 凶） 庚戌（司命 吉） 辛亥（勾陈 凶）

    //     戊戌日
    //     庚子（截路 凶） 辛丑（截路 凶） 壬寅（天乙明堂 吉） 癸卯（勾陈 凶）
    //     甲辰（青龙 吉） 乙巳（明堂 吉） 丙午（截路 凶） 丁未（朱雀 凶）
    //     戊申（金匮 吉） 己酉（天德 吉） 庚戌（白虎 凶） 辛亥（玉堂 吉）

         --------------------------------------------------------------------------
    //     己丑日
    //     甲子（天寡孤辰 凶） 乙丑（朱雀 凶） 丙寅（金匮福神 吉） 丁卯（天德 吉）
    //     戊辰（白虎 凶） 己巳（天德 吉） 庚午（空亡 凶） 辛未（元武 凶）
    //     壬申（司命 吉） 癸酉（截路 凶） 甲戌（青龙 吉） 乙亥（勾陈 凶）

    //     己卯日
    //     甲子（司命 吉） 乙丑（勾陈 凶） 丙寅（青龙 吉） 丁卯（明堂 吉）
    //     戊辰（天刑 凶） 己巳（朱雀 凶） 庚午（贵人 吉） 辛未（天德 吉）
    //     壬申（截路 凶） 癸酉（玉堂 吉） 甲戌（天牢 凶） 乙亥（元武 凶）

    //     己巳日
    //     甲子（白虎 凶） 乙丑（玉堂 吉） 丙寅（天牢 凶） 丁卯（元武 凶）
    //     戊辰（司命 吉） 己巳（勾陈 凶） 庚午（青龙 吉） 辛未（明堂 吉）
    //     壬申（截路 凶） 癸酉（截路空亡 凶） 甲戌（金匮 吉） 乙亥（天德 吉）

    //     己未日
    //     甲子（天刑 凶） 乙丑（朱雀 凶） 丙寅（金匮 吉） 丁卯（天德 吉）
    //     戊辰（白虎 凶） 己巳（玉堂 吉） 庚午（天牢 凶） 辛未（元武 凶）
    //     壬申（司命 吉） 癸酉（截路 凶） 甲戌（青龙 吉） 乙亥（明堂 吉）

    //     己酉日
    //     甲子（天乙贵人 吉） 乙丑（勾陈 凶） 丙寅（青龙 吉） 丁卯（明堂 吉）
    //     戊辰（天刑 凶） 己巳（朱雀 凶） 庚午（金匮 吉） 辛未（天德 吉）
    //     壬申（截路 凶） 癸酉（司命 吉） 甲戌（天牢 凶） 乙亥（元武 凶）

    //     己亥日
    //     甲子（白虎 凶） 乙丑（玉堂 吉） 丙寅（天牢 凶） 丁卯（朱雀 凶）
    //     戊辰（司命 吉） 己巳（勾陈 凶） 庚午（青龙 吉） 辛未（玉堂 吉）
    //     壬申（截路 凶） 癸酉（截路 凶） 甲戌（金匮 吉） 乙亥（天德 吉）

         --------------------------------------------------------------------------
    //     庚子日
    //     丙子（金匮 吉） 丁丑（天德贵人 吉） 戊寅（白虎 凶） 己卯（玉堂 吉）
    //     庚辰（天牢 凶） 辛巳（元武 凶） 壬午（司命 吉） 癸未（截路 凶）
    //     甲申（青龙 吉） 乙酉（明堂 吉） 丙戌（天刑 凶） 丁亥（朱雀 凶）

    //     庚寅日
    //     丙子（青龙 吉） 丁丑（明堂 吉） 戊寅（天刑 凶） 己卯（朱雀 凶）
    //     庚辰（金匮 吉） 辛巳（天德 吉） 壬午（空亡 凶） 癸未（福德天官 吉）
    //     甲申（天牢 凶） 乙酉（元武 凶） 丙戌（黄道 吉） 丁亥（勾陈 凶）

    //     庚辰日
    //     丙子（天牢 凶） 丁丑（元武 凶） 戊寅（司命金匮 吉） 己卯（勾陈 凶）
    //     庚辰（青龙 吉） 辛巳（明堂 吉） 壬午（天刑 凶） 癸未（朱雀 凶）
    //     甲申（天寡 凶） 乙酉（天德 吉） 丙戌（白虎 凶） 丁亥（玉堂 吉）

    //     庚午日
    //     丙子（金匮 吉） 丁丑（天德 吉） 戊寅（白虎 凶） 己卯（玉堂 吉）
    //     庚辰（天牢 凶） 辛巳（元武 凶） 壬午（司命 吉） 癸未（截路 凶）
    //     甲申（青龙 吉） 乙酉（明堂 吉） 丙戌（天寡天刑 凶） 丁亥（朱雀 凶）

    //     庚申日
    //     丙子（青龙 吉） 丁丑（明堂贵人 吉） 戊寅（天刑 凶） 己卯（朱雀 凶）
    //     庚辰（金匮 吉） 辛巳（天德 吉） 壬午（青龙 吉） 癸未（玉堂金匮 吉）
    //     甲申（天牢 凶） 乙酉（元武 凶） 丙戌（司命 吉） 丁亥（勾陈 凶）

    //     庚戌日
    //     丙子（天牢 凶） 丁丑（元武 凶） 戊寅（司命 吉） 己卯（勾陈 凶）
    //     庚辰（青龙 吉） 辛巳（明堂 吉） 壬午（截路 凶） 癸未（截路 凶）
    //     甲申（金匮 吉） 乙酉（天德 吉） 丙戌（白虎 凶） 丁亥（玉堂 吉）

         --------------------------------------------------------------------------
    //     辛丑日
    //     戊子（天刑天牢 凶） 己丑（朱雀 凶） 庚寅（金匮 吉） 辛卯（天德三合 吉）
    //     壬辰（截路 凶） 癸巳（天官福星 吉） 甲午（黑煞 凶） 乙未（元武天牢 凶）
    //     丙申（司命喜神 吉） 丁酉（勾陈 凶） 戊戌（青龙 吉） 己亥（明堂三合 吉）

    //     辛卯日
    //     戊子（司命 吉） 己丑（勾陈 凶） 庚寅（青龙贵人 吉） 辛卯（明堂 吉）
    //     壬辰（截路空亡 凶） 癸巳（截路 凶） 甲午（金匮 吉） 乙未（天德 吉）
    //     丙申（白虎 凶） 丁酉（玉堂 吉） 戊戌（天牢 凶） 己亥（元武 凶）

    //     辛巳日
    //     戊子（白虎 凶） 己丑（玉堂 吉） 庚寅（天牢 凶） 辛卯（元武 凶）
    //     壬辰（贵人黄道 吉） 癸巳（截路 凶） 甲午（天乙贵人 吉） 乙未（明堂 吉）
    //     丙申（天刑 凶） 丁酉（朱雀 凶） 戊戌（金匮 吉） 己亥（朱雀 凶）

    //     辛未日
    //     戊子（天刑天牢 凶） 己丑（朱雀 凶） 庚寅（金匮 吉） 辛卯（天德三合 吉）
    //     壬辰（截路 凶） 癸巳（天官贵人 吉） 甲午（黑煞 凶） 乙未（元武 凶）
    //     丙申（司命喜神 吉） 丁酉（勾陈 凶） 戊戌（青龙 吉） 己亥（明堂三合 吉）

    //     辛酉日
    //     戊子（司命 吉） 己丑（勾陈 凶） 庚寅（青龙贵人 吉） 辛卯（明堂 吉）
    //     壬辰（截路 凶） 癸巳（截路 凶） 甲午（金匮 吉） 乙未（天德 吉）
    //     丙申（白虎 凶） 丁酉（玉堂 吉） 戊戌（天牢 凶） 己亥（元武 凶）

    //     辛亥日
    //     戊子（白虎 凶） 己丑（玉堂 吉） 庚寅（天牢 凶） 辛卯（元武 凶）
    //     壬辰（司命 吉） 癸巳（勾陈 凶） 甲午（青龙 吉） 乙未（勾陈 凶）
    //     丙申（天刑 凶） 丁酉（朱雀 凶） 戊戌（金匮 吉） 己亥（天德 吉）

         --------------------------------------------------------------------------
    //     壬子日
    //     庚子（金匮 吉） 辛丑（天德 吉） 壬寅（截路 凶） 癸卯（玉堂 吉）
    //     甲辰（天牢 凶） 乙巳（元武 凶） 丙午（司命 吉） 丁未（勾陈 凶）
    //     戊申（青龙 吉） 己酉（明堂 吉） 庚戌（天刑 凶） 辛亥（朱雀 凶）

    //     壬寅日
    //     庚子（青龙 吉） 辛丑（明堂 吉） 壬寅（天刑 凶） 癸卯（截路 凶）
    //     甲辰（金匮 吉） 乙巳（天乙贵人 吉） 丙午（白虎 凶） 丁未（玉堂 吉）
    //     戊申（天牢 凶） 己酉（元武 凶） 庚戌（司命 吉） 辛亥（勾陈 凶）

    //     壬辰日
    //     庚子（天牢 凶） 辛丑（元武 凶） 壬寅（司命 吉） 癸卯（截路 凶）
    //     甲辰（青龙 吉） 乙巳（天乙贵人 吉） 丙午（天刑 凶） 丁未（朱雀 凶）
    //     戊申（金匮 吉） 己酉（天乙贵人 吉） 庚戌（白虎 凶） 辛亥（天德 吉）

    //     壬午日
    //     庚子（金匮 吉） 辛丑（天德 吉） 壬寅（截路 凶） 癸卯（贵人 吉）
    //     甲辰（天牢 凶） 乙巳（截路 凶） 丙午（司命 吉） 丁未（勾陈 凶）
    //     戊申（青龙 吉） 己酉（明堂 吉） 庚戌（天牢 凶） 辛亥（朱雀 凶）

    //     壬申日
    //     庚子（青龙 吉） 辛丑（明堂 吉） 壬寅（天刑 凶） 癸卯（截路 凶）
    //     甲辰（金匮 吉） 乙巳（天乙贵人 吉） 丙午（白虎 凶） 丁未（玉堂 吉）
    //     戊申（天牢 凶） 己酉（元武 凶） 庚戌（司命 吉） 辛亥（勾陈 凶）

    //     壬戌日
    //     庚子（天刑 凶） 辛丑（元武 凶） 壬寅（司命 吉） 癸卯（截路 凶）
    //     甲辰（青龙 吉） 乙巳（明堂 吉） 丙午（天牢 凶） 丁未（朱雀 凶）
    //     戊申（金匮 吉） 己酉（天德 吉） 庚戌（白虎 凶） 辛亥（玉堂 吉）

         --------------------------------------------------------------------------
    //     癸丑日
    //     壬子（截路 凶） 癸丑（截路 凶） 甲寅（金匮 吉） 乙卯（天德 吉）
    //     丙辰（白虎 凶） 丁巳（玉堂 吉） 戊午（天刑天牢 凶） 己未（元武 凶）
    //     庚申（司命 吉） 辛酉（勾陈 凶） 壬戌（青龙 吉） 癸亥（明堂 吉）

    //     癸卯日
    //     壬子（司命 吉） 癸丑（天寡孤辰 凶） 甲寅（青龙 吉） 乙卯（贵人 吉）
    //     丙辰（天刑 凶） 丁巳（朱雀黑道 凶） 戊午（金匮福德 吉） 己未（勾陈 凶）
    //     庚申（白虎 凶） 辛酉（玉堂 吉） 壬戌（天牢 凶） 癸亥（黑道 凶）

    //     癸巳日
    //     壬子（截路 凶） 癸丑（天乙玉堂 吉） 甲寅（天牢 凶） 乙卯（元武 凶）
    //     丙辰（司命 吉） 丁巳（勾陈 凶） 戊午（青龙 吉） 己未（明堂 吉）
    //     庚申（天刑 凶） 辛酉（朱雀 凶） 壬戌（金匮 吉） 癸亥（天德 吉）

    //     癸未日
    //     壬子（截路 凶） 癸丑（截路 凶） 甲寅（金匮 吉） 乙卯（天德 吉）
    //     丙辰（白虎 凶） 丁巳（玉堂 吉） 戊午（天牢 凶） 己未（元武 凶）
    //     庚申（司命 吉） 辛酉（勾陈 凶） 壬戌（青龙 吉） 癸亥（明堂 吉）

    //     癸酉日
    //     壬子（司命 吉） 癸丑（天寡孤辰 凶） 甲寅（青龙 吉） 乙卯（贵人 吉）
    //     丙辰（天刑 凶） 丁巳（朱雀黑道 凶） 戊午（金匮福德 吉） 己未（天德 吉）
    //     庚申（白虎 凶） 辛酉（玉堂 吉） 壬戌（天牢 凶） 癸亥（黑道 凶）

    //     癸亥日
    //     壬子（截路 凶） 癸丑（玉堂 吉） 甲寅（天牢 凶） 乙卯（元武 凶）
    //     丙辰（司命 吉） 丁巳（勾陈 凶） 戊午（青龙 吉） 己未（明堂 吉）
    //     庚申（天刑 凶） 辛酉（朱雀 凶） 壬戌（金匮 吉） 癸亥（天德 吉）
         */
}
