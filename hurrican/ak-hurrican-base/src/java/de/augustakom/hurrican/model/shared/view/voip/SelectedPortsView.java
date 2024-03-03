/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 21.11.2011 09:06:43
 */
package de.augustakom.hurrican.model.shared.view.voip;

import java.util.*;
import javax.annotation.*;
import com.google.common.collect.Lists;

import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.hurrican.model.cc.AuftragVoIPDN2EGPort;

/**
 *
 */
public final class SelectedPortsView extends Observable {

    @Nonnull
    public static SelectedPortsView createFromAuftragVoipDn2EgPorts(
            @Nonnull final List<AuftragVoIPDN2EGPort> voipDN2EgPorts,
            final Date validFrom, final Date validTo,
            final int numberOfPorts) {
        return new SelectedPortsView(voipDN2EgPorts, validFrom, validTo, numberOfPorts);
    }

    @Nonnull
    public static SelectedPortsView createNewSelectedPorts(@Nonnull final boolean[] portSelection,
            @Nonnull final Date validFrom,
            @Nonnull final Date validTo) {
        return new SelectedPortsView(portSelection, validFrom, validTo);
    }

    public static final class SelectedPort {
        private SelectedPort(Long auftragVoipDn2EgPortId, boolean selected) {
            this.selected = selected;
            this.auftragVoipDn2EgPortId = auftragVoipDn2EgPortId;
        }

        final Long auftragVoipDn2EgPortId;
        boolean selected;

        @Nonnull
        public static SelectedPort createSelectedPort(long auftragVoipDn2EgPortId) {
            return new SelectedPort(auftragVoipDn2EgPortId, true);
        }

        @Nonnull
        public static SelectedPort createUnselectedPort() {
            return new SelectedPort(null, false);
        }

        @Nonnull
        public static SelectedPort createNewselectedPort() {
            return new SelectedPort(null, true);
        }

        public Long getAuftragVoipDn2EgPortId() {
            return auftragVoipDn2EgPortId;
        }

        public boolean isSelected() {
            return selected;
        }
    }

    private SelectedPortsView(@Nonnull final List<AuftragVoIPDN2EGPort> voipDN2EgPorts, @Nonnull final Date validFrom,
            @Nonnull final Date validTo, @Nonnull final int numberOfPorts) {
        this.ports = new SelectedPort[numberOfPorts];
        this.validFrom = validFrom;
        this.validTo = validTo;

        for (int i = 0; i < numberOfPorts; i++) {
            ports[i] = SelectedPort.createUnselectedPort();
        }

        for (final AuftragVoIPDN2EGPort voipDN2EgPort : voipDN2EgPorts) {
            if (DateTools.isDateAfterOrEqual(voipDN2EgPort.getValidFrom(), this.validFrom)
                    && DateTools.isDateBeforeOrEqual(voipDN2EgPort.getValidTo(), this.validTo)) {
                final int index = voipDN2EgPort.getEgPort().getNumber() - 1;
                // Sollte der index >= ports.length sein, so sind mehr AuftragVoIPDN2EGPort im vorfeld konfiguriert
                // worden, als die techn. Leistungen es im Moment angeben.
                if (index < ports.length) {
                    ports[index] = SelectedPort.createSelectedPort(voipDN2EgPort.getId());
                }
            }
        }
    }

    private SelectedPortsView(@Nonnull boolean[] portSelection, @Nonnull final Date validFrom,
            @Nonnull final Date validTo) {
        this.ports = new SelectedPort[portSelection.length];
        this.validFrom = validFrom;
        this.validTo = validTo;

        for (int i = 0; i < portSelection.length; i++) {
            if (portSelection[i]) {
                ports[i] = SelectedPort.createNewselectedPort();
            }
            else {
                ports[i] = SelectedPort.createUnselectedPort();
            }
        }
    }

    @Nonnull
    private final SelectedPort[] ports;
    private Date validFrom;
    private Date validTo;

    public void setValidTo(Date validTo) {
        setChanged();
        this.validTo = validTo;
        notifyObservers();
    }

    @Nonnull
    public Date getValidFrom() {
        return new Date(validFrom.getTime());
    }

    @Nonnull
    public Date getValidTo() {
        return new Date(validTo.getTime());
    }

    public boolean isAllSelected() {
        boolean result = true;
        for (int i = 0; i < getPorts().length; i++) {
            if (!ports[i].selected) {
                result = false;
                break;
            }
        }
        return result;
    }

    public boolean isNoneSelected() {
        boolean noneSelected = true;
        for (int i = 0; i < getPorts().length; i++) {
            if (isPortSelected(i)) {
                noneSelected = false;
            }
        }
        return noneSelected;
    }

    public void deselectAll() {
        fillPorts(false);
    }

    public void selectAll() {
        for (int i = 0; i < ports.length; i++) {
            selectOnIndex(i);
        }
    }

    private void fillPorts(boolean selected) {
        setChanged();
        for (int i = 0; i < ports.length; i++) {
            ports[i].selected = selected;
        }
        notifyObservers();
    }

    public void selectOnIndex(int index) {
        setChanged();
        ports[index].selected = true;
        notifyObservers();
    }

    public void deselectOnIndex(int index) {
        setChanged();
        ports[index].selected = false;
        notifyObservers();
    }

    public boolean isPortSelected(int index) {
        return index < getPorts().length ? getPorts()[index].selected : false;
    }

    public List<Integer> getSelectedPortNumbers() {
        List<Integer> selectedPortNumbers = Lists.newArrayList();
        for (int i = 0; i < ports.length; i++) {
            if (isPortSelected(i)) {
                selectedPortNumbers.add(i + 1);
            }
        }
        return selectedPortNumbers;
    }

    /**
     * @return Returns a copy of the ports.
     */
    @Nonnull
    public SelectedPort[] getPorts() {
        return Arrays.copyOf(ports, ports.length);
    }

    @Nonnull
    public SelectedPort getPortAtIndex(int index) {
        final SelectedPort port = ports[index];
        return new SelectedPort(port.auftragVoipDn2EgPortId, port.selected);
    }

    public int getNumberOfPorts() {
        return ports.length;
    }

    public void selectOnPortNr(int portNr) {
        setChanged();
        selectOnIndex(portNr - 1);
        notifyObservers();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = (prime * result) + Arrays.hashCode(ports);
        return result;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        SelectedPortsView other = (SelectedPortsView) obj;
        if (!Arrays.equals(ports, other.ports)) {
            return false;
        }
        return true;
    }

    @Nonnull
    @Override
    public String toString() {
        return "SelectedPorts [ports=" + Arrays.toString(ports) + "]";
    }
}
