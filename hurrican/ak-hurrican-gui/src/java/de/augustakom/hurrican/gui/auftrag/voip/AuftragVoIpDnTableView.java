/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 24.04.13 11:21
 */
package de.augustakom.hurrican.gui.auftrag.voip;

import java.util.*;

import de.augustakom.hurrican.model.shared.view.voip.AuftragVoipDNView;
import de.augustakom.hurrican.model.shared.view.voip.SelectedPortsView;

/**
 *
 */
public class AuftragVoIpDnTableView {

    public AuftragVoIpDnTableView(AuftragVoipDNView auftragVoipDNView, SelectedPortsView selectedPortsView) {
        this.auftragVoipDNView = auftragVoipDNView;
        this.selectedPortsView = selectedPortsView;
    }

    final AuftragVoipDNView auftragVoipDNView;
    final SelectedPortsView selectedPortsView;

    public Date getPortSelectionValidFrom() {
        return selectedPortsView.getValidFrom();
    }

    public Date getPortSelectionValidTo() {
        return selectedPortsView.getValidTo();
    }

    public boolean hasChanged() {
        return selectedPortsView.hasChanged();
    }

    public void notifyObservers() {
        selectedPortsView.notifyObservers();
    }

    public void notifyObservers(Object arg) {
        selectedPortsView.notifyObservers(arg);
    }

    public void addObserver(Observer o) {
        selectedPortsView.addObserver(o);
    }

    public int getMaxPortCount() {
        return selectedPortsView.getNumberOfPorts();
    }

    public boolean isBlock() {
        return auftragVoipDNView.isBlock();
    }

    public Boolean getMainNumber() {
        return auftragVoipDNView.getMainNumber();
    }

    public Long getAuftragId() {
        return auftragVoipDNView.getAuftragId();
    }

    public String getOnKz() {
        return auftragVoipDNView.getOnKz();
    }

    public String getDnBase() {
        return auftragVoipDNView.getDnBase();
    }

    public Long getDnNoOrig() {
        return auftragVoipDNView.getDnNoOrig();
    }

    public Date getGueltigBis() {
        return auftragVoipDNView.getGueltigBis();
    }

    public Date getGueltigVon() {
        return auftragVoipDNView.getGueltigVon();
    }

    public String getDirectDial() {
        return auftragVoipDNView.getDirectDial();
    }

    public String getRangeFrom() {
        return auftragVoipDNView.getRangeFrom();
    }

    public String getRangeTo() {
        return auftragVoipDNView.getRangeTo();
    }

    public boolean isAllSelected() {
        return selectedPortsView.isAllSelected();
    }

    public boolean isPortSelected(int index) {
        return selectedPortsView.isPortSelected(index);
    }

    public int getNumberOfPorts() {
        return selectedPortsView.getNumberOfPorts();
    }

    public void selectOnIndex(int index) {
        selectedPortsView.selectOnIndex(index);
    }

    public void deselectAll() {
        selectedPortsView.deselectAll();
    }

    public void deselectOnIndex(int index) {
        selectedPortsView.deselectOnIndex(index);
    }

    public void selectAll() {
        selectedPortsView.selectAll();
    }
}
