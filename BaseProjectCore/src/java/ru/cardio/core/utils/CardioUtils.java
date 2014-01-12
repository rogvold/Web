package ru.cardio.core.utils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import ru.cardio.core.entity.SimplePoint;

/**
 *
 * @author rogvold
 */
public class CardioUtils {

    public static final String DEFAULT_SESSION_RATES_DELIMITER = ";";

    public static List<Integer> getIntervalsFromString(String text, String delimiter) {
        List<Integer> list = new ArrayList();
        String[] array = text.split(delimiter);
        for (String s : array) {
            list.add(Integer.parseInt(s));
        }
        return list;
    }

    public static String getStringFromItervals(List<Integer> rates) {
        if (rates == null) {
            return "";
        }
        String s = "";
        for (int i = 0; i < rates.size() - 1; i++) {
            s += rates.get(i) + DEFAULT_SESSION_RATES_DELIMITER;
        }
        s += rates.get(rates.size() - 1);
        return s;
    }

    public static Date getEndDate(Date startDate, List<Integer> rates) {
        Long time = startDate.getTime();
        for (Integer r : rates) {
            time += r;
        }
        return new Date(time);
    }

    public static Long getTotalDuration(List<Integer> list) {
        Long sum = 0L;
        for (Integer i : list) {
            sum += i;
        }
        return sum;
    }

    public static List<Integer> getRatesFromSimplePointsArray(List<SimplePoint> spl) {
        List<Integer> list = new ArrayList();
        if (spl == null) {
            return null;
        }
        for (SimplePoint sp : spl) {
            list.add((int) Math.round(sp.getValue()));
        }
        return list;
    }
}
