package com.chat.server.handler;

import com.chat.server.ClientStarter;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.EventLoop;

import java.util.concurrent.TimeUnit;

/**
 * @Program: chatRoom
 * @Description: 掉线重连
 * @Author: LHB
 * @Version: v0.0.1
 * @Time: 2021-10-13 13:06
 **/
@ChannelHandler.Sharable
public class ReconnectHandler extends ChannelInboundHandlerAdapter {
    private int retries = 0;
    private ClientStarter clientStarter;
    private RetryPolicy retryPolicy;

    public ReconnectHandler(ClientStarter clientStarter) {
        this.clientStarter = clientStarter;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("Successfully established a connection to the server.");
        retries++;
        // 设置channel为活跃状态
        ctx.fireChannelActive();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("channelInactive");
        if (retries < 1) {
            System.out.println("Lost the connection with the server.");
            ctx.close();
        }
        boolean allowRetry = getRetryPolicy().allowRetry(retries);
        if (allowRetry) {
            long sleepTimeMs = getRetryPolicy().getSleepTimeMs(retries);
            System.out.println(String.format("Try to reconnect to the server after %dms. Retry count: %d.", sleepTimeMs, ++retries));
            EventLoop eventExecutors = ctx.channel().eventLoop();
            eventExecutors.schedule(new Runnable() {
                @Override
                public void run() {
                    System.out.println("Reconnecting...");
                    try {
                        clientStarter.connect();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            },sleepTimeMs, TimeUnit.MILLISECONDS);
        }
        ctx.fireChannelActive();
    }

    private RetryPolicy getRetryPolicy() {
        if (this.retryPolicy == null) {
            this.retryPolicy = clientStarter.getRetryPolicy();
        }
        return this.retryPolicy;
    }
}
