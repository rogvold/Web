package ru.cardio.core.managers;

import javax.ejb.Local;
import ru.cardio.core.jpa.entity.IndicatorHash;
import ru.cardio.exceptions.CardioException;
import ru.cardio.indicators.AbstractIndicatorsService;

/**
 *
 * @author rogvold
 */
@Local
public interface IndicatorHashManagerLocal {
    
    
    public IndicatorHash getIndicatorHashBySessionId(Long sessionId);
    
    public String getCalculatedParameterJsonPlot(Long sessionId, AbstractIndicatorsService a, String indicatorName, boolean recalculate) throws CardioException;
    
}
