package com.abigdreamer.message.tcp.client;

import com.abigdreamer.message.tcp.struct.NettyMessage;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * 握手认证客户端
 * @author Darkness
 * @date 2017年4月11日 下午3:47:24
 * @version 1.0
 * @since 1.0
 */
@Sharable
public class NettyMessageHandler extends SimpleChannelInboundHandler<Object> {
	
	private ChannelHandlerContext ctx;
	
	public void sendMessage(NettyMessage message) {
		System.out.println("sendMessage:" + ctx + ", " + message);
		NettyClient.ctx.writeAndFlush(message);
	}

	/**
	 * Calls {@link ChannelHandlerContext#fireChannelActive()} to forward to the
	 * next {@link ChannelHandler} in the {@link ChannelPipeline}.
	 * 
	 * Sub-classes may override this method to change behavior.
	 */
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		System.out.println("NettyMessageHandler cannel active, ctx:" + ctx);
		this.ctx = ctx;
	}

	/**
	 * Calls {@link ChannelHandlerContext#fireChannelRead(Object)} to forward to
	 * the next {@link ChannelHandler} in the {@link ChannelPipeline}.
	 * 
	 * Sub-classes may override this method to change behavior.
	 */
	@Override
	public void channelRead0(ChannelHandlerContext ctx, Object message) throws Exception {
		System.out.println("init nettyMessageHandler ctx," + ctx);
		this.ctx = ctx;
		ctx.fireChannelRead(message);
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		ctx.fireExceptionCaught(cause);
	}
}
