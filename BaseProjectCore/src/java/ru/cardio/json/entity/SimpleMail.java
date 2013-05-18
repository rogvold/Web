package ru.cardio.json.entity;

/**
 *
 * @author Shaykhlislamov Sabir (email: sha-sabir@yandex.ru)
 */
public class SimpleMail {

    private String text;
    private String recipient;
    private String theme;
    private Long timestamp;
    private Integer alertLevel;

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getTheme() {
        return theme;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public Integer getAlertLevel() {
        return alertLevel;
    }

    public void setAlertLevel(Integer alertLevel) {
        this.alertLevel = alertLevel;
    }
    
    
}
