/*
 * Copyright (c) 2009 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 09.03.2009 13:41:27
 */
package de.augustakom.hurrican.service.cc.impl;

import java.util.*;
import javax.annotation.*;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import de.augustakom.authentication.model.AKUser;
import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.common.tools.lang.BooleanTools;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.common.tools.lang.Pair;
import de.augustakom.common.tools.lang.ResourceReader;
import de.augustakom.common.tools.lang.StringTools;
import de.augustakom.hurrican.HurricanConstants;
import de.augustakom.hurrican.annotation.CcTxRequired;
import de.augustakom.hurrican.dao.cc.ExtServiceProviderDAO;
import de.augustakom.hurrican.model.billing.Kunde;
import de.augustakom.hurrican.model.cc.Abteilung;
import de.augustakom.hurrican.model.cc.Auftrag;
import de.augustakom.hurrican.model.cc.ExtServiceProvider;
import de.augustakom.hurrican.model.cc.Mail;
import de.augustakom.hurrican.model.cc.Niederlassung;
import de.augustakom.hurrican.model.cc.VerbindungsBezeichnung;
import de.augustakom.hurrican.model.cc.Verlauf;
import de.augustakom.hurrican.model.cc.VerlaufAbteilung;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.billing.KundenService;
import de.augustakom.hurrican.service.cc.BAService;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.ExtServiceProviderService;
import de.augustakom.hurrican.service.cc.MailService;
import de.augustakom.hurrican.service.cc.NiederlassungService;
import de.augustakom.hurrican.service.cc.PhysikService;

/**
 * Service-Implementierung fuer ExtServiceProviderService.
 *
 *
 */
@CcTxRequired
public class ExtServiceProviderServiceImpl extends DefaultCCService implements ExtServiceProviderService {

    private static final Logger LOGGER = Logger.getLogger(ExtServiceProviderServiceImpl.class);

    private static final String MNET_SUFFIX = "@m-net.de";

    @Resource(name = "de.augustakom.hurrican.service.cc.CCAuftragService")
    private CCAuftragService cas = null;

    @Resource(name = "de.augustakom.hurrican.service.billing.KundenService")
    private KundenService ks = null;

    @Resource(name = "de.augustakom.hurrican.service.cc.ExtServiceProviderService")
    private ExtServiceProviderService es = null;

    @Resource(name = "de.augustakom.hurrican.service.cc.PhysikService")
    private PhysikService phys = null;

    private ExtServiceProviderDAO extDAO = null;
    private ResourceReader resourceReader = new ResourceReader(
            "de.augustakom.hurrican.service.cc.resources.BAVerlauf");

    @Override
    public List<ExtServiceProvider> findAllServiceProvider() throws FindException {
        try {
            return getExtDAO().findAll(ExtServiceProvider.class);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(e.getMessage(), e);
        }
    }

    @Override
    public ExtServiceProvider findById(Long id) throws FindException {
        if (id == null) { return null; }
        try {
            return getExtDAO().findById(id, ExtServiceProvider.class);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(e.getMessage(), e);
        }
    }

    @Override
    public void sendAuftragEmail(Long verlaufId, List<Pair<byte[], String>> attachments, Long sessionId)
            throws FindException {
        sendEmail(verlaufId, attachments, sessionId, Boolean.FALSE);
    }

    @Override
    public void sendStornoEmail(Long verlaufId, Long sessionId) throws FindException {
        sendEmail(verlaufId, null, sessionId, Boolean.TRUE);
    }

    Niederlassung getNiederlassung(Long niederlassungsId) throws FindException, ServiceNotFoundException {
        NiederlassungService ns = getCCService(NiederlassungService.class);
        Niederlassung nl = ns.findNiederlassung(niederlassungsId);
        return nl;
    }

