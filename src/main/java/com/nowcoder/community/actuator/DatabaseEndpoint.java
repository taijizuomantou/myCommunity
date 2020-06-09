package com.nowcoder.community.actuator;

import com.nowcoder.community.util.CommunityUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@Component
@Endpoint(id = "database")//一定要记得做权限管理
public class DatabaseEndpoint {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseEndpoint.class);

    @Autowired
    private DataSource dataSource;

    //该方法是通过get请求来访问的
    @ReadOperation
    public String checkConnection(){
        try (Connection connection = dataSource.getConnection();){
            return CommunityUtil.getJSONString(0,"获取连接成功");
        } catch (SQLException e) {
            logger.error("获取数据库连接失败");
            e.printStackTrace();
            return CommunityUtil.getJSONString(1,"获取连接失败");
        }

    }

}
