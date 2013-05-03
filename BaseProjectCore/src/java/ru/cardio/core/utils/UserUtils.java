package ru.cardio.core.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import ru.cardio.exceptions.CardioException;

/**
 *
 * @author rogvold
 */
public class UserUtils {

    public static boolean isValidEmail(String email) throws CardioException {
        try {
            Pattern p = Pattern.compile("[\\w\\.-]*[a-zA-Z0-9_]@[\\w\\.-]*[a-zA-Z0-9]\\.[a-zA-Z][a-zA-Z\\.]*[a-zA-Z]");
            System.out.println("isValidEmail: email = " + email);
            Matcher m = p.matcher(email);
            return m.matches();
        } catch (Exception e) {
            throw new CardioException("cannot parse email '" + email + "'");
        }
    }

    public static void checkPassword(String password) throws CardioException {
        if (password == null) {
            throw new CardioException("invalid password = '" + password + "'");
        }
    }
}
