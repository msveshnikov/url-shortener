package main.java.bench;

import com.fourspaces.couchdb.Session;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/")
public class UrlShortener {
    Session dbSession;

    @GET
    @Produces("text/plain")
    @Path("{shorturl}")
    public Response redirect(@PathParam("shorturl") String shortUrl, @QueryParam("url") String longUrl) {
        connectCouch();
        if (shortUrl.equals("shorten")) {
            if (longUrl != null)
                return Response.status(Response.Status.OK).entity("Shortening long=" + longUrl).build();
            else return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        } else
            return Response.status(Response.Status.MOVED_PERMANENTLY).entity("Redirect to " + shortUrl).build();
    }

    private void connectCouch() {
        dbSession = new Session("localhost", 5984);
        String dbname = "shortener";

        List<String> listofdb = dbSession.getDatabaseNames();
        if (!listofdb.contains(dbname))
            dbSession.createDatabase(dbname);
    }
}
