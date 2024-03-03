/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 06.03.2009 11:24:21
 */
package de.augustakom.hurrican.gui.auftrag.wizards.shared;

import java.awt.event.*;
import org.apache.log4j.Logger;

import de.augustakom.hurrican.gui.base.AbstractServiceAction;
import de.augustakom.hurrican.model.cc.Auftrag;
import de.augustakom.hurrican.model.cc.AuftragIntern;
import de.augustakom.hurrican.service.cc.AuftragInternService;


/**
 * Action, um ein AuftragIntern-Objekt zu einem Auftrag anzulegen.
 *
 *
 */
public class CreateAuftragInternAction extends AbstractServiceAction {

    private static final Logger LOGGER = Logger.getLogger(CreateAuftragInternAction.class);

    private Auftrag auftrag = null;
    private AuftragIntern auftragIntern = null;

    /**
     * Konstruktor.
     *
     * @param auftrag Auftrag, zu dem die AuftragIntern-Referenz angelegt werden soll
     * @param ai      AuftragIntern Detail
     */
    public CreateAuftragInternAction(Auftrag auftrag, AuftragIntern ai) {
        super();
        this.auftrag = auftrag;
        this.auftragIntern = ai;
    }

    /**
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
        try {
            AuftragInternService ais = getCCService(AuftragInternService.class);
            auftragIntern.setAuftragId(auftrag.getId());
            ais.saveAuftragIntern(auftragIntern, false);
        }
        catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
        }
    }

}


