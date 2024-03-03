/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 23.03.2005 09:12:32
 */
package de.augustakom.hurrican.service.cc.impl.reportdata;

import java.util.*;
import net.sf.jasperreports.engine.JRException;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import de.augustakom.authentication.model.AKUser;
import de.augustakom.authentication.service.AKUserService;
import de.augustakom.common.tools.reports.AKReportException;
import de.augustakom.hurrican.model.cc.Abteilung;
import de.augustakom.hurrican.model.cc.Auftrag;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.AuftragTechnik;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.cc.Produkt;
import de.augustakom.hurrican.model.cc.VerbindungsBezeichnung;
import de.augustakom.hurrican.model.cc.Verlauf;
import de.augustakom.hurrican.model.cc.VerlaufAbteilung;
import de.augustakom.hurrican.model.cc.innenauftrag.IA;
import de.augustakom.hurrican.service.billing.OEService;
import de.augustakom.hurrican.service.cc.BAService;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.EndstellenService;
import de.augustakom.hurrican.service.cc.InnenauftragService;
import de.augustakom.hurrican.service.cc.PhysikService;
import de.augustakom.hurrican.service.cc.ProduktService;


/**
 * Abstrakte Jasper-DataSource, um die Basisdaten fuer Verlauf-Reports zu laden.
 *
 *
 */
public abstract class AbstractVerlaufJasperDS extends AbstractCCJasperDS {

    private static final Logger LOGGER = Logger.getLogger(AbstractVerlaufJasperDS.class);
    protected Auftrag auftrag = null;
    protected AuftragDaten auftragDaten = null;
    protected AuftragTechnik auftragTechnik = null;
    protected VerbindungsBezeichnung verbindungsBezeichnung = null;
    protected Produkt produkt = null;
    protected List<Endstelle> endstellen = null;
    protected Verlauf verlauf = null;
    protected VerlaufAbteilung verlAbtSCV = null;
    protected String billingProduktName = null;
    protected AKUser projektleiter = null;
    protected IA innenauftrag = null;

    /**
     * Konstruktor mit Angabe der ID.
     *
     * @param verlaufId
     * @throws AKReportException
     */
    public AbstractVerlaufJasperDS(Long verlaufId) throws AKReportException {
        super();
        initDefault(verlaufId);
    }

    /**
     * Laedt die Default-Daten.
     *
     * @param verlaufId
     * @throws AKReportException
     */
    protected void initDefault(Long verlaufId) throws AKReportException {
        try {
            BAService baService = getCCService(BAService.class);
            verlauf = baService.findVerlauf(verlaufId);
            if (verlauf == null) {
                throw new AKReportException("Projektierungsdaten konnten nicht ermittelt werden!");
            }

            verlAbtSCV = baService.findVerlaufAbteilung(verlaufId, Abteilung.AM);

            CCAuftragService auftragService = getCCService(CCAuftragService.class);
            auftrag = auftragService.findAuftragById(verlauf.getAuftragId());
            auftragDaten = auftragService.findAuftragDatenByAuftragId(verlauf.getAuftragId());
            auftragTechnik = auftragService.findAuftragTechnikByAuftragId(verlauf.getAuftragId());

            PhysikService ps = getCCService(PhysikService.class);
            verbindungsBezeichnung = ps.findVerbindungsBezeichnungById(auftragTechnik.getVbzId());

            EndstellenService esSrv = getCCService(EndstellenService.class);
            endstellen = esSrv.findEndstellen4Auftrag(verlauf.getAuftragId());

            ProduktService prodService = getCCService(ProduktService.class);
            produkt = prodService.findProdukt(auftragDaten.getProdId());

            InnenauftragService innenauftragService = getCCService(InnenauftragService.class);
            innenauftrag = innenauftragService.findIA4Auftrag(verlauf.getAuftragId());

            if (auftragDaten.getAuftragNoOrig() != null) {
                OEService oes = getBillingService(OEService.class);
                List<Long> aIds = new ArrayList<Long>();
                aIds.add(auftragDaten.getAuftragNoOrig());
                Map<Long, String> pnamen = oes.findProduktNamen4Auftraege(aIds);
                billingProduktName = pnamen.get(auftragDaten.getAuftragNoOrig());
            }

            if (auftragTechnik.getProjectResponsibleUserId() != null) {
                AKUserService userService = getAuthenticationService(AKUserService.class);
                projektleiter = userService.findById(auftragTechnik.getProjectResponsibleUserId());
            }
        }
        catch (AKReportException e) {
            throw e;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new AKReportException("Projektierungsdaten konnten nicht ermittelt werden!", e);
        }
    }

