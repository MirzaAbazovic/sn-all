/*
 * Copyright (c) 2010 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 24.08.2010
 */
package de.augustakom.hurrican.service.cc.impl;

import java.util.*;
import javax.annotation.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.log4j.Logger;
import org.springframework.util.NumberUtils;

import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.common.tools.lang.NumberTools;
import de.augustakom.common.tools.lang.Pair;
import de.augustakom.hurrican.annotation.CcTxRequiredReadOnly;
import de.augustakom.hurrican.model.billing.Adresse;
import de.augustakom.hurrican.model.billing.BAuftrag;
import de.augustakom.hurrican.model.billing.Device;
import de.augustakom.hurrican.model.billing.DeviceFritzBox;
import de.augustakom.hurrican.model.billing.Kunde;
import de.augustakom.hurrican.model.cc.Auftrag;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.AuftragStatus;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.cc.Equipment;
import de.augustakom.hurrican.model.cc.Produkt;
import de.augustakom.hurrican.model.cc.Rangierung;
import de.augustakom.hurrican.model.cc.Rangierung.Freigegeben;
import de.augustakom.hurrican.model.cc.VerbindungsBezeichnung;
import de.augustakom.hurrican.model.cc.Verlauf;
import de.augustakom.hurrican.model.cc.hardware.HWBaugruppe;
import de.augustakom.hurrican.model.cc.hardware.HWRack;
import de.augustakom.hurrican.model.cc.view.FTTXKundendatenView;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.billing.BillingAuftragService;
import de.augustakom.hurrican.service.billing.DeviceService;
import de.augustakom.hurrican.service.billing.KundenService;
import de.augustakom.hurrican.service.cc.BAService;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.FTTXInfoService;
import de.augustakom.hurrican.service.cc.HWService;
import de.augustakom.hurrican.service.cc.PhysikService;
import de.augustakom.hurrican.service.cc.ProduktService;
import de.augustakom.hurrican.service.cc.RangierungsService;

/**
 * Implementierung von <code>FTTXInfoService</code>
 */
@CcTxRequiredReadOnly
public class FTTXInfoServiceImpl extends DefaultCCService implements FTTXInfoService {

    private static final Logger LOGGER = Logger.getLogger(FTTXInfoServiceImpl.class);

    protected enum RangAuftrag {
        inBetrieb, inAenderung, inRealisierung, inProjektierung, inKuendigung, inErfassung, gekuendigt, nil
    }

    @Resource(name = "de.augustakom.hurrican.service.cc.BAService")
    private BAService baService;
    @Resource(name = "de.augustakom.hurrican.service.cc.ProduktService")
    private ProduktService produktService;
    @Resource(name = "de.augustakom.hurrican.service.cc.PhysikService")
    private PhysikService physikService;
    @Resource(name = "de.augustakom.hurrican.service.cc.HWService")
    private HWService hwService;
    @Resource(name = "de.augustakom.hurrican.service.cc.RangierungsService")
    private RangierungsService rangierungsService;
    @Resource(name = "de.augustakom.hurrican.service.cc.CCAuftragService")
    private CCAuftragService auftragService;
    @Resource(name = "de.augustakom.hurrican.service.billing.KundenService")
    private KundenService kundenService;
    @Resource(name = "de.augustakom.hurrican.service.billing.BillingAuftragService")
    private BillingAuftragService billingAuftragService;
    @Resource(name = "de.augustakom.hurrican.service.billing.DeviceService")
    private DeviceService deviceService;

