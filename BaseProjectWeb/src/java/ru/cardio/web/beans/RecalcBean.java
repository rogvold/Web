package ru.cardio.web.beans;

import java.io.Serializable;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import ru.cardio.core.managers.IndicatorsManagerLocal;
import ru.cardio.exceptions.CardioException;

/**
 *
 * @author Shaykhlislamov Sabir (email: sha-sabir@yandex.ru)
 */
@ManagedBean
@ViewScoped
public class RecalcBean implements Serializable {

    @EJB
    IndicatorsManagerLocal indMan;

    public void recalculateEvaluations(Long userId) throws CardioException {
        System.out.println("recalculateEvaluations bean / userId = " + userId);
        indMan.recalculateAllEvaluations(userId);
    }
}
