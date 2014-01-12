package ru.cardio.core.entity;

import java.util.List;

/**
 *
 * @author Shaykhlislamov Sabir (email: sha-sabir@yandex.ru)
 */
public class SyncSession {

    private Long timestamp;
    private List<EvaluationPoint> evaluation;
    private List<SimplePoint> rates;
    private String description;

    public SyncSession(Long timestamp, List<EvaluationPoint> evaluation, List<SimplePoint> rates, String description) {
        this.timestamp = timestamp;
        this.evaluation = evaluation;
        this.rates = rates;
        this.description = description;
    }

    public List<EvaluationPoint> getEvaluation() {
        return evaluation;
    }

    public void setEvaluation(List<EvaluationPoint> evaluation) {
        this.evaluation = evaluation;
    }

    public List<SimplePoint> getRates() {
        return rates;
    }

    public void setRates(List<SimplePoint> rates) {
        this.rates = rates;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "SyncSession{" + "timestamp=" + timestamp + ", description=" + description + "}";
    }
}
