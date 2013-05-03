package ru.cardio.web.beans;

import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import ru.cardio.core.managers.TimetableManagerLocal;
import ru.cardio.json.entity.UsersTimetablePoint;
import web.utils.SessionUtils;

/**
 *
 * @author Shaykhlislamov Sabir (email: sha-sabir@yandex.ru)
 */
@ManagedBean
@ViewScoped
public class TimetableBean {

    @EJB
    TimetableManagerLocal tMan;
    
    List<UsersTimetablePoint> timetable;

    Long userId;
    
    @PostConstruct
    private void init() {
        timetable = tMan.getFullTimetable();
//        System.out.println("timetable");
        this.userId = SessionUtils.getUserId();
    }

    public void addPoints(String jsonPointsArray) {
        tMan.addTimetablePoints(Long.MIN_VALUE, null);
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public List<UsersTimetablePoint> getTimetable() {
        return timetable;
    }
    
    
    
}
