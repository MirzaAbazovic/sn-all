/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 02.08.2011 14:22:54
 */
package de.mnet.wita.service.impl;

import static com.google.common.collect.Sets.*;

import java.util.*;
import java.util.stream.*;
import javax.annotation.*;
import javax.validation.constraints.*;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.common.tools.lang.NumberTools;
import de.augustakom.common.tools.lang.Pair;
import de.augustakom.hurrican.annotation.CcTxRequired;
import de.augustakom.hurrican.model.TNB;
import de.augustakom.hurrican.model.billing.DNTNB;
import de.augustakom.hurrican.model.billing.Rufnummer;
import de.augustakom.hurrican.model.cc.Carrier;
import de.augustakom.hurrican.model.cc.CarrierKennung;
import de.augustakom.hurrican.model.cc.Carrierbestellung;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.cc.Equipment;
import de.augustakom.hurrican.model.cc.Feature;
import de.augustakom.hurrican.model.cc.KvzAdresse;
import de.augustakom.hurrican.model.cc.Rangierung;
import de.augustakom.hurrican.model.cc.tal.CBVorgang;
import de.augustakom.hurrican.model.cc.tal.CBVorgangSubOrder;
import de.augustakom.hurrican.model.cc.tal.TalRealisierungsZeitfenster;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.billing.RufnummerService;
import de.augustakom.hurrican.service.cc.CarrierService;
import de.augustakom.hurrican.service.cc.EndstellenService;
import de.augustakom.hurrican.service.cc.FeatureService;
import de.augustakom.hurrican.service.cc.HVTService;
import de.augustakom.hurrican.service.cc.RangierungsService;
import de.mnet.wita.AbmMeldungsCode;
import de.mnet.wita.exceptions.WitaBaseException;
import de.mnet.wita.exceptions.WitaDataAggregationException;
import de.mnet.wita.message.MnetWitaRequest;
import de.mnet.wita.message.auftrag.Kundenwunschtermin;
import de.mnet.wita.message.auftrag.Schaltangaben;
import de.mnet.wita.message.auftrag.SchaltungKupfer;
import de.mnet.wita.message.auftrag.SchaltungKvzTal;
import de.mnet.wita.message.common.Uebertragungsverfahren;
import de.mnet.wita.message.meldung.AuftragsBestaetigungsMeldung;
import de.mnet.wita.message.meldung.position.MeldungsPositionWithAnsprechpartner;
import de.mnet.wita.model.Vorabstimmung;
import de.mnet.wita.model.WitaCBVorgang;
import de.mnet.wita.service.WitaTalOrderService;
import de.mnet.wita.service.WitaVorabstimmungService;

@CcTxRequired
public class WitaDataService {

    private static final Logger LOGGER = Logger.getLogger(WitaDataService.class);

    @Resource(name = "de.augustakom.hurrican.service.cc.CarrierService")
    private CarrierService carrierService;
    @Resource(name = "de.augustakom.hurrican.service.cc.HVTService")
    private HVTService hvtService;
    @Resource(name = "de.augustakom.hurrican.service.cc.EndstellenService")
    private EndstellenService endstellenService;
    @Resource(name = "de.augustakom.hurrican.service.cc.RangierungsService")
    private RangierungsService rangierungsService;
    @Resource(name = "de.augustakom.hurrican.service.billing.RufnummerService")
    private RufnummerService rufnummerService;
    @Resource(name = "de.mnet.wita.service.WitaVorabstimmungService")
    private WitaVorabstimmungService witaVorabstimmungService;
    @Resource(name = "de.mnet.wita.service.WitaTalOrderService")
    private WitaTalOrderService witaTalOrderService;
    @Autowired
    private FeatureService featureService;

    /**
     * Ermittelt die DTAG Equipments des urspruenglichen (First im Pair) und des aktuellen (Second im Pair) Auftrags.
     */
    public Pair<Equipment, Equipment> loadEquipments(Carrierbestellung carrierbestellung, Long auftragIdNew)
            throws FindException {
        Endstelle endstelle = loadEndstelle(auftragIdNew, carrierbestellung);
        String endstellenTyp = (endstelle != null) ? endstelle.getEndstelleTyp() : null;

        Equipment equipmentNew = loadEquipment(auftragIdNew, endstellenTyp);
        Equipment equipmentOld = (equipmentNew != null) ? loadEquipment(carrierbestellung.getAuftragId4TalNA(),
                endstellenTyp) : null;

        return Pair.create(equipmentOld, equipmentNew);
    }

