package ru.cardio.json.entity;

import java.util.List;

/**
 *
 * @author Shaykhlislamov Sabir (email: sha-sabir@yandex.ru)
 */
public class UsersTimetablePoint extends SimpleTimetablePoint {

    public UsersTimetablePoint(List<Long> usersId, Integer day, Integer lesson) {
        super(day, lesson);
        this.usersId = usersId;
    }
    protected List<Long> usersId;

    public List<Long> getUsersId() {
        return usersId;
    }

    public void setUsersId(List<Long> usersId) {
        this.usersId = usersId;
    }
}
