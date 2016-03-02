package spice.javasocket;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.CompletionHandler;
import java.nio.charset.Charset;

/**
 * used when socket read
 */
class ReadWriteHandler implements CompletionHandler<Integer, Attachment> {
    /**
     * callback by AsynchronousSocketChannel.read(newAttach.buffer, newAttach, rwHandler);
     * @param result normal read value is 5 , what's -1 means?
     * @param attach same as newAttach
     */
    @Override
    public void completed(Integer result, Attachment attach) {
        try {
            SocketHelper.log("ReadWriteHandler - classpath - " + getClass().getName());
            SocketHelper.log("ReadWriteHandler - `result`:Integer - " + result);
            SocketHelper.log("ReadWriteHandler - `attach`:Attachment -");
            attach.printString("ReadWriteHandler");
        } catch(Throwable t) {
            t.printStackTrace();
        }

        //what's -1 means?
        if (result == -1) {
            try {
                attach.client.close();
                SocketHelper.log("Stopped listening to the client - " + attach.clientAddr);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            return;
        }

        if (attach.isRead) {
            attach.buffer.flip();
            int limits = attach.buffer.limit();
            byte bytes[] = new byte[limits];
            attach.buffer.get(bytes, 0, limits);
            Charset cs = Charset.forName("UTF-8");
            String msg = new String(bytes, cs);
            SocketHelper.log("ReadWriteHandler - Client says: " + msg);
            attach.isRead = false;
            attach.buffer.rewind();

//            //send back
//            ReadWriteHandler rwHandler2 = new ReadWriteHandler();
//            Attachment newAttach2 = new Attachment();
//            newAttach2.server = attach.server;
//            newAttach2.client = attach.client;
//            newAttach2.buffer = catchCodingExp("back to you - " + msg);
//            newAttach2.isRead = true;
//            newAttach2.clientAddr = attach.clientAddr;
//
//            attach.client.write(newAttach2.buffer, newAttach2, rwHandler2);

        } else {
            //send back
            ReadWriteHandler rwHandler2 = new ReadWriteHandler();
            Attachment newAttach2 = new Attachment();
            newAttach2.server = attach.server;
            newAttach2.client = attach.client;
            newAttach2.buffer = catchCodingExp("back to you - " + "msg");
            newAttach2.isRead = true;
            newAttach2.clientAddr = attach.clientAddr;

            attach.client.write(newAttach2.buffer, newAttach2, rwHandler2);

            // Write to the client
            attach.client.write(attach.buffer, attach, this);
            attach.isRead = true;
            attach.buffer.clear();
            attach.client.read(attach.buffer, attach, this);
        }

        SocketHelper.log(this.getClass().getCanonicalName() + "ReadWriteHandler - attach.client.read");

        //ready read again
        ReadWriteHandler rwHandler = new ReadWriteHandler();

        Attachment newAttach = new Attachment();
        newAttach.server = attach.server;
        newAttach.client = attach.client;
        newAttach.buffer = ByteBuffer.allocate(2048);
        newAttach.isRead = true;
        newAttach.clientAddr = attach.clientAddr;

        attach.client.read(newAttach.buffer, newAttach, rwHandler);
    }

    @Override
    public void failed(Throwable e, Attachment attach) {
        e.printStackTrace();
    }

    private ByteBuffer catchCodingExp(String msg) {
        ByteBuffer bf = null;
        try {
            bf = ByteBuffer.wrap(msg.getBytes("UTF-8")); //ByteBuffer.allocate(2048);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return bf;
    }
}

