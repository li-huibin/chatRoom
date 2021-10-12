package com.chat.common.handler;

import com.chat.common.entity.Message;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * @Program: chatRoom
 * @Description: 自定义编码器，解决拆包、粘包问题
 * @Author: LHB
 * @Version: v0.0.1
 * @Time: 2021-10-12 15:50
 **/
public class ProtostuffDecode extends ByteToMessageDecoder {
    private int length = 0;
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
//        System.out.println(in);
        // int为4个字节
        if (in.readableBytes() >= 4) {
            if (this.length == 0) {
                this.length = in.readInt();
//                System.out.println("in.readableBytes() = " + in.readableBytes());
//                System.out.println("[28]length = " + length);
            }
            if (in.readableBytes() < length) {
                System.out.println("当前数据接收不完整，等待数据发送完毕再接收……");
                return;
            }
            byte[] messageBuf = new byte[length];
            if (in.readableBytes() >= length) {
                in.readBytes(messageBuf);

                // 封装成Message对象，传递到下一个handler业务处理
                Message message = new Message();
                message.setLength(length);
                message.setMessage(messageBuf);
                out.add(message);
            }
            length = 0;
        }
    }
}
