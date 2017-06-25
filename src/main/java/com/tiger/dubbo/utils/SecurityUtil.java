package com.tiger.dubbo.utils;


import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 加/解密工具类,
 *
 */
public class SecurityUtil {

    /**
     * md5加密方法
     * @param str
     * @return
     */
    public static String md5(String str) {

        try {
            // 得到一个信息摘要器
            MessageDigest digest = MessageDigest.getInstance("md5");
            byte[] result = digest.digest(str.getBytes());
            StringBuffer buffer = new StringBuffer();
            // 把没一个byte 做一个与运算 0xff;
            for (byte b : result) {
                // 与运算
                int number = b & 0xff;// 加盐
                String s = Integer.toHexString(number);
                if (s.length() == 1) {
                    buffer.append("0");
                }
                buffer.append(s);
            }

            // 标准的md5加密后的结果
            return buffer.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return "";
        }

    }
}
