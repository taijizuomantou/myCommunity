package com.nowcoder.community.util;

import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;


@Component
public class SensitiveFilter {

    private static final Logger logger = LoggerFactory.getLogger(SensitiveFilter.class);

    //替换符
    private static final String REPLACEMENT = "喵喵喵";

    //根节点初始化
    private TrieNode root = new TrieNode();

    @PostConstruct
    public void init(){
        try(
            InputStream is = this.getClass().getClassLoader().getResourceAsStream("sensitive-words.txt");//从target，classes目录下读取配置文件
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        ){
            String keyword;
            while((keyword = reader.readLine()) != null){
                //将keyword添加到敏感词
                this.addKeyWord(keyword);
            }
        }catch(IOException e){
            logger.error("加载敏感词文件失败" + e.getMessage());
        }
    }

    //将一个敏感词添加到前缀树中去
    private void addKeyWord(String keyword){
        TrieNode tempNode = root;
        for(int i = 0;i < keyword.length(); i ++) {
            char c = keyword.charAt(i);
            TrieNode subNode = tempNode.getSubNode(c);

            if (subNode == null) {
                //初始化子节点
                subNode = new TrieNode();
                tempNode.addSubNode(c, subNode);
            }
            //指向子节点进入下一轮循环
            tempNode = subNode;

            //设置结束标识
            if (i == keyword.length() - 1) {
                tempNode.setKeyEnd(true);
            }
        }
    }

    /*
   *过滤敏感词
   *@param text:待过滤的文本
   *@return 过滤后的问答本
     */
    public String filter(String text){
        if(StringUtils.isBlank(text))return null;

        //指针1，指向树
        TrieNode tempNode = root;
        //指针2 begin
        int begin = 0;
        //指针3
        int position = 0;
        // 最终的结果
        StringBuilder sb = new StringBuilder();

        while(begin < text.length()){
            char c = text.charAt(position);

            //跳过符号
            if(isSymbol(c)){
                // 若指针1处于根节点，此时b= p,将词符号记入结果
                if(begin == position){
                    sb.append(c);
                    begin ++;
                }
                //无论符号在开头还是中间指针三都向下走一步
                position ++;
                continue;
            }

            //检查下级节点
            tempNode = tempNode.getSubNode(c);
            if(tempNode == null){
                //以begin为开头的字符串不是敏感词
                sb.append(text.charAt(begin));
                //进入下一个位置
                begin ++;
                position = begin;
                //重新指向根节点
                tempNode = root;
            }
            else if(tempNode.isKeyEnd){
                //发现了敏感词,begin开头,position结尾
                sb.append(REPLACEMENT);
                //进入下一个位置
                begin = position + 1;
                position ++;
                //重新指向根节点
                tempNode = root;
            }
            else{
                //还在探索是否为敏感词,继续检查下一个字符
                position ++;
            }
        }
        return sb.toString();
    }

    //判断是否为符号
    private boolean isSymbol(Character c){
        //0x2E80 - 0x9FFF是东亚文字范围
        return !CharUtils.isAsciiAlphanumeric(c) && (c < 0x2E80 || c > 0x9FFF );
    }

    //前缀树
    private class TrieNode{

        //关键词结束的表示
        private boolean isKeyEnd = false;

        //子节点(key是下级节点的字符，value是下级节点）
        private Map<Character,TrieNode> subnodes = new HashMap<>();

        public boolean isKeyEnd() {
            return isKeyEnd;
        }

        public void setKeyEnd(boolean keyEnd) {
            isKeyEnd = keyEnd;
        }

        //添加子节点
        public void addSubNode(Character c, TrieNode node){
            subnodes.put(c,node);
        }

        //获取子节点
        public TrieNode getSubNode(Character c){
            return subnodes.get(c);
        }
    }

}
