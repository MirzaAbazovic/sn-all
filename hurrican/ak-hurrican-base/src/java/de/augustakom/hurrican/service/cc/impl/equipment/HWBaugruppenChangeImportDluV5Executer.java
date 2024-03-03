/*
 * Copyright (c) 2010 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 23.11.2010 09:05:16
 */
package de.augustakom.hurrican.service.cc.impl.equipment;

import java.io.*;
import java.util.*;
import java.util.Map.*;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.SystemUtils;
import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import de.augustakom.authentication.model.AKUser;
import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.common.tools.poi.XlsPoiTool;
import de.augustakom.hurrican.HurricanConstants;
import de.augustakom.hurrican.model.cc.Equipment;
import de.augustakom.hurrican.model.cc.equipment.HWBaugruppenChangeDlu;
import de.augustakom.hurrican.model.cc.equipment.HWBaugruppenChangeDluV5;
import de.augustakom.hurrican.service.base.exceptions.DeleteException;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.cc.HWBaugruppenChangeService;
import de.augustakom.hurrican.service.cc.HWService;
import de.augustakom.hurrican.service.cc.RangierungsService;


/**
 * Executer-Klasse, um den Import der HW-EQN/V5-Port Mappings durchzufuehren.
 */
public class HWBaugruppenChangeImportDluV5Executer extends AbstractHWBaugruppenChangeDluExecuter {

    private static final Logger LOGGER = Logger.getLogger(HWBaugruppenChangeImportDluV5Executer.class);

    public static final int COL_ID_HW_EQN = 0;
    public static final int COL_ID_V5_PORT = 1;

    private HWBaugruppenChangeDlu hwBgChangeDlu;
    private HWBaugruppenChangeService hwBaugruppenChangeService;
    private HWService hwService;
    private RangierungsService rangierungsService;
    private InputStream fileToImport;
    private AKUser currentUser;

    List<Equipment> dluEquipments;
    Map<String, String> hwEqnToV5PortMappings;

    /**
     * Uebergibt dem Executer die notwendigen Modelle u. Services.
     *
     * @param hwBgChangeDlu
     * @param hwBaugruppenChangeService
     * @param hwService
     * @param rangierungsService
     * @param fileToImport
     * @param currentUser
     */
    public void configure(HWBaugruppenChangeDlu hwBgChangeDlu,
            HWBaugruppenChangeService hwBaugruppenChangeService,
            HWService hwService,
            RangierungsService rangierungsService,
            InputStream fileToImport,
            AKUser currentUser) {
        setHwBgChangeDlu(hwBgChangeDlu);
        setHwBaugruppenChangeService(hwBaugruppenChangeService);
        setHwService(hwService);
        setRangierungsService(rangierungsService);
        setFileToImport(fileToImport);
        setCurrentUser(currentUser);
    }

    @Override
    public void execute() throws StoreException {
        try {
            deleteExistingV5Mappings();
            readFile();
            readEquipments4Dlu();
            validateV5Mappings();
            createAndSaveV5PortMappings();
        }
        catch (StoreException e) {
            throw e;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException("Error during import of HW_EQN/V5-Port Mappings: " + e.getMessage(), e);
        }
    }


    /**
     * Loescht alle vorhandenen V5-Mappings zu dem aktuellen DLU-Schwenk. Der Loesch-Vorgang findet in einer eigenen
     * Transaktion statt!
     *
     * @throws DeleteException
     */
    void deleteExistingV5Mappings() throws DeleteException {
        hwBaugruppenChangeService.deleteDluV5MappingsNewTx(hwBgChangeDlu.getId());
    }


    /**
     * Liest das File mit den Mappings des (alten) HW_EQNs zum neuen V5-Port ein.
     */
    void readFile() throws Exception {
        Sheet sheet = XlsPoiTool.loadExcelFile(fileToImport);
        if (sheet == null) {
            throw new IOException("Excel-Sheet not loaded!");
        }

        hwEqnToV5PortMappings = new HashMap<String, String>();

        for (int row = 1, lastRowNo = sheet.getLastRowNum(); row <= lastRowNo; row++) { // erste Zeile = Ueberschrift!
            Row currentRow = sheet.getRow(row);
            if ((currentRow != null) && StringUtils.isNotBlank(
                    XlsPoiTool.getContentAsString(currentRow, COL_ID_HW_EQN))) {

                String hwEqn = XlsPoiTool.getContentAsString(currentRow, COL_ID_HW_EQN);
                String v5Port = XlsPoiTool.getContentAsString(currentRow, COL_ID_V5_PORT);

                hwEqnToV5PortMappings.put(hwEqn, v5Port);
            }
        }
    }


    /**
     * Ermittelt alle Equipments der DLU. <br> (Es werden nur eingebaute Baugruppen beruecksichtigt.)
     *
     * @throws FindException
     */
    void readEquipments4Dlu() throws FindException {
        dluEquipments = readEquipments4Dlu(hwBgChangeDlu, hwService, rangierungsService);
    }


    /**
     * Erstellt die Modelle fuer die V5-Port Mappings und speichert diese ab.
     *
     * @throws StoreException
     */
    void createAndSaveV5PortMappings() throws StoreException {
        if (CollectionTools.isNotEmpty(dluEquipments)) {
            for (Equipment equipment : dluEquipments) {
                if ((hwEqnToV5PortMappings != null) && hwEqnToV5PortMappings.containsKey(equipment.getHwEQN())) {
                    HWBaugruppenChangeDluV5 v5 = new HWBaugruppenChangeDluV5();
                    v5.setHwBgChangeDluId(hwBgChangeDlu.getId());
                    v5.setDluEquipment(equipment);
                    v5.setHwEqn(equipment.getHwEQN());
                    v5.setV5Port(hwEqnToV5PortMappings.get(equipment.getHwEQN()));
                    v5.setUserW((currentUser != null) ? currentUser.getLoginName() : HurricanConstants.UNKNOWN);

                    hwBaugruppenChangeService.saveHWBaugruppenChangeDluV5(v5);
                }
            }
        }
    }


