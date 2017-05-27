package com.xdreamaker.lighting.test;
import com.xdreamaker.lighting.LightingClient;

/**
 * @author Darkness
 * @date 2017年4月12日 上午10:24:31
 * @version 1.0
 * @since 1.0 
 */
public class LightingClientDemo {
	
	public static void main(String[] args) {
		final String testIp = "127.0.0.1";//"192.168.1.10";//
		
		// 服务器 ip
		final String serverIp = testIp;//"192.168.1.10";
		// 服务器端口
		final int serverPort = 9028;

		// 灯控制客户端
		final String localHost = testIp;//"192.168.1.10";
		final int localPort = 12188;
		final LightingClient client = new LightingClient(localHost, localPort);
		
		System.out.println("before connect");
		new Thread(new Runnable() {

			@Override
			public void run() {
				// 连接服务器
				try {
					client.connect(serverIp, serverPort);
				} catch (Exception e) {// 连接失败，请检查服务器ip、端口号是否正确
					e.printStackTrace();
				}
			}
			
		}).start();
		
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		System.out.println("after connect");
		
		String lightingId5 = "0051006001002";//"0301001003000";//018 1007 005 002	18巷道7列5层 二楼
		String lightingId6 = "0061002005002";
		
		System.out.println("send before");
		
		for (int i = 0; i < 10000; i++) {
			String lightingId = i % 2 == 0 ? lightingId5 : lightingId6;
			// 灯点亮
			client.openLight(lightingId);

//			try {
//				Thread.sleep(10);
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
			// 灯关闭
			client.closeLight(lightingId);
		}
	}
}
