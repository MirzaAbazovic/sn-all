package de.augustakom.hurrican.gui.shared;

import static de.augustakom.hurrican.gui.shared.CCAnsprechpartnerViewTableModel.*;
import static org.testng.Assert.*;

import java.util.*;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.hurrican.model.cc.view.CCAnsprechpartnerView;

/**
 * Test class for {@link CCAnsprechpartnerViewTableModel}.
 */
@Test(groups = { BaseTest.UNIT })
public class CCAnsprechpartnerViewTableModelTest {

    private static final Boolean TAKE_OVER = Boolean.TRUE;
    private static final String AP_TYPE = "AP_TYPE";
    private static final String AP_NAME = "NAME";
    private static final String AP_VORNAME = "VORNAME";
    private static final Long AP_AUFTRAG_ID = 123L;
    private static final Long AP_ORDER_NO = 456L;
    private static final String AP_TELEFON = "TELEFON";
    private static final String AP_HANDY = "HANDY";
    private static final String AP_EMAIL = "EMAIL";


    private CCAnsprechpartnerViewTableModel ccAnsprechpartnerViewTableModel = new CCAnsprechpartnerViewTableModel();

    @BeforeTest
    public void setUp() {
        final CCAnsprechpartnerView ccAnsprechpartnerView = new CCAnsprechpartnerView();
        ccAnsprechpartnerView.setTakeOver(TAKE_OVER);
        ccAnsprechpartnerView.setAnsprechpartnerType(AP_TYPE);
        ccAnsprechpartnerView.setName(AP_NAME);
        ccAnsprechpartnerView.setVorname(AP_VORNAME);
        ccAnsprechpartnerView.setAuftragId(AP_AUFTRAG_ID);
        ccAnsprechpartnerView.setOrderNo(AP_ORDER_NO);
        ccAnsprechpartnerView.setTelefon(AP_TELEFON);
        ccAnsprechpartnerView.setHandy(AP_HANDY);
        ccAnsprechpartnerView.setEmail(AP_EMAIL);

        ccAnsprechpartnerViewTableModel.setData(Collections.singletonList(ccAnsprechpartnerView));
    }

    @Test
    public void testGetColumnClass_COL_AP_TAKE_OVER() {
        Class<?> current = ccAnsprechpartnerViewTableModel.getColumnClass(COL_AP_TAKE_OVER);
        assertNotNull(current);
        assertEquals(current, Boolean.class);
    }

    @Test
    public void testGetColumnClass_COL_AP_TYP() {
        Class<?> current = ccAnsprechpartnerViewTableModel.getColumnClass(COL_AP_TYP);
        assertNotNull(current);
        assertEquals(current, String.class);
    }

    @Test
    public void testGetColumnClass_COL_AP_NAME() {
        Class<?> current = ccAnsprechpartnerViewTableModel.getColumnClass(COL_AP_NAME);
        assertNotNull(current);
        assertEquals(current, String.class);
    }

    @Test
    public void testGetColumnClass_COL_AP_VORNAME() {
        Class<?> current = ccAnsprechpartnerViewTableModel.getColumnClass(COL_AP_VORNAME);
        assertNotNull(current);
        assertEquals(current, String.class);
    }

    @Test
    public void testGetColumnClass_COL_AP_AUFTRAG_ID() {
        Class<?> current = ccAnsprechpartnerViewTableModel.getColumnClass(COL_AP_AUFTRAG_ID);
        assertNotNull(current);
        assertEquals(current, Long.class);
    }

    @Test
    public void testGetColumnClass_COL_AP_BILLING_NO() {
        Class<?> current = ccAnsprechpartnerViewTableModel.getColumnClass(COL_AP_BILLING_NO);
        assertNotNull(current);
        assertEquals(current, Long.class);
    }

    @Test
    public void testGetColumnClass_COL_AP_TELEFON() {
        Class<?> current = ccAnsprechpartnerViewTableModel.getColumnClass(COL_AP_TELEFON);
        assertNotNull(current);
        assertEquals(current, String.class);
    }

    @Test
    public void testGetColumnClass_COL_AP_HANDY() {
        Class<?> current = ccAnsprechpartnerViewTableModel.getColumnClass(COL_AP_HANDY);
        assertNotNull(current);
        assertEquals(current, String.class);
    }

    @Test
    public void testGetColumnClass_COL_AP_EMAIL() {
        Class<?> current = ccAnsprechpartnerViewTableModel.getColumnClass(COL_AP_EMAIL);
        assertNotNull(current);
        assertEquals(current, String.class);
    }

    @Test
    public void testGetColumnClass_Default() {
        Class<?> current = ccAnsprechpartnerViewTableModel.getColumnClass(99999999);
        assertNull(current);
    }

