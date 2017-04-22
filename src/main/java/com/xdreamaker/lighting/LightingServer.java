package com.xdreamaker.lighting;

import com.abigdreamer.message.tcp.server.NettyServer;

/**
 * com.xdreamaker.lighting.LightingServer
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
		String serverHost = Config.get("serverHost");
		int serverPort = Config.getInt("serverPort");
		
		new LightingServer(serverHost, serverPort).registerChannelHandler(new LightControlHandler()).start();
	}

}
