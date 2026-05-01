package einstein.armortrimitemfix;

public class ItemLayerKeyContext {
    private static final ThreadLocal<Integer> LAYER_INDEX = ThreadLocal.withInitial(() -> 0);

    public static void set(int index) { LAYER_INDEX.set(index); }
    public static int get() { return LAYER_INDEX.get(); }
    public static void remove() { LAYER_INDEX.remove(); }
}
