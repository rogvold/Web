package ru.cardio.web.webservices.secure;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import ru.cardio.core.jpa.entity.User;
import ru.cardio.core.managers.CardioSessionManagerLocal;
import ru.cardio.core.managers.TokenManagerLocal;
import ru.cardio.core.managers.UserManagerLocal;
import ru.cardio.core.utils.TokenUtils;
import ru.cardio.exceptions.CardioException;
import ru.cardio.json.additionals.JsonResponse;
import ru.cardio.json.additionals.ResponseConstants;
import ru.cardio.json.entity.SimpleRatesData;
import ru.cardio.web.json.utils.SecureCardioExceptionWrapper;
import ru.cardio.web.json.utils.SecureResponseWrapper;

/**
 * REST Web Service
 *
 * @author rogvold
 */
@Path("SecureRatesUploading")
@Stateless
public class SecureRatesUploadingResource {

    @Context
    private UriInfo context;
    @EJB
    CardioSessionManagerLocal cardMan;
    @EJB
    TokenManagerLocal tokenMan;
    @EJB
    UserManagerLocal userMan;

    /**
     * Creates a new instance of SecureRatesUploadingResource
     */
    public SecureRatesUploadingResource() {
    }

    @POST
    @Path("upload")
    @Produces("application/json")
    public String uploadRates(@FormParam("token") String token, @FormParam("json") String json) {
        try {
            TokenUtils.checkToken(tokenMan, token);
            SimpleRatesData sdr = SimpleRatesData.fromJson(json);
            cardMan.addRates(tokenMan.getUserIdByToken(token), sdr.getRates(), sdr.getStart(), (sdr.getCreate() == 1) ? true : false);
            JsonResponse<String> jr = new JsonResponse<String>(ResponseConstants.OK, null, ResponseConstants.YES);
            return SecureResponseWrapper.getJsonResponse(jr);
        } catch (CardioException e) {
            return SecureCardioExceptionWrapper.wrapException(e);
        }
    }

    @POST
    @Path("smartupload")
    @Produces("application/json")
    public String smartUploadRates(@FormParam("token") String token, @FormParam("json") String json) {
        try {
            TokenUtils.checkToken(tokenMan, token);
            SimpleRatesData sdr = SimpleRatesData.fromJson(json);
            cardMan.addRates(tokenMan.getUserIdByToken(token), sdr.getRates(), sdr.getStart(), (sdr.getId() == null) ? true : false);

            JsonResponse<String> jr = new JsonResponse<String>(ResponseConstants.OK, null, ResponseConstants.YES);
            return SecureResponseWrapper.getJsonResponse(jr);
        } catch (CardioException e) {
            return SecureCardioExceptionWrapper.wrapException(e);
        }
    }

    @POST
    @Path("sync")
    @Produces("application/json")
    public String syncRates(@FormParam("token") String token, @FormParam("json") String json) {
        try {
            TokenUtils.checkToken(tokenMan, token);
//            Long userId = TokenUtils.
            Long userId = tokenMan.getUserIdByToken(token);
            User u = userMan.getUserById(userId);

            SimpleRatesData sdr = SimpleRatesData.fromJson(json);
            sdr.setEmail(u.getEmail());
            sdr.setPassword(u.getPassword());

            sdr.setCreate(0);
            cardMan.syncRates(sdr);
            JsonResponse<String> jr = new JsonResponse<String>(ResponseConstants.OK, null, ResponseConstants.YES);
            return SecureResponseWrapper.getJsonResponse(jr);
        } catch (CardioException e) {
            return SecureCardioExceptionWrapper.wrapException(e);
        }
    }

    @POST
    @Path("upload_rates")
    @Produces("application/json")
    public String uploadRatesReturningId(@FormParam("token") String token, @FormParam("json") String json) {
        try {
            TokenUtils.checkToken(tokenMan, token);
            SimpleRatesData sdr = SimpleRatesData.fromJson(json);
            Long id = cardMan.addRatesReturningSessionId(tokenMan.getUserIdByToken(token), sdr.getRates(), sdr.getStart(), (sdr.getCreate() == 1) ? true : false);
            JsonResponse<Long> jr = new JsonResponse<Long>(ResponseConstants.OK, null, id);
            return SecureResponseWrapper.getJsonResponse(jr);
        } catch (CardioException e) {
            return SecureCardioExceptionWrapper.wrapException(e);
        }
    }
}
