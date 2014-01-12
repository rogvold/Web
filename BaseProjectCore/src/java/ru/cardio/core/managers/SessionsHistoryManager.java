package ru.cardio.core.managers;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import ru.cardio.core.entity.MergedSessionsHistory;
import ru.cardio.core.entity.SimplePoint;
import ru.cardio.core.jpa.entity.CardioSession;
import ru.cardio.core.jpa.entity.SessionsHistory;
import ru.cardio.core.jpa.entity.User;
import ru.cardio.exceptions.CardioException;

/**
 *
 * @author rogvold
 */
@Stateless
public class SessionsHistoryManager implements SessionsHistoryManagerLocal {

    @PersistenceContext(unitName = "BaseProjectCorePU")
    EntityManager em;
    @EJB
    CardioSessionManagerLocal cardMan;
    @EJB
    UserManagerLocal userMan;

    @Override
    public void initAllSessionsHistory() throws CardioException {
        List<User> allUsers = userMan.getAllUsersByRole(User.USER);
        for (User u : allUsers) {
            initUserSessionsHistory(u.getId());
        }
    }

    @Override
    public List<SessionsHistory> getUserSessionsDictionary(Long userId) throws CardioException {
        Query q = em.createQuery("select s from SessionsHistory s where s.userId = :uId order by s.sessionsUpdateCount desc").setParameter("uId", userId);
        List<SessionsHistory> sList = q.getResultList();
        return sList;
    }

    private List<SessionsHistory> getSessionsHistoryAfterCounter(Long userId, Long counter) {
        Query q = em.createQuery("select s from SessionsHistory s where ( (s.userId = :uId) and (s.sessionsUpdateCount > :cntr) ) order by s.sessionsUpdateCount desc").setParameter("uId", userId).setParameter("cntr", counter);
        List<SessionsHistory> list = q.getResultList();
        if (list == null || list.isEmpty()) {
            return null;
        }
        return list;
    }

    private List<Long> deserializeSessionsHistory(String data) {
        Gson gson = new Gson();
        List<Long> list = gson.fromJson(data, new TypeToken<List<Long>>() {
        }.getType());
        return list;
    }

    private String getJsonTimestampsList(List<Long> timestamps) {
        Gson gson = new Gson();
        return gson.toJson(timestamps);
    }

    @Override
    public MergedSessionsHistory getMergedSessionHistory(Long userId, Long counter) throws CardioException {
        List<SessionsHistory> iList = getSessionsHistoryAfterCounter(userId, counter);
        System.out.println("getMergedSessionHistory: userId = " + userId + "; counter = " + counter + "; sessionsAfterCounter = " + iList);
        Set<Long> deletedSet = new HashSet();
        Set<Long> newSet = new HashSet();
        Set<Long> updatedSet = new HashSet();
        if (iList == null) {
            return null;
        }
        for (SessionsHistory ss : iList) {
            List<Long> deletedSessions = deserializeSessionsHistory(ss.getDeletedSessions());
            List<Long> newSessions = deserializeSessionsHistory(ss.getNewSessions());
            if (ss.getUpdatedSession() != null) {
                updatedSet.add(ss.getUpdatedSession());
            }
            if (deletedSessions != null) {
                deletedSet.addAll(deletedSessions);
            }
            if (newSessions != null) {
                newSet.addAll(newSessions);
            }
//            if (newSessions != null) {
//                for (Long newSession : newSessions) {
//                    deletedSet.remove(newSession);
//                    updatedSet.remove(newSession);
//                }
//            }
//            if (deletedSessions != null) {
//                for (Long deletedSession : deletedSessions) {
//                    newSet.remove(deletedSession);
//                }
//            }
        }
        List<Long> mergedNewSessions = new ArrayList();
        List<Long> mergedDeletedSessions = new ArrayList();
        List<Long> mergedUpdatedSessions = new ArrayList();


        System.out.println("sets before intersection: ");
        System.out.println("newSet:" + newSet);
        System.out.println("deletedSet:" + deletedSet);
        System.out.println("updatedSet:" + updatedSet);

        for (Long s : newSet) {
            if (!deletedSet.contains(s)) {
                mergedNewSessions.add(s);
            }
        }

        for (Long s : deletedSet) {
            if (!newSet.contains(s)) {
                mergedDeletedSessions.add(s);
            }
        }

        for (Long s : updatedSet) {
            if (!newSet.contains(s)) {
                mergedUpdatedSessions.add(s);
            }
        }

//        mergedUpdatedSessions.addAll(updatedSet);


        return new MergedSessionsHistory(mergedNewSessions, mergedDeletedSessions, mergedUpdatedSessions);
    }

    @Override
    public SessionsHistory getLastUserSessionHistory(Long userId) throws CardioException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    private String prepareListJson(List<Long> list) {
        if (list == null) {
            return null;
        }
        Gson gson = new Gson();
        return gson.toJson(list);
    }

    @Override
    public Long updateSessionsHistory(Long userId, List<Long> newSessions, List<Long> deletedSessions, Long updatedSession) throws CardioException {
        Long lastCount = getLastSessionUpdateCount(userId);
        if (newSessions == null && deletedSessions == null) {
            return lastCount;
        }
        SessionsHistory ss = new SessionsHistory();
        ss.setDeletedSessions(prepareListJson(deletedSessions));
        ss.setNewSessions(prepareListJson(newSessions));
        ss.setUpdatedSession(updatedSession);
        ss.setSessionsUpdateCount(lastCount + 1);
        ss.setUserId(userId);
        ss = em.merge(ss);
        return lastCount + 1;
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
        createSessionsHistory(1L, userId, newListString, null, null);
    }

    private Long getLastSessionUpdateCount(Long userId) throws CardioException {
        Query q = em.createQuery("select s from SessionsHistory s where s.userId = :uId order by s.sessionsUpdateCount desc").setParameter("uId", userId);
        List<SessionsHistory> sList = q.getResultList();
        System.out.println("getLastSessionUpdateCount: sList = " + sList);
        if (sList == null || sList.isEmpty()) {
            initUserSessionsHistory(userId);
            return getLastSessionUpdateCount(userId);
        } else {
            return sList.get(0).getSessionsUpdateCount();
        }
    }
}
