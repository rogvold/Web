package ru.cardio.core.utils;

import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.List;
import ru.cardio.core.jpa.entity.CardioSession;
import ru.cardio.core.managers.CardioSessionManagerLocal;
import ru.cardio.core.managers.IndicatorHashManagerLocal;
import ru.cardio.exceptions.CardioException;
import ru.cardio.indicators.HRVIndicatorsService;

/**
 *
 * @author Shaykhlislamov Sabir (email: sha-sabir@yandex.ru)
 */
public class IndicatorUtils {

    public static final int STRESS_BORDER = 150;

    public static List<Float> getTensionArray(IndicatorHashManagerLocal hashMan, Long sessionId) throws CardioException {
        String jsonArray = hashMan.getCalculatedParameterJsonPlot(sessionId, new HRVIndicatorsService(), "IN", false);
        float[][] dummy = new float[0][0];
        float[][] newArray;
        Gson gson = new Gson();
        newArray = gson.fromJson(jsonArray, dummy.getClass());
        List<Float> list = new ArrayList();
        for (int i = 0; i < newArray.length; i++) {
            list.add(new Float(newArray[i][1]));
        }
        return list;
    }

    public static double getMaxTension(List<Float> list) throws CardioException {
        Float max = Float.MIN_VALUE;
        for (Float f : list) {
            if (f > max) {
                max = f;
            }
        }
        return Math.floor(max * 1.0) / 1.0;
    }

    public static double getMinTension(List<Float> list) throws CardioException {
        Float min = Float.MAX_VALUE;
        for (Float f : list) {
            if (f < min) {
                min = f;
            }
        }
        return Math.floor(min * 1) / 1.0;
    }

    public static double getAvrTension(List<Float> list) throws CardioException {
        if ((list == null) || (list.isEmpty())) {
            throw new CardioException("not enough data for tension plot");
        }
        double sum = 0.0;
        for (Float f : list) {
            sum += f;
        }
        return Math.floor(sum * 1.0 / list.size()) / 1.0;
    }

    public static double getStressPercents(List<Float> list) throws CardioException {
        if ((list == null) || (list.isEmpty())) {
            throw new CardioException("not enough data for tension plot");
        }
        int k = 0;
        for (Float f : list) {
            if (f > STRESS_BORDER) {
                k++;
            }
        }
        return Math.floor(100.0 * k / list.size()) / 100.0;
    }

    public static SimpleSession getSimpleSessionWithTension(IndicatorHashManagerLocal hashMan, CardioSession cs) throws CardioException {
        List<Float> list = getTensionArray(hashMan, cs.getId());
        SimpleSession ss = new SimpleSession(cs.getId(), cs.getStartDate().getTime(), cs.getEndDate().getTime(), getMaxTension(list), getMinTension(list), getAvrTension(list), getStressPercents(list));
        return ss;
    }

    public static List<SimpleSession> getUserSimpleSessions(Long userId, IndicatorHashManagerLocal hashMan, CardioSessionManagerLocal cardMan) throws CardioException {
        List<CardioSession> allSessions = cardMan.getUserCardioSessions(userId);
        List<SimpleSession> sList = new ArrayList();

        for (int i = allSessions.size() - 1; i >= 0; i--) {
            try {
                sList.add(getSimpleSessionWithTension(hashMan, allSessions.get(i)));
            } catch (CardioException e) {
            }
        }

//        for (CardioSession cs : allSessions) {
//            try {
//                sList.add(getSimpleSessionWithTension(hashMan, cs));
//            } catch (CardioException e) {
//            }
//        }
        return sList;
    }
}
