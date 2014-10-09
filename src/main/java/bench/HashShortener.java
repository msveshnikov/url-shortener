/*
 * Copyright (c) 2014 Thumbtack Technologies
 */

package bench;

import java.io.IOException;

public class HashShortener implements Shortener {
    private final Coder coder = new Coder();
    private ShortenerDAO dao;

    public HashShortener(ShortenerDAO dao) {
        this.dao = dao;
    }

    @Override
    public String shorten(String longUrl) throws IOException {
        String shortened = coder.charCode(Math.abs(hash(longUrl) % 50000000000l));
        if (dao.isConnected())  // to work halfway without CouchDB
            dao.savePair(shortened, longUrl);
        return shortened;
    }

    @Override
    public String lengthen(String shortUrl) throws IOException {
        return dao.findByShort(shortUrl);
    }

    private long hash(String longUrl) {
        long h = 1125899906842597L; // prime
        int len = longUrl.length();
        for (int i = 0; i < len; i++) {
            h = 31 * h + longUrl.charAt(i);
        }
        return h;
    }


}