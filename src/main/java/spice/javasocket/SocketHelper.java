package spice.javasocket;

import java.lang.Thread;
/**
 *
 */
public class SocketHelper {
    public static void log(String str) {
        System.out.println(System.nanoTime() + " - " + Thread.currentThread().getName() + " - " +str);
    }
}
