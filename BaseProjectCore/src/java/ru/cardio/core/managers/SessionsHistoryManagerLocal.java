package ru.cardio.core.managers;

import java.util.List;
import javax.ejb.Local;
import ru.cardio.core.entity.MergedSessionsHistory;
import ru.cardio.core.jpa.entity.SessionsHistory;
import ru.cardio.exceptions.CardioException;

/**
 *
 * @author rogvold
 */
@Local
public interface SessionsHistoryManagerLocal {

    public void initAllSessionsHistory() throws CardioException;

    public List<SessionsHistory> getUserSessionsDictionary(Long userId) throws CardioException;

    public SessionsHistory getLastUserSessionHistory(Long userId) throws CardioException;

    public Long updateSessionsHistory(Long userId, List<Long> newSessions, List<Long> deletedSessions, Long updatedSession) throws CardioException;

    public MergedSessionsHistory getMergedSessionHistory(Long userId, Long counter) throws CardioException;
}
