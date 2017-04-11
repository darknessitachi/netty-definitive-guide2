package com.abigdreamer.message.tcp.client;

import com.abigdreamer.message.tcp.MessageType;
import com.abigdreamer.message.tcp.struct.Header;
import com.abigdreamer.message.tcp.struct.NettyMessage;

import io.netty.channel.ChannelHandler;
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
public class LoginAuthRequestHandler extends SimpleChannelInboundHandler<NettyMessage> {

	/**
	 * Calls {@link ChannelHandlerContext#fireChannelActive()} to forward to the
	 * next {@link ChannelHandler} in the {@link ChannelPipeline}.
	 * 
	 * Sub-classes may override this method to change behavior.
	 */
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		ctx.writeAndFlush(buildLoginRequest());
	}

	/**
	 * Calls {@link ChannelHandlerContext#fireChannelRead(Object)} to forward to
	 * the next {@link ChannelHandler} in the {@link ChannelPipeline}.
	 * 
	 * Sub-classes may override this method to change behavior.
	 */
	@Override
	public void channelRead0(ChannelHandlerContext ctx, NettyMessage message) throws Exception {
		// 如果是握手应答消息，需要判断是否认证成功
		if (message.getHeader() != null && message.getHeader().getType() == MessageType.LOGIN_RESPONSE.value()) {
			byte loginResult = (byte) message.getBody();
			if (loginResult != (byte) 0) {
				// 握手失败，关闭连接
				ctx.close();
			} else {
				System.out.println("Login is ok : " + message);
				ctx.fireChannelRead(message);
			}
		} else {
			ctx.fireChannelRead(message);
		}
	}

	private NettyMessage buildLoginRequest() {
		NettyMessage message = new NettyMessage();
		Header header = new Header();
		header.setType(MessageType.LOGIN_REQUEST.value());
		message.setHeader(header);
		return message;
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		ctx.fireExceptionCaught(cause);
	}
}
