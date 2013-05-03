package ru.cardio.core.managers;

import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import ru.cardio.core.jpa.entity.Token;
import ru.cardio.core.jpa.entity.User;
import ru.cardio.exceptions.CardioException;
import ru.cardio.json.additionals.ResponseConstants;
import ru.cardio.json.entity.SimpleToken;
import ru.cardio.utils.StringUtils;

/**
 *
 * @author rogvold
 */
@Stateless
public class TokenManager implements TokenManagerLocal {

    private static final Integer TOKEN_LENGTH = 16;
    
    @PersistenceContext(unitName = "BaseProjectCorePU")
    EntityManager em;
    @EJB
    UserManagerLocal userMan;

    private List<Token> getAllUserTokens(Long userId) {
        Query q = em.createQuery("select t from Token t where t.userId = :userId and t.status = :status order by t.creationDate desc").setParameter("userId", userId).setParameter("status", Token.STATUS_ACTIVE);
        List<Token> list = q.getResultList();
        if ((list != null) && (list.isEmpty())) {
            return null;
        }
        return list;
    }

    private void deleteToken(Token token) {
        token.setStatus(Token.STATUS_DELETED);
        em.merge(token);
    }

    private void deleteAllUserTokens(Long userId) {
        List<Token> list = getAllUserTokens(userId);
        if (list == null) return;
        for (Token t : list) {
            deleteToken(t);
        }
    }

    private void checkTokenExpiration(Token t) throws CardioException {
        if (t.getExpiredDate().getTime() < (new Date()).getTime()) {
            throw new CardioException("token is expired", ResponseConstants.INVALID_TOKEN_CODE);
        }
    }

    @Override
    public Long getUserIdByToken(String tokenString) throws CardioException {
        Query q = em.createQuery("select t from User u, Token t where u.id = t.userId and t.token = :token").setParameter("token", tokenString);
        List<Token> list = q.getResultList();
        if ((list == null) || (list.isEmpty())) {
            throw new CardioException("you should get a new token", ResponseConstants.INVALID_TOKEN_CODE);
        }
        checkTokenExpiration(list.get(0));
        return list.get(0).getUserId();
    }

    @Override
    public void createToken(String email, String password, String deviceId) throws CardioException {
        userMan.checkAuthInfo(email, password);
        System.out.println("createToken: auth info checked");
        
        User u = userMan.getUserByEmail(email);
        
        System.out.println("createToken: user = " + u);
        
        deleteAllUserTokens(u.getId());
        System.out.println("all user tokens deleted");
        Date now = new Date();
        Token t = new Token(StringUtils.generateRandomString(TOKEN_LENGTH), deviceId, u.getId(), now, new Date(now.getTime() + Token.EXPIRES_IN));
        t = em.merge(t);
    }

    @Override
    public void resetToken(Long userId) throws CardioException {
        deleteAllUserTokens(userId);
    }

    @Override
    public SimpleToken getUserToken(Long userId) throws CardioException {
        try {
            List<Token> list = getAllUserTokens(userId);
            SimpleToken sToken = new SimpleToken(list.get(0).getToken(), list.get(0).getExpiredDate().getTime());
            return sToken;
        } catch (Exception e) {
            throw new CardioException("you have no tokens...", ResponseConstants.INVALID_TOKEN_CODE);
        }
    }
}
