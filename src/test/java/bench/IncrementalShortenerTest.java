package bench;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class IncrementalShortenerTest {
    Shortener shortener;
    static ShortenerDAO dao;
    private Coder coder = new Coder();

    public IncrementalShortenerTest() {
        dao = mock(ShortenerDAO.class);
        shortener = new IncrementalShortener(dao);
    }

    @Test
    public void testShorten() throws Exception {
        when(dao.findMaxId()).thenReturn(5l);
        when(dao.isConnected()).thenReturn(true);
        String shortUrl = shortener.shorten("www.mail.ru");
        verify(dao, times(1)).saveTriple(eq(6l), eq(coder.charCode(6)), eq("www.mail.ru"));
        assertEquals(coder.charCode(6), shortUrl);
    }

    @Test
    public void testLengthen() throws Exception {
        when(dao.findById(1l)).thenReturn("www.mir-omsk.ru");
        String longUrl = shortener.lengthen("b");
        assertEquals("www.mir-omsk.ru", longUrl);
    }
}