package net.felder;

import net.felder.keymapping.ix.model.OrderedPair;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

/**
 * Created by bfelder on 6/30/17.
 */
public class OrderedPairTest {
    @Test
    public void testEquality() {
        OrderedPair<String, String> pair1 = new OrderedPair("foo", "bar");
        OrderedPair<String, String> pair1a = new OrderedPair("foo", "bar");
        OrderedPair<String, String> pair2 = new OrderedPair("bar", "foo");
        assertEquals(pair1, pair1a);
        assertNotEquals(pair1, pair2);
    }
}
