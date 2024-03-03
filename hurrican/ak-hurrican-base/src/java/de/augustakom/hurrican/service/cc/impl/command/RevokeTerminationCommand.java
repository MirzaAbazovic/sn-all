/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 16.08.2004 13:56:39
 */
package de.augustakom.hurrican.service.cc.impl.command;

import java.util.*;
import org.apache.log4j.Logger;

import de.augustakom.common.service.exceptions.ServiceCommandException;
import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.common.tools.lang.BooleanTools;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.common.tools.lang.NumberTools;
import de.augustakom.hurrican.annotation.CcTxRequired;
import de.augustakom.hurrican.model.cc.Auftrag2TechLeistung;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.cc.IntAccount;
import de.augustakom.hurrican.model.cc.Produkt;
import de.augustakom.hurrican.model.cc.Rangierung;
import de.augustakom.hurrican.model.cc.temp.RevokeTerminationModel;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.HurricanServiceCommandException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.cc.AccountService;
import de.augustakom.hurrican.service.cc.CCLeistungsService;
import de.augustakom.hurrican.service.cc.EndstellenService;
import de.augustakom.hurrican.service.cc.ProduktService;
import de.augustakom.hurrican.service.cc.RangierungsService;

/**
 * Command-Klasse, um eine Kuendigung rueckgaengig zu machen. <br/>
 * Es werden dabei folgende Aktionen ausgefuehrt: <br/>
 * <ul>
 *     <li>Rangierung(en) werden aktiviert</li>
 *     <li>aktive Bauauftraege werden storniert</li>
 *     <li>techn. Leistungen werden aktiviert</li>
 *     <li>Auftragsart wird zurueck gesetzt</li>
 *     <li>Accounts werden entsperrt</li>
 *     <li>Auftragsstatus wird geaendert</li>
 *     <li>ggf. wird eine CPS-Tx erstellt und gesendet</li>
 * </ul>
 *
 *
 */
@CcTxRequired
public class RevokeTerminationCommand extends AbstractRevokeCommand {

    private static final Logger LOGGER = Logger.getLogger(RevokeTerminationCommand.class);

    private RevokeTerminationModel revokeTermination = null;

    private ProduktService produktService;
    private RangierungsService rangierungsService;
    private CCLeistungsService ccLeistungsService;
    private EndstellenService endstellenService;
    private AccountService accountService;

    /**
     * @see de.augustakom.common.service.iface.IServiceCommand#execute()
     */
    @Override
    public Object execute() throws ServiceCommandException {
        try {
            revokeTermination = (RevokeTerminationModel) getPreparedValue(KEY_REVOKE_MODEL);
            checkValues(revokeTermination);
            doRevoke();
        }
        catch (ServiceCommandException e) {
            throw e;
        }
        catch (Exception e) {
            throw new HurricanServiceCommandException(e);
        }

        return getWarnings();
    }


    @Override
    protected void doRevoke() throws HurricanServiceCommandException {
        if (BooleanTools.nullToFalse(revokeTermination.getIsRangierung())) {
            activateRangierung();
        }

        // Verlauf prüfen
        revokeProvisioningOrder(revokeTermination);

        if (BooleanTools.nullToFalse(revokeTermination.getIsTechLeistungen())) {
            activateTechService();
        }

        // Auftragsart umschreiben
        changeOrderType(revokeTermination);

        // Account entsperren (falls gesperrt)
        unlockAccount();

        if (BooleanTools.nullToFalse(revokeTermination.getIsAuftragStatus())) {
            // Auftragsstatus korrigieren
            // Kündigungsdatum entfernen
            changeOrderState(revokeTermination);
        }

        // CPS-Tx erstellen / senden
        sendCpsTransaction(revokeTermination);
    }

    /* Rangierung wieder aktivieren */
    private void activateRangierung() throws HurricanServiceCommandException {
        try {
            if (null != auftragDaten.getProdId()) {
                Produkt produkt = produktService.findProdukt(auftragDaten.getProdId());
                Integer esTyp = produkt.getEndstellenTyp();

                if ((esTyp != null) && esTyp.equals(Produkt.ES_TYP_A_UND_B)) {
                    activateMapping(Endstelle.ENDSTELLEN_TYP_A);
                    activateMapping(Endstelle.ENDSTELLEN_TYP_B);
                }
                else if ((esTyp != null) && esTyp.equals(Produkt.ES_TYP_NUR_B)) {
                    activateMapping(Endstelle.ENDSTELLEN_TYP_B);
                }
            }
        }
        catch (FindException e) {
            LOGGER.error(e.getMessage(), e);
            throw new HurricanServiceCommandException(e);
        }
    }

