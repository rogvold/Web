package ru.cardio.web.beans;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import ru.cardio.core.jpa.entity.User;
import ru.cardio.core.managers.UserManagerLocal;
import web.utils.SessionUtils;

/**
 *
 * @author Shaykhlislamov Sabir (email: sha-sabir@yandex.ru)
 */
@ManagedBean
@ViewScoped
public class HomeBean {

    @EJB
    UserManagerLocal userMan;
    
    private Long userId;
    private User user;
    
    @PostConstruct
    private void init() {
        this.userId = SessionUtils.getUserId();
        this.user = userMan.getUserById(userId);
    }

    public User getUser() {
        return user;
    }

    public Long getUserId() {
        return userId;
    }
    
}
