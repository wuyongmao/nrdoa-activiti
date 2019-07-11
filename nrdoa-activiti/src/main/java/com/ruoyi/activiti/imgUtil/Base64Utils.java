package com.ruoyi.activiti.imgUtil;

import java.io.IOException;
import java.io.InputStream;

import sun.misc.BASE64Encoder;

/**
 * Created by user on 2018/7/12.
 */
public class Base64Utils {
    private static BASE64Encoder encoder = new BASE64Encoder();

    public static String ioToBase64(InputStream in) throws IOException {
        String strBase64 = null;
        try {
            byte[] bytes = new byte[in.available()];
            // 将文件中的内容读入到数组中
            in.read(bytes);
            strBase64 = encoder.encode(bytes);      //将字节流数组转换为字符串
            in.close();
        } catch (IOException ioe) {
        }
        return strBase64;
    }
}
