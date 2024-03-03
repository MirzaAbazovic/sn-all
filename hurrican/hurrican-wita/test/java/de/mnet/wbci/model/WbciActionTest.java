/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 18.11.13
 */
package de.mnet.wbci.model;

import static de.mnet.wbci.model.CarrierRole.*;
import static de.mnet.wbci.model.WbciAction.*;
import static de.mnet.wbci.model.WbciGeschaeftsfallStatus.*;
import static de.mnet.wbci.model.WbciRequestStatus.*;
import static org.testng.Assert.*;

import java.util.*;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;

@SuppressWarnings("unchecked")
@Test(groups = BaseTest.UNIT)
public class WbciActionTest {

    @DataProvider
    public Object[][] validActions() {
        StornoAenderungAufAnfrage stornoAen = new StornoAenderungAufAnfrage();
        StornoAufhebungAufAnfrage stornoAuf = new StornoAufhebungAufAnfrage();
        return new Object[][] {
                // ACTION , ROLE, CURRENT REQUEST STATUSES, CURRENT GESCHAEFTSFALL STATUS, GESCHAEFTSFALL TYPE
                { CREATE_RUEM_VA, ABGEBEND, new WbciRequestStatus[] { VA_EMPFANGEN }, ACTIVE, null, null },
                { CREATE_ABBM, ABGEBEND, new WbciRequestStatus[] { VA_EMPFANGEN }, ACTIVE, null, null },
                { CREATE_AKM_TR, AUFNEHMEND, new WbciRequestStatus[] { RUEM_VA_EMPFANGEN, TV_ERLM_EMPFANGEN, STORNO_ABBM_VERSENDET }, ACTIVE, null, null },
                { CREATE_ABBM_TR, ABGEBEND, new WbciRequestStatus[] { AKM_TR_EMPFANGEN, TV_ABBM_VERSENDET }, ACTIVE, null, null },
                { CREATE_TV, AUFNEHMEND, new WbciRequestStatus[] { RUEM_VA_EMPFANGEN }, ACTIVE, null, null },
                { CREATE_TV, AUFNEHMEND, new WbciRequestStatus[] { AKM_TR_VERSENDET }, ACTIVE, null, null },
                { CREATE_TV, AUFNEHMEND, new WbciRequestStatus[] { AKM_TR_VERSENDET, TV_ERLM_EMPFANGEN, STORNO_ABBM_VERSENDET }, ACTIVE, null, null },
                { CREATE_TV_ABBM, ABGEBEND, new WbciRequestStatus[] { AKM_TR_VERSENDET, TV_EMPFANGEN }, ACTIVE, null, null },
                { CREATE_TV_ERLM, ABGEBEND, new WbciRequestStatus[] { AKM_TR_VERSENDET, TV_EMPFANGEN }, ACTIVE, null, null },
                { CREATE_STORNO, AUFNEHMEND, new WbciRequestStatus[] { RUEM_VA_EMPFANGEN }, ACTIVE, null, null },
                { CREATE_STORNO, ABGEBEND, new WbciRequestStatus[] { RUEM_VA_VERSENDET }, ACTIVE, null, null },
                { CREATE_STORNO, AUFNEHMEND, new WbciRequestStatus[] { VA_VORGEHALTEN, VA_VERSENDET, AKM_TR_VERSENDET, TV_ERLM_EMPFANGEN, STORNO_ABBM_VERSENDET }, ACTIVE, null, null },
                { CREATE_STORNO_AENDERUNG, ABGEBEND, new WbciRequestStatus[] { RUEM_VA_VERSENDET, TV_ERLM_VERSENDET }, ACTIVE, null, null },
                { CREATE_STORNO_AENDERUNG, AUFNEHMEND, new WbciRequestStatus[] { AKM_TR_VERSENDET, TV_ERLM_EMPFANGEN, STORNO_ABBM_VERSENDET }, ACTIVE, null, null },
                { CREATE_STORNO_ABBM, AUFNEHMEND, new WbciRequestStatus[] { AKM_TR_VERSENDET, STORNO_EMPFANGEN }, ACTIVE, null, null },
                { CREATE_STORNO_ERLM, ABGEBEND, new WbciRequestStatus[] { AKM_TR_VERSENDET, STORNO_EMPFANGEN }, ACTIVE, Arrays.asList(MeldungTyp.RUEM_VA, MeldungTyp.AKM_TR), stornoAen },
                { CREATE_STORNO_ERLM, ABGEBEND, new WbciRequestStatus[] { AKM_TR_VERSENDET, STORNO_EMPFANGEN }, ACTIVE, Arrays.asList(MeldungTyp.AKM_TR), stornoAuf },
                { CLOSE_GF, AUFNEHMEND, new WbciRequestStatus[] { ABBM_VERSENDET }, ACTIVE, null, null },
                { CLOSE_GF, ABGEBEND, new WbciRequestStatus[] { ABBM_EMPFANGEN }, ACTIVE, null, null },
                { CLOSE_GF, AUFNEHMEND, new WbciRequestStatus[] { RUEM_VA_EMPFANGEN, STORNO_ERLM_EMPFANGEN }, ACTIVE, null, null },
                { CLOSE_GF, AUFNEHMEND, new WbciRequestStatus[] { RUEM_VA_EMPFANGEN, STORNO_ERLM_VERSENDET }, ACTIVE, null, null },
                { CLOSE_GF, AUFNEHMEND, new WbciRequestStatus[] { AKM_TR_EMPFANGEN, STORNO_ERLM_EMPFANGEN}, NEW_VA_EXPIRED, null, null },
                { CLOSE_GF, ABGEBEND, new WbciRequestStatus[] { AKM_TR_EMPFANGEN, STORNO_ERLM_EMPFANGEN }, NEW_VA_EXPIRED, null, null },
                { EDIT_AUTOMATED_FLAG, AUFNEHMEND, new WbciRequestStatus[] { VA_EMPFANGEN }, ACTIVE, null, null },
                { EDIT_AUTOMATED_FLAG, ABGEBEND, new WbciRequestStatus[] { RUEM_VA_VERSENDET }, ACTIVE, null, null },
                { EDIT_AUTOMATED_FLAG, AUFNEHMEND, new WbciRequestStatus[] { RUEM_VA_EMPFANGEN, STORNO_ERLM_EMPFANGEN }, ACTIVE, null, null },
                { EDIT_AUTOMATED_FLAG, AUFNEHMEND, new WbciRequestStatus[] { VA_VERSENDET }, ACTIVE, null, null },
        };
    }

