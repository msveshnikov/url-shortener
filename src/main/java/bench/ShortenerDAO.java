/*
 * Copyright (c) 2014 Thumbtack Technologies
 */

package bench;

import java.io.IOException;

public interface ShortenerDAO {
    public void savePair(String shortUrl, String longUrl) throws IOException;

    public void saveTriple(long id, String shortUrl, String longUrl) throws IOException;

    public long findMaxId() throws IOException;

    public String findById(long id) throws IOException;

    public String findByShort(String shortUrl) throws IOException;

    public Boolean isConnected();
}