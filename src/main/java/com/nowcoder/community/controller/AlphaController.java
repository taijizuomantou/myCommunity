package com.nowcoder.community.controller;

import com.nowcoder.community.service.AlphaService;
import com.nowcoder.community.util.CommunityUtil;
import com.tencent.cloud.CosStsClient;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

@Controller
@RequestMapping("/alpha")
public class AlphaController {
    @Autowired
    private AlphaService alphaService;
    @RequestMapping("/hello")
    @ResponseBody
    //after application set http://localhost:8080/community/alpha/hello
    public String sayHello(){//http://localhost:8080/alpha/hello
        return "Hellow World";
    }

    @RequestMapping("/data")
    @ResponseBody
    public String getData(){
        return alphaService.find();
    }

    @RequestMapping("/http")
    public void http(HttpServletRequest request, HttpServletResponse response){
        //获取请求数据
        System.out.println(request.getMethod());
        System.out.println(request.getServletPath());
        Enumeration<String> enumeration = request.getHeaderNames();
        while(enumeration.hasMoreElements()){
            String name = enumeration.nextElement();
            String value = request.getHeader(name);
            System.out.println(name + ":"+ value);
        }
        System.out.println(request.getParameter("code"));//?code=123&name=zhangsan

        //返回响应数据
        response.setContentType("text/html;charset=utf-8");
        try(PrintWriter writer = response.getWriter();) {
            writer.write("<h1>牛客网</h1>");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return;
    }

    // GET请求 默认发送的请求

    //查询 /students?current=1&limit=20
    //只能处理get
    @RequestMapping(path ="/students",method = RequestMethod.GET)
    @ResponseBody
    public String getStudents(
            @RequestParam(name = "current", required = false, defaultValue = "1") int current,
            @RequestParam(name = "limit", required = false, defaultValue = "100") int limit){//只要参数名和传过来的一致即可
        System.out.println(current);
        System.out.println(limit);
        return "some students";
    }

    //查询一个学生 /student/123 把参数加在路径中
    @RequestMapping(path="/student/{id}",method=RequestMethod.GET)
    @ResponseBody
    public String getStudent(@PathVariable("id")int id){
        System.out.println(id);
        return "a student";
    }

    //浏览器向服务器提交数据时常用post。项目中只用post和get就足够了
    //POST请求（//get请求也可以传参，明面上传数据量有限）
    @RequestMapping(path="/student",method = RequestMethod.POST)
    @ResponseBody
    public String saveStudent(String name,int age){//和name一样就行
        System.out.println(name);
        System.out.println(age);
        return "success";
    }

    //响应HTML数据

    @RequestMapping(path="/teacher",method=RequestMethod.GET)//http://localhost:8080/community/alpha/teacher
    //默认返回HTML
    public ModelAndView getTeacher(){
        ModelAndView mav = new ModelAndView();
        mav.addObject("name","张三");
        mav.addObject("age",30);
        mav.setViewName("/demo/view");//view.html
        return mav;
    }

    //更简单的方法
    @RequestMapping(path="/school",method=RequestMethod.GET)
    public String getSchool(Model model){//返回view的路径
        model.addAttribute("name","北京大学");
        model.addAttribute("age",80);
        return "/demo/view";
    }

    //响应JSON数据，一般是在异步请求中。比如判断昵称是否被占用
    //Java对象 JS解析JAVA
    //通过JSON将JAVA对象->JSON->JS对象

    @RequestMapping(path="/emp",method=RequestMethod.GET)
    @ResponseBody
    public Map<String,Object> getEmp(){
        Map<String,Object> emp = new HashMap<>();
        emp.put("name","张三");
        emp.put("age",23);
        emp.put("salary",800.00);
        return emp;
    }

    @RequestMapping(path="/emps",method=RequestMethod.GET)
    @ResponseBody
    //[{"name":"张三","salary":800.0,"age":23},{"name":"李四","salary":9000.0,"age":24},{"name":"王五","salary":90.0,"age":600}]
    public List<Map<String,Object>> getEmps(){
        List<Map<String,Object>>list = new ArrayList<>();
        Map<String,Object> emp = new HashMap<>();
        emp.put("name","张三");
        emp.put("age",23);
        emp.put("salary",800.00);
        list.add(emp);
        emp = new HashMap<>();
        emp.put("name","李四");
        emp.put("age",24);
        emp.put("salary",9000.00);
        list.add(emp);
        emp = new HashMap<>();
        emp.put("name","王五");
        emp.put("age",600);
        emp.put("salary",90.00);
        list.add(emp);
        return list;
    }

    //cookie示例.存在头部

    @RequestMapping(path="/cookie/set",method = RequestMethod.GET)
    @ResponseBody
    public String setCookie(HttpServletResponse response){
        Cookie cookie =new Cookie("code", CommunityUtil.generateUUID());//每个cookie对象只能存一对name value
        //设置cookie生效的范围。有些请求不需要cookie
        //cookie.setPath("/community");//在整个项目下都有效
        cookie.setPath("/community/alpha");//只在alpha路径下有效
        //默认情况下浏览器的内存关掉时cookie消失。
        //设置cookie的生效时间，会存在硬盘里长期有效，直到超时
        cookie.setMaxAge(60*10);//单位是秒 10min
        //发送cookie把cookie放在response的头部
        response.addCookie(cookie);
        return "set cookie";
    }

    @RequestMapping(path="/cookie/get",method = RequestMethod.GET)
    @ResponseBody
    public String getCookie(@CookieValue("code")String code){
        System.out.println(code);
        return "get cookie";
    }

    //session示例
    @RequestMapping(path="/session/set",method = RequestMethod.GET)
    @ResponseBody
    public String setSession(HttpSession session){//自动注入
        session.setAttribute("id",1);
        session.setAttribute("name","Test");
        return "set session";
    }

    @RequestMapping(path="/session/get",method = RequestMethod.GET)
    @ResponseBody
    public String getSession(HttpSession session){
        System.out.println(session.getAttribute("id"));
        System.out.println(session.getAttribute("name"));

        return "get session";
    }

    //AJAX示例
    @RequestMapping(path="ajax",method=RequestMethod.POST)
    @ResponseBody
    public String testAjax(String name, int age){
        System.out.println(name);
        System.out.println(age);
        return CommunityUtil.getJSONString(0,"操作成功");
    }

    @RequestMapping(path="search", method = RequestMethod.GET)
    public String search(String keyword, Model model) {
        System.out.println(keyword);
        return "/demo/search";
    }
    //跳转到testGet.html视图
    @RequestMapping("/")
    public String toTestGet(){
        return "/site/testAjaxGet";
    }

    //返回String对象
    @GetMapping("/testGet")
    @ResponseBody
    public String testGet(){
        JSONObject resultJson = new JSONObject();
        TreeMap<String, Object> config = new TreeMap<String, Object>();

        try {
            // 替换为您的 SecretId
            config.put("SecretId", "AKIDf7KUTDL6i41i14aPjpgZCbkjCOM3GaQv");
            // 替换为您的 SecretKey
            config.put("SecretKey", "LQGrVq5Awb0A6qJhwyvnZ8RULxJCrGSs");

            // 临时密钥有效时长，单位是秒，默认1800秒，最长可设定有效期为7200秒
            config.put("durationSeconds", 1800);

            // 换成您的 bucket
            config.put("bucket", "community-header-1252397182");
            // 换成 bucket 所在地区
            config.put("region", "ap-nanjing");

            // 这里改成允许的路径前缀，可以根据自己网站的用户登录态判断允许上传的具体路径，例子：a.jpg 或者 a/* 或者 * 。
            // 如果填写了“*”，将允许用户访问所有资源；除非业务需要，否则请按照最小权限原则授予用户相应的访问权限范围。
            config.put("allowPrefix", "*");

            // 密钥的权限列表。简单上传、表单上传和分片上传需要以下的权限，其他权限列表请看 https://cloud.tencent.com/document/product/436/31923
            String[] allowActions = new String[] {
                    // 简单上传
                    "name/cos:PutObject",
                    // 表单上传、小程序上传
                    "name/cos:PostObject",
            };
            config.put("allowActions", allowActions);

            JSONObject credential = CosStsClient.getCredential(config);
            //成功返回临时密钥信息，如下打印密钥信息
            System.out.println(credential);
            return credential.toString(4);
        } catch (Exception e) {
            //失败抛出异常
            e.printStackTrace();
            throw new IllegalArgumentException("no valid secret !");
        }

    }

}
