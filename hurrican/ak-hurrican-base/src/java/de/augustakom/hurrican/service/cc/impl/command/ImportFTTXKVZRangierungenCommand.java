/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 04.06.2012 09:54:23
 */
package de.augustakom.hurrican.service.cc.impl.command;

import java.util.*;
import javax.annotation.*;
import com.google.common.base.Function;
import com.google.common.collect.Lists;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Row;

import de.augustakom.authentication.model.AKUser;
import de.augustakom.common.tools.lang.Pair;
import de.augustakom.common.tools.messages.AKMessages;
import de.augustakom.common.tools.poi.XlsPoiTool;
import de.augustakom.hurrican.HurricanConstants;
import de.augustakom.hurrican.annotation.CcTxRequired;
import de.augustakom.hurrican.annotation.CcTxRequiresNew;
import de.augustakom.hurrican.model.cc.EqStatus;
import de.augustakom.hurrican.model.cc.Equipment;
import de.augustakom.hurrican.model.cc.HVTStandort;
import de.augustakom.hurrican.model.cc.PhysikTyp;
import de.augustakom.hurrican.model.cc.Rangierung;
import de.augustakom.hurrican.model.cc.Rangierung.Freigegeben;
import de.augustakom.hurrican.model.cc.RangierungsAuftrag;
import de.augustakom.hurrican.model.cc.Uebertragungsverfahren;
import de.augustakom.hurrican.model.cc.hardware.HWBaugruppe;
import de.augustakom.hurrican.model.cc.hardware.HWRack;
import de.augustakom.hurrican.model.cc.hardware.HWSubrack;
import de.augustakom.hurrican.model.cc.view.XlsImportResultView.SingleRowResult;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.HurricanServiceCommandException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.cc.HVTService;
import de.augustakom.hurrican.service.cc.HWService;
import de.augustakom.hurrican.service.cc.PhysikService;
import de.augustakom.hurrican.service.cc.RangierungAdminService;
import de.augustakom.hurrican.service.cc.RangierungsService;

/**
 * Command, um FTTC KVZ Rangierungen zu importieren. Zunaechst erstellt das Command einen Rangierungsauftrag, danach
 * werden die zusammenhängenden Ports und Stifte ermittelt. Alle Equipment Entitaeten werden auf den {@code
 * EqStatus.frei} geprueft, rangiert und freigegeben.
 */
public class ImportFTTXKVZRangierungenCommand extends AbstractXlsImportCommand {

    private static final Logger LOGGER = Logger.getLogger(ImportFTTXKVZRangierungenCommand.class);

    static enum Column {
        GERAETEBEZEICHNUNG_RACK,
        MODULNUMMER_SUBRACK,
        MODULNUMMER_BAUGRUPPE,
        HW_EQN_START,
        VERTEILER_START,
        LEISTE_START,
        STIFT_START,
        ANZAHL,
        PHYSIKTYP,
        UETV,
        FREIGABESTATUS
    }

    /**
     * Der aufrufende (angemeldete) Benutzer<br> Typ: AKUser
     */
    public static final String PARAM_AK_USER = "PARAM_AK_USER";

    @Resource(name = "de.augustakom.hurrican.service.cc.HWService")
    private HWService hwService;
    @Resource(name = "de.augustakom.hurrican.service.cc.HVTService")
    private HVTService hvtService;
    @Resource(name = "de.augustakom.hurrican.service.cc.RangierungsService")
    private RangierungsService rangierungsService;
    @Resource(name = "de.augustakom.hurrican.service.cc.PhysikService")
    private PhysikService physikService;
    @Resource(name = "de.augustakom.hurrican.service.cc.RangierungAdminService")
    private RangierungAdminService rangierungAdminService;

    Function<Equipment, Long> eq2IdTransformer = new Function<Equipment, Long>() {
        @Override
        public Long apply(final Equipment port) {
            return port.getId();
        }
    };