    @Test
    public void getValueAt_COL_AP_TAKE_OVER() {
        Object current = ccAnsprechpartnerViewTableModel.getValueAt(0, COL_AP_TAKE_OVER);
        assertNotNull(current);
        assertEquals(current, TAKE_OVER);
    }

    @Test
    public void getValueAt_COL_AP_TYP() {
        Object current = ccAnsprechpartnerViewTableModel.getValueAt(0, COL_AP_TYP);
        assertNotNull(current);
        assertEquals(current, AP_TYPE);
    }

    @Test
    public void getValueAt_COL_AP_NAME() {
        Object current = ccAnsprechpartnerViewTableModel.getValueAt(0, COL_AP_NAME);
        assertNotNull(current);
        assertEquals(current, AP_NAME);
    }

    @Test
    public void getValueAt_COL_AP_VORNAME() {
        Object current = ccAnsprechpartnerViewTableModel.getValueAt(0, COL_AP_VORNAME);
        assertNotNull(current);
        assertEquals(current, AP_VORNAME);
    }

    @Test
    public void getValueAt_COL_AP_AUFTRAG_ID() {
        Object current = ccAnsprechpartnerViewTableModel.getValueAt(0, COL_AP_AUFTRAG_ID);
        assertNotNull(current);
        assertEquals(current, AP_AUFTRAG_ID);
    }

    @Test
    public void getValueAt_COL_AP_BILLING_NO() {
        Object current = ccAnsprechpartnerViewTableModel.getValueAt(0, COL_AP_BILLING_NO);
        assertNotNull(current);
        assertEquals(current, AP_ORDER_NO);
    }

    @Test
    public void getValueAt_COL_AP_TELEFON() {
        Object current = ccAnsprechpartnerViewTableModel.getValueAt(0, COL_AP_TELEFON);
        assertNotNull(current);
        assertEquals(current, AP_TELEFON);
    }

    @Test
    public void getValueAt_COL_AP_HANDY() {
        Object current = ccAnsprechpartnerViewTableModel.getValueAt(0, COL_AP_HANDY);
        assertNotNull(current);
        assertEquals(current, AP_HANDY);
    }

    @Test
    public void getValueAt_COL_AP_EMAIL() {
        Object current = ccAnsprechpartnerViewTableModel.getValueAt(0, COL_AP_EMAIL);
        assertNotNull(current);
        assertEquals(current, AP_EMAIL);
    }

    @Test
    public void getValueAt_Default() {
        Object current = ccAnsprechpartnerViewTableModel.getValueAt(0, 99999999);
        assertNull(current);
    }

    @Test
    public void testGetColumnName_COL_AP_TAKE_OVER() {
        assertEquals(ccAnsprechpartnerViewTableModel.getColumnName(COL_AP_TAKE_OVER), COL_NAME_AP_TAKE_OVER);
    }

    @Test
    public void testGetColumnName_COL_AP_TYP() {
        assertEquals(ccAnsprechpartnerViewTableModel.getColumnName(COL_AP_TYP), COL_NAME_AP_TYP);
    }

    @Test
    public void testGetColumnName_COL_AP_NAME() {
        assertEquals(ccAnsprechpartnerViewTableModel.getColumnName(COL_AP_NAME), COL_NAME_AP_NAME);
    }

    @Test
    public void testGetColumnName_COL_AP_VORNAME() {
        assertEquals(ccAnsprechpartnerViewTableModel.getColumnName(COL_AP_VORNAME), COL_NAME_AP_VORNAME);
    }

    @Test
    public void testGetColumnName_COL_AP_AUFTRAG_ID() {
        assertEquals(ccAnsprechpartnerViewTableModel.getColumnName(COL_AP_AUFTRAG_ID), COL_NAME_AP_AUFTRAG_ID);
    }

    @Test
    public void testGetColumnName_COL_AP_BILLING_NO() {
        assertEquals(ccAnsprechpartnerViewTableModel.getColumnName(COL_AP_BILLING_NO), COL_NAME_AP_BILLING_NO);
    }

    @Test
    public void testGetColumnName_COL_AP_TELEFON() {
        assertEquals(ccAnsprechpartnerViewTableModel.getColumnName(COL_AP_TELEFON), COL_NAME_AP_TELEFON);
    }

    @Test
    public void testGetColumnName_COL_AP_HANDY() {
        assertEquals(ccAnsprechpartnerViewTableModel.getColumnName(COL_AP_HANDY), COL_NAME_AP_HANDY);
    }

    @Test
    public void testGetColumnName_COL_AP_EMAIL() {
        assertEquals(ccAnsprechpartnerViewTableModel.getColumnName(COL_AP_EMAIL), COL_NAME_AP_EMAIL);
    }

    @Test
    public void testGetColumnName_Default() {
        assertNull(ccAnsprechpartnerViewTableModel.getColumnName(99999999));
    }

}