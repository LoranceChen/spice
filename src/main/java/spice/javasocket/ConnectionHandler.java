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
    @Override
    public void completed(AsynchronousSocketChannel client, Attachment attach) {
        try {
            SocketAddress clientAddr = client.getRemoteAddress();
            System.out.format("Accepted a connection from  %s%n", clientAddr);
            attach.server.accept(attach, this);
            ReadWriteHandler rwHandler = new ReadWriteHandler();
            Attachment newAttach = new Attachment();
            newAttach.server = attach.server;
            AsynchronousServerSocketChannel server = attach.server;

//            /**
//             * tst
//             */
//            Boolean b = server.getOption(StandardSocketOptions.SO_BROADCAST);
//            Object x = server.getOption(StandardSocketOptions.SO_KEEPALIVE);
//            Object x2 = server.getOption(StandardSocketOptions.SO_LINGER);
//            Object x3 = server.getOption(StandardSocketOptions.SO_RCVBUF);
//            Object x4 = server.getOption(StandardSocketOptions.SO_REUSEADDR);
//            int s = server.getOption(StandardSocketOptions.SO_SNDBUF);
//            Object x5 = server.setOption(StandardSocketOptions.SO_SNDBUF,s+1024);

            newAttach.client = client;
            newAttach.buffer = ByteBuffer.allocate(2048);
            newAttach.isRead = true;
            newAttach.clientAddr = clientAddr;
            System.out.format("begin - client.read(newAttach.buffer, newAttach, rwHandler);");
            client.read(newAttach.buffer, newAttach, rwHandler);
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