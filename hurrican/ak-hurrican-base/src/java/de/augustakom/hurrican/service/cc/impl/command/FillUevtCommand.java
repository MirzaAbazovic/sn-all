/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 01.04.2005 09:28:48
 */
package de.augustakom.hurrican.service.cc.impl.command;

import java.util.*;
import javax.annotation.*;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import de.augustakom.common.service.exceptions.ServiceCommandException;
import de.augustakom.common.service.iface.IServiceCallback;
import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.hurrican.annotation.CcTxRequired;
import de.augustakom.hurrican.dao.cc.EquipmentDAO;
import de.augustakom.hurrican.dao.cc.HVTBestellungDAO;
import de.augustakom.hurrican.model.cc.AbstractCCIDModel;
import de.augustakom.hurrican.model.cc.EqStatus;
import de.augustakom.hurrican.model.cc.EqVerwendung;
import de.augustakom.hurrican.model.cc.Equipment;
import de.augustakom.hurrican.model.cc.HVTBestellHistory;
import de.augustakom.hurrican.model.cc.HVTBestellung;
import de.augustakom.hurrican.model.cc.HVTGruppe;
import de.augustakom.hurrican.model.cc.HVTStandort;
import de.augustakom.hurrican.model.cc.RangSchnittstelle;
import de.augustakom.hurrican.model.cc.UEVT;
import de.augustakom.hurrican.model.cc.view.EquipmentBelegungView;
import de.augustakom.hurrican.service.base.exceptions.HurricanServiceCommandException;
import de.augustakom.hurrican.service.cc.HVTService;
import de.augustakom.hurrican.service.cc.HVTToolService;
import de.augustakom.hurrican.service.cc.HWSwitchService;


/**
 * Command-Klasse, um Stifte fuer einen UEVT zu vergeben.
 *
 *
 */
@CcTxRequired
public class FillUevtCommand extends AbstractServiceCommand {

    private static final Logger LOGGER = Logger.getLogger(FillUevtCommand.class);

    /**
     * Key fuer die prepare-Methode, um dem Command die ID der zu verwendenden HVT-Bestellung zu uebergeben.
     */
    public static final String HVT_BESTELLUNG_ID = "hvt.bestellung.id";
    /**
     * Key fuer die prepare-Methode, um dem Command die Bezeichnung der Leiste zu uebergeben, die aufgefuellt werden
     * soll.
     */
    public static final String UEVT_LEISTE1 = "uevt.leiste1";
    /**
     * Key fuer die prepare-Methode, um dem Command die KVZ-Nummer zu uebergeben (optional).
     */
    public static final String KVZ_NUMMER = "kvz.nummer";
    /**
     * Key fuer die prepare-Methode, um dem Command die ÜVt-Cluster-Nr zu übergeben (required).
     */
    public static final String UEVT_CLUSTER_NO = "uevt.cluster.no";
    /**
     * Key fuer die prepare-Methode, um dem Command den Benutzernamen mitzuteilen.
     */
    public static final String USER_NAME = "user.name";
    /**
     * Key fuer die prepare-Methode, um dem Command mitzuteilen, ob eine neue Leiste angelegt werden soll.
     */
    public static final String CREATE_LEISTE = "create.leiste";
    /**
     * Key fuer die prepare-Methode, um dem Command die Anzahl der Stifte auf einer neuen Leiste zu uebergeben.
     */
    public static final String CREATE_STIFTE = "create.stifte";
    /**
     * Key fuer die prepare-Methode, um dem Command ein Objekt vom Typ <code>ServiceCallback</code> zu uebergeben.
     */
    public static final String SERVICE_CALLBACK = "service.callback";

    private HVTBestellungDAO hvtBestellungDAO = null;
    private EquipmentDAO equipmentDAO = null;

    @Resource(name = "de.augustakom.hurrican.service.cc.HWSwitchService")
    private HWSwitchService hwSwitchService;

