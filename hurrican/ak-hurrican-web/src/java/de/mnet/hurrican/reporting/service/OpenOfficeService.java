/*
 * Copyright (c) 2010 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 31.03.2010
 */
package de.mnet.hurrican.reporting.service;

import com.artofsolving.jodconverter.DocumentConverter;
import com.artofsolving.jodconverter.openoffice.connection.OpenOfficeConnection;

public interface OpenOfficeService {

    OpenOfficeConnection getConnection();

    DocumentConverter getDocumentConverter();

}
