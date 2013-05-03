

package ru.cardio.web.json.utils;

import com.google.gson.Gson;
import ru.cardio.exceptions.CardioException;
import ru.cardio.json.additionals.JsonResponse;
import ru.cardio.json.additionals.ResponseConstants;
import ru.cardio.json.entity.JsonError;

/**
 *
 * @author Shaykhlislamov Sabir (email: sha-sabir@yandex.ru)
 */
public class SecureCardioExceptionWrapper {
    
    public static String wrapException(CardioException exc){
        return (new Gson()).toJson( new JsonResponse(ResponseConstants.ERROR, new JsonError(exc.getMessage(), exc.getErrorCode()), null));
    }

}
