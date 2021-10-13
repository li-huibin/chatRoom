package com.chat.server;

import com.chat.common.entity.Message;
import com.chat.common.handler.ProtostuffDecode;
import com.chat.common.handler.ProtostuffEncode;
import com.chat.server.handler.ClientHandlerInitializer;
import com.chat.server.handler.ExponentBackOffRetry;
import com.chat.server.handler.NettyClientHandler;
import com.chat.server.handler.RetryPolicy;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

import java.util.Scanner;
import java.util.concurrent.TimeUnit;

/**
 * @Class ClientStarter
 * @Description 客户端启动类
 * @Author lihuibin
 * @Date 2021/10/10 18:07
 * @Version 1.0
 * 更新记录:
 * 更新时间        更新人        更新位置        更新内容
 * 2021/10/10       lihuibin       新建           新建
 */
public class ClientStarter {

    private String host = "";
    private int port = 0;

    // 重连策略
    private RetryPolicy retryPolicy;
    private NioEventLoopGroup group;
    private Channel channel;

    /**
     * 创建客户端启动对象
     * 注意客户端使用的不是ServerBootstrap 而是Bootstrap
     */
    private Bootstrap bootstrap = null;

    public ClientStarter(String host,int port) throws Exception {
        this(host,port,new ExponentBackOffRetry(3000,Integer.MAX_VALUE,60 * 1000));
    }

    public ClientStarter(String host, int port, RetryPolicy retryPolicy) throws Exception {
        this.host = host;
        this.port = port;
        this.retryPolicy = retryPolicy;
        init();
    }

    public RetryPolicy getRetryPolicy() {
        return this.retryPolicy;
    }

    public Channel getChannel() {
        return channel;
    }

    public void init() throws Exception {
        /** 客户端需要一个事件循环组 */
        this.group = new NioEventLoopGroup();
        bootstrap = new Bootstrap();
        // 设置相关参数
        // 设置线程组
        this.bootstrap.group(group)
                // 使用NioSocketChannel作为客户端的通道实现
                .channel(NioSocketChannel.class)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .handler(new ClientHandlerInitializer(ClientStarter.this));
        System.out.println("netty client start...");
    }

    public void connect() throws InterruptedException {
//        try {
            // 启动客户端去链接服务器端
            ChannelFuture channelFuture = bootstrap.connect(this.host, this.port).sync();
            channelFuture.addListener(getConnectionListener());
            this.channel = channelFuture.channel();
            //获取channel
            Scanner scanner = new Scanner(System.in);
            while (scanner.hasNextLine()) {
                String str = scanner.nextLine();
                Message message = new Message(str);
                channel.writeAndFlush(message);
            }
//            channelFuture.channel().writeAndFlush(new Message("sakjdfklsajfk"));
            channelFuture.channel().closeFuture().sync();
            scanner.close();

//        } finally {
//            group.shutdownGracefully();
//        }
    }


    private ChannelFutureListener getConnectionListener() {
        return new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                if (future.isSuccess()) {
                    System.out.println("客户端启动中...");
                }
                if (future.isDone()) {
                    System.out.println("客户端启动成功...OK！");
                    System.out.println("*******************************");
                }
                if (!future.isSuccess()) {
                    System.out.println("ssssssssssss");
                    future.channel().pipeline().fireChannelActive();
                }
            }
        };
    }

    public static void main(String[] args) throws Exception {
        ClientStarter clientStarter = new ClientStarter("127.0.0.1", 9000);
        clientStarter.connect();
    }
}
