/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 07.02.14
 */
package de.mnet.wbci.model.helper;

import java.util.*;
import org.apache.commons.collections.CollectionUtils;

import de.mnet.wbci.model.MeldungPosition;
import de.mnet.wbci.model.MeldungsCode;

/**
 * Misc. helper methods for working with Meldungscodes.
 */
public final class MeldungsCodeHelper {

    private MeldungsCodeHelper() {
    }

    public static <MP extends MeldungPosition> void removeMeldungsCodes(Set<MP> positions, List<MeldungsCode> codes) {
        if (CollectionUtils.isNotEmpty(positions)) {
            Iterator<MP> itor = positions.iterator();
            while (itor.hasNext()) {
                MP mp = itor.next();
                for (MeldungsCode mc : codes) {
                    if (mc.equals(mp.getMeldungsCode())) {
                        itor.remove();
                        break;
                    }
                }
            }
        }
    }

    public static <MP extends MeldungPosition> void removeMeldungsCodes(Set<MP> positions, MeldungsCode... codes) {
        removeMeldungsCodes(positions, Arrays.asList(codes));
    }

    public static <MP extends MeldungPosition> boolean containsMeldungsCode(Set<MP> positions, MeldungsCode code) {
        if (CollectionUtils.isNotEmpty(positions)) {
            for (MP mp : positions) {
                if (code.equals(mp.getMeldungsCode())) {
                    return true;
                }
            }
        }

        return false;
    }
}
