package ru.cardio.web.webservices.internal;

import java.util.Date;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import ru.cardio.core.jpa.entity.User;
import ru.cardio.core.managers.AttachmentManagerLocal;
import ru.cardio.core.managers.TokenManagerLocal;
import ru.cardio.core.managers.UserManagerLocal;
import ru.cardio.core.utils.TokenUtils;
import ru.cardio.exceptions.CardioException;
import ru.cardio.helpers.UserHelper;
import ru.cardio.json.additionals.JsonResponse;
import ru.cardio.json.additionals.ResponseConstants;
import ru.cardio.json.entity.SimpleUser;
import ru.cardio.web.json.utils.SecureCardioExceptionWrapper;
import ru.cardio.web.json.utils.SecureResponseWrapper;
import web.utils.SessionUtils;

/**
 * REST Web Service
 *
 * @author rogvold
 */
@Path("UserInfo")
@Stateless
public class UserInfoResource {

    @Context
    private UriInfo context;
    @EJB
    UserManagerLocal userMan;
    @EJB
    AttachmentManagerLocal attMan;

    /**
     * Creates a new instance of UserInfoResource
     */
    public UserInfoResource() {
    }

    @POST
    @Produces("application/json")
    @Path("update_info")
    public String updateInfo(@Context HttpServletRequest req, @FormParam("json") String json) {
        try {
            userMan.updateInfo(SessionUtils.getCurrentUserIdThrowingException(req), UserHelper.getSimpleUserFromJson(json));
            JsonResponse<String> jr = new JsonResponse<String>(ResponseConstants.OK, null, ResponseConstants.YES);
            return SecureResponseWrapper.getJsonResponse(jr);
        } catch (CardioException e) {
            return SecureCardioExceptionWrapper.wrapException(e);
        }
    }

    @GET
    @Produces("application/json")
    @Path("get_info")
    public String getInfo(@Context HttpServletRequest req) {
        try {
            Long userId = SessionUtils.getCurrentUserIdThrowingException(req);
            SimpleUser su = userMan.getSimpleInfo(userId);
            if (su.getBirthDate() == null) {
                su.setBirthDate(new Date());
            }
            JsonResponse<SimpleUser> jr = new JsonResponse<SimpleUser>(ResponseConstants.OK, null, su);
            return SecureResponseWrapper.getJsonResponse(jr);
        } catch (CardioException e) {
            return SecureCardioExceptionWrapper.wrapException(e);
        }
    }

    @GET
    @Produces("application/json")
    @Path("update_avatar")
    public String updateAvatar(@Context HttpServletRequest req, @QueryParam("id") Long avId) {
        try {
            userMan.updateAvatar(SessionUtils.getCurrentUserIdThrowingException(req), avId);
            JsonResponse<String> jr = new JsonResponse<String>(ResponseConstants.OK, null, ResponseConstants.YES);
            return SecureResponseWrapper.getJsonResponse(jr);
        } catch (CardioException e) {
            return SecureCardioExceptionWrapper.wrapException(e);
        }
    }

    @GET
    @Produces("application/json")
    @Path("crop_avatar")
    public String cropAvatar(@Context HttpServletRequest req, @QueryParam("x") Integer x, @QueryParam("y") Integer y, @QueryParam("w") Integer w, @QueryParam("h") Integer h) {
        try {
            Long userId = SessionUtils.getCurrentUserIdThrowingException(req);
            attMan.cropAvatar(userId, x, y, w, h);
            JsonResponse<String> jr = new JsonResponse<String>(ResponseConstants.OK, null, ResponseConstants.YES);
            return SecureResponseWrapper.getJsonResponse(jr);
        } catch (CardioException e) {
            return SecureCardioExceptionWrapper.wrapException(e);
        }
    }

    @GET
    @Produces("application/json")
    @Path("crop_image")
    public String cropAvatar(@Context HttpServletRequest req, @QueryParam("attId") Long attId, @QueryParam("x") Integer x, @QueryParam("y") Integer y, @QueryParam("w") Integer w, @QueryParam("h") Integer h) {
        try {
            Long userId = SessionUtils.getCurrentUserIdThrowingException(req);
//            attMan.cropAvatar(userId, x, y, w, h);
            attMan.cropExistingFile(userId, attId, x, y, w, h);
            JsonResponse<String> jr = new JsonResponse<String>(ResponseConstants.OK, null, ResponseConstants.YES);
            return SecureResponseWrapper.getJsonResponse(jr);
        } catch (CardioException e) {
            return SecureCardioExceptionWrapper.wrapException(e);
        }
    }
}
