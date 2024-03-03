/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 18.05.2011 15:49:23
 */
package de.augustakom.hurrican.service.cc.impl.command;

import java.util.*;
import javax.annotation.*;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.SystemUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.log4j.Logger;

import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.common.tools.lang.BooleanTools;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.common.tools.lang.NumberTools;
import de.augustakom.common.tools.messages.AKWarnings;
import de.augustakom.hurrican.annotation.CcTxRequired;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.AuftragStatus;
import de.augustakom.hurrican.model.cc.Carrier;
import de.augustakom.hurrican.model.cc.Carrierbestellung;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.cc.Produkt;
import de.augustakom.hurrican.model.cc.Rangierung;
import de.augustakom.hurrican.model.cc.Verlauf;
import de.augustakom.hurrican.model.cc.VerlaufStatus;
import de.augustakom.hurrican.model.cc.view.PhysikFreigebenView;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.HurricanServiceCommandException;
import de.augustakom.hurrican.service.cc.BAService;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.CarrierService;
import de.augustakom.hurrican.service.cc.ProduktService;
import de.augustakom.hurrican.service.cc.RangierungsService;


/**
 * Command-Klasse um zu bestimmen inwiefern eine Rangierung automatisch freigegeben werden darf. Automatische Freigabe
 * erlaubt, wenn: <ul> <li>Port laut EWSD-Daten (T_PORT_GESAMT) frei oder nicht vorhanden <li>Auftrag in Storno oder
 * Absage <li>Auftrag gekündigt -> Kündigungs-BA abgeschlossen <li>CB-Kündigungstermin erfasst und überschritten oder
 * keine Carrierbestellung vorhanden oder Auftrag steht in Storno oder Absage und LBZ in CB nicht hinterlegt <li>falls
 * CB MNET -> Kündigungsdatum nicht betrachten <li>Achtung: Mehrere CBs möglich -> alle CBs MNET -> wie oben, ansonsten
 * Kündigungstermin des nicht MNet CBs beachten <li>wenn Kündigungstermin (Auftrag) + 10 erreicht </ul> Die View wird
 * geprüft, ob die automatische Freigabe greift. Wenn ja, ist das Flag 'freigeben' gesetzt, wenn nein, ist die
 * ClarifyInfo mit einem Text gesetzt. Wird eine Exception geworfen, muss das Flag 'freigeben' gelöscht und die
 * ClarifyInfo gesetzt sein.
 */
public class EWSDAutoFreigabeCommand extends AbstractServiceCommand {

    private static final Logger LOGGER = Logger.getLogger(EWSDAutoFreigabeCommand.class);

    // Property-Keys
    /**
     * Value fuer die prepare-Methode, um die Physik Freigeben View zu uebergeben.
     */
    public static final String RANGIER_RELATIONEN_MAP = "rangier.relationen.map";

    // Properties
    private Map<Long, List<PhysikFreigebenView>> rangierungRelationen;

    // Resources
    @Resource(name = "de.augustakom.hurrican.service.cc.RangierungsService")
    private RangierungsService rangierungsService;
    @Resource(name = "de.augustakom.hurrican.service.cc.CCAuftragService")
    private CCAuftragService auftragService;
    @Resource(name = "de.augustakom.hurrican.service.cc.ProduktService")
    private ProduktService produktService;
    @Resource(name = "de.augustakom.hurrican.service.cc.CarrierService")
    private CarrierService carrierService;
    @Resource(name = "de.augustakom.hurrican.service.cc.BAService")
    private BAService baService;

    // Vars
    public static final String PORT_GESAMT_FREE = "FREE";
    private AuftragDaten auftragDaten;
    private Date now;

    @Override
    @CcTxRequired
    public Object execute() throws Exception {
        checkValues();
        AKWarnings warnings = new AKWarnings();
        now = DateUtils.truncate(new Date(), Calendar.DAY_OF_MONTH);

        if (MapUtils.isNotEmpty(rangierungRelationen)) {
            Set<Long> keys = rangierungRelationen.keySet();
            if (CollectionTools.isNotEmpty(keys)) {
                for (Long rangierId : keys) {
                    List<PhysikFreigebenView> freigabeViews = rangierungRelationen.get(rangierId);
                    if (CollectionTools.isNotEmpty(freigabeViews)) {
                        for (PhysikFreigebenView freigabeView : freigabeViews) {
                            processView(warnings, freigabeView);
                        }
                    }
                }
            }
        }
        return warnings;
    }

