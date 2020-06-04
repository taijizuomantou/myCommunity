package com.nowcoder.community.controller;

import com.nowcoder.community.annotation.LoginRequired;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.FollowService;
import com.nowcoder.community.service.LikeService;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.util.CommunityConstant;
import com.nowcoder.community.util.CommunityUtil;
import com.nowcoder.community.util.HostHolder;
import com.tencent.cloud.CosStsClient;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.Map;
import java.util.TreeMap;

@Controller
@RequestMapping("/user")
public class UserController implements CommunityConstant {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Value("${community.path.upload}")
    private String uploadPath;

    @Value("${community.path.domain}")
    private String domain;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Autowired
    private UserService userService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private LikeService likeService;

    @Autowired
    private FollowService followService;

    @Value("${tencent.key.access}")
    private String accessKey;

    @Value("${tencent.key.secret}")
    private String secretKey;

    @Value("${tencent.bucket.header.name}")
    private String headerBucketName;

    @Value("${tencent.bucket.header.region}")
    private String headerBucketRegion;

    @Value("${tencent.bucket.header.url}")
    private String headerBucketUrl;

    @GetMapping("/getTempTencetnAuth")
    @ResponseBody
    public String testGet(){
        JSONObject resultJson = new JSONObject();
        TreeMap<String, Object> config = new TreeMap<String, Object>();

        try {
            // 替换为您的 SecretId
            config.put("SecretId", accessKey);
            // 替换为您的 SecretKey
            config.put("SecretKey", secretKey);

            // 临时密钥有效时长，单位是秒，默认1800秒，最长可设定有效期为7200秒
            config.put("durationSeconds", 1800);

            // 换成您的 bucket
            config.put("bucket", headerBucketName);
            // 换成 bucket 所在地区
            config.put("region", headerBucketRegion);

            // 这里改成允许的路径前缀，可以根据自己网站的用户登录态判断允许上传的具体路径，例子：a.jpg 或者 a/* 或者 * 。
            // 如果填写了“*”，将允许用户访问所有资源；除非业务需要，否则请按照最小权限原则授予用户相应的访问权限范围。
            config.put("allowPrefix", "*");

            // 密钥的权限列表。简单上传、表单上传和分片上传需要以下的权限，其他权限列表请看 https://cloud.tencent.com/document/product/436/31923
            String[] allowActions = new String[] {
                    // 简单上传
                    "name/cos:PutObject",
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


    @LoginRequired
    @RequestMapping(path="/setting", method = RequestMethod.GET)
    public String getSettingPage(Model model){
        //上传文件名称
        String fileName = CommunityUtil.generateUUID();

        model.addAttribute("fileName",fileName);
        model.addAttribute("bucket",headerBucketName);
        model.addAttribute("region",headerBucketRegion);

        return "/site/setting";
    }

    //更新头像路径
    @RequestMapping(path="/header/url", method = RequestMethod.POST)
    @ResponseBody
    public String updateHeaderUrl(String fileName){
        if(StringUtils.isBlank(fileName)){
            return CommunityUtil.getJSONString(1,"文件名不能为空");
        }
        String url = headerBucketUrl + "/" + fileName;
        userService.updateHeader(hostHolder.getUser().getId(), url);
        return CommunityUtil.getJSONString(0);
    }

    //废弃
    @LoginRequired
    @RequestMapping(path="/upload", method = RequestMethod.POST)
    public String uploadHeader(MultipartFile headerImage, Model model){//因为MulitipartFile在表现层
        if(headerImage == null){
            model.addAttribute("error","您还没有选择图片");
            return "/site/setting";
        }

        String fileName = headerImage.getOriginalFilename();

        String suffix = fileName.substring(fileName.lastIndexOf('.'));
        if(StringUtils.isBlank(suffix)){
            model.addAttribute("error","文件的格式不正确");
            return "/site/setting";
        }

        //生成随机的文件名
        fileName = CommunityUtil.generateUUID() + suffix;
        //确定文件的存放路径
        File dest = new File(uploadPath + "/" + fileName);
        try {
            //存储文件
            headerImage.transferTo(dest);
        } catch (IOException e) {
            logger.error("上传文件失败"+e.getMessage());
            throw new RuntimeException("上传文件失败，服务器发生异常！",e);
        }

        //更新当前用户的头像路径。注意该路径不是本地路径而是web访问路径
        //http://localhost:8080/community/user/header/xxxx.png
        User user = hostHolder.getUser();
        String headerUrl = domain + contextPath +"/user/header/"+fileName;
        userService.updateHeader(user.getId(),headerUrl);
        return "redirect:/index";
    }

    //废弃
    @RequestMapping(path="/header/{fileName}",method = RequestMethod.GET)
    public void getHeader(@PathVariable("fileName")String fileName, HttpServletResponse response){
        //服务器存放的路径
        fileName = uploadPath + "/" + fileName;
        //解析文件的后缀 向浏览器输出图片。文件的格式就是文件的后缀。
        String suffix = fileName.substring(fileName.lastIndexOf('.'));
        //响应图片
        response.setContentType("image/" + suffix);
        try (
                OutputStream os = response.getOutputStream();
                FileInputStream fis = new FileInputStream(fileName);//自动关闭
        ){

            //声明一个缓冲区
            byte[]buffer = new byte[1024];
            int b = 0;
            while((b = fis.read(buffer))!=-1){
                os.write(buffer,0,b);
            }
        } catch (IOException e) {
            logger.error("读取头像失败"+e.getMessage());
        }
    }

    @LoginRequired
    @RequestMapping(path="/update", method = RequestMethod.POST)
    public String updatePassword(Model model, String oldPassword, String newPassword){
        User user = hostHolder.getUser();
        int userId = user.getId();
        Map<String,Object> map = userService.updatePassword(userId,oldPassword,newPassword);
        if(map != null){
            model.addAttribute("oldPasswordMsg",map.get("oldPasswordMsg"));
            model.addAttribute("newPasswordMsg",map.get("newPasswordMsg"));
            return "/site/setting";
        }
        return "redirect:/index";
    }

    //个人主页 ,不仅可以查看自己的主页
    @RequestMapping(path="profile/{userId}", method = RequestMethod.GET)
    public String getProfilePage(@PathVariable("userId")int userId, Model model){
        User user = userService.findUserById(userId);
        if(user == null){
            throw new RuntimeException("该用户不存在！");
        }

        //用户
        model.addAttribute("user",user);
        //用户获赞数
        int likeCount = likeService.findUserLikeCount(user.getId());
        model.addAttribute("likeCount",likeCount);

        //关注数量
        long followeeCount = followService.findFolloweeCount(userId,ENTITY_TYPE_USER);
        model.addAttribute("followeeCount",followeeCount);
        //粉丝数量
        long followerCount = followService.findFollowerCount(ENTITY_TYPE_USER,userId);
        model.addAttribute("followerCount",followerCount);
        //是否已关注
        boolean hasFollowed = false;
        if(hostHolder.getUser()!=null){
            hasFollowed = followService.hasFollowed(hostHolder.getUser().getId(),ENTITY_TYPE_USER, userId);
        }
        model.addAttribute("hasFollowed",hasFollowed);

        return "/site/profile";

    }
}