    @DataProvider
    public Object[][] invalidActions() {
        StornoAenderungAufAnfrage stornoAen = new StornoAenderungAufAnfrage();
        return new Object[][] {
                // ACTION , ROLE, CURRENT REQUEST STATUSES, CURRENT GESCHAEFTSFALL STATUS, GESCHAEFTSFALL TYPE

                // wrong carrier role
                { CREATE_RUEM_VA, AUFNEHMEND, new WbciRequestStatus[] { VA_EMPFANGEN }, ACTIVE, null, null },

                // wrong va status
                { CREATE_RUEM_VA, ABGEBEND, new WbciRequestStatus[] { VA_VERSENDET }, ACTIVE, null, null },

                // wrong gf status
                { CREATE_RUEM_VA, ABGEBEND, new WbciRequestStatus[] { VA_EMPFANGEN }, COMPLETE, null, null },

                // wrong carrier role
                { CREATE_ABBM, AUFNEHMEND, new WbciRequestStatus[] { VA_EMPFANGEN }, ACTIVE, null, null },

                // wrong va status
                { CREATE_ABBM, ABGEBEND, new WbciRequestStatus[] { VA_VERSENDET }, ACTIVE, null, null },

                // wrong gf status
                { CREATE_ABBM, ABGEBEND, new WbciRequestStatus[] { VA_EMPFANGEN }, COMPLETE, null, null },

                // wrong va status
                { CREATE_AKM_TR, AUFNEHMEND, new WbciRequestStatus[] { VA_EMPFANGEN }, ACTIVE, null, null },

                // wrong carrier role
                { CREATE_AKM_TR, ABGEBEND, new WbciRequestStatus[] { RUEM_VA_EMPFANGEN }, ACTIVE, null, null },

                // not allowed with active tv
                { CREATE_AKM_TR, AUFNEHMEND, new WbciRequestStatus[] { RUEM_VA_EMPFANGEN, TV_VERSENDET }, ACTIVE, null, null },

                // wrong gf status
                { CREATE_AKM_TR, AUFNEHMEND, new WbciRequestStatus[] { RUEM_VA_EMPFANGEN, TV_ERLM_EMPFANGEN, STORNO_ABBM_VERSENDET }, COMPLETE, null, null },

                // wrong va status
                { CREATE_ABBM_TR, ABGEBEND, new WbciRequestStatus[] { RUEM_VA_VERSENDET }, ACTIVE, null, null },

                // wrong carrier role
                { CREATE_ABBM_TR, AUFNEHMEND, new WbciRequestStatus[] { AKM_TR_EMPFANGEN, TV_ABBM_VERSENDET }, ACTIVE, null, null },

                // not allowed with active tv
                { CREATE_ABBM_TR, ABGEBEND, new WbciRequestStatus[] { AKM_TR_EMPFANGEN, TV_EMPFANGEN }, ACTIVE, null, null },

                // not allowed with active storno
                { CREATE_ABBM_TR, ABGEBEND, new WbciRequestStatus[] { AKM_TR_EMPFANGEN, STORNO_VORGEHALTEN }, ACTIVE, null, null },

                // wrong gf status
                { CREATE_ABBM_TR, ABGEBEND, new WbciRequestStatus[] { AKM_TR_EMPFANGEN, TV_ABBM_VERSENDET }, COMPLETE, null, null },

                // wrong carrier role
                { CREATE_TV, ABGEBEND, new WbciRequestStatus[] { RUEM_VA_EMPFANGEN }, ACTIVE, null, null },

                // wrong va status
                { CREATE_TV, AUFNEHMEND, new WbciRequestStatus[] { VA_VERSENDET }, ACTIVE, null, null },

                // not allowed with active tv
                { CREATE_TV, AUFNEHMEND, new WbciRequestStatus[] { RUEM_VA_EMPFANGEN, TV_VERSENDET }, ACTIVE, null, null },

                // not allowed with active storno
                { CREATE_TV, AUFNEHMEND, new WbciRequestStatus[] { RUEM_VA_EMPFANGEN, STORNO_EMPFANGEN }, ACTIVE, null, null },

                // wrong gf status
                { CREATE_TV, AUFNEHMEND, new WbciRequestStatus[] { RUEM_VA_EMPFANGEN }, COMPLETE, null, null },

                // wrong carrier role
                { CREATE_TV_ABBM, AUFNEHMEND, new WbciRequestStatus[] { AKM_TR_VERSENDET, TV_EMPFANGEN }, ACTIVE, null, null },

                // wrong tv status
                { CREATE_TV_ABBM, ABGEBEND, new WbciRequestStatus[] { AKM_TR_VERSENDET }, ACTIVE, null, null },

                // wrong tv status
                { CREATE_TV_ABBM, ABGEBEND, new WbciRequestStatus[] { AKM_TR_VERSENDET, TV_ABBM_VERSENDET }, ACTIVE, null, null },

                // wrong gf status
                { CREATE_TV_ABBM, ABGEBEND, new WbciRequestStatus[] { AKM_TR_VERSENDET, TV_EMPFANGEN }, COMPLETE, null, null },

                // wrong carrier role
                { CREATE_TV_ERLM, AUFNEHMEND, new WbciRequestStatus[] { AKM_TR_VERSENDET, TV_EMPFANGEN }, ACTIVE, null, null },

                // wrong tv status
                { CREATE_TV_ERLM, ABGEBEND, new WbciRequestStatus[] { AKM_TR_VERSENDET }, ACTIVE, null, null },

                // wrong tv status
                { CREATE_TV_ERLM, ABGEBEND, new WbciRequestStatus[] { AKM_TR_VERSENDET, TV_ERLM_VERSENDET }, ACTIVE, null, null },

                // wrong gf status
                { CREATE_TV_ERLM, ABGEBEND, new WbciRequestStatus[] { AKM_TR_VERSENDET, TV_EMPFANGEN }, COMPLETE, null, null },

                // not allowed with active tv
                { CREATE_STORNO, AUFNEHMEND, new WbciRequestStatus[] { RUEM_VA_EMPFANGEN, TV_VERSENDET }, ACTIVE, null, null },

                // not allowed with active storno
                { CREATE_STORNO, AUFNEHMEND, new WbciRequestStatus[] { RUEM_VA_EMPFANGEN, STORNO_EMPFANGEN }, ACTIVE, null, null },

                // wrong gf status
                { CREATE_STORNO, AUFNEHMEND, new WbciRequestStatus[] { RUEM_VA_EMPFANGEN }, COMPLETE, null, null },

                // not allowed with active tv
                { CREATE_STORNO_AENDERUNG, AUFNEHMEND, new WbciRequestStatus[] { RUEM_VA_EMPFANGEN, TV_VERSENDET }, ACTIVE, null, null },

                // no new requests allowed after STR-ERLM
                { CREATE_STORNO_AENDERUNG, AUFNEHMEND, new WbciRequestStatus[] { RUEM_VA_EMPFANGEN, STORNO_ERLM_VERSENDET}, ACTIVE, null, null },

                // no new requests allowed after STR-ERLM
                { CREATE_STORNO_AENDERUNG, AUFNEHMEND, new WbciRequestStatus[] { RUEM_VA_EMPFANGEN, STORNO_ERLM_EMPFANGEN}, ACTIVE, null, null },

                // not allowed with active storno
                { CREATE_STORNO_AENDERUNG, AUFNEHMEND, new WbciRequestStatus[] { RUEM_VA_EMPFANGEN, STORNO_EMPFANGEN }, ACTIVE, null, null },

                // wrong gf status
                { CREATE_STORNO_AENDERUNG, AUFNEHMEND, new WbciRequestStatus[] { RUEM_VA_EMPFANGEN }, COMPLETE, null, null },

                // wrong va status
                { CREATE_STORNO_AENDERUNG, AUFNEHMEND, new WbciRequestStatus[] { VA_VERSENDET }, ACTIVE, null, null },

                // wrong storno status
                { CREATE_STORNO_ABBM, ABGEBEND, new WbciRequestStatus[] { AKM_TR_VERSENDET }, ACTIVE, null, null },

                // wrong storno status
                { CREATE_STORNO_ABBM, ABGEBEND, new WbciRequestStatus[] { AKM_TR_VERSENDET, STORNO_ABBM_VERSENDET }, ACTIVE, null, null },

                // wrong gf status
                { CREATE_STORNO_ABBM, AUFNEHMEND, new WbciRequestStatus[] { AKM_TR_VERSENDET, STORNO_EMPFANGEN }, COMPLETE, null, null },

                // wrong storno status
                { CREATE_STORNO_ERLM, ABGEBEND, new WbciRequestStatus[] { AKM_TR_VERSENDET }, ACTIVE, null, null },

                // wrong storno status
                { CREATE_STORNO_ERLM, ABGEBEND, new WbciRequestStatus[] { AKM_TR_VERSENDET, STORNO_ERLM_VERSENDET }, ACTIVE, null, null },

                // wrong gf status
                { CREATE_STORNO_ERLM, AUFNEHMEND, new WbciRequestStatus[] { AKM_TR_VERSENDET, STORNO_EMPFANGEN }, COMPLETE, null, null },

                // special case: ruem-va necessary
                { CREATE_STORNO_ERLM, ABGEBEND, new WbciRequestStatus[] { VA_EMPFANGEN, STORNO_EMPFANGEN }, ACTIVE, Arrays.asList(), stornoAen },

                // wrong va or storno statuses
                { CLOSE_GF, ABGEBEND, new WbciRequestStatus[] { AKM_TR_VERSENDET }, ACTIVE, null, null },
                { CLOSE_GF, ABGEBEND, new WbciRequestStatus[] { AKM_TR_VERSENDET, STORNO_ABBM_VERSENDET }, ACTIVE, null, null },

                // wrong gf status
                { CLOSE_GF, ABGEBEND, new WbciRequestStatus[] { ABBM_EMPFANGEN }, COMPLETE, null, null },

                // wrong request status, gf status
                { EDIT_AUTOMATED_FLAG, AUFNEHMEND, new WbciRequestStatus[] { RUEM_VA_EMPFANGEN, STORNO_VERSENDET }, ACTIVE, null, null },
                { EDIT_AUTOMATED_FLAG, AUFNEHMEND, new WbciRequestStatus[] { AKM_TR_EMPFANGEN }, ACTIVE, null, null },
                { EDIT_AUTOMATED_FLAG, AUFNEHMEND, new WbciRequestStatus[] { AKM_TR_VERSENDET }, ACTIVE, null, null },
                { EDIT_AUTOMATED_FLAG, AUFNEHMEND, new WbciRequestStatus[] { RUEM_VA_EMPFANGEN, TV_VERSENDET }, ACTIVE, null, null },
                { EDIT_AUTOMATED_FLAG, ABGEBEND, new WbciRequestStatus[] { RUEM_VA_VERSENDET }, COMPLETE, null, null }

        };
    }

