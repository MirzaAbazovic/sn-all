/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 19.10.2009 15:05:41
 */
package de.augustakom.hurrican.service.cc.impl.reportdata;

import java.util.*;
import net.sf.jasperreports.engine.JRException;
import org.apache.log4j.Logger;

import de.augustakom.common.tools.collections.CollectionMapConverter;
import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.common.tools.lang.BooleanTools;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.common.tools.lang.StringTools;
import de.augustakom.common.tools.reports.AKReportException;
import de.augustakom.hurrican.model.cc.BrasPool;
import de.augustakom.hurrican.model.cc.EQCrossConnection;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.cc.Equipment;
import de.augustakom.hurrican.model.cc.Produkt;
import de.augustakom.hurrican.model.cc.Rangierung;
import de.augustakom.hurrican.model.cc.Reference;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.cc.EQCrossConnectionService;
import de.augustakom.hurrican.service.cc.EndstellenService;
import de.augustakom.hurrican.service.cc.ProduktService;
import de.augustakom.hurrican.service.cc.RangierungsService;
import de.augustakom.hurrican.service.cc.ReferenceService;


/**
 * Jasper DS fuer die CrossConnections eines Ports. <br>
 *
 *
 */
public class EQCrossConnectionJasperDS extends AbstractCCJasperDS {

    private static final Logger LOGGER = Logger.getLogger(EQCrossConnectionJasperDS.class);

    private Long auftragId = null;
    private Iterator<EQCrossConnectionView> dataIterator = null;
    private EQCrossConnectionView currentData = null;
    private Map<Long, Reference> crossConnectionTypes = null;

    private EndstellenService endstellenService = null;
    private RangierungsService rangierungsService = null;
    private EQCrossConnectionService eqCrossConnectionService = null;
    private ReferenceService referenceService = null;
    private ProduktService produktService = null;

    /**
     * Konstruktor mit Angabe der Auftrags-ID fuer die die Cross-Connections ermittelt werden sollen.
     *
     * @param egConfig
     * @throws AKReportException
     */
    public EQCrossConnectionJasperDS(Long auftragId) throws AKReportException {
        super();
        this.auftragId = auftragId;
        init();
    }

