package p1;

/**
 *
 */
public interface Itf {
    void ha();
    default void HE(){
        System.out.println("default interface");
    }
}
