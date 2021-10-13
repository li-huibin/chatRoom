package com.chat.client.handler;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

/**
 * @Program: chatRoom
 * @Description: <p>在规定时间内未收到客户端的任何数据包, 将主动断开该连接</p>
 * @Author: LHB
 * @Version: v0.0.1
 * @Time: 2021-10-13 13:56
 **/
public class ServerIdleStateTrigger extends ChannelInboundHandlerAdapter {
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleState state = ((IdleStateEvent)evt).state();
            switch (state) {
                case WRITER_IDLE:
                    break;
                case READER_IDLE:
                    // 在规定时间内没有收到客户端的上行数据, 主动断开连接
                    ctx.disconnect();
                    break;
                case ALL_IDLE:
                    break;
                default:
            }
        }
    }
}