    /*
     * Rangierung wieder aktivieren - prüfen, ob nicht schon anderem Auftrag zugeordnet ist (ES_ID muss -1 sein oder
     * eine Endstellen-ID des wieder zu aktivierenden Auftrags haben) - prüfen, ob Rangierung GültigBis 01.01.2200
     * (ansonsten wurde schon eine Kreuzung ausgeführt; dann Kündigung rückgängig nicht möglich)
     */
    private void activateMapping(String endStellenTyp) throws HurricanServiceCommandException {
        // Methode erweitern, dass Endstellen A+B beruecksichtigt werden (evtl. Parameter in Methoden-Deklaration)
        // activateMapping nur durchfuehren, wenn Produkt entsprechend konfiguriert
        // Konstanten Produkt.ES_TYP_xxx

        if (endStellenTyp == null) {
            throw new HurricanServiceCommandException("Endstelle nicht definiert!");
        }

        try {
            Rangierung[] rangs = rangierungsService.findRangierungenTx(this.revokeTermination.getAuftragId(), endStellenTyp);
            if (rangs == null) {
                addWarning(this, "Rangierung der Endstelle " + endStellenTyp + " konnte nicht ermittelt werden!");
            }

            Endstelle esB = endstellenService.findEndstelle4Auftrag(this.revokeTermination.getAuftragId(), endStellenTyp);
            if (esB == null) {
                addWarning(this, "Endstelle " + endStellenTyp + " konnte nicht ermittelt werden!");
            }

            if (rangs != null) {
                for (Rangierung rangierung : rangs) {
                    Long esId = rangierung.getEsId();
                    Date gueltigBis = rangierung.getGueltigBis();

                    /*
                     * prüfen, ob nicht schon anderem Auftrag zugeordnet ist (ES_ID muss -1 oder NULL sein oder eine
                     * Endstellen-ID des wieder zu aktivierenden Auftrags haben)
                     */
                    if ((esId != null) &&
                            (!(esId.equals(Rangierung.RANGIERUNG_NOT_ACTIVE) || ((esB != null) && (NumberTools.equal(esB.getId(), esId)))))) {
                        throw new HurricanServiceCommandException("Rangierung ist bereits einem anderen Auftrag zugeordnet!");
                    }

                    /*
                     * prüfen, ob Rangierung GültigBis 01.01.2200 (ansonsten wurde schon eine Kreuzung ausgeführt; dann
                     * Kündigung rückgängig nicht möglich)
                     */
                    if ((gueltigBis != null) && !DateTools.isDateEqual(gueltigBis, DateTools.getHurricanEndDate())) {
                        throw new HurricanServiceCommandException("Das Gültigkeitsdatum (bis) " +
                                DateTools.formatDate(gueltigBis, DateTools.PATTERN_DAY_MONTH_YEAR) +
                                " der Rangierung ist nicht auf 'HurricanEndDate' gesetzt." +
                                " Somit wurde eine Kreuzung bereits ausgeführt! ES_ID der Rangierung: " + esId);
                    }

                    // FreigabeAb Datum wieder auf NULL setzen
                    rangierung.setFreigabeAb(null);
                    rangierung.setEsId((esB == null) ? Rangierung.RANGIERUNG_NOT_ACTIVE : esB.getId());
                    rangierung.setBemerkung(null);
                    rangierungsService.saveRangierung(rangierung, false);
                }
            }
        }
        catch (StoreException e) {
            LOGGER.error(e.getMessage(), e);
            throw new HurricanServiceCommandException(e);
        }
        catch (FindException e) {
            LOGGER.error(e.getMessage(), e);
            throw new HurricanServiceCommandException(e);
        }
    }

