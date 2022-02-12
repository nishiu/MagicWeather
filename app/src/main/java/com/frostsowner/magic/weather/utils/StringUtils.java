package com.frostsowner.magic.weather.utils;

import android.util.Log;

import com.fm.commons.logic.BeanFactory;
import com.frostsowner.magic.weather.logic.SimpleDateManager;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressWarnings("rawtypes")
public class StringUtils {
	public static final String Path_Seperator = "/";
	public static final String Line_Seperator = System.getProperty("line.separator");

	public static final String File_Seperator = System.getProperty("file.separator");

	public static final String File_Encoding = System.getProperty("file.encoding");

	private static char[] chars = new char[]{'a','b','c','d','e','g','g','h','i','j','k','l','m','n','o','p','q','r','s','t'
			,'u','v','w','x','y','z','A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S','T',
			'U','V','W','X','Y','Z','0','1','2','3','4','5','6','7','8','9'};


	public static final boolean isTrue(String pvalue) {
		return "true".equalsIgnoreCase(pvalue) || "1".equalsIgnoreCase(pvalue);
	}

	public static final String formatPhoneNumber(String phone_number) {
		if (isEmpty(phone_number)) {
			return null;
		}
		String tmp = phone_number.trim();
		if (tmp.length() == 11) {
			return tmp;
		}
		if (tmp.startsWith("86")) {
			return tmp.substring(2);
		}
		if (tmp.startsWith("+86")) {
			return tmp.substring(3);
		}
		if (tmp.startsWith("0086")) {
			return tmp.substring(4);
		}
		return phone_number;
	}

	public static final String formatUrl(String prefix, String path) {
		if (path == null)
			return path;
		if (path.startsWith("http://"))
			return path;
		return format(prefix) + format(path);
	}

	public static final int size(String text) {
		if (isEmail(text)) {
			return 0;
		}
		return text.trim().length();
	}

	public static final int size(List list) {
		if (isEmpty(list)) {
			return 0;
		}
		return list.size();
	}

	public static final int size(Set set) {
		if (isEmpty(set)) {
			return 0;
		}
		return set.size();
	}

	public static final int size(Map map) {
		if (isEmpty(map)) {
			return 0;
		}
		return map.size();
	}

	public static final int size(String[] array) {
		if (isEmpty(array)) {
			return 0;
		}
		return array.length;
	}

	public static final int size(int[] array) {
		if (isEmpty(array)) {
			return 0;
		}
		return array.length;
	}

	public static final int size(Object[] array) {
		if (isEmpty(array)) {
			return 0;
		}
		return array.length;
	}

	public static final boolean isContain(String[] array, String value) {
		if ((isEmpty(array)) || (isEmpty(value)))
			return false;
		int size = size(array);
		for (int i = 0; i < size; i++) {
			if (isSame(array[i], value))
				return true;
		}
		return false;
	}

	public static final boolean isContain(String content, String value) {
		if ((isEmpty(content)) || (isEmpty(value)))
			return false;
		return content.indexOf(value) != -1;
	}

	public static final boolean isContain(List list, Object object) {
		if (isEmpty(list)) {
			return false;
		}
		return list.contains(object);
	}

	public static final boolean isNull(Object o) {
		return o == null;
	}

	public static final boolean isEmpty(Object o) {
		return o == null;
	}

	public static final boolean isEmpty(Object[] array) {
		return (array == null) || (array.length == 0);
	}

	public static final boolean isEmpty(String value) {
		return (value == null) || (value.trim().length() == 0 || value.trim().equalsIgnoreCase("NULL"));
	}

	public static final boolean isEmpty(String[] array) {
		return (array == null) || (array.length == 0);
	}

	public static final boolean isEmpty(int[] array) {
		return (array == null) || (array.length == 0);
	}

	public static final boolean isEmpty(StringBuffer sb) {
		return (sb == null) || (sb.length() == 0);
	}

	public static final boolean isEmpty(List list) {
		return (list == null) || (list.size() == 0);
	}

	public static final boolean isEmpty(Set set) {
		return (set == null) || (set.size() == 0);
	}

	public static final boolean isEmpty(Map map) {
		return (map == null) || (map.size() == 0);
	}

	public static final boolean isSame(String value1, String value2) {
		if ((isEmpty(value1)) && (isEmpty(value2)))
			return true;
		if ((!isEmpty(value1)) && (!isEmpty(value2))) {
			return value1.trim().equals(value2.trim());
		}
		return false;
	}

