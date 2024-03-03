/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 16.12.14
 */
package de.mnet.hurrican.atlas.simulator.archive.builder;

import org.springframework.stereotype.Component;

/**
 *
 */
@Component("archiveDocument")
public class ArchiveDocument extends AbstractDocumentArchiveTestBuilder {

    @Override
    protected void configure() {
        receiveArchiveDocument();
        echo("Received archive document request!");
    }
}
