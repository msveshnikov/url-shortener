package main.java.bench;

import com.fourspaces.couchdb.Database;
import com.fourspaces.couchdb.Document;
import com.fourspaces.couchdb.Session;
import net.sf.json.JSONObject;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.net.URLEncoder;
import java.util.List;


@Path("/")
public class UrlShortener {
    Session dbSession;
    Database db;

    @GET
    @Produces("text/plain")
    @Path("{shorturl}")
    public Response Main(@PathParam("shorturl") String shortUrl, @QueryParam("url") String longUrl, @Context UriInfo ui) {
        try {
            connectCouch();
            if (shortUrl.equals("shorten")) {
                String base = ui.getBaseUri().getScheme() + "://" + ui.getBaseUri().getHost() + ":" + ui.getBaseUri().getPort();
                return shorten(longUrl, base);
            } else
                return redirect(shortUrl);
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }

    private Response redirect(String shortUrl) throws Exception {
        JSONObject d = findById(shortback(shortUrl));
        String longUrl = d.getString("long");
        return Response.status(Response.Status.MOVED_PERMANENTLY).entity(URLEncoder.encode(longUrl, "ASCII")).build();
    }

    private Response shorten(String longUrl, String base) throws Exception {
        if (longUrl == null) throw new Exception("url param not set");
        Document doc = new Document();
        String decoded = java.net.URLDecoder.decode(longUrl, "ASCII");
        int nextId = getMax() + 1;
        doc.put("myid", nextId);
        String shortened = shorturl(nextId);
        doc.put("short", shortened);
        doc.put("long", decoded);
        db.saveDocument(doc);
        return Response.status(Response.Status.OK).entity(base + "/" + shortened).build();
    }

    private static final String ALPHABET = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int BASE = ALPHABET.length();

    private String shorturl(int id) {
        StringBuilder sb = new StringBuilder();
        while (id > 0) {
            sb.append(ALPHABET.charAt(id % BASE));
            id /= BASE;
        }
        return sb.reverse().toString();
    }

    private int shortback(String shortUrl) {
        int num = 0;
        for (int i = 0, len = shortUrl.length(); i < len; i++) {
            num = num * BASE + ALPHABET.indexOf(shortUrl.charAt(i));
        }
        return num;
    }

    private int getMax() throws Exception {
        JSONObject jsonObject = getJSON("http://localhost:5984/shortener/_design/couchview/_view/autoinc?startkey=2000000000&descending=true&limit=1");
        return jsonObject.getJSONArray("rows").getJSONObject(0).getInt("key");
    }

    private JSONObject findById(int myid) throws Exception {
        JSONObject jsonObject = getJSON("http://localhost:5984/shortener/_design/couchview/_view/autoinc?key="
                + myid + "&include_docs=true");
        if (jsonObject.getJSONArray("rows").size() != 0)
            return jsonObject.getJSONArray("rows").getJSONObject(0).getJSONObject("doc");
        else throw new Exception("No such URL found");
    }

    private JSONObject getJSON(String url) throws Exception {
        HttpClient httpclient = new HttpClient();
        org.apache.commons.httpclient.HttpMethod method = new GetMethod(url);
        try {
            int statusCode = httpclient.executeMethod(method);
            if (statusCode != HttpStatus.SC_OK) {
                throw new Exception("getJSON failed: " + method.getStatusLine());
            }
            return JSONObject.fromObject(new String(method.getResponseBody()));
        } finally {
            method.releaseConnection();
        }
    }

    private void createView() {
        try {
            Document d = db.getDocument("_design/couchview");
            if (d != null) db.deleteDocument(d);
        } catch (Exception e) {
        }
        Document doc = new Document();
        doc.setId("_design/couchview");
        String str = "{\"autoinc\": {\"map\": \"function(doc) { emit(doc.myid, null) } \"}}";
        doc.put("views", str);
        db.saveDocument(doc);
    }

    private void connectCouch() {
        dbSession = new Session("localhost", 5984);
        String dbname = "shortener";
        List<String> listofdb = dbSession.getDatabaseNames();
        if (!listofdb.contains(dbname)) {
            dbSession.createDatabase(dbname);
        }
        db = dbSession.getDatabase(dbname);
        createView();
    }
}
