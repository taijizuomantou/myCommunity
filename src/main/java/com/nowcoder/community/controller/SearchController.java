package com.nowcoder.community.controller;

import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.entity.Page;
import com.nowcoder.community.service.ElasticsearchService;
import com.nowcoder.community.service.LikeService;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.util.CommunityConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class SearchController implements CommunityConstant {

    @Autowired
    private ElasticsearchService elasticsearchService;

    //查看搜索到帖子的作者和点赞数量
    @Autowired
    private UserService userService;

    @Autowired
    private LikeService likeService;

    //   /search?keyword=xxx
    @RequestMapping(path="/search", method = RequestMethod.GET)
    public String search(String keyword, Page page, Model model) {
        //搜索帖子
        org.springframework.data.domain.Page<DiscussPost> searchResult=
         elasticsearchService.searchDIscussPost(keyword, page.getCurrent() - 1, page.getLimit()); //page是从1开始search方法中index从零开始
        //聚合数据
        List<Map<String,Object>>discussposts = new ArrayList<>();
        if(searchResult != null){
            for(DiscussPost post: searchResult){
                Map<String,Object> map = new HashMap<>();
                //帖子
                map.put("post",post);
                //作者
                map.put("user",userService.findUserById(post.getUserId()));
                //点赞数量
                map.put("likeCount",likeService.findEntityLikeCount(ENTITY_TYPE_POST, post.getId()));

                discussposts.add(map);
            }
        }
        model.addAttribute("discussposts",discussposts);
        model.addAttribute("keyword",keyword);

        //设置分页信息
        page.setPath("/search?keyword="+keyword);
        page.setRows(searchResult == null ? 0: (int)searchResult.getTotalElements());

        return "/site/search";
    }

}
