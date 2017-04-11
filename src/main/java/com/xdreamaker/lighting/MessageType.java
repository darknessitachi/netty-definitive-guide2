package com.xdreamaker.lighting;

/**
 * 
 * @author Darkness
 * @date 2017年4月11日 下午3:38:08
 * @version 1.0
 * @since 1.0
 */
public enum MessageType {
	// 0-50为系统保留消息类型
	OpenLight((byte) 51), 
	CloseLight((byte) 52);

	private byte value;

	private MessageType(byte value) {
		this.value = value;
	}

	public byte value() {
		return this.value;
	}
}