    private Equipment loadEquipment(Long auftragId, String endstellenTyp) throws FindException {
        Endstelle endstelle = endstellenService.findEndstelle4Auftrag(auftragId, endstellenTyp);
        Rangierung rangierung = ((endstelle != null) && (endstelle.getRangierId() != null)) ? rangierungsService
                .findRangierung(endstelle.getRangierId()) : null;

        return ((rangierung != null) && (rangierung.getEqOutId() != null)) ? rangierungsService
                .findEquipment(rangierung.getEqOutId()) : null;
    }

    public Vorabstimmung loadVorabstimmung(WitaCBVorgang witaCBVorgang) {
        Vorabstimmung vorabstimmung;
        if (witaCBVorgang.getCbId() != null) {
            Carrierbestellung carrierbestellung = loadCarrierbestellung(witaCBVorgang);
            Endstelle endstelle = loadEndstelle(witaCBVorgang.getAuftragId(), carrierbestellung);
            if (endstelle == null) { throw new WitaDataAggregationException("Keine Endstelle gefunden"); }
            vorabstimmung = witaVorabstimmungService.findVorabstimmung(endstelle, witaCBVorgang.getAuftragId());

            if ((vorabstimmung == null) && (witaCBVorgang.getAuftragsKlammer() != null)) {
                // Vorabstimmung ueber Auftragsklammer ermitteln
                List<Vorabstimmung> vorabstimmungen = witaVorabstimmungService.findVorabstimmungForAuftragsKlammer(
                        witaCBVorgang.getAuftragsKlammer(), endstelle);
                if (CollectionTools.hasExpectedSize(vorabstimmungen, 1)) {
                    vorabstimmung = vorabstimmungen.get(0);
                }
                else if (CollectionTools.isNotEmpty(vorabstimmungen) && (vorabstimmungen.size() > 1)) {
                    // @formatter:off
                    throw new WitaDataAggregationException(
                        "Für die Auftragsklammer wurden mehrere mögliche Vorabstimmungs-Objekte auf den anderen gebündelten Aufträgen gefunden."
                                + "Bitte definieren Sie nur ein Vorabstimmungs-Objekt für die gesamte Klammer oder für jeden techn. Auftrag ein eigenes Vorabstimmungs-Objekt.");
                    // @formatter:on
                }
            }
        }
        else {
            vorabstimmung = witaVorabstimmungService.findVorabstimmungForRexMk(witaCBVorgang.getAuftragId());
        }
        if (vorabstimmung == null) {
            // @formatter:off
            throw new WitaDataAggregationException(
                "Konnte keine Provider-Daten für die Carrierbestellung ermitteln!");
            // @formatter:on
        }
        return vorabstimmung;
    }

    public Endstelle loadEndstelle(Long auftragId, Carrierbestellung carrierbestellung) {
        try {
            List<Endstelle> endstellen = endstellenService.findEndstellen4Auftrag(auftragId);
            if (CollectionTools.isNotEmpty(endstellen)) {
                for (Endstelle endstelle : endstellen) {
                    // @formatter:off
                    if (NumberTools.equal(endstelle.getCb2EsId(), carrierbestellung.getCb2EsId())) {
                        return endstelle;
                    }
                    // @formatter:on
                }
            }
            return null;
        }
        catch (Exception e) {
            throw new WitaDataAggregationException("Fehler bei der Ermittlung der Endstelle: " + e.getMessage(), e);
        }
    }

    /**
     * Ermittelt die zum WITA-Bestellvorgang zugehoerigen Endstellen. Bei 4-Draht wird zusätzlich die Endstelle, der
     * zugehörigen {@link CBVorgangSubOrder} geladen.
     *
     * @return die zum WITA-Bestellvorgang zugehoerigen Endstellen (zwei Endstellen, falls bei 4-Draht)
     * @throws WitaDataAggregationException wenn bei der Ermittlung der Daten ein Fehler auftritt
     */
    public List<Endstelle> loadEndstellen(CBVorgang cbVorgang) throws WitaDataAggregationException {
        try {
            if (cbVorgang.getCbId() == null) {
                throw new WitaDataAggregationException(
                        "CBVorgang ist keiner Carrierbestellung zugeordnet!");
            }
            return loadEndstellenViaCarrierbestellung(cbVorgang);
        }
        catch (FindException e) {
            throw new WitaDataAggregationException("Fehler bei der Ermittlung der Endstelle: " + e.getMessage(), e);
        }
    }

