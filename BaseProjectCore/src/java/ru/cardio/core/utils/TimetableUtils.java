package ru.cardio.core.utils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.util.List;
import ru.cardio.core.jpa.entity.UserCard;
import ru.cardio.core.managers.UserCardManagerLocal;
import ru.cardio.json.entity.SimpleTimetablePoint;

/**
 *
 * @author Shaykhlislamov Sabir (email: sha-sabir@yandex.ru)
 */
public class TimetableUtils {

    public static List<SimpleTimetablePoint> decodeTimetablePoints(Long userId, UserCardManagerLocal cardMan) {
        UserCard uc = cardMan.getCardByUserId(userId);
        if (uc.getJsonTimetable() == null) {
            return null;
        }
        Gson gson = new Gson();
        return gson.fromJson(uc.getJsonTimetable(), new TypeToken<List<SimpleTimetablePoint>>() {
        }.getType());
    }

    public static String encodeTimetablePoints(List<SimpleTimetablePoint> sList) {
        Gson gson = new Gson();
        return gson.toJson(sList);
    }

    public static List<SimpleTimetablePoint> deserializeGsonTimetablePointsList(String json) {
        Gson gson = new Gson();
        return gson.fromJson(json, new TypeToken<List<SimpleTimetablePoint>>() {
        }.getType());
    }
    
    
}
