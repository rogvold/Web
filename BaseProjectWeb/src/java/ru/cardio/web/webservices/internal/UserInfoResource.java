package ru.cardio.web.webservices.internal;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import ru.cardio.core.jpa.entity.User;
import ru.cardio.core.managers.UserManagerLocal;
import ru.cardio.exceptions.CardioException;
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
@Path("UserInfo")
@Stateless
public class UserInfoResource {

    @Context
    private UriInfo context;
    @EJB
    UserManagerLocal userMan;

    /**
     * Creates a new instance of UserInfoResource
     */
    public UserInfoResource() {
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
}
