package spice.language.default_keyword;

/**
 * interface use default keyword define NO virtual method,
 * if same method used in multi-parent,
 * the abstract class must be override at subclass
 */
public class DefaultKeyword implements Interface1, Interface2 {
    @Override
    public int A() {
        return 0;
    }
}
