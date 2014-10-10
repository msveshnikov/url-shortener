package bench;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.*;

public class HashShortenerTest {
    Shortener shortener;
    ShortenerDAO dao;
    Coder coder = new Coder();

    public HashShortenerTest() {
        dao = mock(ShortenerDAO.class);
        shortener = new HashShortener(dao);
    }

    @Test
    public void testShorten() throws Exception {
        when(dao.isConnected()).thenReturn(true);
        String shortUrl = shortener.shorten("www.mail.ru");
        verify(dao, times(1)).savePair(any(String.class), eq("www.mail.ru"));
        assertTrue(shortUrl.length() >= 5);
    }

    @Test
    public void testLengthen() throws Exception {
        when(dao.findByShort("abcdef")).thenReturn("www.mir-omsk.ru");
        String longUrl = shortener.lengthen("abcdef");
        assertEquals("www.mir-omsk.ru", longUrl);
    }
}