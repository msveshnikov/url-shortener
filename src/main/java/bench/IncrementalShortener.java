/*
 * Copyright (c) 2014 Thumbtack Technologies
 */

package bench;

import java.io.IOException;

public class IncrementalShortener implements Shortener {

    private final Coder coder = new Coder();
    private ShortenerDAO dao;

    public IncrementalShortener(ShortenerDAO dao) {
        this.dao = dao;
    }

    @Override
    public String shorten(String longUrl) throws IOException {
        long nextId = dao.findMaxId() + 1;
        String shortened = coder.charCode(nextId);
        dao.saveTriple(nextId, shortened, longUrl);
        return shortened;
    }

    @Override
    public String lengthen(String shortUrl) throws IOException {
        return dao.findById(coder.charDecode(shortUrl));
    }
}
