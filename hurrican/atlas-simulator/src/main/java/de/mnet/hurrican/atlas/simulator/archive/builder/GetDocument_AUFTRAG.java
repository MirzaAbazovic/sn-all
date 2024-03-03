/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 18.12.14
 */
package de.mnet.hurrican.atlas.simulator.archive.builder;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component("0000004")
@Scope("prototype")
public class GetDocument_AUFTRAG extends AbstractDocumentArchiveTestBuilder {
    @Override
    protected void configure() {
        configureGetDocument("kundenauftrag.pdf", "pdf", "Auftrag");
    }


}
