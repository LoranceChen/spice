package spice.javasocket;


import java.nio.channels.AsynchronousSocketChannel;
import java.util.HashMap;

/**
 * manage all linked sockets and sockets access controller
 */
public class SocketPool {
    private static HashMap<String, AsynchronousSocketChannel> sockets = new HashMap<>();

    public static synchronized void put(AsynchronousSocketChannel socket) {
        String addressKey = null;
        try {
            addressKey = socket.getRemoteAddress().toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
//        if (addressKey == null)//throw
        sockets.put(addressKey, socket);
    }

    public static synchronized void remove(String key) {
        sockets.remove(key);
    }

    public static AsynchronousSocketChannel get(String key) {
        return sockets.get(key);
    }
}
