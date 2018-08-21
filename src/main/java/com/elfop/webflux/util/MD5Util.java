package com.elfop.webflux.util;

import lombok.extern.slf4j.Slf4j;

import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @Description:
 * @version: V1.0
 * @author: liu zhenming
 * @Email: 1119264845@qq.com
 * @Date: 2018-08-10 10:01
 */
@Slf4j
public class MD5Util {

    /**
     * 加密
     *
     * @param temp 需要加密的字符
     * @return 加密后的字符
     */
    public static String encrypt(String temp) {
        try {
            // 得到一个信息摘要器
            MessageDigest digests = MessageDigest.getInstance("md5");
            byte[] result = digests.digest(temp.getBytes(Charset.forName("UTF-8")));
            StringBuilder buffer = new StringBuilder();
            // 把每一个byte 做一个与运算 0xff;
            for (byte b : result) {
                // 加盐
                // 与运算
                int number = b & 0xff;
                String str = Integer.toHexString(number);
                if (str.length() == 1) {
                    buffer.append("0");
                }
                buffer.append(str);
            }
            // 标准的md5加密后的结果
            return buffer.toString();
        } catch (NoSuchAlgorithmException e) {
            log.error(e.getMessage(), e);
            return "";
        }
    }

}
