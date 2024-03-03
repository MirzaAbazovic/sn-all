/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 *
 */
package de.augustakom.common.gui.swing.xml;

import java.io.*;
import javax.swing.text.*;

/**
 * A factory to create a view of some portion of document subject. This is intended to enable customization of how views
 * get mapped over a document model.
 */
public class XmlViewFactory extends Object implements ViewFactory, Serializable {

    @Override
    public View create(Element element) {
        return new XmlView(element);
    }
}
