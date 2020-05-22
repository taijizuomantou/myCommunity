package com.nowcoder.community.dao;

import com.nowcoder.community.entity.Message;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MessageMapper {

    //查询当前用户的回话列表，针对每个回话只返回一条最新的私信
    List<Message> selectConversations(int userId, int offset, int limit);

    //查询当前用户的回话数量
    int selectConversationsCount(int userId);

    //查询某个回话所包含的私信列表
    List<Message> selectLetters(String conversationId, int offset, int limit);

    //查询某个回话所包含的私信的数量
    int selectLettersCount(String conversationId);

    //查询未读私信的数量 回话id作为动态条件去拼。使得该方法能够实现两种业务
    int selectLetterUnreadCount(int userId, String conversationId);



}
