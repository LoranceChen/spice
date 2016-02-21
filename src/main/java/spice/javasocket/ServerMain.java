package spice.javasocket;

import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.nio.channels.AsynchronousServerSocketChannel;

/**
 * 1.accpet and build socket
 * 2.
 */
public class ServerMain {
    public static void main(String[] args) throws Exception {
        AsynchronousServerSocketChannel server = AsynchronousServerSocketChannel.open();// www  .  ja va2 s  .c om
        String host = "localhost";
        int port = 8989;
        InetSocketAddress sAddr = new InetSocketAddress(host, port);
        server.bind(sAddr);
        System.out.format("Server is listening at %s%n", sAddr);
        Attachment attach = new Attachment();
        attach.server = server;

        //accept method returns immediately.
        server.accept(attach, new ConnectionHandler());
        Thread.currentThread().join();
    }

//    public AsynchronousServerSocketChannel start() {
////        AsynchronousServerSocketChannel
//
//    }
}