package ru.cardio.web.webservices.secure;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import ru.cardio.core.jpa.entity.Token;
import ru.cardio.core.managers.TokenManagerLocal;
import ru.cardio.core.managers.UserManagerLocal;
import ru.cardio.exceptions.CardioException;
import ru.cardio.json.additionals.JsonResponse;
import ru.cardio.json.additionals.ResponseConstants;
import ru.cardio.json.entity.JsonError;
import ru.cardio.json.entity.SimpleToken;
import ru.cardio.web.json.utils.SecureCardioExceptionWrapper;
import ru.cardio.web.json.utils.SecureResponseWrapper;

/**
 * REST Web Service
 *
 * @author rogvold
 */
@Path("token")
@Stateless
public class TokenResource {

    @Context
    private UriInfo context;
    @EJB
    TokenManagerLocal tokenMan;
    @EJB
    UserManagerLocal userMan;

    /**
     * Creates a new instance of TokenResource
     */
    public TokenResource() {
    }

    @POST
    @Path("authorize")
    @Produces("application/json")
    public String getNewToken(@FormParam("email") String email, @FormParam("password") String password, @FormParam("deviceId") String deviceId) {
        try {
            userMan.checkAuthInfo(email, password);
            
            System.out.println("trying to create token");
            
            tokenMan.createToken(email, password, deviceId);
            System.out.println("token created");
            JsonResponse<SimpleToken> jr = new JsonResponse<SimpleToken>(ResponseConstants.OK, null, tokenMan.getUserToken(userMan.getUserByEmail(email).getId()));
            return SecureResponseWrapper.getJsonResponse(jr);
        } catch (CardioException e) {
            System.out.println("getNewToken: exc = " + e.getMessage());
            return SecureCardioExceptionWrapper.wrapException(e);
        }
    }

    @POST
    @Path("reset")
    @Produces("application/json")
    public String resetToken(@FormParam("token") String tokenString) {
        try {
            Long userId = tokenMan.getUserIdByToken(tokenString);
            tokenMan.resetToken(userId);
            JsonResponse<String> jr = new JsonResponse<String>(ResponseConstants.OK, null, ResponseConstants.YES);
            return SecureResponseWrapper.getJsonResponse(jr);
        } catch (CardioException e) {
            return SecureCardioExceptionWrapper.wrapException(e);
        }
    }
    
//    @POST
//    @Path("delete_token")
//    @Pro
}
