/*

 * Copyright (c) 2017 - M-net Telekommunikations GmbH

 * All rights reserved.

 * -------------------------------------------------------

 * File created: 06.03.2017

 */

package de.mnet.hurrican.wholesale.ws.outbound.testdata;


import de.mnet.esb.cdm.supplierpartner.wholesaleorderservice.v2.complex.FTTxAnschlussEinfachType;
import de.mnet.esb.cdm.supplierpartner.wholesaleorderservice.v2.message.MeldungsattributeAKMPVType;
import de.mnet.esb.cdm.supplierpartner.wholesaleorderservice.v2.message.MeldungstypAKMPVType;

/**
 * Created by freitagin on 06.03.2017.
 */

public class MeldungstypAKMPVTypeTestdata {

    public static final String VORABSTIMMUNG_ID = "DEU.MNET.VH00000084";
    public static final String VERTRAGSNUMMER = "0815123";
    public static final String EXTERNE_AUFTRAGSNUMMER = "42";
    public static final String AUFTRAGGEBERNUMMER = "1_und_1";
    public static final String LINE_ID = "DEU1234345";

    public static MeldungstypAKMPVType createMeldungstypAKMPVType() {
        MeldungstypAKMPVType meldungstypAKMPVType = new MeldungstypAKMPVType();
        meldungstypAKMPVType.setMeldungsattribute(createMeldungsattributeAKMPVType());
        return meldungstypAKMPVType;
    }

    private static MeldungsattributeAKMPVType createMeldungsattributeAKMPVType() {
        MeldungsattributeAKMPVType meldungsattributeAKMPVType = new MeldungsattributeAKMPVType();
        meldungsattributeAKMPVType.setVorabstimmungId(VORABSTIMMUNG_ID);
        meldungsattributeAKMPVType.setVertragsnummer(VERTRAGSNUMMER);
        meldungsattributeAKMPVType.setExterneAuftragsnummer(EXTERNE_AUFTRAGSNUMMER);
        meldungsattributeAKMPVType.setAuftraggebernummer(AUFTRAGGEBERNUMMER);
        meldungsattributeAKMPVType.setAnschluss(createFtTxAnschlussEinfachType());
        return meldungsattributeAKMPVType;
    }

    private static FTTxAnschlussEinfachType createFtTxAnschlussEinfachType() {
        FTTxAnschlussEinfachType anschluss = new FTTxAnschlussEinfachType();
        anschluss.setLineId(LINE_ID);
        return anschluss;
    }

}
