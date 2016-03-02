package spice.javasocket.javaclient;

import spice.javasocket.SocketHelper;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.concurrent.Future;

/**
 * TODO reuse ByteBuffer.allocate(2048); space rather then allocate on every input.
 */
public class ClientMain {
    public static void main(String[] args) throws Exception {
        AsynchronousSocketChannel channel = AsynchronousSocketChannel.open();
        SocketAddress serverAddr = new InetSocketAddress("localhost", 10001);

        Future<Void> result = channel.connect(serverAddr);
        result.get();

        SocketHelper.log("channel: AsynchronousSocketChannel - LocalAddress - " + channel.getLocalAddress());
        SocketHelper.log("channel: AsynchronousSocketChannel - RemoteAddress - " + channel.getRemoteAddress());

        System.out.println("Connected");
//        Attachment attach = new Attachment();
//        attach.channel = channel;
//        attach.buffer = ByteBuffer.allocate(2048);
//        attach.isRead = false;
//        attach.mainThread = Thread.currentThread();
//
//        Charset cs = Charset.forName("UTF-8");
//        String msg = "Hello";
//        byte[] data = msg.getBytes(cs);
//        attach.buffer.put(data);
//        attach.buffer.flip();
//
//        ReadWriteHandler readWriteHandler = new ReadWriteHandler();
//
//        //first param operate by write method, and it transfer to ReadWriteHandler by attach with completed method in ReadWriteHandler
//        channel.write(attach.buffer, attach, readWriteHandler);


//
        //simulate out environment send data to server
        while(true) {
            MyReadWriteHandler readWriteHandler = new MyReadWriteHandler();

            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            String accStr;

            Attachment attach = new Attachment();
            attach.channel = channel;
            attach.buffer = ByteBuffer.allocate(2048);
            attach.isRead = false;

            System.out.println("Enter your line to send server: ");
            accStr = br.readLine();
            byte[] data2 = accStr.getBytes();
            attach.buffer.put(data2);
            attach.buffer.flip();
            channel.write(attach.buffer, attach, readWriteHandler);
        }

//        attach.mainThread.join();
    }
}
