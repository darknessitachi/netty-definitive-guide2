package com.abigdreamer.message.serial;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Queue;
import java.util.TooManyListenersException;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.lang3.ArrayUtils;

import com.abigdreamer.ark.framework.collection.TwoTuple;
import com.abigdreamer.commons.util.ByteUtil;
import com.xdreamaker.lighting.LongValue;

import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;

public class HardwareSerialPort {
	
	public static interface DataRecivedListener {
		void onRecived(byte[] data, long cost);
	}
	
	NRSerialPort serial;
	DataInputStream inputStream;
	DataOutputStream outs;
	DataRecivedListener listener = null;
	private static AtomicInteger count = new AtomicInteger();
	
	private LongValue singleStart = new LongValue();
	
	private boolean isTimeOut() {
		singleCost = System.currentTimeMillis() - singleStart.get();
		if(singleCost > 1000) {
			return true;
		}
		return false;
	}
	
	public HardwareSerialPort(String port, int baudRate) {
		serial = new NRSerialPort(port, baudRate);
		
		executorService.execute(new Runnable() {
			
			@Override
			public void run() {
//				long start = 0;
				
				while(true) {
					
//					System.out.println("count:" + count.get());
					if(count.get() != 0) {
						if(isTimeOut()) {
							count.decrementAndGet();
							if(count.get()<0) {
								count.set(0);
							}
						} else {
							try {
								Thread.sleep(10);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
							continue;
						}
					}
					
					
					TwoTuple<String, Boolean>  info = queue.poll();
					if(info != null) {
						boolean needLock = info.second;
						if(needLock) {
							count.incrementAndGet();
						}
						
						try {
							Thread.sleep(10);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						
//						if(start == 0) {
//							start = System.currentTimeMillis();
//						}
						
						byte[] dataBytes = ByteUtil.hexString2Bytes(info.first);
						sendDataToSerialPort(dataBytes);
						
//						System.out.println("cost:" + (System.currentTimeMillis() - start) + " ms");
						
//						start = System.currentTimeMillis();
					}
				}
			}
		});
	}
	
	public void addListener(DataRecivedListener listener) {
		this.listener = listener;
	}
	
	byte[] fullData = new byte[8];
	long singleCost;
	
	public void onDataRecived(byte[] data, long cost) {
//		cost = System.currentTimeMillis() - start;
		singleCost = System.currentTimeMillis() - singleStart.get();
		
		if(listener != null) {
			listener.onRecived(data, singleCost);
		} else {
			System.out.println("recived:" + ByteUtil.bytes2HexString(data));	
		}
		
		count.decrementAndGet();
		if(count.get()<0) {
			count.set(0);
		}
	}
	 
	public void connect() {
		serial.connect();
		
		 inputStream = new DataInputStream(serial.getInputStream());
		 outs = new DataOutputStream(serial.getOutputStream());

		try {
			serial.addEventListener(new SerialReader(inputStream, this));
			serial.notifyOnDataAvailable(true);
		} catch (TooManyListenersException e) {
			throw new RuntimeException(e.getMessage());
		}
	}
	
	 public void disconnect() {
		serial.disconnect();
	}
	 
	private Queue<TwoTuple<String, Boolean>> queue = new ConcurrentLinkedQueue<>();
	 
	 public void sendData(String data) {
		 sendData(data, false);
	 }
	
	public void sendData(String data, boolean needLock) {
		queue.offer(new TwoTuple<String, Boolean>(data, needLock));
	}
	
	private ExecutorService executorService = Executors.newSingleThreadExecutor();
	
	private void sendDataToSerialPort(byte[] data) {
//		System.out.println(LocalDateTime.now() + " " + ByteUtil.bytes2HexString(data));
		singleStart.set(System.currentTimeMillis());;
		try {
			outs.write(data);
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}
	}
	
	public static class SerialReader implements SerialPortEventListener {

		private HardwareSerialPort serialPort;
		private DataInputStream inStream;
		private byte[] buffer = new byte[1024];

		private long start = System.currentTimeMillis();
		
		public SerialReader(DataInputStream inStream, HardwareSerialPort serialPort) {
			this.inStream = inStream;
			this.serialPort = serialPort;
		}
		int currentIndex = 0;
		@Override
		public void serialEvent(SerialPortEvent event) {
			// 定义用于缓存读入数据的数组
			// 记录已经到达串口COM21且未被读取的数据的字节（Byte）数。
			int availableBytes = 0;

			// 如果是数据可用的时间发送，则进行数据的读写
			try {
				availableBytes = inStream.available();
				while (availableBytes > 0) {
					int numBytes = inStream.read(buffer, currentIndex, availableBytes);

					currentIndex += numBytes;
					// for(int i = 0; i < cache.length && i < availableBytes;
					// i++){
					// //解码并输出数据
					// System.out.print((char)cache[i]);
					// }
					// onDataRecived(data);

					availableBytes = inStream.available();
				}
//				System.out.println(currentIndex);
				byte[] data = ArrayUtils.subarray(buffer, 0, currentIndex);
				
				if(currentIndex == 8) {
					currentIndex = 0;
					serialPort.onDataRecived(data, System.currentTimeMillis() -start);
					
					start = System.currentTimeMillis();
				}
			} catch (IOException e) {
				throw new RuntimeException(e.getMessage());
			}
		}
	}
	
	public static void main(String[] args) throws IOException, TooManyListenersException, InterruptedException {
		for (String s : NRSerialPort.getAvailableSerialPorts()) {
			System.out.println("Availible port: " + s);
		}
		String port = "COM3";//"/dev/tty.SLAB_USBtoUART";
		int baudRate = 9600;
		final HardwareSerialPort hardwareSerialPort = new HardwareSerialPort(port, baudRate);
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
		
		Thread.sleep(1000);
		
		String data = "";
		for (int i = 0; i < 100; i++) {
			data = "06 05 00 01 FF 00 dc 4d";
			if(i==3) {
				data = "07 05 00 01 FF 00 dd 9c";
			} 
			if(i==4) {
				data = "07 05 00 01 FF 00 dd 9c";
			} 
			if(i==5) {
				data = "07 05 00 01 FF 00 dd 9c";
			} 
//			System.out.println(data);
//			data = "06 05 00 01 FF 00 dc 4d";
			hardwareSerialPort.sendData(data, true);
		}
		
	}
}
