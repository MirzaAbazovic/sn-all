/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 27.12.2005 08:06:50
 */
package de.augustakom.common.tools.file;

import static org.testng.Assert.*;
import static org.testng.FileAssert.*;

import java.io.*;
import org.apache.log4j.Logger;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;

/**
 * TestNG Test-Case fuer <code>FileTools</code>
 */
@Test(groups = { "unit" })
public class FileToolsTest extends BaseTest {

    private static final Logger LOGGER = Logger.getLogger(FileToolsTest.class);

    private File tmpFile = null;

    private static final String FILE_PREFIX_TOOLSTEST = "FileToolsTest";

    @BeforeMethod
    public void setUp() throws Exception {
        tmpFile = File.createTempFile(FILE_PREFIX_TOOLSTEST, null);
        FileWriter fw = new FileWriter(tmpFile);

        // TODO get more/other/better content
        fw.write("test\ntest2\ntest3\ntest\t4");
        fw.close();
    }

    @AfterMethod
    public void tearDown() throws Exception {
        deleteTempFile(tmpFile);
    }

    /**
     * Hilfsfunktion, um Datei zu löschen; loggt Warnung, falls Löschen nicht erfolgreich
     */
    private void deleteTempFile(File tmp) {
        if (!tmp.delete()) {
            LOGGER.warn("created Tempfile '" + tmp.getAbsolutePath() + "' could not be deleted");
        }
    }

    /**
     * Test method for 'de.augustakom.common.tools.file.FileTools.getFileExtension(File)'
     */
    public void testGetFileExtension() {
        assertEquals(FileTools.getExtension(tmpFile), "tmp", "file extention incorrect");
    }

    /**
     * Test method for 'de.augustakom.common.tools.file.FileTools.copyFile(File, File)'
     */
    public void testCopyFile() throws Exception {

        File toCopy = tmpFile;
        File dest = File.createTempFile(FILE_PREFIX_TOOLSTEST, null);

        try {
            FileTools.copyFile(toCopy, dest, false);

            assertLength(toCopy, dest.length(), "file length not equal");

            // check content of files
            FileReader toCopyReader = new FileReader(toCopy);
            FileReader destReader = new FileReader(dest);

            int toCopyLength = (int) toCopy.length();
            int destLength = (int) dest.length();

            char[] toCopyChars = new char[toCopyLength];
            char[] destChars = new char[destLength];

            toCopyReader.read(toCopyChars, 0, toCopyLength);
            destReader.read(destChars, 0, destLength);

            toCopyReader.close();
            destReader.close();

            String toCopyString = new String(toCopyChars);
            String destString = new String(destChars);

            assertEquals(destString, toCopyString, "file content not equal");
        }
        finally {
            deleteTempFile(dest);
        }
    }
}