    private List<Endstelle> loadEndstellenViaCarrierbestellung(CBVorgang cbVorgang) throws FindException {
        List<Endstelle> endstellen = new ArrayList<>();
        endstellen.addAll(endstellenService.findEndstellen4Auftrag(cbVorgang.getAuftragId()));
        if (CollectionTools.isEmpty(endstellen)) {
            throw new WitaDataAggregationException(
                    "Keine Endstellen fuer den Auftrag gefunden.");
        }

        if (Boolean.TRUE.equals(cbVorgang.getVierDraht())) { // add CbVorgangSubOrders for Vier-Draht-Bestellung
            Set<CBVorgangSubOrder> subOrders = cbVorgang.getSubOrders();
            if (CollectionTools.isEmpty(subOrders)) {
                throw new WitaDataAggregationException("Zweiter Port fuer 4-Draht-Bestellung fehlt.");
            }
            else if (subOrders.size() != 1) {
                throw new WitaDataAggregationException(
                        "Mehr als zwei Ports fuer 4-Draht-Bestellung angegeben.");
            }
            endstellen.addAll(endstellenService.findEndstellen4Auftrag(subOrders.iterator().next().getAuftragId()));
        }

        Carrierbestellung carrierbestellung = carrierService.findCB(cbVorgang.getCbId());
        if (carrierbestellung == null) {
            throw new WitaDataAggregationException(
                    "Zugehoerige Carrierbestellung wurde nicht gefunden!");
        }

        List<Endstelle> relevantEndstellen = Lists.newArrayList();
        for (Endstelle endstelle : endstellen) {
            if (NumberTools.equal(endstelle.getCb2EsId(), carrierbestellung.getCb2EsId())) {
                relevantEndstellen.add(endstelle);
            }
        }
        return relevantEndstellen;
    }

    /**
     * siehe {@link WitaDataService#loadDtagEquipments(CBVorgang cbVorgang)}<br>
     * Sofern eine {@link de.mnet.wita.exceptions.WitaDataAggregationException} auftritt, so wird KEIN(!) Rollback
     * veranlasst!
     */
    @Transactional(
            value = "cc.hibernateTxManager",
            noRollbackFor = de.mnet.wita.exceptions.WitaDataAggregationException.class,
            propagation = Propagation.REQUIRED
    )
    public List<Equipment> loadDtagEquipmentsWithNoRollback(CBVorgang cbVorgang) {
        return loadDtagEquipments(cbVorgang);
    }

    /**
     * Ermittelt den DTAG-Port, der fuer den TAL-Vorgang relevant ist. <br> Falls kein valider DTAG-Port gefunden wird,
     * wird eine {@link WitaDataAggregationException} erzeugt.
     *
     * @return der zum {@link CBVorgang} gehoerende valide DTAG-Port (zwei Ports, falls bei 4-Draht)
     */
    @Nonnull
    public List<Equipment> loadDtagEquipments(CBVorgang cbVorgang) {
        List<Rangierung> rangierungen = loadRangierungen(cbVorgang);
        if (rangierungen.isEmpty()) {
            throw new WitaDataAggregationException(
                    "Es konnte keine Rangierung ermittelt werden!");
        }
        if (rangierungen.size() > 2) {
            throw new WitaDataAggregationException(
                    "Es wurden mehr als 2 Rangierungen gefunden!");
        }

        try {
            List<Equipment> result = new ArrayList<>();
            for (Rangierung rangierung : rangierungen) {
                Equipment eqOut = rangierungsService.findEquipment(rangierung.getEqOutId());
                if (eqOut == null) {
                    throw new WitaDataAggregationException(
                            "Der DTAG-Port für eine Rangierung konnte nicht ermittelt werden!");
                }

                if (!isValidDtagEquipment(eqOut)) {
                    throw new WitaDataAggregationException(
                            "Bei dem ermittelten Port handelt es sich um keinen gueltigen DTAG-Port!");
                }
                result.add(eqOut);
            }
            return result;
        }
        catch (FindException e) {
            LOGGER.error(e.getMessage(), e);
            throw new WitaDataAggregationException("Bei der Ermittlung des DTAG-Ports ist ein Fehler aufgetreten: "
                    + e.getMessage(), e);
        }
    }

