/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 15.07.2011 14:15:28
 */
package de.mnet.wita.model;

import de.augustakom.common.model.EntityBuilder;
import de.augustakom.common.model.SessionFactoryAware;
import de.augustakom.hurrican.model.cc.WitaCBVorgangAnlage;
import de.augustakom.hurrican.model.exmodules.archive.ArchiveDocumentType;

@SuppressWarnings("unused")
@SessionFactoryAware("cc.sessionFactory")
public class WitaCBVorgangAnlageBuilder extends EntityBuilder<WitaCBVorgangAnlageBuilder, WitaCBVorgangAnlage> {

    private String archiveKey = "KEY-123";
    private String mimeType = "image/tiff";
    private String anlagentyp = "Kuendigungsschreiben";
    private ArchiveDocumentType archiveDocumentType = ArchiveDocumentType.CUDA_KUENDIGUNG;
    private String archiveVertragsNr = "12345";

    public WitaCBVorgangAnlageBuilder withArchiveDocumentType(ArchiveDocumentType archiveDocumentType) {
        this.archiveDocumentType = archiveDocumentType;
        return this;
    }

}
