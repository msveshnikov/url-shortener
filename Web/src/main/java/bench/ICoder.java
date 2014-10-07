package bench;


public interface ICoder {
    String charCode(long id);
    long charDecode(String shortUrl);
}
