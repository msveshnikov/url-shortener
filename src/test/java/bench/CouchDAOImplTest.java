package bench;

import org.junit.Before;
import org.junit.Test;

public class CouchDAOImplTest {
    CouchDAOImpl dao;

    @Before
    public void setUp() throws Exception {
        dao=new CouchDAOImpl("test");
    }

    @Test
    public void testSavePair() throws Exception {

    }

    @Test
    public void testSaveTriple() throws Exception {

    }

    @Test
    public void testFindById() throws Exception {

    }

    @Test
    public void testFindMaxId() throws Exception {

    }

    @Test
    public void testFindByShort() throws Exception {

    }

    @Test
    public void testHistoryByUserId() throws Exception {

    }

    @Test
    public void testSaveShort() throws Exception {

    }


    @Test
    public void testGetJSON() throws Exception {
        dao.getJSON("http://api.geonames.org/weatherJSON?north=44.1&south=-9.9&east=-22.4&west=55.2&username=demo");
    }
}