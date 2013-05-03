package ru.cardio.web.webservices;

import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import ru.cardio.core.jpa.entity.CardioSession;
import ru.cardio.core.managers.CardioSessionManagerLocal;
import ru.cardio.core.managers.UserManagerLocal;
import ru.cardio.exceptions.CardioException;
import ru.cardio.json.entity.SessionInfo;
import ru.cardio.web.json.utils.CardioExceptionWrapper;
import ru.cardio.web.webservices.utils.WebServiceUtils;

/**
 * REST Web Service
 *
 * @author rogvold
 */
@Path("session")
@Stateless
public class SessionResource {

    @Context
    private UriInfo context;
    @EJB
    CardioSessionManagerLocal csMan;
    @EJB
    UserManagerLocal userMan;

    /**
     * Creates a new instance of SessionResource
     */
    public SessionResource() {
    }

    /**
     * Retrieves representation of an instance of
     * ru.cardio.web.webservices.SessionResource
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
    @Path("all")
    public String getAllSessions(@QueryParam("secret") String secret, @QueryParam("email") String email, @QueryParam("password") String password) {
        try {
            userMan.checkAuthInfo(email, password);
            List<CardioSession> list = csMan.getUserCardioSesisons(email);
            List<SessionInfo> slist = new ArrayList();
            for (CardioSession cs : list) {
                slist.add(new SessionInfo(cs.getId(), cs.getStartDate().getTime(), cs.getEndDate().getTime()));
            }
            Gson gson = new Gson();
            return gson.toJson(slist);
        } catch (CardioException ex) {
            return CardioExceptionWrapper.wrapException(ex);
        }
    }

    @POST
    @Produces("application/json")
    @Path("rates")
    public String getSessionRates(@QueryParam("secret") String secret, @QueryParam("email") String email, @QueryParam("password") String password, @QueryParam("sessionId") Long sessionId) {
        try {
            WebServiceUtils.checkSecret(secret);
            csMan.checkRights(email, password, sessionId);
            Gson gson = new Gson();
            return gson.toJson(csMan.getRatesInCardioSession(sessionId, false));
        } catch (CardioException e) {
            return CardioExceptionWrapper.wrapException(e);
        }
    }

    /**
     * PUT method for updating or creating an instance of SessionResource
     *
     * @param content representation for the resource
     * @return an HTTP response with content of the updated or created resource.
     */
    @PUT
    @Consumes("application/json")
    public void putJson(String content) {
    }
}
