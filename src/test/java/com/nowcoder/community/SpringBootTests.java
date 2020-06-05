package com.nowcoder.community;

import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.service.DiscussPostService;
import org.junit.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)//设置与main包中相同的配置类
public class SpringBootTests {

    @Autowired
    private DiscussPostService discussPostService;

    private DiscussPost data;

    @BeforeClass
    public static void  beforeClass(){
        System.out.println("beforeClass");
    }

    @AfterClass
    public static void afterClass(){
        System.out.println("afterClass");
    }

    @Before
    public void before(){//每个方法之前初始化一份新数据
        //初始化测试数据
        System.out.println("before");
        data = new DiscussPost();
        data.setUserId(111);
        data.setTitle("Test Title");
        data.setContent("Test Content");
        data.setCreateTime(new Date());
        discussPostService.addDiscussPost(data);
    }

    @After
    public void after(){
        //删除测试数据
        System.out.println("after");
        discussPostService.updateStatus(data.getUserId(), 2);
    }

    @Test
    public void test1(){
        System.out.println("test1");
    }

    @Test
    public void test2(){
        System.out.println("test2");
    }

    @Test
    public void testFindById(){
        DiscussPost post = discussPostService.findDisccusPostById(data.getId());
        Assert.assertNotNull(post);
        Assert.assertEquals(data.getTitle(), post.getTitle());
        Assert.assertEquals(data.getContent(), post.getContent());
        Assert.assertEquals(data.toString(), post.toString()); //createTime存在问题，可能是因为mysql中的timestamp类和Date
        //修改mysql中create_time精度之后正确
    }

    @Test
    public void testUpdateScore(){
        int rows = discussPostService.updateScore(data.getId(), 2000.0);
        Assert.assertEquals(1,rows);
        DiscussPost post = discussPostService.findDisccusPostById(data.getId());
        Assert.assertEquals(2000.00,post.getScore(),2);//delta表示精度


    }

}
