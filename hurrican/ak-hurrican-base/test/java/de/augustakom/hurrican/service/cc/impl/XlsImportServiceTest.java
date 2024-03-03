package de.augustakom.hurrican.service.cc.impl;

import java.io.*;
import java.net.*;
import java.time.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.common.tools.file.FileTools;
import de.augustakom.hurrican.model.cc.HvtUmzugBuilder;
import de.augustakom.hurrican.model.cc.hvt.umzug.HvtUmzug;
import de.augustakom.hurrican.model.cc.hvt.umzug.HvtUmzugStatus;
import de.augustakom.hurrican.model.cc.view.XlsImportResultView;
import de.augustakom.hurrican.service.AbstractHurricanBaseServiceTest;
import de.augustakom.hurrican.service.cc.XlsImportService;

@Test(groups = { BaseTest.SERVICE })
public class XlsImportServiceTest extends AbstractHurricanBaseServiceTest {

    @Autowired
    XlsImportService xlsImportService;

    public void importFttxKvz() throws Exception {
        URL resource = getClass().getResource("/Import_FTTX_KVZ_Test.xls");
        byte[] xlsData = FileTools.convertToByteArray(new File(resource.getFile()));

        XlsImportResultView[] importResult = xlsImportService.importFttxKvz(xlsData, getSessionId());

        Assert.assertEquals(importResult[0].getSuccessCount(), 7);
        Assert.assertEquals(importResult[0].getFailedCount(), 6);

        Assert.assertEquals(importResult[1].getSuccessCount(), 4);
        Assert.assertEquals(importResult[1].getFailedCount(), 3);
    }

    public void importHvtUmzugDetails() throws Exception {
        final HvtUmzug HvtUmzug = new HvtUmzugBuilder()
                .withBearbeiter("John Doe")
                .withImportAm(LocalDateTime.now())
                .withStatus(HvtUmzugStatus.OFFEN)
                .setPersist(false)
                .build();

        final URL resource = getClass().getResource("/ImportHvtUmzugDetails.xlsx");
        final byte[] xlsData = FileTools.convertToByteArray(new File(resource.getFile()));
        final XlsImportResultView[] importResult = xlsImportService.importHvtUmzugDetails(HvtUmzug, xlsData);

        Assert.assertEquals(importResult.length, 1);

        final int successCount = 49;
        final int warningCount = 1;
        final int failedCount = 0;
        Assert.assertEquals(importResult[0].getSuccessCount(), successCount);
        Assert.assertEquals(importResult[0].getWarningCount(), warningCount);
        Assert.assertEquals(importResult[0].getFailedCount(), failedCount);
        Assert.assertEquals(HvtUmzug.getHvtUmzugDetails().size(), successCount);
    }
}