    /**
     * Ueberprueft, ob zu allen Equipment-Ports der DLU ein V5-Mapping vorhanden ist und ob in dem V5-Mapping nicht auch
     * Eintraege von nicht existierenden DLU Ports vorhanden sind. Falls Differenzen gefunden werden, wird eine
     * Exception mit entsprechendem Text generiert.
     *
     * @throws FindException
     */
    void validateV5Mappings() throws StoreException {
        StringBuilder message = new StringBuilder();

        if (CollectionTools.isEmpty(dluEquipments)) {
            message.append("Die Liste der DLU Ports ist leer!");
            message.append(SystemUtils.LINE_SEPARATOR);
        }

        if (MapUtils.isEmpty(hwEqnToV5PortMappings)) {
            message.append("Die Liste der V5 Mappings ist leer!");
            message.append(SystemUtils.LINE_SEPARATOR);
        }

        List<Equipment> dluEquipmentWithoutMapping = findMissingPortsInMappingFile();
        if (CollectionTools.isNotEmpty(dluEquipmentWithoutMapping)) {
            message.append("DLU-Equipments in Hurrican ohne V5-Mappings:");
            message.append(SystemUtils.LINE_SEPARATOR);
            for (Equipment eq : dluEquipmentWithoutMapping) {
                message.append(eq.getHwEQN() + SystemUtils.LINE_SEPARATOR);
            }
        }

        Map<String, String> v5MappingsWithoutEqReference = findV5MappingWithoutEquipment();
        if ((v5MappingsWithoutEqReference != null)
                && (v5MappingsWithoutEqReference.entrySet() != null)
                && (!v5MappingsWithoutEqReference.entrySet().isEmpty())) {
            if (message.length() > 0) {
                message.append(SystemUtils.LINE_SEPARATOR);
            }
            message.append("V5-Mappings ohne Equipment-Referenz in Hurrican:");
            message.append(SystemUtils.LINE_SEPARATOR);

            Iterator<Entry<String, String>> entryIt = v5MappingsWithoutEqReference.entrySet().iterator();
            while (entryIt.hasNext()) {
                Entry<String, String> entry = entryIt.next();
                message.append(entry.getKey()).append("/").append(entry.getValue()).append(SystemUtils.LINE_SEPARATOR);
            }
        }

        if (StringUtils.isNotBlank(message.toString())) {
            throw new StoreException("Eintraege im angegebenen Mapping-File sind ungueltig:\n" + message.toString());
        }
    }


    /**
     * Ermittelt die Equipments der DLU, zu denen im Mapping-File kein V5-Port angegeben ist.
     *
     * @return
     */
    List<Equipment> findMissingPortsInMappingFile() {
        List<Equipment> missing = new ArrayList<Equipment>();
        if (CollectionTools.isNotEmpty(dluEquipments)) {
            for (Equipment equipment : dluEquipments) {
                if ((hwEqnToV5PortMappings != null) && !hwEqnToV5PortMappings.containsKey(equipment.getHwEQN())) {
                    missing.add(equipment);
                }
            }
        }

        return missing;
    }


    /**
     * Ermittelt alle Eintraege im HW_EQN/V5-Port Mapping File, zu denen keine Equipment-Referenz vorhanden ist.
     *
     * @return
     */
    Map<String, String> findV5MappingWithoutEquipment() {
        Map<String, String> mappingsWithoutEquipment = new HashMap<String, String>();
        if ((hwEqnToV5PortMappings != null) && (hwEqnToV5PortMappings.entrySet() != null)) {
            Iterator<Entry<String, String>> entryIt = hwEqnToV5PortMappings.entrySet().iterator();
            while (entryIt.hasNext()) {
                Entry<String, String> entry = entryIt.next();
                String hwEqn = entry.getKey();
                boolean foundDluEquipment = false;
                if (CollectionTools.isNotEmpty(dluEquipments)) {
                    for (Equipment dluEquipment : dluEquipments) {
                        if (StringUtils.equals(hwEqn, dluEquipment.getHwEQN())) {
                            foundDluEquipment = true;
                            break;
                        }
                    }
                }

                if (!foundDluEquipment) {
                    mappingsWithoutEquipment.put(hwEqn, entry.getValue());
                }
            }
        }

        return mappingsWithoutEquipment;
    }


    public void setHwBgChangeDlu(HWBaugruppenChangeDlu hwBgChangeDlu) {
        this.hwBgChangeDlu = hwBgChangeDlu;
    }

    public void setHwBaugruppenChangeService(HWBaugruppenChangeService hwBaugruppenChangeService) {
        this.hwBaugruppenChangeService = hwBaugruppenChangeService;
    }

    public void setRangierungsService(RangierungsService rangierungsService) {
        this.rangierungsService = rangierungsService;
    }

    public void setHwService(HWService hwService) {
        this.hwService = hwService;
    }

    public void setFileToImport(InputStream fileToImport) {
        this.fileToImport = fileToImport;
    }

    public void setCurrentUser(AKUser currentUser) {
        this.currentUser = currentUser;
    }

}
