package main.java.bench;

import javax.ws.rs.*;
@Path("/")
public class UrlShortener {

    @GET
    @Produces("text/plain")
    @Path("{shorturl}")
    public String redirect(@PathParam("shorturl") String shortUrl,@QueryParam("url") String longUrl) {
        return "Redirect to " + shortUrl+" long="+longUrl;
    }
}
