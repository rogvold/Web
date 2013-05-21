package ru.cardio.json.entity;

/**
 *
 * @author Shaykhlislamov Sabir (email: sha-sabir@yandex.ru)
 */
public class SessionInfo extends JsonEntityAbstract {

    private Long id;
    private long start;
    private long end;

    public SessionInfo(Long id, long start, long end) {
        this.id = id;
        this.start = start;
        this.end = end;
    }
    
    public long getEnd() {
        return end;
    }

    public void setEnd(long end) {
        this.end = end;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public long getStart() {
        return start;
    }

    public void setStart(long start) {
        this.start = start;
    }
        
}
