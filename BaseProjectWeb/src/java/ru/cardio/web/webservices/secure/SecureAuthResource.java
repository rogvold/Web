package ru.cardio.web.webservices.secure;

import java.util.Date;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import ru.cardio.core.jpa.entity.User;
import ru.cardio.core.managers.TokenManagerLocal;
import ru.cardio.core.managers.UserManagerLocal;
import ru.cardio.core.utils.TokenUtils;
import ru.cardio.core.utils.UserUtils;
import ru.cardio.exceptions.CardioException;
import ru.cardio.helpers.UserHelper;
import ru.cardio.json.additionals.JsonResponse;
import ru.cardio.json.additionals.ResponseConstants;
import ru.cardio.json.entity.SimpleUser;
import ru.cardio.web.json.utils.SecureCardioExceptionWrapper;
import ru.cardio.web.json.utils.SecureResponseWrapper;
import web.utils.SessionListener;

/**
 * REST Web Service
 *
 * @author rogvold
 */
@Path("SecureAuth")
@Stateless
public class SecureAuthResource {
    
    @Context
    private UriInfo context;
    @EJB
    UserManagerLocal userMan;
    @EJB
    TokenManagerLocal tokenMan;

    /**
     * Creates a new instance of SecureAuthResource
     */
    public SecureAuthResource() {
    }
    
    @POST
    @Produces("application/json")
    @Path("check_existence")
    public String checkEmailExistence(@FormParam("email") String email) {
        try {
            JsonResponse<String> jr = new JsonResponse<String>(ResponseConstants.OK, null, userMan.userExistsByEmail(email) ? ResponseConstants.YES : ResponseConstants.NO);
            return SecureResponseWrapper.getJsonResponse(jr);
        } catch (CardioException e) {
            return SecureCardioExceptionWrapper.wrapException(e);
        }
    }
    
    @POST
    @Produces("application/json")
    @Path("check_data")
    public String checkUserAuthorisationData(@FormParam("email") String email, @FormParam("password") String password) {
        try {
            JsonResponse<String> jr = new JsonResponse<String>(ResponseConstants.OK, null, userMan.checkEmailAndPassword(email, password) ? ResponseConstants.YES : ResponseConstants.NO);
            return SecureResponseWrapper.getJsonResponse(jr);
        } catch (CardioException e) {
            return SecureCardioExceptionWrapper.wrapException(e);
        }
    }
    
    @POST
    @Produces("application/json")
    @Path("register")
    public String registerUser(@FormParam("email") String email, @FormParam("password") String password) {
        try {
            User regUser = userMan.registerNewUser(email, password, "", "", User.USER);
            JsonResponse<String> jr = new JsonResponse<String>(ResponseConstants.OK, null, regUser != null ? ResponseConstants.YES : ResponseConstants.NO);
            return SecureResponseWrapper.getJsonResponse(jr);
        } catch (CardioException e) {
            return SecureCardioExceptionWrapper.wrapException(e);
        }
    }
    
    @POST
    @Produces("application/json")
    @Path("info")
    public String getUserInfo(@FormParam("token") String token) {
        try {
            TokenUtils.checkToken(tokenMan, token);
            Long userId = tokenMan.getUserIdByToken(token);
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
    
    @POST
    @Produces("application/json")
    @Path("update_info")
    public String updateInfo(@FormParam("token") String token, @FormParam("json") String json) {
        try {
            TokenUtils.checkToken(tokenMan, token);
            userMan.updateInfo(UserHelper.getSimpleUserFromJson(json));
            JsonResponse<String> jr = new JsonResponse<String>(ResponseConstants.OK, null, ResponseConstants.YES);
            return SecureResponseWrapper.getJsonResponse(jr);
        } catch (CardioException e) {
            return SecureCardioExceptionWrapper.wrapException(e);
        }
    }
    
    @POST
    @Produces("application/json")
    @Path("create_session")
    public String createSession(@Context HttpServletRequest req, @FormParam("email") String email, @FormParam("password") String password) {
        try {
            JsonResponse<String> jr = new JsonResponse<String>(ResponseConstants.OK, null, userMan.checkEmailAndPassword(email, password) ? ResponseConstants.YES : ResponseConstants.NO);
            SessionListener.setSessionAttribute(req.getSession(true), "userId", userMan.getUserByEmail(email).getId());
            
            return SecureResponseWrapper.getJsonResponse(jr);
        } catch (CardioException e) {
            return SecureCardioExceptionWrapper.wrapException(e);
        }
    }
    
    @POST
    @Produces("application/json")
    @Path("create_user")
    public String createNewUser(@Context HttpServletRequest req, @FormParam("email") String email, @FormParam("password") String password) {
        try {
            UserUtils.isValidEmail(email);
            UserUtils.checkPassword(password);
            JsonResponse<String> jr = new JsonResponse<String>(ResponseConstants.OK, null, ResponseConstants.YES);
            userMan.registerNewUser(email, password, null, null, User.USER);
            SessionListener.setSessionAttribute(req.getSession(true), "userId", userMan.getUserByEmail(email).getId());
            return SecureResponseWrapper.getJsonResponse(jr);
        } catch (CardioException e) {
            return SecureCardioExceptionWrapper.wrapException(e);
        }
    }
}
