/*
 * Copyright (c) 2014 Thumbtack Technologies
 */

package bench;

public class Coder {
    private static final String ALPHABET = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final long BASE = ALPHABET.length();

    public String charCode(long id) {
        StringBuilder sb = new StringBuilder();
        while (id > 0) {
            sb.append(ALPHABET.charAt((int) (id % BASE)));
            id /= BASE;
        }
        return sb.reverse().toString();
    }


    public long charDecode(String shortUrl) {
        long num = 0;
        for (int i = 0, len = shortUrl.length(); i < len; i++) {
            num = num * BASE + ALPHABET.indexOf(shortUrl.charAt(i));
        }
        return num;
    }

}