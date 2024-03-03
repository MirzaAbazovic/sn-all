/**
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 27.08.2009 10:26:29
 */
package de.augustakom.hurrican.service.cc.impl.helper;

import java.util.*;
import javax.annotation.*;
import org.apache.commons.lang.SystemUtils;
import org.apache.log4j.Logger;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;

import de.augustakom.common.tools.net.AKMailException;
import de.augustakom.hurrican.model.billing.Rufnummer;
import de.augustakom.hurrican.model.billing.query.RufnummerQuery;
import de.augustakom.hurrican.model.cc.Lock;
import de.augustakom.hurrican.model.cc.SperreInfo;
import de.augustakom.hurrican.model.cc.VerbindungsBezeichnung;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.billing.RufnummerService;
import de.augustakom.hurrican.service.cc.PhysikService;
import de.augustakom.hurrican.service.cc.ReferenceService;
import de.augustakom.hurrican.service.cc.SperreVerteilungService;


/**
 * Klasse fuer Email-Benachrichtigung von Abteilungen bei Sperren
 *
 *
 */
public class SendMailHelper {
    private static final Logger LOGGER = Logger.getLogger(SendMailHelper.class);

    @Resource(name = "de.augustakom.hurrican.service.cc.PhysikService")
    private PhysikService physikService = null;
    @Resource(name = "de.augustakom.hurrican.service.billing.RufnummerService")
    private RufnummerService rufnummerService = null;
    @Resource(name = "de.augustakom.hurrican.service.cc.ReferenceService")
    private ReferenceService referenceService = null;
    @Resource(name = "de.augustakom.hurrican.service.cc.SperreVerteilungService")
    private SperreVerteilungService sperreVerteilungService = null;
    @Resource(name = "mailSender")
    private MailSender mailSender = null;

