package com.abigdreamer.commons.util;

/**
 * 在Java中字节与十六进制的相互转换主要思想有两点：
	1、二进制字节转十六进制时，将字节高位与0xF0做"&"操作,然后再左移4位，得到字节高位的十六进制A;将字节低位与0x0F做"&"操作，得到低位的十六进制B，将两个十六进制数拼装到一块AB就是该字节的十六进制表示。
	2、十六进制转二进制字节时，将十六进制字符对应的十进制数字右移动4为，得到字节高位A;将字节低位的十六进制字符对应的十进制数字B与A做"|"运算，即可得到十六进制的二进制字节表示
 */
public class ByteUtil {
	
	private static String hexStr =  "0123456789ABCDEF";
	
	private static String[] binaryArray = {
								"0000","0001","0010","0011",
								"0100","0101","0110","0111",
								"1000","1001","1010","1011",
								"1100","1101","1110","1111"};
		
	/**
	 * 
	 * @param str
	 * @return 转换为二进制字符串
	 */
	public static String bytes2BinaryStr(byte[] bArray) {

		String outStr = "";
		int pos = 0;
		for (byte b : bArray) {
			// 高四位
			pos = (b & 0xF0) >> 4;
			outStr += binaryArray[pos];
			// 低四位
			pos = b & 0x0F;
			outStr += binaryArray[pos];
			outStr += " ";
		}
		return outStr;
	}
	
	/**
	 * 
	 * @param bytes
	 * @return 将二进制转换为十六进制字符输出
	 */
	public static String bytes2HexString(byte... bytes) {
		String result = "";
		String hex = "";
		for (int i = 0; i < bytes.length; i++) {
			// 字节高4位
			hex = String.valueOf(hexStr.charAt((bytes[i] & 0xF0) >> 4));
			// 字节低4位
			hex += String.valueOf(hexStr.charAt(bytes[i] & 0x0F));
			result += hex;
			if(i!=bytes.length-1){
				result += " ";
			}
		}
		return result;
	}
	
	/**
	 * 
	 * @param hexString
	 * @return 将十六进制转换为字节数组
	 */
	public static byte[] hexString2Bytes(String hexString) {
		hexString = hexString.toUpperCase();
		hexString = hexString.replaceAll(" ", "");
		// hexString的长度对2取整，作为bytes的长度
		int len = hexString.length() / 2;
		byte[] bytes = new byte[len];
		byte high = 0;// 字节高四位
		byte low = 0;// 字节低四位

		for (int i = 0; i < len; i++) {
			// 右移四位得到高位
			high = (byte) ((hexStr.indexOf(hexString.charAt(2 * i))) << 4);
			low = (byte) hexStr.indexOf(hexString.charAt(2 * i + 1));
			bytes[i] = (byte) (high | low);// 高地位做或运算
		}
		return bytes;
	}
	
	public static void main(String[] args) {
//		String str = "二进制与十六进制互转测试";
//		System.out.println("源字符串：\n"+str);
//		
//		String hexString = bytes2HexString(str.getBytes());
//		System.out.println("转换为十六进制：\n"+hexString);
//		System.out.println("转换为二进制：\n"+bytes2BinaryStr(str.getBytes()));
//		
//		byte [] bArray = hexString2Bytes(hexString);
//		System.out.println("将str的十六进制文件转换为二进制再转为String：\n"+new String(bArray));
//		
		
		String hexString = "01 05 00 01 FF 00 DD FA";
		byte[] bytes = hexString2Bytes(hexString);
		System.out.println(bytes2HexString(bytes));
	}
}

