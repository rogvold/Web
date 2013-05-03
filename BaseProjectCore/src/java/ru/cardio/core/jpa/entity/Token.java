

package ru.cardio.core.jpa.entity;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;

/**
 *
 * @author Shaykhlislamov Sabir (email: sha-sabir@yandex.ru)
 */
@Entity
public class Token implements Serializable {
    private static final long serialVersionUID = 1L;
    public static final long EXPIRES_IN = 12*60*60*1000;
    public static final Integer STATUS_ACTIVE = 1;
    public static final Integer STATUS_DELETED = 0;
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String token;
    private String deviceId;
    private Long userId;
    private Integer status;
    
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date creationDate;
    
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date expiredDate;

    public Token() {
    }

    public Token(String token, String deviceId, Long userId, Date creationDate, Date expiredDate) {
        this.token = token;
        this.deviceId = deviceId;
        this.userId = userId;
        this.creationDate = creationDate;
        this.expiredDate = expiredDate;
        this.status = STATUS_ACTIVE;
    }

    
    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public Date getExpiredDate() {
        return expiredDate;
    }

    public void setExpiredDate(Date expiredDate) {
        this.expiredDate = expiredDate;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
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
        if (!(object instanceof Token)) {
            return false;
        }
        Token other = (Token) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ru.cardio.core.jpa.entity.Token[ id=" + id + " ]";
    }

}
