/**
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 08.01.2010 14:57:57
 */
package de.augustakom.hurrican.tools.comparator;

import java.io.*;
import java.util.*;

import de.augustakom.hurrican.model.cc.Equipment;
import de.augustakom.hurrican.model.shared.iface.HwEqnAwareModel;

/**
 * Teilt die HW EQN beim Separator ('-') und vergleicht die einzelnen Teile, indem es versucht sie als Zahlen zu
 * interpretieren und dann die Zahlen zu vergleichen. Schlaegt dies fehlt, werden die Strings verglichen.
 * <p/>
 * Sollten die EQNs eine unterschiedliche Anzahl Separatoren haben, so wird die EQN mit mehr Separatoren als groesser
 * angesehen.
 * <p/>
 * Vision: Nutzt einen Mechanismus, der den Typ des Ports in Betracht zieht.
 *
 *
 */
public class HwEqnComparator implements Comparator<HwEqnAwareModel>, Serializable {

    @edu.umd.cs.findbugs.annotations.SuppressWarnings(value = "NP_NULL_ON_SOME_PATH_MIGHT_BE_INFEASIBLE", justification = "Kann nicht vorkommen.")
    @SuppressWarnings("null")
    @Override
    public int compare(HwEqnAwareModel o1, HwEqnAwareModel o2) {
        String hwEQN1 = o1.getHwEQN();
        String hwEQN2 = o2.getHwEQN();

        if ((hwEQN1 == null) && (hwEQN2 == null)) {
            return 0;
        }
        else if (hwEQN1 == null) {
            return -1;
        }
        else if (hwEQN2 == null) {
            return 1;
        }

        String[] parts1 = hwEQN1.split(Equipment.HW_EQN_SEPARATOR);
        String[] parts2 = hwEQN2.split(Equipment.HW_EQN_SEPARATOR);
        if (parts1.length != parts2.length) {
            return parts1.length > parts2.length ? 1 : -1; // this is just random...
        }
        for (int i = 0; i < parts1.length; ++i) {
            int result = 0;
            try {
                Integer v1 = Integer.valueOf(parts1[i]);
                Integer v2 = Integer.valueOf(parts2[i]);
                result = v1.compareTo(v2);
            }
            catch (NumberFormatException e) {
                result = parts1[i].compareTo(parts2[i]);
            }
            if (result != 0) {
                return result;
            }
        }
        return 0;
    }
}
