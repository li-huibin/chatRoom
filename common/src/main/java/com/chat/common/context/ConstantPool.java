package com.chat.common.context;

/**
 * @Program: chatRoom
 * @Description: 常量池
 * @Author: LHB
 * @Version: v0.0.1
 * @Time: 2021-10-13 15:27
 **/
public interface ConstantPool {
    /** ping消息间隔(s) */
    public final int PING_DELAY = 3;
    /** 掉线重连时间间隔(s) */
    public final int RECONNECT_TIME_DELAY = 15;
    /** 掉线重连次数 */
    public final int RECONNECT_TIMES = 10;
}
