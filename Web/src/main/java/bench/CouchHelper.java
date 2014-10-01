/*
 * Copyright (c) 2014. Thumbtack Technologies
 */

package bench;

import net.sf.json.JSONObject;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;

public class CouchHelper {

    public static JSONObject getJSON(String url) throws IOException {
        HttpClient httpclient = new DefaultHttpClient();
        HttpGet get = new HttpGet(url);
        get.setHeader("Accept", "application/json");
        HttpResponse response = httpclient.execute(get);
        if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
            throw new IOException("getJSON failed: " + response.getStatusLine() + "\nURL=" + url);
        }
        String responseString = new BasicResponseHandler().handleResponse(response);
        return JSONObject.fromObject(responseString);
    }

    public static JSONObject getDocument(String db, String id) throws IOException {
        return getJSON("http://localhost:5984/" + db + "/" + id);
    }

    public static void createDocument(String db, JSONObject doc) throws IOException {
        HttpClient httpclient = new DefaultHttpClient();
        String url = "http://localhost:5984/" + db;
        HttpPost post = new HttpPost(url);
        HttpEntity entity = new StringEntity(doc.toString());
        post.setEntity(entity);
        post.setHeader("Content-Type", "application/json");
        HttpResponse response = httpclient.execute(post);
        if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) return;
        String responseString = new BasicResponseHandler().handleResponse(response);
        JSONObject result = JSONObject.fromObject(responseString);
        doc.put("_id", result.get("_id"));
    }

    public static void updateDocument(String db, JSONObject doc, String id) throws IOException {
        HttpClient httpclient = new DefaultHttpClient();
        String url = "http://localhost:5984/" + db + "/" + id;
        HttpPut put = new HttpPut(url);
        HttpEntity entity = new StringEntity(doc.toString());
        put.setEntity(entity);
        put.setHeader("Content-Type", "application/json");
        HttpResponse response = httpclient.execute(put);
        if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
            throw new IOException("post failed: " + response.getStatusLine() + "\nURL=" + url);
        }
    }

    public static void createDatabase(String db) throws IOException {
        HttpClient httpclient = new DefaultHttpClient();
        String url = "http://localhost:5984/" + db + "/";
        httpclient.execute(new HttpPut(url));
    }

}
