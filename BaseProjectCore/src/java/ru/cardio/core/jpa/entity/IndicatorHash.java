package ru.cardio.core.jpa.entity;

import java.io.Serializable;
import javax.persistence.*;

/**
 *
 * @author Shaykhlislamov Sabir (email: sha-sabir@yandex.ru)
 */
@Entity
public class IndicatorHash implements Serializable {
    private static final long serialVersionUID = 1L;
    
    public static final Integer STATUS_CALCULATED = 1;
    public static final Integer STATUS_NOT_CALUCLATED = 0;
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column
    @Lob
    private String indicatorsData;
    
    @Column
    private Long cardioSessionId;
    
    @Column
    private Integer status;
    
    @Column
    private Long step;
    
    @Column
    private Long indicatorWindow;
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCardioSessionId() {
        return cardioSessionId;
    }

    public void setCardioSessionId(Long cardioSessionId) {
        this.cardioSessionId = cardioSessionId;
    }

    public String getIndicatorsData() {
        return indicatorsData;
    }

    public void setIndicatorsData(String indicatorsData) {
        this.indicatorsData = indicatorsData;
    }

    public Long getIndicatorWindow() {
        return indicatorWindow;
    }

    public void setIndicatorWindow(Long indicatorWindow) {
        this.indicatorWindow = indicatorWindow;
    }

    

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Long getStep() {
        return step;
    }

    public void setStep(Long step) {
        this.step = step;
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
        if (!(object instanceof IndicatorHash)) {
            return false;
        }
        IndicatorHash other = (IndicatorHash) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ru.cardio.core.jpa.entity.IndicatorHash[ id=" + id + " ]";
    }

}
