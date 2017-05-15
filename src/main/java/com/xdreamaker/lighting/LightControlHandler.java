package com.xdreamaker.lighting;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.bcel.generic.NEW;

import com.abigdreamer.ark.data.excel.ExcelReader;
import com.abigdreamer.ark.data.excel.factory.Excel2007Factory;
import com.abigdreamer.message.tcp.struct.NettyMessage;
import com.google.common.base.Joiner;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * 心跳应答消息处理
 * 
 * @author Darkness
 * @date 2017年4月11日 下午3:57:07
 * @version 1.0
 * @since 1.0
 */
@Sharable
public class LightControlHandler extends SimpleChannelInboundHandler<NettyMessage> {
	
	@Override
	public void channelRead0(ChannelHandlerContext ctx, NettyMessage message) throws Exception {
		if(message.getHeader() == null) {
			ctx.fireChannelRead(message);
			return;
		}
		System.out.println("on recived message, type: " + message.getHeader().getType() + ", body:" + message.getBody());
		if (message.getHeader().getType() == MessageType.OpenLight.value()
			|| message.getHeader().getType() == MessageType.CloseLight.value()) {
			System.out.println("Receive client lighting message : ---> " + message);
			System.out.println("body:" + message.getBody().toString());
			boolean isOpen = message.getHeader().getType() == MessageType.OpenLight.value();
			onMessage(isOpen, message.getBody().toString());
			
		} else {
			ctx.fireChannelRead(message);
		}
	}
	
	private Map<String, Integer> mapping = new HashMap<>();
	
	public LightControlHandler() {
		readExcel();
	}
	
	private void readExcel() {
		String filePath = System.getProperty("user.dir") + File.separator + "CargoNoModbusMapping.xlsx";
		File file = new File(filePath);
		ExcelReader readExcel = new Excel2007Factory().createExcelReader(file);
		try {
			readExcel.open();
		} catch (IOException e) {
			e.printStackTrace();
		}
		readExcel.setSheetNum(0); // 设置读取索引为0的工作表
		// 总行数
		int count = readExcel.getRowCount();
		
		mapping.clear(); 
		
		for (int i = 0; i <= count; i++) {
			String[] rows = readExcel.readExcelLine(i);
			if(rows != null) {
				System.out.println("[" + i + "]" + Joiner.on("  ").join(rows));
				try {
					mapping.put(rows[0], Integer.parseInt(rows[2]));
				} catch (NumberFormatException e) {
					e.printStackTrace();
					System.out.println("配置信息错误：" + rows[0] + "," + rows[1] + "," + rows[2]);
					System.out.println("尝试强制转换");
					try {
						mapping.put(rows[0], (int)Double.parseDouble(rows[2]));
						System.out.println("强制转换成功");
					} catch (NumberFormatException e1) {
						//e1.printStackTrace();
						System.out.println("强制转换失败，请检查配置");	
					}
				}
			}
		}
	}
	
	HardwareHarnessSerialPort hardwareSerialPort;
	int count=0;
	Object syncObject = new Object();
	
	public void onMessage(boolean isOpen, String lightingId) {
		if (hardwareSerialPort == null) {
			synchronized (syncObject) {
				String port = Config.get("serialPort");
				int baudRate = Config.getInt("baudRate");
				hardwareSerialPort = new HardwareHarnessSerialPort(port, baudRate);
				hardwareSerialPort.connect();	
			}
		}
		
		Integer cargoNo = mapping.get(lightingId);
		if(cargoNo != null) {
			int cargoIndex = Integer.parseInt(lightingId.substring(0, 3));
			hardwareSerialPort.sendLightingCommand(cargoIndex, cargoNo, isOpen, null);
		}
//		hardwareSerialPort.disconnect();
	}
	
//	public static void main(String[] args) {
//		new LightControlHandler().readExcel();
//	}
	
	public static void main(String[] args) {
		int value = (int)Double.parseDouble("8.0");
		System.out.println(value);
	}

}
