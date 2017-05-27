package com.xdreamaker.lighting;

import java.io.IOException;
import java.util.TooManyListenersException;
import java.util.concurrent.atomic.AtomicInteger;

import com.abigdreamer.commons.util.ByteUtil;
import com.abigdreamer.message.serial.CRC16;
import com.abigdreamer.message.serial.HardwareSerialPort;

import gnu.io.NRSerialPort;

public class HardwareHarnessSerialPort extends HardwareSerialPort {

	public HardwareHarnessSerialPort(String port, int baudRate) {
		super(port, baudRate);
	}
	
	public void sendLightingCommand(int cargoIndex, int cargoNo, boolean isOpen) {
		
		String cargoIndexHex = String.format("%02x", cargoIndex);
		String cargoNoHex = String.format("%02x", cargoNo);
		String commandHex = isOpen ? "FF": "00";
		String hex = cargoIndexHex + " 05 00 "+ cargoNoHex +" "+commandHex+" 00";
		int crc = CRC16.calcCrc16(ByteUtil.hexString2Bytes(hex));
		String crc16 = String.format("%04x", crc);
		String rc16Result = crc16.substring(2, 4) + " " + crc16.substring(0, 2); 
		
		String sendData= hex + " " + rc16Result;
		System.out.println("[send data]" + sendData);
		this.sendData(sendData, true);
	}
	
	/** 
     * 将byte转换为一个长度为8的byte数组，数组每个值代表bit 
     */  
    public static byte[] getBooleanArray(byte b) {  
        byte[] array = new byte[8];  
        for (int i = 7; i >= 0; i--) {  
            array[i] = (byte)(b & 1);  
            b = (byte) (b >> 1);  
        }  
        return array;  
    }
	
	public static void main(String[] args) throws IOException, TooManyListenersException, InterruptedException {
		for (String s : NRSerialPort.getAvailableSerialPorts()) {
			System.out.println("Availible port: " + s);
		}
		
		String port = "COM3";
		int baudRate = 9600;
		HardwareHarnessSerialPort hardwareSerialPort = new HardwareHarnessSerialPort(port, baudRate);
		hardwareSerialPort.connect();
		
		final AtomicInteger atomicInteger = new AtomicInteger();
		final LongValue allCost = new LongValue(0);
		
		hardwareSerialPort.addListener(new DataRecivedListener() {
			@Override
			public void onRecived(byte[] data, long cost) {
				allCost.add(cost);
				atomicInteger.getAndIncrement();
				
//				if(atomicInteger.get()%10==0) {
////					System.out.println(allCost.get());
////					System.out.println(atomicInteger.get());
//					long avgCost = allCost.get()/(atomicInteger.get()+0L);
////					System.out.println(atomicInteger.get()%10==0);
////					System.out.println(avgCost);
////					System.out.println("cost:" + allCost.get()/atomicInteger.get());
//					System.out.println("recived["+atomicInteger.get()+"], avgCost: "+avgCost);	
//				}
				
				System.out.println("recived["+atomicInteger.get()+"], cost: "+cost+",message:" + ByteUtil.bytes2HexString(data));	
				//hardwareSerialPort.disconnect();
			}
		});
		
		for (int i = 0; i < 10; i++) {
//			Thread.sleep(500);
			hardwareSerialPort.sendLightingCommand(30, 11, true);
		}
//		hardwareSerialPort.disconnect();
	}

}
