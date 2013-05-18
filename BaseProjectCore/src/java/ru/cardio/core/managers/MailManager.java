package ru.cardio.core.managers;

import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.ejb.Asynchronous;
import javax.ejb.Stateless;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import ru.cardio.exceptions.CardioException;
import ru.cardio.utils.StringUtils;

/**
 *
 * @author rogvold
 */
@Stateless
public class MailManager implements MailManagerLocal {

    public static final String MAIL_JNDI = "mail/myMailSession";
    @Resource(name = "mail/myMailSession")
    private Session mailSession;

    @Override
    @Asynchronous
    public void sendMail(String to, String theme, Long timestamp, String text) throws CardioException {
        try {

            System.out.println("sendMail(): to = " + to + "; theme = " + theme + "; text = " + text);

            if (StringUtils.isValidEmail(to) == false) {
                System.out.println("invalid email (email=" + to + ")");
                throw new CardioException("invalid email");
            }

            System.out.println("sendMail(): try to send message;  to = " + to + " from = " + theme + " text = " + text);
            Message message = new MimeMessage(mailSession);
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to, false));
            message.setSubject(theme);
            message.setText(text);
            Date timeStamp = new Date();
            message.setSentDate(new Date(timestamp));
            Transport.send(message);
            System.out.println("sendMail(): message sent!  to = " + to + " from = " + theme + " text = " + text);
        } catch (MessagingException ex) {
            System.out.println("ERROR WHILE SENDING EMAIL");
            System.out.println(ex.toString());
            Logger.getLogger(MailManager.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
}
