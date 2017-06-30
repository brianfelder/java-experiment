package net.felder.keymapping.ix.model;

import com.google.common.base.Objects;

/**
 * Created by bfelder on 6/30/17.
 */
public class Pair<T1, T2> {
    private T1 item1;
    private T2 item2;

    public Pair(T1 item1, T2 item2) {
        this.item1 = item1;
        this.item2 = item2;
    }

    public T1 getItem1() {
        return item1;
    }

    public T2 getItem2() {
        return item2;
    }

    @Override
    public boolean equals(Object other) {
        if (other == null || ! this.getClass().equals(other.getClass())) {
            return false;
        }
        return this.hashCode() == other.hashCode();
    }

    /**
     * We want the hashCode to be the same regardless which item is in which position.
     * @return
     */
    @Override
    public int hashCode(){
        int forwardHashCode = Objects.hashCode(item1, item2);
        int backwardHashCode = Objects.hashCode(item2, item1);
        int toReturn = forwardHashCode & backwardHashCode;
        return toReturn;
    }

}
