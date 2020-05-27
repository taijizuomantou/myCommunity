package com.nowcoder.community.dao.elasticsearch;

import com.nowcoder.community.entity.DiscussPost;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository//mapper是mybatis专有的注解。都不用写，自动增删改查
public interface DiscussPostRepository extends ElasticsearchRepository<DiscussPost, Integer> {//实体类的主键是什么类型

}
