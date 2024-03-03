/*
 * Copyright (c) 2008 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 15.12.2008 13:23:17
 */
package de.augustakom.hurrican.model.cc.hardware;

import javax.persistence.*;

import de.augustakom.common.tools.lang.NumberTools;
import de.augustakom.common.tools.lang.StringTools;
import de.augustakom.hurrican.model.cc.AbstractCCHistoryModel;
import de.augustakom.hurrican.model.shared.iface.HvtIdStandortModel;


/**
 * Modell, um ein Hardware-Rack abzubilden.
 *
 *
 */
public class HWRack extends AbstractCCHistoryModel implements HvtIdStandortModel {
    private static final long serialVersionUID = -6759475336601422919L;
    /**
     * Konstante fuer Parameter 'rackTyp'. Definiert ein Rack vom Typ 'DSLAM'
     */
    public static final String RACK_TYPE_DSLAM = "DSLAM";
    /**
     * Konstante fuer Parameter 'rackTyp'. Definiert ein Rack vom Typ 'DLU'
     */
    public static final String RACK_TYPE_DLU = "DLU";
    /**
     * Konstante fuer Parameter 'rackTyp'. Definiert ein Rack vom Typ 'LTG'
     */
    public static final String RACK_TYPE_LTG = "LTG";
    /**
     * Konstante fuer Parameter 'rackTyp'. Definiert ein Rack vom Typ 'ROUTER'
     */
    public static final String RACK_TYPE_ROUTER = "ROUTER";
    /**
     * Konstante fuer Parameter 'rackTyp'. Definiert ein Rack vom Typ 'MDU'
     */
    public static final String RACK_TYPE_MDU = "MDU";
    /**
     * Konstante fuer Parameter 'rackTyp'. Definiert ein Rack vom Typ 'ONT'
     */
    public static final String RACK_TYPE_ONT = "ONT";
    /**
     * Konstante fuer Parameter 'rackTyp'. Definiert ein Rack vom Typ 'DPO'
     */
    public static final String RACK_TYPE_DPO = "DPO";
    /**
     * Konstante fuer Parameter 'rackTyp'. Definiert ein Rack vom Typ 'DPU'
     */
    public static final String RACK_TYPE_DPU = "DPU";
    /**
     * Konstante fuer Parameter 'rackTyp'. Definiert ein Rack vom Typ 'OLT'
     */
    public static final String RACK_TYPE_OLT = "OLT";
    /**
     * Konstante fuer Parameter 'rackTyp'. Definiert ein Rack vom Typ 'PDH'
     */
    public static final String RACK_TYPE_PDH = "PDH";
    /**
     * Konstante fuer Parameter 'rackTyp'. Definiert ein Rack vom Typ 'SDH'
     */
    public static final String RACK_TYPE_SDH = "SDH";
    /**
     * Konstante fuer Parameter 'rackTyp'. Definiert ein Rack vom Typ 'UEVT'
     */
    public static final String RACK_TYPE_UEVT = "UEVT";

    /**
     * Liefert ein Klassenobjekt des gegebenen RackTyps zurueck
     */
    public static Class<? extends HWRack> getRackClass(String type) {
        if (RACK_TYPE_DSLAM.equals(type)) {
            return HWDslam.class;
        }
        if (RACK_TYPE_DLU.equals(type)) {
            return HWDlu.class;
        }
        if (RACK_TYPE_LTG.equals(type)) {
            return HWLtg.class;
        }
        if (RACK_TYPE_MDU.equals(type)) {
            return HWMdu.class;
        }
        if (RACK_TYPE_ONT.equals(type)) {
            return HWOnt.class;
        }
        if (RACK_TYPE_DPO.equals(type)) {
            return HWDpo.class;
        }
        if (RACK_TYPE_OLT.equals(type)) {
            return HWOlt.class;
        }
        if (RACK_TYPE_DPU.equals(type)) {
            return HWDpu.class;
        }
        if (RACK_TYPE_PDH.equals(type)) {
            return HWPdh.class;
        }
        if (RACK_TYPE_ROUTER.equals(type)) {
            return HWRouter.class;
        }
        if (RACK_TYPE_SDH.equals(type)) {
            return HWSdh.class;
        }
        if (RACK_TYPE_UEVT.equals(type)) {
            return HWRack.class;
        }
        return null;
    }