    /**
     * Ueberprueft, ob es sich bei dem angegebenen Equipment um einen gueltigen DTAG-Port handelt. <br> <br> Ein Port
     * wird als gueltiger DTAG-Port angesehen, wenn folgende Bedingungen erfuellt sind: <ul> <li>Carrier=DTAG
     * <li>RangSSType ist gesetzt <li>RangBucht/Leiste1/Stift1 sind gesetzt </ul>
     */
    boolean isValidDtagEquipment(Equipment equipment) {
        if (!StringUtils.equalsIgnoreCase(equipment.getCarrier(), Carrier.CARRIER_DTAG)) { return false; }
        if (StringUtils.isBlank(equipment.getRangSSType())) { return false; }
        if (StringUtils.isBlank(equipment.getRangBucht()) || StringUtils.isBlank(equipment.getRangLeiste1())
                || StringUtils.isBlank(equipment.getRangStift1())) { return false; }
        return true;
    }

    /**
     * Ermittelt die Rangierung, die der Endstelle zugeordnet ist (zwei Rangierung, falls bei 4-Draht).
     */
    List<Rangierung> loadRangierungen(CBVorgang cbVorgang) {
        try {
            List<Endstelle> endstellen = loadEndstellen(cbVorgang);
            if (endstellen.isEmpty()) {
                throw new WitaDataAggregationException(
                        "Die Endstelle konnte nicht ermittelt werden.");
            }
            if (endstellen.size() > 2) {
                throw new WitaDataAggregationException(
                        "Es wurden mehr als 2 Endstellen gefunden.");
            }

            List<Rangierung> rangierungen = new ArrayList<>();
            for (Endstelle endstelle : endstellen) {
                if (endstelle.getRangierId() == null) {
                    throw new WitaDataAggregationException(
                            "Einer Endstelle ist keine Rangierung zugeordnet!");
                }
                rangierungen.add(rangierungsService.findRangierung(endstelle.getRangierId()));
            }
            return rangierungen;
        }
        catch (FindException e) {
            LOGGER.error(e.getMessage(), e);
            throw new WitaDataAggregationException("Bei der Ermittlung der Rangierung ist ein Fehler aufgetreten: "
                    + e.getMessage(), e);
        }
    }

    public Carrierbestellung loadCarrierbestellung(CBVorgang cbVorgang) {
        try {
            Carrierbestellung cb = carrierService.findCB(cbVorgang.getCbId());
            Preconditions.checkNotNull(cb, "Die aktuelle Carrierbestellung konnte nicht ermittelt werden!");
            return cb;
        }
        catch (FindException e) {
            LOGGER.error(e.getMessage(), e);
            throw new WitaDataAggregationException(
                    "Bei der Ermittlung der Carrierbestellung ist ein Fehler aufgetreten: " + e.getMessage(), e);
        }
    }

