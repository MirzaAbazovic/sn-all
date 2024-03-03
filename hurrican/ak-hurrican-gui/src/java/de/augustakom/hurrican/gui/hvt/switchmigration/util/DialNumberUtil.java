/*
 * Copyright (c) 2016 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 18.10.2016
 */
package de.augustakom.hurrican.gui.hvt.switchmigration.util;

import java.util.*;
import java.util.stream.*;

import de.augustakom.common.tools.lang.Pair;
import de.augustakom.hurrican.gui.hvt.switchmigration.SwitchMigrationPanel;

public final class DialNumberUtil {

    private DialNumberUtil() {
    }

    public static int calculateDialNumberSum(Collection<SwitchMigrationPanel.SwitchMigrationViewObject> data) {
        Stream<SwitchMigrationPanel.SwitchMigrationViewObject> selected = getSelectedItems(data);
        Set<Pair<Long, Integer>> pairs = getDistinctBillingIdDialNumberCountPairs(selected);
        return pairs.stream().mapToInt(pair -> {
            if (pair.getSecond() != null)
                return pair.getSecond();
            else
                return 0;
        }).sum();
    }

    private static Set<Pair<Long, Integer>> getDistinctBillingIdDialNumberCountPairs(
            Stream<SwitchMigrationPanel.SwitchMigrationViewObject> selected) {
        return selected.map(row ->
                Pair.create(row.getBillingAuftragId(), row.getDialNumberCount())).collect(Collectors.toSet());
    }

    private static Stream<SwitchMigrationPanel.SwitchMigrationViewObject> getSelectedItems(
            Collection<SwitchMigrationPanel.SwitchMigrationViewObject> data) {
        return data.stream().filter(SwitchMigrationPanel.SwitchMigrationViewObject::getSelected);
    }
}