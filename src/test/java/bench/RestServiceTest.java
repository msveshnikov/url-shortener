package bench;

import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.core.Response;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

public class RestServiceTest {
    private RestService restService;

    @Before
    public void setUp() throws Exception {
        restService = new RestService();
    }

    @Test
    public void testMain() throws Exception {
        assertEquals(500, restService.main("abcd", "", null, null).getStatus());
        Response shorten = restService.main("shorten", "www.mail.ru", null, null);
        assertEquals(200, shorten.getStatus());
        String s = (String) shorten.getEntity();
        assertTrue(s.length() > 3);
        Response redir = restService.main(s.substring(1, s.length()), "", null, null);
        assertEquals(301, redir.getStatus());
        assertEquals("http://www.mail.ru", redir.getMetadata().getFirst("Location").toString());
    }
}