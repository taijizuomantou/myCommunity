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

    //新增一个消息
    int inserMessage(Message message);

    //修改消息的状态
    int updateStatus(List<Integer> ids, int status);

    //查询某人能看到的某个主题下最新的通知
    Message selectLatestNotice(int userId, String topic);

    //查询某个主题所包含的通知的数量
    int selectNoticeCount(int userId, String topic);

    //查询未读的通知的数量
    int selectNoticeUnreadCount(int userId, String topic);

    //查询某个主题所包含的所有通知 要包含分页
    List<Message> selectNotices(int userId, String topic, int offset, int limit);

}
