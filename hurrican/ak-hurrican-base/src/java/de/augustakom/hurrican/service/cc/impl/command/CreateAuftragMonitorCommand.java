/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 09.08.2004 10:24:47
 */
package de.augustakom.hurrican.service.cc.impl.command;

import java.util.*;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import de.augustakom.common.service.exceptions.ServiceCommandException;
import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.common.tools.lang.MapTools;
import de.augustakom.common.tools.lang.NumberTools;
import de.augustakom.common.tools.lang.Pair;
import de.augustakom.hurrican.annotation.CcTxRequired;
import de.augustakom.hurrican.dao.cc.CCAuftragViewDAO;
import de.augustakom.hurrican.model.billing.view.BAuftragLeistungView;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.AuftragStatus;
import de.augustakom.hurrican.model.cc.Produkt;
import de.augustakom.hurrican.model.cc.temp.AuftragsMonitor;
import de.augustakom.hurrican.model.cc.view.LeistungsDiffView;
import de.augustakom.hurrican.model.shared.view.Billing2HurricanProdMapping;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.HurricanServiceCommandException;
import de.augustakom.hurrican.service.billing.BillingAuftragService;
import de.augustakom.hurrican.service.cc.CCLeistungsService;
import de.augustakom.hurrican.service.cc.ProduktService;

/**
 * ServiceCommand, um einen AuftragsMonitor zu erstellen.
 *
 *
 */
@CcTxRequired
public class CreateAuftragMonitorCommand extends AbstractServiceCommand {

    private static final Logger LOGGER = Logger.getLogger(CreateAuftragMonitorCommand.class);

    /**
     * Key fuer die prepare-Methode, um eine Instanz von BillingAuftragService zu uebergeben.
     */
    public static final String BILLING_AUFTRAG_SERVICE = "billing.auftrag.service";
    /**
     * Key fuer die prepare-Methode, um die Kundennummer zu uebergeben.
     */
    public static final String KUNDE_NO = "kunde.no";
    /**
     * Key fuer die prepare-Methode, um eine Taifun-Auftragsnummer zu uebergeben.
     */
    public static final String TAIFUN_ORDER__NO = "taifun.order.no";
    /**
     * Key fuer die prepare-Methode, um das DAO-Objekt vom Typ CCAuftragViewDAO zu uebergeben.
     */
    public static final String AUFTRAG_VIEW_DAO = "auftrag.view.dao";

    private Long kundeNoOrig = null;
    private Long taifunOrderNoOrig = null;
    private List<AuftragsMonitor> resultAuftragMonitor = null;
    private List<BAuftragLeistungView> billingAuftraegeTmp = null;

    @Override
    public Object execute() throws ServiceCommandException {
        try {
            checkValues();

            // aktive CC-Auftraege fuer Kunde suchen
            CCAuftragViewDAO ccDao = (CCAuftragViewDAO) getPreparedValue(AUFTRAG_VIEW_DAO);
            List<Map<String, Object>> ccAuftraege = ccDao.findActiveAuftraege4AM(kundeNoOrig, taifunOrderNoOrig);
            calculateCount(ccAuftraege);

            // aktive Billing-Auftraege fuer Kunde suchen
            billingAuftraegeTmp = ((BillingAuftragService) getPreparedValue(BILLING_AUFTRAG_SERVICE))
                    .findActiveAuftraege4AM(kundeNoOrig, taifunOrderNoOrig);

            ProduktService ps = getCCService(ProduktService.class);
            List<Billing2HurricanProdMapping> billingAuftraege = ps.doProductMapping(billingAuftraegeTmp);

            // Unterschiede zwischen CC- und Billing-Auftraege ermitteln.
            resultAuftragMonitor = new ArrayList<AuftragsMonitor>();
            compareCC2Billing(ccAuftraege, billingAuftraege);
            compareBilling2CC(billingAuftraege, ccAuftraege);

            return resultAuftragMonitor;
        }
        catch (ServiceCommandException | RuntimeException e) {
            throw e;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new HurricanServiceCommandException(e);
        }
    }

