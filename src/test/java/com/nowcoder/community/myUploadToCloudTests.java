package com.nowcoder.community;

import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.model.PutObjectRequest;
import com.qcloud.cos.model.PutObjectResult;
import com.qcloud.cos.region.Region;
import com.tencent.cloud.CosStsClient;
import org.json.JSONObject;
import org.junit.Test;

import java.io.File;
import java.util.TreeMap;


public class myUploadToCloudTests {
    @Test
    public static void main(String[] args) {
        // 1 初始化用户身份信息（secretId, secretKey）。
        String secretId = "AKIDf7KUTDL6i41i14aPjpgZCbkjCOM3GaQv";
        String secretKey = "LQGrVq5Awb0A6qJhwyvnZ8RULxJCrGSs";
        COSCredentials cred = new BasicCOSCredentials(secretId, secretKey);
// 2 设置 bucket 的区域, COS 地域的简称请参照 https://cloud.tencent.com/document/product/436/6224
// clientConfig 中包含了设置 region, https(默认 http), 超时, 代理等 set 方法, 使用可参见源码或者常见问题 Java SDK 部分。
        Region region = new Region("ap-nanjing");
        ClientConfig clientConfig = new ClientConfig(region);
// 3 生成 cos 客户端。
        COSClient cosClient = new COSClient(cred, clientConfig);
        File localFile = new File("C:/java_work/data/wk-image/0f5c87093ee34415a0cb58b639b2ca76.png");
// 指定要上传到的存储桶
        String bucketName = "community-header-1252397182";
// 指定要上传到 COS 上对象键
        String key = "image0.png";
        PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, key, localFile);
        PutObjectResult putObjectResult = cosClient.putObject(putObjectRequest);
    }


//    public static void main(String[] args) {
//        JSONObject resultJson = new JSONObject();
//        TreeMap<String, Object> config = new TreeMap<String, Object>();
//
//        try {
//            // 替换为您的 SecretId
//            config.put("SecretId", "AKIDf7KUTDL6i41i14aPjpgZCbkjCOM3GaQv");
//            // 替换为您的 SecretKey
//            config.put("SecretKey", "LQGrVq5Awb0A6qJhwyvnZ8RULxJCrGSs");
//
//            // 临时密钥有效时长，单位是秒，默认1800秒，最长可设定有效期为7200秒
//            config.put("durationSeconds", 1800);
//
//            // 换成您的 bucket
//            config.put("bucket", "community-header-1252397182");
//            // 换成 bucket 所在地区
//            config.put("region", "ap-nanjing");
//
//            // 这里改成允许的路径前缀，可以根据自己网站的用户登录态判断允许上传的具体路径，例子：a.jpg 或者 a/* 或者 * 。
//            // 如果填写了“*”，将允许用户访问所有资源；除非业务需要，否则请按照最小权限原则授予用户相应的访问权限范围。
//            config.put("allowPrefix", "*");
//
//            // 密钥的权限列表。简单上传、表单上传和分片上传需要以下的权限，其他权限列表请看 https://cloud.tencent.com/document/product/436/31923
//            String[] allowActions = new String[] {
//                    // 简单上传
//                    "name/cos:PutObject",
//                    // 表单上传、小程序上传
//                    "name/cos:PostObject",
//            };
//            config.put("allowActions", allowActions);
//
//            JSONObject credential = CosStsClient.getCredential(config);
//            //成功返回临时密钥信息，如下打印密钥信息
//            System.out.println(credential);
//        } catch (Exception e) {
//            //失败抛出异常
//            e.printStackTrace();
//            throw new IllegalArgumentException("no valid secret !");
//        }
//    }
}
