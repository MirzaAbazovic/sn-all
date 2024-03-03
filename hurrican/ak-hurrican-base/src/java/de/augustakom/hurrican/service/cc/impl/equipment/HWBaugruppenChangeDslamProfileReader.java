/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 11.09.2014
 */
package de.augustakom.hurrican.service.cc.impl.equipment;

import java.io.*;
import java.util.*;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import de.augustakom.common.tools.lang.Pair;
import de.augustakom.common.tools.poi.XlsPoiTool;
import de.augustakom.hurrican.model.cc.DSLAMProfile;
import de.augustakom.hurrican.model.cc.Uebertragungsverfahren;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.ImportException;
import de.augustakom.hurrican.service.cc.DSLAMService;

/**
 * Hilfsklasse um ein XLS mit einem Mapping von DSLAM Profilen zu laden.
 *
 *
 */
public class HWBaugruppenChangeDslamProfileReader {

    private DSLAMService dslamService;

    public HWBaugruppenChangeDslamProfileReader(DSLAMService dslamService) {
        this.dslamService = dslamService;
    }

    /**
     * @param xlsFile Dateiname der XLS (im lokalen Filesystem)
     * @return Multimap mit Mapping "ID altes Profil", UETV - "neue moeglichen DSLAM Profile". Wenn für die Bezeichnung des
     * neuen Profils aus dem XLS kein DSLAM Profil gefunden wurde, enthaelt die Map leere Liste als Value. Die Multimap
     * haelt alle zu dem Namen gefundenden Profile, die zuordnung der neuen Zielprofile erfolgt ohne beruecksichtigung
     * auf Baugruppentypen. D.h. es muss entsprechen dem Ziel-BG-Typ das entsprechende Profil der Liste entnommen
     * werden.
     * @throws ImportException
     */
    public Multimap<Pair<Long, Uebertragungsverfahren>, DSLAMProfile> loadDslamProfilesFromXls(File xlsFile) throws ImportException, FindException {
        Multimap<Pair<Long, Uebertragungsverfahren>, DSLAMProfile> profileMapping = ArrayListMultimap.create();
        try {
            Sheet sheet = XlsPoiTool.loadExcelFile(new FileInputStream(xlsFile));
            int maxRows = sheet.getLastRowNum();
            for (int rowIndex = 1; rowIndex <= maxRows; rowIndex++) {
                Row row = sheet.getRow(rowIndex);
                String oldDslamProfileName = XlsPoiTool.getContentAsString(row, 0);
                String uetvAsString = XlsPoiTool.getContentAsString(row, 1);
                String newDslamProfileName = XlsPoiTool.getContentAsString(row, 2);

                List<DSLAMProfile> oldDslamProfiles = dslamService.findDSLAMProfiles(oldDslamProfileName);
                if (oldDslamProfiles.isEmpty()) {
                    continue;
                }
                Uebertragungsverfahren uetv = null;
                if(StringUtils.isNotBlank(uetvAsString)) {
                    uetv = Uebertragungsverfahren.valueOf(uetvAsString.trim());
                }

                List<DSLAMProfile> dslamProfiles = dslamService.findDSLAMProfiles(newDslamProfileName);
                for (final DSLAMProfile oldDslamProfile : oldDslamProfiles) {
                    profileMapping.putAll(Pair.create(oldDslamProfile.getId(), uetv), dslamProfiles);
                }
            }
        }
        catch (InvalidFormatException | IOException | IllegalArgumentException e) {
            throw new ImportException("Fehler beim Laden der DSLAM-Profil XLS.", e);
        }
        return profileMapping;
    }
}
