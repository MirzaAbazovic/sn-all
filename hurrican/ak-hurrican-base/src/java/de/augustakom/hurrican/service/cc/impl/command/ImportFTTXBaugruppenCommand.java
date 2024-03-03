/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 13.04.2012 13:17:30
 */
package de.augustakom.hurrican.service.cc.impl.command;

import javax.annotation.*;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.Row;

import de.augustakom.common.tools.poi.XlsPoiTool;
import de.augustakom.hurrican.annotation.CcTxRequiresNew;
import de.augustakom.hurrican.model.cc.EqStatus;
import de.augustakom.hurrican.model.cc.Equipment;
import de.augustakom.hurrican.model.cc.hardware.HWBaugruppe;
import de.augustakom.hurrican.model.cc.hardware.HWBaugruppenTyp;
import de.augustakom.hurrican.model.cc.hardware.HWRack;
import de.augustakom.hurrican.model.cc.hardware.HWSubrack;
import de.augustakom.hurrican.model.cc.hardware.HWSwitch;
import de.augustakom.hurrican.model.cc.view.XlsImportResultView.SingleRowResult;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.HurricanServiceCommandException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.cc.HWService;
import de.augustakom.hurrican.service.cc.RangierungsService;

/**
 * Command, um Hardware Baugruppen zu importieren.
 */
public class ImportFTTXBaugruppenCommand extends AbstractXlsImportCommand {

    protected static final int COLUMN_RACK = 0;
    protected static final int COLUMN_SUBRACK_MOD_NR = 1;
    protected static final int COLUMN_TYP = 2;
    protected static final int COLUMN_MODUL_NR = 3;
    protected static final int COLUMN_RANG_VERTEILER_OUT = 4;
    protected static final int COLUMN_RANG_BUCHT_OUT = 5;
    protected static final int COLUMN_RANG_STIFT_1_OUT = 6;

    @Resource(name = "de.augustakom.hurrican.service.cc.HWService")
    private HWService hwService;

    @Resource(name = "de.augustakom.hurrican.service.cc.RangierungsService")
    private RangierungsService rangierungsService;

    @Override
    @CcTxRequiresNew
    public Object execute() throws Exception {
        Row row = getPreparedValue(PARAM_IMPORT_ROW, Row.class, false, "Keine zu parsende Zeile übergeben");

        SingleRowResult importResult = new SingleRowResult();

        HWRack hwRack = findHwRack(row);
        HWBaugruppe hwBaugruppe = createBaugruppe(row, hwRack);
        createPorts(row, hwRack, hwBaugruppe);

        return importResult;
    }

    void createPorts(Row row, HWRack hwRack, HWBaugruppe hwBaugruppe)
            throws HurricanServiceCommandException, StoreException, FindException {
        final Integer portCount = findPortCount(hwBaugruppe);
        final String rangVerteiler = parseRangVerteiler(row);
        final String rangBucht = parseRangBucht(row);
        final int rangStift1StartValue = parseRangStift1StartValue(row);

        final String hwEqnBase = buildHwEqnBase(hwBaugruppe);

        for (int i = 0; i < portCount; i++) {
            createPort(hwRack, hwBaugruppe, rangVerteiler, rangBucht, rangStift1StartValue, hwEqnBase, i);
        }
    }

    private String parseRangBucht(Row row) throws HurricanServiceCommandException {
        final String rangBucht = XlsPoiTool.getContentAsString(row, COLUMN_RANG_BUCHT_OUT);
        if (StringUtils.isEmpty(rangBucht)) {
            throw new HurricanServiceCommandException("Spalte RANG_BUCHT_OUT muss angegeben sein!");
        }
        return rangBucht;
    }

    private String parseRangVerteiler(Row row) throws HurricanServiceCommandException {
        final String rangVerteiler = XlsPoiTool.getContentAsString(row, COLUMN_RANG_VERTEILER_OUT);
        if (StringUtils.isEmpty(rangVerteiler)) {
            throw new HurricanServiceCommandException("Spalte RANG_VERTEILER_OUT muss angegeben sein!");
        }
        return rangVerteiler;
    }

    protected final String buildHwEqnBase(HWBaugruppe hwBaugruppe) throws HurricanServiceCommandException {
        final Integer modulNrRackPart = hwBaugruppe.getModNumberRackPart();
        final Integer modulNrSubrackPart = hwBaugruppe.getModNumberSubrackPart();
        final Integer modulNrLineKartePart = hwBaugruppe.getModNumberLineKartePart();

        if (modulNrRackPart == null || modulNrSubrackPart == null || modulNrLineKartePart == null) {
            throw new HurricanServiceCommandException("Spalte ModulNr muss der Form 'R1/S1-LT01' entsprechen!");
        }

        return String.format("%d-%d-%d-", modulNrRackPart, modulNrSubrackPart, modulNrLineKartePart);
    }

    private int parseRangStift1StartValue(Row row) throws HurricanServiceCommandException {
        final Integer rangStift1StartValue = XlsPoiTool.getContentAsInt(row, COLUMN_RANG_STIFT_1_OUT);
        if (rangStift1StartValue == null) {
            throw new HurricanServiceCommandException("Spalte RANG_STIFT1_OUT muss angegeben sein!");
        }
        return rangStift1StartValue;
    }