    /*
     * techn. Leistungen evtl. aktivieren (gekündigte Tech-Leistungen wieder aktivieren; nur die TechLeistungen mit dem
     * letzten Kündigungsdatum) - alle TechLs zum Auftrag ermitteln - alle TechLs mit letztem Kündigungsdatum ermitteln
     * und diese wieder aktivieren
     *
     * Die Methode ermittelt alle Leistungen mit dem letzten Kündigungsdatum und aktiviert diese Leistungen wieder
     */
    private void activateTechService() throws HurricanServiceCommandException {
        try {
            List<Auftrag2TechLeistung> a2tls = ccLeistungsService.findAuftrag2TechLeistungen(revokeTermination.getAuftragId(), null, false);
            if (CollectionTools.isNotEmpty(a2tls)) {
                Collection<Auftrag2TechLeistung> serviceToSetCollection = getLatestServices(a2tls);

                if (CollectionTools.isNotEmpty(serviceToSetCollection)) {
                    for (Auftrag2TechLeistung auftrag2TechLeistung : serviceToSetCollection) {
                        auftrag2TechLeistung.setAktivBis(null);
                        ccLeistungsService.saveAuftrag2TechLeistung(auftrag2TechLeistung);
                    }
                }
            }
        }
        catch (StoreException e) {
            LOGGER.error(e.getMessage(), e);
            throw new HurricanServiceCommandException(e);
        }
        catch (FindException e) {
            LOGGER.error(e.getMessage(), e);
            throw new HurricanServiceCommandException(e);
        }
    }

    /*
     * Account entsperren (falls gesperrt)
     */
    private void unlockAccount() throws HurricanServiceCommandException {
        try {
            List<IntAccount> accountList = accountService.findIntAccounts4Auftrag(revokeTermination.getAuftragId());
            if (CollectionTools.isNotEmpty(accountList)) {
                for (IntAccount intAccount : accountList) {
                    intAccount.setGesperrt(Boolean.FALSE);
                    accountService.saveIntAccount(intAccount, Boolean.FALSE);
                }
            }
        }
        catch (FindException e) {
            LOGGER.error(e.getMessage(), e);
            throw new HurricanServiceCommandException(e);
        }
        catch (StoreException e) {
            LOGGER.error(e.getMessage(), e);
            throw new HurricanServiceCommandException(e);
        }
    }


    private Collection<Auftrag2TechLeistung> getLatestServices(List<Auftrag2TechLeistung> techServiceList) throws HurricanServiceCommandException {

        SortedMap<Long, Collection<Auftrag2TechLeistung>> treeMap = new TreeMap<>(Collections.reverseOrder());

        if (techServiceList == null) {
            throw new HurricanServiceCommandException("Liste mit technischen Leistungen nicht definiert!");
        }

        for (Auftrag2TechLeistung techLeistung : techServiceList) {
            Date aktivBis = techLeistung.getAktivBis();

            if (aktivBis == null) {
                aktivBis = DateTools.getHurricanEndDate();
            }

            if ((aktivBis != null) && (aktivBis.getTime() <= DateTools.getHurricanEndDate().getTime())) {
                long aktivBisMs = aktivBis.getTime();
                Collection<Auftrag2TechLeistung> list = treeMap.get(aktivBisMs) == null ?
                        new ArrayList<>() : treeMap.get(aktivBisMs);
                list.add(techLeistung);

                if (!treeMap.containsValue(list)) {
                    treeMap.put(aktivBisMs, list);
                }
            }
        }

        if (!treeMap.isEmpty()) {
            Long firstKey = treeMap.firstKey();

            if (firstKey != null) {
                return treeMap.get(firstKey);
            }
        }
        return null;
    }


    /**
     * Injected
     */
    public void setProduktService(ProduktService produktService) {
        this.produktService = produktService;
    }

    /**
     * Injected
     */
    public void setRangierungsService(RangierungsService rangierungsService) {
        this.rangierungsService = rangierungsService;
    }

    /**
     * Injected
     */
    public void setCcLeistungsService(CCLeistungsService ccLeistungsService) {
        this.ccLeistungsService = ccLeistungsService;
    }

    /**
     * Injected
     */
    public void setEndstellenService(EndstellenService endstellenService) {
        this.endstellenService = endstellenService;
    }

    /**
     * Injected
     */
    public void setAccountService(AccountService accountService) {
        this.accountService = accountService;
    }

}
