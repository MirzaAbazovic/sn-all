/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 23.09.2004 15:07:47
 */
package de.augustakom.hurrican.service.cc.impl;

import java.util.*;
import javax.annotation.*;
import net.sf.jasperreports.engine.JasperPrint;
import org.apache.log4j.Logger;

import de.augustakom.common.tools.collections.CollectionMapConverter;
import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.common.tools.dao.iface.ByExampleDAO;
import de.augustakom.common.tools.dao.iface.FindDAO;
import de.augustakom.common.tools.dao.iface.StoreDAO;
import de.augustakom.common.tools.reports.AKReportException;
import de.augustakom.common.tools.reports.jasper.AKJasperReportContext;
import de.augustakom.common.tools.reports.jasper.AKJasperReportHelper;
import de.augustakom.hurrican.annotation.CcTxRequired;
import de.augustakom.hurrican.dao.cc.VpnDAO;
import de.augustakom.hurrican.model.billing.BAuftrag;
import de.augustakom.hurrican.model.billing.Kunde;
import de.augustakom.hurrican.model.cc.AuftragTechnik;
import de.augustakom.hurrican.model.cc.Niederlassung;
import de.augustakom.hurrican.model.cc.Reference;
import de.augustakom.hurrican.model.cc.VPN;
import de.augustakom.hurrican.model.cc.VPNKonfiguration;
import de.augustakom.hurrican.model.shared.view.AuftragEndstelleView;
import de.augustakom.hurrican.model.shared.view.VPNVertragView;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.billing.BillingAuftragService;
import de.augustakom.hurrican.service.billing.KundenService;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.NiederlassungService;
import de.augustakom.hurrican.service.cc.PhysikService;
import de.augustakom.hurrican.service.cc.ReferenceService;
import de.augustakom.hurrican.service.cc.VPNService;
import de.augustakom.hurrican.service.cc.impl.reportdata.VPNAccountJasperDS;
import de.augustakom.hurrican.service.cc.impl.reportdata.VPNUebersichtJasperDS;
import de.augustakom.hurrican.service.utils.HistoryHelper;


/**
 * Service-Implementierung fuer die Verwaltung von VPN-Auftraegen.
 *
 *
 */
@CcTxRequired
public class VPNServiceImpl implements VPNService {

    private static final Logger LOGGER = Logger.getLogger(VPNServiceImpl.class);

    @Resource(name = "de.augustakom.hurrican.service.cc.CCAuftragService")
    private CCAuftragService auftragService;
    @Resource(name = "de.augustakom.hurrican.service.cc.ReferenceService")
    private ReferenceService referenceService;
    @Resource(name = "de.augustakom.hurrican.service.cc.PhysikService")
    private PhysikService physikService;
    @Resource(name = "de.augustakom.hurrican.service.cc.NiederlassungService")
    private NiederlassungService niederlassungService;
    @Resource(name = "de.augustakom.hurrican.service.billing.KundenService")
    private KundenService kundenService;
    @Resource(name = "de.augustakom.hurrican.service.billing.BillingAuftragService")
    private BillingAuftragService billingAuftragService;

    @Resource(name = "vpnDAO")
    private VpnDAO vpnDao;

