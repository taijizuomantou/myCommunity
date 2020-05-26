package com.nowcoder.community;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.sql.SQLOutput;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)//设置与main包中相同的配置类
public class KafkaTests {

    @Autowired
    private KafkaProducer kafkaProducer;


    @Test
    public void testKafka(){

        kafkaProducer.sendMessage("test","在一起");
        kafkaProducer.sendMessage("test","不能浮现");
        try {
            Thread.sleep(1000 * 10); // 10 秒钟
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}

//主动
@Component
class KafkaProducer{

    @Autowired
    private KafkaTemplate kafkaTemplate;

    public void sendMessage(String topic, String content){
        kafkaTemplate.send(topic, content);
    }

}

//被动。存在延时
@Component
class KafkaConsumer{

    @KafkaListener(topics={"test"})
    public void handlerMessage(ConsumerRecord record){
        System.out.println(record.value());
    }

}