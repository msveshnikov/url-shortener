/*
 * Copyright (c) 2014 Thumbtack Technologies
 */

package bench;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContext;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.io.IOException;
import java.net.URI;
import java.util.Properties;

@Path("/")
public class RestService {
    public static final String URL_PARAM = "url";// const only
    public static final String URL_PARAM_NOT_SET = "url param not set";
    String DBNAME = "shortener";
    String SHORTEN_VERB = "shorten";
    String COUCH_URL = "http://localhost:5984/";
    private Shortener shortener;
    private static final Logger logger = LoggerFactory.getLogger(RestService.class);

    @GET
    @Produces("text/plain")
    @Path("{shorturl}")
    public Response main(@PathParam("shorturl") String shortUrl, @QueryParam(URL_PARAM) String longUrl,
                         @Context UriInfo ui, @Context ServletContext context) {
        try {
            logger.info("REST request {} {}", shortUrl, longUrl);
            readConfig(context);
            shortener = new HashShortener(new CouchDAOImpl(DBNAME, COUCH_URL));
//            shortener = new IncrementalShortener(new CouchDAOImpl(DBNAME, COUCH_URL));

            if (shortUrl.equals("favicon.ico")) return null;
            if (shortUrl.equals(SHORTEN_VERB)) {
                String base = "";
                if (ui != null)
                    base = ui.getBaseUri().getScheme() + "://" + ui.getBaseUri().getHost() + ":" + ui.getBaseUri().getPort();
                return shorten(longUrl, base);
            } else
                return redirect(shortUrl);
        } catch (Exception e) {
            logger.error("REST failed {}", e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }

    Response redirect(String shortUrl) throws Exception {
        String longUrl = shortener.lengthen(shortUrl);
        if (longUrl.length() < 4 || !longUrl.substring(0, 4).equals("http"))
            longUrl = "http://" + longUrl;
        logger.info("Redirecting to {}", longUrl);
        return Response.status(Response.Status.MOVED_PERMANENTLY).location(URI.create(longUrl)).build();
    }

    Response shorten(String longUrl, String base) throws Exception {
        if (longUrl == null) throw new Exception(URL_PARAM_NOT_SET);
        String decoded = java.net.URLDecoder.decode(longUrl, "ASCII");
        String shortened = shortener.shorten(decoded);
        logger.info("Shortening to {}", shortened);
        return Response.status(Response.Status.OK).entity(base + "/" + shortened).build();
    }

    void readConfig(ServletContext context) throws IOException {
        if (context == null) return;
        String resourceFileName = "/WEB-INF/config/rest.properties";
        Properties configuration = new Properties();
        configuration.load(context.getResourceAsStream(resourceFileName));
        DBNAME = configuration.getProperty("DBNAME");
        SHORTEN_VERB = configuration.getProperty("SHORTEN_VERB");
        COUCH_URL = configuration.getProperty("COUCH_URL");
    }
}
