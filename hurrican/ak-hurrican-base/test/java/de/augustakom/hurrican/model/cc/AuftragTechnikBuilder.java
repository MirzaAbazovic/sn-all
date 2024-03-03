/**
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 17.09.2009 11:26:25
 */
package de.augustakom.hurrican.model.cc;

import java.util.*;

import de.augustakom.common.model.DontCreateBuilder;
import de.augustakom.common.model.ReferencedEntityId;
import de.augustakom.common.service.iface.IServiceObject;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.hurrican.model.cc.hardware.HWSwitch;


/**
 *
 */
@SuppressWarnings("unused")
public class AuftragTechnikBuilder extends AbstractCCIDModelBuilder<AuftragTechnikBuilder, AuftragTechnik> implements IServiceObject {

    private AuftragBuilder auftragBuilder;
    private AuftragTechnik2EndstelleBuilder auftragTechnik2EndstelleBuilder;
    private Long auftragId = null;
    private Long intAccountId = null;
    private IntAccountBuilder intAccountBuilder;
    private Date gueltigVon = new Date(0);
    private Date gueltigBis = DateTools.getHurricanEndDate();
    private VerbindungsBezeichnungBuilder vbzBuilder;
    @ReferencedEntityId("vpnId")
    @DontCreateBuilder
    private VPNBuilder vpnBuilder;
    private NiederlassungBuilder niederlassungBuilder;
    private Long auftragsart = null;
    private boolean preventCPSProvisioning;
    private Long projectResponsibleUserId = null;
    private Long projectLeadUserId = null;
    private HWSwitch hwSwitch;

    @Override
    protected void initialize() {
        auftragTechnik2EndstelleBuilder.withAuftragTechnikBuilder(this);
    }

    @Override
    protected void beforeBuild() {
        if (vbzBuilder == null) {
            vbzBuilder = getBuilder(VerbindungsBezeichnungBuilder.class);
        }
    }

    public AuftragBuilder getAuftragBuilder() {
        return auftragBuilder;
    }

    public AuftragTechnik2EndstelleBuilder getAuftragTechnik2EndstelleBuilder() {
        return auftragTechnik2EndstelleBuilder;
    }


    public AuftragTechnikBuilder withAuftragBuilder(AuftragBuilder auftragBuilder) {
        this.auftragBuilder = auftragBuilder;
        if (auftragBuilder.getAuftragTechnikBuilder() != this) {
            auftragBuilder.withAuftragTechnikBuilder(this);
        }
        return this;
    }

    public AuftragTechnikBuilder withEndstelleBuilder(EndstelleBuilder endstelleBuilder) {
        this.withAuftragTechnik2EndstelleBuilder(endstelleBuilder.getEndstelleGruppeBuilder());
        return this;
    }

    public AuftragTechnikBuilder withAuftragTechnik2EndstelleBuilder(AuftragTechnik2EndstelleBuilder auftragTechnik2EndstelleBuilder) {
        this.auftragTechnik2EndstelleBuilder = auftragTechnik2EndstelleBuilder;
        if (auftragTechnik2EndstelleBuilder.getAuftragTechnikBuilder() != this) {
            auftragTechnik2EndstelleBuilder.withAuftragTechnikBuilder(this);
        }
        return this;
    }

    public AuftragTechnikBuilder withNiederlassungBuilder(NiederlassungBuilder niederlassungBuilder) {
        this.niederlassungBuilder = niederlassungBuilder;
        return this;
    }

    public NiederlassungBuilder getNiederlassungBuilder() {
        return niederlassungBuilder;
    }

    public Long getIntAccountId() {
        return intAccountId;
    }

    public AuftragTechnikBuilder withIntAccountId(Long intAccountId) {
        this.intAccountId = intAccountId;
        return this;
    }

    public AuftragTechnikBuilder withIntAccountBuilder(IntAccountBuilder intAccountBuilder) {
        this.intAccountBuilder = intAccountBuilder;
        return this;
    }

    public AuftragTechnikBuilder withVerbindungsBezeichnungBuilder(VerbindungsBezeichnungBuilder verbindungsBezeichnungBuilder) {
        this.vbzBuilder = verbindungsBezeichnungBuilder;
        return this;
    }

    public AuftragTechnikBuilder withVPNBuilder(VPNBuilder vpnBuilder) {
        this.vpnBuilder = vpnBuilder;
        return this;
    }

    public AuftragTechnikBuilder withGueltigBis(Date gueltigBis) {
        this.gueltigBis = gueltigBis;
        return this;
    }

    public AuftragTechnikBuilder withAuftragart(Long auftragsart) {
        this.auftragsart = auftragsart;
        return this;
    }

    public AuftragTechnikBuilder withPreventCPSProvisioning(Boolean preventCPSProvisioning) {
        this.preventCPSProvisioning = preventCPSProvisioning;
        return this;
    }

    public AuftragTechnikBuilder withProjectResponsibleUserId(Long projectResponsibleUserId) {
        this.projectResponsibleUserId = projectResponsibleUserId;
        return this;
    }

    public AuftragTechnikBuilder withProjectLeadUserId(Long projectLeadUserId) {
        this.projectLeadUserId = projectLeadUserId;
        return this;
    }

    public AuftragTechnikBuilder withAuftragId(Long auftragId) {
        this.auftragId = auftragId;
        return this;
    }

    public AuftragTechnikBuilder withHwSwitch(HWSwitch hwSwitch) {
        this.hwSwitch = hwSwitch;
        return this;
    }

}
