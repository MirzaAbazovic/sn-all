/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 27.12.2011 10:45:18
 */
package de.augustakom.hurrican.model.cc.view;

import java.util.*;

import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.hurrican.model.cc.AbstractCCModel;
import de.augustakom.hurrican.model.cc.AuftragHousingKey;
import de.augustakom.hurrican.model.cc.housing.Transponder;
import de.augustakom.hurrican.model.cc.housing.TransponderGroup;

/**
 * View-Klasse zur Darstellung von Housing-Keys.
 */
public class AuftragHousingKeyView extends AbstractCCModel {

    private Long auftragHousingKeyId;
    private Long transponderId;
    private String transponderGroupDescription;
    private String customerFirstName;
    private String customerLastName;

    /**
     * Erstellt aus dem angegebenen {@link AuftragHousingKey} Objekt eine Liste von {@link AuftragHousingKeyView}
     * Objekten. <br> Falls der {@link AuftragHousingKey} eine Transponder-Gruppe hinterlegt hat, wird fuer jeden
     * Transponder aus der Transponder-Gruppe ein {@link AuftragHousingKeyView} Objekt erstellt und der Result-Liste
     * zugeordnet.
     */
    public static List<AuftragHousingKeyView> createAuftragHousingKeyView(AuftragHousingKey auftragHousingKey) {
        List<AuftragHousingKeyView> views = new ArrayList<AuftragHousingKeyView>();
        if (auftragHousingKey.getTransponderGroup() != null) {
            TransponderGroup transponderGroup = auftragHousingKey.getTransponderGroup();
            if (CollectionTools.isNotEmpty(transponderGroup.getTransponders())) {
                for (Transponder transponder : transponderGroup.getTransponders()) {
                    AuftragHousingKeyView view = new AuftragHousingKeyView();
                    view.setAuftragHousingKeyId(auftragHousingKey.getId());
                    view.setTransponderGroupDescription(transponderGroup.getTransponderDescription());
                    view.setTransponderId(transponder.getTransponderId());
                    view.setCustomerFirstName(transponder.getCustomerFirstName());
                    view.setCustomerLastName(transponder.getCustomerLastName());
                    views.add(view);
                }
            }
        }
        else if (auftragHousingKey.getTransponder() != null) {
            AuftragHousingKeyView view = new AuftragHousingKeyView();
            view.setAuftragHousingKeyId(auftragHousingKey.getId());
            view.setTransponderId(auftragHousingKey.getTransponder().getTransponderId());
            view.setCustomerFirstName(auftragHousingKey.getTransponder().getCustomerFirstName());
            view.setCustomerLastName(auftragHousingKey.getTransponder().getCustomerLastName());
            views.add(view);
        }

        return views;
    }

    public Long getTransponderId() {
        return transponderId;
    }

    public void setTransponderId(Long transponderId) {
        this.transponderId = transponderId;
    }

    public String getTransponderGroupDescription() {
        return transponderGroupDescription;
    }

    public void setTransponderGroupDescription(String transponderGroupDescription) {
        this.transponderGroupDescription = transponderGroupDescription;
    }

    public String getCustomerFirstName() {
        return customerFirstName;
    }

    public void setCustomerFirstName(String customerFirstName) {
        this.customerFirstName = customerFirstName;
    }

    public String getCustomerLastName() {
        return customerLastName;
    }

    public void setCustomerLastName(String customerLastName) {
        this.customerLastName = customerLastName;
    }

    public Long getAuftragHousingKeyId() {
        return auftragHousingKeyId;
    }

    public void setAuftragHousingKeyId(Long auftragHousingKeyId) {
        this.auftragHousingKeyId = auftragHousingKeyId;
    }

}
