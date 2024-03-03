/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 24.09.13
 */
package de.augustakom.hurrican.gui.tools.wbci.tables;

import java.util.*;

import de.augustakom.common.gui.swing.table.ReflectionTableMetaDataWithSize;
import de.mnet.wbci.model.CarrierRole;

/**
 * Specific class for the {@link PreAgreementTableModel} to represent the table data in the {@link
 * de.augustakom.hurrican.gui.tools.wbci.PreAgreementReceivingEKPOverviewPanel}.
 *
 *
 */
public class PreAgreementReceivingEKPTableModel extends PreAgreementTableModel {

    private static final long serialVersionUID = 3694234199159120463L;

    public PreAgreementReceivingEKPTableModel() {
        super(createReceivingEKPMetaData());
    }

    private static ReflectionTableMetaDataWithSize[] createReceivingEKPMetaData() {
        List<ReflectionTableMetaDataWithSize> metaData = createReceivingEKPMetaDataList();
        ReflectionTableMetaDataWithSize[] metaDataArray = new ReflectionTableMetaDataWithSize[metaData.size()];
        metaData.toArray(metaDataArray);
        return metaDataArray;
    }

    private static List<ReflectionTableMetaDataWithSize> createReceivingEKPMetaDataList() {
        List<ReflectionTableMetaDataWithSize> metaData = new ArrayList<>();
        metaData.addAll(createTabelModelMetaData(CarrierRole.AUFNEHMEND));
        return metaData;
    }

    @Override
    public List<ReflectionTableMetaDataWithSize> getDataModelMetaData() {
        return createReceivingEKPMetaDataList();
    }
}
