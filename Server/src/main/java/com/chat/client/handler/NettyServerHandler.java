package com.chat.client.handler;

import com.chat.common.entity.Message;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.CharsetUtil;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @Class NettyServerHandler
 * @Description 自定义Handler需要继承netty规定好的某个HandlerAdapter（规范）
 * @Author lihuibin
 * @Date 2021/10/10 17:55
 * @Version 1.0
 * 更新记录:
 * 更新时间        更新人        更新位置        更新内容
 * 2021/10/10       lihuibin       新建           新建
 */
@ChannelHandler.Sharable
public class NettyServerHandler extends SimpleChannelInboundHandler<Message> {

    // GlobalEventExcutor.INSTANCE是全局的时间执行器，是一个单例
    private static ChannelGroup channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    /**
     * 读取客户端发送的数据
     * @param ctx 上下文对象，含有通道channel，管道pipline
     * @param msg 就是客户端发送的数据
     * @throws Exception
     */
//    @Override
//    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
//        if (!channelGroup.isEmpty()) {
//            for (Channel channel : channelGroup) {
//                if (channel != ctx.channel()) {
//                    channel.writeAndFlush("【"+ ctx.channel().remoteAddress() +"】: " + msg + "\n");
//                } else {
//                    channel.writeAndFlush("【发送消息】: " + msg + "\n");
//                }
//            }
//        }
//    }

    /**
     * 读取客户端发送的数据
     * @param channelHandlerContext
     * @param msg
     * @throws Exception
     */
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Message msg) throws Exception {
        if (msg.getType() == 1) {
            String repMsg = "【心跳检测】: " + msg.getMessage();
            Message message = new Message((byte) 1,repMsg);
            channelHandlerContext.channel().writeAndFlush(message);
        } else {
            if (!channelGroup.isEmpty()) {
                for (Channel channel : channelGroup) {
                    if (channel != channelHandlerContext.channel()) {
                        String repMsg = "【" + channel.remoteAddress() + "】: " + msg.getMessage();
                        Message message = new Message(repMsg);
                        channel.writeAndFlush(message);
                    } else {
                        String repMsg = "【发送消息】: " + msg.getMessage();
                        Message message = new Message(repMsg);
                        channel.writeAndFlush(message);
                    }
                }
            }
        }
    }

    /**
     * 数据读取完毕处理方法
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
    }

    /**
     * 处理异常，一般是需要关闭通道
     * @param ctx
     * @param cause
     * @throws Exception
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println(ctx.channel().remoteAddress() + " 上线了");
        if (!channelGroup.isEmpty()) {
            String msg = "【"+ ctx.channel().remoteAddress() +"】 " + sdf.format(new Date(System.currentTimeMillis())) + " 上线了";
            channelGroup.writeAndFlush(new Message(msg));
        }
        channelGroup.add(ctx.channel());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        if (!channelGroup.isEmpty()) {
            channelGroup.remove(ctx.channel());
            String msg = ctx.channel().remoteAddress() + " 下线了";
            channelGroup.writeAndFlush(new Message(msg));
        }
    }

}
