package com.nowcoder.community;

import java.io.IOException;

public class WkTests {

    public static void main(String[] args) {
        String cmd = "C:/java_work/wkhtmltopdf/bin/wkhtmltoimage  --quality 75 https://www.nowcoder.com C:/java_work/data/wk-image/3.png";
        try {
            Runtime.getRuntime().exec(cmd);//不等待操作系统的反馈和主程序是异步的
            System.out.println("ok");//先看到ok等一会儿才会生成图片
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
