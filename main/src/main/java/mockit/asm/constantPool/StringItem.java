package mockit.asm.constantPool;

import org.checkerframework.checker.index.qual.NonNegative;
import org.checkerframework.checker.nullness.qual.NonNull;

public final class StringItem extends Item {
    @NonNull
    @SuppressWarnings("NullableProblems")
    String strVal;

    StringItem() {
        super(0);
        strVal = "";
    }

    public StringItem(@NonNegative int index, int type, @NonNull String strVal) {
        super(index);
        set(type, strVal);
    }

    StringItem(@NonNegative int index, @NonNull StringItem item) {
        super(index, item);
        strVal = item.strVal;
    }

    @NonNull
    public String getValue() {
        return strVal;
    }

    /**
     * Sets this string item value.
     */
    void set(int type, @NonNull String strVal) {
        this.type = type;
        this.strVal = strVal;
        setHashCode(strVal.hashCode());
    }

    @Override
    boolean isEqualTo(@NonNull Item item) {
        return ((StringItem) item).strVal.equals(strVal);
    }
}
