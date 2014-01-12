package ru.cardio.core.entity;

import java.util.List;

/**
 *
 * @author Shaykhlislamov Sabir (email: sha-sabir@yandex.ru)
 */
public class SyncPullData {

    private List<SyncSession> newSessions;
    private List<SyncSession> updatedSessions;
    private List<Long> deletedSessions;

    public SyncPullData(List<SyncSession> newSessions, List<SyncSession> updatedSessions, List<Long> deletedSessions) {
        this.newSessions = newSessions;
        this.updatedSessions = updatedSessions;
        this.deletedSessions = deletedSessions;
    }

    public List<SyncSession> getUpdatedSessions() {
        return updatedSessions;
    }

    public void setUpdatedSessions(List<SyncSession> updatedSessions) {
        this.updatedSessions = updatedSessions;
    }

    public List<Long> getDeletedSessions() {
        return deletedSessions;
    }

    public void setDeletedSessions(List<Long> deletedSessions) {
        this.deletedSessions = deletedSessions;
    }

    public List<SyncSession> getNewSessions() {
        return newSessions;
    }

    public void setNewSessions(List<SyncSession> newSessions) {
        this.newSessions = newSessions;
    }

    @Override
    public String toString() {
        return "deletedSessions = " + deletedSessions + "; newSessions = " + newSessions + "; updatedSessions = " + updatedSessions;
    }
}
