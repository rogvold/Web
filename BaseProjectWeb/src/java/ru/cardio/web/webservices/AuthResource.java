package ru.cardio.web.webservices;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import ru.cardio.core.jpa.entity.User;
import ru.cardio.core.managers.UserManagerLocal;
import ru.cardio.exceptions.CardioException;
import ru.cardio.helpers.UserHelper;
import ru.cardio.json.additionals.JsonResponse;
import ru.cardio.json.additionals.ResponseConstants;
import ru.cardio.web.json.utils.CardioExceptionWrapper;
import ru.cardio.web.json.utils.SecureResponseWrapper;
import ru.cardio.web.json.utils.SimpleResponseWrapper;
import ru.cardio.web.webservices.utils.WebServiceUtils;
import web.utils.SessionListener;
import web.utils.SessionUtils;

/**
 * REST Web Service
 *
 * @author rogvold
 */
@Path("auth")
@Stateless
public class AuthResource {

    private static final String YES = "1";
    private static final String NO = "0";
    private static final String ERROR = "-1";
    @EJB
    UserManagerLocal userMan;
    @Context
    private UriInfo context;

    /**
     * Creates a new instance of AuthResource
     */
    public AuthResource() {
    }

    private class SimpleResponse {

        private String response;

        public SimpleResponse(String response) {
            this.response = response;
        }

        public String getResponse() {
            return response;
        }

        public void setResponse(String response) {
            this.response = response;
        }
    }

    /**
     * Retrieves representation of an instance of
     * ru.cardio.web.webservices.AuthResource
     *
     * @return an instance of java.lang.String
     */
    @GET
    @Produces("application/xml")
    public String getXml() {
        //TODO return proper representation object
        throw new UnsupportedOperationException();
    }

    @POST
    @Produces("application/json")
    @Path("check_data")
    public String checkUserAuthorisationData(@QueryParam("secret") String secret, @QueryParam("email") String email, @QueryParam("password") String password) {
        try {
            WebServiceUtils.checkSecret(secret);
            return SimpleResponseWrapper.getJsonResponse(userMan.checkEmailAndPassword(email, password) ? YES : NO);
        } catch (CardioException e) {
            return CardioExceptionWrapper.wrapException(e);
        }
    }

    @POST
    @Produces("application/json")
    @Path("check_existence")
    public String checkEmailExistence(@QueryParam("secret") String secret, @QueryParam("email") String email) {
        try {
            WebServiceUtils.checkSecret(secret);
            return SimpleResponseWrapper.getJsonResponse(userMan.userExistsByEmail(email) ? YES : NO);
        } catch (CardioException e) {
            return CardioExceptionWrapper.wrapException(e);
        }
    }

    @POST
    @Produces("application/json")
    @Path("register")
    public String registerUser(@QueryParam("secret") String secret, @QueryParam("email") String email, @QueryParam("password") String password) {
        try {
            WebServiceUtils.checkSecret(secret);
            return SimpleResponseWrapper.getJsonResponse((userMan.registerNewUser(email, password, "", "", User.USER)) != null ? YES : NO);
        } catch (CardioException e) {
            return CardioExceptionWrapper.wrapException(e);
        }
    }

    @POST
    @Produces("application/json")
    @Path("update_info")
    public String updateInfo(@QueryParam("secret") String secret, @QueryParam("json") String json) {
        try {
            WebServiceUtils.checkSecret(secret);
            userMan.updateInfo(UserHelper.getSimpleUserFromJson(json));
            return SimpleResponseWrapper.getJsonResponse(YES);
        } catch (CardioException e) {
            return CardioExceptionWrapper.wrapException(e);
        }
    }

    @POST
    @Produces("application/json")
    @Path("update_info_form")
    public String updateInfoForm(@QueryParam("secret") String secret, @FormParam("json") String json) {
        try {
            WebServiceUtils.checkSecret(secret);
            userMan.updateInfo(UserHelper.getSimpleUserFromJson(json));
            return SimpleResponseWrapper.getJsonResponse(YES);
        } catch (CardioException e) {
            return CardioExceptionWrapper.wrapException(e);
        }
    }

    @POST
    @Produces("application/json")
    @Path("info")
    public String getUserInfo(@QueryParam("secret") String secret, @QueryParam("email") String email, @QueryParam("password") String password) {
        try {
            WebServiceUtils.checkSecret(secret);
            return UserHelper.getJsonFromSimpleUser(userMan.getSimpleInfo(email, password));
        } catch (CardioException e) {
            return CardioExceptionWrapper.wrapException(e);
        }
    }

    @GET
    @Produces("text/html")
    @Path("available")
    public String checkAvailability() {
        return "status: \"available\"";
    }

    @GET
    @Produces("text/html")
    @Path("logout")
    public String logout(@Context HttpServletRequest req) {
        try {
            Long userId = SessionUtils.getCurrentUserIdThrowingException(req);
            SessionListener.setSessionAttribute(req.getSession(true), "userId", null);
            JsonResponse<String> jr = new JsonResponse<String>(ResponseConstants.OK, null, ResponseConstants.YES);
            return SecureResponseWrapper.getJsonResponse(jr);
        } catch (CardioException e) {
            return CardioExceptionWrapper.wrapException(e);
        }
    }

    /**
     * PUT method for updating or creating an instance of AuthResource
     *
     * @param content representation for the resource
     * @return an HTTP response with content of the updated or created resource.
     */
    @PUT
    @Consumes("application/xml")
    public void putXml(String content) {
    }
}
