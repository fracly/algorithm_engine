package cn.escheduler.common.utils;

public class StringUtils {
    /**
     * check string empty
     */
    public static boolean isEmpty(String str) {
        if(str == null || str.length() == 0) {
            return true;
        }
        return false;
    }
}
