package ru.cardio.web.webservices.internal;

import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import ru.cardio.core.entity.EvaluationPoint;
import ru.cardio.core.entity.SimpleEvaluation;
import ru.cardio.core.entity.SimplePoint;
import ru.cardio.core.entity.SyncSession;
import ru.cardio.core.jpa.entity.CardioSession;
import ru.cardio.core.jpa.entity.User;
import ru.cardio.core.managers.*;
import ru.cardio.core.utils.IndicatorUtils;
import ru.cardio.core.utils.SimpleSession;
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
    @EJB
    UserManagerLocal userMan;
    @EJB
    CardioSessionEvaluationManagerLocal cseMan;

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

    @GET
    @Produces("application/json")
    @Path("current_pulse")
    public String getCurrentPulse(@Context HttpServletRequest req) {
        try {
            Long userId = SessionUtils.getCurrentUserIdThrowingException(req);
            JsonResponse<Double> jr = new JsonResponse<Double>(ResponseConstants.OK, null, cardMan.getCurrentPulseOfUser(userId));
            return SecureResponseWrapper.getJsonResponse(jr);
        } catch (CardioException e) {
            return SecureCardioExceptionWrapper.wrapException(e);
        }
    }

    @GET
    @Produces("application/json")
    @Path("beats_amount")
    public String getBeantsAmount(@Context HttpServletRequest req) {
        try {
            Long userId = SessionUtils.getCurrentUserIdThrowingException(req);
            User u = userMan.getUserById(userId);
            JsonResponse<Long> jr = new JsonResponse<Long>(ResponseConstants.OK, null, u.getBeatsAmount());
            return SecureResponseWrapper.getJsonResponse(jr);
        } catch (CardioException e) {
            return SecureCardioExceptionWrapper.wrapException(e);
        }
    }

    @GET
    @Produces("application/json")
    @Path("current_tension")
    public String getCurrentTension(@Context HttpServletRequest req) {
        try {
            Long userId = SessionUtils.getCurrentUserIdThrowingException(req);
            JsonResponse<Double> jr = new JsonResponse<Double>(ResponseConstants.OK, null, cardMan.getCurrentTensionOfUser(userId));
            return SecureResponseWrapper.getJsonResponse(jr);
        } catch (CardioException e) {
            return SecureCardioExceptionWrapper.wrapException(e);
        }
    }

    @GET
    @Produces("application/json")
    @Path("is_active")
    public String isActive(@Context HttpServletRequest req) {
        try {
            Long userId = SessionUtils.getCurrentUserIdThrowingException(req);
            JsonResponse<Integer> jr = new JsonResponse<Integer>(ResponseConstants.OK, null, userMan.isActive(userId) ? 1 : 0);
            return SecureResponseWrapper.getJsonResponse(jr);
        } catch (CardioException e) {
            return SecureCardioExceptionWrapper.wrapException(e);
        }
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
    @Path("rates")
    public String getUserRates(@Context HttpServletRequest req, @QueryParam("sessionId") Long sessionId) {
        try {
            //TODO: check rights
            if (sessionId == null) {
                throw new CardioException("getUserRates: sessionId is null");
            }
            List<Integer> list = cardMan.getRatesInCardioSession(sessionId, false);
            JsonResponse<List<Integer>> jr = new JsonResponse<List<Integer>>(ResponseConstants.OK, null, list);
            return SecureResponseWrapper.getJsonResponse(jr);
        } catch (CardioException e) {
            return SecureCardioExceptionWrapper.wrapException(e);
        }
    }

    @GET
    @Produces("application/json")
    @Path("tension_array")
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

    @GET
    @Produces("application/json")
    @Path("last_data_received")
    public String getLastDataTimestamp(@Context HttpServletRequest req) {
        try {
            Long userId = SessionUtils.getCurrentUserIdThrowingException(req);
            User u = userMan.getUserById(userId);
            JsonResponse<Long> jr = new JsonResponse<Long>(ResponseConstants.OK, null, u.getLastDataRecievedDate().getTime());
            return SecureResponseWrapper.getJsonResponse(jr);
        } catch (CardioException e) {
            return SecureCardioExceptionWrapper.wrapException(e);
        }
    }

    @POST
    @Produces("application/json")
    @Path("session_evaluation")
    public String pullSessionList(@FormParam("sessionId") Long sessionId) {
        try {
            SyncSession ss = cseMan.getSessionWithEvaluation(sessionId);
            JsonResponse<SyncSession> jr = new JsonResponse<SyncSession>(ResponseConstants.OK, null, ss);
            return SecureResponseWrapper.getJsonResponse(jr);
        } catch (CardioException e) {
            return SecureCardioExceptionWrapper.wrapException(e);
        }
    }

    @POST
    @Produces("application/json")
    @Path("last_rates")
    public String lastRates(@Context HttpServletRequest req, @FormParam("amount") int amount) {
        try {
            Long userId = SessionUtils.getCurrentUserIdThrowingException(req);
            List<SimplePoint> sList = cardMan.getLastRates(userId, amount);
            JsonResponse<List<SimplePoint>> jr = new JsonResponse<List<SimplePoint>>(ResponseConstants.OK, null, sList);
            return SecureResponseWrapper.getJsonResponse(jr);
        } catch (CardioException e) {
            return SecureCardioExceptionWrapper.wrapException(e);
        }
    }

    @POST
    @Produces("application/json")
    @Path("last_session_rates")
    public String lastSessionRates(@Context HttpServletRequest req) {
        try {
            Long userId = SessionUtils.getCurrentUserIdThrowingException(req);
            List<SimplePoint> sList = cardMan.getLastSessionPoints(userId);
            JsonResponse<List<SimplePoint>> jr = new JsonResponse<List<SimplePoint>>(ResponseConstants.OK, null, sList);
            return SecureResponseWrapper.getJsonResponse(jr);
        } catch (CardioException e) {
            return SecureCardioExceptionWrapper.wrapException(e);
        }
    }

    @POST
    @Produces("application/json")
    @Path("last_session_evaluation")
    public String lastSessionEvaluation(@Context HttpServletRequest req) {
        try {
            Long userId = SessionUtils.getCurrentUserIdThrowingException(req);
            CardioSession lastCs = cardMan.getLastCardioSession(userId);
            List<EvaluationPoint> ss = (lastCs == null) ? null : cseMan.getSessionWithEvaluation(lastCs.getId()).getEvaluation();
            JsonResponse<List<EvaluationPoint>> jr = new JsonResponse<List<EvaluationPoint>>(ResponseConstants.OK, null, ss);
            return SecureResponseWrapper.getJsonResponse(jr);
        } catch (CardioException e) {
            return SecureCardioExceptionWrapper.wrapException(e);
        }
    }

    @POST
    @Produces("application/json")
    @Path("current_session_id")
    public String currentSessionId(@Context HttpServletRequest req) {
        try {
            Long userId = SessionUtils.getCurrentUserIdThrowingException(req);
            CardioSession lastCs = cardMan.getLastCardioSession(userId);
            JsonResponse<Long> jr = new JsonResponse<Long>(ResponseConstants.OK, null, lastCs == null ? null : lastCs.getId());
            return SecureResponseWrapper.getJsonResponse(jr);
        } catch (CardioException e) {
            return SecureCardioExceptionWrapper.wrapException(e);
        }
    }

    @POST
    @Produces("application/json")
    @Path("last_evaluation")
    public String lastEvaluation(@Context HttpServletRequest req, @FormParam("amount") int amount) {
        try {
            Long userId = SessionUtils.getCurrentUserIdThrowingException(req);
            CardioSession lastCs = cardMan.getLastCardioSession(userId);
            List<EvaluationPoint> ss = (lastCs == null) ? null : cseMan.getSessionWithEvaluation(lastCs.getId()).getEvaluation();
            if (ss != null && !ss.isEmpty()) {
                if (ss.size() > amount) {
                    ss = ss.subList(ss.size() - amount, ss.size());
                }
            }
            JsonResponse<List<EvaluationPoint>> jr = new JsonResponse<List<EvaluationPoint>>(ResponseConstants.OK, null, ss);

            return SecureResponseWrapper.getJsonResponse(jr);
        } catch (CardioException e) {
            return SecureCardioExceptionWrapper.wrapException(e);
        }
    }
//    @POST
//    @Produces("application/json")
//    @Path("last_sync_session")
//    public String lastSyncSession(@Context HttpServletRequest req, @FormParam("amount") int amount) {
//        try {
//            if (amount == -1){
//                amount = Integer.MAX_VALUE;
//            }
//            Long userId = SessionUtils.getCurrentUserIdThrowingException(req);
//            CardioSession lastCs = cardMan.getLastCardioSession(userId);
//            SyncSession ss = (lastCs == null) ? null : cseMan.getSessionWithEvaluation(lastCs.getId());
//            if (ss != null && !ss.isEmpty()) {
//                if (ss.size() > amount) {
//                    ss = ss.subList(ss.size() - amount, ss.size());
//                }
//            }
//            JsonResponse<List<EvaluationPoint>> jr = new JsonResponse<List<EvaluationPoint>>(ResponseConstants.OK, null, ss);
//
//            return SecureResponseWrapper.getJsonResponse(jr);
//        } catch (CardioException e) {
//            return SecureCardioExceptionWrapper.wrapException(e);
//        }
//    }
}
