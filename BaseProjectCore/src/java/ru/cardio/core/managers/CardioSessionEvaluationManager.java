package ru.cardio.core.managers;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import ru.cardio.core.entity.*;
import ru.cardio.core.jpa.entity.CardioSession;
import ru.cardio.core.jpa.entity.CardioSessionEvaluation;
import ru.cardio.core.utils.CardioUtils;
import ru.cardio.exceptions.CardioException;

/**
 *
 * @author rogvold
 */
@Stateless
public class CardioSessionEvaluationManager implements CardioSessionEvaluationManagerLocal {

    @PersistenceContext(unitName = "BaseProjectCorePU")
    EntityManager em;
    @EJB
    CardioSessionManagerLocal cardMan;
    @EJB
    SessionsHistoryManagerLocal chMan;

    @Override
    public CardioSessionEvaluation getCardioSessionEvaluation(Long sessionId, String name) throws CardioException {
        if (isExistsCardioSessionEvaluation(sessionId, name)) {
            return (CardioSessionEvaluation) em.createQuery("select c from CardioSessionEvaluation c where c.name = :name and c.sessionId = :sId").setParameter("name", name).setParameter("sId", sessionId).getResultList().get(0);
        }
        return null;
    }

    @Override
    public boolean isExistsCardioSessionEvaluation(Long sessionId, String name) throws CardioException {
        Query q = em.createQuery("select c from CardioSessionEvaluation c where c.name = :name and c.sessionId = :sId").setParameter("name", name).setParameter("sId", sessionId);
        List<CardioSessionEvaluation> list = q.getResultList();
        if (list == null || list.isEmpty()) {
            return false;
        }
        return true;
    }

    @Override
    public CardioSessionEvaluation createCardioSessionEvaluation(Long sessionId, String name, String data, boolean rewriteIfExists) throws CardioException {
        CardioSessionEvaluation cse = getCardioSessionEvaluation(sessionId, name);
        if (cse == null) {
            cse = new CardioSessionEvaluation(data, name, sessionId);
        } else {
            cse.setData(data);
        }
        return em.merge(cse);
    }

    @Override
    public CardioSessionEvaluation createCardioSessionEvaluation(Long sessionId, SimpleEvaluation se) throws CardioException {
        Gson gson = new Gson();
        return createCardioSessionEvaluation(sessionId, se.getName(), gson.toJson(se.getPoints()), true);
    }

    @Override
    public void processNewSyncCardioSession(Long userId, SyncSession ss) throws CardioException {
        if (userId == null) {
            throw new CardioException("processNewSyncCardioSession: userId is not defined");
        }
        if (ss == null) {
            throw new CardioException();
        }
        //adding new rates
        List<Integer> rates = new ArrayList();
        for (SimplePoint sp : ss.getRates()) {
            rates.add((int) Math.floor(sp.getValue()));
        }
        CardioSession cs = cardMan.createCardioSession(userId, ss.getTimestamp(), ss.getDescription(), rates);
        List<SimpleEvaluation> seList = getSimpleEvaluationFromEvaluationPointsList(ss.getEvaluation());
        for (SimpleEvaluation se : seList) {
            createCardioSessionEvaluation(cs.getId(), se);
        }
    }

    private void processNewSyncCardioSessions(Long userId, List<SyncSession> syncSessions) throws CardioException {
        if (syncSessions == null) {
            return;
        }
        for (SyncSession ss : syncSessions) {
            processNewSyncCardioSession(userId, ss);
        }
    }

    @Override
    public void processDeletedSessions(List<Long> timestamps, Long userId) throws CardioException {
        if (timestamps == null) {
            return;
        }
        for (Long t : timestamps) {
            cardMan.deleteCardioSession(userId, t);
        }
    }

    private List<Long> getTimestampsFromSyncSessionList(List<SyncSession> list) {
        if (list == null) {
            return null;
        }
        List<Long> l = new ArrayList();
        for (SyncSession ss : list) {
            l.add(ss.getTimestamp());
        }
        return l;
    }

