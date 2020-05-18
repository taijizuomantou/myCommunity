package com.nowcoder.community.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.text.SimpleDateFormat;

//使用第三方的Bean
@Configuration
//表示为配置类
public class AlphaConfig {
    @Bean
    public SimpleDateFormat simpleDateFormat(){//方法名就是bean的名字
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    }
}
