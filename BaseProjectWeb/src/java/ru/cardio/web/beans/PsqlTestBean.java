package ru.cardio.web.beans;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import ru.cardio.core.managers.CardioSessionEvaluationManagerLocal;
import ru.cardio.core.managers.SessionsHistoryManager;
import ru.cardio.core.managers.SessionsHistoryManagerLocal;
import ru.cardio.exceptions.CardioException;

/**
 *
 * @author Shaykhlislamov Sabir (email: sha-sabir@yandex.ru)
 */
@ManagedBean
@ViewScoped
public class PsqlTestBean {

    @EJB
    SessionsHistoryManagerLocal shm;
    @EJB
    CardioSessionEvaluationManagerLocal csem;

    public void pull(Long userId) throws CardioException {
        csem.pullSessions(userId, 0L);
    }
}
