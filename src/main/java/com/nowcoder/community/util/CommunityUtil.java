package com.nowcoder.community.util;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.DigestUtils;

import java.util.HashMap;
import java.util.Map;
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

    //fast json 分别是编号提示和业务数据
    public static String getJSONString(int code, String msg, Map<String,Object> map){
        JSONObject json = new JSONObject();
        json.put("code",code);
        json.put("msg",msg);
        if(map != null){
            for(String key: map.keySet()){
                json.put(key,map.get(key));
            }
        }
        return json.toJSONString();
    }

    //重载
    public static String getJSONString(int code, String msg){
        return getJSONString(code,msg,null);
    }
    public static String getJSONString(int code){
        return getJSONString(code,null,null);
    }

    //由于不需要容器来管理可以在这里直接使用main方法来测试
    public static void main(String[] args) {
        Map<String,Object> map = new HashMap<>();
        map.put("name","zhangsan");
        map.put("age",25);
        System.out.println(getJSONString(0,"ok",map));
    }

}
