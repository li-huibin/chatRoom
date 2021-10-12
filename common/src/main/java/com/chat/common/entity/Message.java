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
    /** 消息长度 */
    private int length;
    /** 消息体 */
    private byte[] message;

    public Message() {
    }

    public Message(int length, byte[] message) {
        this.length = length;
        this.message = message;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public byte[] getMessage() {
        return message;
    }

    public void setMessage(byte[] message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "Message{" +
                "length=" + length +
                ", message=" + Arrays.toString(message) +
                '}';
    }
}