    @Override
    @CcTxRequired
    public Object execute() throws Exception {
        final Row row = getPreparedValue(PARAM_IMPORT_ROW, Row.class, false, "Keine zu parsende Zeile übergeben!");
        final Long sessionId = getPreparedValue(PARAM_SESSION_ID, Long.class, false, "Keine sessionId übergeben!");
        final SingleRowResult importResult = new SingleRowResult();
        final Freigegeben freigegeben = getFreigegeben(row);

        final int anzahl = getAnzahl(row);
        final HWRack hwRack = findHWRack(row);
        final HWBaugruppe hwBaugruppe = findHWBaugruppe(row, hwRack);
        final Equipment ersterPort = findErsterPort(hwBaugruppe, row);
        final Rangierung rangierungErsterPort = rangierungsService.findRangierung4Equipment(ersterPort.getId(), true);

        Long rangierungsAuftragId;
        if (rangierungErsterPort == null) {
            final HVTStandort hvtStandort = findHVTStandort(hwRack);
            final Pair<List<Equipment>, List<Equipment>> equipments =
                    findAndCheckEquipments(ersterPort, row, hvtStandort, hwRack, anzahl);
            final AKUser user = getPreparedValue(PARAM_AK_USER, AKUser.class, true, null);
            // Rangierungen erstellen, dazu RangierungsAuftrag anlegen und durchbringen
            rangierungsAuftragId = createRangierungsauftragAndRangierungen(row, sessionId, user, importResult, anzahl,
                    hvtStandort, equipments);
        }
        else {
            rangierungsAuftragId = rangierungErsterPort.getRangierungsAuftragId();
        }

        modifyRangierungenSetFreigegeben(sessionId, freigegeben, anzahl, rangierungsAuftragId, ersterPort);

        return importResult;
    }

    /**
     * @param equipments
     * @throws HurricanServiceCommandException
     */
    private void checkPortsAndStifte(final List<Equipment> ports, final List<Equipment> stifte)
            throws HurricanServiceCommandException {
        for (final Equipment port : ports) {
            checkPort(port);
        }
        for (final Equipment stift : stifte) {
            checkStift(stift);
        }
    }

    void modifyRangierungenSetFreigegeben(final Long sessionId, final Freigegeben freigegeben, final int anzahl,
            final Long rangierungsAuftragId, final Equipment ersterPort)
            throws HurricanServiceCommandException, FindException, StoreException {

        if (rangierungsAuftragId != null) {
            final RangierungsAuftrag rangierungsAuftrag = rangierungAdminService.findRA(rangierungsAuftragId);
            if ((rangierungsAuftrag != null) && (rangierungsAuftrag.getAusgefuehrtAm() == null)) {
                rangierungAdminService.releaseRangierungen4RA(rangierungsAuftragId, Float.valueOf(0.0f), sessionId);
            }
        }

        final List<Equipment> ports = rangierungsService.findConsecutivePorts(ersterPort, anzahl);
        final Map<Long, Rangierung> rangierungen = rangierungsService.findRangierungen4Equipments(ports);
        final List<Rangierung> modify = new ArrayList<Rangierung>(anzahl);
        final StringBuilder missingHWEqns = new StringBuilder();
        for (final Equipment port : ports) {
            Rangierung rangierung = rangierungen.get(port.getId());
            if (rangierung != null) {
                modify.add(rangierung);
            }
            else {
                if (missingHWEqns.length() > 0) {
                    missingHWEqns.append(", ");
                }
                missingHWEqns.append(port.getHwEQN());
            }
        }

        if (missingHWEqns.length() > 0) {
            throw new HurricanServiceCommandException(String.format(
                    "Nicht zu jedem Port existiert eine Rangierung! Folgende Ports sind nicht rangiert \"%s\".",
                    missingHWEqns.toString()));
        }
        for (Rangierung rangierung : modify) {
            rangierung.setFreigegeben(freigegeben);
        }
    }

    private Long createRangierungsauftragAndRangierungen(final Row row, final Long sessionId, final AKUser user,
            final SingleRowResult importResult, final int anzahl, final HVTStandort hvtStandort,
            final Pair<List<Equipment>, List<Equipment>> equipments) throws HurricanServiceCommandException,
            FindException, StoreException {
        final RangierungsAuftrag rangierungsAuftrag = createRangierungsAuftrag(row, user, hvtStandort, anzahl);
        final Long rangierungsAuftragId = rangierungsAuftrag.getId();
        final AKMessages messages = createRangierungen(row, equipments, rangierungsAuftrag, sessionId);
        if ((messages != null) && messages.isNotEmpty()) {
            importResult.addWarning(messages.getMessagesAsText());
        }
        return rangierungsAuftragId;
    }

    private Freigegeben getFreigegeben(final Row row) throws HurricanServiceCommandException {
        final String freigegeben = getColumnAsString(row, Column.FREIGABESTATUS.ordinal(),
                "Status der Rangierung fehlt!").trim();
        try {
            return Freigegeben.valueOf(freigegeben);
        }
        catch (final IllegalArgumentException e) {
            throw new HurricanServiceCommandException("Ungültiger Wert im Feld Status!");
        }
    }

