
package ru.cardio.core.managers;

import javax.ejb.Local;
import ru.cardio.core.jpa.entity.Token;
import ru.cardio.core.jpa.entity.User;
import ru.cardio.exceptions.CardioException;
import ru.cardio.json.entity.SimpleToken;

/**
 *
 * @author rogvold
 */
@Local
public interface TokenManagerLocal {

    
//    public Token getTokenByTokenString(String tokenString) throws CardioException;
    
    public Long getUserIdByToken(String tokenString) throws CardioException;
    
    public void createToken(String email, String password, String deviceId) throws CardioException;
    
    public void resetToken(Long userId) throws CardioException;
    
    public SimpleToken getUserToken(Long userId) throws CardioException;
    
}
