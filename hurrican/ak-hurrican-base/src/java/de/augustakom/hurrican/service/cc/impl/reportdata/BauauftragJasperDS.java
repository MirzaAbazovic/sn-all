/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 23.03.2005 08:16:53
 */
package de.augustakom.hurrican.service.cc.impl.reportdata;

import java.time.*;
import java.time.format.*;
import java.util.*;
import net.sf.jasperreports.engine.JRException;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import de.augustakom.common.tools.lang.BooleanTools;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.common.tools.lang.StringTools;
import de.augustakom.common.tools.reports.AKReportException;
import de.augustakom.hurrican.model.billing.Rufnummer;
import de.augustakom.hurrican.model.cc.Abteilung;
import de.augustakom.hurrican.model.cc.AccountArt;
import de.augustakom.hurrican.model.cc.BAVerlaufAnlass;
import de.augustakom.hurrican.model.cc.ExtServiceProvider;
import de.augustakom.hurrican.model.cc.IntAccount;
import de.augustakom.hurrican.model.cc.Niederlassung;
import de.augustakom.hurrican.model.cc.Reference;
import de.augustakom.hurrican.model.cc.VPN;
import de.augustakom.hurrican.model.cc.VPNKonfiguration;
import de.augustakom.hurrican.model.cc.VerbindungsBezeichnung;
import de.augustakom.hurrican.model.cc.VerlaufAbteilung;
import de.augustakom.hurrican.model.cc.view.TimeSlotHolder;
import de.augustakom.hurrican.service.billing.RufnummerService;
import de.augustakom.hurrican.service.cc.AccountService;
import de.augustakom.hurrican.service.cc.BAConfigService;
import de.augustakom.hurrican.service.cc.BAService;
import de.augustakom.hurrican.service.cc.ExtServiceProviderService;
import de.augustakom.hurrican.service.cc.NiederlassungService;
import de.augustakom.hurrican.service.cc.PhysikService;
import de.augustakom.hurrican.service.cc.ReferenceService;
import de.augustakom.hurrican.service.cc.VPNService;
import de.augustakom.hurrican.service.cc.ffm.FFMService;
import de.augustakom.hurrican.service.cc.impl.helper.VoIpDataHelper;
import de.mnet.common.tools.DateConverterUtils;
import de.mnet.esb.cdm.resource.workforceservice.v1.RequestedTimeslot;
import de.mnet.esb.cdm.resource.workforceservice.v1.WorkforceOrder;


/**
 * Jasper-DataSource, um einen Bauauftrag zu drucken.
 *
 *
 */
public class BauauftragJasperDS extends AbstractVerlaufJasperDS {

    private static final Logger LOGGER = Logger.getLogger(BauauftragJasperDS.class);

    private BAVerlaufAnlass baTyp = null;
    private VPN vpn = null;
    private VPNKonfiguration vpnKonf = null;
    private VerbindungsBezeichnung vpnVbz = null;
    private IntAccount intAccount = null;
    private String accountRealm = null;
    private AccountArt accountArt = null;
    private Rufnummer rufnummer = null;
    private Reference instRef = null;
    private Boolean egMontageMnet = null;
    private ExtServiceProvider extPartner = null;
    private Niederlassung dispoNL = null;
    private TimeSlotHolder timeSlotHolder;
    private WorkforceOrder workforceOrder;

    private boolean printData = true;

    /**
     * Konstruktor mit Angabe der Verlaufs-ID des zu druckenden Bauauftrags.
     *
     * @param verlaufId verlauf ID
     * @throws AKReportException
     */
    public BauauftragJasperDS(Long verlaufId) throws AKReportException {
        super(verlaufId);
        init();
    }

