/*
 * Copyright (c) 2014 Thumbtack Technologies
 */

package bench;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.net.URI;

@Path("/")
public class RestService {
    public static final String DBNAME = "shortener";//config
    public static final String SHORTEN_VERB = "shorten";//config
    public static final String URL_PARAM = "url";//config

    public static final String URL_PARAM_NOT_SET = "url param not set";

    private final Shortener shortener = new HashShortener(new CouchDAOImpl(DBNAME));
//    private final Shortener shortener = new IncrementalShortener(new CouchDAOImpl(DBNAME));

    @GET
    @Produces("text/plain")
    @Path("{shorturl}")
    public Response main(@PathParam("shorturl") String shortUrl, @QueryParam(URL_PARAM) String longUrl, @Context UriInfo ui) {
        try {
            if (shortUrl.equals("favicon.ico")) return null;
            if (shortUrl.equals(SHORTEN_VERB)) {
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
        String longUrl = shortener.lengthen(shortUrl);
        if (longUrl.length() < 4 || !longUrl.substring(0, 4).equals("http"))
            longUrl = "http://" + longUrl;
        return Response.status(Response.Status.MOVED_PERMANENTLY).location(URI.create(longUrl)).build();
    }

    Response shorten(String longUrl, String base) throws Exception {
        if (longUrl == null) throw new Exception(URL_PARAM_NOT_SET);
        String decoded = java.net.URLDecoder.decode(longUrl, "ASCII");
        String shortened = shortener.shorten(decoded);
        return Response.status(Response.Status.OK).entity(base + "/" + shortened).build();
    }
}
