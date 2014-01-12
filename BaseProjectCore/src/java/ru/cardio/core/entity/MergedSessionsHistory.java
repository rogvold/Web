package ru.cardio.core.entity;

import java.util.List;

/**
 *
 * @author Shaykhlislamov Sabir (email: sha-sabir@yandex.ru)
 */
public class MergedSessionsHistory {

    List<Long> newSessions;
    List<Long> deletedSessions;
    List<Long> updatedSessions;

    public MergedSessionsHistory(List<Long> newSessions, List<Long> deletedSessions, List<Long> updatedSessions) {
        this.newSessions = newSessions;
        this.deletedSessions = deletedSessions;
        this.updatedSessions = updatedSessions;
    }

    public List<Long> getUpdatedSessions() {
        return updatedSessions;
    }

    public void setUpdatedSessions(List<Long> updatedSessions) {
        this.updatedSessions = updatedSessions;
    }

    public List<Long> getDeletedSessions() {
        return deletedSessions;
    }

    public void setDeletedSessions(List<Long> deletedSessions) {
        this.deletedSessions = deletedSessions;
    }

    public List<Long> getNewSessions() {
        return newSessions;
    }

    public void setNewSessions(List<Long> newSessions) {
        this.newSessions = newSessions;
    }

    @Override
    public String toString() {
        return "MergedSessionsHistory{" + "newSessions=" + newSessions + ", deletedSessions=" + deletedSessions + ", updatedSessions=" + updatedSessions + '}';
    }
}