    private Long hvtIdStandort = null;
    public static final String HVT_STANDORT_ID = "hvtIdStandort";
    private String rackTyp = null;
    public static final String RACK_TYP = "rackTyp";
    private String anlagenBez = null;
    public static final String ANLAGEN_BEZ = "anlagenBez";
    private String managementBez = null;
    public static final String MANAGEMENT_BEZ = "managementBez";

    /**
     * Im Falle einer ONT bezieht sich die GeraeteBez auf den Raum am Standort, an dem "irgendeine" Ont verbaut ist. Die
     * physikalische ONT wird durch Ihre Seriennr. identifiziert
     */
    private String geraeteBez = null;


    public static final String GERAETE_BEZ = "geraeteBez";
    private Long hwProducer = null;
    public static final String HW_PRODUCER = "hwProducer";
    private Long hvtRaumId = null;


    /**
     * Ueberprueft, ob das Rack von dem angegebenen Hersteller ist.
     *
     * @param hwProducerId
     * @return
     */
    public boolean isHwOfProducer(Long hwProducerId) {
        return NumberTools.equal(getHwProducer(), hwProducerId);
    }

    /**
     * Prueft ob es sich um ein {@link HWDpo} oder  {@link HWOlt} handelt.
     * @return true bei {@link HWDpo} oder {@link HWOlt}, ansonsten false
     */
    @Transient
    public boolean isDpoOrOntRack() {
        return (isDpoRack() || HWRack.RACK_TYPE_ONT.equals(this.getRackTyp()));
    }

    /**
     * Prueft ob es sich um ein {@link HWDpo}.
     * @return true bei {@link HWDpo}, ansonsten false
     */
    @Transient
    public boolean isDpoRack() {
        return HWRack.RACK_TYPE_DPO.equals(this.getRackTyp());
    }

    /**
     * Prueft ob es sich um ein {@link HWDpu}.
     *
     * @return true bei {@link HWDpu}, ansonsten false
     */
    @Transient
    public boolean isDpuRack() {
        return HWRack.RACK_TYPE_DPU.equals(this.getRackTyp());
    }

    /**
     * Gibt den Rack Typen, 'anlagenBez' und 'geraeteBez' kombiniert zurueck.
     */
    @Transient
    public String getDisplay() {
        return StringTools.join(new String[] { this.getClass().getSimpleName().substring(2).toUpperCase(),
                getAnlagenBez(), getGeraeteBez() }, " - ", true);
    }

    /**
     * Gibt die Management-Bezeichnung der Baugruppe zurueck. Dies ist der Name der Baugruppe, wie er in den
     * verschiedenen Management-Systemen der Netztechnik hinterlegt ist.
     *
     * @return managementBez
     */
    public String getManagementBez() {
        return managementBez;
    }

    /**
     * Definiert eine Management-Bezeichnung fuer die Baugruppe. Dies ist der Name der Baugruppe, wie er in den
     * verschiedenen Management-Systemen der Netztechnik hinterlegt ist.
     *
     * @param managementBez Festzulegende managementBez
     */
    public void setManagementBez(String managementBez) {
        this.managementBez = managementBez;
    }

    public String getAnlagenBez() {
        return anlagenBez;
    }

    public void setAnlagenBez(String anlagenBez) {
        this.anlagenBez = anlagenBez;
    }

    public String getGeraeteBez() {
        return geraeteBez;
    }

    public void setGeraeteBez(String geraeteBez) {
        this.geraeteBez = geraeteBez;
    }

    @Override
    public Long getHvtIdStandort() {
        return hvtIdStandort;
    }

    public void setHvtIdStandort(Long hvtIdStandort) {
        this.hvtIdStandort = hvtIdStandort;
    }

    public Long getHvtRaumId() {
        return hvtRaumId;
    }

    public void setHvtRaumId(Long hvtRaumId) {
        this.hvtRaumId = hvtRaumId;
    }

    public Long getHwProducer() {
        return hwProducer;
    }

    public void setHwProducer(Long hwProducer) {
        this.hwProducer = hwProducer;
    }

    public String getRackTyp() {
        return rackTyp;
    }

    public void setRackTyp(String rackTyp) {
        this.rackTyp = rackTyp;
    }
}