	public static final boolean isShort(String value) {
		if (isEmpty(value))
			return false;
		try {
			Short.parseShort(value);
		} catch (NumberFormatException e) {
			return false;
		}
		return true;
	}

	public static final boolean isInt(String value) {
		if (isEmpty(value))
			return false;
		try {
			Integer.parseInt(value);
		} catch (NumberFormatException e) {
			return false;
		}
		return true;
	}

	public static final boolean isLong(String value) {
		if (isEmpty(value))
			return false;
		try {
			Long.parseLong(value);
		} catch (NumberFormatException e) {
			return false;
		}
		return true;
	}

	public static final boolean isDouble(String value) {
		if (isEmpty(value))
			return false;
		try {
			Double.parseDouble(value);
		} catch (NumberFormatException e) {
			return false;
		}
		return true;
	}

	public static final boolean isChinese(String text){
		return validate("^[a-z0-9A-Z\\u4e00-\\u9fa5]+$",text);
	}

	public static final boolean isNumber(String text) {
		return validate("[0-9]+$", text);
	}

	public static final boolean isPhone(String text){
		return text.length()==11&&isNumber(text);
	}

	public static final boolean isEmail(String text) {
		return validate("[(\\w-)+\\.]+@{1}[(\\w-)+\\.]+[a-z]{2,3}$", text);
	}

	public static final boolean isUrl(String text) {
		return validate("(http://)?[(\\w-)+\\./]+[a-z]{2,3}[/(\\w-\\.)+]*", text);
	}

	public static final boolean validate(String regex, String text) {
		Pattern pattern = Pattern.compile(regex, 2);
		return pattern.matcher(text).matches();
	}

	public static final String stripHtml(String html) {
		String regex = "</?[^>]+>";
		return html.replaceAll(regex, "");
	}

	protected static final String[] stripImageLink(String html) {
		String regex = "</?[^>]+>";
		return Pattern.compile(regex, 2).split(html);
	}

	protected static final String[] stripFlashLink(String html) {
		String regex = "</?[^>]+>";
		return Pattern.compile(regex, 2).split(html);
	}

	public static final boolean isLengthBetween(String text, int min, int max) {
		int length = size(text);
		return (length >= min) && (length <= max);
	}

	public static final String format(String value) {
		return format(value, "");
	}

	public static final String format(int value) {
		return value + "";
	}

	public static final String format(String value, String defaultValue) {
		if (isEmpty(value)) {
			return defaultValue;
		}
		return value.trim();
	}

	public static final String format(String style, int value) {
		return format(style, value, null);
	}

	public static final String format(String style, int value, String defaultValue) {
		if (isEmpty(style)) {
			return defaultValue;
		}
		DecimalFormat df = new DecimalFormat(style);
		return format(df.format(value), defaultValue);
	}

	public static final String format(Date date) {
		return formatDateTimeByStyle("yyyy-MM-dd HH:mm:ss", date, null);
	}

	public static final String formatYMD(Date date) {
		return formatDateTimeByStyle("yyyyMMdd", date, null);
	}

	public static final String formatYM(Date date) {
		return formatDateTimeByStyle("yyyyMM", date, null);
	}

	public static final String format(Date date, Date defaultValue) {
		if (date == null)
			date = defaultValue;
		return formatDateTimeByStyle("yyyy-MM-dd HH:mm:ss", date, null);
	}

	public static final String format(Date date, String defaultValue) {
		return formatDateTimeByStyle("yyyy-MM-dd HH:mm:ss", date, defaultValue);
	}

	public static final String formatDate(Date date) {
		return formatDateTimeByStyle("yyyy-MM-dd", date, null);
	}

	public static final String formatDate(Date date, Date defaultValue) {
		if (date == null)
			date = defaultValue;
		return formatDateTimeByStyle("yyyy-MM-dd", date, null);
	}

	public static final String formatDate(Date date, String defaultValue) {
		return formatDateTimeByStyle("yyyy-MM-dd", date, defaultValue);
	}

	public static final String formatTime(Date date) {
		return formatDateTimeByStyle("HH:mm:ss", date, null);
	}

	public static final String formatTime(Date date, Date defaultValue) {
		if (date == null)
			date = defaultValue;
		return formatDateTimeByStyle("HH:mm:ss", date, null);
	}

	public static final String formatTime(Date date, String defaultValue) {
		return formatDateTimeByStyle("HH:mm:ss", date, defaultValue);
	}

