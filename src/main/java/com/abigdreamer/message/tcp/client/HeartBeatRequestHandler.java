package com.abigdreamer.message.tcp.client;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import com.abigdreamer.message.tcp.MessageType;
import com.abigdreamer.message.tcp.struct.Header;
import com.abigdreamer.message.tcp.struct.NettyMessage;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * 心跳请求消息处理
 * 
 * @author Darkness
 * @date 2017年4月11日 下午3:55:10
 * @version 1.0
 * @since 1.0
 */
public class HeartBeatRequestHandler extends SimpleChannelInboundHandler<NettyMessage> {

	private volatile ScheduledFuture<?> heartBeat;

	@Override
	public void channelRead0(ChannelHandlerContext ctx, NettyMessage message) throws Exception {
		// 握手成功，主动发送心跳消息
		if (message.getHeader() != null && message.getHeader().getType() == MessageType.LOGIN_RESPONSE.value()) {
			heartBeat = ctx.executor().scheduleAtFixedRate(new HeartBeatTask(ctx), 0, 5000, TimeUnit.MILLISECONDS);
		} else if (message.getHeader() != null && message.getHeader().getType() == MessageType.HEARTBEAT_RESPONSE.value()) {
			System.out.println("Client receive server heart beat message : ---> " + message);
		} else {
			ctx.fireChannelRead(message);
		}
	}

	private class HeartBeatTask implements Runnable {
		private final ChannelHandlerContext ctx;

		public HeartBeatTask(final ChannelHandlerContext ctx) {
			this.ctx = ctx;
		}

		@Override
		public void run() {
			NettyMessage heatBeat = buildHeatBeat();
			System.out.println("Client send heart beat messsage to server : ---> " + heatBeat);
			ctx.writeAndFlush(heatBeat);
		}

		private NettyMessage buildHeatBeat() {
			NettyMessage message = new NettyMessage();
			Header header = new Header();
			header.setType(MessageType.HEARTBEAT_REQUEST.value());
			message.setHeader(header);
			return message;
		}
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		cause.printStackTrace();
		if (heartBeat != null) {
			heartBeat.cancel(true);
			heartBeat = null;
		}
		ctx.fireExceptionCaught(cause);
	}
}
