/**
 * Copyright (c) 2010 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 08.12.2010
 */

package de.augustakom.hurrican.model.cc;

import javax.persistence.*;
import javax.validation.constraints.*;

import de.augustakom.hurrican.model.cc.hardware.HWSwitch;
import de.augustakom.hurrican.model.shared.iface.CCAuftragModel;

/**
 * Modell-Klasse fuer SIP Intertrunk Auftraege, die Switch/TrunkGroup Kombinationen halten.
 *
 *
 */
@Entity
@Table(name = "T_AUFTRAG_SIP_INTER_TRUNK")
@SequenceGenerator(name = "SEQ_GEN", sequenceName = "S_T_AUFTRAG_SIP_INTER_TRUNK_0", allocationSize = 1)
public class AuftragSIPInterTrunk extends AbstractCCIDModel implements CCAuftragModel {

    private Long auftragId;
    private HWSwitch hwSwitch = null;
    private String trunkGroup = null;
    private String userW = null;

    @Override
    @Column(name = "AUFTRAG_ID")
    @NotNull
    public Long getAuftragId() {
        return auftragId;
    }

    @Override
    public void setAuftragId(Long auftragId) {
        this.auftragId = auftragId;
    }

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "HW_SWITCH", nullable = false)
    public HWSwitch getHwSwitch() {
        return hwSwitch;
    }

    public void setHwSwitch(HWSwitch hwSwitch) {
        this.hwSwitch = hwSwitch;
    }

    @Column(name = "TRUNK_GROUP")
    public String getTrunkGroup() {
        return trunkGroup;
    }

    public void setTrunkGroup(String trunkGroup) {
        this.trunkGroup = trunkGroup;
    }

    @Column(name = "USERW")
    public String getUserW() {
        return userW;
    }

    public void setUserW(String userW) {
        this.userW = userW;
    }

}
