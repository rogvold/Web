package ru.cardio.core.entity;

import java.util.List;

/**
 *
 * @author Shaykhlislamov Sabir (email: sha-sabir@yandex.ru)
 */
public class SimpleEvaluation {

    private String name;
    private List<SimplePoint> points;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<SimplePoint> getPoints() {
        return points;
    }

    public void setPoints(List<SimplePoint> points) {
        this.points = points;
    }
}
