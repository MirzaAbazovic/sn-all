/*
 * Copyright (c) 2007 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 15.10.2007 13:08:18
 */
package de.augustakom.hurrican.service.cc.impl.reportdata;

import java.util.*;
import net.sf.jasperreports.engine.JRException;
import org.apache.log4j.Logger;

import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.common.tools.lang.NumberTools;
import de.augustakom.common.tools.reports.AKReportException;
import de.augustakom.hurrican.model.cc.Equipment;
import de.augustakom.hurrican.model.cc.HVTGruppe;
import de.augustakom.hurrican.model.cc.PhysikTyp;
import de.augustakom.hurrican.model.cc.Rangierung;
import de.augustakom.hurrican.model.cc.RangierungsAuftrag;
import de.augustakom.hurrican.model.cc.innenauftrag.IA;
import de.augustakom.hurrican.service.cc.HVTService;
import de.augustakom.hurrican.service.cc.InnenauftragService;
import de.augustakom.hurrican.service.cc.PhysikService;
import de.augustakom.hurrican.service.cc.RangierungAdminService;
import de.augustakom.hurrican.service.cc.RangierungsService;


/**
 * Jasper-DataSource, um die Rangierungsdaten fuer einen bestimmten Rangierungs-Auftrag zu laden.
 *
 *
 */
public class RangierungslisteJasperDS extends AbstractCCJasperDS {

    private static final Logger LOGGER = Logger.getLogger(RangierungslisteJasperDS.class);

    private Long raId = null;

    private RangierungsAuftrag rangierungsAuftrag = null;
    private HVTGruppe hvtGruppe = null;
    private PhysikTyp physiktypParent = null;
    private PhysikTyp physiktypChild = null;

    private Iterator<Rangierung> dataIterator = null;
    private Rangierung currentData = null;
    private IA innenAuftrag = null;

    private Map<Long, Equipment> rangierung2EqIn = null;
    private Map<Long, Equipment> rangierung2EqOut = null;

    /**
     * Konstruktor mit Angabe der ID vom Rangierungsauftrag.
     *
     * @param raId ID des Rangierungsauftrags. werden sollen.
     * @throws AKReportException
     */
    public RangierungslisteJasperDS(Long raId) throws AKReportException {
        super();
        this.raId = raId;
        init();
    }

    /**
     * @see de.augustakom.hurrican.service.cc.impl.reportdata.AbstractCCJasperDS#init()
     */
    @Override
    protected void init() throws AKReportException {
        try {
            rangierung2EqIn = new HashMap<Long, Equipment>();
            rangierung2EqOut = new HashMap<Long, Equipment>();

            RangierungAdminService ras = getCCService(RangierungAdminService.class);
            rangierungsAuftrag = ras.findRA(raId);
            if (rangierungsAuftrag == null) {
                throw new AKReportException("Rangierungsauftrag konnte nicht ermittelt werden!");
            }

            // Innenauftrag laden
            InnenauftragService ias = getCCService(InnenauftragService.class);
            innenAuftrag = ias.findIA4RangierungsAuftrag(raId);

            List<Rangierung> rangierungen = ras.findRangierungen4RA(raId);
            // Rangierungen nach LtgLfdNr und ID sortieren
            if (CollectionTools.isNotEmpty(rangierungen)) {
                Collections.sort(rangierungen, new Comparator<Rangierung>() {
                    public int compare(Rangierung r1, Rangierung r2) {
                        if (r1.getLeitungLfdNr() != null) {
                            if (NumberTools.isLess(r1.getLeitungLfdNr(), r2.getLeitungLfdNr())) {
                                return -1;
                            }
                            else if (NumberTools.isGreater(r1.getLeitungLfdNr(), r2.getLeitungLfdNr())) {
                                return 1;
                            }
                            else {
                                // nach ID sortieren
                                if (NumberTools.isLess(r1.getId(), r2.getId())) {
                                    return -1;
                                }
                                else if (NumberTools.isGreater(r1.getId(), r2.getId())) {
                                    return 1;
                                }
                            }
                        }

                        return 0;
                    }
                });
                RangierungsService rs = getCCService(RangierungsService.class);
                for (Rangierung r : rangierungen) {
                    Equipment eqIn = (r.getEqInId() != null) ? rs.findEquipment(r.getEqInId()) : null;
                    rangierung2EqIn.put(r.getId(), eqIn);

                    Equipment eqOut = (r.getEqOutId() != null) ? rs.findEquipment(r.getEqOutId()) : null;
                    rangierung2EqOut.put(r.getId(), eqOut);
                }

                dataIterator = rangierungen.iterator();
            }
            else {
                dataIterator = null;
                throw new AKReportException("Es konnten keine Rangierungen ermittelt werden!");
            }

            HVTService hvts = getCCService(HVTService.class);
            hvtGruppe = hvts.findHVTGruppe4Standort(rangierungsAuftrag.getHvtStandortId());

            PhysikService ps = getCCService(PhysikService.class);
            physiktypParent = ps.findPhysikTyp(rangierungsAuftrag.getPhysiktypParent());
            physiktypChild = (rangierungsAuftrag.getPhysiktypChild() != null)
                    ? ps.findPhysikTyp(rangierungsAuftrag.getPhysiktypChild()) : null;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new AKReportException("Fehler beim Erstellen der Rangierungsliste: " + e.getMessage(), e);
        }
    }

    /**
     * @see net.sf.jasperreports.engine.JRDataSource#next()
     */
    public boolean next() throws JRException {
        boolean hasNext = false;

        if (dataIterator != null) {
            hasNext = dataIterator.hasNext();
            if (hasNext) {
                currentData = dataIterator.next();
            }
        }
        return hasNext;
    }

    @Override
    protected Object getFieldValue(String field) throws JRException {
        if (currentData != null) {
            Equipment eqIn = rangierung2EqIn.get(currentData.getId());
            Equipment eqOut = rangierung2EqOut.get(currentData.getId());

            if ("RA_ID".equals(field)) {
                return rangierungsAuftrag.getId();
            }
            else if ("HVT".equals(field)) {
                return hvtGruppe.getOrtsteil();
            }
            else if ("FAELLIG_AM".equals(field)) {
                return rangierungsAuftrag.getFaelligAm();
            }
            else if ("PORT_COUNT".equals(field)) {
                return rangierungsAuftrag.getAnzahlPorts();
            }
            else if ("PHYSIKTYPEN".equals(field)) {
                StringBuilder sb = new StringBuilder(physiktypParent.getName());
                if (physiktypChild != null) {
                    sb.append(" + ").append(physiktypChild.getName());
                }
                return sb.toString();
            }
            else if ("HW_EQN_IN".equals(field)) {
                String eqn = (eqIn != null) ? eqIn.getHwEQN() : null;
                return (eqn != null) ? eqn : " ";
            }
            else if ("HW_EQN_OUT".equals(field)) {
                String eqn = (eqOut != null) ? eqOut.getHwEQN() : null;
                return (eqn != null) ? eqn : " ";
            }
            else if ("IN_PORT".equals(field)) {
                return (eqIn != null) ? eqIn.getEinbau1() : " ";
            }
            else if ("OUT_PORT".equals(field)) {
                return (eqOut != null) ? eqOut.getEinbau1() : " ";
            }
            else if ("IA_NO".equals(field)) {

                return (innenAuftrag != null) ? innenAuftrag.getIaNummer() : " ";
            }
        }

        return null;
    }

}


