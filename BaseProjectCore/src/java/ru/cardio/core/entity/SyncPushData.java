package ru.cardio.core.entity;

import java.util.List;

/**
 *
 * @author Shaykhlislamov Sabir (email: sha-sabir@yandex.ru)
 */
public class SyncPushData {

    private List<SyncSession> newSessions;
    private List<Long> deletedSessions;
    private SyncSession updatedSession;

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

    public SyncSession getUpdatedSession() {
        return updatedSession;
    }

    public void setUpdatedSession(SyncSession updatedSession) {
        this.updatedSession = updatedSession;
    }
}
