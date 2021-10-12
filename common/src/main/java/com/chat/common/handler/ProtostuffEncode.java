package com.chat.common.handler;

import com.chat.common.entity.Message;
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
        out.writeInt(msg.getLength());
        out.writeBytes(msg.getMessage());
    }
}
