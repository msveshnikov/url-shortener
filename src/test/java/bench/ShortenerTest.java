package bench;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

@RunWith(Parameterized.class)
public class ShortenerTest {
    Shortener shortener;
    static ShortenerDAO dao;

    public ShortenerTest(Shortener shortener) {
        this.shortener = shortener;
    }

    @Test
    public void testShorten() throws Exception {

    }

    @Test
    public void testLengthen() throws Exception {

    }

    @Parameterized.Parameters
    public static Collection<Object[]> instancesToTest() {
        return Arrays.asList(
                new Object[]{new HashShortener(dao)},
                new Object[]{new IncrementalShortener(dao)}
        );
    }
}