    /**
     * FTTB und FTTH
     * <ul>
     *     <li>Port belegt und aktiver Auftrag: Antwort siehe OK-Fall mit Auftrags- und Kundendaten
     *     <li>Port frei bzw. kein aktiver Auftrag: Antwort siehe OK-Fall ohne Auftrags- und Kundendaten
     *     <li>Port nicht gefunden: Antwort siehe Fehler-Fall
     * </ul>
     */
    @Override
    public void getKundendaten4Command(FTTXKundendatenView view) throws FindException {
        Date requestDate = (view.getDatum() != null) ? DateUtils.truncate(view.getDatum(), Calendar.DAY_OF_MONTH) : null;
        Date now = DateUtils.truncate(new Date(), Calendar.DAY_OF_MONTH);
        if ((requestDate != null)
                && DateTools.isBefore(requestDate, now)) {
            throw new FindException("Das im Request angegebene Datum darf nicht in der Vergangenheit liegen.");
        }

        try {
            // Preconditions
            if (StringUtils.isBlank(view.getPort())) {
                throw new FindException("Portbezeichner (HWEQN) fehlt!.");
            }

            // Rangierung ermitteln
            String geraetebezeichnung = view.getGeraetebezeichnung();
            Equipment eqIn = findPort(geraetebezeichnung, view.getPort());
            if (eqIn == null) {
                throw new FindException(String.format("Der Port für die Gerätebezeichnung '%s' konnte nicht " +
                        "ermittelt werden.", geraetebezeichnung));
            }
            Rangierung rang = rangierungsService.findRangierung4Equipment(eqIn.getId(), Boolean.TRUE);
            if (rang == null) {
                throw new FindException("Rangierung konnte nicht ermittelt werden.");
            }

            // Nach Datum filtern und den am besten passenden Auftrag ziehen
            List<AuftragDaten> auftragDaten = findAndFilterAuftragDaten(now, requestDate, rang.getEqInId());
            AuftragDaten ad = getBestMatch(auftragDaten);
            // Auftrag ermitteln
            if (ad != null) {
                Auftrag auftrag = auftragService.findAuftragById(ad.getAuftragId());
                if (auftrag == null) {
                    throw new FindException(String.format("Zu den Auftragsdaten [AuftragId=%d] konnte die Auftrag " +
                            "Entitaet nicht ermittelt werden!", ad.getAuftragId()));
                }
                // Auftrag verfügbar
                loadOrderData(view, ad, auftrag);
            }

            // Portstatus
            view.setPortstatus(getPortstatus(rang, ad, now, requestDate));
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(e.getMessage(), e);
        }
    }

    /**
     * Ermittelt den Port-Status an Hand des Auftrags und der Datumswerte.
     *
     * @param rang
     * @param ad
     * @param now
     * @param datum
     * @return
     * @throws FindException
     */
    String getPortstatus(Rangierung rang, AuftragDaten ad, Date now, Date datum) throws FindException {
        boolean auftragAktiv = (ad != null);
        FttxPortStatus portstatus = FttxPortStatus.unbekannt;

        if (rang.getFreigegeben() == Freigegeben.defekt) {
            portstatus = FttxPortStatus.defekt;
        }
        else if ((rang.getFreigegeben() != Freigegeben.freigegeben)
                && (rang.getFreigegeben() != Freigegeben.defekt)) {
            portstatus = FttxPortStatus.gesperrt;
        }
        else if (auftragAktiv) {
            portstatus = FttxPortStatus.belegt;
        }
        else if (!auftragAktiv) {
            if (rang.getEsId() == null) {
                portstatus = FttxPortStatus.frei;
            }
            else if (NumberTools.equal(rang.getEsId(), Rangierung.RANGIERUNG_NOT_ACTIVE)) {
                portstatus = FttxPortStatus.freigabebereit;
            }
            else {
                portstatus = getPortStatusReserviert(rang, now, datum);
            }
        }
        return portstatus.name();
    }

    private FttxPortStatus getPortStatusReserviert(Rangierung rang, Date now, Date datum) {
        FttxPortStatus portstatus;
        if ((datum != null) && DateTools.isAfter(datum, now)) {
            // 'datum' liegt in der Zukunft
            List<AuftragDaten> auftragDaten = findAndFilterAuftragDaten(now, now, rang.getEqInId());
            AuftragDaten adBestMatch = getBestMatch(auftragDaten);
            if (adBestMatch == null) {
                // Port ist jetzt reserviert, vermutlich also auch in Zukunft
                portstatus = FttxPortStatus.reserviert;
            }
            else {
                // Port ist jetzt belegt, zum Zeitpunkt 'datum' aber nicht mehr ->Port vermutlich freigabe bereit
                portstatus = FttxPortStatus.freigabebereit;
            }
        }
        else {
            // 'datum' liegt nicht in der Zukunft
            portstatus = FttxPortStatus.reserviert;
        }
        return portstatus;
    }

