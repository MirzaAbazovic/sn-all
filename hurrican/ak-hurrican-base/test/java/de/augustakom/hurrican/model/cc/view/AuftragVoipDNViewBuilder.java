/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 18.11.2011 13:45:26
 */
package de.augustakom.hurrican.model.cc.view;

import java.util.*;
import com.google.common.collect.Lists;

import de.augustakom.common.model.EntityBuilder;
import de.augustakom.hurrican.model.cc.AuftragVoIPDN2EGPort;
import de.augustakom.hurrican.model.cc.Reference;
import de.augustakom.hurrican.model.cc.VoipDnPlanView;
import de.augustakom.hurrican.model.shared.view.voip.AuftragVoipDNView;
import de.augustakom.hurrican.model.shared.view.voip.BlockDNView;
import de.augustakom.hurrican.model.shared.view.voip.SelectedPortsView;

/**
 * nicht persistierender Builder für Objekte des Typs {@link AuftragVoipDNView}
 */
@SuppressWarnings("unused")
public class AuftragVoipDNViewBuilder extends EntityBuilder<AuftragVoipDNViewBuilder, AuftragVoipDNView> {

    public static class SelectedPortsBuilder {
        private SelectedPortsView selectedPorts = null;

        public SelectedPortsBuilder(final List<AuftragVoIPDN2EGPort> auftragVoIPDN2EGPorts, final Date validFrom,
                final Date validTo, final int numberOfPorts) {
            selectedPorts = SelectedPortsView.
                    createFromAuftragVoipDn2EgPorts(auftragVoIPDN2EGPorts, validFrom, validTo, numberOfPorts);
        }

        public SelectedPortsBuilder(final boolean[] portSelection, final Date validFrom, final Date validTo) {
            selectedPorts = SelectedPortsView.createNewSelectedPorts(portSelection, validFrom, validTo);
        }

        public SelectedPortsBuilder withPortNrSelected(int portNr) {
            this.selectedPorts.selectOnPortNr(portNr);
            return this;
        }

        public SelectedPortsView build() {
            return selectedPorts;
        }
    }

    private Long auftragId;
    private String onKz;
    private String dnBase;
    private Long dnNoOrig;
    private Date gueltigVon;
    private Date gueltigBis;
    private String histStatus;
    private Boolean mainNumber;
    private BlockDNView block = BlockDNView.NO_BLOCK;
    private String taifunDescription;
    private String sipPassword;
    private Reference sipDomain;
    private List<SelectedPortsView> selectedPorts = Lists.newArrayList();
    private boolean changed; // wegen 'extends Observable'
    private List<VoipDnPlanView> voipDnPlanViews = Lists.newArrayList();

    public AuftragVoipDNViewBuilder() {
        setPersist(false);
    }

    public AuftragVoipDNViewBuilder withDnBase(String dnBase) {
        this.dnBase = dnBase;
        return this;
    }

    public AuftragVoipDNViewBuilder withSipDomain(final Reference sipDomain) {
        this.sipDomain = sipDomain;
        return this;
    }

    public AuftragVoipDNViewBuilder withAuftragId(Long id) {
        this.auftragId = id;
        return this;
    }

    public AuftragVoipDNViewBuilder withOnKz(String onKz) {
        this.onKz = onKz;
        return this;
    }

    public AuftragVoipDNViewBuilder withDnNoOrig(Long dnNoOrig) {
        this.dnNoOrig = dnNoOrig;
        return this;
    }

    public AuftragVoipDNViewBuilder withGueltigVon(Date gueltigVon) {
        this.gueltigVon = gueltigVon;
        return this;
    }

    public AuftragVoipDNViewBuilder withGueltigBis(Date gueltigBis) {
        this.gueltigBis = gueltigBis;
        return this;
    }

    public AuftragVoipDNViewBuilder withHistStatus(String histStatus) {
        this.histStatus = histStatus;
        return this;
    }

    public AuftragVoipDNViewBuilder withMainNumber(Boolean mainNumber) {
        this.mainNumber = mainNumber;
        return this;
    }

    public AuftragVoipDNViewBuilder withBlock(BlockDNView block) {
        this.block = block;
        return this;
    }

    public AuftragVoipDNViewBuilder withTaifunDescription(String taifunDescription) {
        this.taifunDescription = taifunDescription;
        return this;
    }

    public AuftragVoipDNViewBuilder addSelectedPorts(SelectedPortsBuilder selectedPortsBuilder) {
        this.selectedPorts.add(selectedPortsBuilder.build());
        return this;
    }

    public AuftragVoipDNViewBuilder withSipPassword(String sipPassword) {
        this.sipPassword = sipPassword;
        return this;
    }

    public AuftragVoipDNViewBuilder withVoipDnPlanViews(List<VoipDnPlanView> voipDnPlanViews) {
        this.voipDnPlanViews = voipDnPlanViews;
        return this;
    }
}
