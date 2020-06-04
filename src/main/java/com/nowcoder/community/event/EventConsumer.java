package com.nowcoder.community.event;

import com.alibaba.fastjson.JSONObject;
import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.entity.Event;
import com.nowcoder.community.entity.Message;
import com.nowcoder.community.service.DiscussPostService;
import com.nowcoder.community.service.ElasticsearchService;
import com.nowcoder.community.service.MessageService;
import com.nowcoder.community.util.CommunityConstant;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.exception.CosClientException;
import com.qcloud.cos.exception.CosServiceException;
import com.qcloud.cos.model.PutObjectRequest;
import com.qcloud.cos.model.PutObjectResult;
import com.qcloud.cos.region.Region;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.connection.FutureResult;
import org.springframework.data.redis.connection.ReactiveNumberCommands;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Future;

@Component
public class EventConsumer implements CommunityConstant {

    private static final Logger logger = LoggerFactory.getLogger(EventConsumer.class);

    @Autowired
    private MessageService messageService;

    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private ElasticsearchService elasticsearchService;

    @Value("${wk.image.storage}")
    private String wkImageStorage;

    @Value("${wk.image.cmd}")
    private String wkImageCommand;

    @Value("${tencent.key.access}")
    private String accessKey;

    @Value("${tencent.key.secret}")
    private String secretKey;

    @Value("${tencent.bucket.share.name}")
    private String shareBucketName;

    @Value("${tencent.bucket.share.region}")
    private String shareRegion;

    @Autowired
    private ThreadPoolTaskScheduler taskScheduler;


    @KafkaListener(topics = {TOPIC_COMMENT, TOPIC_LIKE, TOPIC_FOLLOW})
    public void handleCommentMessage(ConsumerRecord record) {
        if (record == null || record.value() == null) {
            logger.error("消息的内容为空!");
            return;
        }

        Event event = JSONObject.parseObject(record.value().toString(), Event.class);
        if (event == null) {
            logger.error("消息格式错误!");
            return;
        }

        // 发送站内通知
        Message message = new Message();
        message.setFromId(SYSTEM_USER_ID);
        message.setToId(event.getEntityUserId());
        message.setConversationId(event.getTopic());
        message.setCreateTime(new Date());

        Map<String, Object> content = new HashMap<>();
        content.put("userId", event.getUserId());
        content.put("entityType", event.getEntityType());
        content.put("entityId", event.getEntityId());

        if (!event.getData().isEmpty()) {
            for (Map.Entry<String, Object> entry : event.getData().entrySet()) {
                content.put(entry.getKey(), entry.getValue());
            }
        }

        message.setContent(JSONObject.toJSONString(content));
        messageService.addMessage(message);
    }

    //消费发帖事件
    @KafkaListener(topics={TOPIC_PUBLISH})
    public void handlePublishMessage(ConsumerRecord record){
        if (record == null || record.value() == null) {
            logger.error("消息的内容为空!");
            return;
        }

        Event event = JSONObject.parseObject(record.value().toString(), Event.class);
        if (event == null) {
            logger.error("消息格式错误!");
            return;
        }

        DiscussPost discussPost = discussPostService.findDisccusPostById(event.getEntityId());
        elasticsearchService.saveDiscussPost(discussPost);
    }

    //消费删帖事件
    @KafkaListener(topics={TOPIC_DELETE})
    public void handleDeleteMessage(ConsumerRecord record){
        if (record == null || record.value() == null) {
            logger.error("消息的内容为空!");
            return;
        }

        Event event = JSONObject.parseObject(record.value().toString(), Event.class);
        if (event == null) {
            logger.error("消息格式错误!");
            return;
        }

        elasticsearchService.deleteDiscussPost(event.getEntityId());
    }

    //消费分享事件
    @KafkaListener(topics=TOPIC_SHARE)
    public void handleShareMessage(ConsumerRecord record){
        if (record == null || record.value() == null) {
            logger.error("消息的内容为空!");
            return;
        }

        Event event = JSONObject.parseObject(record.value().toString(), Event.class);
        if (event == null) {
            logger.error("消息格式错误!");
            return;
        }

        String htmlUrl = (String) event.getData().get("htmlUrl");
        String fileName = (String) event.getData().get("fileName");
        String suffix = (String) event.getData().get("suffix");

        String cmd = wkImageCommand + " --quality 75 "
                + htmlUrl + " " + wkImageStorage +"/"+fileName + suffix;
        try {
            Runtime.getRuntime().exec(cmd);
            logger.info("生成长图成功" + cmd);//该命令会先执行完
        } catch (IOException e) {
            logger.error("生成长图失败" + e.getMessage());
        }

        //每隔一段时间看一下图片生成了吗。如果超时则取消上传
        //只有一个服务器会运行到这里，因此不受分布式的限制
        //只有一个服务器能抢到消息
        //启动定时器监视该图片一旦图片生成就上传至腾讯云
        UploadTask task = new UploadTask(fileName, suffix);
        //需要在run方法之内停止定时器。启动定时器时有返回值
        Future future = taskScheduler.scheduleAtFixedRate(task, 500);
        task.setFuture(future);
    }
    class UploadTask implements  Runnable{

        //文件名称
        private String fileName;
        //文件后缀
        private String suffix;
        //启动任务的返回值用来停止定时器
        private Future future;
        //图片未生成，客户端网络，服务器网络，要保证定时器总会停掉
        //开始时间
        private long startTime;
        //上传次数
        private int uploadTimes;


        public UploadTask(String fileName, String suffix) {
            this.fileName = fileName;
            this.suffix = suffix;
            this.startTime = System.currentTimeMillis();
        }

        public void setFuture(Future future) {
            this.future = future;
        }


        @Override
        public void run() {
            //生成失败
            if(System.currentTimeMillis() - startTime > 30000){
                logger.error("生成分享图片，执行时间过长，终止任务"+fileName);
                future.cancel(true);
                return;
            }
            //上传失败
            if(uploadTimes >= 3){
                logger.error("上传次数过多，终止任务"+fileName);
                future.cancel(true);
                return;
            }
            //本地存放照片的路径
            String path = wkImageStorage + "/" + fileName + suffix;
            File file = new File(path);
            //文件不存在则什么都不做
            if(file.exists()){
                logger.info(String.format("开始第%d次上传[%s]",++uploadTimes, fileName));
                //生成上传凭证
                COSCredentials cred = new BasicCOSCredentials(accessKey, secretKey);
                Region region = new Region(shareRegion);
                ClientConfig clientConfig = new ClientConfig(region);
                COSClient cosClient = new COSClient(cred, clientConfig);
                PutObjectRequest putObjectRequest = new PutObjectRequest(shareBucketName, fileName, file);
                try{
                    PutObjectResult putObjectResult = cosClient.putObject(putObjectRequest);
                    logger.info(String.format("第%d次上传成功[%s]",uploadTimes,fileName));
                    future.cancel(true);
                    //处理响应结果
                }catch (CosClientException e){
                    logger.info(String.format("第%d次上传失败[%s].",uploadTimes, fileName));
                }
            }
            else{
                logger.info("等待图片生成[" +fileName+"]." );
            }
        }
    }


}
