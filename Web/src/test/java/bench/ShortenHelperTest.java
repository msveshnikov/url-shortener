/*
 * Copyright (c) 2014 Thumbtack Technologies
 */

package bench;

import org.junit.Before;
import org.junit.Test;

import javax.servlet.jsp.JspWriter;
import java.io.IOException;

import static org.junit.Assert.assertTrue;

public class ShortenHelperTest {

    private ShortenHelper helper;

    @Before
    public void setUp() throws Exception {
        helper = new ShortenHelper();
    }

//  required started REST service :(
//    @Test
//    public void testGetShort() throws Exception {
//        helper.getShort("www.mail.ru", "{  \"id\": \"2\"  }");
//    }

    @Test
    public void testHistoryByUserId() throws Exception {
        helper.saveShort("111", "{  \"id\": \"2\"  }", "222");
        assertTrue(helper.historyByUserId("2").size() > 0);
    }

    @Test
    public void testPrintPreviousShorts() throws Exception {
        final StringBuilder out = new StringBuilder();

        helper.PrintPreviousShorts("{  \"id\": \"1\" , \"name\":\"Max\", \"picture\":\"1.jpg\"  }", new JspWriter(1, false) {
            @Override
            public void newLine() throws IOException {

            }

            @Override
            public void print(boolean b) throws IOException {

            }

            @Override
            public void print(char c) throws IOException {

            }

            @Override
            public void print(int i) throws IOException {

            }

            @Override
            public void print(long l) throws IOException {

            }

            @Override
            public void print(float v) throws IOException {

            }

            @Override
            public void print(double v) throws IOException {

            }

            @Override
            public void print(char[] chars) throws IOException {

            }

            @Override
            public void print(String s) throws IOException {

            }

            @Override
            public void print(Object o) throws IOException {

            }

            @Override
            public void println() throws IOException {

            }

            @Override
            public void println(boolean b) throws IOException {

            }

            @Override
            public void println(char c) throws IOException {

            }

            @Override
            public void println(int i) throws IOException {

            }

            @Override
            public void println(long l) throws IOException {

            }

            @Override
            public void println(float v) throws IOException {

            }

            @Override
            public void println(double v) throws IOException {

            }

            @Override
            public void println(char[] chars) throws IOException {

            }

            @Override
            public void println(String s) throws IOException {
                out.append(s);
            }

            @Override
            public void println(Object o) throws IOException {

            }

            @Override
            public void clear() throws IOException {

            }

            @Override
            public void clearBuffer() throws IOException {

            }

            @Override
            public void flush() throws IOException {

            }

            @Override
            public void close() throws IOException {

            }

            @Override
            public int getRemaining() {
                return 0;
            }

            @Override
            public void write(char[] cbuf, int off, int len) throws IOException {

            }
        });
        assertTrue(out.length() > 50);
    }
}