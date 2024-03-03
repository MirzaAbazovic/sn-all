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
 * Specific class for the {@link de.augustakom.hurrican.gui.tools.wbci.tables.PreAgreementTableModel} to represent the
 * table data in the {@link de.augustakom.hurrican.gui.tools.wbci.PreAgreementDonatingEKPOverviewPanel}.
 *
 *
 */
public class PreAgreementDonatingEKPTableModel extends PreAgreementTableModel {

    private static final long serialVersionUID = -916069433884547539L;

    public PreAgreementDonatingEKPTableModel() {
        super(createDonatingEKPMetaData());
    }

    private static ReflectionTableMetaDataWithSize[] createDonatingEKPMetaData() {
        List<ReflectionTableMetaDataWithSize> metaData = createDonatingEKPMetaDataList();
        ReflectionTableMetaDataWithSize[] metaDataArray = new ReflectionTableMetaDataWithSize[metaData.size()];
        metaData.toArray(metaDataArray);
        return metaDataArray;
    }

    private static List<ReflectionTableMetaDataWithSize> createDonatingEKPMetaDataList() {
        List<ReflectionTableMetaDataWithSize> metaData = new ArrayList<>();
        metaData.addAll(createTabelModelMetaData(CarrierRole.ABGEBEND));
        return metaData;
    }

    @Override
    public List<ReflectionTableMetaDataWithSize> getDataModelMetaData() {
        return createDonatingEKPMetaDataList();
    }
}
