/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 28.07.2004 10:23:06
 */
package de.augustakom.hurrican.service.cc.impl;

import java.util.*;
import org.apache.log4j.Logger;

import de.augustakom.hurrican.annotation.CcTxRequired;
import de.augustakom.hurrican.dao.cc.GuiDAO;
import de.augustakom.hurrican.model.cc.gui.GUIDefinition;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.LoadException;
import de.augustakom.hurrican.service.cc.GUIService;


/**
 * Service-Implementierung von GUIService.
 *
 *
 */
@CcTxRequired
public class GUIServiceImpl extends DefaultCCService implements GUIService {

    private static final Logger LOGGER = Logger.getLogger(GUIServiceImpl.class);

    @Override
    public GUIDefinition findGUIDefinitionByClass(String className) throws FindException {
        try {
            GuiDAO dao = (GuiDAO) getDAO();
            return dao.findGUIDefByClass(className);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public List<GUIDefinition> getGUIDefinitions4Reference(Long referenceId, String refHerkunft, String guiType) throws LoadException {
        try {
            GuiDAO dao = (GuiDAO) getDAO();
            return dao.findGUIDefinitionen(referenceId, refHerkunft, guiType);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new LoadException(LoadException._UNEXPECTED_ERROR, e);
        }
    }

}


