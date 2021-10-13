package com.chat.server.handler;

import com.chat.common.handler.ProtostuffDecode;
import com.chat.common.handler.ProtostuffEncode;
import com.chat.server.ClientStarter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;

import java.util.Objects;

/**
 * @Program: chatRoom
 * @Description: 客户端处理器初始化
 * @Author: LHB
 * @Version: v0.0.1
 * @Time: 2021-10-13 11:10
 **/
public class ClientHandlerInitializer extends ChannelInitializer<SocketChannel> {
    private ReconnectHandler reconnectHandler;

    public ClientHandlerInitializer(ClientStarter clientStarter) throws Exception {
        if (clientStarter == null) {
            throw new Exception("ClientStarter can not be null.");
        }
        this.reconnectHandler = new ReconnectHandler(clientStarter);
    }

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
//      pipeline.addLast("encoder", new StringEncoder());
//      pipeline.addLast("decoder", new StringDecoder());
        pipeline.addLast(this.reconnectHandler);
        pipeline.addLast("decoder",new ProtostuffDecode());
        pipeline.addLast("encoder",new ProtostuffEncode());
        pipeline.addLast(new NettyClientHandler());
        pipeline.addLast(new Pinger());
    }
}
