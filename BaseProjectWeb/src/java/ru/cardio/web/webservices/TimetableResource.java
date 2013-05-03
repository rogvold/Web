package ru.cardio.web.webservices;

import com.google.gson.Gson;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import ru.cardio.core.managers.TimetableManagerLocal;
import ru.cardio.core.utils.TimetableUtils;
import ru.cardio.exceptions.CardioException;
import ru.cardio.json.additionals.JsonResponse;
import ru.cardio.json.additionals.ResponseConstants;
import ru.cardio.json.entity.SimpleTimetablePoint;
import ru.cardio.json.entity.SimpleUserTimetablePoint;
import ru.cardio.json.entity.UsersTimetablePoint;
import ru.cardio.web.json.utils.CardioExceptionWrapper;
import ru.cardio.web.json.utils.SecureResponseWrapper;
import web.utils.SessionUtils;

/**
 * REST Web Service
 *
 * @author rogvold
 */
@Path("timetable")
@Stateless
public class TimetableResource {

    @Context
    private UriInfo context;
    @EJB
    TimetableManagerLocal tMan;

    /**
     * Creates a new instance of TimetableResource
     */
    public TimetableResource() {
    }

    @POST
    @Produces("application/json")
    @Path("update")
    public String update(@Context HttpServletRequest req, String data) {
        try {
            Long userId = SessionUtils.getCurrentUserIdThrowingException(req);
            List<SimpleTimetablePoint> points = TimetableUtils.deserializeGsonTimetablePointsList(data);
            tMan.updateTimetablePoints(userId, points);
            JsonResponse<String> jr = new JsonResponse<String>(ResponseConstants.OK, null, ResponseConstants.YES);
            return SecureResponseWrapper.getJsonResponse(jr);
        } catch (CardioException e) {
            return CardioExceptionWrapper.wrapException(e);
        }
    }

    @GET
    @Produces("application/json")
    @Path("my")
    public String getMyTimetable(@Context HttpServletRequest req) {
        try {
            Long userId = SessionUtils.getCurrentUserIdThrowingException(req);
            JsonResponse<List<SimpleTimetablePoint>> jr = new JsonResponse<List<SimpleTimetablePoint>>(ResponseConstants.OK, null, tMan.getUserTimetable(userId));
            return SecureResponseWrapper.getJsonResponse(jr);
        } catch (CardioException e) {
            return CardioExceptionWrapper.wrapException(e);
        }
    }

    @GET
    @Produces("application/json")
    @Path("full")
    public String getFullTimetable() {
        try {
            List<SimpleUserTimetablePoint> pool = tMan.getFullInformativeTimetable();
            if (pool == null) {
                throw new CardioException("Timetable is not available");
            }
            JsonResponse<List<SimpleUserTimetablePoint>> jr = new JsonResponse<List<SimpleUserTimetablePoint>>(ResponseConstants.OK, null, pool);
            return SecureResponseWrapper.getJsonResponse(jr);
        } catch (CardioException e) {
            return CardioExceptionWrapper.wrapException(e);
        }
    }
}
