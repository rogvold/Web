package ru.cardio.exceptions;

import ru.cardio.json.additionals.ResponseConstants;

/**
 *
 * @author rogvold
 */
public class CardioException extends Exception {

    
    private Integer errorCode;

    /**
     * Creates a new instance of
     * <code>CardioException</code> without detail message.
     */
    public CardioException() {
    }

    /**
     * Constructs an instance of
     * <code>CardioException</code> with the specified detail message.
     *
     * @param msg the detail message.
     */
    public CardioException(String msg) {
        super(msg);
        this.errorCode = ResponseConstants.NORMAL_ERROR_CODE;
    }

    public CardioException(String message, Integer errorCode){
        super(message);
        this.errorCode = errorCode;
    }

    public Integer getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(Integer errorCode) {
        this.errorCode = errorCode;
    }
    
    
}
