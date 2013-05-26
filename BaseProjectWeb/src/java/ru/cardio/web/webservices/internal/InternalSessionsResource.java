/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.cardio.web.webservices.internal;

import com.google.gson.Gson;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import ru.cardio.core.jpa.entity.CardioSession;
import ru.cardio.core.managers.CardioSessionManagerLocal;
import ru.cardio.core.managers.IndicatorHashManagerLocal;
import ru.cardio.core.managers.IndicatorsManagerLocal;
import ru.cardio.core.utils.IndicatorUtils;
import ru.cardio.core.utils.SimpleSession;
import ru.cardio.core.utils.TokenUtils;
import ru.cardio.exceptions.CardioException;
import ru.cardio.indicators.HRVIndicatorsService;
import ru.cardio.json.additionals.JsonResponse;
import ru.cardio.json.additionals.ResponseConstants;
import ru.cardio.web.json.utils.SecureCardioExceptionWrapper;
import ru.cardio.web.json.utils.SecureResponseWrapper;
import web.utils.SessionUtils;

/**
 * REST Web Service
 *
 * @author rogvold
 */
@Path("internal_sessions")
@Stateless
public class InternalSessionsResource {

    @Context
    private UriInfo context;
    @EJB
    CardioSessionManagerLocal cardMan;
    @EJB
    IndicatorHashManagerLocal hashMan;

    /**
     * Creates a new instance of InternalSessionsResource
     */
    public InternalSessionsResource() {
    }

    /**
     * Retrieves representation of an instance of
     * ru.cardio.web.webservices.internal.InternalSessionsResource
     *
     * @return an instance of java.lang.String
     */
    @GET
    @Produces("application/json")
    public String getJson() {
        //TODO return proper representation object
        throw new UnsupportedOperationException();
    }

    /**
     * PUT method for updating or creating an instance of
     * InternalSessionsResource
     *
     * @param content representation for the resource
     * @return an HTTP response with content of the updated or created resource.
     */
    @PUT
    @Consumes("application/json")
    public void putJson(String content) {
    }

    @POST
    @Produces("application/json")
    @Path("update_session_description")
    public String updateSessionDescription(@Context HttpServletRequest req, @QueryParam("sessionId") Long sessionId, @FormParam("description") String description) {
        try {
            System.out.println("description = " + description);
            Long userId = SessionUtils.getCurrentUserIdThrowingException(req);
            cardMan.checkRights(userId, sessionId);
            cardMan.updateSessionDescription(sessionId, description);
            JsonResponse<String> jr = new JsonResponse<String>(ResponseConstants.OK, null, ResponseConstants.YES);
            return SecureResponseWrapper.getJsonResponse(jr);
        } catch (CardioException e) {
            return SecureCardioExceptionWrapper.wrapException(e);
        }
    }

    @GET
    @Produces("application/json")
    @Path("session_descrtiption")
    public String getSessionComment(@Context HttpServletRequest req, @QueryParam("sessionId") Long sessionId) {
        try {
            Long userId = SessionUtils.getCurrentUserIdThrowingException(req);
            cardMan.checkRights(userId, sessionId);
            CardioSession cs = cardMan.getCardioSessionById(sessionId);
            JsonResponse<String> jr = new JsonResponse<String>(ResponseConstants.OK, null, cs.getDescription());
            return SecureResponseWrapper.getJsonResponse(jr);
        } catch (CardioException e) {
            return SecureCardioExceptionWrapper.wrapException(e);
        }
    }

    @GET
    @Produces("application/json")
    @Path("all")
    public String getHistorySessions(@Context HttpServletRequest req) {
        try {
            Long userId = SessionUtils.getCurrentUserIdThrowingException(req);
            JsonResponse<List<SimpleSession>> jr = new JsonResponse<List<SimpleSession>>(ResponseConstants.OK, null, IndicatorUtils.getUserSimpleSessions(userId, hashMan, cardMan));
            return SecureResponseWrapper.getJsonResponse(jr);
        } catch (CardioException e) {
            return SecureCardioExceptionWrapper.wrapException(e);
        }
    }

    @GET
    @Produces("application/json")
    @Path("tension")
    public String getTensionIndex(@Context HttpServletRequest req, @QueryParam("sessionId") Long sessionId) {
        try {
            Long userId = SessionUtils.getCurrentUserIdThrowingException(req);
//            float[][] newArray = hashMan.getParameterPlotArray(sessionId, new HRVIndicatorsService(), "IN", false);
//            JsonResponse<float[][]> jr = new JsonResponse<float[][]>(ResponseConstants.OK, null, newArray);
            JsonResponse<String> jr = new JsonResponse<String>(ResponseConstants.OK, null, hashMan.getCalculatedParameterJsonPlot(sessionId, new HRVIndicatorsService(), "IN", false));
            String rString = SecureResponseWrapper.getJsonResponse(jr);
            rString = rString.replace("\"[[", "[[");
            rString = rString.replace("]]\"}", "]]}");
            return rString;
        } catch (CardioException e) {
            return SecureCardioExceptionWrapper.wrapException(e);
        }
    }
}
