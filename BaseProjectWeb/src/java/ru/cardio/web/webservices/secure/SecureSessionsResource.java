package ru.cardio.web.webservices.secure;

import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import ru.cardio.core.jpa.entity.CardioSession;
import ru.cardio.core.managers.CardioSessionManagerLocal;
import ru.cardio.core.managers.TokenManagerLocal;
import ru.cardio.core.managers.UserManagerLocal;
import ru.cardio.core.utils.TokenUtils;
import ru.cardio.exceptions.CardioException;
import ru.cardio.json.additionals.JsonResponse;
import ru.cardio.json.additionals.ResponseConstants;
import ru.cardio.json.entity.SessionInfo;
import ru.cardio.web.json.utils.SecureCardioExceptionWrapper;
import ru.cardio.web.json.utils.SecureResponseWrapper;

/**
 * REST Web Service
 *
 * @author rogvold
 */
@Path("SecureSessions")
@Stateless
public class SecureSessionsResource {

    @Context
    private UriInfo context;
    @EJB
    CardioSessionManagerLocal csMan;
    @EJB
    UserManagerLocal userMan;
    @EJB
    TokenManagerLocal tokenMan;

    /**
     * Creates a new instance of SecureSessionsResource
     */
    public SecureSessionsResource() {
    }

    @POST
    @Produces("application/json")
    @Path("all")
    public String getAllSessions(@FormParam("token") String token) {
        try {
            TokenUtils.checkToken(tokenMan, token);
            Long userId = tokenMan.getUserIdByToken(token);
            System.out.println("try to get all sessions for user id = " + userId);
            List<CardioSession> list = csMan.getUserCardioSessions(userId);
            List<SessionInfo> slist = new ArrayList();
            for (CardioSession cs : list) {
                slist.add(new SessionInfo(cs.getId(), cs.getStartDate().getTime(), cs.getEndDate().getTime()));
            }
            JsonResponse<List<SessionInfo>> jr = new JsonResponse<List<SessionInfo>>(ResponseConstants.OK, null, slist);
            return SecureResponseWrapper.getJsonResponse(jr);
        } catch (CardioException ex) {
            return SecureCardioExceptionWrapper.wrapException(ex);
        }
    }

    @POST
    @Produces("application/json")
    @Path("range")
    public String getRangeSessions(@FormParam("token") String token, @FormParam("id") Long borderId, @FormParam("amount") Integer amount) {
        try {
            TokenUtils.checkToken(tokenMan, token);
            Long userId = tokenMan.getUserIdByToken(token);
            System.out.println("try to get range sessions for user id = " + userId + "; borderId=" + borderId + ", amount=" + amount);
            List<CardioSession> list = csMan.getUserCardioSessionsBeforeId(userId, borderId, amount);
            List<SessionInfo> slist = new ArrayList();
            for (CardioSession cs : list) {
                slist.add(new SessionInfo(cs.getId(), cs.getStartDate().getTime(), cs.getEndDate().getTime()));
            }
            JsonResponse<List<SessionInfo>> jr = new JsonResponse<List<SessionInfo>>(ResponseConstants.OK, null, slist);
            return SecureResponseWrapper.getJsonResponse(jr);
        } catch (CardioException ex) {
            return SecureCardioExceptionWrapper.wrapException(ex);
        }
    }

    @POST
    @Produces("application/json")
    @Path("last_session_info")
    public String getLastSessionInfo(@FormParam("token") String token) {
        try {
            TokenUtils.checkToken(tokenMan, token);
            Long userId = tokenMan.getUserIdByToken(token);
            System.out.println("trying to get last cardio session for useId = " + userId);
            CardioSession cs = csMan.getLastCardioSession(userId);
            SessionInfo si = new SessionInfo(cs.getId(), cs.getStartDate().getTime(), cs.getEndDate().getTime());
            JsonResponse<SessionInfo> jr = new JsonResponse<SessionInfo>(ResponseConstants.OK, null, si);
            return SecureResponseWrapper.getJsonResponse(jr);
        } catch (CardioException ex) {
            return SecureCardioExceptionWrapper.wrapException(ex);
        }
    }

    @POST
    @Produces("application/json")
    @Path("rates")
    public String getSessionRates(@FormParam("token") String token, @FormParam("sessionId") Long sessionId) {
        try {
            TokenUtils.checkToken(tokenMan, token);
            csMan.checkRights(tokenMan.getUserIdByToken(token), sessionId);
            List<Integer> list = csMan.getRatesInCardioSession(sessionId, false);
            JsonResponse<List<Integer>> jr = new JsonResponse<List<Integer>>(ResponseConstants.OK, null, list);
            return SecureResponseWrapper.getJsonResponse(jr);
        } catch (CardioException e) {
            return SecureCardioExceptionWrapper.wrapException(e);
        }
    }
}
