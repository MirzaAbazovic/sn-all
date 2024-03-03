/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 08.04.2010 08:24:48
 */
package de.augustakom.hurrican.model.cc.equipment;

import javax.persistence.*;

import de.augustakom.hurrican.model.cc.AbstractCCIDModel;
import de.augustakom.hurrican.model.cc.hardware.HWBaugruppe;
import de.augustakom.hurrican.model.cc.hardware.HWBaugruppenTyp;


/**
 * Modell-Klasse fuer die Definition von Baugruppen-Aenderungen vom Typ 'Baugruppentyp aendern'.
 *
 *
 */
@Entity
@Table(name = "T_HW_BG_CHANGE_BG_TYPE")
@SequenceGenerator(name = "SEQ_GEN", sequenceName = "S_T_HW_BG_CHANGE_BG_TYPE_0", allocationSize = 1)
public class HWBaugruppenChangeBgType extends AbstractCCIDModel {

    public static final String HW_BAUGRUPPE = "hwBaugruppe";
    private HWBaugruppe hwBaugruppe;

    public static final String HW_BAUGRUPPEN_TYP_NEW = "hwBaugruppenTypNew";
    private HWBaugruppenTyp hwBaugruppenTypNew;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "HW_BAUGRUPPEN_ID", nullable = true)
    public HWBaugruppe getHwBaugruppe() {
        return hwBaugruppe;
    }

    public void setHwBaugruppe(HWBaugruppe hwBaugruppe) {
        this.hwBaugruppe = hwBaugruppe;
    }

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "HW_BAUGRUPPENTYP_NEW", nullable = true)
    public HWBaugruppenTyp getHwBaugruppenTypNew() {
        return hwBaugruppenTypNew;
    }

    public void setHwBaugruppenTypNew(HWBaugruppenTyp hwBaugruppenTypNew) {
        this.hwBaugruppenTypNew = hwBaugruppenTypNew;
    }

}