    protected AuftragDaten getBestMatch(List<AuftragDaten> auftragDaten) {
        if (CollectionTools.isEmpty(auftragDaten)) { return null; }

        RangAuftrag rangBestMatch = RangAuftrag.nil;
        AuftragDaten bestMatch = null;
        for (AuftragDaten ad : auftragDaten) {
            RangAuftrag rang = getRangAuftrag(ad);

            // Ermittelten Rang mit bisher bestem Ergebnis vergleichen
            if ((rang != RangAuftrag.nil) && (rang.ordinal() < rangBestMatch.ordinal())) {
                bestMatch = ad;
                rangBestMatch = rang;
            }
            if (rangBestMatch == RangAuftrag.inBetrieb) {
                // Besser wird's nicht mehr
                break;
            }
        }
        return bestMatch;
    }

    protected RangAuftrag getRangAuftrag(AuftragDaten ad) {
        if (ad.isCancelled()) {
            return RangAuftrag.gekuendigt;
        }
        else if (ad.isInErfassung()) {
            return RangAuftrag.inErfassung;
        }
        else if (ad.isInKuendigung()) {
            return RangAuftrag.inKuendigung;
        }
        else if (ad.isInProjektierung()) {
            return RangAuftrag.inProjektierung;
        }
        else if (ad.isInAenderung()) {
            return RangAuftrag.inAenderung;
        }
        else if (ad.isInRealisierung()) {
            return RangAuftrag.inRealisierung;
        }
        else if (ad.isInBetrieb()) {
            return RangAuftrag.inBetrieb;
        }
        return RangAuftrag.nil;
    }

    /**
     * Ermittelt alle AuftragDaten zu einem Equipment und jeweils ein Anfang - Ende Intervall. Zurückgeliefert werden
     * nur die AuftragDaten, deren Intervall mit {@code datum} matcht, also {@code datum} im Intervall liegt.
     *
     * @return die Liste der AuftragsDaten oder {@code null}
     */
    private List<AuftragDaten> findAndFilterAuftragDaten(Date now, Date datum, Long eqId) {
        if (eqId == null) { return null; }

        try {
            // Pool aller relevanten Aufträge ermitteln
            List<AuftragDaten> auftragDatenAll = auftragService.findAuftragDatenByEquipment(eqId);

            // Validieren
            if (CollectionTools.isNotEmpty(auftragDatenAll)) {
                List<AuftragDaten> auftragDatenValid = new ArrayList<>(auftragDatenAll.size());
                // Folgende Aufträge ausschließen:
                // - stornierte Aufträge
                // - abgesagte Aufträge
                // - konsolidierte Aufträge
                for (AuftragDaten ad : auftragDatenAll) {
                    if (!AuftragStatus.isNotValid(ad.getStatusId())) {
                        auftragDatenValid.add(ad);
                    }
                }

                // Filtern
                if (CollectionTools.isNotEmpty(auftragDatenValid)) {
                    List<AuftragDaten> auftragDatenFiltered = new ArrayList<>(auftragDatenValid.size());
                    Date match = (datum != null) ? datum : now;

                    // Intervalle gegen Datum(match) prüfen
                    for (AuftragDaten ad : auftragDatenValid) {
                        Pair<Date, Date> interval = getInterval4AuftragDaten(ad);
                        if ((interval == null) || ((interval.getFirst() == null) && (interval.getSecond() == null))) {
                            // Es konnte keine sinnvolle Zeitspanne ermittelt werden
                            continue;
                        }
                        else if ((interval.getFirst() != null) && (interval.getSecond() != null)) {
                            // Zeitspanne hat einen Anfang und ein Ende
                            if (DateTools.isDateAfterOrEqual(match, interval.getFirst())
                                    && DateTools.isDateBeforeOrEqual(match, interval.getSecond())) {
                                auftragDatenFiltered.add(ad);
                            }
                        }
                        else if (interval.getFirst() != null
                                && DateTools.isDateAfterOrEqual(match, interval.getFirst())) {
                            // Zeitspanne hat einen Anfang
                            auftragDatenFiltered.add(ad);
                        }
                        else if (interval.getSecond() != null
                                && DateTools.isDateBeforeOrEqual(match, interval.getSecond())) {
                            // Zeitspanne hat ein Ende
                            // Annahme: Anfang liegt maximal in der Vergangenheit -> match liegt in Zeitspanne
                            auftragDatenFiltered.add(ad);
                        }
                    }
                    return auftragDatenFiltered;
                }
            }
        }
        catch (FindException e) {
            // Es konnten keine AuftragDaten ermittelt werden
            LOGGER.error(e.getMessage(), e);
        }
        return null;
    }

