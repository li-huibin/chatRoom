package com.chat.client.handler;

import com.chat.common.entity.Message;
import io.netty.channel.ChannelHandlerContext;

/**
 * @Program: chatRoom
 * @Description:
 * @Author: LHB
 * @Version: v0.0.1
 * @Time: 2021-10-13 08:58
 **/
public class HeartbeatServerHandler{
    public void channelRead0(ChannelHandlerContext ctx, Message msg) throws Exception {
        System.out.println("=====> 心跳检测");
    }

    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {

    }
}
