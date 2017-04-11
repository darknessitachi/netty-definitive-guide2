package com.abigdreamer.message.tcp.server;

import com.abigdreamer.message.tcp.MessageType;
import com.abigdreamer.message.tcp.struct.Header;
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
public class HeartBeatResponseHandler extends SimpleChannelInboundHandler<NettyMessage> {
	@Override
	public void channelRead0(ChannelHandlerContext ctx, NettyMessage message) throws Exception {
		// 返回心跳应答消息
		if (message.getHeader() != null && message.getHeader().getType() == MessageType.HEARTBEAT_REQUEST.value()) {
			System.out.println("Receive client heart beat message : ---> " + message);
			NettyMessage heartBeat = buildHeatBeat();
			System.out.println("Send heart beat response message to client : ---> " + heartBeat);
			ctx.writeAndFlush(heartBeat);
		} else {
			ctx.fireChannelRead(message);
		}
	}

	private NettyMessage buildHeatBeat() {
		NettyMessage message = new NettyMessage();
		Header header = new Header();
		header.setType(MessageType.HEARTBEAT_RESPONSE.value());
		message.setHeader(header);
		return message;
	}

}
