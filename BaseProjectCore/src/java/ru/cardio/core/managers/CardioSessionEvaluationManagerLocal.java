package ru.cardio.core.managers;

import java.util.List;
import javax.ejb.Local;
import ru.cardio.core.entity.SimpleEvaluation;
import ru.cardio.core.entity.SyncPullData;
import ru.cardio.core.entity.SyncPushData;
import ru.cardio.core.entity.SyncSession;
import ru.cardio.core.jpa.entity.CardioSessionEvaluation;
import ru.cardio.exceptions.CardioException;

/**
 *
 * @author rogvold
 */
@Local
public interface CardioSessionEvaluationManagerLocal {

    public CardioSessionEvaluation getCardioSessionEvaluation(Long sessionId, String name) throws CardioException;

    public boolean isExistsCardioSessionEvaluation(Long sessionId, String name) throws CardioException;

    public CardioSessionEvaluation createCardioSessionEvaluation(Long sessionId, String name, String data, boolean rewriteIfExists) throws CardioException;

    public CardioSessionEvaluation createCardioSessionEvaluation(Long sessionId, SimpleEvaluation se) throws CardioException;
    
    

    public void processNewSyncCardioSession(Long userId, SyncSession ss) throws CardioException;

    public void processDeletedSessions(List<Long> timestamps, Long userId) throws CardioException;
    
    public void processUpdatedSession(SyncSession ss, Long userId) throws CardioException;
    
    public Long pushSessions(Long userId, SyncPushData pushData) throws CardioException;
    
    public List<SimpleEvaluation> getSessionEvaluations(Long userId, Long timestamp) throws CardioException;
    
    public SyncPullData pullSessions(Long userId, Long localCounter) throws CardioException;
    
    public SyncSession getSessionWithEvaluation(Long sessionId) throws CardioException;
    
}