    /**
     * @see de.augustakom.hurrican.service.cc.impl.reportdata.AbstractCCJasperDS#init()
     */
    @Override
    protected void init() throws AKReportException {
        try {

            endstellenService = getCCService(EndstellenService.class);
            rangierungsService = getCCService(RangierungsService.class);
            eqCrossConnectionService = getCCService(EQCrossConnectionService.class);
            referenceService = getCCService(ReferenceService.class);
            produktService = getCCService(ProduktService.class);

            Produkt produkt = produktService.findProdukt4Auftrag(auftragId);
            if (BooleanTools.nullToFalse(produktService.isVierDrahtProdukt(produkt.getId()))) {
                // bei 4-Draht Auftrag keine CrossConnections anzeigen!
                this.dataIterator = new ArrayList<EQCrossConnectionView>().iterator();
            }
            else {
                List<Reference> references = referenceService.findReferencesByType(Reference.REF_TYPE_XCONN_TYPES, Boolean.FALSE);
                crossConnectionTypes = new HashMap<Long, Reference>();
                CollectionMapConverter.convert2Map(references, crossConnectionTypes, "getId", null);

                List<EQCrossConnectionView> crossConnectionViews = new ArrayList<EQCrossConnectionView>();
                List<Endstelle> endstellen = endstellenService.findEndstellen4Auftrag(auftragId);
                if (CollectionTools.isNotEmpty(endstellen)) {
                    for (Endstelle endstelle : endstellen) {
                        loadCrossConnections(endstelle, crossConnectionViews);
                    }
                }
                this.dataIterator = crossConnectionViews.iterator();
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    /*
     * Laedt die CrossConnection der angegebenen Endstelle und erstellt daraus EQCrossConnectionView-Objekte
     * die der Liste <code>crossConnectionViews</code> hinzugefuegt werden.
     * @param endstelle
     * @param crossConnectionViews
     * @throws FindException
     */
    private void loadCrossConnections(Endstelle endstelle, List<EQCrossConnectionView> crossConnectionViews) throws FindException {
        if (endstelle.getRangierId() != null) {
            Rangierung rangierung = rangierungsService.findRangierung(endstelle.getRangierId());
            if ((rangierung != null) && (rangierung.getEqInId() != null)) {
                Equipment eqIn = rangierungsService.findEquipment(rangierung.getEqInId());
                List<EQCrossConnection> crossConnections =
                        eqCrossConnectionService.findEQCrossConnections(eqIn.getId(), DateTools.getHurricanEndDate());
                if (CollectionTools.isNotEmpty(crossConnections)) {
                    for (EQCrossConnection eqCrossConnection : crossConnections) {
                        EQCrossConnectionView view = new EQCrossConnectionView();

                        Long brasPoolId = eqCrossConnection.getBrasPoolId();
                        if (brasPoolId != null) {
                            BrasPool brasPool = eqCrossConnectionService.findBrasPoolById(brasPoolId);
                            view.atmPort = brasPool.getPort();
                            view.atmSlot = brasPool.getSlot();
                            view.nasIdentifier = brasPool.getNasIdentifier();
                            view.backupAtmPort = brasPool.getBackupPort();
                            view.backupAtmSlot = brasPool.getBackupSlot();
                            view.backupNasIdentifier = brasPool.getBackupNasIdentifier();
                        }

                        view.hwEQN = eqIn.getHwEQN();
                        view.ccType = crossConnectionTypes.get(eqCrossConnection.getCrossConnectionTypeRefId()).getStrValue();
                        view.ntValues = createCrossConnectionValues(eqCrossConnection.getNtInner(), eqCrossConnection.getNtOuter());
                        view.ltValues = createCrossConnectionValues(eqCrossConnection.getLtInner(), eqCrossConnection.getLtOuter());
                        view.brasValues = createCrossConnectionValues(eqCrossConnection.getBrasInner(), eqCrossConnection.getBrasOuter());
                        crossConnectionViews.add(view);
                    }
                }
            }
        }
    }

    /*
     * Generiert aus den beiden angegebenen Werten einen String mit "/" als Trennzeichen.
     * @param inner
     * @param outer
     * @return
     */
    String createCrossConnectionValues(Integer inner, Integer outer) {
        String innerValue = (inner != null) ? "" + inner : null;
        String outerValue = (outer != null) ? "" + outer : null;
        return StringTools.join(new String[] { outerValue, innerValue }, " / ", true);
    }

    /**
     * @see net.sf.jasperreports.engine.JRDataSource#next()
     */
    @Override
    public boolean next() throws JRException {
        boolean hasNext = false;

        if (this.dataIterator != null) {
            hasNext = this.dataIterator.hasNext();
            if (hasNext) {
                this.currentData = this.dataIterator.next();
            }
        }
        return hasNext;
    }

    /**
     * @see de.augustakom.hurrican.service.base.impl.AbstractHurricanJasperDS#getFieldValue(java.lang.String)
     */
    @Override
    protected Object getFieldValue(String field) throws JRException {
        if ("CC_TYPE".equals(field)) {
            return currentData.ccType;
        }
        else if ("HW_EQN".equals(field)) {
            return currentData.hwEQN;
        }
        else if ("NT_VALUES".equals(field)) {
            return currentData.ntValues;
        }
        else if ("LT_VALUES".equals(field)) {
            return currentData.ltValues;
        }
        else if ("BRAS_VALUES".equals(field)) {
            return currentData.brasValues;
        }

        // BRAS pool keys
        else if ("NAS_IDENTIFIER".equals(field)) {
            return currentData.nasIdentifier;
        }
        else if ("ATM_SLOT".equals(field)) {
            return currentData.atmSlot;
        }
        else if ("ATM_PORT".equals(field)) {
            return currentData.atmPort;
        }
        else if ("BACKUP_NAS_IDENTIFIER".equals(field)) {
            return currentData.backupNasIdentifier;
        }
        else if ("BACKUP_ATM_SLOT".equals(field)) {
            return currentData.backupAtmSlot;
        }
        else if ("BACKUP_ATM_PORT".equals(field)) {
            return currentData.backupAtmPort;
        }

        return null;
    }

    /* Hilfsklasse fuer die Jasper-DS. */
    static class EQCrossConnectionView {
        String ccType;
        String hwEQN;
        String ntValues;
        String ltValues;
        String brasValues;

        // values from BRAS pool
        String nasIdentifier;
        Integer atmSlot;
        Integer atmPort;
        String backupNasIdentifier;
        Integer backupAtmSlot;
        Integer backupAtmPort;
    }
}


