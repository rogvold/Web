package ru.cardio.web.beans;

import java.util.Date;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import ru.cardio.core.managers.MailManagerLocal;
import ru.cardio.exceptions.CardioException;
import ru.cardio.json.entity.SimpleMail;

/**
 *
 * @author Shaykhlislamov Sabir (email: sha-sabir@yandex.ru)
 */
@ManagedBean
@ViewScoped
public class TestMailBean {

    @EJB
    MailManagerLocal mailMan;

    public void sendTest() throws CardioException {
        System.out.println("bean: sending mail");
        SimpleMail sm = new SimpleMail();
        sm.setAlertLevel(0);
        sm.setRecipient("sha-sabir@yandex.ru");
        sm.setText("test");
        sm.setTheme("test theme");
        sm.setTimestamp((new Date()).getTime());

        mailMan.sendMail(sm.getRecipient(), sm.getTheme(), sm.getTimestamp(), sm.getText());

    }
}
