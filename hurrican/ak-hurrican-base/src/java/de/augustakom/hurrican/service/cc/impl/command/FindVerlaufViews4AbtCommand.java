/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 10.01.2005 16:25:52
 */
package de.augustakom.hurrican.service.cc.impl.command;

import java.io.*;
import java.util.*;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;

import de.augustakom.common.tools.lang.BooleanTools;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.common.tools.lang.NumberTools;
import de.augustakom.common.tools.lang.Pair;
import de.augustakom.hurrican.annotation.CcTxRequired;
import de.augustakom.hurrican.dao.cc.VerlaufViewDAO;
import de.augustakom.hurrican.model.billing.view.TimeSlotView;
import de.augustakom.hurrican.model.cc.Abteilung;
import de.augustakom.hurrican.model.cc.view.AbstractBauauftragView;
import de.augustakom.hurrican.model.cc.view.AbstractVerlaufView;
import de.augustakom.hurrican.model.cc.view.ProjektierungsView;
import de.augustakom.hurrican.model.cc.view.TimeSlotAware;
import de.augustakom.hurrican.model.cc.view.VerlaufAmRlView;
import de.augustakom.hurrican.model.cc.view.VerlaufEXTView;
import de.augustakom.hurrican.model.cc.view.VerlaufFieldServiceView;
import de.augustakom.hurrican.model.cc.view.VerlaufStConnectView;
import de.augustakom.hurrican.model.cc.view.VerlaufStOnlineView;
import de.augustakom.hurrican.model.cc.view.VerlaufStVoiceView;
import de.augustakom.hurrican.model.cc.view.VerlaufUniversalView;
import de.augustakom.hurrican.model.shared.iface.RNModel;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.billing.BillingAuftragService;
import de.augustakom.hurrican.service.billing.KundenService;
import de.augustakom.hurrican.service.billing.OEService;
import de.augustakom.hurrican.service.billing.RufnummerService;

/**
 * Command-Klasse zur Ermittlung der el. Verlaeufe bzw. Bauauftraege fuer eine best. Abteilung. Wenn {@link
 * FindVerlaufViews4AbtCommand#REALISIERUNG_FROM} und/oder {@link FindVerlaufViews4AbtCommand#REALISIERUNG_TO} gesetzt
 * sind/ist, wird nach allen Bauaufträgen gesucht, die einen entsprechenden Realisierungstermin aufweisen (die Daten
 * selber werden eingeschlossen, also <= und >=). Ansonsten wird eine Liste mit allen offenen Bauaufträgen der Abteilung
 * zurückgegeben. Die {@link FindVerlaufViews4AbtCommand#execute()} gibt je nach übergebener Abteilung eine Subklasse
 * von {@link AbstractVerlaufView} zurück. <p><b>Achtung: </b>Für Projektierungen werden evtl. übergebenen
 * Realisierungstermine (von, bis) nicht beachtet</p>
 *
 *
 */
public class FindVerlaufViews4AbtCommand extends AbstractServiceCommand {

    private static final Logger LOGGER = Logger.getLogger(FindVerlaufViews4AbtCommand.class);

    /**
     * Key fuer die prepare-Methode, um dem Command die Abteilungs-ID mitzuteilen.
     */
    public static final String ABTEILUNG_ID = "abteilung.id";
    /**
     * Key fuer die prepare-Methode, um zu definieren, ob ein abteilungs-spezifisches oder allgemeines Query
     * fuer die Datenermittlung verwendet werden soll.
     */
    public static final String USE_UNIVERSAL_QUERY = "use.universal.query";
    /**
     * Key fuer die prepare-Methode, um dem Command mitzuteilen, ob Ruecklaeufer gesucht werden.
     */
    public static final String SEARCH_4_RUECKLAEUFER = "search.4.ruecklaeufer";
    /**
     * Key fuer die prepare-Methode, um dem Command mitzuteilen, ob nach Projektierungen gesucht werden soll.
     */
    public static final String SEARCH_4_PROJEKTIERUNG = "search.4.projektierung";

