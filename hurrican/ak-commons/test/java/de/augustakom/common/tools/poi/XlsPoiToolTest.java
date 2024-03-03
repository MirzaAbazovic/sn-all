package de.augustakom.common.tools.poi;

import java.io.*;
import java.net.*;
import java.sql.*;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.testng.Assert;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;

@Test(groups = BaseTest.UNIT)
public class XlsPoiToolTest extends BaseTest {

    @Test
    public void testAllXlsx() throws Exception {
        testAll(".xlsx");
    }

    @Test
    public void testAllXls() throws Exception {
        testAll(".xls");
    }

    @SuppressWarnings("deprecation")
    private void testAll(String suffix) throws Exception {
        URL resource = getClass().getResource("/" + getClass().getSimpleName() + suffix);
        Workbook workbook = WorkbookFactory.create(new File(resource.getFile()));
        Sheet sheet = workbook.getSheetAt(0);

        // String erste Zeile
        int row = 1;
        Assert.assertEquals(XlsPoiTool.getContentAsString(sheet.getRow(row), 2), "abc");
        Assert.assertEquals(XlsPoiTool.getContentAsString(sheet.getRow(row), 3), "22");
        Assert.assertEquals(XlsPoiTool.getContentAsString(sheet.getRow(row), 4), null);
        Assert.assertEquals(XlsPoiTool.getContentAsString(sheet.getRow(row), 5), "true");
        // String zweite Zeile
        row = 2;
        Assert.assertEquals(XlsPoiTool.getContentAsString(sheet.getRow(row), 2), null);
        Assert.assertEquals(XlsPoiTool.getContentAsString(sheet.getRow(row), 3), null);
        Assert.assertEquals(XlsPoiTool.getContentAsString(sheet.getRow(row), 4), null);
        Assert.assertEquals(XlsPoiTool.getContentAsString(sheet.getRow(row), 5), null);

        // Numeric erste Zeile
        row = 3;
        Assert.assertEquals(XlsPoiTool.getContentAsInt(sheet.getRow(row), 2), Integer.valueOf(123));
        Assert.assertEquals(XlsPoiTool.getContentAsInt(sheet.getRow(row), 3), Integer.valueOf(1234));
        Assert.assertNull(XlsPoiTool.getContentAsInt(sheet.getRow(row), 4));
        // Numeric zweite Zeile
        row = 4;
        Assert.assertNull(XlsPoiTool.getContentAsInt(sheet.getRow(row), 2));
        Assert.assertNull(XlsPoiTool.getContentAsInt(sheet.getRow(row), 3));
        Assert.assertNull(XlsPoiTool.getContentAsInt(sheet.getRow(row), 4));

        // Date erste Zeile
        row = 5;
        Assert.assertNull(XlsPoiTool.getContentAsDate(sheet.getRow(row), 4));
        Assert.assertEquals(XlsPoiTool.getContentAsDate(sheet.getRow(row), 6), new Date(112, 03, 21));
        // Date zweite Zeile
        row = 6;
        Assert.assertNull(XlsPoiTool.getContentAsDate(sheet.getRow(row), 4));
        Assert.assertNull(XlsPoiTool.getContentAsDate(sheet.getRow(row), 6));

        // empty
        Assert.assertTrue(XlsPoiTool.isEmpty(sheet.getRow(1), 4));
        Assert.assertTrue(XlsPoiTool.isEmpty(sheet.getRow(3), 4));
        Assert.assertTrue(XlsPoiTool.isEmpty(sheet.getRow(5), 4));
    }
}
