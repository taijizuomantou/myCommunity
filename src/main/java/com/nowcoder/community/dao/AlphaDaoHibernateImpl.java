package com.nowcoder.community.dao;

import org.springframework.stereotype.Repository;

@Repository("alphahibernate")
//自定义bean的名字
public class AlphaDaoHibernateImpl implements AlphaDao {
    @Override
    public String select() {
        return "Hibernate";
    }
}
