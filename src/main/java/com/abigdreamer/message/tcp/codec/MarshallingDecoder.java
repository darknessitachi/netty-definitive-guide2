package com.abigdreamer.message.tcp.codec;

import io.netty.buffer.ByteBuf;

import java.io.IOException;
import java.io.StreamCorruptedException;

import org.jboss.marshalling.ByteInput;
import org.jboss.marshalling.Unmarshaller;

/**
 * 
 * @author Darkness
 * @date 2017年4月11日 下午4:05:03
 * @version 1.0
 * @since 1.0
 */
public class MarshallingDecoder {

	private final Unmarshaller unmarshaller;

	/**
	 * Creates a new decoder whose maximum object size is {@code 1048576} bytes.
	 * If the size of the received object is greater than {@code 1048576} bytes,
	 * a {@link StreamCorruptedException} will be raised.
	 * 
	 * @throws IOException
	 * 
	 */
	public MarshallingDecoder() throws IOException {
		unmarshaller = MarshallingCodecFactory.buildUnMarshalling();
	}

	protected Object decode(ByteBuf in) throws Exception {
		int objectSize = in.readInt();
		ByteBuf buf = in.slice(in.readerIndex(), objectSize);
		ByteInput input = new ChannelBufferByteInput(buf);
		try {
			unmarshaller.start(input);
			Object obj = unmarshaller.readObject();
			unmarshaller.finish();
			in.readerIndex(in.readerIndex() + objectSize);
			return obj;
		} finally {
			unmarshaller.close();
		}
	}
}
