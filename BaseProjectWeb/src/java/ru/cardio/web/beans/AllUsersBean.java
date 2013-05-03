package ru.cardio.web.beans;

import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import ru.cardio.core.jpa.entity.User;
import ru.cardio.core.managers.UserManagerLocal;

/**
 *
 * @author Shaykhlislamov Sabir (email: sha-sabir@yandex.ru)
 */
@ManagedBean
@ViewScoped
public class AllUsersBean {
    @EJB
    UserManagerLocal userMan;
    
    List<User> users;
    
    @PostConstruct
    private void init(){
        this.users = userMan.getAllUsersByRole(User.USER);
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }
    
    
    
}
