package com.chat.common.handler;

import com.chat.common.entity.Message;
import com.chat.common.protostuff.ProtostuffUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * @Program: chatRoom
 * @Description: 自定义解码器
 * @Author: LHB
 * @Version: v0.0.1
 * @Time: 2021-10-12 15:45
 **/
public class ProtostuffEncode extends MessageToByteEncoder<Message> {
    @Override
    protected void encode(ChannelHandlerContext ctx, Message msg, ByteBuf out) throws Exception {
//        System.out.println("编码 = " + msg.toString());
        byte[] serializer = ProtostuffUtil.serializer(msg);
        // 包长度，用来解决拆包粘包问题
        out.writeInt(serializer.length);
        out.writeBytes(serializer);
    }
}
