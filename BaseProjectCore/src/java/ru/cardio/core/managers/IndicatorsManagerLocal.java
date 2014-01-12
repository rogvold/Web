package ru.cardio.core.managers;

import java.util.List;
import javax.ejb.Local;
import ru.cardio.core.jpa.entity.CardioSessionEvaluation;
import ru.cardio.exceptions.CardioException;
import ru.cardio.graphics.MyPlot;
import ru.cardio.indicators.AbstractIndicatorsService;

/**
 *
 * @author rogvold
 */
@Local
public interface IndicatorsManagerLocal {

    public MyPlot getPlotOfParameters(Long sessionId, AbstractIndicatorsService a, String indicatorName, long msStep) throws Exception;

    public List<MyPlot> getAllImplementedParametersPlots(Long sessionId, long msStep) throws Exception;

    public String getJsonOfAllImplementedParametersPlots(Long sessionId, long msStep) throws Exception;
    
    public List<CardioSessionEvaluation> recalculateAllEvaluations(Long sessionId, long msStep) throws CardioException;
    public void recalculateAllEvaluations(Long userId) throws CardioException;

    public Double getLastTension(Long sessionId) throws CardioException;
}