    // Modelle
    private IServiceCallback serviceCallback = null;
    private boolean createLeiste = false;
    private String user = null;
    private HVTBestellung hvtBestellung = null;
    private UEVT uevt = null;
    private String leiste1 = null;
    private String kvzNummer = null;
    private Integer uevtClusterNo = null;
    private EquipmentBelegungView eqBelegung = null;
    private List<Equipment> createdEQs = null;
    private List<Equipment> existingEquipments = null;
    private HVTGruppe hvtGruppe = null;

    // sonstiges
    private int availableSum = -1;   // Anzahl der noch verfuegbaren Stifte der HVT-Bestellung
    private int anzahl4Vergabe = -1; // Anzahl der einzuspielenden Stifte
    private int maxKvzDA = 0;       // hoechste bestehender Wert fuer KVZ Doppelader

    /**
     * @see de.augustakom.hurrican.service.cc.impl.command.AbstractServiceCommand#execute()
     */
    @Override
    public Object execute() throws Exception {
        checkValues();
        checkHVTBestellung();
        calculateStifte4Vergabe();
        findMaxKvzDA();
        stifteVergeben();

        return createdEQs;
    }

    /* Prueft, ob die HVT-Bestellung verwendet werden kann. */
    private void checkHVTBestellung() throws HurricanServiceCommandException {
        if (hvtBestellung.getBereitgestellt() != null) {
            throw new HurricanServiceCommandException("HVT-Bestellung ist bereits abgeschlossen. Stiftvergabe nicht möglich!");
        }

        if ((hvtBestellung.getAnzahlCuDA() == null) || (hvtBestellung.getAnzahlCuDA() <= 0)) {
            throw new HurricanServiceCommandException("Die CuDA-Anzahl der HVT-Bestellung ist ungültig! Anzahl: " +
                    hvtBestellung.getAnzahlCuDA());
        }

        if (!StringUtils.equalsIgnoreCase(hvtBestellung.getPhysiktyp(), HVTBestellung.PHYSIKTYP_N) &&
                !StringUtils.equalsIgnoreCase(hvtBestellung.getPhysiktyp(), HVTBestellung.PHYSIKTYP_H)) {
            throw new HurricanServiceCommandException("Der Physiktyp der HVT-Bestellung ist ungültig!");
        }

        try {
            // Anzahl der noch verfuegbaren Stifte der HVT-Bestellung ermitteln
            int sumVergeben = hvtBestellungDAO.getCuDACount4Bestellung(hvtBestellung.getId());
            availableSum = hvtBestellung.getAnzahlCuDA() - sumVergeben;
            if (availableSum <= 0) {
                throw new HurricanServiceCommandException("HVT-Bestellung ist bereits komplett eingespielt!");
            }
        }
        catch (HurricanServiceCommandException e) {
            throw e;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new HurricanServiceCommandException("Bei der Ermittlung der noch verfuegbaren Stifte " +
                    "ist ein Fehler aufgetreten. Stifte wurden nicht eingespielt!", e);
        }
    }

