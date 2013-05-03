package ru.cardio.json.entity;

import java.util.List;

/**
 *
 * @author Shaykhlislamov Sabir (email: sha-sabir@yandex.ru)
 */
public class SimpleUserTimetablePoint extends SimpleTimetablePoint {

    private List<SimpleUser> users;

    public SimpleUserTimetablePoint(List<SimpleUser> users, Integer day, Integer lesson) {
        super(day, lesson);
        this.users = users;
    }

    public List<SimpleUser> getUsers() {
        return users;
    }

    public void setUsers(List<SimpleUser> users) {
        this.users = users;
    }
}