    /**
     * Key fuer die prepare-Methode, um dem Command mitzuteilen, ab welchem Realisierungtermin gesucht werden soll.
     */
    public static final String REALISIERUNG_FROM = "realisierung.from";
    /**
     * Key fuer die prepare-Methode, um dem Command mitzuteilen, bis zu welchem Realisierungtermin gesucht werden soll.
     */
    public static final String REALISIERUNG_TO = "realisierung.to";

    // Parameter
    private Long abteilungId = null;
    private Boolean search4RL = null;
    private Boolean search4Proj = null;
    private Date realisierungFrom = null;
    private Date realisierungTo = null;
    private boolean useUniversalQuery = false;

    // DAOs
    private VerlaufViewDAO verlaufViewDAO = null;

    /**
     * @see de.augustakom.common.service.iface.IServiceCommand#execute()
     */
    @Override
    @CcTxRequired
    public Object execute() throws Exception {
        checkValues();
        if (Boolean.TRUE.equals(search4Proj)) {
            return findProjektierungen4Abt();
        }
        return findVerlauf4Abt();
    }

    /**
     * Sucht nach den Projektierungen fuer eine best. Abteilung.
     */
    private List<? extends AbstractVerlaufView> findProjektierungen4Abt() {
        boolean ruecklaeufer = Boolean.TRUE.equals(search4RL);
        List<ProjektierungsView> result = getVerlaufViewDAO().findProjektierungen(abteilungId, ruecklaeufer);
        if (CollectionUtils.isNotEmpty(result)) {
            loadOeNamen4Verlaeufe(result);
            loadHauptRN4Verlaeufe(result);
            loadKundenNamen4Verlaeufe(result);
        }
        return result;
    }