    /**
     * Ermittelt die aktuelleste Carrierbestellung von dem Auftrag, der ueber {@link
     * Carrierbestellung#getAuftragId4TalNA} referenziert ist.
     *
     * @param cbVorgang               aktueller CB-Vorgang, ueber den der Typ der Endstelle ermittelt werden kann
     * @param actualCarrierbestellung aktuelle Carrierbestellung, die eine Auftrags-Referenz besitzt
     * @return (letzte) Carrierbestellung des Auftrags, der ueber {@link Carrierbestellung#getAuftragId4TalNA}
     * referenziert ist.
     */
    public Carrierbestellung getReferencingCarrierbestellung(CBVorgang cbVorgang,
            Carrierbestellung actualCarrierbestellung) {
        List<Endstelle> endstellenAkt = loadEndstellen(cbVorgang);
        if (endstellenAkt.isEmpty()) {
            throw new WitaDataAggregationException("Es wurde keine Endstelle zur aktuellen Carrierbestellung gefunden!");
        }
        else if (!cbVorgang.isVierDraht() && (endstellenAkt.size() > 1)) {
            throw new WitaDataAggregationException(
                    "Es wurde mehr als EINE Endstelle zur aktuellen Carrierbestellung gefunden!");
        }
        Endstelle endstelleAkt = endstellenAkt.get(0);
        try {
            Carrierbestellung carrierbestellungOfOldOrder = null;
            List<Endstelle> endstellenAlt = endstellenService.findEndstellen4Auftrag(actualCarrierbestellung
                    .getAuftragId4TalNA());
            if (endstellenAlt != null) {
                for (Endstelle endstelleTmp : endstellenAlt) {
                    if (endstelleTmp.getEndstelleTyp().equals(endstelleAkt.getEndstelleTyp())) {
                        List<Carrierbestellung> cbs = carrierService.findCBs(endstelleTmp.getCb2EsId());
                        if (!cbs.isEmpty()) {
                            carrierbestellungOfOldOrder = cbs.get(cbs.size() - 1);

                            if (carrierbestellungOfOldOrder.getId().equals(actualCarrierbestellung.getId())) {
                                // Sonderfall: Benutze alte Carrierbestellung fuer DTAG Portwechsel
                                if (cbs.size() < 2) {
                                    throw new WitaDataAggregationException(
                                            "Vorherige Carrierbestellung für DTAG Portwechsel konnte nicht ermittelt werden!");
                                }
                                carrierbestellungOfOldOrder = cbs.get(cbs.size() - 2);
                            }
                        }
                        else {
                            throw new WitaDataAggregationException(
                                    "Carrierbestellung für Vorgänger-Auftrag konnte nicht ermittelt werden!");
                        }
                    }
                }
            }
            else {
                throw new WitaDataAggregationException(
                        "Es konnte keine Endstelle für den Vorgänger-Auftrag ermittelt werden!");
            }

            return carrierbestellungOfOldOrder;
        }
        catch (FindException e) {
            LOGGER.error(e.getMessage(), e);
            throw new WitaDataAggregationException(
                    "Bei der Ermittlung der Carrierbestellung des Vorgänger-Auftrags ist ein Fehler aufgetreten: "
                            + e.getMessage(), e
            );
        }
    }

    /**
     * Erstellt aus dem {@link Equipment} Objekt die notwendigen Schaltangaben.
     */
    public Schaltangaben createSchaltangaben(Collection<Equipment> dtagEquipment) {
        List<SchaltungKupfer> schaltungsListKupfer = new ArrayList<>();
        List<SchaltungKvzTal> schaltungsListKvz = new ArrayList<>();
        for (Equipment equipment : dtagEquipment) {
            if (equipment.isPortForHvtTal()) {
                schaltungsListKupfer.add(createSchaltungKupferFor(equipment, equipment.getUetv()));
            }
            else if (equipment.isPortForKvzTal()) {
                schaltungsListKvz.add(createSchaltungKvzTal(equipment, equipment.getUetv()));
            }
        }
        return defineSchaltangaben(schaltungsListKupfer, schaltungsListKvz);
    }

    /**
     * Erstellt aus dem {@link Equipment} Objekt die notwendigen Schaltangaben - ersetzt das Uebertragungsverfahren.
     */
    public Schaltangaben createSchaltangaben(Collection<Equipment> dtagEquipment,
            de.augustakom.hurrican.model.cc.Uebertragungsverfahren previousUetv) {
        List<SchaltungKupfer> schaltungsListKupfer = new ArrayList<>();
        List<SchaltungKvzTal> schaltungsListKvz = new ArrayList<>();
        for (Equipment equipment : dtagEquipment) {
            if (equipment.isPortForHvtTal()) {
                schaltungsListKupfer.add(createSchaltungKupferFor(equipment, previousUetv));
            }
            else if (equipment.isPortForKvzTal()) {
                schaltungsListKvz.add(createSchaltungKvzTal(equipment, previousUetv));
            }
        }

        return defineSchaltangaben(schaltungsListKupfer, schaltungsListKvz);
    }

    private Schaltangaben defineSchaltangaben(List<SchaltungKupfer> schaltungsListKupfer, List<SchaltungKvzTal> schaltungsListKvz) {
        Schaltangaben schaltangaben = new Schaltangaben();
        if (CollectionTools.isNotEmpty(schaltungsListKupfer)) {
            schaltangaben.setSchaltungKupfer(schaltungsListKupfer);
        }
        else if (CollectionTools.isNotEmpty(schaltungsListKvz)) {
            schaltangaben.setSchaltungKvzTal(schaltungsListKvz);
        }
        return schaltangaben;
    }

