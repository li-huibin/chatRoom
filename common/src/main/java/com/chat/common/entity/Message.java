package com.chat.common.entity;

import java.util.Arrays;

/**
 * @Program: chatRoom
 * @Description: 消息数据结构体
 * @Author: LHB
 * @Version: v0.0.1
 * @Time: 2021-10-12 15:57
 **/
public class Message {
    /** 消息类型 1-心跳检测；2-客户消息发送 */
    private byte type;
    /** 消息体 */
    private String message;

    public Message() {
    }

    public Message(byte type, String message) {
        this.type = type;
        this.message = message;
    }

    public Message(String message) {
        this.type = 2;
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public byte getType() {
        return type;
    }

    public void setType(byte type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "Message{" +
                "type=" + type +
                ", message=" + message +
                '}';
    }
}
