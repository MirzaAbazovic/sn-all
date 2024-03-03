/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 13.11.2013
 */
package de.augustakom.hurrican.service.cc;

import java.util.*;
import javax.annotation.*;

import de.augustakom.hurrican.model.cc.TechLeistung;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.mnet.annotation.ObjectsAreNonnullByDefault;

public interface QoSProfilService extends ICCService {

    @CheckForNull
    QosProfileWithValidFromAndDownstream getQoSProfilDownStreamAndValidDate4Auftrag(long auftragId, Date validDate)
            throws FindException;

    @ObjectsAreNonnullByDefault
    public static class QosProfileWithValidFromAndDownstream {
        public final TechLeistung qosProfile;
        public final Date validFrom;
        public final Long downstream;

        public QosProfileWithValidFromAndDownstream(final TechLeistung qosProfile, final Date validFrom,
                final Long downstream) {
            this.qosProfile = qosProfile;
            this.validFrom = validFrom;
            this.downstream = downstream;
        }
    }
}
