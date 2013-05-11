package web.utils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import ru.cardio.exceptions.CardioException;
import ru.cardio.json.additionals.ResponseConstants;

/**
 *
 * @author danon
 */
public class SessionUtils {

    public static boolean isSignedIn() {
        if (SessionListener.getSessionAttribute("userId", false) != null) {
            return true;
        }
        return false;
    }

    public static Long getUserId() {
        Long uId = ((Long) SessionListener.getSessionAttribute("userId", true));
        return uId;
    }

    public static Long getCurrentUserId(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        Long userId = (Long) session.getAttribute("userId");
        return userId;
    }

    public static Long getCurrentUserIdThrowingException(HttpServletRequest req) throws CardioException {
        HttpSession session = req.getSession(true);
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            throw new CardioException("You are not logged in", ResponseConstants.USER_IS_NOT_LOGGED_IN);
        }
        return userId;
    }
}