    private void processView(AKWarnings warnings, PhysikFreigebenView freigabeView) {
        try {
            freigabeView.setClarifyInfo(null);
            freigabeView.setFreigeben(Boolean.FALSE);

            if (freigabeView.getAuftragId() != null) {
                if (BooleanTools.nullToFalse(freigabeView.getIsLocked())) {
                    // Kunde ist nach §95 TKG gesperrt -> die Freigabe der Rangierung darf durch diesen
                    // Auftrag nicht geblockt werden -> andere Auftraege (neuere) eventuell schon
                    freigabeView.setFreigeben(Boolean.TRUE);
                }
                else if (isOrderCanceled(freigabeView)) {
                    // wenn die aktuelle Rangierung im aktuellen Auftrag an
                    // einer zweiten bereits historisierten Rangierung haengt
                    // (via LeitungGesamtId), so muss dieser Auftrag nicht
                    // weiter betrachtet werden
                    freigabeView.setFreigeben(Boolean.TRUE);
                }
                else if ((processPortGesamt(freigabeView)) && (processOrder(freigabeView))
                        && (processCB(freigabeView)) && (processOrderTermDate(freigabeView))) {
                    // Standard Checks alle mit OK durchlaufen
                    freigabeView.setFreigeben(Boolean.TRUE);
                }
            }
            else {
                // Freigabebereite Rangierung ohne Auftrag -> freigeben, auch wenn die EWSD den
                // Port nicht als frei angibt -> kein Check auf PortGesamt
                freigabeView.setFreigeben(Boolean.TRUE);
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            warnings.addAKWarning(this, e.getMessage());
        }
    }

    boolean processOrderTermDate(PhysikFreigebenView freigabeView) {
        // bei Absagen ist kein Kündigungsdatum gesetzt
        if (NumberTools.equal(auftragDaten.getStatusId(), AuftragStatus.ABSAGE)
                && (auftragDaten.getKuendigung() == null)) {
            return true;
        }
        // Kein Kuendigungsdatum gesetzt (ausser bei Absagen)
        if (auftragDaten.getKuendigung() == null) {
            freigabeView.setClarifyInfo("Kündigungstermin des Auftrages nicht gesetzt!");
            return false;
        }
        // Kuendigungsdatum + Delay in Vergangenheit?
        Date termDate = DateUtils.truncate(DateTools.changeDate(auftragDaten.getKuendigung(),
                Calendar.DATE, RangierungsService.DELAY_4_RANGIERUNGS_FREIGABE), Calendar.DAY_OF_MONTH);
        if (DateTools.isAfter(now, termDate)) {
            return true;
        }
        freigabeView.setClarifyInfo(String.format("Kündigung des Auftrags ist nach dem %s (inklusive Delay)"
                + " festgesetzt!", DateTools.formatDate(termDate, DateTools.PATTERN_DAY_MONTH_YEAR)));
        return false;
    }

    boolean processCB(PhysikFreigebenView freigabeView) throws HurricanServiceCommandException {
        List<Carrierbestellung> cbs = getCarrierbestellungen(freigabeView);
        if (CollectionTools.isEmpty(cbs)) {
            return true;
        }
        List<Carrierbestellung> nonMNetCBs = new ArrayList<>();
        for (Carrierbestellung carrierBestellung : cbs) {
            if (!Carrier.isMNetCarrier(carrierBestellung.getCarrier())) {
                nonMNetCBs.add(carrierBestellung);
            }
        }
        if (CollectionTools.isEmpty(nonMNetCBs)) {
            return true;
        }
        for (Carrierbestellung carrierBestellung : nonMNetCBs) {
            if (!(isAuftragStornoOrAbsage() && StringUtils.isBlank(carrierBestellung.getLbz()))) {
                // Auftrag steht auf ABSAGE oder STORNO und keine LBZ hinterlegt -> Freigabe erlaubt

                if (carrierBestellung.getKuendBestaetigungCarrier() != null) {
                    Date kuendigungsTermin = DateUtils.truncate(carrierBestellung.getKuendBestaetigungCarrier(),
                            Calendar.DAY_OF_MONTH);
                    if (DateTools.isDateBeforeOrEqual(now, kuendigungsTermin)) {
                        freigabeView.setClarifyInfo(String.format(
                                "Kündigungsdatum der CB liegt in der Zukunft! (LBZ %s)",
                                carrierBestellung.getLbz()));
                        return false;
                    }
                }
                else {
                    freigabeView.setClarifyInfo(String.format("Kündigungsdatum der CB ist nicht gesetzt! (LBZ %s)",
                            carrierBestellung.getLbz()));
                    return false;
                }
            }
        }

        // Alle CBs geprüft (gueltige TermDates, LBZ hinterlegt), kein KO Kriterium ermittelt -> Freigabe erlaubt
        return true;
    }

    private List<Carrierbestellung> getCarrierbestellungen(PhysikFreigebenView freigabeView) throws HurricanServiceCommandException {
        try {
            List<Carrierbestellung> carrierBestellungen = carrierService.findCBs4EndstelleTx(freigabeView.getEndstelleId());
            if (carrierBestellungen == null) {
                carrierBestellungen = Collections.emptyList();
            }
            return carrierBestellungen;
        }
        catch (FindException e) {
            String message = String.format("%sEndstellen ID %s: Carrierbestellungen zum Auftrag %s nicht ermittelbar!",
                    SystemUtils.LINE_SEPARATOR, freigabeView.getRangierId(), freigabeView.getAuftragId());
            freigabeView.setClarifyInfo(message);
            throw new HurricanServiceCommandException(message);
        }
    }

    boolean processOrder(PhysikFreigebenView freigabeView) throws HurricanServiceCommandException {

        auftragDaten = getAuftragDaten(freigabeView);
        // Auftrag auf Absage oder Storno?
        if (isAuftragStornoOrAbsage()) {
            return true;
        }
        // Auftrag NICHT gekündigt?
        if (!auftragDaten.isCancelled()) {
            freigabeView.setClarifyInfo("Auftrag ist nicht gekündigt!");
            return false;
        }
        // Kündigungs-BA abgeschlossen?
        try {
            List<Verlauf> verlaeufe = baService.findVerlaeufe4Auftrag(freigabeView.getAuftragId());
            if (CollectionTools.isNotEmpty(verlaeufe)) {
                Verlauf verlauf = verlaeufe.get(0);
                if (!verlauf.isKuendigung()) {
                    freigabeView.setClarifyInfo("Aktueller BA ist kein Kündigungs-BA!");
                    return false;
                }
                if (!NumberTools.equal(verlauf.getVerlaufStatusId(), VerlaufStatus.KUENDIGUNG_VERLAUF_ABGESCHLOSSEN)) {
                    freigabeView.setClarifyInfo("Kündigungs-BA ist nicht abgeschlossen!");
                    return false;
                }
                return true;
            }
            else {
                // Wholesale Auftraege haben keine BAs
                // Allgemein: Produkte, die keine BAs konfiguriert haben,
                // koennen auch ohne Kuendings BA freigegeben werden
                Produkt produkt = getProdukt(auftragDaten.getProdId(), freigabeView);
                if (!produkt.getElVerlauf()) {
                    return true;
                }
            }
        }
        catch (FindException e) {
            LOGGER.info(e.getMessage());
            // Keine Verlaufe -> kein Kündungs BA -> return false
        }
        freigabeView.setClarifyInfo("Kein BA zum Auftrag verfügbar!");
        return false;
    }

    private boolean isAuftragStornoOrAbsage() {
        return NumberTools.isIn(auftragDaten.getStatusId(), new Number[] { AuftragStatus.STORNO, AuftragStatus.ABSAGE });
    }

    private AuftragDaten getAuftragDaten(PhysikFreigebenView freigabeView) throws HurricanServiceCommandException {
        try {
            AuftragDaten auftragDatenLokal = auftragService.findAuftragDatenByAuftragId(freigabeView.getAuftragId());
            if (auftragDatenLokal == null) {
                throw new FindException(SystemUtils.LINE_SEPARATOR + "Auftrag Daten nicht verfügbar!");
            }
            return auftragDatenLokal;
        }
        catch (FindException e) {
            String message = String.format("%sRangier ID %s: Auftrag Daten zum Auftrag %s nicht ermittelbar!",
                    SystemUtils.LINE_SEPARATOR, freigabeView.getRangierId(), freigabeView.getAuftragId());
            freigabeView.setClarifyInfo(message);
            throw new HurricanServiceCommandException(message);
        }
    }

    private Produkt getProdukt(Long produktId, PhysikFreigebenView freigabeView) throws HurricanServiceCommandException {
        try {
            Produkt produkt = produktService.findProdukt(produktId);
            if (produkt == null) {
                throw new FindException(SystemUtils.LINE_SEPARATOR + "Produkt zu Auftrag nicht verfügbar!");
            }
            return produkt;
        }
        catch (FindException e) {
            String message = String.format("%sRangier ID %s: Zum Auftrag %s ist das Produkt nicht ermittelbar!",
                    SystemUtils.LINE_SEPARATOR, freigabeView.getRangierId(), freigabeView.getAuftragId());
            freigabeView.setClarifyInfo(message);
            throw new HurricanServiceCommandException(message);
        }
    }

    boolean processPortGesamt(PhysikFreigebenView freigabeView) {
        if ((freigabeView.getPortGesamtStatus() == null)
                || StringUtils.equals(PORT_GESAMT_FREE, freigabeView.getPortGesamtStatus())) {
            return true;
        }
        freigabeView.setClarifyInfo("EWSD-Port belegt");
        return false;
    }

    /**
     * Prüft ob es für den Auftrag neben der freizugebenden Rangierung nur noch historisierte Rangierungen gibt.
     *
     * @param freigabeView
     * @param esTyp
     * @return
     * @throws FindException
     */
    boolean checkRangierungenHistorized(PhysikFreigebenView freigabeView, String esTyp) throws FindException {
        Rangierung[] rangierungenFuerEndstelle = rangierungsService.findRangierungenTx(freigabeView.getAuftragId(),
                esTyp);
        if (ArrayUtils.isNotEmpty(rangierungenFuerEndstelle)) {
            boolean currentFound = false;
            boolean siblingHistorized = false;
            for (Rangierung rangierung : rangierungenFuerEndstelle) {
                if (NumberTools.equal(rangierung.getId(), freigabeView.getRangierId())) {
                    currentFound = true;
                }
                else if (!DateTools.isHurricanEndDate(rangierung.getGueltigBis())) {
                    siblingHistorized = true;
                }
            }
            if (currentFound && siblingHistorized) {
                return true;
            }
        }
        return false;
    }

    boolean isOrderCanceled(PhysikFreigebenView freigabeView) throws FindException {
        return checkRangierungenHistorized(freigabeView, Endstelle.ENDSTELLEN_TYP_B)
                || checkRangierungenHistorized(freigabeView, Endstelle.ENDSTELLEN_TYP_A);
    }

    /**
     * Ueberprueft, ob alle benoetigten Daten uebergeben wurden.
     */
    @SuppressWarnings("unchecked")
    private void checkValues() throws HurricanServiceCommandException {
        rangierungRelationen = (getPreparedValue(RANGIER_RELATIONEN_MAP) instanceof Map<?, ?>)
                ? (Map<Long, List<PhysikFreigebenView>>) getPreparedValue(RANGIER_RELATIONEN_MAP) : null;
        if (rangierungRelationen == null) {
            throw new HurricanServiceCommandException(SystemUtils.LINE_SEPARATOR + "Die Map der Freigabeviews fehlt!");
        }
    }

    /**
     * Injected by Test
     */
    public void setAuftragDaten(AuftragDaten auftragDaten) {
        this.auftragDaten = auftragDaten;
    }

    /**
     * Injected by Test
     */
    public void setNow(Date now) {
        this.now = DateUtils.truncate(now, Calendar.DAY_OF_MONTH);
    }

    /**
     * Injected by Test
     */
    public void setAuftragService(CCAuftragService auftragService) {
        this.auftragService = auftragService;
    }

    /**
     * Injected by Test
     */
    public void setBaService(BAService baService) {
        this.baService = baService;
    }

    /**
     * Injected by Test
     */
    public void setCarrierService(CarrierService carrierService) {
        this.carrierService = carrierService;
    }

    /**
     * Injected by Test
     */
    public void setProduktService(ProduktService produktService) {
        this.produktService = produktService;
    }

    /**
     * Injected by Test
     */
    public void setRangierungsService(RangierungsService rangierungsService) {
        this.rangierungsService = rangierungsService;
    }

}
