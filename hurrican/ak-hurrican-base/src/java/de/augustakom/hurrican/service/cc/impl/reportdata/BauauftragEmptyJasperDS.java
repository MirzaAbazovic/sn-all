/*
 * Copyright (c) 2007 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 08.03.2007 09:23:01
 */
package de.augustakom.hurrican.service.cc.impl.reportdata;

import java.util.*;
import net.sf.jasperreports.engine.JRException;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import de.augustakom.authentication.model.AKUser;
import de.augustakom.authentication.service.AKUserService;
import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.common.tools.lang.NumberTools;
import de.augustakom.common.tools.lang.StringTools;
import de.augustakom.common.tools.reports.AKReportException;
import de.augustakom.hurrican.model.billing.Rufnummer;
import de.augustakom.hurrican.model.cc.AccountArt;
import de.augustakom.hurrican.model.cc.Auftrag;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.AuftragTechnik;
import de.augustakom.hurrican.model.cc.BAVerlaufAnlass;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.cc.IntAccount;
import de.augustakom.hurrican.model.cc.Produkt;
import de.augustakom.hurrican.model.cc.VPN;
import de.augustakom.hurrican.model.cc.VPNKonfiguration;
import de.augustakom.hurrican.model.cc.VerbindungsBezeichnung;
import de.augustakom.hurrican.model.cc.Verlauf;
import de.augustakom.hurrican.model.cc.VerlaufStatus;
import de.augustakom.hurrican.service.billing.OEService;
import de.augustakom.hurrican.service.billing.RufnummerService;
import de.augustakom.hurrican.service.cc.AccountService;
import de.augustakom.hurrican.service.cc.BAService;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.EndstellenService;
import de.augustakom.hurrican.service.cc.PhysikService;
import de.augustakom.hurrican.service.cc.ProduktService;
import de.augustakom.hurrican.service.cc.VPNService;
import de.augustakom.hurrican.service.cc.impl.helper.VoIpDataHelper;


/**
 * Jasper-DataSource fuer einen Bauauftrag. <br> Im Unterschied zur 'normalen' BauauftragJasperDS wird hier kein Verlauf
 * uebergeben, sondern nur der Auftrag!
 *
 *
 */
public class BauauftragEmptyJasperDS extends AbstractCCJasperDS {

    private static final Logger LOGGER = Logger.getLogger(BauauftragEmptyJasperDS.class);

    private Long auftragId = null;

    protected Auftrag auftrag = null;
    protected AuftragDaten auftragDaten = null;
    protected AuftragTechnik auftragTechnik = null;
    protected VerbindungsBezeichnung verbindungsBezeichnung = null;
    protected Produkt produkt = null;
    protected List<Endstelle> endstellen = null;
    protected String billingProduktName = null;
    private VPN vpn = null;
    private VPNKonfiguration vpnKonf = null;
    private VerbindungsBezeichnung vpnVbz = null;
    private IntAccount intAccount = null;
    private String accountRealm = null;
    private AccountArt accountArt = null;
    private Rufnummer rufnummer = null;
    protected AKUser projektleiter = null;
    private Verlauf verlauf = null;

    private boolean printData = true;

    /**
     * Konstruktor mit Angabe der Auftrags-ID.
     *
     * @param auftragId
     * @throws AKReportException
     */
    public BauauftragEmptyJasperDS(Long auftragId) throws AKReportException {
        super();
        this.auftragId = auftragId;
        init();
    }

