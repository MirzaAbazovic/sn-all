/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 19.08.2014
 */
package de.augustakom.hurrican.model.billing.helper;

import java.util.*;
import javax.annotation.*;
import javax.validation.constraints.*;

import de.augustakom.hurrican.model.billing.Rufnummer;
import de.augustakom.hurrican.model.billing.RufnummerPortierungSelection;

/**
 *
 */
public class RufnummerPortierungSelectionHelper {

    /**
     * Exctracts {@link Rufnummer#dnNoOrig} from a collection of {@link RufnummerPortierungSelection}.
     */
    public static
    @NotNull
    Set<Long> getRufnummerIds(@Nullable Collection<RufnummerPortierungSelection> rufnummerList) {
        Set<Long> rufnummerIds = new HashSet<>();
        if (rufnummerList != null) {
            for (RufnummerPortierungSelection rufnummer : rufnummerList) {
                if (Boolean.TRUE.equals(rufnummer.getSelected())) {
                    rufnummerIds.add(rufnummer.getRufnummer().getDnNoOrig());
                }
            }
        }
        return rufnummerIds;
    }
}
