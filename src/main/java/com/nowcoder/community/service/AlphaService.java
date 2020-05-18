package com.nowcoder.community.service;

import com.nowcoder.community.dao.AlphaDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@Service
//@Scope("prototype")
//prototype 每次新的实例
public class AlphaService {//容器管理创建初始化和销毁
    @Autowired
    private AlphaDao alphaDao;
    public AlphaService(){
        System.out.println("实例化service");
    }
    @PostConstruct
    //在构造器之后调用
    public void init(){
        System.out.println("初始化service");
    }
    @PreDestroy
    public void destory(){
        System.out.println("销毁service");
    }

    public String find(){
        return alphaDao.select();
    }
}