    private void updateSimpleEvaluation(Long userId, Long timestamp, String description, SimpleEvaluation se) throws CardioException {
        if (se == null) {
            return;
        }
        CardioSession cs = cardMan.getCardioSessionByStart(userId, new Date(timestamp));
        if (cs == null) {
            throw new CardioException("update: can not find session with timestamp = " + timestamp);
        }

        System.out.println("trying to getOldCse: cs = " + cs + "; csId = " + cs.getId() + "; se.getName() = " + se.getName());
        CardioSessionEvaluation oldCse = getCardioSessionEvaluation(cs.getId(), se.getName());
        Gson gson = new Gson();
        System.out.println("oldCse = " + oldCse);
        if (oldCse == null) {
            System.out.println("oldCse: id = " + oldCse.getId());
            oldCse = new CardioSessionEvaluation();
            oldCse.setName(se.getName());
            oldCse.setSessionId(cs.getId());
        }
//        cs.setDescription(description);
//        em.merge(cs);

        List<SimplePoint> oldPoints = gson.fromJson(oldCse.getData(), new TypeToken<List<SimplePoint>>() {
        }.getType());
        if (oldPoints == null) {
            oldPoints = new ArrayList();
        }

        if (se.getPoints() == null) {
            return;
        }

        System.out.println("points to add: " + se.getPoints());

        System.out.println("");
        for (SimplePoint sp : se.getPoints()) {
            oldPoints.add(sp);
        }
        oldCse.setData(gson.toJson(oldPoints));
        em.merge(oldCse);
    }

    private void updateEvaluation(Long userId, Long timestamp, String description, List<SimpleEvaluation> ev) throws CardioException {
        if (ev == null) {
            return;
        }
        for (SimpleEvaluation se : ev) {
            updateSimpleEvaluation(userId, timestamp, description, se);
        }
    }

    @Override
    public void processUpdatedSession(SyncSession ss, Long userId) throws CardioException {
        if (ss == null) {
            return;
        }
        System.out.println("processUpdatedSession occured");
        System.out.println("SyncSession = ");
        System.out.println(ss);
        //ok
//        cardMan.updateCardioSession(userId, ss.getTimestamp(), ss.getDescription(), null)

//        CardioSession cs = cardMan.getCardioSessionByStart(userId, new Date(ss.getTimestamp()));
//        cs.setDescription(ss.getDescription());
//        em.merge(cs);
        
        updateEvaluation(userId, ss.getTimestamp(), ss.getDescription(), getSimpleEvaluationFromEvaluationPointsList(ss.getEvaluation()));
        cardMan.updateCardioSession(userId, ss.getTimestamp(), ss.getDescription(), CardioUtils.getRatesFromSimplePointsArray(ss.getRates()));
    }

    private SimpleEvaluation getSimpleEvaluationFromCardioSessionEvaluation(CardioSessionEvaluation cse) {
        SimpleEvaluation se = new SimpleEvaluation();
        se.setName(cse.getName());
        Gson gson = new Gson();
        List<SimplePoint> points = gson.fromJson(cse.getData(), new TypeToken<List<SimplePoint>>() {
        }.getType());
        se.setPoints(points);
        return se;
    }

    @Override
    public List<SimpleEvaluation> getSessionEvaluations(Long userId, Long timestamp) throws CardioException {
        CardioSession cs = cardMan.getCardioSessionByStart(userId, new Date(timestamp));
        if (cs == null) {
            return null;
//            throw new CardioException("getSessionEvaluations: cardio sessoin with timestamp = " + timestamp + " does not exist");
        }
        Query q = em.createQuery("select c from CardioSessionEvaluation c where c.sessionId = :sId").setParameter("sId", cs.getId());
        List<CardioSessionEvaluation> list = q.getResultList();
        if (list == null || list.isEmpty()) {
            return null;
        }
        List<SimpleEvaluation> evaluations = new ArrayList();
        for (CardioSessionEvaluation cse : list) {
            evaluations.add(getSimpleEvaluationFromCardioSessionEvaluation(cse));
        }
        return evaluations;
    }

    private List<SimpleEvaluation> getSimpleEvaluationFromEvaluationPointsList(List<EvaluationPoint> list) {
        //TODO: use reflection

        if (list == null) {
            return null;
        }

        SimpleEvaluation tension = new SimpleEvaluation();
        tension.setName("tension");

        SimpleEvaluation ulf = new SimpleEvaluation();
        ulf.setName("ulf");
        SimpleEvaluation vlf = new SimpleEvaluation();
        vlf.setName("vlf");
        SimpleEvaluation lf = new SimpleEvaluation();
        lf.setName("lf");
        SimpleEvaluation hf = new SimpleEvaluation();
        hf.setName("hf");

        List<SimplePoint> tensionPoints = new ArrayList();
        List<SimplePoint> ulfPoints = new ArrayList();
        List<SimplePoint> vlfPoints = new ArrayList();
        List<SimplePoint> lfPoints = new ArrayList();
        List<SimplePoint> hfPoints = new ArrayList();

        for (EvaluationPoint se : list) {
            Long x = se.getTimestamp();
            tensionPoints.add(new SimplePoint(x, se.getTension()));
            ulfPoints.add(new SimplePoint(x, se.getUlf()));
            vlfPoints.add(new SimplePoint(x, se.getVlf()));
            lfPoints.add(new SimplePoint(x, se.getLf()));
            hfPoints.add(new SimplePoint(x, se.getHf()));
        }

        tension.setPoints(tensionPoints);
        ulf.setPoints(ulfPoints);
        vlf.setPoints(vlfPoints);
        lf.setPoints(lfPoints);
        hf.setPoints(hfPoints);

        List<SimpleEvaluation> seList = new ArrayList();
        seList.add(tension);
        seList.add(ulf);
        seList.add(vlf);
        seList.add(lf);
        seList.add(hf);

        return seList;
    }

