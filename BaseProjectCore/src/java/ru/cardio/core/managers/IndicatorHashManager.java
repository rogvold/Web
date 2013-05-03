package ru.cardio.core.managers;

import com.google.gson.Gson;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import ru.cardio.core.jpa.entity.Constant;
import ru.cardio.core.jpa.entity.IndicatorHash;
import ru.cardio.exceptions.CardioException;
import ru.cardio.graphics.MyPlot;
import ru.cardio.graphics.MyPoint;
import ru.cardio.indicators.AbstractIndicatorsService;

/**
 *
 * @author rogvold
 */
@Stateless
public class IndicatorHashManager implements IndicatorHashManagerLocal {

    @PersistenceContext(unitName = "BaseProjectCorePU")
    EntityManager em;
    @EJB
    ConstantManagerLocal cMan;
    @EJB
    IndicatorsManagerLocal indMan;

    //! if shouldRecalculate than it's important to recalculate!
    private boolean shouldRecalculate(Long sessionId) {

        IndicatorHash ih = getIndicatorHashBySessionId(sessionId);
        if (ih == null) {
            return true;
        }
        if (IndicatorHash.STATUS_NOT_CALUCLATED.equals(ih.getStatus())) {
            return true;
        }
        Long cWindow = Long.parseLong(cMan.getConstantValueByName(Constant.WINDOW_DURATION_NAME));
        Long cStep = Long.parseLong(cMan.getConstantValueByName(Constant.STEP_DURATION_NAME));

        if ((!cStep.equals(ih.getStep())) || (!cWindow.equals(ih.getIndicatorWindow()))) {
            return true;
        }
        return false;
    }

    private double[][] get2dArray(MyPlot plot) {
        double[][] arr = new double[plot.getPoints().size()][2];
        int i = 0;
        for (MyPoint point : plot.getPoints()) {
            arr[i][0] = point.getX();
            arr[i][1] = point.getY();
            i++;
        }
        return arr;
    }

    private void recalculateParametersJsonList(Long sessionId, AbstractIndicatorsService a, String indicatorName) throws Exception {
        MyPlot plot = indMan.getPlotOfParameters(sessionId, a, indicatorName, Long.parseLong(cMan.getConstantValueByName(Constant.STEP_DURATION_NAME)));
        IndicatorHash ih = getIndicatorHashBySessionId(sessionId);
        Gson gson = new Gson();
        System.out.println("recalculateParametersJsonList : ih = " + ih);

        if (ih == null) {
            ih = new IndicatorHash();
        }
        System.out.println("new ih = " + ih);
        System.out.println("plot = " + plot);

        ih.setStatus(IndicatorHash.STATUS_CALCULATED);
        ih.setStep(Long.parseLong(cMan.getConstantValueByName(Constant.STEP_DURATION_NAME)));
        ih.setIndicatorWindow(Long.parseLong(cMan.getConstantValueByName(Constant.WINDOW_DURATION_NAME)));
        ih.setIndicatorsData(gson.toJson(get2dArray(plot)));
        ih.setCardioSessionId(sessionId);

        em.merge(ih);
    }

    @Override
    public String getCalculatedParameterJsonPlot(Long sessionId, AbstractIndicatorsService a, String indicatorName, boolean recalculate) throws CardioException {
        if (recalculate || shouldRecalculate(sessionId)) {
            try {
                recalculateParametersJsonList(sessionId, a, indicatorName);
            } catch (Exception ex) {
                throw new CardioException("Cannot calculate tension index for this session.");
            }
        }
        IndicatorHash ih = getIndicatorHashBySessionId(sessionId);
        return ih.getIndicatorsData();
    }

    @Override
    public IndicatorHash getIndicatorHashBySessionId(Long sessionId) {
        Query q = em.createQuery("select h from IndicatorHash h where h.cardioSessionId = :sId").setParameter("sId", sessionId);
        List<IndicatorHash> list = q.getResultList();
        return (list == null || list.isEmpty()) ? null : list.get(0);
    }
}
