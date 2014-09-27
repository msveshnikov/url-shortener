package bench;

import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;

public class UrlShortenerTest {
    private UrlShortener urlShortener;

    @Before
    public void setUp() throws Exception {
        urlShortener = new UrlShortener();
    }

    @Test
    public void testMain() throws Exception {

    }

    @Test
    public void testRedirect() throws Exception {

    }

    @Test
    public void testShorten() throws Exception {

    }

    @Test
    public void testCharCode() throws Exception {
        assertEquals("b", urlShortener.charCode(1));
        assertEquals("", urlShortener.charCode(0));
        assertEquals("bb", urlShortener.charCode(63));
        assertEquals("emjc", urlShortener.charCode(1000000));
    }

    @Test
    public void testCharDecode() throws Exception {
        assertEquals(1, urlShortener.charDecode("b"));
        assertEquals(63, urlShortener.charDecode("bb"));
        assertEquals(1000000, urlShortener.charDecode("emjc"));
    }

    @Test
    public void testGetMax() throws Exception {

    }

    @Test
    public void testFindById() throws Exception {

    }

    @Test
    public void testGetJSON() throws Exception {

    }

    @Test
    public void testCreateView() throws Exception {

    }

    @Test
    public void testConnectCouch() throws Exception {

    }
}