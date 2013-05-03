

package ru.cardio.json.entity;

/**
 *
 * @author Shaykhlislamov Sabir (email: sha-sabir@yandex.ru)
 */
public class JsonError {

    public static final Integer INVALID_TOKEN_CODE = 2;
    public static final Integer ANY_ERROR_CODE = 3;
    
    private String message;
    private Integer code;

    public JsonError(String message, Integer code) {
        this.message = message;
        this.code = code;
    }
    
    
    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
    
    
}
