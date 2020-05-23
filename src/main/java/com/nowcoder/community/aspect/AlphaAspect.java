package com.nowcoder.community.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

//@Component
//@Aspect

public class AlphaAspect {

    //*表示方法的返回值 com.nowcoder.community.service包名表示包下的所有类.*,.*所有的方法（..）所有参数
    @Pointcut("execution(* com.nowcoder.community.service.*.*(..))")
    public void pointcut(){

    }

    //在连接点pointcut()开头的位置记日志
    @Before("pointcut()")
    public void before(){
        System.out.println("before");
    }

    //在连接点pointcut()后面的位置记日志
    @After("pointcut()")
    public void after(){
        System.out.println("after");
    }

    //在有了返回值之后
    @AfterReturning("pointcut()")
    public void afterReturning(){
        System.out.println("afterReturning");
    }

    //在有了返回值之后
    @AfterThrowing("pointcut()")
    public void afterThrowing(){
        System.out.println("afterThrowing");
    }

    //前后同时织如逻辑
    @Around("pointcut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        System.out.println("around before");
        Object obj = joinPoint.proceed();//调用目标组件的方法
        System.out.println("around after");
        return obj;
    }


}