    private void sendEmail(Long verlaufId, List<Pair<byte[], String>> attachments, Long sessionId, Boolean storno)
            throws FindException {
        if (verlaufId == null) {
            throw new FindException(FindException.EMPTY_FIND_PARAMETER);
        }
        try {
            Verlauf verlauf = getBaService().findVerlauf(verlaufId);
            VerlaufAbteilung verlAbt = getBaService().findVerlaufAbteilung(verlaufId, Abteilung.EXTERN);
            ExtServiceProvider extServiceProvider = (verlAbt != null) ? es.findById(verlAbt.getExtServiceProviderId()) : null;
            Auftrag auftrag = (verlauf != null) ? cas.findAuftragById(verlauf.getAuftragId()) : null;
            Kunde kunde = (auftrag != null) ? ks.findKunde(auftrag.getKundeNo()) : null;
            AKUser user = getAKUserBySessionId(sessionId);

            // Checks
            if (extServiceProvider == null) {
                throw new FindException("Kein Service-Partner ermittelbar");
            }
            if (StringUtils.isBlank(extServiceProvider.getEmail())) {
                throw new FindException("Service-Partner besitzt keine Email-Adresse");
            }
            if ((verlAbt == null) || (verlAbt.getRealisierungsdatum() == null)) {
                throw new FindException("Kein Realisierungsdatum verfügbar");
            }
            if (kunde == null) {
                throw new FindException("Kunde nicht ermittelbar");
            }
            if ((user == null) || (user.getNiederlassungId() == null)) {
                throw new FindException("Niederlassung zum Benutzer kann nicht ermittelt werden.");
            }

            // Email der Technik-Abteilung ermitteln
            Niederlassung nl = getNiederlassung(user.getNiederlassungId());
            String ccAdresse = (nl != null) ? nl.getDispoTeampostfach() : null;

            if (StringUtils.isBlank(ccAdresse)) {
                throw new FindException("Adresse der Technik-Abteilung/Dispo nicht ermittelbar.");
            }

            // Subject generieren
            StringBuilder subject = new StringBuilder();

            if (BooleanTools.nullToFalse(storno)) {
                subject.append(resourceReader.getValue("storno.email.subject",
                        new Object[] { generateStringOfVbz(getAllVerbindungsbezeichnungen(verlauf), ", "), StringTools.join(new String[] { kunde.getName(), kunde.getVorname() }, " ", true),
                                DateTools.formatDate(verlAbt.getRealisierungsdatum(), DateTools.PATTERN_DAY_MONTH_YEAR) }
                ));
            }
            else {
                subject.append(resourceReader.getValue("verlauf.email.subject",
                        new Object[] { generateStringOfVbz(getAllVerbindungsbezeichnungen(verlauf), ", "), StringTools.join(new String[] { kunde.getName(), kunde.getVorname() }, " ", true),
                                DateTools.formatDate(verlAbt.getRealisierungsdatum(), DateTools.PATTERN_DAY_MONTH_YEAR) }
                ));
            }

            // EMail-Message
            Mail mail = new Mail();
            mail.setFrom(getMnetMail(extServiceProvider.getEmail(), ccAdresse));
            mail.setCc(getMnetMail(extServiceProvider.getEmail(), ccAdresse));
            mail.setTo(extServiceProvider.getEmail());
            mail.setSubject(subject.toString());
            if (auftrag != null) {
                mail.setAuftragId(auftrag.getAuftragId());
            }
            if (verlauf != null) {
                mail.setVerlaufId(verlauf.getId());
            }

            // BA als PDF anhaengen
            if (!BooleanTools.nullToFalse(storno)) {
                if (attachments == null || attachments.isEmpty()) {
                    mail.setText("Kein Bauauftrag vorhanden.");
                }
                else {
                    // Installationsauftrag + BA-Report als Anhang hinzufuegen, falls vorhanden
                    for (final Pair<byte[], String> attachment : attachments) {
                        mail.addMailAttachment(attachment.getSecond(), attachment.getFirst());
                    }
                }
            }
            sendEmail(mail, sessionId);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(e.getMessage(), e);
        }
    }

    /**
     * @return Returns the baService.
     * @throws ServiceNotFoundException
     */
    public BAService getBaService() throws ServiceNotFoundException {
        return getCCService(BAService.class);
    }

    void sendEmail(Mail mail, Long sessionId) throws IllegalArgumentException, ServiceNotFoundException {
        getCCService(MailService.class).sendMail(mail, sessionId);
    }

    /**
     * Prueft, ob in {@code mailAddresses} eine M-net Adresse mit eingetragen ist. Ist dies der Fall, wird diese zurueck
     * gegeben; andernfalls {@code defaultMail}
     */
    private String getMnetMail(String mailAddresses, String defaultMail) {
        String mnetMail = null;
        if (StringUtils.contains(mailAddresses, MNET_SUFFIX)) {
            String[] mails = null;
            if (StringUtils.contains(mailAddresses, HurricanConstants.EMAIL_SEPARATOR)) {
                mails = StringUtils.split(mailAddresses, HurricanConstants.EMAIL_SEPARATOR);
            }
            else if (StringUtils.contains(mailAddresses, ",")) {
                mails = StringUtils.split(mailAddresses, ",");
            }

            if (mails != null) {
                for (String mailAddress : mails) {
                    if (StringUtils.endsWith(mailAddress, MNET_SUFFIX)) {
                        mnetMail = mailAddress;
                        break;
                    }
                }
            }
        }
        return (StringUtils.isNotBlank(mnetMail)) ? mnetMail : defaultMail;
    }

    /**
     * Sammelt alle Verbindungsbezeichnungen zu einem Verlauf
     */
    Set<String> getAllVerbindungsbezeichnungen(Verlauf verlauf) throws FindException {
        if (verlauf == null) {
            return Collections.emptySet();
        }

        VerbindungsBezeichnung vbz;
        Set<Long> verlaufIds = verlauf.getAllOrderIdsOfVerlauf();
        Set<String> verbindungsBezeichnungen = new HashSet<String>();

        //Verbindungsbezeichnungen anhand der AuftragIds laden
        if (CollectionTools.isNotEmpty(verlaufIds)) {
            for (Long id : verlaufIds) {
                vbz = phys.findVerbindungsBezeichnungByAuftragId(id);
                if ((vbz != null) && StringUtils.isNotBlank(vbz.getVbz())) {
                    verbindungsBezeichnungen.add(vbz.getVbz());
                }
            }
        }
        return verbindungsBezeichnungen;
    }

    /**
     * Generiert einen String aus allen gefundenen Verbindungsbezeichnungen
     */
    String generateStringOfVbz(Set<String> verbindungsBezeichnungen, String seperator) {
        StringBuilder buffer = new StringBuilder();
        if (CollectionTools.isNotEmpty(verbindungsBezeichnungen)) {
            for (String bezeichnung : verbindungsBezeichnungen) {
                if (StringUtils.isNotBlank(bezeichnung)) {
                    if (buffer.length() > 0) {
                        buffer.append(seperator);
                    }
                    buffer.append(bezeichnung);
                }
            }
        }
        return buffer.toString();
    }

    /**
     * Injected
     */

    public ExtServiceProviderDAO getExtDAO() {
        return extDAO;
    }

    public void setExtDAO(ExtServiceProviderDAO extDAO) {
        this.extDAO = extDAO;
    }

    public void setCCAuftragService(CCAuftragService cas) {
        this.cas = cas;
    }

    public void setKundenService(KundenService ks) {
        this.ks = ks;
    }

    public void setExtServiceProviderService(ExtServiceProviderService es) {
        this.es = es;
    }

    public void setPhysikService(PhysikService phys) {
        this.phys = phys;
    }

}