    /**
     * Sucht nach den Verlaeufen fuer die jeweiligen Abteilungen.
     */
    private List<? extends AbstractBauauftragView> findVerlauf4Abt() throws FindException {
        if (useUniversalQuery) {
            List<VerlaufUniversalView> result =
                getVerlaufViewDAO().findBasWithUniversalQuery(abteilungId, realisierungFrom, realisierungTo);
            result = filterMultiples(result);
            if (CollectionUtils.isNotEmpty(result)) {
                loadOeNamen4Verlaeufe(result);
                loadKundenNamen4Verlaeufe(result);
                loadTimeSlotViews4Verlaeufe(result);
            }
            return result;
        }
        else {
            if (NumberTools.equal(abteilungId, Abteilung.ST_CONNECT)) {
                List<VerlaufStConnectView> result = getVerlaufViewDAO().find4STConnect(realisierungFrom, realisierungTo);
                result = filterMultiples(result);
                if (CollectionUtils.isNotEmpty(result)) {
                    loadOeNamen4Verlaeufe(result);
                    loadKundenNamen4Verlaeufe(result);
                }
                return result;
            }
            else if (Abteilung.isDispoOrNP(abteilungId)) {
                List<? extends AbstractBauauftragView> result;
                if (BooleanTools.nullToFalse(search4RL)) {
                    result = getVerlaufViewDAO().findRL4DispoOrNP(abteilungId, realisierungFrom, realisierungTo);
                }
                else {
                    result = getVerlaufViewDAO().find4DispoOrNP(abteilungId, realisierungFrom, realisierungTo);
                }
                result = filterMultiples(result);

                if (CollectionUtils.isNotEmpty(result)) {
                    loadKundenNamen4Verlaeufe(result);
                    loadOeNamen4Verlaeufe(result);
                    // enabled also for RL
                    loadTimeSlotViews4Verlaeufe(result);
                }
                return result;
            }
            else if (NumberTools.equal(abteilungId, Abteilung.ST_VOICE)) {
                List<VerlaufStVoiceView> result = getVerlaufViewDAO().find4STVoice(realisierungFrom, realisierungTo);
                result = filterMultiples(result);
                if (CollectionUtils.isNotEmpty(result)) {
                    loadKundenNamen4Verlaeufe(result);
                    loadOeNamen4Verlaeufe(result);
                    loadHauptRN4Verlaeufe(result);
                }
                return result;
            }
            else if (NumberTools.equal(abteilungId, Abteilung.FIELD_SERVICE)) {
                List<VerlaufFieldServiceView> result = getVerlaufViewDAO().find4FieldService(realisierungFrom, realisierungTo);
                result = filterMultiples(result);
                if (CollectionUtils.isNotEmpty(result)) {
                    loadKundenNamen4Verlaeufe(result);
                    loadOeNamen4Verlaeufe(result);
                }
                return result;
            }
            else if (NumberTools.equal(abteilungId, Abteilung.AM)) {
                if (BooleanTools.nullToFalse(search4RL)) {
                    List<VerlaufAmRlView> result = getVerlaufViewDAO().findRL4Am(realisierungFrom, realisierungTo);
                    result = filterMultiples(result);
                    if (CollectionUtils.isNotEmpty(result)) {
                        loadKundenNamen4Verlaeufe(result);
                    }
                    return result;
                }
                return null;
            }
            else if (NumberTools.isIn(abteilungId, new Long[] { Abteilung.ST_ONLINE, Abteilung.MQUEUE })) {
                List<VerlaufStOnlineView> result = getVerlaufViewDAO().find4STOnline(abteilungId, realisierungFrom, realisierungTo);
                result = filterMultiples(result);
                if (CollectionUtils.isNotEmpty(result)) {
                    loadKundenNamen4Verlaeufe(result);
                    //                loadOeNamen4Verlaeufe(result);  // OE-Name nicht laden, da nicht in Tabelle dargestellt!
                }
                return result;
            }
            else if (NumberTools.isIn(abteilungId, new Long[] { Abteilung.EXTERN, Abteilung.FFM })) {
                List<VerlaufEXTView> result = getVerlaufViewDAO().find4EXTERN(abteilungId, realisierungFrom, realisierungTo);
                result = filterMultiples(result);
                if (CollectionUtils.isNotEmpty(result)) {
                    loadKundenNamen4Verlaeufe(result);
                    loadOeNamen4Verlaeufe(result);
                    loadTimeSlotViews4Verlaeufe(result);
                }
                return result;
            }
            else {
                throw new FindException("Die Abteilungs-ID (" + abteilungId + ") wird (noch) nicht unterstützt! " +
                        "Bauaufträge konnten nicht ermittelt werden.");
            }
        }
    }

