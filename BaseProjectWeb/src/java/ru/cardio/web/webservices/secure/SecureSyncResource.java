package ru.cardio.web.webservices.secure;

import com.google.gson.Gson;
import java.util.Date;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import ru.cardio.core.entity.SyncPullData;
import ru.cardio.core.entity.SyncPushData;
import ru.cardio.core.managers.TokenManagerLocal;
import ru.cardio.core.managers.UserManagerLocal;
import ru.cardio.core.utils.TokenUtils;
import ru.cardio.exceptions.CardioException;
import ru.cardio.json.additionals.JsonResponse;
import ru.cardio.json.additionals.ResponseConstants;
import ru.cardio.json.entity.SimpleUser;
import ru.cardio.web.json.utils.SecureCardioExceptionWrapper;
import ru.cardio.web.json.utils.SecureResponseWrapper;
import ru.cardio.core.jpa.entity.User;
import ru.cardio.core.managers.CardioSessionEvaluationManagerLocal;
import ru.cardio.helpers.UserHelper;

/**
 * REST Web Service
 *
 * @author rogvold
 */
@Path("sync")
@Stateless
public class SecureSyncResource {

    @Context
    private UriInfo context;
    @EJB
    UserManagerLocal userMan;
    @EJB
    TokenManagerLocal tokenMan;
    @EJB
    CardioSessionEvaluationManagerLocal cseMan;

    /**
     * Creates a new instance of SecureSyncResource
     */
    public SecureSyncResource() {
    }

    /**
     * Retrieves representation of an instance of
     * ru.cardio.web.webservices.secure.SecureSyncResource
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
     * PUT method for updating or creating an instance of SecureSyncResource
     *
     * @param content representation for the resource
     * @return an HTTP response with content of the updated or created resource.
     */
    @PUT
    @Consumes("application/json")
    public void putJson(String content){
    }

    @POST
    @Produces("application/json")
    @Path("get_user_info_version")
    public String checkUserAuthorisationData(@FormParam("token") String token) {
        try {
            TokenUtils.checkToken(tokenMan, token);
            Long userId = tokenMan.getUserIdByToken(token);
            JsonResponse<Integer> jr = new JsonResponse<Integer>(ResponseConstants.OK, null, userMan.getUserInfoVersion(userId));
            return SecureResponseWrapper.getJsonResponse(jr);
        } catch (CardioException e) {
            return SecureCardioExceptionWrapper.wrapException(e);
        }
    }

    @POST
    @Produces("application/json")
    @Path("pull_user_info")
    public String getUserInfo(@FormParam("token") String token, @FormParam("local_counter") Integer counter) {
        try {
            TokenUtils.checkToken(tokenMan, token);
            Long userId = tokenMan.getUserIdByToken(token);
            User u = userMan.getUserById(userId);
            SimpleUser su = null;
            if (!u.getUserInfoUpdateCount().equals(counter)) {
                su = userMan.getSimpleInfo(userId);
            }
            if (su != null && su.getBirthDate() == null) {
                su.setBirthDate(new Date());
            }
            JsonResponse<SimpleUser> jr = new JsonResponse<SimpleUser>(ResponseConstants.OK, null, su);
            return SecureResponseWrapper.getJsonResponse(jr);
        } catch (CardioException e) {
            return SecureCardioExceptionWrapper.wrapException(e);
        }
    }

    @POST
    @Produces("application/json")
    @Path("push_user_info")
    public String updateInfo(@FormParam("token") String token, @FormParam("json") String json) {
        try {
            TokenUtils.checkToken(tokenMan, token);
            userMan.updateInfo(UserHelper.getSimpleUserFromJson(json));
            Long userId = tokenMan.getUserIdByToken(token);
            User u = userMan.getUserById(userId);

            JsonResponse<Integer> jr = new JsonResponse<Integer>(ResponseConstants.OK, null, u.getUserInfoUpdateCount());
            return SecureResponseWrapper.getJsonResponse(jr);
        } catch (CardioException e) {
            return SecureCardioExceptionWrapper.wrapException(e);
        }
    }

    @POST
    @Produces("application/json")
    @Path("push_session_list")
    public String pushSessionList(@FormParam("token") String token, @FormParam("json") String json) {
        try {
            TokenUtils.checkToken(tokenMan, token);
            Long userId = tokenMan.getUserIdByToken(token);
            Gson gson = new Gson();
            System.out.println("pushSessionList: json = " + json);
            try {
                SyncPushData spd = gson.fromJson(json, SyncPushData.class);
            } catch (Exception e) {
                throw new CardioException("parse error: " + e.getMessage());
            }

            Long cntr = cseMan.pushSessions(userId, gson.fromJson(json, SyncPushData.class));

            JsonResponse<Long> jr = new JsonResponse<Long>(ResponseConstants.OK, null, cntr);
            System.out.println("push response: " + SecureResponseWrapper.getJsonResponse(jr));
            return SecureResponseWrapper.getJsonResponse(jr);
        } catch (CardioException e) {
            return SecureCardioExceptionWrapper.wrapException(e);
        }
    }

    @POST
    @Produces("application/json")
    @Path("pull_session_list")
    public String pullSessionList(@FormParam("token") String token, @FormParam("local_counter") Long localCounter) {
        try {
            TokenUtils.checkToken(tokenMan, token);
            Long userId = tokenMan.getUserIdByToken(token);
            SyncPullData spd = cseMan.pullSessions(userId, localCounter);
            System.out.println("pull response: spd = " + spd);
            JsonResponse<SyncPullData> jr = new JsonResponse<SyncPullData>(ResponseConstants.OK, null, spd);
            return SecureResponseWrapper.getJsonResponse(jr);
        } catch (CardioException e) {
            return SecureCardioExceptionWrapper.wrapException(e);
        }
    }

}
