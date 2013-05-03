

package ru.cardio.core.utils;

import ru.cardio.core.managers.TokenManagerLocal;
import ru.cardio.exceptions.CardioException;

/**
 *
 * @author Shaykhlislamov Sabir (email: sha-sabir@yandex.ru)
 */
public class TokenUtils {

    public static void checkToken(TokenManagerLocal tokenMan, String tokenString) throws CardioException{
        tokenMan.getUserIdByToken(tokenString);
    }
}
