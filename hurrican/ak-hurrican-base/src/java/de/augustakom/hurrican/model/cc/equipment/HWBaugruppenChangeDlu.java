/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 08.04.2010 08:30:50
 */
package de.augustakom.hurrican.model.cc.equipment;

import java.util.*;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.apache.commons.lang.StringUtils;

import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.hurrican.model.cc.AbstractCCIDModel;
import de.augustakom.hurrican.model.cc.hardware.HWRack;
import de.augustakom.hurrican.model.cc.hardware.HWSwitch;


/**
 * Modell-Klasse fuer die Definition von Baugruppen-Aenderungen vom Typ 'DLU Wechsel'.
 */
@Entity
@Table(name = "T_HW_BG_CHANGE_DLU")
@SequenceGenerator(name = "SEQ_GEN", sequenceName = "S_T_HW_BG_CHANGE_DLU_0", allocationSize = 1)
public class HWBaugruppenChangeDlu extends AbstractCCIDModel {

    public static final String DLU_RACK_OLD = "dluRackOld";
    private HWRack dluRackOld;

    public static final String DLU_NUMBER_NEW = "dluNumberNew";
    private String dluNumberNew;

    public static final String DLU_SWITCH_NEW = "dluSwitchNew";
    private HWSwitch dluSwitchNew;

    public static final String DLU_MEDIAGATEWAY_NEW = "dluMediaGatewayNew";
    private String dluMediaGatewayNew;

    public static final String DLU_ACCESSCONTROLLER_NEW = "dluAccessControllerNew";
    private String dluAccessControllerNew;


    /**
     * Prueft, ob die Angaben fuer den DLU-Schwenk sinnvoll sind. Dies ist dann der Fall, wenn MediaGateway,
     * AccessController und V5-Mapping jeweils gesetzt oder leer sind.
     *
     * @return
     */
    public boolean isValid(List<HWBaugruppenChangeDluV5> v5Mappings) {
        if (StringUtils.isBlank(getDluAccessControllerNew())
                && StringUtils.isBlank(getDluMediaGatewayNew())
                && CollectionTools.isEmpty(v5Mappings)) {
            return true;
        }
        else if (StringUtils.isNotBlank(getDluAccessControllerNew())
                && StringUtils.isNotBlank(getDluMediaGatewayNew())
                && CollectionTools.isNotEmpty(v5Mappings)) {
            return true;
        }

        return false;
    }

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "DLU_RACK_ID_OLD", nullable = true)
    public HWRack getDluRackOld() {
        return dluRackOld;
    }

    public void setDluRackOld(HWRack dluRackOld) {
        this.dluRackOld = dluRackOld;
    }

    @Column(name = "DLU_NUMBER_NEW")
    @NotNull
    public String getDluNumberNew() {
        return dluNumberNew;
    }

    public void setDluNumberNew(String dluNumberNew) {
        this.dluNumberNew = dluNumberNew;
    }

    @ManyToOne
    @JoinColumn(name = "DLU_SWITCH_NEW", nullable = false)
    @NotNull
    public HWSwitch getDluSwitchNew() {
        return dluSwitchNew;
    }

    public void setDluSwitchNew(HWSwitch dluSwitchNew) {
        this.dluSwitchNew = dluSwitchNew;
    }

    @Column(name = "DLU_MEDIAGATEWAY_NEW")
    public String getDluMediaGatewayNew() {
        return dluMediaGatewayNew;
    }

    public void setDluMediaGatewayNew(String dluMediaGatewayNew) {
        this.dluMediaGatewayNew = dluMediaGatewayNew;
    }

    @Column(name = "DLU_ACCESSCONTROLLER_NEW")
    public String getDluAccessControllerNew() {
        return dluAccessControllerNew;
    }

    public void setDluAccessControllerNew(String dluAccessControllerNew) {
        this.dluAccessControllerNew = dluAccessControllerNew;
    }


}
