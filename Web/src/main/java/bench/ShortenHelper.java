/*
 * Copyright (c) 2014 Thumbtack Technologies
 */

package bench;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

import javax.servlet.jsp.JspWriter;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import static bench.CouchHelper.*;

public class ShortenHelper {
    final static String dbname = "users";

    private void prepareDb() throws IOException {
        createDatabase(dbname);
        createView();
    }

    void createView() throws IOException {
        JSONObject doc = new JSONObject();
        doc.put("_id", "_design/couchview");
        String str = "{\"userid\": {\"map\": \"function(doc) { emit(doc.userid, doc.short) } \"}}";
        doc.put("views", str);
        createDocument(dbname, doc);
    }

    public List<String> historyByUserId(String userId) throws Exception {
        JSONObject result = getJSON("http://localhost:5984/users/_design/couchview/_view/userid?key=%22" + userId + "%22");
        JSONArray arr = result.getJSONArray("rows");
        List<String> list = new ArrayList<String>();
        for (int i = 0; i < arr.size(); i++) {
            list.add(arr.getJSONObject(i).getString("value"));
        }
        return list;
    }

    public String getShort(String url, String userinfo) throws Exception {
        HttpClient httpclient = new DefaultHttpClient();
        String encoded = URLEncoder.encode(url, "ASCII");
        HttpGet get = new HttpGet("http://" + GoogleAuthHelper.host + "/shorten?url=" + encoded);
        HttpResponse response = httpclient.execute(get);
        if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
            throw new Exception("REST service failed: " + response.getStatusLine() + "\nURL=" + url);
        }
        String shorturl = new BasicResponseHandler().handleResponse(response);
        saveShort(url, userinfo, shorturl);
        return shorturl;
    }

    public void saveShort(String url, String userinfo, String shorturl) throws IOException {
        try {
            prepareDb();
        } catch (Exception e) {
            return;
        }
        if (userinfo != null) {
            String userId = JSONObject.fromObject(userinfo).getString("id");
            JSONObject doc = new JSONObject();
            doc.put("userid", userId);
            doc.put("short", shorturl);
            doc.put("long", url);
            createDocument(dbname, doc);
        }
    }

    public void PrintPreviousShorts(String userinfo, JspWriter out) throws Exception {
        try {
            prepareDb();
        } catch (Exception e) {
            return;
        }
        String userId = JSONObject.fromObject(userinfo).getString("id");
        String name = JSONObject.fromObject(userinfo).getString("name");
        String picture = JSONObject.fromObject(userinfo).getString("picture");
        // TODO: to JSP
        out.println("<img src=\"" + picture + "\"  height=\"42\" width=\"42\">");
        out.println("Welcome, " + name + "<br><br>");
        for (String url : historyByUserId(userId)) {
            out.println("<a href='" + url + "'>" + url + "</a><br>");
        }
    }
}



















