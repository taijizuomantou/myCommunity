package com.nowcoder.community.util;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.DigestUtils;

import java.util.UUID;

public class CommunityUtil
{

    //生成随机字符串，可用于激活码，上传文件、头像的名字
    public static String generateUUID(){
        return UUID.randomUUID().toString().replaceAll("-","");//去掉其中的-
    }

    //MD5加密
    //只能加密不能解密 hello-> abc123def456 保证每次加密的结果相同。（黑客也会）
    //sult 密码+随机字符串 hello+3e4a8 -> abc123def456abc
    public static String md5(String key){
        if(StringUtils.isBlank(key)){
            return null;
        }
        return DigestUtils.md5DigestAsHex(key.getBytes());//得到16位的md5 的编码
    }

}
