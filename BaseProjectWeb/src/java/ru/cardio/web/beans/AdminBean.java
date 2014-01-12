package ru.cardio.web.beans;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import ru.cardio.core.managers.CardioSessionManagerLocal;
import ru.cardio.core.managers.IndicatorsManagerLocal;
import ru.cardio.core.managers.SessionsHistoryManagerLocal;
import ru.cardio.exceptions.CardioException;

/**
 *
 * @author Shaykhlislamov Sabir (email: sha-sabir@yandex.ru)
 */
@ManagedBean
@ViewScoped
public class AdminBean {

    @EJB
    CardioSessionManagerLocal cardMan;
    @EJB
    SessionsHistoryManagerLocal sMan;
    @EJB
    IndicatorsManagerLocal indMan;

    public void recalculateBeats() throws CardioException {
        cardMan.recalculateAllBeats();
    }

    public void initSessions() throws CardioException {
        sMan.initAllSessionsHistory();
    }

    public void recalculateEvaluations(Long userId) throws CardioException {
        System.out.println("recalculateEvaluations bean/ userId = " + userId);
        indMan.recalculateAllEvaluations(userId);
    }
}
