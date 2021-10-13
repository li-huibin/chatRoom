package com.chat.server.handler;

import com.chat.common.context.ConstantPool;
import com.chat.server.ClientStarter;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.EventLoop;

import java.util.Random;
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
        retries = 0;
        // 设置channel为活跃状态
        ctx.fireChannelActive();
    }

    private RetryPolicy getRetryPolicy() {
        if (this.retryPolicy == null) {
            this.retryPolicy = clientStarter.getRetryPolicy();
        }
        return this.retryPolicy;
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) {
        if (retries >= ConstantPool.RECONNECT_TIMES) {
            System.out.println("Lost the connection with the server.");
            ctx.close();
            ctx.fireChannelInactive();
        }
        boolean allowRetry = getRetryPolicy().allowRetry(retries);
        if (allowRetry) {
            long sleepTimeMs = getRetryPolicy().getSleepTimeMs(retries);
            retries ++;
            System.out.println(String.format("Try to reconnect to the server after %dms. Retry count: %d.", sleepTimeMs, retries));
            EventLoop eventExecutors = ctx.channel().eventLoop();
            eventExecutors.schedule(new Runnable() {
                @Override
                public void run() {
                    try {
                        System.out.println("Reconnecting...");
                        clientStarter.connect();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                // 自动重连时间间隔后期应该改成区某个时间区间内的随机时间间隔(例如：[0,10]时间区间，new Random().nextInt(10);去0~10之间的随机数)，这样可以减少服务器重连压力
            }, ConstantPool.RECONNECT_TIME_DELAY, TimeUnit.SECONDS);
        }
    }
}
