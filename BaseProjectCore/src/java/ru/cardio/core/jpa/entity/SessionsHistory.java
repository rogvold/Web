package ru.cardio.core.jpa.entity;

import java.io.Serializable;
import javax.persistence.*;

/**
 *
 * @author Shaykhlislamov Sabir (email: sha-sabir@yandex.ru)
 */
@Entity
public class SessionsHistory implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private Long userId;
    @Column(length = 50000)
    private String newSessions;
    @Column(length = 50000)
    private String deletedSessions;
    private Long sessionsUpdateCount;
    @Column
    private Long updatedSession;

    public Long getUpdatedSession() {
        return updatedSession;
    }

    public void setUpdatedSession(Long updatedSession) {
        this.updatedSession = updatedSession;
    }

    public SessionsHistory(Long userId, String newSessions, String deletedSessions, Long sessionsUpdateCount, Long updatedSession) {
        this.userId = userId;
        this.newSessions = newSessions;
        this.deletedSessions = deletedSessions;
        this.sessionsUpdateCount = sessionsUpdateCount;
        this.updatedSession = updatedSession;
    }

    public SessionsHistory() {
    }

    public String getDeletedSessions() {
        return deletedSessions;
    }

    public void setDeletedSessions(String deletedSessions) {
        this.deletedSessions = deletedSessions;
    }

    public String getNewSessions() {
        return newSessions;
    }

    public void setNewSessions(String newSessions) {
        this.newSessions = newSessions;
    }

    public Long getSessionsUpdateCount() {
        return sessionsUpdateCount;
    }

    public void setSessionsUpdateCount(Long sessionsUpdateCount) {
        this.sessionsUpdateCount = sessionsUpdateCount;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof SessionsHistory)) {
            return false;
        }
        SessionsHistory other = (SessionsHistory) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return " [ id=" + id + " , sessionsUpdateCount = " + sessionsUpdateCount +"]";
    }
}
