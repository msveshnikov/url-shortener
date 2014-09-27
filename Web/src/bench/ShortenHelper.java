package bench;

import com.fourspaces.couchdb.Database;
import com.fourspaces.couchdb.Document;
import com.fourspaces.couchdb.Session;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ShortenHelper {
    final static String dbname = "users";
    private final Database db;

    public ShortenHelper() throws IOException {
        Session dbSession = new Session("localhost", 5984);
        List<String> listofdb = dbSession.getDatabaseNames();
        if (!listofdb.contains(dbname)) {
            dbSession.createDatabase(dbname);
        }
        db = dbSession.getDatabase(dbname);
        createView();
    }

    private void createView() throws IOException {
        try {
            Document d = db.getDocument("_design/couchview");
            if (d != null) db.deleteDocument(d);
        } catch (Exception e) {
        }
        Document doc = new Document();
        doc.setId("_design/couchview");
        String str = "{\"userid\": {\"map\": \"function(doc) { emit(doc.userid, doc.short) } \"}}";
        doc.put("views", str);
        db.saveDocument(doc);
    }

    private List<String> historyByUserId(String userId) throws Exception {
        JSONObject result = UrlShortener.getJSON("http://localhost:5984/users/_design/couchview/_view/userid?key=%22" + userId + "%22");
        JSONArray arr = result.getJSONArray("rows");
        List<String> list = new ArrayList<String>();
        for (int i = 0; i < arr.size(); i++) {
            list.add(arr.getJSONObject(i).getString("value"));
        }
        return list;
    }

    public String getShort(String url, HttpSession session) throws Exception {
        HttpClient httpclient = new DefaultHttpClient();
        String encoded = java.net.URLEncoder.encode(url, "ASCII");
        HttpGet get = new HttpGet("http://localhost:8080/shorten?url=" + encoded);
        HttpResponse response = httpclient.execute(get);
        if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
            throw new Exception("REST service failed: " + response.getStatusLine() + "\nURL=" + url);
        }
        String shorturl = new BasicResponseHandler().handleResponse(response);
        saveShort(url, session, shorturl);
        return shorturl;
    }

    private void saveShort(String url, HttpSession session, String shorturl) throws IOException {
        String userinfo = (String) session.getAttribute("userinfo");
        if (userinfo != null) {
            String userId = JSONObject.fromObject(userinfo).getString("id");
            Document doc = new Document();
            doc.put("userid", userId);
            doc.put("short", shorturl);
            doc.put("long", url);
            db.saveDocument(doc);
        }
    }

    public void PrintPreviousShorts(HttpSession session, JspWriter out) throws Exception {
        String userinfo = (String) session.getAttribute("userinfo");
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



















