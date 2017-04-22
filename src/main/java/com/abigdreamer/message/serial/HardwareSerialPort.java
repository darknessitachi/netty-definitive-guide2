package com.abigdreamer.message.serial;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.TooManyListenersException;

import org.apache.commons.lang3.ArrayUtils;

import com.abigdreamer.commons.util.ByteUtil;

import gnu.io.NRSerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;

public class HardwareSerialPort {
	
	NRSerialPort serial;
	DataInputStream inputStream;
	DataOutputStream outs;
	
	public HardwareSerialPort(String port, int baudRate) {
		serial = new NRSerialPort(port, baudRate);
	}
	
	public void onDataRecived(byte[] data) {
		 System.out.println("recived:" + ByteUtil.bytes2HexString(data));
	}
	
	public void connect() {
		serial.connect();
		
		 inputStream = new DataInputStream(serial.getInputStream());
		 outs = new DataOutputStream(serial.getOutputStream());

		try {
			serial.addEventListener(new SerialReader(inputStream, this));
			serial.notifyOnDataAvailable(true);
		} catch (TooManyListenersException e) {
			e.printStackTrace();
		}
	}
	
	 public void disconnect() {
		serial.disconnect();
	}

	public void sendData(String data) {
		byte[] dataBytes = ByteUtil.hexString2Bytes(data);
		sendData(dataBytes);
	}
	
	
	
	public void sendData(byte[] data) {
		try {
			outs.write(data);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	
	public static class SerialReader implements SerialPortEventListener {
		 
		 private HardwareSerialPort serialPort;
	        private DataInputStream inStream;
	        private byte[] buffer = new byte[1024];

	        public SerialReader(DataInputStream inStream, HardwareSerialPort serialPort) {
	            this.inStream = inStream;
	            this.serialPort = serialPort;
	        }

	        @Override
	        public void serialEvent(SerialPortEvent event) {
	        	//定义用于缓存读入数据的数组
		        //记录已经到达串口COM21且未被读取的数据的字节（Byte）数。
		        int availableBytes = 0;
		        
		        int currentIndex = 0;
		        
		        //如果是数据可用的时间发送，则进行数据的读写
		            try {
		                availableBytes = inStream.available();
		                while(availableBytes > 0){
		                	
		                	int numBytes = inStream.read(buffer, currentIndex, availableBytes);
		                	
		                	currentIndex += numBytes;
//	                    for(int i = 0; i < cache.length && i < availableBytes; i++){
//	                        //解码并输出数据
//	                        System.out.print((char)cache[i]);
//	                    }
//		                    onDataRecived(data);
		                    
		                    availableBytes = inStream.available();
		                }
		                
		                byte[] data = ArrayUtils.subarray(buffer, 0, currentIndex);
	                    serialPort.onDataRecived(data);
		            } catch (IOException e) {
		                e.printStackTrace();
		            }
	        }
	    }
	
	public static void main(String[] args) throws IOException, TooManyListenersException {
		for (String s : NRSerialPort.getAvailableSerialPorts()) {
			System.out.println("Availible port: " + s);
		}
		String port = "/dev/tty.SLAB_USBtoUART";
		int baudRate = 115200;
		HardwareSerialPort hardwareSerialPort = new HardwareSerialPort(port, baudRate);
		hardwareSerialPort.connect();

		hardwareSerialPort.sendData("01 05 00 00 FF 00 8C 3A");
//		hardwareSerialPort.sendData("7E F3 D1");
		
		hardwareSerialPort.disconnect();
		
	}
}
