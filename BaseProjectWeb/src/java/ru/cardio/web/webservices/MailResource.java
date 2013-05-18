package ru.cardio.web.webservices;

import com.google.gson.Gson;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import ru.cardio.core.managers.MailManagerLocal;
import ru.cardio.core.managers.TokenManagerLocal;
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
@Path("mail")
@Stateless
public class MailResource {

    @Context
    private UriInfo context;
    @EJB
    MailManagerLocal mailMan;
    @EJB
    TokenManagerLocal tokenMan;

    /**
     * Creates a new instance of MailResource
     */
    public MailResource() {
    }

    /**
     * Retrieves representation of an instance of
     * ru.cardio.web.webservices.MailResource
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
     * PUT method for updating or creating an instance of MailResource
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
    @Path("alert")
    private String alertMessage(@Context HttpServletRequest req) {
        try {
            String json = null;
//            TokenUtils.checkToken(tokenMan, token);
            System.out.println("alertMessage: json = " + json);
            SimpleMail sm = (new Gson()).fromJson(json, SimpleMail.class);
            mailMan.sendMail(sm.getRecipient(), sm.getTheme(), sm.getTimestamp(), sm.getText());
           
            JsonResponse<String> jr = new JsonResponse<String>(ResponseConstants.OK, null, ResponseConstants.YES);
            return SecureResponseWrapper.getJsonResponse(jr);
        } catch (CardioException e) {
            return CardioExceptionWrapper.wrapException(e);
        }
    }
}
