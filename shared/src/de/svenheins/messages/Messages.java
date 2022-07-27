package de.svenheins.messages;

import java.nio.ByteBuffer;

public abstract class Messages {
	/**
     * Create a READY message packet.  There is no payload associated with
     * a READY message.
     * @return A <code>ByteBuffer</code> "ready" packet
     */
    public static ByteBuffer createReadyPkt() {
        byte[] bytes = new byte[1];
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        buffer.put((byte)OPCODE.READY.ordinal());
        
        buffer.flip();
        return buffer;
    }
}
