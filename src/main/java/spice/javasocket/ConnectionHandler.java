package spice.javasocket;

import java.io.IOException;
import java.net.SocketAddress;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

/**
 *
 */
class ConnectionHandler implements
        CompletionHandler<AsynchronousSocketChannel, Attachment> {

    /**
     * callback by AsynchronousServerSocketChannel.accept(attach, new ConnectionHandler());
     * @param client is connected client driver by AsynchronousServerSocketChannel,
     * @param attach same as attach in callback
     */
    @Override
    public void completed(AsynchronousSocketChannel client, Attachment attach) {
        //check parameter's value at entrance
        try {
            SocketHelper.log(this.getClass().getName());
            SocketHelper.log("ConnectionHandler - `client` - " + client);
            SocketHelper.log("ConnectionHandler - `client` - hashcode - " + client.hashCode());
            SocketHelper.log("ConnectionHandler - `client` - local address - " + client.getLocalAddress());
            SocketHelper.log("ConnectionHandler - `client` - remote address - " + client.getRemoteAddress());

            attach.printString("ConnectionHandler - `attach`");
        } catch(Throwable t) {
            t.printStackTrace();
        }
        try {
            SocketAddress clientAddr = client.getRemoteAddress();
            attach.server.accept(attach, this);
            ReadWriteHandler rwHandler = new ReadWriteHandler();
            Attachment newAttach = new Attachment();
            newAttach.server = attach.server;
            newAttach.client = client;
            newAttach.buffer = ByteBuffer.allocate(2048);
            newAttach.isRead = true;
            newAttach.clientAddr = clientAddr;
            //reading
            client.read(newAttach.buffer, newAttach, rwHandler);

            newAttach.printString("ConnectionHandler");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void failed(Throwable e, Attachment attach) {
        System.out.println("Failed to accept a  connection.");
        e.printStackTrace();
    }
}