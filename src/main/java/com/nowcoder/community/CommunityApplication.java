package com.nowcoder.community;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;

@SpringBootApplication

public class CommunityApplication {//注解@SpringBootAppli所标识的类标识是个配置类，往往程序入口使用
	//会扫描配置类所在的包以及子包下的文件，只扫描@controller component service repository这些注解的bean
	//都是基于component 。service 业务，处理请求controller，数据库访问repository，任何地方都能用component
	//是最先被加载的bean

	@PostConstruct
	public void init(){
		//解决netty启动冲突的问题
		//从Netty4Utils中找到的setAvailableProcessors
		System.setProperty("es.set.netty.runtime.available.processors","false");

	}

	public static void main(String[] args) {
		SpringApplication.run(CommunityApplication.class, args);//自动创建容器
	}

}
