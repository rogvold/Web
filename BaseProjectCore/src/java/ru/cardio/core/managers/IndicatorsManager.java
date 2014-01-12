package ru.cardio.core.managers;

import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import ru.cardio.core.entity.Rate;
import ru.cardio.core.entity.SimplePoint;
import ru.cardio.core.jpa.entity.*;
import ru.cardio.exceptions.CardioException;
import ru.cardio.graphics.MyPlot;
import ru.cardio.graphics.MyPoint;
import ru.cardio.indicators.*;

/**
 *
 * @author rogvold
 */
@Stateless
public class IndicatorsManager implements IndicatorsManagerLocal {

    @PersistenceContext(unitName = "BaseProjectCorePU")
    EntityManager em;
    @EJB
    CardioSessionManagerLocal cardMan;
    @EJB
    ConstantManagerLocal cMan;
    @EJB
    CardioSessionEvaluationManagerLocal cardevMan;
    @EJB
    SessionsHistoryManagerLocal histMan;

    @Override
    public MyPlot getPlotOfParameters(Long sessionId, AbstractIndicatorsService a, String indicatorName, long msStep) throws Exception {
        if (sessionId == null) {
            throw new CardioException("getPlotOfParameters: sessionId is null");
        }
        List<Rate> rates = cardMan.getRatesInCardioSession(sessionId, -1);
        List<MyPoint> points = new ArrayList();
        String dur = cMan.getConstantValueByName(Constant.WINDOW_DURATION_NAME);
        if (dur != null) {
            a.setDuration(Integer.parseInt(dur) * 1000);
        }


//        System.out.println("getPlotOfParameters:  rates = " + rates);
        System.out.println("getPlotOfParameters: step = " + msStep + " duration = " + a.getDuration());


        int beginIndex = 0;
        int sum = 0;
        for (int i = 0; i < rates.size(); i++) {
            beginIndex = i;
            if (sum >= a.getDuration()) {
                break;
            }
            sum += rates.get(i).getDuration();
        }

        System.out.println("beginIndex = " + beginIndex);
        if (beginIndex == rates.size() - 1) {
            System.out.println("not enough data...");
            return null;
        }


        int curr = beginIndex;
        int prev = 0;//not right
        List<Integer> list = new ArrayList();

        while (curr < rates.size()) {
            list.clear();
            for (int i = prev; i < curr; i++) {
                list.add(rates.get(i).getDuration());
            }
            a.setIntervals(list);
            System.out.println("calculating indicator " + indicatorName);

            points.add(new MyPoint(rates.get(curr).getStartDate().getTime(), a.parameter(indicatorName)));

            int t = 0;
            int curr2 = curr, prev2 = prev;
            for (int u = curr; u < rates.size(); u++) {
                if (t >= msStep) {
                    break;
                }
                t += rates.get(u).getDuration();
                curr2++;
                prev2++;
            }
            prev = prev2;
            curr = curr2;
        }

        MyPlot myPlot = new MyPlot(points, indicatorName);
        return myPlot;
    }

    private CardioSessionEvaluation getEvaluationFromMyPlot(MyPlot plot, Long sessionId, String name) throws CardioException {
        if (plot == null || plot.getPoints() == null) {
            return null;
        }
        CardioSession cs = cardMan.getCardioSessionById(sessionId);
        if (cs == null) {
            return null;
        }

        CardioSessionEvaluation cse = cardevMan.getCardioSessionEvaluation(sessionId, name);
        if (cse == null) {
            cse = new CardioSessionEvaluation();
        }
        List<SimplePoint> sList = new ArrayList();
        for (MyPoint p : plot.getPoints()) {
            SimplePoint sp = new SimplePoint(p.getX(), p.getY());
            sList.add(sp);
        }
        cse.setData((new Gson()).toJson(sList));
        cse.setSessionId(sessionId);
        cse.setName(name);

        return em.merge(cse);
    }

    private List<Integer> getLastRatesInSession(Long sessionId, Integer dur) throws CardioException {
        List<Rate> rates = cardMan.getRatesInCardioSession(sessionId, -1);
        int beginIndex = 0;
        int sum = 0;
        for (int i = rates.size() - 1; i >= 0; i--) {
            beginIndex = i;
            sum += rates.get(i).getDuration();
            if (sum >= dur) {
                break;
            }
        }
        if ((beginIndex == 0) && (sum < dur)) {
            throw new CardioException("not enough data for tension index");
        }
        List<Integer> list = new ArrayList();

        for (int i = beginIndex; i < rates.size(); i++) {
            list.add(rates.get(i).getDuration());
        }

        return list;
    }

    private List<Integer> getLastRatesInSession(Long sessionId) throws CardioException {
        Integer dur = Integer.parseInt(cMan.getConstantValueByName(Constant.WINDOW_DURATION_NAME));
        return getLastRatesInSession(sessionId, dur);
    }