    @Test(dataProvider = "validActions")
    public void shouldSucceed(WbciAction wbciAction, CarrierRole role, WbciRequestStatus[] geschaeftsfallRequestStatuses,
            WbciGeschaeftsfallStatus geschaeftsfallStatus, List<MeldungTyp> meldungTypen, WbciRequest wbciRequest) throws Exception {
        assertTrue(wbciAction.isActionPermitted(role, Arrays.asList(geschaeftsfallRequestStatuses), geschaeftsfallStatus,
                meldungTypen, wbciRequest));
    }

    @Test(dataProvider = "invalidActions")
    public void shouldFail(WbciAction wbciAction, CarrierRole role, WbciRequestStatus[] geschaeftsfallRequestStatuses,
            WbciGeschaeftsfallStatus geschaeftsfallStatus, List<MeldungTyp> meldungTypen, WbciRequest wbciRequest) throws Exception {
        assertFalse(wbciAction.isActionPermitted(role, Arrays.asList(geschaeftsfallRequestStatuses), geschaeftsfallStatus,
                meldungTypen, wbciRequest));
    }

    @Test
    public void isVaAction() {
        assertTrue(WbciAction.CREATE_ABBM.isVaAction());
        assertTrue(WbciAction.CREATE_RUEM_VA.isVaAction());
        assertTrue(WbciAction.CREATE_AKM_TR.isVaAction());
        assertTrue(WbciAction.CREATE_ABBM_TR.isVaAction());
        assertFalse(WbciAction.CREATE_STORNO.isVaAction());
    }