    /**
     * Ermittelt Zeitspanne zu einem Auftrag. <lu> <li>Inbetriebnahmedatum = Anfang(first), Kündigungsdatum =
     * Ende(second) <li>falls Inbetriebnahme {@code null}: Realisierungstermin von aktivem Bauauftrag <li>falls
     * Realisierungstermin {@code null}: Vorgabe AM Datum </lu>
     *
     * @param ad
     * @return
     */
    private Pair<Date, Date> getInterval4AuftragDaten(AuftragDaten ad) {
        Date first = ad.getInbetriebnahme();
        Date second = ad.getKuendigung();

        if (first == null) {
            //  Realisierungstermin von aktivem Bauauftrag ermitteln
            try {
                Verlauf actVerlauf = baService.findActVerlauf4Auftrag(ad.getAuftragId(), false);
                first = (actVerlauf != null) ? actVerlauf.getRealisierungstermin() : null;
            }
            catch (FindException e) {
                // Exception ignorieren
                LOGGER.warn(e.getMessage(), e);
            }

            if (first == null) {
                first = ad.getVorgabeSCV();
            }
        }

        first = (first != null) ? DateUtils.truncate(first, Calendar.DAY_OF_MONTH) : null;
        second = (second != null) ? DateUtils.truncate(second, Calendar.DAY_OF_MONTH) : null;
        return new Pair<>(first, second);
    }

    /**
     * Liefert das Equipment/den Port einer MDU
     *
     * @return Equipment oder null
     */
    private Equipment findPort(final String geraetebezeichnung, final String port)
            throws FindException {
        if (geraetebezeichnung == null) {
            return null;
        }

        HWRack rack = hwService.findActiveRackByBezeichnung(geraetebezeichnung);
        if (rack == null) {
            throw new FindException(String.format("Ein Gerät mit der Bezeichnung '%s' konnte nicht " +
                    "gefunden werden!", geraetebezeichnung));
        }

        List<HWBaugruppe> mduBaugruppen = hwService.findBaugruppen4Rack(rack.getId());
        if (CollectionTools.isEmpty(mduBaugruppen)) {
            throw new FindException(String.format("Das Gerät '%s' besitzt keine Baugruppen und somit keinen Port!",
                    geraetebezeichnung));
        }

        for (HWBaugruppe hwBaugruppe : mduBaugruppen) {
            try {
                Equipment eq = rangierungsService.findEquipmentByBaugruppe(hwBaugruppe.getId(), port, null);
                if (eq != null) {
                    return eq;
                }
            }
            catch (FindException e) {
                LOGGER.info(e.getMessage());
                // Exception fangen und naechste Baugruppe testen
            }
        }

        return null;
    }

