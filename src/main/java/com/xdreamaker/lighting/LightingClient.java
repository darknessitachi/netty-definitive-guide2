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

	public void openLight(int stationNo, int cargoNo) {
		NettyMessage message = new NettyMessage();
		Header header = new Header();
		header.setType(MessageType.OpenLight.value());
		message.setHeader(header);
		
		message.setBody("");
		
		sendMessage(message);
	}
	
	public void closeLight(int stationNo, int cargoNo) {
		NettyMessage message = new NettyMessage();
		Header header = new Header();
		header.setType(MessageType.CloseLight.value());
		message.setHeader(header);
		
		message.setBody("");
		
		sendMessage(message);
	}
	
	public static void main(String[] args) {
		//018 1007 005 002	18巷道7列5层 二楼

		// 服务器 ip
		String serverIp = "192.1658.0.100";
		// 服务器端口
		int serverPort = 8100;

		// 灯控制客户端
		String localHost = "127.0.0.1";
	    int localPort = 12088;
		LightingClient client = new LightingClient(localHost, localPort);
		// 连接服务器
		try {
			client.connect(serverIp, serverPort);
		} catch (Exception e) {// 连接失败，请检查服务器ip、端口号是否正确
			e.printStackTrace();
		}

		int stationNo = 1;// 站号

		int cargoNo = 15;// 仓位号

		// 灯点亮
		client.openLight(stationNo, cargoNo);

		// 灯关闭
		client.closeLight(stationNo, cargoNo);
	}

}
