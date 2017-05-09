package cn.edu.cuit.wsy.travelnote.utils;


import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by jianglei on 2016/12/1.
 */

public class StringUtil {
    public static String playMosaic(String src, int start, int num) {
        if (start <= 0 || start + num > src.length())
            throw new IllegalArgumentException("range is not in " + src + "\'s length");
        char[] chars = new char[num];
        for (int i = 0; i < num; i++) {
            chars[i] = '*';
        }
        String mosaic = new String(chars);
        return src.replace(src.substring(start, start + num), mosaic);
    }
    public static String getFormatDate(long time) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日 HH时mm分");
        Date date = new Date(time);
        return format.format(date);
    }
    public static String getShortDate(long time) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date(time);
        return format.format(date);
    }
    public static final long HOUR24=24*60*60*1000;
    public static final long HOUR48=48*60*60*1000;
    public static final long HOUR1=60*60*1000;
    public static String getTimeString(long time){
        long now=System.currentTimeMillis();
        Date date=new Date(now);
        long len=now-time;
        if (len>=HOUR48){
            return getShortDate(time);
        }else if (len>=HOUR24){
            return "1天前";
        }else if (len>HOUR1){
            int hour= (int) (len/HOUR1);
            return hour+"小时前";
        }else {
            return "刚刚";
        }
    }
}
