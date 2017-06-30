package net.felder;

import net.felder.keymapping.ix.model.Pair;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by bfelder on 6/30/17.
 */
public class PairTest {
    @Test
    public void testPairTwoWayEquality() {
        Pair<String, String> pair1 = new Pair("foo", "bar");
        Pair<String, String> pair2 = new Pair("bar", "foo");
        assertEquals(pair1, pair2);
    }
}
