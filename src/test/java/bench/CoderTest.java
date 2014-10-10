/*
 * Copyright (c) 2014. Thumbtack Technologies
 */

package bench;

import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;

public class CoderTest extends TestCase {
    private Coder coder;

    @Before
    public void setUp() throws Exception {
        coder = new Coder();
    }

    @Test
    public void testCharCode() throws Exception {
        assertEquals("b", coder.charCode(1));
        assertEquals("", coder.charCode(0));
        assertEquals("bb", coder.charCode(63));
        assertEquals("emjc", coder.charCode(1000000));
        coder.charCode(0);
        coder.charCode(1000000000);
    }

    public void testCharDecode() throws Exception {
        assertEquals(1, coder.charDecode("b"));
        assertEquals(63, coder.charDecode("bb"));
        assertEquals(1000000, coder.charDecode("emjc"));
        coder.charDecode("wiewie");
        coder.charDecode("Qwi9487");
    }
}