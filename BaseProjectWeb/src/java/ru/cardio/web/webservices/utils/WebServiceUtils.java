package ru.cardio.web.webservices.utils;

import ru.cardio.core.services.ConfigService;
import ru.cardio.exceptions.CardioException;

/**
 *
 * @author Shaykhlislamov Sabir (email: sha-sabir@yandex.ru)
 */
public class WebServiceUtils {

    public static void checkSecret(String secret) throws CardioException {
        String trueSecret = (String) ConfigService.getParameters().get("secret");
        if (trueSecret == null) {
            return;
        }
        if (!trueSecret.equals(secret)) {
            throw new CardioException("invalid secret");
        }
    }
}
