package bench;

public class Coder implements ICoder {
    private static final String ALPHABET = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final long BASE = ALPHABET.length();

    @Override
    public String charCode(long id) {
        StringBuilder sb = new StringBuilder();
        while (id > 0) {
            sb.append(ALPHABET.charAt((int) (id % BASE)));
            id /= BASE;
        }
        return sb.reverse().toString();
    }

    @Override
    public long charDecode(String shortUrl) {
        long num = 0;
        for (int i = 0, len = shortUrl.length(); i < len; i++) {
            num = num * BASE + ALPHABET.indexOf(shortUrl.charAt(i));
        }
        return num;
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