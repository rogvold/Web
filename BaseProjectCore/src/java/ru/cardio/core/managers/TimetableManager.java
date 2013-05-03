package ru.cardio.core.managers;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import ru.cardio.core.jpa.entity.UserCard;
import ru.cardio.core.utils.TimetableUtils;
import ru.cardio.exceptions.CardioException;
import ru.cardio.json.entity.SimpleTimetablePoint;
import ru.cardio.json.entity.SimpleUser;
import ru.cardio.json.entity.SimpleUserTimetablePoint;
import ru.cardio.json.entity.UsersTimetablePoint;

/**
 *
 * @author rogvold
 */
@Stateless
public class TimetableManager implements TimetableManagerLocal {

    public static final Integer MAX_LESSONS_AMOUNT = 7;
    @PersistenceContext(unitName = "BaseProjectCorePU")
    EntityManager em;
    @EJB
    UserManagerLocal userMan;
    @EJB
    UserCardManagerLocal cardMan;

    private List<UsersTimetablePoint> getVoidUsersTimetablePoints() {
        List<UsersTimetablePoint> list = new ArrayList();
        for (int j = 1; j <= MAX_LESSONS_AMOUNT; j++) {
            for (int i = 1; i <= 6; i++) {
                list.add(new UsersTimetablePoint(new ArrayList<Long>(), i, j));
            }
        }
        return list;
    }

    private List<UsersTimetablePoint> addUserToTimetable(List<UsersTimetablePoint> pool, Long uId, SimpleTimetablePoint stp) {
        for (UsersTimetablePoint point : pool) {
            if (point.equals(stp)) {
                List<Long> sil = point.getUsersId();
                sil.add(uId);
                point.setUsersId(sil);
                return pool;
            }
        }
        return pool;
    }

    @Override
    public List<UsersTimetablePoint> getFullTimetable() {

        Query q = em.createQuery("select uc from UserCard uc where uc.jsonTimetable is NOT NULL");
        List<UserCard> users = q.getResultList();

        List<UsersTimetablePoint> pool = getVoidUsersTimetablePoints();

        for (UserCard uc : users) {
            List<SimpleTimetablePoint> simplePoints = TimetableUtils.decodeTimetablePoints(uc.getUserId(), cardMan);
            for (SimpleTimetablePoint sp : simplePoints) {
                pool = addUserToTimetable(pool, uc.getUserId(), sp);
            }
        }

        return pool;
    }

    @Override
    public UsersTimetablePoint getUsersTimetablePoint(SimpleTimetablePoint stp) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void addTimetablePoints(Long userId, List<SimpleTimetablePoint> stpList) {
        UserCard uc = cardMan.getCardByUserId(userId);
        if (uc == null) {
            return;
        }
        List<SimpleTimetablePoint> sList = TimetableUtils.decodeTimetablePoints(userId, cardMan);
        if (sList == null) {
            sList = new ArrayList();
        }
        sList.addAll(stpList);
        uc.setJsonTimetable(TimetableUtils.encodeTimetablePoints(sList));
    }

    @Override
    public void addTimetablePoint(Long userId, SimpleTimetablePoint stp) {
        UserCard uc = cardMan.getCardByUserId(userId);
        if (uc == null) {
            return;
        }
        List<SimpleTimetablePoint> stpList = TimetableUtils.decodeTimetablePoints(userId, cardMan);
        stpList.add(stp);
        uc.setJsonTimetable(TimetableUtils.encodeTimetablePoints(stpList));
        em.merge(uc);
    }

    @Override
    public void updateTimetablePoints(Long userId, List<SimpleTimetablePoint> stpList) {
        UserCard uc = cardMan.getCardByUserId(userId);
        if (uc == null) {
            return;
        }
        uc.setJsonTimetable(TimetableUtils.encodeTimetablePoints(stpList));
    }

    @Override
    public List<SimpleTimetablePoint> getUserTimetable(Long userId) {
        return TimetableUtils.decodeTimetablePoints(userId, cardMan);
    }

    @Override
    public List<SimpleUserTimetablePoint> getFullInformativeTimetable() throws CardioException {
        List<UsersTimetablePoint> sPool = getFullTimetable();
        List<SimpleUserTimetablePoint> list = new ArrayList();
        for (UsersTimetablePoint p : sPool) {
            SimpleUserTimetablePoint sutp = new SimpleUserTimetablePoint(null, p.getDay(), p.getLesson());
            List<SimpleUser> sList = new ArrayList();
            for (Long uId : p.getUsersId()) {
                SimpleUser su = userMan.getSimpleInfo(uId);
                su.setPassword(null);
                sList.add(su);
            }
            sutp.setUsers(sList);
            list.add(sutp);
        }
        return list;
    }

    @Override
    public List<SimpleUser> getSimpleUsersInTimetable() throws CardioException {

        Query q = em.createQuery("select uc.userId from UserCard uc where uc.jsonTimetable is NOT NULL");
        List<Long> usersId = q.getResultList();
        List<SimpleUser> sList = new ArrayList();
        for (Long id : usersId) {
            SimpleUser su = userMan.getSimpleInfo(id);
            su.setPassword(null);
            sList.add(su);
        }
        return sList;
    }
}