    /* Ermittelt die Anzahl der Stifte, die in die Leiste noch eingespielt werden koennen. */
    private void calculateStifte4Vergabe() throws ServiceCommandException {
        try {
            if (createLeiste) {
                eqBelegung = new EquipmentBelegungView();
                eqBelegung.setPhysiktyp(hvtBestellung.getPhysiktyp());
                eqBelegung.setUevt(uevt.getUevt());
                eqBelegung.setHvtIdStandort(uevt.getHvtIdStandort());
                eqBelegung.setSwitchAK((hvtGruppe != null && hvtGruppe.getHwSwitch() != null) ? hvtGruppe.getHwSwitch().getName() : "?");
                eqBelegung.setLeiste1(leiste1);
                eqBelegung.setStiftMax("00");
            }
            else {
                // Ermittlung der bereits existierenden Ports fuer die Leiste
                Equipment example = new Equipment();
                example.setHvtIdStandort(uevt.getHvtIdStandort());
                example.setRangVerteiler(uevt.getUevt());
                example.setKvzNummer(kvzNummer);
                example.setRangLeiste1(leiste1);
                existingEquipments = equipmentDAO.findEquipments(example, new String[] { AbstractCCIDModel.ID });

                List<EquipmentBelegungView> views = hvtBestellungDAO.findEQs4Uevt(uevt.getId(), leiste1);
                if (!views.isEmpty()) {
                    eqBelegung = views.get(0);
                }
                else {
                    throw new HurricanServiceCommandException("Die aktuelle Belegung der Leiste konnte nicht " +
                            "ermittelt werden! Stifte wurden nicht eingespielt!");
                }

                if (!StringUtils.equals(eqBelegung.getPhysiktyp(), hvtBestellung.getPhysiktyp())) {
                    throw new HurricanServiceCommandException("Die Physiktypen der Leiste und der HVT-Bestellung " +
                            "sind unterschiedlich. Stifte wurden nicht eingespielt!");
                }

                hvtBestellungDAO.loadStifte(eqBelegung);
            }

            int stifte4Leiste = eqBelegung.getStifteGesamt() != null ? eqBelegung.getStifteGesamt() : 0;
            final int maxStifte = getMaxStifte();

            if (stifte4Leiste >= maxStifte) {
                throw new HurricanServiceCommandException("Die Leiste ist bereits voll! Stifte " +
                        "wurden nicht eingespielt!");
            }

            int tmpCount = maxStifte - stifte4Leiste;
            if (tmpCount > availableSum) {
                anzahl4Vergabe = availableSum;
            }
            else {
                anzahl4Vergabe = tmpCount;
            }

            if (anzahl4Vergabe <= 0) {
                throw new HurricanServiceCommandException("In die Leiste können keine Stifte mehr eingspielt werden!");
            }
        }
        catch (ServiceCommandException e) {
            throw e;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new HurricanServiceCommandException("Die aktuelle Belegung der Leiste " + leiste1 +
                    " konnte nicht ermittelt werden! Stifte wurden nicht eingespielt!");
        }
    }

    private int getMaxStifte() {
        int maxStifte = -1;
        try {
            maxStifte = (Integer) getPreparedValue(CREATE_STIFTE);
        }
        catch (Exception e) {
            // ignore
        }
        if (maxStifte == -1) {
            maxStifte = (StringUtils.equals(eqBelegung.getPhysiktyp(), HVTBestellung.PHYSIKTYP_H))
                    ? Equipment.MAX_EQUIPMENT_COUNT_4_H : Equipment.MAX_EQUIPMENT_COUNT_4_N;
            if ((maxStifte == Equipment.MAX_EQUIPMENT_COUNT_4_H) && StringUtils.isNotBlank(kvzNummer)) {
                maxStifte = Equipment.MAX_EQUIPMENT_COUNT_4_H_KVZ;
            }
        }
        return maxStifte;
    }

