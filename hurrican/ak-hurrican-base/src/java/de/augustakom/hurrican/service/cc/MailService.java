package de.augustakom.hurrican.service.cc;

import java.util.*;
import org.springframework.mail.javamail.JavaMailSender;

import de.augustakom.hurrican.exceptions.ProcessPendingEmailsException;
import de.augustakom.hurrican.model.cc.Mail;
import de.augustakom.hurrican.model.cc.MailAttachment;
import de.augustakom.hurrican.service.base.exceptions.FindException;

public interface MailService extends ICCService {

    /**
     * Anzahl der maximalen Versuche eine Email zu verschicken
     */
    static final int MAX_NUMBER_OF_TRIES = 5;

    /**
     * Speichert die zu sendende Mail in der Datenbank.
     *
     * @param mail
     * @param sessionId of current session
     * @throws IllegalArgumentException falls eine Mail-Adresse ungültig ist, keine Versuche übrig oder falls die Mail
     *                                  bereits gesendet wurde
     */
    void sendMail(Mail mail, Long sessionId);

    /**
     * Speichert die zu sendende Mail mit den angegebene Attachments in der Datenbank.
     *
     * @param mail
     * @param sessionId       of current session
     * @param mailAttachments
     * @throws IllegalArgumentException falls eine Mail-Adresse ungültig ist
     */
    void sendMail(Mail mail, Long sessionId, MailAttachment... mailAttachments);

    /**
     * Arbeitet analog zu sendMail Als User wird jedoch Hurrican-Server angegeben
     */
    void sendMailFromHurricanServer(Mail mail);


    /**
     * Setzt das Datum, an dem die Mail versendet wurde, auf das aktuelle und speichert die Mail
     *
     * @param mail
     * @throws IllegalArgumentException falls Mail bereits gesendet wurde
     */
    void sendMailSuccessful(Mail mail);


    /**
     * Setzt die Anzahl der Veruche für die übergebene Mail um eins hoch und speichert die Mail. Falls dadurch {@link
     * MailService#MAX_NUMBER_OF_TRIES} überschritten wird, wird eine Fehlermeldung geloggt
     *
     * @param mail
     */
    void sendMailFailed(Mail mail);

    /**
     * Liefert alle offenen Mails (<code>{@link Mail#getSentAt()} == null</code>), bei denen die Anzahl der Versuche
     * dieser zu verschickenden Mail kleiner als <code>{@link MailService#MAX_NUMBER_OF_TRIES}</code> ist
     *
     * @return Liste mit allen offenen Mails mit noch vorhandenen Versuchen oder leere Liste
     * @throws FindException falls bei Suche ein Fehler auftritt
     */
    List<Mail> findAllPendingMails() throws FindException;

    /**
     * Sendet alle noch nicht versandeten E-Mails über den {@link JavaMailSender} heraus. Wird durch den Schedueler-Job
     * 'ProcessPendingEmailsJob' ausgelöst.
     *
     * @throws ProcessPendingEmailsException wird geworfen wenn bei der Erzeugung oder beim Versand der E-Mail(s) ein
     *                                       Fehler auftrat.
     */
    void processPendingEmails() throws ProcessPendingEmailsException;

}
