/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 02.12.2011 09:24:34
 */
package de.mnet.antivirus.scan;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

import java.io.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.mnet.antivirus.scan.AntivirusScanService.AntivirusScanStatus;
import de.mnet.antivirus.scan.executor.AntivirusCheckExecutor;


@Test(groups = BaseTest.UNIT)
public class AntivirusScanServiceImplTest {

    @Mock
    private AntivirusCheckExecutor antiCheckExecutor;

    @InjectMocks
    private AntivirusScanServiceImpl antivirusScanServiceImpl;

    private String filePath = System.getProperty("user.home") + File.separatorChar + "antiviruscheck"
            + File.separatorChar;
    private String fileName = "testFile.txt";

    @BeforeMethod
    public void initTest() {
        antivirusScanServiceImpl = new AntivirusScanServiceImpl();
        MockitoAnnotations.initMocks(this);
        antivirusScanServiceImpl.setScanFilePath(filePath);
        antivirusScanServiceImpl.setFileName(fileName);
    }

    public void testScanInfectedFileStream() throws IOException {
        byte[] stream = "bad content".getBytes("ISO-8859-1");
        when(antiCheckExecutor.scanFile(any(File.class))).thenReturn(AntivirusScanStatus.FILE_INFECTED);
        assertEquals(antivirusScanServiceImpl.scanFileStream(stream), AntivirusScanStatus.FILE_INFECTED);
        assertFalse((new File(filePath + fileName).exists()));
    }

    public void testScanCleanFileStream() throws IOException {
        byte[] stream = "clean".getBytes("ISO-8859-1");
        when(antiCheckExecutor.scanFile(any(File.class))).thenReturn(AntivirusScanStatus.FILE_OK);
        assertEquals(antivirusScanServiceImpl.scanFileStream(stream), AntivirusScanStatus.FILE_OK);
        assertFalse((new File(filePath + fileName).exists()));
    }

}


