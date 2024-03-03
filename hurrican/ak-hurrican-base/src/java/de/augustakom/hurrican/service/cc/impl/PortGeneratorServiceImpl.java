/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 21.12.2009 10:14:33
 */
package de.augustakom.hurrican.service.cc.impl;

import java.io.*;
import java.util.*;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import de.augustakom.common.service.base.ServiceCommandResult;
import de.augustakom.common.tools.poi.XlsPoiTool;
import de.augustakom.hurrican.annotation.CcTxRequired;
import de.augustakom.hurrican.model.cc.Equipment;
import de.augustakom.hurrican.model.cc.equipment.PortGeneratorDetails;
import de.augustakom.hurrican.model.cc.equipment.PortGeneratorImport;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.cc.HWService;
import de.augustakom.hurrican.service.cc.HWSwitchService;
import de.augustakom.hurrican.service.cc.PortGeneratorService;
import de.augustakom.hurrican.service.cc.impl.ports.AbstractPortConfigurationCheck;
import de.augustakom.hurrican.service.cc.impl.ports.PortGenerator;

/**
 * Implementierung von <code>PortGeneratorService</code>
 *
 *
 */
@CcTxRequired
public class PortGeneratorServiceImpl extends DefaultCCService implements PortGeneratorService {

    private static final Logger LOGGER = Logger.getLogger(PortGeneratorServiceImpl.class);

    private HWService hwService;
    private HWSwitchService hwSwitchService;
    private List<AbstractPortConfigurationCheck> portConfigurationChecks;

    @Override
    public List<PortGeneratorImport> retrievePorts(InputStream fileToImport, PortGeneratorDetails details)
            throws IOException, FindException {

        if (fileToImport == null) {
            throw new IllegalArgumentException("File to import is not defined!");
        }

        Sheet importSheet;
        try {
            importSheet = XlsPoiTool.loadExcelFile(fileToImport);
        }
        catch (InvalidFormatException e) {
            throw new IOException(e);
        }

        List<PortGeneratorImport> portsToGenerate = new ArrayList<>();
        for (int row = 1, lastRowNo = importSheet.getLastRowNum(); row <= lastRowNo; row++) {
            Row currentRow = importSheet.getRow(row);
            if ((currentRow != null)
                    && StringUtils.isNotBlank(XlsPoiTool.getContentAsString(currentRow,
                    PortGeneratorImport.COL_ID_HW_EQN))) {
                PortGeneratorImport portToGenerate = new PortGeneratorImport();
                portToGenerate.setHwEqn(XlsPoiTool.getContentAsString(currentRow, PortGeneratorImport.COL_ID_HW_EQN));
                portToGenerate.setV5Port(XlsPoiTool.getContentAsString(currentRow, PortGeneratorImport.COL_ID_V5_PORT));
                portToGenerate.setRangVerteilerIn(XlsPoiTool.getContentAsString(currentRow,
                        PortGeneratorImport.COL_ID_RANG_VERTEILER_IN));
                portToGenerate.setRangBuchtIn(XlsPoiTool.getContentAsString(currentRow,
                        PortGeneratorImport.COL_ID_RANG_BUCHT_IN));
                portToGenerate.setRangLeiste1In(XlsPoiTool.getContentAsString(currentRow,
                        PortGeneratorImport.COL_ID_RANG_LEISTE1_IN));
                portToGenerate.setRangStift1In(XlsPoiTool.getContentAsString(currentRow,
                        PortGeneratorImport.COL_ID_RANG_STIFT1_IN));
                portToGenerate.setRangLeiste2In(XlsPoiTool.getContentAsString(currentRow,
                        PortGeneratorImport.COL_ID_RANG_LEISTE2_IN));
                portToGenerate.setRangStift2In(XlsPoiTool.getContentAsString(currentRow,
                        PortGeneratorImport.COL_ID_RANG_STIFT2_IN));
                portToGenerate.setRangVerteilerOut(XlsPoiTool.getContentAsString(currentRow,
                        PortGeneratorImport.COL_ID_RANG_VERTEILER_OUT));
                portToGenerate.setRangBuchtOut(XlsPoiTool.getContentAsString(currentRow,
                        PortGeneratorImport.COL_ID_RANG_BUCHT_OUT));
                portToGenerate.setRangLeiste1Out(XlsPoiTool.getContentAsString(currentRow,
                        PortGeneratorImport.COL_ID_RANG_LEISTE1_OUT));
                portToGenerate.setRangStift1Out(XlsPoiTool.getContentAsString(currentRow,
                        PortGeneratorImport.COL_ID_RANG_STIFT1_OUT));
                portToGenerate.setRangLeiste2Out(XlsPoiTool.getContentAsString(currentRow,
                        PortGeneratorImport.COL_ID_RANG_LEISTE2_OUT));
                portToGenerate.setRangStift2Out(XlsPoiTool.getContentAsString(currentRow,
                        PortGeneratorImport.COL_ID_RANG_STIFT2_OUT));
                portToGenerate
                        .setSwitchName(XlsPoiTool.getContentAsString(currentRow, PortGeneratorImport.COL_ID_SWITCH));

                portsToGenerate.add(portToGenerate);
            }
        }
        return checkPortsToGenerate(details, portsToGenerate);
    }

    /**
     * Funktion prüft die zu importierenden Ports auf Vollständigkeit, Validität, setzt eine Flag, ob ein gegebener Port
     * bereits vorhanden ist, etc. und gibt die Liste mit den Ports zurück
     *
     * @throws FindException falls die zu importierenden Daten nicht valide sind
     */
    private List<PortGeneratorImport> checkPortsToGenerate(PortGeneratorDetails details,
            List<PortGeneratorImport> portsToGenerate) throws FindException {
        for (AbstractPortConfigurationCheck check : portConfigurationChecks) {
            ServiceCommandResult result = check.executeCheck(details, portsToGenerate);
            if (!result.isOk()) {
                throw new FindException(result.getMessage());
            }
        }
        return portsToGenerate;
    }

    @Override
    public List<Equipment> generatePorts(PortGeneratorDetails details,
            List<PortGeneratorImport> portsToGenerate, Long sessionId) throws FindException {
        try {
            PortGenerator portGenerator = new PortGenerator();
            portGenerator.configurePortGenerator(details, hwService, hwSwitchService, getAKUserBySessionIdSilent(sessionId));

            List<Equipment> result = new ArrayList<>();
            for (PortGeneratorImport port : portsToGenerate) {
                if (!port.getPortAlreadyExists()) {
                    List<Equipment> generatedPorts = portGenerator.generatePort(port);
                    result.addAll(generatedPorts);
                }
            }
            return result;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException("Error generating the ports: " + e.getMessage(), e);
        }
    }

    /**
     * Injected by Spring
     */
    public void setHwService(HWService hwService) {
        this.hwService = hwService;
    }

    /**
     * Injected by Spring
     */
    public void setHwSwitchService(HWSwitchService hwSwitchService) {
        this.hwSwitchService = hwSwitchService;
    }

    /**
     * Injected by Spring
     */
    public void setPortConfigurationChecks(List<AbstractPortConfigurationCheck> portConfigurationChecks) {
        this.portConfigurationChecks = portConfigurationChecks;
    }

}
