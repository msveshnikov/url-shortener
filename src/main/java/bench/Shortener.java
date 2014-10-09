/*
 * Copyright (c) 2014 Thumbtack Technologies
 */

package bench;

import java.io.IOException;

public interface Shortener {
    String shorten(String longUrl) throws IOException;

    String lengthen(String shortUrl) throws IOException;
}
