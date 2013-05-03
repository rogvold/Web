package ru.cardio.json.entity;

/**
 *
 * @author Shaykhlislamov Sabir (email: sha-sabir@yandex.ru)
 */
/**
 * represents point in timetable (for ex. 3rd pair on Wednesday - day=3, lesson
 * = 3)
 *
 * @author rogvold
 */
public class SimpleTimetablePoint {

    protected Integer day;
    protected Integer lesson;

    public SimpleTimetablePoint(Integer day, Integer lesson) {
        this.day = day;
        this.lesson = lesson;
    }

    public Integer getDay() {
        return day;
    }

    public void setDay(Integer day) {
        this.day = day;
    }

    public Integer getLesson() {
        return lesson;
    }

    public void setLesson(Integer lesson) {
        this.lesson = lesson;
    }

    @Override
    public boolean equals(Object obj) {
        SimpleTimetablePoint oSTP = (SimpleTimetablePoint) obj;
        if (oSTP.getDay().equals(this.getDay()) && oSTP.getLesson().equals(this.getLesson())) {
            return true;
        }
        return false;
    }
}
