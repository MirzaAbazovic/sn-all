/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 22.07.13
 */
package de.augustakom.hurrican.gui.tools.wbci.tables;

import static de.augustakom.hurrican.gui.tools.wbci.tables.PreAgreementTableModel.ColumnMetaData.*;
import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

import java.time.*;
import java.util.*;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import de.augustakom.authentication.model.AKUser;
import de.augustakom.common.BaseTest;
import de.augustakom.common.service.holiday.DateCalculationHelper;
import de.mnet.wbci.model.PreAgreementType;
import de.mnet.wbci.model.PreAgreementVO;
import de.mnet.wbci.model.Technologie;
import de.mnet.wbci.model.WbciGeschaeftsfallStatus;
import de.mnet.wbci.model.WbciRequestStatus;

/**
 *
 */
@Test(groups = BaseTest.UNIT)
public class PreAgreementTableModelTest {

    @Mock
    private PreAgreementVO preAgreementVOMock;
    private String vaid = "VA_TEST_0001";
    private Long auftrnrTech = 100L;
    private Long auftrnrTaifun = 1L;
    private PreAgreementType preAgreementType = PreAgreementType.GK;
    private String gf = "Kuendigung mit Rufnummernportierung";
    private String akz = "Standard";
    private String ekpAbg = "EndkundenVertragspartnerAbg";
    private String ekpAuf = "EndkundenVertragspartnerAuf";
    private Date vorgabeDatum = new Date();
    private WbciRequestStatus reqStatus = WbciRequestStatus.ABBM_EMPFANGEN;
    private String gfStatus = WbciGeschaeftsfallStatus.ACTIVE.getDescription();
    private Date processedAt = new Date();
    private String rueckmeldung = "Rückmeldung vorhanden";
    private Date rueckmeldungDate = new Date();
    private String bearbeiter = "WBCI-Tester";
    private String team = "WBCI-Team";
    private String aktuellerBearbeiter = "WBCI-Tester-2";
    private String niederlassung = "Münchnen";
    private Boolean leistungsUE = true;
    private Technologie mNetTech = Technologie.TAL_ISDN;
    private Date wechseltermin = Date.from(DateCalculationHelper.getDateInWorkingDaysFromNow(14).atZone(ZoneId.systemDefault()).toInstant());
    private Boolean klaerfall = true;
    private Boolean automatable = true;
    private int daysUntillDeadlineMnet = 3;
    private String internalStatus = "Interner Status";

