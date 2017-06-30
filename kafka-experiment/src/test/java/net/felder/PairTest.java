package net.felder;

import net.felder.keymapping.ix.model.Pair;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

/**
 * Created by bfelder on 6/30/17.
 */
public class PairTest {
    @Test
    public void testEquality() {
        Pair<String, String> pair1 = new Pair("foo", "bar");
        Pair<String, String> pair1a = new Pair("foo", "bar");
        Pair<String, String> pair2 = new Pair("bar", "foo");
        assertEquals(pair1, pair1a);
        assertNotEquals(pair1, pair2);
    }
}
