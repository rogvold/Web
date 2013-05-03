package ru.cardio.web.webservices.secure;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import ru.cardio.core.managers.CardioSessionManagerLocal;
import ru.cardio.core.managers.IndicatorHashManagerLocal;
import ru.cardio.core.managers.TokenManagerLocal;
import ru.cardio.core.utils.TokenUtils;
import ru.cardio.exceptions.CardioException;
import ru.cardio.indicators.HRVIndicatorsService;
import ru.cardio.json.additionals.JsonResponse;
import ru.cardio.json.additionals.ResponseConstants;
import ru.cardio.web.json.utils.SecureCardioExceptionWrapper;
import ru.cardio.web.json.utils.SecureResponseWrapper;
import com.google.gson.Gson;
/**
 * REST Web Service
 *
 * @author rogvold
 */
@Path("SecureIndicators")
@Stateless
public class SecureIndicatorsResource {

    @Context
    private UriInfo context;
    @EJB
    TokenManagerLocal tokenMan;
    @EJB
    IndicatorHashManagerLocal hashMan;
    @EJB
    CardioSessionManagerLocal csMan;

    /**
     * Creates a new instance of SecureIndicatorsResource
     */
    public SecureIndicatorsResource() {
    }

    @POST
    @Produces("application/json")
    @Path("{sessionId}/tension")
    public String getTensionIndex(@FormParam("token") String token, @PathParam("sessionId") Long sessionId) {
        try {
            TokenUtils.checkToken(tokenMan, token);
            csMan.checkRights(tokenMan.getUserIdByToken(token), sessionId);
            String jsonArray = hashMan.getCalculatedParameterJsonPlot(sessionId, new HRVIndicatorsService(), "IN", false);
            float[][] dummy = new float[0][0];  // The same type as your "newMap"
            float[][] newArray;
            Gson gson = new Gson();
            newArray = gson.fromJson(jsonArray, dummy.getClass());

            JsonResponse<float[][]> jr = new JsonResponse<float[][]>(ResponseConstants.OK, null, newArray);
            return SecureResponseWrapper.getJsonResponse(jr);
        } catch (CardioException e) {
            return SecureCardioExceptionWrapper.wrapException(e);
        }
    }
}
