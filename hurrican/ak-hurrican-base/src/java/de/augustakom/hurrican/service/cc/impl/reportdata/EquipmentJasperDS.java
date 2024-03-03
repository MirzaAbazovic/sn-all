/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 29.03.2005 13:08:18
 */
package de.augustakom.hurrican.service.cc.impl.reportdata;

import java.util.*;
import net.sf.jasperreports.engine.JRException;
import org.apache.log4j.Logger;

import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.common.tools.lang.NumberTools;
import de.augustakom.common.tools.lang.Pair;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.cc.Equipment;
import de.augustakom.hurrican.model.cc.Rangierung;
import de.augustakom.hurrican.model.cc.hardware.HWBaugruppe;
import de.augustakom.hurrican.model.cc.hardware.HWBaugruppenTyp;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.cc.EndstellenService;
import de.augustakom.hurrican.service.cc.HWService;
import de.augustakom.hurrican.service.cc.RangierungsService;


/**
 * Jasper-DataSource, um die Equipment-Daten einer Endstelle zu laden.
 *
 *
 */
public class EquipmentJasperDS extends AbstractCCJasperDS {

    private static final Logger LOGGER = Logger.getLogger(EquipmentJasperDS.class);

    private Long esId = null;

    private Long eqInId = null;

    private Iterator<Pair<Equipment, HWBaugruppenTyp>> dataIterator = null;
    private Pair<Equipment, HWBaugruppenTyp> currentData = null;

    /**
     * Konstruktor mit Angabe der Endstellen-ID.
     *
     * @param esId ID der Endstelle, deren Equipment-Daten geladen werden sollen.
     */
    public EquipmentJasperDS(Long esId) {
        super();
        this.esId = esId;
        init();
    }

    /**
     * @see de.augustakom.hurrican.service.cc.impl.reportdata.AbstractCCJasperDS#init()
     */
    @Override
    protected final void init() {
        try {
            EndstellenService esSrv = getCCService(EndstellenService.class);
            Endstelle endstelle = esSrv.findEndstelle(esId);

            if ((endstelle != null) && (endstelle.getRangierId() != null)) {
                RangierungsService rs = getCCService(RangierungsService.class);
                Rangierung rangierung = rs.findRangierung(endstelle.getRangierId());
                if (rangierung != null) {
                    List<Pair<Equipment, HWBaugruppenTyp>> equipments = new ArrayList<>();
                    Equipment eqIn = rs.findEquipment(rangierung.getEqInId());
                    if (eqIn != null) {
                        eqInId = eqIn.getId();
                        equipments.add(Pair.create(eqIn, getHWBaugruppenTyp(eqIn)));
                    }

                    Equipment eqOut = rs.findEquipment(rangierung.getEqOutId());
                    if (eqOut != null) {
                        equipments.add(Pair.create(eqOut, getHWBaugruppenTyp(eqOut)));
                    }
                    dataIterator = equipments.iterator();
                }
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    HWBaugruppenTyp getHWBaugruppenTyp(Equipment equipment) throws ServiceNotFoundException, FindException {
        if ((equipment != null) && (equipment.getHwBaugruppenId() != null)) {
            HWService hwService = getCCService(HWService.class);
            HWBaugruppe hwBg = hwService.findBaugruppe(equipment.getHwBaugruppenId());
            return (hwBg != null) ? hwBg.getHwBaugruppenTyp() : null;
        }
        return null;
    }

    /**
     * @see net.sf.jasperreports.engine.JRDataSource#next()
     */
    @Override
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

    /**
     * @see de.augustakom.hurrican.service.base.impl.AbstractHurricanJasperDS#getFieldValue(java.lang.String)
     */
    @Override
    protected Object getFieldValue(String field) throws JRException {
        if (currentData != null) {
            if ("EQ_IN".equals(field)) {
                return NumberTools.equal(eqInId, currentData.getFirst().getId());
            }
            else if ("SWITCH".equals(field)) {
                return currentData.getFirst().getHwSwitch() != null ? currentData.getFirst().getHwSwitch().getName() : null;
            }
            else if ("TS1".equals(field)) {
                return currentData.getFirst().getTs1();
            }
            else if ("TS2".equals(field)) {
                return currentData.getFirst().getTs2();
            }
            else if ("HWPORT".equals(field)) {
                return (currentData.getSecond() != null) ? currentData.getSecond().getName() : "";
            }
            else if ("HWEQN".equals(field)) {
                return currentData.getFirst().getHwEQN();
            }
            else if ("VERTEILER".equals(field)) {
                return currentData.getFirst().getRangVerteiler();
            }
            else if ("REIHE".equals(field)) {
                return currentData.getFirst().getRangReihe();
            }
            else if ("BUCHT".equals(field)) {
                return currentData.getFirst().getRangBucht();
            }
            else if ("LEISTE1".equals(field)) {
                return currentData.getFirst().getRangLeiste1();
            }
            else if ("STIFT1".equals(field)) {
                return currentData.getFirst().getRangStift1();
            }
            else if ("LEISTE2".equals(field)) {
                return currentData.getFirst().getRangLeiste2();
            }
            else if ("STIFT2".equals(field)) {
                return currentData.getFirst().getRangStift2();
            }
            else if ("SCHNITTSTELLE".equals(field)) {
                return (currentData.getFirst().getRangSchnittstelle() != null) ? currentData.getFirst().getRangSchnittstelle().name() : null;
            }
            else if ("HWSCHNITTSTELLE".equals(field)) {
                return currentData.getFirst().getHwSchnittstelle();
            }
        }

        return null;
    }

}