    /**
     * @see de.augustakom.hurrican.service.cc.impl.reportdata.AbstractCCJasperDS#init()
     */
    @Override
    protected void init() throws AKReportException {
        try {
            CCAuftragService as = getCCService(CCAuftragService.class);
            auftrag = as.findAuftragById(auftragId);
            auftragDaten = as.findAuftragDatenByAuftragId(auftragId);
            auftragTechnik = as.findAuftragTechnikByAuftragId(auftragId);

            PhysikService ps = getCCService(PhysikService.class);
            verbindungsBezeichnung = ps.findVerbindungsBezeichnungById(auftragTechnik.getVbzId());

            EndstellenService esSrv = getCCService(EndstellenService.class);
            endstellen = esSrv.findEndstellen4Auftrag(auftragId);

            if (auftragDaten != null) {
                ProduktService prodService = getCCService(ProduktService.class);
                produkt = prodService.findProdukt(auftragDaten.getProdId());

                if (auftragDaten.getAuftragNoOrig() != null) {
                    OEService oes = getBillingService(OEService.class);
                    List<Long> aIds = new ArrayList<Long>();
                    aIds.add(auftragDaten.getAuftragNoOrig());
                    Map<Long, String> pnamen = oes.findProduktNamen4Auftraege(aIds);
                    billingProduktName = pnamen.get(auftragDaten.getAuftragNoOrig());

                    RufnummerService rs = getBillingService(RufnummerService.class);
                    List<Rufnummer> rns = rs.findByParam(Rufnummer.STRATEGY_FIND_BY_AUFTRAG_NO_ORIG,
                            new Object[] { auftragDaten.getAuftragNoOrig(), Boolean.TRUE });
                    rufnummer = ((rns != null) && (!rns.isEmpty())) ? rns.get(0) : null;
                }
            }

            VPNService vpns = getCCService(VPNService.class);
            vpn = vpns.findVPNByAuftragId(auftragId);
            vpnKonf = vpns.findVPNKonfiguration4Auftrag(auftragId);
            if (vpnKonf != null) {
                vpnVbz = ps.findVerbindungsBezeichnungByAuftragId(vpnKonf.getPhysAuftragId());
            }

            AccountService accs = getCCService(AccountService.class);
            intAccount = accs.findIntAccountById(auftragTechnik.getIntAccountId());
            if (intAccount != null) {
                accountRealm = accs.getAccountRealm(auftragId);
                accountArt = accs.findAccountArt4LiNr(intAccount.getLiNr());
            }

            if (auftragTechnik.getProjectResponsibleUserId() != null) {
                AKUserService userService = getAuthenticationService(AKUserService.class);
                projektleiter = userService.findById(auftragTechnik.getProjectResponsibleUserId());
            }

            // Ermittle Verlauf fuer Neuschaltung
            BAService baService = getCCService(BAService.class);
            List<Verlauf> verlaeufe = baService.findVerlaeufe4Auftrag(auftragId);
            if (CollectionTools.isNotEmpty(verlaeufe)) {
                for (Verlauf v : verlaeufe) {
                    if (NumberTools.isIn(v.getAnlass(), new Long[] { BAVerlaufAnlass.NEUSCHALTUNG,
                            BAVerlaufAnlass.ABW_TKG46_NEUSCHALTUNG, BAVerlaufAnlass.ANSCHLUSSUEBERNAHME })
                            && !NumberTools.equal(v.getVerlaufStatusId(), VerlaufStatus.VERLAUF_STORNIERT)) {
                        verlauf = v;
                        break;
                    }
                }
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new AKReportException("Die Daten fuer den Bauauftrag konnten nicht ermittelt werden!", e);
        }
    }

    /**
     * @see net.sf.jasperreports.engine.JRDataSource#next()
     */
    @Override
    public boolean next() throws JRException {
        if (printData) {
            printData = false;
            return true;
        }
        return false;
    }

    /**
     * @see de.augustakom.hurrican.service.cc.impl.reportdata.AbstractCCJasperDS#getFieldValue(java.lang.String)
     */
    @Override
    protected Object getFieldValue(String field) throws JRException {
        if ("AUFTRAGSART".equals(field)) {
            return "unbekannt";
        }
        else if (VBZ_KEY.equals(field)) {
            return (verbindungsBezeichnung != null) ? verbindungsBezeichnung.getVbz() : null;
        }
        else if ("PROJEKTLEITER".equals(field)) {
            return (projektleiter != null) ? projektleiter.getNameAndFirstName() : null;
        }
        else if ("VPN_KANALB".equals(field)) {
            return (vpnKonf != null) ? vpnKonf.getKanalbuendelung() : null;
        }
        else if ("VPN_ANZAHLK".equals(field)) {
            return (vpnKonf != null) ? vpnKonf.getAnzahlKanaele() : null;
        }
        else if ("VPN_PHYS_VBZ".equals(field)) {
            return (vpnVbz != null) ? vpnVbz.getVbz() : null;
        }
        else if ("VPN_NR".equals(field)) {
            return (vpn != null) ? vpn.getVpnNr() : null;
        }
        else if ("INT_ACCOUNT".equals(field)) {
            if (intAccount != null) {
                return StringTools.join(new String[] { intAccount.getAccount(), accountRealm }, "", true);
            }
            return null;
        }
        else if ("INT_ACCOUNT_PW".equals(field)) {
            return (intAccount != null) ? intAccount.getPasswort() : null;
        }
        else if ("INT_ACCOUNT_TYP".equals(field)) {
            return (accountArt != null) ? accountArt.getText() : null;
        }
        else if ("PORTIERUNGSDATUM".equals(field)) {
            return (rufnummer != null) ? rufnummer.getRealDate() : null;
        }
        else if ("PORTIERUNG_VON".equals(field)) {
            return (rufnummer != null) ? rufnummer.getPortierungVon() : null;
        }
        else if ("PORTIERUNG_BIS".equals(field)) {
            return (rufnummer != null) ? rufnummer.getPortierungBis() : null;
        }
        else if ("VerbindungsBezeichnung".equals(field)) {
            return (verbindungsBezeichnung != null) ? verbindungsBezeichnung.getVbz() : null;
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
        else if ("BEARBEITER".equals(field)) {
            return (auftragDaten != null) ? auftragDaten.getBearbeiter() : null;
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
            return (auftragDaten != null) ? auftragDaten.getVorgabeSCV() : null;
        }
        else if ("AUFTRAG_BEMERKUNG".equals(field)) {
            return (auftragDaten != null) ? auftragDaten.getBemerkungen() : null;
        }
        else if ("VPLS_ID".equals(field)) {
            return (auftragTechnik != null) ? auftragTechnik.getVplsId() : null;
        }
        else if ("TIME_SLOT".equals(field)) {
            if ((auftragDaten != null) && (auftragDaten.getInbetriebnahme() != null)) {
                return DateTools.formatDate(auftragDaten.getInbetriebnahme(), DateTools.PATTERN_DAY_MONTH_YEAR);
            }
            else if ((verlauf != null) && (verlauf.getRealisierungstermin() != null)) {
                return DateTools.formatDate(verlauf.getRealisierungstermin(), DateTools.PATTERN_DAY_MONTH_YEAR);
            }
            else {
                return null;
            }
        }
        else if ("PPPOE_USER".equals(field)) {
            return VoIpDataHelper.getPppoeUser(auftragDaten);
        }
        else if ("PPPOE_PW".equals(field)) {
            return VoIpDataHelper.PPPOE_PW;
        }

        return null;
    }

    /* Ermittelt die Endstellen-ID der Endstelle vom Typ <code>typ</code> */
    private Long getEsId(String typ) {
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
