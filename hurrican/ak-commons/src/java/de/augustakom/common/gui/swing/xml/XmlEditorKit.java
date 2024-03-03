/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 *
 */
package de.augustakom.common.gui.swing.xml;

import javax.swing.text.*;

/**
 * This is the set of things needed by a text component to be a reasonably functioning editor for a xml document. This
 * implementation provides a default implementation which treats xml as styled text and provides a minimal set of
 * actions for editing xml text.
 */
class XmlEditorKit extends StyledEditorKit {

    private ViewFactory xmlViewFactory;

    public XmlEditorKit() {
        xmlViewFactory = new XmlViewFactory();
    }

    @Override
    public ViewFactory getViewFactory() {
        return xmlViewFactory;
    }

    @Override
    public String getContentType() {
        return "text/xml";
    }
}
