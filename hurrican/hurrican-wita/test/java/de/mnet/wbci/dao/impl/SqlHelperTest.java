package de.mnet.wbci.dao.impl;

import static org.testng.Assert.*;

import java.util.*;
import org.testng.annotations.Test;

import de.mnet.wbci.model.Technologie;
import de.mnet.wbci.model.WbciGeschaeftsfallStatus;

public class SqlHelperTest {

    @Test
    public void testGetTechnologieString() throws Exception {
        assertEquals(SqlHelper.getTechnologieString(null), "'TAL_ISDN','TAL_DSL','TAL_VDSL','ADSL_SA_Annex_J'," +
                        "'ADSL_SA_Annex_B','ADSL_SA','ADSL_SH','SDSL_SA','VDSL_SA','UMTS','LTE','FTTC','FTTB'," +
                        "'FTTH','HFC','KOAX','KUPFER','KUPFER_MX','GF','KUPFER_GF','SONSTIGES'"
        );
        assertEquals(SqlHelper.getTechnologieString(Arrays.asList(Technologie.FTTC, Technologie.TAL_DSL)),
                "'FTTC','TAL_DSL'"
        );
    }

    @Test
    public void testGetGeschaeftsfallStatusString() throws Exception {
        assertEquals(SqlHelper.getGeschaeftsfallStatusString(null),
                "'ACTIVE','PASSIVE','NEW_VA','NEW_VA_EXPIRED','COMPLETE'");
        assertEquals(SqlHelper.getGeschaeftsfallStatusString(Arrays.asList(WbciGeschaeftsfallStatus.ACTIVE, WbciGeschaeftsfallStatus.COMPLETE)),
                "'ACTIVE','COMPLETE'");
    }
}