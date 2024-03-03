/*
 * Copyright (c) 2016 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 17.10.2016
 */
package de.augustakom.hurrican.gui.hvt.switchmigration.worker;

import java.util.*;
import java.util.stream.*;
import javax.swing.*;

import de.augustakom.common.gui.swing.table.AKTableModel;
import de.augustakom.common.tools.lang.Pair;
import de.augustakom.hurrican.gui.hvt.switchmigration.SwitchMigrationPanel;
import de.augustakom.hurrican.gui.hvt.switchmigration.util.SideEffect;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.billing.RufnummerService;

public class DialNumberCountWorker extends SwingWorker<Void, Void> {
    private RufnummerService rufnummerService;
    private AKTableModel<SwitchMigrationPanel.SwitchMigrationViewObject> tableModel;
    private SideEffect postExecute;

    public DialNumberCountWorker(RufnummerService rufnummerService,
            AKTableModel<SwitchMigrationPanel.SwitchMigrationViewObject> tableModel,
            SideEffect preExecute,
            SideEffect postExecute) {
        super();
        this.rufnummerService = rufnummerService;
        this.tableModel = tableModel;
        this.postExecute = postExecute;
        preExecute.execute();
    }


    @Override
    protected Void doInBackground() throws Exception {
        Collection<SwitchMigrationPanel.SwitchMigrationViewObject> data =
                tableModel.getData();
        List<Pair<Long, Integer>> result = findDialNumberCount(data);
        setDialNumberCount(data, result);
        return null;
    }

    private List<Pair<Long, Integer>> findDialNumberCount(Collection<SwitchMigrationPanel.SwitchMigrationViewObject> data) throws FindException {
        List<Long> ids = data.
                stream().map(SwitchMigrationPanel.SwitchMigrationViewObject::getBillingAuftragId).
                collect(Collectors.toList());
        return rufnummerService.findRNCount(ids);
    }

    private void setDialNumberCount(Collection<SwitchMigrationPanel.SwitchMigrationViewObject> data, List<Pair<Long, Integer>> result) {
        result.forEach(pair -> {
            Optional<SwitchMigrationPanel.SwitchMigrationViewObject> found = data.parallelStream().
                    filter(d -> d.getBillingAuftragId().longValue() == pair.getFirst().longValue()).findFirst();
            if (found.isPresent())
                found.get().setDialNumberCount(pair.getSecond());
        });
    }

    @Override
    protected void done() {
        tableModel.fireTableDataChanged();
        postExecute.execute();
    }
}