    @BeforeMethod
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        when(preAgreementVOMock.getVaid()).thenReturn(vaid);
        when(preAgreementVOMock.getAuftragId()).thenReturn(auftrnrTech);
        when(preAgreementVOMock.getAuftragNoOrig()).thenReturn(auftrnrTaifun);
        when(preAgreementVOMock.getPreAgreementType()).thenReturn(preAgreementType);
        when(preAgreementVOMock.getGfTypeShortName()).thenReturn(gf);
        when(preAgreementVOMock.getAenderungskz()).thenReturn(akz);
        when(preAgreementVOMock.getEkpAbgITU()).thenReturn(ekpAbg);
        when(preAgreementVOMock.getEkpAufITU()).thenReturn(ekpAuf);
        when(preAgreementVOMock.getVorgabeDatum()).thenReturn(vorgabeDatum);
        when(preAgreementVOMock.getWechseltermin()).thenReturn(wechseltermin);
        when(preAgreementVOMock.getRequestStatus()).thenReturn(reqStatus);
        when(preAgreementVOMock.getGeschaeftsfallStatusDescription()).thenReturn(gfStatus);
        when(preAgreementVOMock.getProcessedAt()).thenReturn(processedAt);
        when(preAgreementVOMock.getRueckmeldung()).thenReturn(rueckmeldung);
        when(preAgreementVOMock.getRueckmeldeDatum()).thenReturn(rueckmeldungDate);
        when(preAgreementVOMock.getUserName()).thenReturn(bearbeiter);
        when(preAgreementVOMock.getTeamName()).thenReturn(team);
        when(preAgreementVOMock.getCurrentUserName()).thenReturn(aktuellerBearbeiter);
        when(preAgreementVOMock.getNiederlassung()).thenReturn(niederlassung);
        when(preAgreementVOMock.getMnetTechnologie()).thenReturn(mNetTech.getWbciTechnologieCode());
        when(preAgreementVOMock.getKlaerfall()).thenReturn(klaerfall);
        when(preAgreementVOMock.getAutomatable()).thenReturn(automatable);
        when(preAgreementVOMock.getDaysUntilDeadlineMnet()).thenReturn(daysUntillDeadlineMnet);
        when(preAgreementVOMock.getDaysUntilDeadlinePartner()).thenReturn(null);
        when(preAgreementVOMock.getInternalStatus()).thenReturn(internalStatus);
    }

    @Test
    public void testMapping() throws Exception {
        for (PreAgreementTableModel testling : Arrays.asList(
                new PreAgreementReceivingEKPTableModel() {

                    private static final long serialVersionUID = -5330735755941756414L;

                    @Override
                    protected AKUser getCurrentUser() {
                        return Mockito.mock(AKUser.class);
                    }
                },
                new PreAgreementDonatingEKPTableModel() {
                    private static final long serialVersionUID = -7691568798995900626L;

                    @Override
                    protected AKUser getCurrentUser() {
                        return Mockito.mock(AKUser.class);
                    }
                }
        )) {
            testling.addObject(preAgreementVOMock);
            int col = 0;
            assertEquals(testling.getRowCount(), 1);
            assertEquals(testling.getColumnName(col), COL_VAID.colName);
            assertEquals(testling.getValueAt(0, col), vaid);
            assertEquals(testling.getColumnName(++col), COL_AUFTRAGSNUMMER_TECH.colName);
            assertEquals(testling.getValueAt(0, col), auftrnrTech);
            assertEquals(testling.getColumnName(++col), COL_AUFTRAGSNUMMER_TAIFUN.colName);
            assertEquals(testling.getValueAt(0, col), auftrnrTaifun);
            assertEquals(testling.getColumnName(++col), COL_PRE_AGREEMENT_TYPE.colName);
            assertEquals(testling.getValueAt(0, col), preAgreementType);
            assertEquals(testling.getColumnName(++col), COL_GF.colName);
            assertEquals(testling.getValueAt(0, col), gf);
            assertEquals(testling.getColumnName(++col), COL_AENDERUNGSKZ.colName);
            assertEquals(testling.getValueAt(0, col), akz);

            if (testling instanceof PreAgreementReceivingEKPTableModel) {
                assertEquals(testling.getColumnName(++col), COL_EKP_ABG.colName);
                assertEquals(testling.getValueAt(0, col), ekpAbg);
            }
            else {
                assertEquals(testling.getColumnName(++col), COL_EKP_AUF.colName);
                assertEquals(testling.getValueAt(0, col), ekpAuf);
            }

            assertEquals(testling.getColumnName(++col), COL_VORGABEDATUM.colName);
            assertEquals(testling.getValueAt(0, col), vorgabeDatum);
            assertEquals(testling.getColumnName(++col), COL_WECHSELTERMIN.colName);
            assertEquals(testling.getValueAt(0, col), wechseltermin);
            assertEquals(testling.getColumnName(++col), COL_STATUS.colName);
            assertEquals(testling.getValueAt(0, col), reqStatus);

            assertEquals(testling.getColumnName(++col), COL_UEBERMITTELT.colName);
            assertEquals(testling.getValueAt(0, col), processedAt);

            assertEquals(testling.getColumnName(++col), COL_RUECKMELDUNG.colName);
            assertEquals(testling.getValueAt(0, col), rueckmeldung);
            assertEquals(testling.getColumnName(++col), COL_RUECKMELDE_DATUM.colName);
            assertEquals(testling.getValueAt(0, col), rueckmeldungDate);
            assertEquals(testling.getColumnName(++col), COL_USER.colName);
            assertEquals(testling.getValueAt(0, col), bearbeiter);
            assertEquals(testling.getColumnName(++col), COL_TEAM.colName);
            assertEquals(testling.getValueAt(0, col), team);
            assertEquals(testling.getColumnName(++col), COL_CURRENT_USER.colName);
            assertEquals(testling.getValueAt(0, col), aktuellerBearbeiter);
            assertEquals(testling.getColumnName(++col), COL_NIEDERLASSUNG.colName);
            assertEquals(testling.getValueAt(0, col), niederlassung);
            assertEquals(testling.getColumnName(++col), COL_OPT_MNET_TECHNOLOGIE.colName);
            assertEquals(testling.getValueAt(0, col), mNetTech.getWbciTechnologieCode());
            assertEquals(testling.getColumnName(++col), COL_INTERNAL_STATUS.colName);
            assertEquals(testling.getValueAt(0, col), internalStatus);
            assertEquals(testling.getColumnName(++col), COL_KLAERFALL.colName);
            assertEquals(testling.getValueAt(0, col), klaerfall);
            assertEquals(testling.getColumnName(++col), COL_AUTOMATABLE.colName);
            assertEquals(testling.getValueAt(0, col), automatable);
            assertEquals(testling.getColumnName(++col), COL_GF_STATUS.colName);
            assertEquals(testling.getValueAt(0, col), gfStatus);
            assertEquals(testling.getColumnName(++col), COL_DAYS_UNTILL_DEADLINE_MNET.colName);
            assertEquals(testling.getValueAt(0, col), daysUntillDeadlineMnet);
            assertEquals(testling.getColumnName(++col), COL_DAYS_UNTILL_DEADLINE_PARTNER.colName);
            assertEquals(testling.getValueAt(0, col), null);
        }
    }

}
