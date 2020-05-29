package com.nowcoder.community.controller;

import com.nowcoder.community.service.DataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Date;

@Controller
public class DataController {

    @Autowired
    private DataService dataService;

    //打开统计页面
    @RequestMapping(path="/data", method = {RequestMethod.GET, RequestMethod.POST})
    public String getDataPage(){
        return  "/site/admin/data";
    }

    //处理统计网站UV的请求
    @RequestMapping(path="/data/uv", method = RequestMethod.POST)
    public String getUV(@DateTimeFormat(pattern = "yyyy-MM-dd") Date start,
                        @DateTimeFormat(pattern = "yyyy-MM-dd")Date end, Model model) {
        long uv = dataService.calculateUV(start, end);
        model.addAttribute("uvResult",uv);
        model.addAttribute("uvStartDate",start);
        model.addAttribute("uvEndDate",end);
       // return "/site/admin/data";
        return "forward:/data";//表示处理一般交给getDataPage接着处理。可以复用getDataPage的逻辑
    }

    //统计活跃用户
    @RequestMapping(path="/data/dau", method = RequestMethod.POST)
    public String getDAU(@DateTimeFormat(pattern = "yyyy-MM-dd") Date start,
                        @DateTimeFormat(pattern = "yyyy-MM-dd")Date end, Model model) {
        long dau = dataService.calculateDAU(start, end);
        model.addAttribute("dauResult",dau);
        model.addAttribute("dauStartDate",start);
        model.addAttribute("dauEndDate",end);
        // return "/site/admin/data";
        return "forward:/data";//表示处理一般交给getDataPage接着处理。可以复用getDataPage的逻辑
    }
}
