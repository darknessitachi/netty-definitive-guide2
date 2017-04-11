package com.abigdreamer.message.tcp.server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.abigdreamer.message.tcp.codec.NettyMessageDecoder;
import com.abigdreamer.message.tcp.codec.NettyMessageEncoder;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.ReadTimeoutHandler;

/**
 * 服务器
 * 
 * @author Darkness
 * @date 2017年4月11日 下午4:02:12
 * @version 1.0
 * @since 1.0
 */
public class NettyServer {
	
	private String host;
	private int port;
	
	private boolean enableWhiteList;
	private List<ChannelHandler> channelHandlers;
	
	public NettyServer(String host, int port) {
		this(host, port, false);
	}
	
	public NettyServer(String host, int port, boolean enableWhiteList) {
		this.host = host;
		this.port = port;
		this.enableWhiteList = enableWhiteList;
		
		this.channelHandlers = new ArrayList<>();
	}

	public NettyServer registerChannelHandler(ChannelHandler handler) {
		this.channelHandlers.add(handler);
		return this;
	}
	
	public void start() throws Exception {
		// 配置服务端的NIO线程组
		EventLoopGroup bossGroup = new NioEventLoopGroup();
		EventLoopGroup workerGroup = new NioEventLoopGroup();
		ServerBootstrap b = new ServerBootstrap();
		b.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class).option(ChannelOption.SO_BACKLOG, 100)
				.handler(new LoggingHandler(LogLevel.INFO)).childHandler(new ChannelInitializer<SocketChannel>() {
					@Override
					public void initChannel(SocketChannel ch) throws IOException {
						ch.pipeline().addLast(new NettyMessageDecoder(1024 * 1024, 4, 4));
						ch.pipeline().addLast(new NettyMessageEncoder());
						ch.pipeline().addLast("readTimeoutHandler", new ReadTimeoutHandler(50));
						ch.pipeline().addLast(new LoginAuthResponseHandler(enableWhiteList));
						ch.pipeline().addLast("HeartBeatHandler", new HeartBeatResponseHandler());
						
						for (ChannelHandler channelHandler : channelHandlers) {
							ch.pipeline().addLast(channelHandler);
						}
					}
				});

		// 绑定端口，同步等待成功
		b.bind(host, port).sync();
		System.out.println("Netty server start ok : " + (host + " : " + port));
	}

	public static void main(String[] args) throws Exception {
		
		String serverHost = "127.0.0.1";
		int serverPort = 8080;
  
		new NettyServer(serverHost, serverPort).start();
	}
}