	public static final String formatDateTime(Date date) {
		return formatDateTimeByStyle("yyyy-MM-dd HH:mm:ss", date, null);
	}

	public static final String formatDateTime(Date date, Date defaultValue) {
		if (date == null)
			date = defaultValue;
		return formatDateTimeByStyle("yyyy-MM-dd HH:mm:ss", date, null);
	}

	public static final String formatDateTime(Date date, String defaultValue) {
		return formatDateTimeByStyle("yyyy-MM-dd HH:mm:ss", date, defaultValue);
	}

	public static final String formatDateTime(String style, Date date) {
		return formatDateTimeByStyle(style, date, null);
	}

	public static final String formatDateTime(String style, Date date, Date defaultValue) {
		if (date == null)
			date = defaultValue;
		return formatDateTimeByStyle(style, date, null);
	}

	public static final String formatDateTime(String style, Date date, String defaultValue) {
		return formatDateTimeByStyle(style, date, defaultValue);
	}

	private static final String formatDateTimeByStyle(String style, Date date, String defaultValue) {
		if ((isEmpty(style)) || (isEmpty(date))) {
			return defaultValue;
		}
		SimpleDateFormat sdf = new SimpleDateFormat(style);
		return format(sdf.format(date), defaultValue);
	}

	public static final String formatTransformStyle(String dateString,SimpleDateFormat oStyle,SimpleDateFormat tStyle){
	    String content = "";
	    try {
            Date date = oStyle.parse(dateString);
            content = tStyle.format(date);
        }catch (ParseException e){
	        e.printStackTrace();
        }
	    return content;
    }

    public static final boolean isNight(String timeString){
	    try {
            SimpleDateManager simpleDateManager = BeanFactory.getBean(SimpleDateManager.class);
			SimpleDateFormat dateFormat = simpleDateManager.getFormatMap("HH:mm:ss");
            timeString = timeString.substring(11,timeString.length());
//            Log.d("ijimu","update time : "+timeString+" , length : "+timeString.length());
            Date nightDate = dateFormat.parse("18:00:00");
            Date nightDate2 = dateFormat.parse("6:00:00");
	        Date targetTime = dateFormat.parse(timeString);
//	        Log.d("ijimu","是否是晚上 : "+(targetTime.getTime() < nightDate2.getTime() || targetTime.getTime() > nightDate.getTime()));
	        return (targetTime.getTime() < nightDate2.getTime() || targetTime.getTime() > nightDate.getTime());
        }catch (ParseException e){
	        e.printStackTrace();
        }
	    return false;
    }

	public static final boolean isNight(long time){
		try {
			if(time == 0)time = System.currentTimeMillis();
			SimpleDateManager simpleDateManager = BeanFactory.getBean(SimpleDateManager.class);
			SimpleDateFormat dateFormat = simpleDateManager.getFormatMap("HH:mm:ss");
			String timeString = simpleDateManager.transformTime(time,"HH:mm:ss");
//            Log.d("ijimu","update time : "+timeString+" , length : "+timeString.length());
			Date nightDate = dateFormat.parse("18:00:00");
			Date nightDate2 = dateFormat.parse("6:00:00");
			Date targetTime = dateFormat.parse(timeString);
//	        Log.d("ijimu","是否是晚上 : "+(targetTime.getTime() < nightDate2.getTime() || targetTime.getTime() > nightDate.getTime()));
			return (targetTime.getTime() < nightDate2.getTime() || targetTime.getTime() > nightDate.getTime());
		}catch (ParseException e){
			e.printStackTrace();
		}
		return false;
	}