    SchaltungKupfer createSchaltungKupferFor(Equipment dtagPort,
            de.augustakom.hurrican.model.cc.Uebertragungsverfahren uetv) {
        SchaltungKupfer schaltungKupfer = new SchaltungKupfer();
        schaltungKupfer.setUEVT(dtagPort.getRangBucht());
        schaltungKupfer.setEVS(dtagPort.getRangLeiste1());
        schaltungKupfer.setDoppelader(Equipment.getDtagValue4Port(dtagPort.getRangStift1()));
        schaltungKupfer.setUebertragungsverfahren(getDtagUebertragungsverfahren(uetv));
        return schaltungKupfer;
    }

    SchaltungKvzTal createSchaltungKvzTal(Equipment dtagPort, de.augustakom.hurrican.model.cc.Uebertragungsverfahren uetv) {
        SchaltungKvzTal kvzTal = new SchaltungKvzTal();
        kvzTal.setKvz(KvzAdresse.formatKvzNrForWita(dtagPort.getKvzNummer()));
        kvzTal.setKvzSchaltnummer(String.format("%s%s", dtagPort.getRangVerteiler(), dtagPort.getKvzDoppelader()));
        kvzTal.setUebertragungsverfahren(getDtagUebertragungsverfahren(uetv));
        return kvzTal;
    }

    /**
     * Ermittelt den WITA Enum Typ fuer das Uebertragungsverfahren.
     *
     * @return Enum mit dem WITA Uebertragungsverfahren oder {@code null}, wenn das Hurrican UETV nicht aufgeloest
     * werden konnte.
     */
    Uebertragungsverfahren getDtagUebertragungsverfahren(
            de.augustakom.hurrican.model.cc.Uebertragungsverfahren equipmentUetv) {
        if (equipmentUetv == null) { return null; }
        try {
            // Bei Niederbitratigen TALs darf kein Uebertragungsverfahren gesetzt werden
            if (de.augustakom.hurrican.model.cc.Uebertragungsverfahren.N01.equals(equipmentUetv)) { return null; }
            return Uebertragungsverfahren.valueOf(equipmentUetv.toString());
        }
        catch (IllegalArgumentException e) {
            throw new WitaDataAggregationException(equipmentUetv.toString()
                    + " ist kein gültiges WITA-Übertragungsverfahren.");
        }
    }

    /**
     * Ermittelt die CarrierKennung des technischen Standorts, der fuer die WITA-Bestellung relevant ist.
     */
    public CarrierKennung loadCarrierKennung(WitaCBVorgang cbVorgang) {
        try {
            List<Endstelle> endstellen = loadEndstellen(cbVorgang);
            if (CollectionUtils.isEmpty(endstellen)) {
                throw new WitaDataAggregationException(
                        "Es konnten keine Endstellen ermittelt werden.");
            }
            Endstelle endstelle = endstellen.get(0); // same Hvt for 4-Draht Endstellen
            if (endstelle.getHvtIdStandort() == null) {
                throw new WitaDataAggregationException(
                        "Der Endstelle ist kein technischer Standort zugeordnet!");
            }
            CarrierKennung carrierKennung = carrierService.findCarrierKennung4Hvt(endstelle.getHvtIdStandort());
            if (carrierKennung == null) {
                throw new WitaDataAggregationException(
                        "Carrier-Kennung wurde nicht gefunden!");
            }
            return carrierKennung;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new WitaDataAggregationException("Fehler beim Ermitteln der CarrierKennung: " + e.getMessage(), e);
        }
    }

    /**
     * Ermittelt die CarrierKennung fuer die WITA Bestellung ueber die REX-MK Rufnummern (ACT_CARRIER)
     */
    public CarrierKennung loadCarrierKennungForRexMk(WitaCBVorgang cbVorgang) {
        if (featureService.isFeatureOnline(Feature.FeatureName.NGN_PORTIERUNGSKENNUNG_FUNCTIONALITY_ENABLED)) {
            return loadCarrierKennungForRexMkViaOnkz(cbVorgang);
        } else {
            return loadCarrierKennungForRexMkViaCarrier(cbVorgang);
        }
    }

