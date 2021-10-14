package com.chat.common.context;

/**
 * @Program: chatRoom
 * @Description: 消息码常量池
 * @Author: LHB
 * @Version: v0.0.1
 * @Time: 2021-10-14 09:41
 **/
public interface MessageCodeConstantPool {
    /** 私聊 */
    int PRIVATE_CHAT_CODE = 1;
    /** 群聊 */
    int GROUP_CHAT_CODE = 2;
    /** ping心跳消息 */
    int PING_MESSAGE_CODE = 3;
    /** pong消息 */
    int PONG_MESSAGE_CODE = 4;
    /** 系统消息 */
    int SYSTEM_MESSAGE_CODE = 5;
}
