package ru.cardio.web.beans;

import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import ru.cardio.core.jpa.entity.CardioSession;
import ru.cardio.core.jpa.entity.User;
import ru.cardio.core.managers.CardioSessionManagerLocal;
import ru.cardio.core.managers.UserManagerLocal;
import ru.cardio.exceptions.CardioException;

/**
 *
 * @author Shaykhlislamov Sabir (email: sha-sabir@yandex.ru)
 */
@ManagedBean
@ViewScoped
public class ExcelBean {

    @EJB
    CardioSessionManagerLocal cardMan;
    @EJB
    UserManagerLocal userMan;
    private User user;
    List<CardioSession> sessions;

    @PostConstruct
    private void init() throws CardioException {
        Long userId = Long.parseLong(FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("userId"));
        this.user = userMan.getUserById(userId);
        this.sessions = cardMan.getUserCardioSessions(userId);
    }

    public Long timestampFromDate(Date date){
        if (date == null){
            return null;
        }
        return date.getTime();
    }
    
    public List<CardioSession> getSessions() {
        return sessions;
    }

    
    public User getUser() {
        return user;
    }
}
