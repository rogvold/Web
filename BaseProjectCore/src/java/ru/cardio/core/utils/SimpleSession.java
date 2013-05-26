package ru.cardio.core.utils;

import java.util.Date;

/**
 *
 * @author rogvold
 */
public class SimpleSession {

    private Long start;
    private Long end;
    private String content;
    private Long id;
    private double maxTension;
    private double minTension;
    private double avrTension;
    private double stressTimePercents;

    public SimpleSession(Long id, Long start, Long end, double maxTension, double minTension, double avrTension) {
        this.start = start;
        this.end = end;
        this.id = id;
        this.maxTension = maxTension;
        this.minTension = minTension;
        this.avrTension = avrTension;
    }

    public SimpleSession(Long id, Long start, Long end, double maxTension, double minTension, double avrTension, double stressTimePercents) {
        this.start = start;
        this.end = end;
        this.id = id;
        this.maxTension = maxTension;
        this.minTension = minTension;
        this.avrTension = avrTension;
        this.stressTimePercents = stressTimePercents;
    }

    public SimpleSession(Long start, Long end, String content, Long id) {
        this.start = start;
        this.end = end;
        this.content = content;
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Long getEnd() {
        return end;
    }

    public void setEnd(Long end) {
        this.end = end;
    }

    public Long getStart() {
        return start;
    }

    public void setStart(Long start) {
        this.start = start;
    }

    public double getAvrTension() {
        return avrTension;
    }

    public void setAvrTension(double avrTension) {
        this.avrTension = avrTension;
    }

    public double getMaxTension() {
        return maxTension;
    }

    public void setMaxTension(double maxTension) {
        this.maxTension = maxTension;
    }

    public double getMinTension() {
        return minTension;
    }

    public void setMinTension(double minTension) {
        this.minTension = minTension;
    }

    public double getStressTimePercents() {
        return stressTimePercents;
    }

    public void setStressTimePercents(double stressTimePercents) {
        this.stressTimePercents = stressTimePercents;
    }
}