    /*
     * Die Gruppierung der Hurrican-Auftraege funktioniert mit Oracle nicht mehr identisch
     * zu MySQL. Da ueber SQL bei Oracle keine entsprechende Abfrage mehr erstellt
     * werden konnte, wird die Anzahl der Hurrican-Auftraege zu einem Taifun-Auftrag
     * ueber diese Funktion ermittelt und gesetzt.
     * (Evtl. kann man die SQL-Abfrage nochmal ueberpruefen und vielleicht mit Sub-Selects
     * oder aehnlichem arbeiten.)
     * @param ccAuftraege
     */
    private void calculateCount(List<Map<String, Object>> ccAuftraege) {
        Map<String, Integer> counts = new HashMap<>();
        for (Map<String, Object> m : ccAuftraege) {
            String key = m.get(CCAuftragViewDAO.AM_KEY_PRODAK_ORDER__NO) + "_" + m.get(CCAuftragViewDAO.AM_KEY_PROD_ID);
            Integer orderCount = ((Number) m.get(CCAuftragViewDAO.AM_KEY_ANZAHL)).intValue();

            if (counts.containsKey(key)) {
                Integer actCount = counts.get(key);
                counts.put(key, NumberTools.add(actCount, orderCount));
            }
            else {
                counts.put(key, orderCount);
            }
        }

        for (Map<String, Object> m : ccAuftraege) {
            String key = m.get(CCAuftragViewDAO.AM_KEY_PRODAK_ORDER__NO) + "_" + m.get(CCAuftragViewDAO.AM_KEY_PROD_ID);
            Integer sumCount = counts.get(key);
            m.put(CCAuftragViewDAO.AM_KEY_ANZAHL, sumCount);
        }
    }