    private List<EvaluationPoint> getNewEvaluation(List<SimpleEvaluation> oldList) {
        List<EvaluationPoint> newList = new ArrayList();
        if (oldList == null) {
            return null;
        }
        try {
            List<SimplePoint> xList = oldList.get(0).getPoints();
            for (int i = 0; i < xList.size(); i++) {
                EvaluationPoint ep = new EvaluationPoint();
                ep.setTimestamp(xList.get(i).getTimestamp());
                for (SimpleEvaluation se : oldList) {
                    try {// very bad..
                        if (se.getName().equals("tension")) {
                            if (se.getPoints().size() > i) {
                                ep.setTension(se.getPoints().get(i).getValue());
                            }
                        }
                        if (se.getName().equals("ulf")) {
                            if (se.getPoints().size() > i) {
                                ep.setUlf(se.getPoints().get(i).getValue());
                            }
                        }
                        if (se.getName().equals("vlf")) {
                            if (se.getPoints().size() > i) {
                                ep.setVlf(se.getPoints().get(i).getValue());
                            }
                        }
                        if (se.getName().equals("lf")) {
                            if (se.getPoints().size() > i) {
                                ep.setLf(se.getPoints().get(i).getValue());
                            }
                        }
                        if (se.getName().equals("hf")) {
                            if (se.getPoints().size() > i) {
                                ep.setHf(se.getPoints().get(i).getValue());
                            }
                        }
                    } catch (Exception e) {
                        System.out.println("getNewEvaluation: something bad occured: " + e.getMessage());
                    }

                }
                newList.add(ep);
            }


        } catch (Exception e) {
            System.out.println("getNewEvaluation: something bad occured: " + e.getMessage());
        }




        return newList;
    }

    private SyncSession getSession(Long userId, Long timestamp) throws CardioException {
        List<SimplePoint> rates = cardMan.getSimplePointsRates(timestamp, userId);
        List<SimpleEvaluation> evaluation = getSessionEvaluations(userId, timestamp);
        String description = null;
        CardioSession cs = cardMan.getCardioSessionByStart(userId, new Date(timestamp));
        return new SyncSession(timestamp, getNewEvaluation(evaluation), rates, cs.getDescription());
    }

    @Override
    public SyncSession getSessionWithEvaluation(Long sessionId) throws CardioException {
        CardioSession cs = cardMan.getCardioSessionById(sessionId);
        return getSession(cs.getUserId(), cs.getStartDate().getTime());
    }

    @Override
    public Long pushSessions(Long userId, SyncPushData pushData) throws CardioException {
//        if (pushData)
        System.out.println("pushDate");
        System.out.println(pushData);
        processNewSyncCardioSessions(userId, pushData.getNewSessions());
        processDeletedSessions(pushData.getDeletedSessions(), userId);
        processUpdatedSession(pushData.getUpdatedSession(), userId);
        Long updatedLong = null;
        if (pushData.getUpdatedSession() != null) {
            updatedLong = pushData.getUpdatedSession().getTimestamp();
        }
        return chMan.updateSessionsHistory(userId, getTimestampsFromSyncSessionList(pushData.getNewSessions()), pushData.getDeletedSessions(), updatedLong);
    }

    @Override
    public SyncPullData pullSessions(Long userId, Long localCounter) throws CardioException {
        System.out.println("pullSessions occured: userId = " + userId + "; localCounter = " + localCounter);
        MergedSessionsHistory merged = chMan.getMergedSessionHistory(userId, localCounter);

        if (merged == null) {
            System.out.println("merged is null");
        } else {
            System.out.println("merged = " + merged.toString());
        }

        List<SyncSession> newSessions = new ArrayList();
        List<Long> deletedSessions = new ArrayList();
        List<SyncSession> updatedSessions = new ArrayList();
        if (merged != null) {
            for (Long newSessionT : merged.getNewSessions()) {
                newSessions.add(getSession(userId, newSessionT));
            }
//            for (Long updatedSessionT : merged.getNewSessions()) {
//                updatedSessions.add(getSession(userId, updatedSessionT));
//            }
            deletedSessions = merged.getDeletedSessions();
        }
        SyncPullData spd = new SyncPullData(newSessions, null, deletedSessions);
        System.out.println("returning spd = " + spd);
        return spd;
    }
}
