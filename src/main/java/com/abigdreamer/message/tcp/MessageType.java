package com.abigdreamer.message.tcp;

/**
 * 
 * @author Darkness
 * @date 2017年4月11日 下午3:38:08
 * @version 1.0
 * @since 1.0
 */
public enum MessageType {

	SERVICE_REQUEST((byte) 0), 
	SERVICE_RESPONSE((byte) 1), 
	ONE_WAY((byte) 2), 
	
	LOGIN_REQUEST((byte) 3), 
	LOGIN_RESPONSE((byte) 4), 
	
	HEARTBEAT_REQUEST((byte) 5), 
	HEARTBEAT_RESPONSE((byte) 6);

	private byte value;

	private MessageType(byte value) {
		this.value = value;
	}

	public byte value() {
		return this.value;
	}
}