    /**
     * 根据当前日期获得是星期几
     * time=yyyy-MM-dd
     * @return
     */
    public static String getWeek(String time,SimpleDateFormat format) {
        String Week = "";
        if(format == null)format = new SimpleDateFormat("yyyy-MM-dd");
        Calendar c = Calendar.getInstance();
        try {
            c.setTime(format.parse(time));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        int wek=c.get(Calendar.DAY_OF_WEEK);

        if (wek == 1) {
            Week += "星期日";
        }
        if (wek == 2) {
            Week += "星期一";
        }
        if (wek == 3) {
            Week += "星期二";
        }
        if (wek == 4) {
            Week += "星期三";
        }
        if (wek == 5) {
            Week += "星期四";
        }
        if (wek == 6) {
            Week += "星期五";
        }
        if (wek == 7) {
            Week += "星期六";
        }
        return Week;
    }


    /**
     * 根据当前日期获得是星期几
     * time=yyyy-MM-dd
     * @return
     */
    public static String getWeek(Date date) {
        String Week = "";
        Calendar c = Calendar.getInstance();
        if(date == null)date = new Date(System.currentTimeMillis());
        c.setTime(date);

        int wek=c.get(Calendar.DAY_OF_WEEK);

        if (wek == 1) {
            Week += "星期日";
        }
        if (wek == 2) {
            Week += "星期一";
        }
        if (wek == 3) {
            Week += "星期二";
        }
        if (wek == 4) {
            Week += "星期三";
        }
        if (wek == 5) {
            Week += "星期四";
        }
        if (wek == 6) {
            Week += "星期五";
        }
        if (wek == 7) {
            Week += "星期六";
        }
        return Week;
    }

	public static String join(String[] keys, Object[] values) {
		StringBuffer builder = new StringBuffer();
		for (int i = 0; i < keys.length; i++) {
			if (i == 0)
				builder.append(keys[i] + "=" + values[i]);
			else
				builder.append("&" + keys[i] + "=" + values[i]);
		}
		return builder.toString();
	}

	public static final String join(int[] array, String joiner) {
		if (isEmpty(array))
			return "";
		StringBuffer sb = new StringBuffer();
		sb.append(array[0]);
		for (int i = 1; i < array.length; i++) {
			sb.append(joiner);
			sb.append(array[i]);
		}

		return sb.toString();
	}

	public static final String[] split(String content, String splitor) {
		if ((isEmpty(content)) || (isEmpty(splitor))) {
			return new String[0];
		}
		return content.split(splitor);
	}

	public static final String[] ListToArray(List list) {
		return ListToArray(list, false);
	}

	public static final String[] ListToArray(List list, boolean filterEmpty) {
		String[] array = new String[size(list)];
		for (int i = 0; i < array.length; i++) {
			if (filterEmpty) {
				if (isEmpty(list.get(i)))
					;
				array[i] = ((String) list.get(i));
			} else {
				array[i] = ((String) list.get(i));
			}
		}
		return array;
	}

	public static final List<String> ArrayToList(String[] array) {
		return ArrayToList(array, false);
	}

	public static final List<String> ArrayToList(String[] array, boolean filterEmpty) {
		List<String> list = new Vector<String>();
		for (int i = 0; i < size(array); i++) {
			if (filterEmpty) {
				if (!isEmpty(array[i]))
					list.add(array[i]);
			} else
				list.add(array[i]);
		}

		return list;
	}

	public static List<Integer> stringToIntList(String source, String delimit) {
		if (null != source) {
			List<Integer> list = new ArrayList<Integer>();
			for (String s : source.split(delimit)) {
				if (!com.frostsowner.magic.weather.utils.StringUtils.isEmpty(s)) {
					list.add(Integer.valueOf(s.trim()));
				}
			}
			return list;
		}
		return null;
	}

	public static Map<String, String> stringToMap(String source, String keyDelimit, String entryDelimit) {
		if (com.frostsowner.magic.weather.utils.StringUtils.isEmpty(source))
			return null;
		Map<String, String> map = new HashMap<String, String>();
		for (String s : source.split(entryDelimit)) {
			String[] sc = s.split(keyDelimit);
			map.put(sc[0], sc.length < 2 ? "" : sc[1]);
		}
		return map;
	}

	public static String mapToString(Map<?, ?> map, String keyDelimit, String entryDelimit) {
		if (null != map) {
			StringBuilder builder = new StringBuilder();
			for (Map.Entry<?, ?> entry : map.entrySet()) {
				builder.append(entry.getKey() + keyDelimit + entry.getValue() + entryDelimit);
			}
			return builder.toString();
		}
		return null;
	}

	public static final String getRandomString(int length) {
		String[] s = { "1", "2", "3", "4", "5", "6", "7", "8", "9", "a", "b", "c", "d", "e", "f", "g", "h", "i", "j",
				"k", "m", "n", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z", "A", "B", "D", "E", "F", "G", "H",
				"J", "L", "M", "N", "Q", "R", "T", "Y" };
		return getRandomgString(length, s);
	}

	private static String getRandomgString(int length, String[] s) {
		if (length < 1)
			return "";
		StringBuffer sb = new StringBuffer(length);
		for (int i = 0; i < length; i++) {
			int position = getRandomNumber(0,s.length - 1);
			Collections.shuffle(Arrays.asList(s));
			sb.append(s[position]);
		}
		return sb.toString();
	}

	public static final int getRandomNumber(int min, int max) {
		Random random = new Random();
		return random.nextInt(max + 1 - min) + min;
	}
	
	public static final String getRandomIntString(int length) {
		String[] s = { "1", "2", "3", "4", "5", "6", "7", "8", "9" , "0"};
		return getRandomgString(length, s);
	}

	public static final String alert(String message, String action) {
		StringBuffer sb = new StringBuffer();
		if (isEmpty(message)) {
			sb.append("<script>");
		} else {
			message = message.replace("'", "‘");
			message = message.replace("\"", "“");
			sb.append("<script>alert('系统提示：\\n\\n" + message + "\\t\\n');");
		}
		if ("close".equalsIgnoreCase(action))
			sb.append("window.close();");
		else if ("back".equalsIgnoreCase(action))
			sb.append("history.go(-1);");
		else
			sb.append("document.location='" + action + "'");
		sb.append("</script>");
		return sb.toString();
	}

	public static final String confirm(String message, String action1, String action2) {
		StringBuffer sb = new StringBuffer();
		if (isEmpty(message)) {
			sb.append("<script>");
		} else {
			message = message.replace("'", "‘");
			message = message.replace("\"", "“");
			sb.append("<script>var result=confirm('系统提示：\\n\\n" + message + "\\t\\n');");
		}
		sb.append("if(result){");
		if ("close".equalsIgnoreCase(action1))
			sb.append("window.close();");
		else if ("back".equalsIgnoreCase(action1))
			sb.append("history.go(-1);");
		else
			sb.append("document.location='" + action1 + "'");
		sb.append("}else{");
		if ("close".equalsIgnoreCase(action2))
			sb.append("window.close();");
		else if ("back".equalsIgnoreCase(action2))
			sb.append("history.go(-1);");
		else
			sb.append("document.location='" + action2 + "'");
		sb.append("}");
		sb.append("</script>");
		return sb.toString();
	}

	public static final String noData(int columnCount, String message) {
		StringBuffer sb = new StringBuffer();
		if (columnCount > 0) {
			sb.append("<tr bgcolor=\"#FFFFFF\">");
			if (columnCount == 1)
				sb.append("<td align=\"center\">");
			else
				sb.append("<td align=\"center\" colspan=\"" + columnCount + "\">");
		} else {
			sb.append("<center>");
		}
		sb.append("<br>");
		if (isEmpty(message))
			message = "尚无任何数据！";
		sb.append(message);
		sb.append("<br><br>");
		if (columnCount > 0)
			sb.append("</td></tr>");
		else
			sb.append("</center>");
		return sb.toString();
	}

	public static final String encodeSql(String sql) {
		if (isEmpty(sql)) {
			return "";
		}
		sql = sql.replace("%", "%%");
		return sql;
	}

	public static final String encodeHtml(String text) {
		if (isEmpty(text)) {
			return "";
		}
		text = text.replaceAll("&", "&amp;");
		text = text.replaceAll("<", "&lt;");
		text = text.replaceAll(">", "&gt;");
		text = text.replaceAll("\"", "&quot;");
		text = text.replaceAll("'", "&apos;");
		text = text.replaceAll(Line_Seperator, "<br>");
		return text;
	}

	public static final String decodeHtml(String html) {
		if (isEmpty(html)) {
			return "";
		}
		html = html.replaceAll("&amp;", "&");
		html = html.replaceAll("&lt;", "<");
		html = html.replaceAll("&gt;", ">");
		html = html.replaceAll("&quot;", "\"");
		html = html.replaceAll("&apos;", "'");
		html = html.replaceAll("<br>", Line_Seperator);
		return html;
	}

	public static final String encodeUrl(String url) {
		return encodeUrl(url, File_Encoding);
	}

	public static final String encodeUrl(String url, String charset) {
		if (isEmpty(url))
			return "";
		try {
			url = URLEncoder.encode(url, charset);
		} catch (UnsupportedEncodingException localUnsupportedEncodingException) {
		}
		return url;
	}

	public static final String decodeUrl(String url) {
		return decodeUrl(url, File_Encoding);
	}

	public static final String decodeUrl(String url, String charset) {
		if (isEmpty(url))
			return "";
		try {
			url = URLDecoder.decode(url, charset);
		} catch (UnsupportedEncodingException localUnsupportedEncodingException) {
		}
		return url;
	}

	public static final String generateOption(String value, String text) {
		return generateOption(value, text, false);
	}

	public static final String generateOption(String value, String text, boolean selected) {
		StringBuffer sb = new StringBuffer();
		sb.append("<option value=\"" + value + "\"");
		if (selected)
			sb.append(" selected");
		sb.append(">");
		sb.append(text);
		sb.append("</option>");
		return sb.toString();
	}

	public static final String middle(String text, int begin, int length) {
		if ((isEmpty(text)) || (begin < 0) || (length < 0)) {
			return "";
		}
		int end = Math.min(begin + length, text.length());
		return text.substring(begin, end);
	}

	public static final String middle(String text, int begin) {
		if ((isEmpty(text)) || (begin < 0)) {
			return "";
		}
		return text.substring(begin);
	}

	public static final String left(String text, int length) {
		if ((isEmpty(text)) || (length < 1))
			return "";
		if (length > text.length()) {
			return text;
		}
		return text.substring(0, length);
	}

	public static final String right(String text, int length) {
		if ((isEmpty(text)) || (length < 1))
			return "";
		if (length > text.length()) {
			return text;
		}
		return text.substring(text.length() - length);
	}

	public static String capitalize(String text) {
		if ((text == null) || (text.length() == 0)) {
			return text;
		}
		return Character.toUpperCase(text.charAt(0)) + text.substring(1);
	}

	public static String lowercaseFirstChar(String text) {
		if ((text == null) || (text.length() == 0)) {
			return text;
		}
		return Character.toLowerCase(text.charAt(0)) + text.substring(1);
	}

	public static String join(Collection<?> elements, String delimiter) {
		return join(elements, delimiter, true);
	}

	public static String join(Object[] elements, String delimiter) {
		return join(elements, delimiter, true);
	}

	public static String join(Collection<?> elements, String delimiter, boolean appendTail) {
		return join(elements, delimiter, null, appendTail);
	}

	public static <T> String join(Collection<T> elements, String delimiter, Formatter<T> formatter,
			boolean appendTail) {
		if ((elements == null) || (elements.isEmpty())) {
			return "";
		}

		StringBuilder buffer = new StringBuilder();
		int i = 0;
		for (T a : elements) {
			if(i == 0){				
				buffer.append(formatter != null ? formatter.toString(a) : a);
			}else{				
				buffer.append(delimiter).append(formatter != null ? formatter.toString(a) : a);
			}
			i++;
		}
		if (!appendTail) {
			buffer.setLength(buffer.length() - delimiter.length());
		}
		return buffer.toString();
	}

	public static String join(Object[] elements, String delimiter, boolean appendTail) {
		return join(Arrays.asList(elements), delimiter, appendTail);
	}
	

	public static boolean IsTelephone(String str_telephone)
    {
        return str_telephone.matches("^(0|86|17951)?(13[0-9]|15[012356789]|17[0678]|18[0-9]|14[57])[0-9]{8}$");
    }

	public static abstract interface Formatter<T> {
		public abstract String toString(T paramT);
	}
	
	// 判断一个字符串是否含有数字
	public static boolean HasDigit(String content) {
	    boolean flag = false;
	    Pattern p = Pattern.compile(".*\\d+.*");
	    Matcher m = p.matcher(content);
	    if (m.matches()) {
	        flag = true;
	    }
	    return flag;
	}

	public static String random(int radius){
		StringBuilder builder = new StringBuilder();
		for(int i=0;i<radius;i++){
			builder.append(chars[new Random().nextInt(chars.length)]);
		}
		return builder.toString();
	}

    /**
     * 获取字符串的MD5编码
     *
     * @param source
     * @return
     */
    public static String getMD5(String source) {
        //定义一个字节数组
        byte[] secretBytes = null;
        try {
            // 生成一个MD5加密计算摘要
            MessageDigest md = MessageDigest.getInstance("MD5");
            //对字符串进行加密
            md.update(source.getBytes());
            //获得加密后的数据
            secretBytes = md.digest();
        } catch (NoSuchAlgorithmException e) {
            Log.e("ijimu", "getMD5 (NoSuchAlgorithmException)", e);
        }
        //将加密后的数据转换为16进制数字
        StringBuilder md5code = new StringBuilder(new BigInteger(1, secretBytes).toString(16));// 16进制数字
        // 如果生成数字未满32位，需要前面补0
        for (int i = 0; i < 32 - md5code.length(); i++) {
            md5code.insert(0, "0");
        }
        return md5code.toString();
    }
}