    /**
     * Methode prueft, ob in der Sperrkonfiguration zu dem Produkt (bzw. zu einer betroffenen Abteilung) eine
     * eMail-Adresse hinterlegt ist. Falls ja, wird an diese eMail-Adresse eine Mail mit bestimmten Informationen zu der
     * Sperre gesendet: <ul> <li>Kunde <li>Auftrag (VerbindungsBezeichnung) <li>Rufnummer (optional) <li>Sperrmodus
     * </ul>
     */
    public void sendMail4Lock(Lock lock, Set<Long> abteilungsIds) throws AKMailException {
        String[] emails = getEMails4Abteilungen(abteilungsIds);

        if ((emails == null) || (emails.length == 0)) {
            return;
        }

        SimpleMailMessage mailMessage = null;
        try {
            VerbindungsBezeichnung verbindungsBezeichnung = physikService.findVerbindungsBezeichnungByAuftragIdTx(lock.getAuftragId());
            List<Rufnummer> rufnummern = findRufnummern(lock);
            mailMessage = makeMailMessage(lock, emails, verbindungsBezeichnung, rufnummern);
            if (mailMessage != null) {
                mailSender.send(mailMessage);
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Sperre - E-Mail gesendet: " + mailMessage);
                }
            }
        }
        catch (MailException e) {
            LOGGER.error(e.getMessage(), e);
            throw new AKMailException(e.getMessage(), e);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new AKMailException(
                    "Bei der Abfrage der Daten für die Sperr-EMail ist ein Fehler aufgetreten. " +
                            "EMail wurde deshalb nicht gesendet!", e
            );
        }

    }


    /**
     * Sucht nach den EMail-Adressen der zu benachrichtigenden Abteilungen.
     */
    private String[] getEMails4Abteilungen(Set<Long> abteilungsIds) {
        List<String> emails = new ArrayList<String>();
        try {
            for (Long abteilungsId : abteilungsIds) {
                List<SperreInfo> sperreInfos = sperreVerteilungService.findSperreInfos(Boolean.TRUE, abteilungsId);
                if (sperreInfos != null) {
                    for (SperreInfo sperreInfo : sperreInfos) {
                        emails.add(sperreInfo.getEmail());
                    }
                }
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }

        return emails.toArray(new String[emails.size()]);
    }


    /**
     * Sucht die Rufnummern via der AuftragNoOrig, die zu einem Lock gehoeren.
     *
     * @param lock
     * @throws FindException
     */
    private List<Rufnummer> findRufnummern(Lock lock) throws FindException {
        List<Rufnummer> rufnummern = null;
        if (lock.getAuftragNoOrig() != null) {
            RufnummerQuery rnQuery = new RufnummerQuery();
            rnQuery.setAuftragNoOrig(lock.getAuftragNoOrig());
            rufnummern = rufnummerService.findByParam(
                    Rufnummer.STRATEGY_FIND_BY_QUERY, new Object[] { rnQuery });
        }
        return rufnummern;
    }


    /**
     * Baut sich die MailMessage zusammen.
     *
     * @param lock
     * @param emails                 An wen sollen die Emails geschickt werden
     * @param verbindungsBezeichnung
     * @param rufnummern             Welche Rufnummern sind betroffen
     * @throws FindException
     * @throws FindException
     */
    private SimpleMailMessage makeMailMessage(Lock lock, String[] emails, VerbindungsBezeichnung verbindungsBezeichnung, List<Rufnummer> rufnummern) throws FindException {
        StringBuilder details = new StringBuilder();
        details.append(lock.getKundeNo());

        if (verbindungsBezeichnung != null) {
            details.append(" ");
            details.append(verbindungsBezeichnung.getVbz());
        }

        if (rufnummern != null) {
            for (Rufnummer rufnummer : rufnummern) {
                details.append(" ");
                details.append(formatRufnummer(rufnummer));
            }
            details.append(SystemUtils.LINE_SEPARATOR);
        }

        if (details.length() > 0) {
            SimpleMailMessage mailMsg = new SimpleMailMessage();

            String lockMode = referenceService.findReference(lock.getLockModeRefId()).getStrValue();
            String mailText = makeMailText(lockMode, details, lock.getCreatedFrom());

            mailMsg.setFrom("Hurrican");
            mailMsg.setTo(emails);
            mailMsg.setSubject("Sperre +++++ Vorgangsnr. " + lock.getId());
            mailMsg.setText(mailText);
            return mailMsg;
        }
        return null;
    }


    /**
     * Baut sich den Text einer Sperrbenachrichtigungemail zusammen
     *
     * @param lock
     * @param details
     * @param lockMode
     * @param createdFrom
     * @throws FindException
     */
    private String makeMailText(String lockMode, StringBuilder details, String createdFrom) {
        return
                "Bitte um Bearbeitung der folgenden Sperren:"
                        + SystemUtils.LINE_SEPARATOR +
                        "Sperr-Art: " + lockMode
                        + SystemUtils.LINE_SEPARATOR
                        + details
                        + SystemUtils.LINE_SEPARATOR
                        + "Mit freundlichen Gruessen"
                        + SystemUtils.LINE_SEPARATOR
                        + createdFrom;
    }


    /**
     * Formatiert eine Rufnummer
     *
     * @param rufnummer
     */
    private StringBuilder formatRufnummer(Rufnummer rufnummer) {
        StringBuilder rufnummerString = new StringBuilder();
        rufnummerString.append(rufnummer.getOnKz());
        rufnummerString.append("/");
        rufnummerString.append(rufnummer.getDnBase());
        String directDial = rufnummer.getDirectDial();
        if (directDial != null) {
            rufnummerString.append("-");
            rufnummerString.append(directDial);
        }
        return rufnummerString;
    }


    public void setPhysikService(PhysikService physikService) {
        this.physikService = physikService;
    }


    public void setRufnummerService(RufnummerService rufnummerService) {
        this.rufnummerService = rufnummerService;
    }


    public void setReferenceService(ReferenceService referenceService) {
        this.referenceService = referenceService;
    }


    public void setSperreVerteilungService(SperreVerteilungService sperreVerteilungService) {
        this.sperreVerteilungService = sperreVerteilungService;
    }


    public void setMailSender(MailSender mailSender) {
        this.mailSender = mailSender;
    }

}
