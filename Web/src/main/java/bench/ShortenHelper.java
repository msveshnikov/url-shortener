/*
 * Copyright (c) 2014. Thumbtack Technologies
 */

package bench;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.ektorp.CouchDbConnector;
import org.ektorp.CouchDbInstance;
import org.ektorp.http.StdHttpClient;
import org.ektorp.impl.StdCouchDbConnector;
import org.ektorp.impl.StdCouchDbInstance;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.servlet.jsp.JspWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Entity
class History {
    @Column(name = "userid")
    String userId;
    @Column(name = "short")
    String shortUrl;
    @Column(name = "long")
    String longUrl;
}

public class ShortenHelper {
    final static String dbname = "users";
    private final CouchDbConnector db;

    public ShortenHelper() throws IOException {
        org.ektorp.http.HttpClient httpClient = new StdHttpClient.Builder().url("http://localhost:5984").build();
        CouchDbInstance dbInstance = new StdCouchDbInstance(httpClient);
        db = new StdCouchDbConnector(dbname, dbInstance);
        db.createDatabaseIfNotExists();
    }

    void createView() throws IOException {
//        Document doc = new Document();
//        doc.setId("_design/couchview");
//        String str = "{\"userid\": {\"map\": \"function(doc) { emit(doc.userid, doc.short) } \"}}";
//        doc.put("views", str);
        //   db.saveDocument(doc);
    }

    public List<String> historyByUserId(String userId) throws Exception {
        JSONObject result = UrlShortener.getJSON("http://localhost:5984/users/_design/couchview/_view/userid?key=%22" + userId + "%22");
        JSONArray arr = result.getJSONArray("rows");
        List<String> list = new ArrayList<String>();
        for (int i = 0; i < arr.size(); i++) {
            list.add(arr.getJSONObject(i).getString("value"));
        }
        return list;
    }

    public String getShort(String url, String userinfo) throws Exception {
        org.apache.http.client.HttpClient httpclient = new DefaultHttpClient();
        String encoded = java.net.URLEncoder.encode(url, "ASCII");
        HttpGet get = new HttpGet("http://" + GoogleAuthHelper.host + "/shorten?url=" + encoded);
        org.apache.http.HttpResponse response = httpclient.execute(get);
        if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
            throw new Exception("REST service failed: " + response.getStatusLine() + "\nURL=" + url);
        }
        String shorturl = new BasicResponseHandler().handleResponse(response);
        saveShort(url, userinfo, shorturl);
        return shorturl;
    }

    public void saveShort(String url, String userinfo, String shorturl) throws IOException {
        if (userinfo != null) {
            String userId = JSONObject.fromObject(userinfo).getString("id");
            History doc = new History();
            doc.userId = userId;
            doc.shortUrl = shorturl;
            doc.longUrl = url;
            db.update(doc);
        }
    }

    public void PrintPreviousShorts(String userinfo, JspWriter out) throws Exception {
        String userId = JSONObject.fromObject(userinfo).getString("id");
        String name = JSONObject.fromObject(userinfo).getString("name");
        String picture = JSONObject.fromObject(userinfo).getString("picture");
        out.println("<img src=\"" + picture + "\"  height=\"42\" width=\"42\">");
        out.println("Welcome, " + name + "<br><br>");
        for (String url : historyByUserId(userId)) {
            out.println("<a href='" + url + "'>" + url + "</a><br>");
        }
    }
}