    /**
     * @see de.augustakom.hurrican.service.cc.impl.reportdata.AbstractCCJasperDS#getFieldValue(java.lang.String)
     */
    @Override
    protected Object getFieldValue(String field) throws JRException {
        if (VBZ_KEY.equals(field)) {
            return (verbindungsBezeichnung != null) ? verbindungsBezeichnung.getVbz() : null;
        }
        else if ("PROJEKTLEITER".equals(field)) {
            return (projektleiter != null) ? projektleiter.getNameAndFirstName() : null;
        }
        else if ("VERLAUF_ID".equals(field)) {
            return verlauf.getId();
        }
        else if ("AUFTRAG_ID".equals(field)) {
            return (auftragDaten != null) ? auftragDaten.getAuftragId() : null;
        }
        else if ("AUFTRAG__NO".equals(field)) {
            return (auftragDaten != null) ? auftragDaten.getAuftragNoOrig() : null;
        }
        else if ("KUNDE__NO".equals(field)) {
            return (auftrag != null) ? auftrag.getKundeNo() : null;
        }
        else if ("BEMERKUNG_SCV".equals(field)) {
            return (verlAbtSCV != null) ? verlAbtSCV.getBemerkung() : null;
        }
        else if ("BEARBEITER".equals(field)) {
            return (verlAbtSCV != null) ? verlAbtSCV.getBearbeiter() : null;
        }
        else if ("ES_ID_A".equals(field)) {
            return getEsId(Endstelle.ENDSTELLEN_TYP_A);
        }
        else if ("ES_ID_B".equals(field)) {
            return getEsId(Endstelle.ENDSTELLEN_TYP_B);
        }
        else if ("PRODUKT".equals(field)) {
            return (produkt != null) ? produkt.getAnschlussart() : null;
        }
        else if ("PRODUKTNAME".equals(field)) {
            return billingProduktName;
        }
        else if ("VORGABE_KUNDE".equals(field)) {
            return (auftragDaten != null) ? auftragDaten.getVorgabeKunde() : null;
        }
        else if ("VORGABE_SCV".equals(field)) {
            return (auftragDaten != null) ? auftragDaten.getVorgabeSCV() : null;
        }
        else if ("REALISIERUNGSTERMIN".equals(field)) {
            return (verlauf != null) ? verlauf.getRealisierungstermin() : null;
        }
        else if ("AUFTRAG_BEMERKUNG".equals(field)) {
            return (auftragDaten != null) ? auftragDaten.getBemerkungen() : null;
        }
        else if ("INNENAUFTRAG".equals(field)) {
            return (innenauftrag != null) ? innenauftrag.getIaNummer() : null;
        }
        else if ("KOSTENSTELLE".equals(field)) {
            return (innenauftrag != null) ? innenauftrag.getKostenstelle() : null;
        }

        LOGGER.warn("Field " + field + " wird von Jasper-DataSource nicht unterstuetzt!");
        return null;
    }

    /* Ermittelt die Endstellen-ID der Endstelle vom Typ <code>typ</code> */
    protected Long getEsId(String typ) {
        if (endstellen != null) {
            for (Endstelle es : endstellen) {
                if (StringUtils.equals(typ, es.getEndstelleTyp())) {
                    return es.getId();
                }
            }
        }
        return null;
    }
}


