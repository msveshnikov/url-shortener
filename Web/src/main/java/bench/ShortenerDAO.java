/*
 * Copyright (c) 2014 Thumbtack Technologies
 */

package bench;

public interface ShortenerDAO {
    public void savePair(String shortUrl, String longUrl);

    public void saveTriple(long id, String shortUrl, String longUrl);

    public long findMaxId();

    public String findById(long id);

    public String findByShort(String shortUrl);
}