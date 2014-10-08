/*
 * Copyright (c) 2014 Thumbtack Technologies
 */

package bench;

public interface Shortener {
    String shorten(String longUrl);

    String lengthen(String shortUrl);
}
