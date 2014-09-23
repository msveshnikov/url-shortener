package main.java.bench;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;

@Path("/")
public class UrlShortener {

    @GET
    @Produces("text/plain")
    @Path("{shorturl}")
    public Response redirect(@PathParam("shorturl") String shortUrl, @QueryParam("url") String longUrl) {
        if (shortUrl.equals("shorten")) {
            if (longUrl != null)
                return Response.status(Response.Status.OK).entity("Shortening long=" + longUrl).build();
            else return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        } else
            return Response.status(Response.Status.MOVED_PERMANENTLY).entity("Redirect to " + shortUrl).build();
    }
}
