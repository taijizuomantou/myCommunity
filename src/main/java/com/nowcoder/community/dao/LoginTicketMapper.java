package com.nowcoder.community.dao;

import com.nowcoder.community.entity.LoginTicket;
import org.apache.ibatis.annotations.*;

//也可以用注解而不是xml文件.也可以写if,但必须套用script标签
//这种方式少些了一个xml文件，阅读更麻烦
@Mapper
//这个组件不推荐使用了
@Deprecated

public interface LoginTicketMapper {

    @Insert({
            "insert into login_ticket(user_id,ticket,status,expired) ",//注意每句话之后要加空格
            "values(#{userId},#{ticket},#{status},#{expired})"
    })
    @Options(useGeneratedKeys = true, keyProperty = "id") //自动生成主键，并自动注入id中
    int insertLoginTicket(LoginTicket loginTicket);

    @Select({
            "select id,user_id,ticket,status,expired ",
            "from login_ticket where ticket=#{ticket}"
    })
    LoginTicket selectByTicket(String ticket);

    @Update({
            "<script>",
            "update login_ticket set status=#{status} where ticket = #{ticket} ",
            "<if test=\"ticket!=null\"> ",
            "and 1 = 1 ",
            "</if>",
            "</script>"
    })
    int updateStatus(String ticket, int status);//用修改状态代替删除

}
