package ru.cardio.core.managers;

import java.util.List;
import javax.ejb.Local;
import ru.cardio.exceptions.CardioException;
import ru.cardio.json.entity.SimpleTimetablePoint;
import ru.cardio.json.entity.SimpleUser;
import ru.cardio.json.entity.SimpleUserTimetablePoint;
import ru.cardio.json.entity.UsersTimetablePoint;

/**
 *
 * @author rogvold
 */
@Local
public interface TimetableManagerLocal {

    public List<UsersTimetablePoint> getFullTimetable();

    public List<SimpleUserTimetablePoint> getFullInformativeTimetable()  throws CardioException;

    public UsersTimetablePoint getUsersTimetablePoint(SimpleTimetablePoint stp);

    public void addTimetablePoints(Long userId, List<SimpleTimetablePoint> stpList);

    public void addTimetablePoint(Long userId, SimpleTimetablePoint stp);

    public void updateTimetablePoints(Long userId, List<SimpleTimetablePoint> stpList);

    public List<SimpleTimetablePoint> getUserTimetable(Long userId);
    
    public List<SimpleUser> getSimpleUsersInTimetable() throws CardioException;
}
