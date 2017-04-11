package com.xdreamaker.lighting;

import com.abigdreamer.message.tcp.struct.NettyMessage;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * 心跳应答消息处理
 * 
 * @author Darkness
 * @date 2017年4月11日 下午3:57:07
 * @version 1.0
 * @since 1.0
 */
public class LightControlHandler extends SimpleChannelInboundHandler<NettyMessage> {
	
	@Override
	public void channelRead0(ChannelHandlerContext ctx, NettyMessage message) throws Exception {
		if(message.getHeader() == null) {
			ctx.fireChannelRead(message);
			return;
		}
		
		if (message.getHeader().getType() == MessageType.OpenLight.value()
			|| message.getHeader().getType() == MessageType.CloseLight.value()) {
			System.out.println("Receive client lighting message : ---> " + message);
			
		} else {
			ctx.fireChannelRead(message);
		}
	}

}