    /**
     * Sucht nach folgenden Differenzen und fuegt diese dem AuftragsMonitor hinzu. <ul> <li>Auftrag befindet sich zwar
     * in CC aber nicht im Billing-System --> Auftrag muss in CC gekuendigt werden. <li>Auftrag befindet sich in CC und
     * im Billing-System, jedoch ist die Anzahl in CC hoeher --> Auftraege muessen in CC gekuendigt werden. <li>Auftrag
     * befindet sich in CC und im Billing-System, jedoch weichen bestimmte Leistungen (z.B. Bandbreite) voneinander ab.
     * </ul>
     *
     * @param cc      Liste der CC-Auftraege
     * @param billing Liste der Billing-Auftraege
     */
    private void compareCC2Billing(List<Map<String, Object>> cc, List<Billing2HurricanProdMapping> billing) throws FindException {
        if (cc != null) {
            for (Map<String, Object> ccMap : cc) {
                Long auftragNoOrig = MapTools.getLong(ccMap, CCAuftragViewDAO.AM_KEY_PRODAK_ORDER__NO);
                Long prodId = MapTools.getLong(ccMap, CCAuftragViewDAO.AM_KEY_PROD_ID);
                int ccAnzahl = (ccMap.get(CCAuftragViewDAO.AM_KEY_ANZAHL) instanceof Number)
                        ? ((Number) ccMap.get(CCAuftragViewDAO.AM_KEY_ANZAHL)).intValue() : 0;

                if ((auftragNoOrig != null) && (prodId != null)) {
                    Billing2HurricanProdMapping bView = findBillingAuftrag(billing, auftragNoOrig, prodId);

                    AuftragsMonitor am = new AuftragsMonitor(kundeNoOrig);
                    am.setOeName((bView != null) ? bView.getOeName() : null);
                    am.setAuftragId(MapTools.getLong(ccMap, CCAuftragViewDAO.AM_KEY_CC_AUFTRAG_ID));
                    am.setAuftragStatusId(MapTools.getLong(ccMap, CCAuftragViewDAO.AM_KEY_CC_AUFTRAG_STATUS_ID));
                    am.setCcProduktId(prodId);
                    am.setCcProdukt(MapTools.getString(ccMap, CCAuftragViewDAO.AM_KEY_ANSCHLUSSART));
                    am.setBundleOrderNo(MapTools.getInteger(ccMap, CCAuftragViewDAO.AM_KEY_BUENDEL_NR));
                    am.setBundleNoHerkunft(MapTools.getString(ccMap, CCAuftragViewDAO.AM_KEY_BUENDEL_NR_HERKUNFT));
                    am.setAuftragNoOrig(auftragNoOrig);
                    am.setAnzahlCC(ccAnzahl);

                    if (bView == null) {
                        // Auftrag in CC, aber nicht in Billing --> in CC kuendigen
                        am.setAmAktion(AuftragsMonitor.AM_AKTION_KUENDIGEN);
                        am.setDifferenz(ccAnzahl);
                        am.setAmText("Nicht in Taifun. In Hurrican kündigen.");

                        resultAuftragMonitor.add(am);
                    }
                    else {
                        int billingAnzahl = getLeistungsCount(billing, prodId, auftragNoOrig);
                        if (billingAnzahl < ccAnzahl) {
                            // Auftrag in CC und in Billing (hoehere Anzahl in CC) --> in CC kuendigen
                            am.setAmAktion(AuftragsMonitor.AM_AKTION_KUENDIGEN);
                            am.setDifferenz(ccAnzahl - billingAnzahl);
                            am.setAmText("Fehlt in Taifun " + (ccAnzahl - billingAnzahl) + "x. In Hurrican kündigen.");

                            resultAuftragMonitor.add(am);
                        }
                        else {
                            // Auftrag in CC und in Billing und Anzahl O.K.
                            //   --> pruefen, ob Leistungsdifferenzen vorhanden sind.
                            List<LeistungsDiffView> diffs = findLeistungsDiff(am);
                            if (CollectionTools.isNotEmpty(diffs)) {
                                am.setAmAktion(AuftragsMonitor.AM_AKTION_LEISTUNGS_DIFF);
                                am.setLeistungsDiffs(diffs);
                                am.setDifferenz(0);
                                am.setAmText("Leistungsdifferenz Taifun <--> Hurrican");

                                resultAuftragMonitor.add(am);
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Sucht nach einem Billing-Auftrag mit der (original) Auftrags-No <code>auftragNoOrig</code> und der externen
     * Produkt-ID <code>extProduktNo</code>.
     */
    private Billing2HurricanProdMapping findBillingAuftrag(
            List<Billing2HurricanProdMapping> billing, Long auftragNoOrig, Long prodId) {
        for (Billing2HurricanProdMapping view : billing) {
            if (auftragNoOrig.equals(view.getAuftragNoOrig()) && prodId.equals(view.getProdId())) {
                return view;
            }
        }

        return null;
    }

    /**
     * Sucht nach folgenden Differenzen und fuegt diese dem AuftragsMonitor hinzu. <ul> <li>Auftrag befindet sich zwar
     * im Billing-System aber nicht in CC --> Auftrag muss in CC angelegt werden. <li>Auftrag befindet sich im
     * Billing-System und in CC, jedoch ist die Anzahl im Billing-System hoeher --> Auftraege muessen in CC angelegt
     * werden. </ul>
     *
     * @param billing Liste der Billing-Auftraege
     * @param cc      Liste der CC-Auftraege
     */
    public void compareBilling2CC(List<Billing2HurricanProdMapping> billing, List<Map<String, Object>> cc) {
        if (billing != null) {
            // Set, um zu speichern, welches Produkt fuer welchen Auftrag bereits geprueft wurde
            //  - Elemente: Pair mit ProduktNo und AuftragNoOrig
            Set<Pair<Long, Long>> productsChecked = new HashSet<>();
            for (Billing2HurricanProdMapping bView : billing) {
                if ((bView.getAuftragNoOrig() != null) && (bView.getProdId() != null)) {

                    Pair<Long, Long> prodAuftrag = Pair.create(bView.getProdId(), bView.getAuftragNoOrig());

                    if (!productsChecked.contains(prodAuftrag)) {
                        productsChecked.add(prodAuftrag);
                        int billingAnzahl = getLeistungsCount(billing, bView.getProdId(), bView.getAuftragNoOrig());
                        Produkt produkt = findCCProdukt(bView.getProdId());
                        String ccProdukt = (produkt != null) ? produkt.getAnschlussart() : null;
                        Long prodId = (produkt != null) ? produkt.getId() : null;

                        Map<String, Object> ccMap = findCCAuftrag(cc, bView.getAuftragNoOrig(), bView.getProdId());
                        if ((ccMap == null) && (billingAnzahl == 1)) {
                            // Auftrag in Billing, aber nicht in CC --> in CC anlegen
                            AuftragsMonitor am = new AuftragsMonitor(kundeNoOrig);
                            am.setAmAktion(AuftragsMonitor.AM_AKTION_ANLEGEN);
                            am.setCcProduktId(prodId);
                            am.setCcProdukt(ccProdukt);
                            am.setOeName(bView.getOeName());
                            am.setAuftragNoOrig(bView.getAuftragNoOrig());
                            am.setOldAuftragNoOrig(getOldAuftragNoOrig(bView.getAuftragNoOrig()));
                            am.setBundleOrderNo(bView.getBundleOrderNo());
                            am.setBundleNoHerkunft(AuftragDaten.BUENDEL_HERKUNFT_BILLING_PRODUKT);
                            am.setAnzahlCC(billingAnzahl);
                            am.setDifferenz(billingAnzahl);
                            am.setAmText("Nicht in Hurrican. In Hurrican anlegen.");

                            resultAuftragMonitor.add(am);
                        }
                        else {
                            int ccAnzahl = ((ccMap != null) && (ccMap.get(CCAuftragViewDAO.AM_KEY_ANZAHL) instanceof Number))
                                    ? ((Number) ccMap.get(CCAuftragViewDAO.AM_KEY_ANZAHL)).intValue() : 0;
                            Short aktionsId = MapTools.getShort(ccMap, CCAuftragViewDAO.AM_KEY_AKTIONS_ID);

                            if (((billingAnzahl > ccAnzahl)
                                    && !(NumberTools.equal(aktionsId, Produkt.AKTIONS_ID_COUNT_IRRELEVANT)
                                    && (ccAnzahl > 0)))) {
                                // Auftrag in Billing und in CC (hoehere Anzahl
                                // in Billing) --> in CC anlegen
                                AuftragsMonitor am = new AuftragsMonitor(kundeNoOrig);
                                am.setAmAktion(AuftragsMonitor.AM_AKTION_ANLEGEN);
                                am.setCcProdukt(ccProdukt);
                                am.setCcProduktId(prodId);
                                am.setOeName(bView.getOeName());
                                am.setAuftragNoOrig(bView.getAuftragNoOrig());
                                am
                                        .setOldAuftragNoOrig(getOldAuftragNoOrig(bView
                                                .getAuftragNoOrig()));
                                am.setBundleOrderNo(bView.getBundleOrderNo());
                                am.setAnzahlCC(billingAnzahl);
                                am.setDifferenz(billingAnzahl - ccAnzahl);
                                am.setAmText("Fehlt in Hurrican " + (billingAnzahl - ccAnzahl) + "x mal. In Hurrican anlegen.");

                                resultAuftragMonitor.add(am);
                            }
                        }
                    }
                }
            }
        }
    }

    /*
     * Ermittelt die Anzahl der in <code>billingViews</code> Leistungen zur Produkt-No. <br>
     * Dies ist die Summe aller Quantities von Leistungen mit der angegebenen ext. Produkt-No.
     */
    private int getLeistungsCount(List<Billing2HurricanProdMapping> billingViews, Long prodId, Long auftragNoOrig) {
        int count = 0;
        for (Billing2HurricanProdMapping view : billingViews) {
            if (NumberTools.equal(view.getAuftragNoOrig(), auftragNoOrig) &&
                    NumberTools.equal(view.getProdId(), prodId)) {
                count++;
            }
        }
        return count;
    }

    /*
     * Sucht nach einem CC-Auftrag mit der (original) Auftrags-No
     * <code>auftragNoOrig</code> und der Produkt-ID <code>prodId</code>.
     */
    private Map<String, Object> findCCAuftrag(List<Map<String, Object>> cc, Long auftragNoOrig, Long prodId) {
        for (Map<String, Object> ccMap : cc) {
            Long ccANo = MapTools.getLong(ccMap, CCAuftragViewDAO.AM_KEY_PRODAK_ORDER__NO);
            Long ccPID = MapTools.getLong(ccMap, CCAuftragViewDAO.AM_KEY_PROD_ID);

            if (auftragNoOrig.equals(ccANo) && prodId.equals(ccPID)) {
                return ccMap;
            }
        }
        return null;
    }

    /*
     * Ermittelt die Leistungsdifferenz zwischen dem Billing- und CC-System
     * fuer einen bestimmten Auftrag.
     */
    private List<LeistungsDiffView> findLeistungsDiff(AuftragsMonitor am) throws FindException {
        try {
            // Leistungsdifferenzen werden nur geprueft, wenn Status < Kuendigung (9000)
            if (NumberTools.isLess(am.getAuftragStatusId(), AuftragStatus.KUENDIGUNG)) {
                CCLeistungsService ls = getCCService(CCLeistungsService.class);
                return ls.findLeistungsDiffs(am.getAuftragId(), am.getAuftragNoOrig(), am.getCcProduktId());
            }
            return null;
        }
        catch (FindException e) {
            throw e;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException("Fehler bei der Ermittlung der Leistungsdifferenzen zum Auftrag " +
                    am.getAuftragId() + ": " + e.getMessage(), e);
        }
    }

    /*
     * Ermittelt den Hurrican-Produktnamen zu der angegebenen Produkt-Id.
     * @param prodId
     * @return
     *
     */
    private Produkt findCCProdukt(Long prodId) {
        try {
            ProduktService ps = getCCService(ProduktService.class);
            return ps.findProdukt(prodId);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return null;
    }

    /* Ueberprueft, ob die Command-Klasse richtig konfiguriert wurde. */
    private void checkValues() throws ServiceCommandException, FindException {
        kundeNoOrig = getPreparedValue(KUNDE_NO, Long.class, false, "Kundennummer aus dem Billing-System wurde nicht angegeben!");
        taifunOrderNoOrig = getPreparedValue(TAIFUN_ORDER__NO, Long.class, true, null);

        if (!(getPreparedValue(BILLING_AUFTRAG_SERVICE) instanceof BillingAuftragService)) {
            throw new HurricanServiceCommandException("Service-Objekt fuer Billing-Auftraege wurde nicht an Command-Objekt übergeben!");
        }
    }

    /**
     * Ermittelt die alte Billing-Auftragsnummer zur angegebenen Auftragsnummer.
     *
     * @param auftragNoOrig
     * @return die alte Billing-Auftragsnummer oder <code>null</code>, falls identisch
     *
     */
    private String getOldAuftragNoOrig(Long auftragNoOrig) {
        if (billingAuftraegeTmp != null) {
            for (BAuftragLeistungView view : billingAuftraegeTmp) {
                if (NumberTools.equal(view.getAuftragNoOrig(), auftragNoOrig)
                        && !StringUtils.equals(view.getOldAuftragNoOrig(), view.getAuftragNoOrig().toString())) {
                    return view.getOldAuftragNoOrig();
                }
            }
        }
        return null;
    }
}


