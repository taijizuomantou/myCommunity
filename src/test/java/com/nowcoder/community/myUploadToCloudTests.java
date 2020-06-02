package com.nowcoder.community;

import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.model.PutObjectRequest;
import com.qcloud.cos.model.PutObjectResult;
import com.qcloud.cos.region.Region;
import org.junit.Test;

import java.io.File;


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


}
