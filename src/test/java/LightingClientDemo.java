import com.xdreamaker.lighting.LightingClient;

/**
 * @author Darkness
 * @date 2017年4月12日 上午10:24:31
 * @version 1.0
 * @since 1.0 
 */
public class LightingClientDemo {
	
	public static void main(String[] args) {
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

		String lightingId = "0181007005002";//018 1007 005 002	18巷道7列5层 二楼

		// 灯点亮
		client.openLight(lightingId);

		// 灯关闭
		client.closeLight(lightingId);
	}
}