    @Override
    public List<VPNVertragView> findVPNVertraege() throws FindException {
        try {
            // VPNs aus CC laden
            List<VPN> vpns = (vpnDao).findAll(VPN.class);

            if (vpns != null) {
                List<Reference> list = referenceService.findReferences();
                Map<Long, String> refMap = new HashMap<Long, String>();
                CollectionMapConverter.convert2Map(list, refMap, "getId", "getStrValue");

                List<Niederlassung> nls = niederlassungService.findNiederlassungen();
                Map<Long, Niederlassung> nlMap = new HashMap<Long, Niederlassung>(nls.size());
                CollectionMapConverter.convert2Map(nls, nlMap, "getId", null);

                List<VPNVertragView> result = new ArrayList<>();
                for (VPN vpn : vpns) {
                    VPNVertragView vpnView = new VPNVertragView();
                    vpnView.setId(vpn.getId());
                    vpnView.setVpnNr(vpn.getVpnNr());
                    vpnView.setVpnName(vpn.getVpnName());
                    vpnView.setVpnType(refMap.get(vpn.getVpnType()));
                    vpnView.setKundeNo(vpn.getKundeNo());
                    vpnView.setVpnDatum(vpn.getDatum());
                    vpnView.setBemerkung(vpn.getBemerkung());
                    vpnView.setVpnEinwahl(vpn.getEinwahl());
                    vpnView.setProjektleiter(vpn.getProjektleiter());
                    vpnView.setNiederlassung(nlMap.get(vpn.getNiederlassungId()));
                    vpnView.setSalesRep(vpn.getSalesRep());
                    vpnView.setQos(vpn.getQos());

                    // Kunde laden
                    Kunde kunde = kundenService.findKunde(vpn.getKundeNo());
                    if (kunde != null) {
                        vpnView.setKundeName(kunde.getName());
                        vpnView.setKundeVorname(kunde.getVorname());
                    }

                    BAuftrag auftrag = billingAuftragService.findAuftrag(vpn.getVpnNr());
                    if (auftrag != null) {
                        vpnView.setCancelled(BAuftrag.STATUS_GEKUENDIGT.equals(auftrag.getAstatus()));
                    }

                    result.add(vpnView);
                }
                return result;
            }
            return null;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public VPN findVPN(Long vpnId) throws FindException {
        if (vpnId == null) { return null; }
        try {
            return ((FindDAO) vpnDao).findById(vpnId, VPN.class);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public VPN findVPNByVpnNr(Long vpnNr) throws FindException {
        if (vpnNr == null) { return null; }
        try {
            VPN example = new VPN();
            example.setVpnNr(vpnNr);

            List<VPN> result = ((ByExampleDAO) vpnDao).queryByExample(example, VPN.class);
            if (CollectionTools.isNotEmpty(result)) {
                if (result.size() == 1) {
                    return result.get(0);
                }
                else {
                    throw new FindException(FindException.INVALID_RESULT_SIZE,
                            new Object[] { Integer.valueOf(1), Integer.valueOf(result.size()) });
                }
            }

            return null;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public VPN findVPNByName(String vpnName) throws FindException {
        if (vpnName == null) { return null; }
        try {
            VPN example = new VPN();
            example.setVpnName(vpnName);

            List<VPN> result = ((ByExampleDAO) vpnDao).queryByExample(example, VPN.class);
            if (CollectionTools.isNotEmpty(result)) {
                if (result.size() == 1) {
                    return result.get(0);
                }
                else {
                    throw new FindException(FindException.INVALID_RESULT_SIZE,
                            new Object[] { Integer.valueOf(1), Integer.valueOf(result.size()) });
                }
            }

            return null;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public VPN findVPNByAuftragId(Long ccAuftragId) throws FindException {
        try {
            return (vpnDao).findVPNByAuftragId(ccAuftragId);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public void saveVPN(VPN toSave) throws StoreException {
        try {
            ((StoreDAO) vpnDao).store(toSave);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException(StoreException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public void addAuftrag2VPN(Long vpnId, AuftragTechnik auftragTechnik) throws StoreException {
        if (vpnId == null) {
            throw new StoreException(StoreException.UNABLE_TO_ADD_AUFTRAG2VPN,
                    new Object[] { "Die VPN-ID wurde nicht übermittelt." });
        }
        if (auftragTechnik == null) {
            throw new StoreException(StoreException.UNABLE_TO_ADD_AUFTRAG2VPN,
                    new Object[] { "Es wurde kein Auftrag angegeben, der in den VPN übernommen werden soll." });
        }

        try {
            auftragTechnik.setVpnId(vpnId);
            auftragService.saveAuftragTechnik(auftragTechnik, true);

            physikService.reCalculateVerbindungsBezeichnung(auftragTechnik.getAuftragId());
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException(StoreException.UNABLE_TO_ADD_AUFTRAG2VPN, new Object[] { e.getMessage() }, e);
        }
    }


    @Override
    public void removeAuftragFromVPN(AuftragTechnik auftragTechnik) throws StoreException {
        if (auftragTechnik == null) {
            throw new StoreException(StoreException.UNABLE_TO_REMOVE_AUFTRAG_FROM_VPN,
                    new Object[] { "Es wurde kein Auftrag angegeben, der aus dem VPN entfernt werden soll." });
        }

        try {
            auftragTechnik.setVpnId(null);
            auftragService.saveAuftragTechnik(auftragTechnik, true);

            physikService.reCalculateVerbindungsBezeichnung(auftragTechnik.getAuftragId());
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException(StoreException.UNABLE_TO_REMOVE_AUFTRAG_FROM_VPN,
                    new Object[] { e.getMessage() }, e);
        }
    }

    @Override
    public VPNKonfiguration findVPNKonfiguration4Auftrag(Long ccAuftragId) throws FindException {
        if (ccAuftragId == null) { return null; }
        try {
            return (vpnDao).findVPNKonfiguration4Auftrag(ccAuftragId);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public VPNKonfiguration saveVPNKonfiguration(VPNKonfiguration toSave, boolean makeHistory) throws StoreException {
        if (toSave == null) { throw new StoreException(StoreException.ERROR_INVALID_PARAMETER_TO_STORE); }
        try {
            VpnDAO dao = vpnDao;
            if (makeHistory && (toSave.getId() != null)) {
                Date now = new Date();
                VPNKonfiguration newVPN = dao.update4History(toSave, toSave.getId(), now);
                return newVPN;
            }
            else {
                HistoryHelper.checkHistoryDates(toSave);
                dao.store(toSave);
                return toSave;
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException(StoreException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public JasperPrint reportVPNUebersicht(Long vpnId) throws AKReportException {
        try {
            List<AuftragEndstelleView> data = auftragService.findAuftragEndstelleViews4VPN(vpnId, null);
            return reportVPNUebersicht(vpnId, data);
        }
        catch (AKReportException e) {
            throw e;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new AKReportException(e.getMessage(), e);
        }
    }

    @Override
    public JasperPrint reportVPNUebersicht(Long vpnId, List<AuftragEndstelleView> data) throws AKReportException {
        if (vpnId == null) {
            throw new AKReportException("Report konnte nicht erstellt werden, da keine VPN-ID angegeben wurde.");
        }

        try {
            // VPN-Daten laden
            VPN vpn = findVPN(vpnId);
            if (vpn == null) {
                throw new AKReportException("Report konnte nicht erstellt werden, da die VPN-Daten nicht gefunden wurden.");
            }

            Kunde kunde = kundenService.findKunde(vpn.getKundeNo());

            Map<String, Object> repParams = new HashMap<String, Object>();
            repParams.put("VPN_ID", vpn.getId());
            repParams.put("KUNDE_NAME", ((kunde != null) ? kunde.getName() : ""));
            repParams.put("KUNDE_VORNAME", ((kunde != null) ? kunde.getVorname() : ""));

            AKJasperReportContext ctx = new AKJasperReportContext(
                    "de/augustakom/hurrican/reports/vpn/VPNUebersicht.jasper", repParams,
                    new VPNUebersichtJasperDS(data));

            AKJasperReportHelper helper = new AKJasperReportHelper();
            return helper.createReport(ctx);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new AKReportException(e.getMessage(), e);
        }
    }

    @Override
    public JasperPrint reportVPNAccount(Long auftragId) throws AKReportException {
        if (auftragId == null) {
            throw new AKReportException("Report konnte nicht erstellt werden, da keine Auftrags-ID angegeben wurde.");
        }

        try {
            VPNAccountJasperDS vpnDS = new VPNAccountJasperDS(auftragId);

            Map<String, Object> params = new HashMap<String, Object>();
            params.put("AUFTRAG_ID", auftragId);

            AKJasperReportContext ctx = new AKJasperReportContext(
                    "de/augustakom/hurrican/reports/vpn/VPNAccount.jasper",
                    params, vpnDS);

            AKJasperReportHelper jrh = new AKJasperReportHelper();
            JasperPrint jp = jrh.createReport(ctx);
            return jp;
        }
        catch (Exception e) {
            throw new AKReportException(e.getMessage(), e);
        }
    }


    /**
     * Injected
     */
    public void setAuftragService(CCAuftragService auftragService) {
        this.auftragService = auftragService;
    }

    /**
     * Injected
     */
    public void setReferenceService(ReferenceService referenceService) {
        this.referenceService = referenceService;
    }

    /**
     * Injected
     */
    public void setPhysikService(PhysikService physikService) {
        this.physikService = physikService;
    }

    /**
     * Set by init method
     */
    public void setKundenService(KundenService kundenService) {
        this.kundenService = kundenService;
    }

}