    @Override
    public Double getLastTension(Long sessionId) throws CardioException {
        HRVIndicatorsService a = new HRVIndicatorsService(getLastRatesInSession(sessionId));
        a.setDuration(Integer.parseInt(cMan.getConstantValueByName(Constant.WINDOW_DURATION_NAME)));
        return a.getIN();
    }

    private List<AbstractIndicatorsService> getAllAvailableIndicatorServices() {
        List<AbstractIndicatorsService> list = new ArrayList();

        list.add(new TimeIndicatorsService());
        list.add(new StatisticsIndicatorsService());
        list.add(new SpectrumIndicatorsService());
        list.add(new HRVIndicatorsService());

        return list;
    }

    @Override
    public List<MyPlot> getAllImplementedParametersPlots(Long sessionId, long msStep) throws Exception {
        List<AbstractIndicatorsService> servicesInstances = getAllAvailableIndicatorServices();
        List<MyPlot> list = new ArrayList();
        for (AbstractIndicatorsService ais : servicesInstances) {
            List<String> names = ais.allParametersNames();
            for (String s : names) {
                list.add(getPlotOfParameters(sessionId, ais, s, msStep));
            }
        }
        return list;
    }

    @Override
    public List<CardioSessionEvaluation> recalculateAllEvaluations(Long sessionId, long msStep) throws CardioException {
        List<CardioSessionEvaluation> l = new ArrayList();
        AbstractIndicatorsService forTension = new HRVIndicatorsService();
        AbstractIndicatorsService forSpectrum = new SpectrumIndicatorsService();
        try {
            CardioSessionEvaluation plotCse = getEvaluationFromMyPlot(getPlotOfParameters(sessionId, forTension, "IN", msStep), sessionId, "tension");
            l.add(plotCse);
            l.add(getEvaluationFromMyPlot(getPlotOfParameters(sessionId, forSpectrum, "HFPercents", msStep), sessionId, "hf"));
            l.add(getEvaluationFromMyPlot(getPlotOfParameters(sessionId, forSpectrum, "LFPercents", msStep), sessionId, "lf"));
            l.add(getEvaluationFromMyPlot(getPlotOfParameters(sessionId, forSpectrum, "VLFPercents", msStep), sessionId, "vlf"));
            l.add(getEvaluationFromMyPlot(getPlotOfParameters(sessionId, forSpectrum, "ULFPercents", msStep), sessionId, "ulf"));

            if (plotCse == null) {
                return null;
            }

        } catch (Exception ex) {
            Logger.getLogger(IndicatorsManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return l;
    }

    private SessionsHistory createSessionsHistory(Long sessionsUpdateCount, Long userId, String newSessions, String deletedSessions, Long updatedSession) {
        SessionsHistory sh = new SessionsHistory(userId, newSessions, deletedSessions, sessionsUpdateCount, updatedSession);
        return em.merge(sh);
    }

    private void initUserSessionsHistory(Long userId) throws CardioException {
        List<Long> newList = new ArrayList();
        //fuck!!!!
        List<CardioSession> sessions = cardMan.getUserCardioSessions(userId);
        for (CardioSession cs : sessions) {
            newList.add(cs.getStartDate().getTime());
        }

        Gson gson = new Gson();
        String newListString = gson.toJson(newList);
        SessionsHistory createSessionsHistory = createSessionsHistory(1L, userId, newListString, null, null);
    }

    @Override
    public String getJsonOfAllImplementedParametersPlots(Long sessionId, long msStep) throws Exception {
        List<MyPlot> allPlots = getAllImplementedParametersPlots(sessionId, msStep);
        // TODO: implement

        return null;
    }

    private void deleteUserSessionsHistory(Long userId) {
        Query q = em.createQuery("select h from SessionsHistory h where h.userId = :id").setParameter("id", userId);
        List<SessionsHistory> list = q.getResultList();
        if (list == null || list.isEmpty()) {
            return;
        }
        for (SessionsHistory h : list) {
            em.remove(h);
        }
    }

    @Override
    public void recalculateAllEvaluations(Long userId) throws CardioException {
        System.out.println("trying to recalculate for userId = " + userId);
        deleteUserSessionsHistory(userId);
        List<CardioSession> csList = cardMan.getUserCardioSessions(userId);
        List<Long> newList = new ArrayList();
        for (CardioSession cs : csList) {
            System.out.println("recalculating for sessionId = " + cs.getId());
            List<CardioSessionEvaluation> li = recalculateAllEvaluations(cs.getId(), 5000);
            if (li != null) {
                newList.add(cs.getStartDate().getTime());
            }
        }

        Gson gson = new Gson();
        String newListString = gson.toJson(newList);
        createSessionsHistory(1L, userId, newListString, null, null);
    }
}
