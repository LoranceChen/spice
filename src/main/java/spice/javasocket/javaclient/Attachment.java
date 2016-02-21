package spice.javasocket.javaclient;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;

/**
 *
 */
class Attachment {
    AsynchronousSocketChannel channel;
    ByteBuffer buffer;
    Thread mainThread;
    boolean isRead;
}