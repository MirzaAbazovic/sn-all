/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 06.06.14
 */
package de.mnet.hurrican.scheduler.job.exports;

import java.io.*;

import org.mockito.Matchers;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;

@Test(groups = BaseTest.UNIT)
public class ExportBillsAndEVNs2ArchiveJobTest {

    @Spy
    private ExportBillsAndEVNs2ArchiveJob underTest;

    @BeforeMethod
    public void beforeMethod() throws Exception {
        underTest = new ExportBillsAndEVNs2ArchiveJob();
        MockitoAnnotations.initMocks(this);
    }

    @DataProvider(name = "copyFileAndRenameCtlAfterwardsAndDoLogEntryRetriesAfterErrorDataProvider")
    public Object[][] copyFileAndRenameCtlAfterwardsAndDoLogEntryRetriesAfterErrorDataProvider() {
        return new Object[][] {
                { new IOException("IOException-Test")},
                { new RuntimeException("any other exception")}
        };
    }

    @Test(dataProvider = "copyFileAndRenameCtlAfterwardsAndDoLogEntryRetriesAfterErrorDataProvider",
        expectedExceptions = Exception.class)
    public void copyFileAndRenameCtlAfterwardsAndDoLogEntryRetriesAfterError(Throwable throwable) throws Exception {
        Mockito.doThrow(throwable).when(underTest).copyFile2Dir(Matchers.any(File.class), Matchers.any(File.class));
        Mockito.doReturn(100).when(underTest).getSleepTime(Matchers.anyBoolean());

        underTest.copyFileAndRenameCtlAfterwardsAndDoLogEntry(new File(""), new File(""), new File(""));

        Mockito.verify(underTest, Mockito.times(3)).copyFile2Dir(Matchers.any(File.class), Matchers.any(File.class));
        Mockito.verify(underTest, Mockito.never()).renameCtlFile(Matchers.any(File.class));
        Mockito.verify(underTest, Mockito.never()).logArchive(Matchers.any(File.class));
    }

    public void copyFileAndRenameCtlAfterwardsAndDoLogEntrySuccess() throws Exception {
        Mockito.doNothing().when(underTest).copyFile2Dir(Matchers.any(File.class), Matchers.any(File.class));
        Mockito.doNothing().when(underTest).renameCtlFile(Matchers.any(File.class));
        Mockito.doNothing().when(underTest).logArchive(Matchers.any(File.class));

        underTest.copyFileAndRenameCtlAfterwardsAndDoLogEntry(new File(""), new File(""), new File(""));

        Mockito.verify(underTest, Mockito.times(1)).copyFile2Dir(Matchers.any(File.class), Matchers.any(File.class));
        Mockito.verify(underTest, Mockito.times(1)).renameCtlFile(Matchers.any(File.class));
        Mockito.verify(underTest, Mockito.times(1)).logArchive(Matchers.any(File.class));
        Mockito.verify(underTest, Mockito.never()).getSleepTime(Matchers.anyBoolean());
    }

}
