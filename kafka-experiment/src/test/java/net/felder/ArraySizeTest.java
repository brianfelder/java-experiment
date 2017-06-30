package net.felder;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by bfelder on 6/29/17.
 */
public class ArraySizeTest {
    @Test
    public void testArrayLength() {
        String[] theArray = new String[10];
        assertEquals(10, theArray.length);
    }
}
