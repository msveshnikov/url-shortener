package bench;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

public class ShortenServlet {
    public static String getShort(String url) throws Exception {
        HttpClient httpclient = new DefaultHttpClient();
        String encoded = java.net.URLEncoder.encode(url, "ASCII");
        HttpGet get = new HttpGet("http://localhost:8080/shorten?url=" + encoded);
        HttpResponse response = httpclient.execute(get);
        if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
            throw new Exception("REST service failed: " + response.getStatusLine() + "\nURL=" + url);
        }
        return new BasicResponseHandler().handleResponse(response);
    }
}
