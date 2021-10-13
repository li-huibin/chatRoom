package com.chat.server.handler;

import com.chat.common.context.ConstantPool;
import com.chat.common.entity.Message;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.concurrent.ScheduledFuture;

import java.util.concurrent.TimeUnit;

/**
 * @Program: chatRoom
 * @Description: <p>客户端连接到服务器端后，会循环执行一个任务：随机等待几秒，然后ping一下Server端，即发送一个心跳包。</p>
 * @Author: LHB
 * @Version: v0.0.1
 * @Time: 2021-10-13 11:18
 **/
public class Pinger extends ChannelInboundHandlerAdapter {
    private Channel channel;

    /**
     * 当客户端连接服务器完成就会触发该方法
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
//        super.channelActive(ctx);
        System.out.println("客户端上线");
        this.channel = ctx.channel();
        ping(ctx.channel());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("客户端下线");
    }

    private void ping(Channel channel) {
        final Channel fChannel = channel;
        ScheduledFuture<?> future = channel.eventLoop().schedule(new Runnable() {
            @Override
            public void run() {
                if (fChannel.isActive()) {
                    fChannel.writeAndFlush(new Message((byte) 1, " "));
                } else {
                    fChannel.closeFuture();
                    throw new RuntimeException();
                }
            }
        }, ConstantPool.PING_DELAY, TimeUnit.SECONDS);

        future.addListener(new GenericFutureListener() {
            @Override
            public void operationComplete(Future future) throws Exception {
                if (future.isSuccess()) {
                    ping(fChannel);
                }
            }
        });
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        // 当Channel已经断开的情况下, 仍然发送数据,
        cause.printStackTrace();
        ctx.close();
    }
}