    private CarrierKennung loadCarrierKennungForRexMkViaOnkz(WitaCBVorgang cbVorgang) {
        final Collection<Rufnummer> rufnummern = loadRufnummern(cbVorgang);
        if (CollectionTools.isEmpty(rufnummern)) {
            throw new WitaDataAggregationException("Für REX-MK sind keine Rufnummern angegeben!");
        }

        final List<String> onkzList = rufnummern.stream()
                .map(rn -> rn.getOnKz())
                .filter(onkz -> StringUtils.isNotEmpty(onkz))
                .distinct()
                .collect(Collectors.toList());
        if (onkzList.size() != 1) {
            final String msg = String.format("Aufnehmender (aktueller) Carrier ONKZ [%s] konnte für die REX-MK Rufnummern "
                    + "nicht (oder nicht eindeutig) identifiziert werden!", onkzList.toString());
            throw new WitaDataAggregationException(msg);
        }
        final String onkz = onkzList.get(0);

        try {
            final String tnbKennung = rufnummerService.findTnbKennung4Onkz(onkz);
            if (StringUtils.isEmpty(tnbKennung)) {
                final String msg = String.format("Aufnehmender (aktueller) Carrier TNB konnte für die REX-MK Rufnummern ONKZ [%s]"
                        + "nicht (oder nicht eindeutig) identifiziert werden!", onkz);
                throw new WitaDataAggregationException(msg);

            }

            final TNB tnb = TNB.fromTnbKennung(tnbKennung);
            if (tnb == null) {
                final String msg = String.format("Aufnehmender (aktueller) Carrier TNB konnte für die TNB Kennung [%s]"
                        + "nicht identifiziert werden!", tnbKennung);
                throw new WitaDataAggregationException(msg);
            }

            final DNTNB dntnb = rufnummerService.findTNB(tnb.carrierName);
            if (dntnb == null) {
                final String msg = String.format("Aufnehmender (aktueller) Carrier DNTNB konnte für die carrier [%s]"
                        + "nicht identifiziert werden!", tnb.carrierName);
                throw new WitaDataAggregationException(msg);

            }
            return carrierService.findCarrierKennung(dntnb.getPortKennung());
        }
        catch (FindException e) {
            LOGGER.error(e.getMessage(), e);
            throw new WitaDataAggregationException(
                    "CarrierKennung für die REX-MK Bestellung konnte nicht ermittelt werden!", e);
        }

    }

    /**
     * Old way to get {@link CarrierKennung} via actCarrier
     * Does not work after carrier migration to NGN
     */
    @Deprecated
    private CarrierKennung loadCarrierKennungForRexMkViaCarrier(WitaCBVorgang cbVorgang) {
        Collection<Rufnummer> rufnummern = loadRufnummern(cbVorgang);
        if (CollectionTools.isEmpty(rufnummern)) {
            throw new WitaDataAggregationException(
                    "Für REX-MK sind keine Rufnummern angegeben!");
        }

        Set<String> actCarrier = new HashSet<>();
        for (Rufnummer dn : rufnummern) {
            if (StringUtils.isNotBlank(dn.getActCarrier())) {
                actCarrier.add(dn.getActCarrier());
            }
        }

        if ((actCarrier.isEmpty()) || (actCarrier.size() > 1)) {
            throw new WitaDataAggregationException(
                    "Aufnehmender (aktueller) Carrier konnte für die REX-MK Rufnummern nicht (oder nicht eindeutig) identifiziert werden!");
        }

        try {
            // CarrierKennung ueber actCarrier laden
            DNTNB dnTnb = rufnummerService.findTNB(actCarrier.iterator().next());
            return carrierService.findCarrierKennung(dnTnb.getPortKennung());
        }
        catch (FindException e) {
            LOGGER.error(e.getMessage(), e);
            throw new WitaDataAggregationException(
                    "CarrierKennung für die REX-MK Bestellung konnte nicht ermittelt werden!", e);
        }
    }

    public Collection<Rufnummer> loadRufnummern(WitaCBVorgang cbVorgang) throws WitaDataAggregationException {
        Set<Long> dnNoOrigs = cbVorgang.getRufnummerIds();
        Set<Rufnummer> foundRufnummern = newHashSet();
        try {
            if (CollectionTools.isNotEmpty(dnNoOrigs)) {
                for (Long dnNoOrig : dnNoOrigs) {
                    Rufnummer lastRN = rufnummerService.findLastRN(dnNoOrig);
                    if (lastRN == null) {
                        throw new WitaDataAggregationException(String.format(
                                "Fehler beim Laden der Rufnummern! Rufnummer mit Id %s konnte nicht geladen werden!", dnNoOrig));
                    }
                    foundRufnummern.add(lastRN);
                }
            }
            return foundRufnummern;
        }
        catch (FindException e) {
            throw new WitaDataAggregationException("Fehler beim Laden der Rufnummern!", e);
        }
    }