    /**
     * Ermittelt Auftragsdaten und uebergibt die notwendigen Werte der View {@code view}.
     */
    private void loadOrderData(FTTXKundendatenView view, AuftragDaten ad, Auftrag auftrag) throws FindException, IllegalArgumentException {
        // Standort-Adresse
        Adresse adr = billingAuftragService.findAnschlussAdresse4Auftrag(ad.getAuftragNoOrig(), Endstelle.ENDSTELLEN_TYP_B);
        if (adr == null) {
            throw new FindException("Standort-Adresse zu Auftrag " + ad.getAuftragId() + " kann nicht ermittelt werden.");
        }
        view.setEsAnrede(adr.getAnrede());
        view.setEsName(adr.getName());
        view.setEsVorname(adr.getVorname());

        BAuftrag bauftrag = billingAuftragService.findAuftrag(ad.getAuftragNoOrig());
        if (bauftrag != null) {
            view.setEsTelefon(bauftrag.getBearbeiterKundeRN());
            view.setEsMail(bauftrag.getBearbeiterKundeEmail());
            view.setAnschlussDoseLage(adr.getFloor());
        }

        // Kunden-Adresse
        adr = kundenService.getAdresse4Kunde(auftrag.getKundeNo());
        if (adr == null) {
            throw new FindException("Kunden-Adresse zu Auftrag " + ad.getAuftragId() + " kann nicht ermittelt werden.");
        }
        view.setKdAnrede(adr.getAnrede());
        view.setKdName(adr.getName());
        view.setKdVorname(adr.getVorname());

        Kunde kd = kundenService.findKunde(auftrag.getKundeNo());
        if (kd != null) {
            view.setKdTelefon(kd.getHauptRufnummer());
            view.setKdMobil(kd.getRnMobile());
            view.setKdMail(kd.getEmail());
        }

        // Endgeraet ermitteln
        List<Device> devices = deviceService.findDevices4Auftrag(ad.getAuftragNoOrig(),
                Device.PROV_SYSTEM_HURRICAN, Device.DEVICE_CLASS_IAD);
        CollectionUtils.filter(devices, new Predicate() {
            @Override
            public boolean evaluate(Object obj) {
                return ((Device) obj).isValid(new Date());
            }
        });

        if (CollectionTools.isNotEmpty(devices)) {
            Device dev = devices.get(0);

            if (dev != null) {
                view.setEgTyp(dev.getTechName());
                view.setEgSeriennummer(dev.getSerialNumber());

                if (StringUtils.equals(dev.getDeviceExtension(), Device.DEVICE_EXTENSION_FRITZBOX)) {
                    DeviceFritzBox fritzBox = deviceService.findDeviceFritzBox(dev.getDevNo());

                    if (fritzBox != null) {
                        view.setEgCwmpId(fritzBox.getCwmpId());
                    }
                }
            }
        }

        // Auftrag Daten
        view.setAuftragsnummerHurrican((auftrag.getId() != null) ?
                NumberUtils.convertNumberToTargetClass(auftrag.getId(), Long.class) : null);
        view.setAuftragsnummerTaifun((ad.getAuftragNoOrig() != null) ?
                ad.getAuftragNoOrig() : null);

        // VerbindungsBezeichnung laden
        VerbindungsBezeichnung vbz = physikService.findVerbindungsBezeichnungByAuftragId(auftrag.getId());
        if (vbz == null) {
            throw new FindException("Verbindungsbezeichnung zu Auftrag " + auftrag.getId() + " kann nicht ermittelt werden.");
        }
        view.setVbz(vbz.getVbz());

        // Produkt laden
        Produkt produkt = produktService.findProdukt(ad.getProdId());
        if (produkt == null) {
            throw new FindException("Das Produkt zu Auftrag " + auftrag.getId() + " kann nicht ermittelt werden.");
        }
        view.setProdukt(produkt.getAnschlussart());

        // Auftragstatus
        AuftragStatus auftragStatus = auftragService.findAuftragStatus(ad.getStatusId());
        if (auftragStatus == null) {
            throw new FindException("Der Auftragstatus zu Auftrag " + auftrag.getId() + " kann nicht ermittelt werden.");
        }
        view.setAuftragsstatus(auftragStatus.getStatusText());
    }

}
