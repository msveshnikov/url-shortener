package bench;

import net.sf.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.core.Response;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

public class UrlShortenerTest {
    private UrlShortener urlShortener;

    @Before
    public void setUp() throws Exception {
        urlShortener = new UrlShortener();
        urlShortener.connectCouch();
    }

    @Test
    public void testMain() throws Exception {
        assertEquals(500, urlShortener.Main("abcd", "", null).getStatus());
        Response shorten = urlShortener.Main("shorten", "www.mail.ru", null);
        assertEquals(200, shorten.getStatus());
        String s = (String) shorten.getEntity();
        assertTrue(s.length() > 3);
        Response redir = urlShortener.Main(s.substring(1, s.length()), "", null);
        assertEquals(301, redir.getStatus());
        assertEquals("http://www.mail.ru", redir.getMetadata().getFirst("Location").toString());
    }

    @Test
    public void testRedirect() throws Exception {

    }

    @Test
    public void testShorten() throws Exception {
        Response shorten = urlShortener.shorten("www.mail.ru", "");
        assertEquals(200, shorten.getStatus());
    }

    @Test
    public void testCharCode() throws Exception {
        assertEquals("b", urlShortener.charCode(1));
        assertEquals("", urlShortener.charCode(0));
        assertEquals("bb", urlShortener.charCode(63));
        assertEquals("emjc", urlShortener.charCode(1000000));
        urlShortener.charCode(0);
        urlShortener.charCode(1000000000);
    }

    @Test
    public void testCharDecode() throws Exception {
        assertEquals(1, urlShortener.charDecode("b"));
        assertEquals(63, urlShortener.charDecode("bb"));
        assertEquals(1000000, urlShortener.charDecode("emjc"));
        urlShortener.charDecode("wiewie");
        urlShortener.charDecode("Qwi9487");
    }

    @Test
    public void testGetMax() throws Exception {
        int max = urlShortener.getMax();
        assertTrue(max > 100);
        JSONObject d = urlShortener.findById(max);
        String longUrl = d.getString("long");
        d.getString("short");
        assertEquals(max, d.getInt("myid"));
    }

    @Test(expected = Exception.class)
    public void testFindById() throws Exception {
        JSONObject d = urlShortener.findById(-5);
    }

    @Test
    public void testGetJSON() throws Exception {
        JSONObject obj = urlShortener.getJSON("http://api.geonames.org/weatherJSON?north=44.1&south=-9.9&east=-22.4&west=55.2&username=demo");
    }

    @Test
    public void testCreateView() throws Exception {

    }
}