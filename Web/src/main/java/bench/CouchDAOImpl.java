/*
 * Copyright (c) 2014 Thumbtack Technologies
 */

package bench;


public class CouchDAOImpl implements ShortenerDAO {
    public CouchDAOImpl(String dbname) {
    }

    @Override
    public void savePair(String shortUrl, String longUrl) {

    }

    @Override
    public void saveTriple(long id, String shortUrl, String longUrl) {

    }

    @Override
    public String findById(long id) {
        return null;
    }

    @Override
    public long findMaxId() {
        return 0;
    }

    @Override
    public String findByShort(String shortUrl) {
        return null;
    }
}