    /**
     * Laedt zu jedem Verlauf den Oe-Namen (Produktname aus Mistral).
     */
    private <T extends AbstractVerlaufView> void loadOeNamen4Verlaeufe(List<T> verlaufViews) {
        Set<Long> auftragNoOrigs = Sets.newHashSetWithExpectedSize(verlaufViews.size());
        for (AbstractVerlaufView view : verlaufViews) {
            if (view.getAuftragNoOrig() != null) {
                auftragNoOrigs.add(view.getAuftragNoOrig());
            }
        }

        if (!auftragNoOrigs.isEmpty()) {
            try {
                OEService oes = getBillingService(OEService.class);
                Map<Long, String> auftrag2ProdName = oes.findProduktNamen4Auftraege(Lists.newArrayList(auftragNoOrigs));
                if (auftrag2ProdName != null) {
                    for (AbstractVerlaufView view : verlaufViews) {
                        if (view.getAuftragNoOrig() != null) {
                            view.setOeName(auftrag2ProdName.get(view.getAuftragNoOrig()));
                        }
                    }
                }
            }
            catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
    }

    /**
     * Laedt Timeslots aus Billing.<p/>
     * <b>Achtung:</b> Es muss auch ein moeglicher Timeslot aus der Carrierbestellung geladen werden,
     * siehe VerlaufViewDAOImpl#SUB_SELECT_LAST_CB_TAL_TIMESLOT
     *
     * @param verlaufViews
     * @param <T>
     */
    private <T extends AbstractBauauftragView> void loadTimeSlotViews4Verlaeufe(List<T> verlaufViews) {
        Set<Long> auftragNoOrigs = Sets.newHashSetWithExpectedSize(verlaufViews.size());
        for (AbstractBauauftragView view : verlaufViews) {
            if (view.getAuftragNoOrig() != null && view instanceof TimeSlotAware) {
                auftragNoOrigs.add(view.getAuftragNoOrig());
            }
        }

        if (!auftragNoOrigs.isEmpty()) {
            try {
                BillingAuftragService bas = getBillingService(BillingAuftragService.class);
                Map<Long, TimeSlotView> timeSlotViewMap = bas.findTimeSlotViews4Auftrag(Lists.newArrayList(auftragNoOrigs));
                if (timeSlotViewMap != null) {
                    for (AbstractBauauftragView view : verlaufViews) {
                        if (view.getAuftragNoOrig() != null) {
                            TimeSlotView timeSlot = timeSlotViewMap.get(view.getAuftragNoOrig());
                            ((TimeSlotAware) view).getTimeSlot().setTimeSlotView(timeSlot);
                        }
                    }
                }
            }
            catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
    }

    /**
     * Laedt zu jedem Verlauf den Kunden-Namen.
     */
    private <T extends AbstractVerlaufView> void loadKundenNamen4Verlaeufe(List<T> verlaufViews) {
        Set<Long> kundeNoOrigs = Sets.newHashSetWithExpectedSize(verlaufViews.size());
        for (AbstractVerlaufView view : verlaufViews) {
            if (view.getKundeNo() != null) {
                kundeNoOrigs.add(view.getKundeNo());
            }
        }

        if (!kundeNoOrigs.isEmpty()) {
            try {
                KundenService ks = getBillingService(KundenService.class);
                Map<Long, String> kNo2Name = ks.findKundenNamen(Lists.newArrayList(kundeNoOrigs));
                if (kNo2Name != null) {
                    for (AbstractVerlaufView view : verlaufViews) {
                        view.setKundenName(kNo2Name.get(view.getKundeNo()));
                    }
                }
            }
            catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
    }

    /**
     * Ermittelt die Haupt-Rufnummern zu den EWSD-Verlaeufen.
     */
    private <T extends AbstractVerlaufView> void loadHauptRN4Verlaeufe(List<T> verlaufViews) {
        Set<Long> auftragNoOrigs = Sets.newHashSetWithExpectedSize(verlaufViews.size());
        for (AbstractVerlaufView view : verlaufViews) {
            if (view.getAuftragNoOrig() != null) {
                auftragNoOrigs.add(view.getAuftragNoOrig());
            }
        }

        if (!auftragNoOrigs.isEmpty()) {
            try {
                RufnummerService rs = getBillingService(RufnummerService.class);
                Map<Long, String> auftrag2RN = rs.findHauptRNs(Lists.newArrayList(auftragNoOrigs));
                if (auftrag2RN != null) {
                    for (T tmp : verlaufViews) {
                        if (tmp instanceof RNModel) {
                            ((RNModel) tmp).setHauptRN(auftrag2RN.get(tmp.getAuftragNoOrig()));
                        }
                    }
                }
            }
            catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
    }

    /**
     * Ueberprueft, ob dem Command alle benoetigten Parameter uebergeben wurden.
     */
    private void checkValues() throws FindException {
        Object abtId = getPreparedValue(ABTEILUNG_ID);
        abteilungId = (abtId instanceof Long) ? (Long) abtId : null;
        if (abteilungId == null) {
            throw new FindException("Es wurde keine Abteilung angegeben, für die der el. Verlauf ermittelt werden soll.");
        }

        Object universalQuery = getPreparedValue(USE_UNIVERSAL_QUERY);
        useUniversalQuery = (universalQuery instanceof Boolean) ? BooleanTools.nullToFalse((Boolean) universalQuery) : false;

        Object ruecklaeufer = getPreparedValue(SEARCH_4_RUECKLAEUFER);
        search4RL = (ruecklaeufer instanceof Boolean) ? (Boolean) ruecklaeufer : null;
        if (ruecklaeufer == null) {
            throw new FindException("Es ist nicht definiert, ob nach Rückläufern oder <normalen> Bauaufträgen gesucht werden soll.");
        }

        Object proj = getPreparedValue(SEARCH_4_PROJEKTIERUNG);
        search4Proj = (proj instanceof Boolean) ? (Boolean) proj : null;

        if (!Boolean.TRUE.equals(search4Proj)) {
            Object realFrom = getPreparedValue(REALISIERUNG_FROM);
            realisierungFrom = (realFrom instanceof Date) ? (Date) realFrom : null;

            Object realTo = getPreparedValue(REALISIERUNG_TO);
            realisierungTo = (realTo instanceof Date) ? (Date) realTo : null;

            if (DateTools.isAfter(realisierungFrom, realisierungTo)) {
                throw new FindException("Der Filter über den Realisierungstermin ist ungültig. Datum \"von\" muss vor \"bis\" sein.");
            }
        }
    }

    /**
     * Filtert aus der Liste alle mehrfach angezeigten Bauauftraege heraus und sortiert anschließend.
     */
    private <T extends AbstractBauauftragView> List<T> filterMultiples(List<T> toFilter) {
        List<T> result = new ArrayList<T>();
        if (CollectionUtils.isEmpty(toFilter)) {
            return result;
        }
        Set<Pair<Long, Long>> duplicates = new HashSet<>();
        for (T view : toFilter) {
            Pair<Long, Long> key = Pair.create(view.getVerlaufId(), view.getNiederlassungId());
            if (!duplicates.contains(key)) {
                result.add(view);
                duplicates.add(key);
            }
        }

        Collections.sort(result, new BauauftragViewComparator<T>());
        return result;
    }

    /**
     * @return Returns the verlaufViewDAO.
     */
    public VerlaufViewDAO getVerlaufViewDAO() {
        return verlaufViewDAO;
    }

    /**
     * @param verlaufViewDAO The verlaufViewDAO to set.
     */
    public void setVerlaufViewDAO(VerlaufViewDAO verlaufViewDAO) {
        this.verlaufViewDAO = verlaufViewDAO;
    }

    /* Comparator, um BauauftragViews nach dem Relisierungsdatum zu sortieren. */
    static class BauauftragViewComparator<T extends AbstractBauauftragView> implements Comparator<T>, Serializable {
        @Override
        public int compare(T view1, T view2) {
            long value1 = (view1.getRealisierungstermin() != null) ? view1.getRealisierungstermin().getTime() : 0;
            long value2 = (view2.getRealisierungstermin() != null) ? view2.getRealisierungstermin().getTime() : 0;

            if (value1 < value2) {
                return -1;
            }
            else if (value1 == value2) {
                int kunde1 = (view1.getKundeNo() != null) ? view1.getKundeNo().intValue() : 0;
                int kunde2 = (view2.getKundeNo() != null) ? view2.getKundeNo().intValue() : 0;
                if (kunde1 < kunde2) {
                    return -1;
                }
                else if (kunde1 > kunde2) {
                    return 1;
                }

                int aId1 = (view1.getAuftragId() != null) ? view1.getAuftragId().intValue() : 0;
                int aId2 = (view2.getAuftragId() != null) ? view2.getAuftragId().intValue() : 0;
                if (aId1 < aId2) {
                    return -1;
                }
                else if (aId1 > aId2) {
                    return 1;
                }

                return 0;
            }

            return 1;
        }
    }
}