    Uebertragungsverfahren findUETV(final Row row) throws HurricanServiceCommandException {
        final String uetvName = getColumnAsString(row, Column.UETV.ordinal(), "Übertragungsverfahren fehlt!");
        Uebertragungsverfahren uetv = null;
        try {
            uetv = Uebertragungsverfahren.valueOf(uetvName.toUpperCase());
        }
        catch (final IllegalArgumentException e) {
            LOGGER.info(e.getMessage());
            // Uebertragungsverfahren existiert nicht!
        }
        if (uetv == null) {
            throw new HurricanServiceCommandException(String.format("Das Übertragungsverfahren %s existiert nicht!",
                    uetvName));
        }
        return uetv;
    }

    AKMessages createRangierungen(final Row row, final Pair<List<Equipment>, List<Equipment>> equipments,
            final RangierungsAuftrag rangierungsAuftrag, final Long sessionId)
            throws HurricanServiceCommandException,
            StoreException {
        final Uebertragungsverfahren uetv = findUETV(row);
        final List<Long> portIds = Lists.transform(equipments.getFirst(), eq2IdTransformer);
        final List<Long> stiftIds = Lists.transform(equipments.getSecond(), eq2IdTransformer);
        return rangierungAdminService.createRangierungen(rangierungsAuftrag.getId(), null, stiftIds,
                null, portIds, Equipment.RANG_SS_2H, uetv, null, sessionId);

    }

    HWRack findHWRack(final Row row) throws HurricanServiceCommandException, FindException {
        final String geraetebezeichnung = getColumnAsString(row, Column.GERAETEBEZEICHNUNG_RACK.ordinal(),
                "Gerätebezeichnung fehlt!");
        final HWRack hwRack = hwService.findRackByBezeichnung(geraetebezeichnung);
        if (hwRack == null) {
            throw new HurricanServiceCommandException(String.format("Das Rack %s konnte nicht ermittelt werden!",
                    geraetebezeichnung));
        }
        return hwRack;
    }

    HWSubrack findHWSubrack(final Row row, final HWRack hwRack) throws HurricanServiceCommandException, FindException {
        final String modulnummerSubrack = getColumnAsString(row, Column.MODULNUMMER_SUBRACK.ordinal(),
                "Modulnummer des Subracks fehlt!");
        final HWSubrack hwSubrack = hwService.findSubrackByHwRackAndModNumber(hwRack.getId(), modulnummerSubrack);
        if (hwSubrack == null) {
            throw new HurricanServiceCommandException(String.format(
                    "Das Subrack mit der Modulnummer %s konnte nicht ermittelt werden!", modulnummerSubrack));
        }
        return hwSubrack;
    }

    HWBaugruppe findHWBaugruppe(final Row row, final HWRack hwRack, final HWSubrack hwSubrack)
            throws HurricanServiceCommandException, FindException {
        final String modulnummerBaugruppe = getColumnAsString(row, Column.MODULNUMMER_BAUGRUPPE.ordinal(),
                "Modulnummer der Baugruppe fehlt!");
        final HWBaugruppe hwBaugruppe = hwService.findBaugruppe(hwRack.getId(), hwSubrack.getId(), modulnummerBaugruppe);
        if (hwBaugruppe == null) {
            throw new HurricanServiceCommandException(String.format(
                    "Die Baugruppe mit der Modulnummer %s konnte nicht ermittelt werden!", modulnummerBaugruppe));
        }
        return hwBaugruppe;
    }

    HWBaugruppe findHWBaugruppe(final Row row, final HWRack hwRack) throws HurricanServiceCommandException,
            FindException {
        final HWSubrack hwSubrack = findHWSubrack(row, hwRack);
        return findHWBaugruppe(row, hwRack, hwSubrack);
    }

    Equipment findErsterPort(final HWBaugruppe hwBaugruppe, final Row row) throws HurricanServiceCommandException,
            FindException {
        final String hwEQN = getColumnAsString(row, Column.HW_EQN_START.ordinal(), "HW_EQN des ersten Ports fehlt!");
        final Equipment ersterPort = rangierungsService.findEquipmentByBaugruppe(hwBaugruppe.getId(), hwEQN, null);
        if (ersterPort == null) {
            throw new HurricanServiceCommandException(String.format(
                    "Der Port mit der HW_EQN %s konnte nicht ermittelt werden!", hwEQN));
        }
        return ersterPort;
    }

    Equipment findErsterStift(final Long hvtStandortId, final Row row) throws HurricanServiceCommandException,
            FindException {
        final String verteiler = getColumnAsString(row, Column.VERTEILER_START.ordinal(),
                "Bezeichnung des Verteilers fehlt!");
        final String leiste = getColumnAsString(row, Column.LEISTE_START.ordinal(), "Bezeichnung der Leiste fehlt!");
        final String stift = getColumnAsString(row, Column.STIFT_START.ordinal(), "Bezeichnung des Stifts fehlt!");
        final Equipment ersterStift = rangierungsService
                .findEQByVerteilerLeisteStift(hvtStandortId, verteiler, leiste, stift);
        if (ersterStift == null) {
            throw new HurricanServiceCommandException(String.format(
                    "Der Stift %s %s %s konnte nicht ermittelt werden!", verteiler, leiste, stift));
        }
        return ersterStift;
    }

