package com.chat.server.handler;

import com.chat.common.entity.Message;
import com.chat.server.ClientStarter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * @Class NettyClientHandler
 * @Description 客户端处理
 * @Author lihuibin
 * @Date 2021/10/10 18:14
 * @Version 1.0
 * 更新记录:
 * 更新时间        更新人        更新位置        更新内容
 * 2021/10/10       lihuibin       新建           新建
 */
public class NettyClientHandler extends SimpleChannelInboundHandler<Message> {
    /**
     * 当客户端连接服务器完成就会触发该方法
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
//        System.out.println("channelActive");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("重新连接客户端");
        ClientStarter.getClient().connect();
    }

    /**
     * 当通道有读取事件时就会触发，即服务端发送数据给客户端
     * @param ctx
     * @param msg
     * @throws Exception
     */
//    @Override
//    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
//        System.out.println(msg);
//    }

    /**
     * 当通道有读取事件时就会触发，即服务端发送数据给客户端
     * @param channelHandlerContext
     * @param s
     * @throws Exception
     */
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Message s) throws Exception {
        if (s.getType() > 1) {
            System.out.println(s.getMessage());
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
