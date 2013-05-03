package ru.cardio.web.beans;

import java.io.Serializable;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import ru.cardio.core.managers.TimetableManagerLocal;
import ru.cardio.exceptions.CardioException;
import ru.cardio.json.entity.SimpleUser;

/**
 *
 * @author Shaykhlislamov Sabir (email: sha-sabir@yandex.ru)
 */
@ManagedBean
@ViewScoped
public class TimetableUsersQrBean implements Serializable{

    @EJB
    TimetableManagerLocal tMan;
    private List<SimpleUser> sList;

    @PostConstruct
    private void init() throws CardioException {
        this.sList = tMan.getSimpleUsersInTimetable();
    }

    public List<SimpleUser> getsList() {
        return sList;
    }
}
