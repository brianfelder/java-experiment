package net.felder.keymapping.ix.model;

import com.google.common.base.Objects;

/**
 * Created by bfelder on 6/30/17.
 * Pair, where the position of the elements matters for equality.
 */
public class OrderedPair<T1, T2> {
    private T1 item1;
    private T2 item2;

    public OrderedPair(T1 item1, T2 item2) {
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
     * In this OrderedPair, the position of the elements matters for equality.
     * @return
     */
    @Override
    public int hashCode(){
        int toReturn = Objects.hashCode(item1, item2);
        return toReturn;
    }

}
