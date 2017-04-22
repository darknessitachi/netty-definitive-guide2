package com.abigdreamer.message.tcp.client;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.abigdreamer.message.tcp.codec.NettyMessageDecoder;
import com.abigdreamer.message.tcp.codec.NettyMessageEncoder;
import com.abigdreamer.message.tcp.struct.NettyMessage;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.ReadTimeoutHandler;

/**
 * 客户端
 * 
 * @author Darkness
 * @date 2017年4月11日 下午3:58:45
 * @version 1.0
 * @since 1.0
 */
public class NettyClient {

	private ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
	EventLoopGroup group = new NioEventLoopGroup();
	
	private String ip;
	private int port;
	
	public NettyClient(String ip, int port) {
		this.ip = ip;
		this.port = port;
	}
	
	public static ChannelHandlerContext ctx;
	
	private NettyMessageHandler messageHandler;
	
	public void connect(final String serverHost, final int serverPort) throws Exception {
		// 配置客户端NIO线程组
		try {
			Bootstrap b = new Bootstrap();
			
			b.group(group).channel(NioSocketChannel.class).option(ChannelOption.TCP_NODELAY, true)
					.handler(new ChannelInitializer<SocketChannel>() {
						@Override
						public void initChannel(SocketChannel ch) throws Exception {
							ch.pipeline().addLast(new NettyMessageDecoder(1024 * 1024, 4, 4));
							ch.pipeline().addLast("MessageEncoder", new NettyMessageEncoder());
							ch.pipeline().addLast("readTimeoutHandler", new ReadTimeoutHandler(50));
							ch.pipeline().addLast("LoginAuthHandler", new LoginAuthRequestHandler());
							ch.pipeline().addLast("HeartBeatHandler", new HeartBeatRequestHandler());
							
							messageHandler = new NettyMessageHandler();
							ch.pipeline().addLast(messageHandler);
						}
					});
			// 发起异步连接操作
			ChannelFuture future = b.connect(new InetSocketAddress(serverHost, serverPort),
					new InetSocketAddress(this.ip, this.port)).sync();
			future.channel().closeFuture().sync();
		} finally {
			// 所有资源释放完成之后，清空资源，再次发起重连操作
			executor.execute(new Runnable() {
				@Override
				public void run() {
					try {
						TimeUnit.SECONDS.sleep(1);
						connect(serverHost, serverPort);// 发起重连操作
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
		}
	}
	
	public void sendMessage(NettyMessage message) {
		messageHandler.sendMessage(message);
	}

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		String localHost = "127.0.0.1";
	    int localPort = 12088;
	    
	    String serverHost = "127.0.0.1";
	    int serverPort = 8080;
	    
	    NettyClient client = new NettyClient(localHost, localPort);
	    client.connect(serverHost, serverPort);
	}

}
