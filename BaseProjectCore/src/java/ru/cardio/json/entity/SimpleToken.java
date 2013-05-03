

package ru.cardio.json.entity;

/**
 *
 * @author Shaykhlislamov Sabir (email: sha-sabir@yandex.ru)
 */
public class SimpleToken {

    private String token;
    private long expiredDate;

    public SimpleToken(String token, long expiredDate) {
        this.token = token;
        this.expiredDate = expiredDate;
    }
    
    public long getExpiredDate() {
        return expiredDate;
    }

    public void setExpiredDate(long expiredDate) {
        this.expiredDate = expiredDate;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
    
    
}
