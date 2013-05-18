package ru.cardio.web.webservices.secure;

import com.google.gson.Gson;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import ru.cardio.core.managers.MailManagerLocal;
import ru.cardio.core.managers.TokenManagerLocal;
import ru.cardio.core.utils.TokenUtils;
import ru.cardio.exceptions.CardioException;
import ru.cardio.json.additionals.JsonResponse;
import ru.cardio.json.additionals.ResponseConstants;
import ru.cardio.json.entity.SimpleMail;
import ru.cardio.web.json.utils.CardioExceptionWrapper;
import ru.cardio.web.json.utils.SecureResponseWrapper;

/**
 * REST Web Service
 *
 * @author rogvold
 */
@Path("email")
@Stateless
public class SecureMailResource {

    @Context
    private UriInfo context;
    @EJB
    MailManagerLocal mailMan;
    @EJB
    TokenManagerLocal tokenMan;

    /**
     * Creates a new instance of SecureMailResource
     */
    public SecureMailResource() {
    }

    @POST
    @Produces("application/json")
    @Path("alert")
    public String alertMessage(@FormParam("token") String token, @FormParam("json") String json) {
        try {
            TokenUtils.checkToken(tokenMan, token);
            System.out.println("alertMessage: json = " + json);
            SimpleMail sm = (new Gson()).fromJson(json, SimpleMail.class);
            if (sm == null) throw new CardioException("SimpleEmail is null");
            mailMan.sendMail(sm.getRecipient(), sm.getTheme(), sm.getTimestamp(), sm.getText());

            JsonResponse<String> jr = new JsonResponse<String>(ResponseConstants.OK, null, ResponseConstants.YES);
            return SecureResponseWrapper.getJsonResponse(jr);
        } catch (CardioException e) {
            return CardioExceptionWrapper.wrapException(e);
        }
    }
}
