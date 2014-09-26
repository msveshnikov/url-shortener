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

public class ShortenServlet {
    private Database db;
    final static String dbname = "users";

    public ShortenServlet() throws IOException {
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

    private List<String> findByUserId(String userId) throws Exception {
        JSONObject result = UrlShortener.getJSON("http://localhost:5984/users/_design/couchview/_view/userid?key=\\\"" + userId + "\\\"");
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

        String userinfo = (String) session.getAttribute("userinfo");
        if (userinfo != null) {
            String userId = JSONObject.fromObject(userinfo).getString("id");
            Document doc = new Document();
            //doc.setId(userId);
            doc.put("userid", userId);
            doc.put("short", shorturl);
            doc.put("long", url);
            db.saveDocument(doc);
        }
        return shorturl;
    }

    public void PrintPreviousShorts(HttpSession session, JspWriter out) throws Exception {
        String userinfo = (String) session.getAttribute("userinfo");
        String userId = JSONObject.fromObject(userinfo).getString("id");
        for (String url : findByUserId(userId)) {
            out.println("<a href='" + url + "'>" + url + "</a><br>");
        }
    }
}



