    /**
     * @see de.augustakom.hurrican.service.cc.impl.reportdata.AbstractCCJasperDS#init()
     */
    @Override
    protected void init() throws AKReportException {
        try {
            BAConfigService bas = getCCService(BAConfigService.class);
            baTyp = bas.findBAVerlaufAnlass(verlauf.getAnlass());

            VPNService vpns = getCCService(VPNService.class);
            vpn = vpns.findVPNByAuftragId(verlauf.getAuftragId());
            vpnKonf = vpns.findVPNKonfiguration4Auftrag(verlauf.getAuftragId());
            if (vpnKonf != null) {
                PhysikService ps = getCCService(PhysikService.class);
                vpnVbz = ps.findVerbindungsBezeichnungByAuftragId(vpnKonf.getPhysAuftragId());
            }

            AccountService accs = getCCService(AccountService.class);
            intAccount = accs.findIntAccountById(auftragTechnik.getIntAccountId());
            if (intAccount != null) {
                accountRealm = accs.getAccountRealm(verlauf.getAuftragId());
                accountArt = accs.findAccountArt4LiNr(intAccount.getLiNr());
            }

            if ((auftragDaten != null) && (auftragDaten.getAuftragNoOrig() != null)) {
                RufnummerService rs = getBillingService(RufnummerService.class);
                List<Rufnummer> rns = rs.findByParam(Rufnummer.STRATEGY_FIND_BY_AUFTRAG_NO_ORIG,
                        new Object[] { auftragDaten.getAuftragNoOrig(), Boolean.TRUE });
                rufnummer = ((rns != null) && (!rns.isEmpty())) ? rns.get(0) : null;
            }

            if (verlauf.getInstallationRefId() != null) {
                ReferenceService rs = getCCService(ReferenceService.class);
                instRef = rs.findReference(verlauf.getInstallationRefId());
            }

            // Ermittle externe Partner
            BAService bs = getCCService(BAService.class);
            VerlaufAbteilung va = bs.findVerlaufAbteilung(verlauf.getId(), Abteilung.EXTERN);
            // EG-Montage auswerten
            egMontageMnet = bs.hasExternInstallation(verlauf.getId());

            if ((va != null) && (va.getExtServiceProviderId() != null)) {
                ExtServiceProviderService es = getCCService(ExtServiceProviderService.class);
                extPartner = es.findById(va.getExtServiceProviderId());
            }

            // Telefonnummer der Dispo ermitteln
            VerlaufAbteilung vaDispo = bs.findVAOfVerteilungsAbt(verlauf.getId());
            if ((vaDispo != null) && (vaDispo.getNiederlassungId() != null)) {
                NiederlassungService ns = getCCService(NiederlassungService.class);
                dispoNL = ns.findNiederlassung(vaDispo.getNiederlassungId());
            }

            // Ermittle Zeitfenster fuer Installation
            timeSlotHolder = bs.getTimeSlotHolder(auftragDaten.getAuftragId());
            FFMService ffmService = getCCService(FFMService.class);
            workforceOrder = ffmService.createOrder(verlauf);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new AKReportException("Projektierungsdaten konnten nicht ermittelt werden!", e);
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
     * @see de.augustakom.hurrican.service.base.impl.AbstractHurricanJasperDS#getFieldValue(java.lang.String)
     */
    @Override
    protected Object getFieldValue(String field) throws JRException {
        if ("AUFTRAGSART".equals(field)) {
            return (baTyp != null) ? baTyp.getName() : null;
        }
        else if ("VPLS_ID".equals(field)) {
            return (auftragTechnik != null) ? auftragTechnik.getVplsId() : null;
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
        else if ("MONTAGE_AKOM".equals(field)) {
            return (egMontageMnet ||
                    ((instRef != null) && BooleanTools.getBoolean(instRef.getIntValue()))
                    || (extPartner != null))
                    ? Boolean.TRUE : null;
        }
        else if ("INSTALLATION_FROM".equals(field)) {
            if ((instRef == null) && (extPartner == null)) {
                return null;
            }
            if (instRef == null) {
                return extPartner.getName();
            }
            else if (extPartner == null) {
                return instRef.getStrValue();
            }
            else {
                return StringTools.join(new String[] { instRef.getStrValue(), extPartner.getName() }, " / ", false);
            }
        }
        else if ("DISPO_PHONE".equals(field)) {
            return ((dispoNL != null) && (dispoNL.getDispoPhone() != null))
                    ? StringUtils.trimToEmpty(dispoNL.getDispoPhone()) : null;
        }
        else if ("TIME_SLOT".equals(field)) {
            return getTimeSlot();
        }
        else if ("PPPOE_USER".equals(field)) {
            return VoIpDataHelper.getPppoeUser(auftragDaten);
        }
        else if ("PPPOE_PW".equals(field)) {
            return VoIpDataHelper.PPPOE_PW;
        }
        else {
            return super.getFieldValue(field);
        }
    }

    /**
     * Zunaechst wird versucht das Zeitfenster aus einer eventuell verfuegbaren FFM Workforce Order zu entnehmen. Sollte
     * keine Order existieren, wird als Fallback auf die TAL/Taifun Zeitfenster zurueckgegriffen. Gibt es hier keinen
     * match mit dem Realisierungstermin bleibt der TimeSlot leer.
     */
    private String getTimeSlot() {
        StringBuilder sb = new StringBuilder();
        LocalTime from = null;
        LocalTime to = null;
        LocalDate date = null;
        if (workforceOrder != null && workforceOrder.getRequestedTimeSlot() != null) {
            // FFM Zeitfenster verfuegbar - hat Vorrang
            date = getTimeSlotDate(workforceOrder.getRequestedTimeSlot());
            if (date != null) {
                from = DateConverterUtils.asLocalTime(workforceOrder.getRequestedTimeSlot().getEarliestStart());
                to = DateConverterUtils.asLocalTime(workforceOrder.getRequestedTimeSlot().getLatestEnd());
            }
        }
        else if (timeSlotHolder != null) {
            // Fallback richtet sich nach dem Realisierungstermin des Bauauftrages und den verfuegbaren Zeitfenstern
            date = DateConverterUtils.asLocalDate(verlauf.getRealisierungstermin());
            TimeSlotHolder.Candidate candidate = timeSlotHolder.matchCandidate(date);
            from = timeSlotHolder.getTimeSlotToUseDaytimeFrom(candidate);
            to = timeSlotHolder.getTimeSlotToUseDaytimeTo(candidate);
        }

        if (date != null) {
            sb.append((date != null)
                    ? date.format(DateTimeFormatter.ofPattern(DateTools.PATTERN_DAY_MONTH_YEAR)) : "");
            sb.append(" von ").append((from != null)
                    ? from.format(DateTimeFormatter.ofPattern(DateTools.PATTERN_TIME)): "");
            sb.append(" bis ").append((to != null)
                    ? to.format(DateTimeFormatter.ofPattern(DateTools.PATTERN_TIME)): "");
        }
        return sb.toString();
    }

    private LocalDate getTimeSlotDate(RequestedTimeslot requestedTimeslot) {
        LocalDateTime dateTime = requestedTimeslot.getEarliestStart();
        if (dateTime == null) {
            requestedTimeslot.getLatestStart();
        }
        if (dateTime == null) {
            requestedTimeslot.getEarliestEnd();
        }
        if (dateTime == null) {
            requestedTimeslot.getLatestEnd();
        }
        return (dateTime != null)? dateTime.toLocalDate() : null;
    }

}
