package com.xdreamaker.lighting;

import java.io.IOException;
import java.util.TooManyListenersException;

import com.abigdreamer.commons.util.ByteUtil;
import com.abigdreamer.message.serial.CRC16;
import com.abigdreamer.message.serial.HardwareSerialPort;

import gnu.io.NRSerialPort;

public class HardwareHarnessSerialPort extends HardwareSerialPort {

	public HardwareHarnessSerialPort(String port, int baudRate) {
		super(port, baudRate);
	}
	@Override
	public void onDataRecived(byte[] data) {
		super.onDataRecived(data);
		if(listener != null) {
			listener.onRecived(data);
		}
	}
	
	DataRecivedListener listener = null;
	
	public static interface DataRecivedListener {
		void onRecived(byte[] data);
	}
	
	public void sendLightingCommand(int cargoIndex, int cargoNo, boolean isOpen, DataRecivedListener listener) {
		
		String cargoIndexHex = String.format("%02x", cargoIndex);
		String cargoNoHex = String.format("%02x", cargoNo);
		String commandHex = isOpen ? "FF": "00";
		String hex = cargoIndexHex + " 05 00 "+ cargoNoHex +" "+commandHex+" 00";
		int crc = CRC16.calcCrc16(ByteUtil.hexString2Bytes(hex));
		String crc16 = String.format("%04x", crc);
		String rc16Result = crc16.substring(2, 4) + " " + crc16.substring(0, 2); 
		
		String sendData= hex + " " + rc16Result;
		System.out.println("[send data]" + sendData);
		this.sendData(sendData);
		
		this.listener = null;
		this.listener = listener;
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
	
	public static void main(String[] args) throws IOException, TooManyListenersException {
		for (String s : NRSerialPort.getAvailableSerialPorts()) {
			System.out.println("Availible port: " + s);
		}
		
		String port = "COM3";
		int baudRate = 115200;
		HardwareHarnessSerialPort hardwareSerialPort = new HardwareHarnessSerialPort(port, baudRate);
		hardwareSerialPort.connect();
		
		hardwareSerialPort.sendLightingCommand(1, 1, true, null);
		
		hardwareSerialPort.disconnect();
	}

}