    /**
     * Ermittelt ONKZ des HVT-Standorts der angegeben Carrierbestellung
     */
    public String loadHVTStandortOnkz4Cb(Long cbId) {
        Long hvtStandort;
        String hvtStandortOnkz = null;
        try {
            hvtStandort = carrierService.findHvtStdId4Cb(cbId);
            hvtStandortOnkz = hvtService.findHVTGruppe4Standort(hvtStandort).getOnkzWithoutLeadingNulls();
        }
        catch (FindException e) {
            // keine Data-Aggregation-Exception da HvtStandort-ONKZ nicht unbedingt beoetigt wird
            LOGGER.error(e.getMessage(), e);
        }
        return hvtStandortOnkz;
    }


    /**
     * Ermittelt ONKZ des HVT-Standorts der angegeben Carrierbestellung
     */
    public String loadHVTStandortOnkz4Auftrag(Long auftragId, String esTyp) {
        String hvtStandortOnkz = null;
        try {
            Endstelle endstelle = endstellenService.findEndstelle4Auftrag(auftragId, esTyp);
            Long hvtIdStandort = endstelle.getHvtIdStandort();
            hvtStandortOnkz = hvtService.findHVTGruppe4Standort(hvtIdStandort).getOnkzWithoutLeadingNulls();
        }
        catch (FindException e) {
            // keine Data-Aggregation-Exception da HvtStandort-ONKZ nicht unbedingt beoetigt wird
            LOGGER.error(e.getMessage(), e);
        }
        return hvtStandortOnkz;
    }


    public Carrierbestellung getCarrierbestellungReferencedByCbVorgang(Long cbVorgangId) {
        WitaCBVorgang cbVorgang = witaTalOrderService.findCBVorgang(cbVorgangId);
        if (cbVorgang == null) {
            throw new WitaBaseException(String.format("WITA Vorgang mit Id %s konnte nicht ermittelt werden!", cbVorgangId));
        }

        try {
            return carrierService.findCB(cbVorgang.getCbId());
        }
        catch (Exception e) {
            throw new WitaBaseException(
                    String.format("Carrierbestellung zum WITA Vorgang mit Id %s konnte nicht ermittelt werden!", cbVorgangId));
        }
    }


    /**
     * Ermittelt zu dem angegebenen WITA Auftrag sowie der WITA ABM das resultierende TAL Zeitfenster. <br/>
     * Dabei wird das urspruenglich von M-net angeforderte Zeitfenster zusammen mit den ABM Melde-Codes ausgewertet
     * und auf das Carrier-unabhaengige {@link de.augustakom.hurrican.model.cc.tal.TalRealisierungsZeitfenster}
     * gemappt. <br/>
     * Falls kein Zeitfenster ermittelt werden kann wird {@link de.augustakom.hurrican.model.cc.tal.TalRealisierungsZeitfenster#VORMITTAG}
     * als Default zurueck geliefert.
     * @param witaRequest
     * @param abm
     * @return
     */
    public @NotNull TalRealisierungsZeitfenster transformWitaZeitfenster(MnetWitaRequest witaRequest, AuftragsBestaetigungsMeldung abm) {
        if (witaRequest != null) {
            Kundenwunschtermin.Zeitfenster witaZF = witaRequest.getGeschaeftsfall().getKundenwunschtermin().getZeitfenster();
            switch (witaZF) {
                case SLOT_2:
                    return TalRealisierungsZeitfenster.VORMITTAG;
                case SLOT_9:
                    if (abm != null) {
                        Optional<MeldungsPositionWithAnsprechpartner> pos = abm.getMeldungsPositionen().stream()
                                .filter(mp -> AbmMeldungsCode.CUSTOMER_REQUIRED.meldungsCode.equals(mp.getMeldungsCode()))
                                .findFirst();
                        if (pos.isPresent()) {
                            return TalRealisierungsZeitfenster.VORMITTAG;
                        }
                    }
                    return TalRealisierungsZeitfenster.GANZTAGS;
                case SLOT_7:
                    return TalRealisierungsZeitfenster.NACHMITTAG;
                default:
                    return TalRealisierungsZeitfenster.VORMITTAG;
            }
        }


        return TalRealisierungsZeitfenster.VORMITTAG;
    }

}
