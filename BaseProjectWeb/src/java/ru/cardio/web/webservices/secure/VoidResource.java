package ru.cardio.web.webservices.secure;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.GET;
import javax.ws.rs.Produces;

/**
 * REST Web Service
 *
 * @author rogvold
 */
@Path("void")
public class VoidResource {

    @Context
    private UriInfo context;

    /**
     * Creates a new instance of VoidResource
     */
    public VoidResource() {
    }

    /**
     * Retrieves representation of an instance of
     * ru.cardio.web.webservices.secure.VoidResource
     *
     * @return an instance of java.lang.String
     */
    @GET
    @Produces("application/json")
    public String getJson() {
        //TODO return proper representation object
        throw new UnsupportedOperationException();
    }

    @GET
    @Produces("application/json")
    @Path("void")
    public String getEmptyString() {
        return "nothing";
    }

    /**
     * PUT method for updating or creating an instance of VoidResource
     *
     * @param content representation for the resource
     * @return an HTTP response with content of the updated or created resource.
     */
    @PUT
    @Consumes("application/json")
    public void putJson(String content) {
    }
}
