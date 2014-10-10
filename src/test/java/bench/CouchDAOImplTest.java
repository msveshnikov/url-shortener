package bench;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.impl.client.DefaultHttpClient;
import org.junit.Test;

import java.io.IOException;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;


public class CouchDAOImplTest {
    CouchDAOImpl dao;


    public CouchDAOImplTest() throws Exception {
        deleteDatabase();
        dao = new CouchDAOImpl("test", "http://localhost:5984/");
        assertTrue(dao.isConnected());
    }

    @Test
    public void testSavePair() throws Exception {
        dao.savePair("short", "long");
    }

    @Test
    public void testSaveTriple() throws Exception {
        dao.saveTriple(5, "short", "long");
    }

    @Test(expected = Exception.class)
    public void testFindById1() throws Exception {
        String d = dao.findById(-5);
    }

    @Test
    public void testFindById() throws Exception {
        testSaveTriple();
        String s = dao.findById(5);
        assertEquals("long", s);
    }

    @Test
    public void testFindMaxId() throws Exception {
        long max = dao.findMaxId();
        assertTrue(max > 0);
    }

    @Test
    public void testFindByShort() throws Exception {
        testSavePair();
        String s = dao.findByShort("short");
        assertEquals("long", s);
    }

    @Test
    public void testHistoryByUserId() throws Exception {
        testSaveShort();
        Iterable<String> h = dao.historyByUserId("123");
        assertEquals("short", h.iterator().next());
    }

    @Test
    public void testSaveShort() throws Exception {
        dao.saveShort("long", "123", "short");
    }

    @Test
    public void testGetJSON() throws Exception {
        dao.getJSON("http://api.geonames.org/weatherJSON?north=44.1&south=-9.9&east=-22.4&west=55.2&username=demo");
    }

    private void deleteDatabase() throws IOException {
        HttpClient httpclient = new DefaultHttpClient();
        String url = "http://localhost:5984/test/";
        httpclient.execute(new HttpDelete(url));
    }
}