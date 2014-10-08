/*
 * Copyright (c) 2014 Thumbtack Technologies
 */

package bench;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.io.IOException;
import java.net.URI;

import static bench.CouchHelper.*;


@Path("/")
public class UrlShortener {
    final static String dbname = "shortener";
    private final ShortenerDAO dao = new CouchDAOImpl(dbname);
    private final ICoder coder = new Coder();

    @GET
    @Produces("text/plain")
    @Path("{shorturl}")
    public Response Main(@PathParam("shorturl") String shortUrl, @QueryParam("url") String longUrl, @Context UriInfo ui) {
        try {
            if (shortUrl.equals("favicon.ico")) return null;
            if (shortUrl.equals("shorten")) {
                String base = "";
                if (ui != null)
                    base = ui.getBaseUri().getScheme() + "://" + ui.getBaseUri().getHost() + ":" + ui.getBaseUri().getPort();
                return shorten(longUrl, base);
            } else
                return redirect(shortUrl);
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }

    Response redirect(String shortUrl) throws Exception {
        JSONObject d = getDocument(dbname, shortUrl);
        String longUrl = d.getString("long");
        if (longUrl.length() < 4 || !longUrl.substring(0, 4).equals("http"))
            longUrl = "http://" + longUrl;
        return Response.status(Response.Status.MOVED_PERMANENTLY).location(URI.create(longUrl)).build();
    }

    Response redirect2(String shortUrl) throws Exception {
        JSONObject d = findById((int) charDecode(shortUrl));
        String longUrl = d.getString("long");
        if (longUrl.length() < 4 || !longUrl.substring(0, 4).equals("http"))
            longUrl = "http://" + longUrl;
        return Response.status(Response.Status.MOVED_PERMANENTLY).location(URI.create(longUrl)).build();
    }

    Response shorten(String longUrl, String base) throws Exception {
        if (longUrl == null) throw new Exception("url param not set");
        JSONObject doc = new JSONObject();
        String decoded = java.net.URLDecoder.decode(longUrl, "ASCII");
        String shortened = coder.charCode(Math.abs(hash(longUrl) % 50000000000l));
        try {
            createDatabase(dbname);
            doc.put("_id", shortened);
            doc.put("long", decoded);
            createDocument(dbname, doc);
        } catch (Exception e) {
        }
        return Response.status(Response.Status.OK).entity(base + "/" + shortened).build();
    }

    Response shorten2(String longUrl, String base) throws Exception {
        if (longUrl == null) throw new Exception("url param not set");
        JSONObject doc = new JSONObject();
        String decoded = java.net.URLDecoder.decode(longUrl, "ASCII");
        int nextId = getMax() + 1;
        doc.put("myid", nextId);
        String shortened = charCode(nextId);
        doc.put("short", shortened);
        doc.put("long", decoded);
        createDocument(dbname, doc);
        return Response.status(Response.Status.OK).entity(base + "/" + shortened).build();
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
        JSONObject doc = new JSONObject();
        doc.put("_id", "_design/couchview");
        String str = "{\"autoinc\": {\"map\": \"function(doc) { emit(doc.myid, null) } \"}}";
        doc.put("views", str);
        createDocument(dbname, doc);
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
        return coder.charCode(id);
    }

    long charDecode(String shortUrl) {
        return coder.charDecode(shortUrl);
    }


}