    @Test
    public void isTvAction() {
        assertTrue(WbciAction.CREATE_TV.isTvAction());
        assertTrue(WbciAction.CREATE_TV_ABBM.isTvAction());
        assertTrue(WbciAction.CREATE_TV_ERLM.isTvAction());
        assertFalse(WbciAction.CREATE_STORNO.isTvAction());
    }

    @Test
    public void isStornoAction() {
        assertTrue(WbciAction.CREATE_STORNO.isStornoAction());
        assertTrue(WbciAction.CREATE_STORNO_ABBM.isStornoAction());
        assertTrue(WbciAction.CREATE_STORNO_ERLM.isStornoAction());
        assertTrue(WbciAction.CREATE_STORNO_AENDERUNG.isStornoAction());
        assertFalse(WbciAction.CREATE_TV.isStornoAction());
    }

    @Test
    public void isCreateStornoAction() {
        assertTrue(WbciAction.CREATE_STORNO.isCreateStornoAction());
        assertTrue(WbciAction.CREATE_STORNO_AENDERUNG.isCreateStornoAction());
        assertFalse(WbciAction.CREATE_STORNO_ERLM.isCreateStornoAction());
    }

    @Test
    public void isCreateStornoErlmAction() {
        assertTrue(WbciAction.CREATE_STORNO_ERLM.isCreateStornoErlmAction());
        assertFalse(WbciAction.CREATE_STORNO.isCreateStornoErlmAction());
    }

    @Test
    public void isGfAction() {
        assertTrue(WbciAction.CLOSE_GF.isGfAction());
        assertFalse(WbciAction.CREATE_STORNO.isGfAction());
    }
}
