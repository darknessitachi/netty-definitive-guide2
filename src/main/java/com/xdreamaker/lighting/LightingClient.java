package com.xdreamaker.lighting;

import com.abigdreamer.message.tcp.client.NettyClient;
import com.abigdreamer.message.tcp.struct.Header;
import com.abigdreamer.message.tcp.struct.NettyMessage;

/**
 * @author Darkness
 * @date 2017年4月11日 下午4:33:28
 * @version 1.0
 * @since 1.0 
 */
public class LightingClient extends NettyClient {

	public LightingClient(String ip, int port) {
		super(ip, port);
	}

	public void openLight(String lightingId) {
		NettyMessage message = new NettyMessage();
		Header header = new Header();
		header.setType(MessageType.OpenLight.value());
		message.setHeader(header);
		
		message.setBody(lightingId);
		
		sendMessage(message);
	}
	
	public void closeLight(String lightingId) {
		NettyMessage message = new NettyMessage();
		Header header = new Header();
		header.setType(MessageType.CloseLight.value());
		message.setHeader(header);
		
		message.setBody(lightingId);
		
		sendMessage(message);
	}
	
}
