package com.chat.client.handler;

import com.chat.common.context.ConstantPool;
import com.chat.common.handler.ProtostuffDecode;
import com.chat.common.handler.ProtostuffEncode;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.TimeUnit;

/**
 * @Program: chatRoom
 * @Description: 服务端处理器初始化
 * @Author: LHB
 * @Version: v0.0.1
 * @Time: 2021-10-13 11:14
 **/
public class ServerHandlerInitializer extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        pipeline.addLast("decoder",new ProtostuffDecode());
        pipeline.addLast("encoder",new ProtostuffEncode());
        // IdleStateHandler的参数readerIdleTime参数指定超过3秒还没有收到客户端连接
        // 会触发IdleStateEvent事件并且交给下一个handler处理，下一个handler必须实现userEventTriggered方法处理对应事件
        // 心跳包接收方延时比发送方多1秒，保证接收方能够接收到数据
        pipeline.addLast(new IdleStateHandler(ConstantPool.PING_DELAY + 1,0,0, TimeUnit.SECONDS));
        pipeline.addLast(new ServerIdleStateTrigger());
        // 对workerGroup的SocketChannel设置处理器
        pipeline.addLast(new NettyServerHandler());
    }
}
