package ru.cardio.entity;

import java.util.List;

/**
 *
 * @author Shaykhlislamov Sabir (email: sha-sabir@yandex.ru)
 */
public class RatesWithInfo {

    private List<Integer> rates;
    private Long userId;
    private Long sessionId;

    public RatesWithInfo(List<Integer> rates, Long userId, Long sessionId) {
        this.rates = rates;
        this.userId = userId;
        this.sessionId = sessionId;
    }

    public RatesWithInfo() {
    }

    public List<Integer> getRates() {
        return rates;
    }

    public void setRates(List<Integer> rates) {
        this.rates = rates;
    }

    public Long getSessionId() {
        return sessionId;
    }

    public void setSessionId(Long sessionId) {
        this.sessionId = sessionId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
