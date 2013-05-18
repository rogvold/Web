package ru.cardio.core.managers;

import javax.ejb.Local;
import ru.cardio.exceptions.CardioException;

/**
 *
 * @author rogvold
 */
@Local
public interface MailManagerLocal {
    
    public void sendMail(String to, String theme, Long timestamp, String text) throws CardioException;
    
}
