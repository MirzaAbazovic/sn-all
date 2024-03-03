/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 08.09.2011 17:48:05
 */
package de.augustakom.hurrican.gui.tools.tal.ioarchive.actions;

import de.augustakom.hurrican.gui.tools.tal.ioarchive.IoArchiveRowModel;

/**
 *
 */
public class ShowCompleteRequestXmlAction extends AbstractShowXMLAction {

    private static final long serialVersionUID = -2316861122370989848L;

    public ShowCompleteRequestXmlAction() {
        super(IoArchiveRowModel.REQUEST_XML_COLUMN, true, true);

    }

}
