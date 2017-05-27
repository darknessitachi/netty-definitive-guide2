package com.xdreamaker.lighting;

import java.util.concurrent.atomic.AtomicInteger;

import com.abigdreamer.commons.util.ByteUtil;
import com.abigdreamer.message.serial.HardwareSerialPort.DataRecivedListener;
import com.abigdreamer.message.tcp.struct.NettyMessage;

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
	
	public LightControlHandler() {
		LightingConfig.readExcel();
	}
	
	HardwareHarnessSerialPort hardwareSerialPort;
	int count=0;
	Object syncObject = new Object();
	
	public void onMessage(boolean isOpen, String lightingId) {
		if (hardwareSerialPort == null) {
			synchronized (syncObject) {
				if(hardwareSerialPort == null) {
					String port = Config.get("serialPort");
					int baudRate = Config.getInt("baudRate");
					hardwareSerialPort = new HardwareHarnessSerialPort(port, baudRate);
					hardwareSerialPort.connect();	
					
					final AtomicInteger atomicInteger = new AtomicInteger();
					final LongValue allCost = new LongValue(0);
					
					hardwareSerialPort.addListener(new DataRecivedListener() {
						@Override
						public void onRecived(byte[] data, long cost) {
							allCost.add(cost);
							atomicInteger.getAndIncrement();
							
	//						if(atomicInteger.get()%10==0) {
	////							System.out.println(allCost.get());
	////							System.out.println(atomicInteger.get());
	//							long avgCost = allCost.get()/(atomicInteger.get()+0L);
	////							System.out.println(atomicInteger.get()%10==0);
	////							System.out.println(avgCost);
	////							System.out.println("cost:" + allCost.get()/atomicInteger.get());
	//							System.out.println("recived["+atomicInteger.get()+"], avgCost: "+avgCost);	
	//						}
							
							System.out.println("recived["+atomicInteger.get()+"], cost: "+cost+",message:" + ByteUtil.bytes2HexString(data));	
							//hardwareSerialPort.disconnect();
						}
					});
				}
			}
		}
		
		Integer cargoNo = LightingConfig.getCargoNo(lightingId);
		if(cargoNo != null) {
			int cargoIndex = Integer.parseInt(lightingId.substring(0, 3));
			hardwareSerialPort.sendLightingCommand(cargoIndex, cargoNo, isOpen);
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
