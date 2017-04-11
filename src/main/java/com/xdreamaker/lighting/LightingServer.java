package com.xdreamaker.lighting;

import com.abigdreamer.message.tcp.server.NettyServer;

/**
 * @author Darkness
 * @date 2017年4月11日 下午4:33:28
 * @version 1.0
 * @since 1.0
 */
public class LightingServer extends NettyServer {

	public LightingServer(String ip, int port) {
		super(ip, port);
	}

	public static void main(String[] args) throws Exception {

		String serverHost = "127.0.0.1";
		int serverPort = 8080;

		new LightingServer(serverHost, serverPort).registerChannelHandler(new LightControlHandler()).start();
	}

}
