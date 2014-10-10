/*
 * Copyright (c) 2014. Thumbtack Technologies
 */

package bench;

import java.io.IOException;

public interface UsersDAO {
    public Iterable<String> historyByUserId(String userId) throws IOException;

    public void saveShort(String longUrl, String userId, String shortUrl) throws IOException;

    public Boolean isConnected();
}