    private Integer findPortCount(HWBaugruppe hwBaugruppe) throws HurricanServiceCommandException {
        final Integer portCount = hwBaugruppe.getHwBaugruppenTyp().getPortCount();
        if (portCount == null) {
            throw new HurricanServiceCommandException(
                    String.format(
                            "Für den Baugruppentyp mit Id %d und Name '%s' ist die Anzahl an Ports nicht hinterlegt!",
                            hwBaugruppe.getHwBaugruppenTyp().getId(), hwBaugruppe.getHwBaugruppenTyp().getName())
            );
        }
        return portCount;
    }

    protected final void createPort(HWRack hwRack, HWBaugruppe hwBaugruppe, final String rangVerteiler,
            final String rangBucht, final int rangStift1StartValue, final String hwEqnBase,
            int counter) throws StoreException {
        final String rangStift1 = String.valueOf(rangStift1StartValue + counter);
        counter++;
        final String hwEqn = hwEqnBase + ((counter < 10) ? ("0" + counter) : counter);
        final String hwSchnittstelleName = hwBaugruppe.getHwBaugruppenTyp().getHwSchnittstelleName();
        Equipment equipment = new Equipment();
        equipment.setRangVerteiler(rangVerteiler);
        equipment.setRangBucht(rangBucht);
        equipment.setRangStift1(rangStift1);
        equipment.setHwEQN(hwEqn);
        equipment.setHwBaugruppenId(hwBaugruppe.getId());
        equipment.setHvtIdStandort(hwRack.getHvtIdStandort());
        equipment.setStatus(EqStatus.frei);
        equipment.setHwSchnittstelle(hwSchnittstelleName);
        equipment.setRangSSType(hwSchnittstelleName);
        rangierungsService.saveEquipment(equipment);
    }

    HWBaugruppe createBaugruppe(Row row, HWRack hwRack) throws Exception {
        HWBaugruppe baugruppeToSave = new HWBaugruppe();

        final Long hwSubrackId = findHwSubrackId(row, hwRack);
        final HWBaugruppenTyp hwBaugruppenTyp = findHwBaugruppenTyp(row);
        final String modNr = parseModNr(row);

        if (hwService.findBaugruppe(hwRack.getId(), hwSubrackId, modNr) != null) {
            throw new HurricanServiceCommandException(String.format(
                    "Es existiert bereits eine Baugruppe mit rackId=%d, subrackId=%d, modNumber=%s", hwRack.getId(),
                    hwSubrackId, modNr));
        }

        baugruppeToSave.setRackId(hwRack.getId());
        baugruppeToSave.setSubrackId(hwSubrackId);
        baugruppeToSave.setHwBaugruppenTyp(hwBaugruppenTyp);
        baugruppeToSave.setModNumber(modNr);
        baugruppeToSave.setEingebaut(true);

        return hwService.saveHWBaugruppe(baugruppeToSave);
    }

    private String parseModNr(Row row) throws HurricanServiceCommandException {
        final String modNr = XlsPoiTool.getContentAsString(row, COLUMN_MODUL_NR);
        if (StringUtils.isEmpty(modNr)) {
            throw new HurricanServiceCommandException("Spalte ModulNr ist leer!");
        }
        return modNr;
    }

    private HWBaugruppenTyp findHwBaugruppenTyp(Row row) throws FindException, HurricanServiceCommandException {
        final String hwBaugruppenTypName = XlsPoiTool.getContentAsString(row, COLUMN_TYP);
        HWBaugruppenTyp hwBaugruppenTyp = hwService.findBaugruppenTypByName(hwBaugruppenTypName);
        if (hwBaugruppenTyp == null) {
            throw new HurricanServiceCommandException(String.format("HWBaugruppenTyp mit Name %s nicht gefunden!",
                    hwBaugruppenTypName));
        }
        return hwBaugruppenTyp;
    }

    private Long findHwSubrackId(Row row, HWRack hwRack) throws FindException, HurricanServiceCommandException {
        Long hwSubrackId;
        final String subrackModNumber = XlsPoiTool.getContentAsString(row, COLUMN_SUBRACK_MOD_NR);
        if (StringUtils.isNotEmpty(subrackModNumber)) {
            HWSubrack hwSubrack = hwService.findSubrackByHwRackAndModNumber(hwRack.getId(), subrackModNumber);
            if (hwSubrack == null) {
                throw new HurricanServiceCommandException(String.format(
                        "HWSubrack mit Subrack-ModulNr %s und HWRack-Id %d nicht gefunden!", subrackModNumber,
                        hwRack.getId()));
            }
            hwSubrackId = hwSubrack.getId();
        }
        else {
            throw new HurricanServiceCommandException("Spalte Subrack-ModulNr darf nicht leer sein!");
        }
        return hwSubrackId;
    }

    private HWRack findHwRack(Row row) throws FindException, HurricanServiceCommandException {
        final String rackGeraeteBezeichnung = XlsPoiTool.getContentAsString(row, COLUMN_RACK);
        if (StringUtils.isEmpty(rackGeraeteBezeichnung)) {
            throw new HurricanServiceCommandException("Gerätebezeichnung (Rack) fehlt!");
        }
        HWRack hwRack = hwService.findRackByBezeichnung(rackGeraeteBezeichnung);
        if (hwRack == null) {
            throw new HurricanServiceCommandException(String.format("HWRack mit Gerätebezeichnung %s nicht gefunden!",
                    rackGeraeteBezeichnung));
        }
        return hwRack;
    }

}
