/*
 * Copyright (c) 2014. Thumbtack Technologies
 */

package bench;

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
    private static final String ALPHABET = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final long BASE = ALPHABET.length();

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

    Response shorten(String longUrl, String base) throws Exception {
        if (longUrl == null) throw new Exception("url param not set");
        JSONObject doc = new JSONObject();
        String decoded = java.net.URLDecoder.decode(longUrl, "ASCII");
        String shortened = charCode(Math.abs(hash(longUrl) % 50000000000l));
        doc.put("_id", shortened);
        doc.put("long", decoded);
        createDocument(dbname, doc);
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

    public void connectCouch() throws IOException {
        createDatabase(dbname);
    }
}
