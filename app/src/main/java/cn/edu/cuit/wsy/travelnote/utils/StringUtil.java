package cn.edu.cuit.wsy.travelnote.utils;


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
}
