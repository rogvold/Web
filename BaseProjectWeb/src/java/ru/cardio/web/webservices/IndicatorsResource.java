package ru.cardio.web.webservices;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import ru.cardio.core.managers.CardioSessionManagerLocal;
import ru.cardio.core.managers.IndicatorHashManagerLocal;
import ru.cardio.core.managers.UserManagerLocal;
import ru.cardio.indicators.HRVIndicatorsService;
import ru.cardio.web.json.utils.CardioExceptionWrapper;
import ru.cardio.web.webservices.utils.WebServiceUtils;

/**
 * REST Web Service
 *
 * @author rogvold
 */
@Path("indicators")
@Stateless
public class IndicatorsResource {

    @EJB
    UserManagerLocal userMan;
    
    @EJB
    IndicatorHashManagerLocal hashMan;
    
    @EJB
    CardioSessionManagerLocal csMan;
    
    @Context
    private UriInfo context;

    /**
     * Creates a new instance of IndicatorsResource
     */
    public IndicatorsResource() {
    }

    /**
     * Retrieves representation of an instance of
     * ru.cardio.web.webservices.IndicatorsResource
     *
     * @return an instance of java.lang.String
     */
    @GET
    @Produces("application/json")
    public String getJson() {
        //TODO return proper representation object
        throw new UnsupportedOperationException();
    }

    @POST
    @Produces("application/json")
    @Path("{sessionId}/tension")
    public String getTensionIndex(@QueryParam("secret") String secret, @QueryParam("email") String email, @QueryParam("password") String password, @PathParam("sessionId") Long sessionId){
        try {
            WebServiceUtils.checkSecret(secret);
//            userMan.checkAuthInfo(email, password);
            csMan.checkRights(email, password, sessionId);
            return hashMan.getCalculatedParameterJsonPlot(sessionId, new HRVIndicatorsService(), "IN", false);
        } catch (Exception e) {
            return CardioExceptionWrapper.wrapException(e);
        }
    }

    /**
     * PUT method for updating or creating an instance of IndicatorsResource
     *
     * @param content representation for the resource
     * @return an HTTP response with content of the updated or created resource.
     */
    @PUT
    @Consumes("application/json")
    public void putJson(String content) {
    }
}