    /* Ermittelt die hoechste bestehende KVZ Doppelader zu der Kombination Standort / KVZ-Nummer */
    private void findMaxKvzDA() throws HurricanServiceCommandException {
        try {
            if (kvzNummer != null) {
                String highestKvzDA = hvtBestellungDAO.getHighestKvzDA(uevt.getHvtIdStandort(), kvzNummer);
                if (StringUtils.isNotBlank(highestKvzDA)) {
                    maxKvzDA = Integer.parseInt(highestKvzDA);
                }
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new HurricanServiceCommandException("Fehler bei der Ermittlung der aktuell hoechsten KVZ Doppelader: " + e.getMessage(), e);
        }
    }

    /**
     * Spielt die Stifte aus der HVT-Bestellung in die UEVT-Leiste ein. (Falls auf der Leiste schon Ports vorhanden
     * sind, diese aber 'Luecken' aufweisen, werden diese Luecken aufgefuellt. Beispiel: - vorhandene Ports:
     * 01,02,03,04,05,10,11,12,13...,60 - 24 Ports werden eingespielt - Ports, die angelegt werden:
     * 06,07,08,09,61,62,63,...,80
     */
    private void stifteVergeben() throws ServiceCommandException {
        if (!infoCallback(anzahl4Vergabe, (availableSum - anzahl4Vergabe), leiste1)) {
            return;
        }

        try {
            createdEQs = new ArrayList<>();
            int counter = 0;
            int stift = 0;
            while (counter < anzahl4Vergabe) {
                stift++;
                String stiftBezeichnung = (stift <= 9) ? "0" + stift : "" + stift;

                if (!doesPortExist(stiftBezeichnung)) {
                    counter++;  // Counter nur erhoehen, wenn Port angelegt wurde!

                    Equipment newEQ = new Equipment();
                    newEQ.setHvtIdStandort(eqBelegung.getHvtIdStandort());
                    newEQ.setHwSwitch(hwSwitchService.findSwitchByName(eqBelegung.getSwitchAK()));
                    newEQ.setRangVerteiler(eqBelegung.getUevt());
                    newEQ.setRangBucht(eqBelegung.getUevt());
                    newEQ.setRangLeiste1(eqBelegung.getLeiste1());
                    newEQ.setRangStift1(stiftBezeichnung);
                    newEQ.setRangSchnittstelle(RangSchnittstelle.valueOf(hvtBestellung.getPhysiktyp()));
                    newEQ.setCarrier("DTAG");
                    newEQ.setStatus(EqStatus.frei);
                    newEQ.setUevtClusterNo(uevtClusterNo);
                    newEQ.setUserW(user);
                    newEQ.setDateW(new Date());

                    if (kvzNummer != null) {
                        newEQ.setKvzNummer(kvzNummer);
                        maxKvzDA++;
                        String kvzDA = String.format("%s", maxKvzDA);
                        kvzDA = StringUtils.leftPad(kvzDA, 4, '0');
                        newEQ.setKvzDoppelader(kvzDA);
                    }

                    if (hvtBestellung.getEqVerwendung() != null) {
                        newEQ.setVerwendung(hvtBestellung.getEqVerwendung());
                    }
                    else {
                        newEQ.setVerwendung(EqVerwendung.STANDARD);
                    }
                    if (StringUtils.equals(hvtBestellung.getPhysiktyp(), HVTBestellung.PHYSIKTYP_N)) {
                        newEQ.setRangSSType(Equipment.RANG_SS_2N);
                    }

                    equipmentDAO.store(newEQ);
                    createdEQs.add(newEQ);
                }
            }

            // Bestell-History erstellen
            HVTBestellHistory history = new HVTBestellHistory();
            history.setBestellId(hvtBestellung.getId());
            history.setBearbeiter(user);
            history.setAnzahl(anzahl4Vergabe);
            history.setLeiste(eqBelegung.getLeiste1());
            history.setDatum(new Date());
            hvtBestellungDAO.store(history);

            // HVTBestellung abschliessen, wenn alle Stifte eingespielt sind
            if ((availableSum - anzahl4Vergabe) == 0) {
                hvtBestellung.setBereitgestellt(new Date());
                hvtBestellungDAO.store(hvtBestellung);
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new HurricanServiceCommandException("Beim Einspielen der Stifte ist ein unerwarteter Fehler " +
                    "aufgetreten! Bitte kontrollieren.");
        }
    }

    private boolean doesPortExist(String stift1) {
        if (CollectionTools.isNotEmpty(existingEquipments)) {
            for (Equipment existingEq : existingEquipments) {
                if (StringUtils.equalsIgnoreCase(existingEq.getRangStift1(), stift1)) {
                    return true;
                }
            }
        }
        return false;
    }

    /* Ruft den ServiceCallback-Handler auf.
     * @param anzahl4Vergabe
     * @param anzahlOffen
     * @param leiste
     * @return true, wenn der Callback-Handler das Einspielen bestaetigt.
     */
    private boolean infoCallback(int anzahl4Vergabe, int anzahlOffen, String leiste) throws ServiceCommandException {
        Map<String, Object> params = new HashMap<>();
        params.put(HVTToolService.CALLBACK_PARAM_ANZAHL_STIFTE, anzahl4Vergabe);
        params.put(HVTToolService.CALLBACK_PARAM_ANZAHL_OFFEN, anzahlOffen);
        params.put(HVTToolService.CALLBACK_PARAM_LEISTE, leiste);

        Object result = serviceCallback.doServiceCallback(this, HVTToolService.CALLBACK_CONFIRM, params);
        if (result instanceof Boolean) {
            return (Boolean) result;
        }

        throw new HurricanServiceCommandException("Callback-Handler liefert nicht das erwartete Ergebnis! " +
                "Stifte wurden nicht eingespielt!\nCallback-Handler: " + serviceCallback.getClass().getName());
    }

    /* Ueberprueft, ob alle benoetigten Daten vorhanden sind. */
    private void checkValues() throws ServiceCommandException {
        Long hvtBestId = (Long) getPreparedValue(HVT_BESTELLUNG_ID);
        if (hvtBestId == null) {
            throw new HurricanServiceCommandException("Die zu verwendende HVT-Bestellung wurde nicht angegeben!");
        }

        try {
            HVTToolService hts = getCCService(HVTToolService.class);
            hvtBestellung = hts.findHVTBestellung(hvtBestId);
            if (hvtBestellung == null) {
                throw new HurricanServiceCommandException("Die HVT-Bestellung konnte nicht ermittelt werden!");
            }

            HVTService hs = getCCService(HVTService.class);
            uevt = hs.findUEVT(hvtBestellung.getUevtId());
            if ((uevt == null) || StringUtils.isBlank(uevt.getUevt())) {
                throw new HurricanServiceCommandException("UEVT zur HVT-Bestellung konnte nicht ermittelt werden!");
            }

            hvtGruppe = hs.findHVTGruppe4Standort(uevt.getHvtIdStandort());
            HVTStandort hvtStandort = hs.findHVTStandort(uevt.getHvtIdStandort());
            if (hvtStandort.isStandortType(HVTStandort.HVT_STANDORT_TYP_KVZ)
                    || hvtStandort.isStandortType(HVTStandort.HVT_STANDORT_TYP_FTTC_KVZ)) {
                kvzNummer = getPreparedValue(KVZ_NUMMER, String.class, false, "KVZ Nummer ist nicht angegeben!");
            }
        }
        catch (ServiceCommandException e) {
            throw e;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new HurricanServiceCommandException("Daten zur Stiftvergabe konnten nicht ermittelt werden!", e);
        }

        leiste1 = (String) getPreparedValue(UEVT_LEISTE1);
        if (StringUtils.isBlank(leiste1)) {
            throw new HurricanServiceCommandException("Die zu fuellende Leiste wurde nicht angegeben!");
        }

        uevtClusterNo = (Integer) getPreparedValue(UEVT_CLUSTER_NO);
        if (uevtClusterNo == null) {
            throw new HurricanServiceCommandException("Keine gültige ÜVt-Cluster-Nr gesetzt.");
        }

        serviceCallback = (IServiceCallback) getPreparedValue(SERVICE_CALLBACK);
        if (serviceCallback == null) {
            throw new HurricanServiceCommandException("ServiceCallback-Handler ist nicht definiert!");
        }

        Boolean tmpCreate = (Boolean) getPreparedValue(CREATE_LEISTE);
        createLeiste = (tmpCreate != null) && tmpCreate;

        user = (String) getPreparedValue(USER_NAME);
    }

    /**
     * @param hvtBestellungDAO The hvtBestellungDAO to set.
     */
    public void setHvtBestellungDAO(HVTBestellungDAO hvtBestellungDAO) {
        this.hvtBestellungDAO = hvtBestellungDAO;
    }

    /**
     * @param equipmentDAO The equipmentDAO to set.
     */
    public void setEquipmentDAO(EquipmentDAO equipmentDAO) {
        this.equipmentDAO = equipmentDAO;
    }

}
