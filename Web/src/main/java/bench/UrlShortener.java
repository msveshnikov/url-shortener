/*
 * Copyright (c) 2014. Thumbtack Technologies
 */

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

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.io.IOException;
import java.net.URI;
import java.util.List;


@Path("/")
public class UrlShortener {
    final static String dbname = "shortener";
    private static final String ALPHABET = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final long BASE = ALPHABET.length();
    private Database db;

    public static JSONObject getJSON(String url) throws Exception {
        HttpClient httpclient = new DefaultHttpClient();
        HttpGet get = new HttpGet(url);
        get.setHeader("Accept", "application/json");
        HttpResponse response = httpclient.execute(get);
        if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
            throw new Exception("getJSON failed: " + response.getStatusLine() + "\nURL=" + url);
        }
        String responseString = new BasicResponseHandler().handleResponse(response);
        return JSONObject.fromObject(responseString);
    }

    @GET
    @Produces("text/plain")
    @Path("{shorturl}")
    public Response Main(@PathParam("shorturl") String shortUrl, @QueryParam("url") String longUrl, @Context UriInfo ui) {
        try {
            if (shortUrl.equals("favicon.ico")) return null;
            connectCouch();
            if (shortUrl.equals("shorten")) {
                String base = "";
                if (ui != null)
                    base = ui.getBaseUri().getScheme() + "://" + ui.getBaseUri().getHost() + ":" + ui.getBaseUri().getPort();
                return shorten2(longUrl, base);
            } else
                return redirect2(shortUrl);
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }

    Response redirect(String shortUrl) throws Exception {
        JSONObject d = findById((int) charDecode(shortUrl));
        String longUrl = d.getString("long");
        if (longUrl.length() < 4 || !longUrl.substring(0, 4).equals("http"))
            longUrl = "http://" + longUrl;
        return Response.status(Response.Status.MOVED_PERMANENTLY).location(URI.create(longUrl)).build();
    }

    Response redirect2(String shortUrl) throws Exception {
        Document d = db.getDocument(shortUrl);
        String longUrl = d.getString("long");
        if (longUrl.length() < 4 || !longUrl.substring(0, 4).equals("http"))
            longUrl = "http://" + longUrl;
        return Response.status(Response.Status.MOVED_PERMANENTLY).location(URI.create(longUrl)).build();
    }

    Response shorten(String longUrl, String base) throws Exception {
        if (longUrl == null) throw new Exception("url param not set");
        Document doc = new Document();
        String decoded = java.net.URLDecoder.decode(longUrl, "ASCII");
        int nextId = getMax() + 1;
        doc.put("myid", nextId);
        String shortened = charCode(nextId);
        doc.put("short", shortened);
        doc.put("long", decoded);
        db.saveDocument(doc);
        return Response.status(Response.Status.OK).entity(base + "/" + shortened).build();
    }

    Response shorten2(String longUrl, String base) throws Exception {
        if (longUrl == null) throw new Exception("url param not set");
        Document doc = new Document();
        String decoded = java.net.URLDecoder.decode(longUrl, "ASCII");
        String shortened = charCode(Math.abs(hash(longUrl) % 50000000000l));
        doc.setId(shortened);
        doc.put("long", decoded);
        db.saveDocument(doc);
        return Response.status(Response.Status.OK).entity(base + "/" + shortened).build();
    }

    private long hash(String longUrl) {
        long h = 1125899906842597L; // prime
        int len = longUrl.length();
        for (int i = 0; i < len; i++) {
            h = 31 * h + longUrl.charAt(i);
        }
        return h;
    }

    String charCode(long id) {
        StringBuilder sb = new StringBuilder();
        while (id > 0) {
            sb.append(ALPHABET.charAt((int) (id % BASE)));
            id /= BASE;
        }
        return sb.reverse().toString();
    }

    long charDecode(String shortUrl) {
        long num = 0;
        for (int i = 0, len = shortUrl.length(); i < len; i++) {
            num = num * BASE + ALPHABET.indexOf(shortUrl.charAt(i));
        }
        return num;
    }

    int getMax() throws Exception {
        JSONObject jsonObject;
        try {
            jsonObject = getJSON("http://localhost:5984/shortener/_design/couchview/_view/autoinc?startkey=2000000000&descending=true&limit=1");
        } catch (Exception e) {
            createView();
            return 1;
        }
        JSONArray rows = jsonObject.getJSONArray("rows");
        return rows.size() == 0 ? 1 : rows.getJSONObject(0).getInt("key");
    }

    JSONObject findById(int myid) throws Exception {
        JSONObject jsonObject = getJSON("http://localhost:5984/shortener/_design/couchview/_view/autoinc?key="
                + myid + "&include_docs=true");
        if (jsonObject.getJSONArray("rows").size() != 0)
            return jsonObject.getJSONArray("rows").getJSONObject(0).getJSONObject("doc");
        else throw new Exception("No such URL found");
    }

    void createView() throws IOException {
        Document doc = new Document();
        doc.setId("_design/couchview");
        String str = "{\"autoinc\": {\"map\": \"function(doc) { emit(doc.myid, null) } \"}}";
        doc.put("views", str);
        db.saveDocument(doc);
    }

    public void connectCouch() throws IOException {
        Session dbSession = new Session("localhost", 5984);
        List<String> listofdb = dbSession.getDatabaseNames();
        if (!listofdb.contains(dbname)) {
            dbSession.createDatabase(dbname);
            db = dbSession.getDatabase(dbname);
            createView();
            return;
        }
        db = dbSession.getDatabase(dbname);
    }
}
