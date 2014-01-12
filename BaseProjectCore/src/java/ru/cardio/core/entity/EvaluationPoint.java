package ru.cardio.core.entity;

/**
 *
 * @author Shaykhlislamov Sabir (email: sha-sabir@yandex.ru)
 */
public class EvaluationPoint {

    private Long timestamp;
    private Double tension;
    private Double ulf;
    private Double vlf;
    private Double lf;
    private Double hf;

    public Double getHf() {
        return hf;
    }

    public void setHf(Double hf) {
        this.hf = hf;
    }

    public Double getLf() {
        return lf;
    }

    public void setLf(Double lf) {
        this.lf = lf;
    }

    public Double getTension() {
        return tension;
    }

    public void setTension(Double tension) {
        this.tension = tension;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public Double getUlf() {
        return ulf;
    }

    public void setUlf(Double ulf) {
        this.ulf = ulf;
    }

    public Double getVlf() {
        return vlf;
    }

    public void setVlf(Double vlf) {
        this.vlf = vlf;
    }
}
