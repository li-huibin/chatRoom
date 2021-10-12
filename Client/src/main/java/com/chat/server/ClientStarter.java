package com.chat.server;

import com.chat.common.entity.Message;
import com.chat.common.handler.ProtostuffDecode;
import com.chat.common.handler.ProtostuffEncode;
import com.chat.server.handler.NettyClientHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.CharsetUtil;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.Scanner;

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
    /** 客户端需要一个事件循环组 */
    private NioEventLoopGroup group = null;

    private static ClientStarter client;
    /**
     * 创建客户端启动对象
     * 注意客户端使用的不是ServerBootstrap 而是Bootstrap
     */
    private Bootstrap bootstrap = null;

    public ClientStarter(String host,int port) {
        this.host = host;
        this.port = port;
        group = new NioEventLoopGroup();
        bootstrap = new Bootstrap();
    }

    public static ClientStarter build(String host, int port) {
        client = new ClientStarter(host,port);
        return client;
    }

    public static ClientStarter getClient() {
        return client;
    }

    public void init() {
        // 设置相关参数
        // 设置线程组
        this.bootstrap.group(group)
                // 使用NioSocketChannel作为客户端的通道实现
                .channel(NioSocketChannel.class)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        //加入处理器
                        ChannelPipeline pipeline = socketChannel.pipeline();
//                        pipeline.addLast("encoder", new StringEncoder());
//                        pipeline.addLast("decoder", new StringDecoder());
                        pipeline.addLast("decoder",new ProtostuffDecode());
                        pipeline.addLast("encoder",new ProtostuffEncode());
                        pipeline.addLast(new NettyClientHandler());
                    }
                });
        System.out.println("netty client start...");
    }

    public void connect() throws InterruptedException {
        try {
            // 启动客户端去链接服务器端
            ChannelFuture channelFuture = bootstrap.connect(this.host, this.port).sync();
            channelFuture.addListener(new GenericFutureListener<Future<? super Void>>() {

                @Override
                public void operationComplete(Future<? super Void> future)
                        throws Exception {
                    if (future.isSuccess()) {
                        System.out.println("客户端启动中...");
                    }
                    if (future.isDone()) {
                        System.out.println("客户端启动成功...OK！");
                        System.out.println("*******************************");
                    }
                }
            });
            Channel channel = channelFuture.channel();
            //获取channel
            Scanner scanner = new Scanner(System.in);
            while (scanner.hasNextLine()) {
                String str = scanner.nextLine();
                Message message = new Message(str.getBytes(CharsetUtil.UTF_8).length,str.getBytes(CharsetUtil.UTF_8));
                channel.writeAndFlush(message);
            }

            channelFuture.channel().closeFuture().sync();
            scanner.close();

        } finally {
            group.shutdownGracefully();
        }
    }


    public static void main(String[] args) throws InterruptedException {
        ClientStarter clientStarter = ClientStarter.build("127.0.0.1", 9000);
        clientStarter.init();
        clientStarter.connect();
    }
}
