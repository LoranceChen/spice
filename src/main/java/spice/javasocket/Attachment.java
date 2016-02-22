package spice.javasocket;

import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;

/**
 *
 */
class Attachment {
    AsynchronousServerSocketChannel server;
    AsynchronousSocketChannel client;
    ByteBuffer buffer;
    SocketAddress clientAddr;
    boolean isRead;//what's means?

    public void printString(String position) {
        try {
            SocketHelper.log(position + " - hashcode - " + this.getClass().getName()+ "@" + Integer.toHexString(hashCode()));

            SocketHelper.log(position + " - server:AsynchronousServerSocketChannel - " + this.server);
            SocketHelper.log(position + " - server:AsynchronousServerSocketChannel - LocalAddress - " + this.server.getLocalAddress());
            SocketHelper.log(position + " - client:AsynchronousSocketChannel - " + this.client);
            //same as `client` parameter
            SocketHelper.log(position + " - client:AsynchronousSocketChannel - LocalAddress - " + ShowAddress(this.client, true));
            SocketHelper.log(position + " - client:AsynchronousSocketChannel - LocalAddress - " + ShowAddress(this.client, false));

            SocketHelper.log(position + " - buffer - " + this.buffer);
            SocketHelper.log(position + " - clientAddr - " + this.clientAddr);
            SocketHelper.log(position + " - isRead - " + this.isRead);
        } catch(Throwable t) {
            t.printStackTrace();
        }
    }

    private SocketAddress ShowAddress(AsynchronousSocketChannel channel, Boolean isLocal) {
        SocketAddress socketAddress = null;
        if(isLocal) {
            try {
                socketAddress = channel.getLocalAddress();
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }
        else {
            try {
                socketAddress = channel.getRemoteAddress();
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }
        return socketAddress;
    }
}
