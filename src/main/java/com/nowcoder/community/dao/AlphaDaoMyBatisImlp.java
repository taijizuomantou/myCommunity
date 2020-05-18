package com.nowcoder.community.dao;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

@Repository
@Primary
//primary 更高的优先级
public class AlphaDaoMyBatisImlp implements AlphaDao{
    @Override
    public String select() {
        return "MyBatis";
    }
}
