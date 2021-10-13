package com.chat.client;

import com.chat.client.handler.NettyServerHandler;
import com.chat.common.handler.ProtostuffDecode;
import com.chat.common.handler.ProtostuffEncode;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

import java.util.concurrent.TimeUnit;

/**
 * @Class ServerStarter
 * @Description 服务启动类
 * @Author lihuibin
 * @Date 2021/10/10 17:00
 * @Version 1.0
 * 更新记录:
 * 更新时间        更新人        更新位置        更新内容
 * 2021/10/10       lihuibin       新建           新建
 */
public class ServerStarter {

    /** 创建两个线程组bossGroup和workerGroup，含有的子线程NioEventLoop的个数默认为cpu核数的两倍 */
    /** bossGroup只是处理连接请求，真正的和客户端业务处理，会交给workerGroup完成 */
    private static EventLoopGroup bossGroup = null;
    private static EventLoopGroup workerGroup = null;
    /** 创建服务器端的启动对象 */
    private static ServerBootstrap bootstrap = null;
    private static String host = "127.0.0.1";
    private static int port = 9000;

    public void init() {
        bossGroup = new NioEventLoopGroup();
        workerGroup = new NioEventLoopGroup();
        bootstrap = new ServerBootstrap();
    }

    public static void close() {
        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();
    }

    public static void connect() throws InterruptedException {
        try {
            // 使用链式编程来配置参数
            // 设置两个线程组
            bootstrap.group(bossGroup, workerGroup)
                    // 使用NioServerSocketChannel作为服务器的通道实现
                    .channel(NioServerSocketChannel.class)
                    // 初始化服务器连接队列大小,服务端处理客户端连接请求是顺序处理的，所以同一时间只能处理一个客户端连接
                    // 多个客户端同时来的时候，服务端将不能处理的客户端连接请求放在队列中等待处理
                    .option(ChannelOption.SO_BACKLOG, 1024)
                    //让客户端保持长期活动状态
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    // 创建通道初始化对象，设置初始化参数
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            ChannelPipeline pipeline = socketChannel.pipeline();
//                            pipeline.addLast("encoder", new StringEncoder());
//                            pipeline.addLast("decoder", new StringDecoder());
                            pipeline.addLast("decoder",new ProtostuffDecode());
                            pipeline.addLast("encoder",new ProtostuffEncode());
                            // IdleStateHandler的参数readerIdleTime参数指定超过3秒还没有收到客户端连接
                            // 会触发IdleStateEvent事件并且交给下一个handler处理，下一个handler必须实现userEventTriggered方法处理对应事件
                            pipeline.addLast(new IdleStateHandler(3,0,0, TimeUnit.SECONDS));
                            // 对workerGroup的SocketChannel设置处理器
                            pipeline.addLast(new NettyServerHandler());
                        }
                    });
            System.out.println("netty server start....");

            // 绑定一个端口并且同步，生成了一个ChannelFuture异步对象，通过isDone()等方法可以判断异步事件的执行情况
            // 启动服务其（并绑定端口），bind是异步操作，sync方法是等待异步操作执行完毕
            ChannelFuture cf = bootstrap.bind(port).sync();
            // 给cf注册监听器，监听器我们关心的事件
            cf.addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture channelFuture) throws Exception {
                    if (channelFuture.isSuccess()) {
                        System.out.println("监听端口9000成功");
                    } else {
                        System.out.println("监听端口9000失败");
                    }
                }
            });
            // 对通道关闭进行监听，closeFuture是异步操作，监听通道关闭
            // 通过sync方法同步等待通道关闭处理完毕，这里会阻塞等待通道关闭完成
            cf.channel().closeFuture().sync();
            cf.addListener(new GenericFutureListener<Future<? super Void>>() {
                @Override
                public void operationComplete(Future<? super Void> future)
                        throws Exception {
                    if(future.isCancelled()){
                        System.out.println("服务器正在关闭..");
                    }
                    if(future.isCancellable()){
                        System.out.println("服务器已经关闭..OK");
                    }
                }
            });
        } finally {
            close();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        new ServerStarter().init();
        connect();
    }
}