    void checkPort(final Equipment port) throws HurricanServiceCommandException {
        if (EqStatus.frei != port.getStatus()) {
            throw new HurricanServiceCommandException(String.format("Der Port %s ist nicht frei!", port.getHwEQN()));
        }
    }

    void checkStift(final Equipment stift) throws HurricanServiceCommandException {
        if (EqStatus.frei != stift.getStatus()) {
            throw new HurricanServiceCommandException(String.format("Der Stift %s %s %s ist nicht frei!",
                    stift.getRangVerteiler(), stift.getRangLeiste1(), stift.getRangStift1()));
        }
    }

    Pair<List<Equipment>, List<Equipment>> findAndCheckEquipments(final Equipment ersterPort, final Row row, final HVTStandort hvtStandort,
            final HWRack hwRack, final int anzahl)
            throws HurricanServiceCommandException, FindException {
        final Equipment ersterStift = findErsterStift(hvtStandort.getId(), row);
        final List<Equipment> ports = rangierungsService.findConsecutivePorts(ersterPort, anzahl);

        final List<Equipment> stifte = rangierungsService.findConsecutiveUEVTStifte(ersterStift, anzahl);
        checkPortsAndStifte(ports, stifte);

        return new Pair<List<Equipment>, List<Equipment>>(ports, stifte);
    }

    int getAnzahl(final Row row) throws HurricanServiceCommandException {
        return getColumnAsInt(row, Column.ANZAHL.ordinal(), "Anzahl Rangierungen fehlt!");
    }

    PhysikTyp findPhysikTyp(final Row row) throws HurricanServiceCommandException, FindException {
        final String physiktypName = getColumnAsString(row, Column.PHYSIKTYP.ordinal(), "Physiktyp fehlt!");
        final PhysikTyp physikTyp = physikService.findPhysikTypByName(physiktypName.toUpperCase());
        if (physikTyp == null) {
            throw new HurricanServiceCommandException(String.format("Der PhysikTyp %s konnte nicht ermittelt werden!",
                    physiktypName));
        }
        return physikTyp;
    }

    HVTStandort findHVTStandort(final HWRack hwRack) throws HurricanServiceCommandException, FindException {
        final HVTStandort hvtStandort = hvtService.findHVTStandort(hwRack.getHvtIdStandort());
        if (hvtStandort == null) {
            throw new HurricanServiceCommandException(String.format(
                    "Der HVT Standort mit Id %s konnte nicht ermittelt werden!", hwRack.getHvtIdStandort()));
        }
        return hvtStandort;
    }

    RangierungsAuftrag createRangierungsAuftrag(final Row row, final AKUser user, final HVTStandort hvtStandort, final int anzahl)
            throws HurricanServiceCommandException, FindException, StoreException {
        final Date now = new Date();
        final PhysikTyp physikTyp = findPhysikTyp(row);
        final RangierungsAuftrag rangierungsAuftrag = new RangierungsAuftrag();
        final String userName = (user != null) ? user.getNameAndFirstName() : HurricanConstants.UNKNOWN;

        rangierungsAuftrag.setHvtStandortId(hvtStandort.getId());
        rangierungsAuftrag.setAnzahlPorts(Integer.valueOf(anzahl));
        rangierungsAuftrag.setPhysiktypParent(physikTyp.getId());
        rangierungsAuftrag.setPhysiktypChild(null);
        rangierungsAuftrag.setAuftragVon(userName + " (Import Rangierungen)");
        rangierungsAuftrag.setTechnikBearbeiter(userName);
        rangierungsAuftrag.setAuftragAm(now);
        rangierungsAuftrag.setFaelligAm(now);
        rangierungAdminService.saveRangierungsAuftrag(rangierungsAuftrag);
        return rangierungsAuftrag;
    }

    int getColumnAsInt(final Row row, final int index, final String message) throws HurricanServiceCommandException {
        final Integer result = XlsPoiTool.getContentAsInt(row, index);
        if (result == null) {
            throw new HurricanServiceCommandException(message);
        }
        return result.intValue();
    }

    String getColumnAsString(final Row row, final int index, final String message)
            throws HurricanServiceCommandException {
        final String result = XlsPoiTool.getContentAsString(row, index);
        if (StringUtils.isEmpty(result)) {
            throw new HurricanServiceCommandException(message);
        }
        return result;
    }

}


