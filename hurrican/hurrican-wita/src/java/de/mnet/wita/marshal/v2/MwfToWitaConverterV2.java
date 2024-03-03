/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 24.09.2014
 */
package de.mnet.wita.marshal.v2;

import com.google.common.collect.ImmutableMap;

import de.mnet.esb.cdm.supplierpartner.lineorderservice.v2.AenderungskennzeichenType;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v2.AktionscodeType;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v2.AnsprechpartnerRolleType;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v2.GeschaeftsfallArtType;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v2.GeschaeftsfallEnumType;
import de.mnet.wita.message.GeschaeftsfallTyp;
import de.mnet.wita.message.auftrag.AktionsCode;
import de.mnet.wita.message.auftrag.AnsprechpartnerRolle;
import de.mnet.wita.message.meldung.position.AenderungsKennzeichen;
import de.mnet.wita.message.meldung.position.GeschaeftsfallArt;

@SuppressWarnings("Duplicates")
public class MwfToWitaConverterV2 {
    // @formatter:off
    private static final ImmutableMap<AenderungsKennzeichen, AenderungskennzeichenType> mwfToWitaAenderungkennzeichen
            = ImmutableMap.of(
                AenderungsKennzeichen.STANDARD, AenderungskennzeichenType.STANDARD,
                AenderungsKennzeichen.STORNO, AenderungskennzeichenType.STORNO,
                AenderungsKennzeichen.TERMINVERSCHIEBUNG, AenderungskennzeichenType.TERMINVERSCHIEBUNG);

    private static final ImmutableMap<GeschaeftsfallArt, GeschaeftsfallArtType> mwfToWitaGeschaeftsfallArt
            = ImmutableMap.<GeschaeftsfallArt, GeschaeftsfallArtType>builder()
                    .put(GeschaeftsfallArt.AENDERUNG, GeschaeftsfallArtType.AENDERUNG)
                    .put(GeschaeftsfallArt.ANBIETERWECHSEL, GeschaeftsfallArtType.ANBIETERWECHSEL)
                    .put(GeschaeftsfallArt.AUSKUNFT, GeschaeftsfallArtType.AUSKUNFT)
                    .put(GeschaeftsfallArt.BEREITSTELLUNG, GeschaeftsfallArtType.BEREITSTELLUNG)
                    .put(GeschaeftsfallArt.BEREITSTELLUNG_SERVICE, GeschaeftsfallArtType.BEREITSTELLUNG_SERVICE)
                    .put(GeschaeftsfallArt.KUENDIGUNG, GeschaeftsfallArtType.KUENDIGUNG)
                    .put(GeschaeftsfallArt.PRODUKTGRUPPENWECHSEL, GeschaeftsfallArtType.PRODUKTGRUPPENWECHSEL)
                    .build();

    private static final ImmutableMap<GeschaeftsfallTyp, GeschaeftsfallEnumType> mwfToWitaGeschaeftsfall
            = ImmutableMap.<GeschaeftsfallTyp, GeschaeftsfallEnumType>builder()
                    .put(GeschaeftsfallTyp.BEREITSTELLUNG, GeschaeftsfallEnumType.NEU)
                    .put(GeschaeftsfallTyp.BESTANDSUEBERSICHT, GeschaeftsfallEnumType.AUS_BUE)
                    .put(GeschaeftsfallTyp.KUENDIGUNG_KUNDE, GeschaeftsfallEnumType.KUE_KD)
                    .put(GeschaeftsfallTyp.KUENDIGUNG_TELEKOM, GeschaeftsfallEnumType.KUE_DT)
                    .put(GeschaeftsfallTyp.LEISTUNGSMERKMAL_AENDERUNG, GeschaeftsfallEnumType.AEN_LMAE)
                    .put(GeschaeftsfallTyp.LEISTUNGS_AENDERUNG, GeschaeftsfallEnumType.LAE)
                    .put(GeschaeftsfallTyp.PORTWECHSEL, GeschaeftsfallEnumType.SER_POW)
                    .put(GeschaeftsfallTyp.PRODUKTGRUPPENWECHSEL, GeschaeftsfallEnumType.PGW)
                    .put(GeschaeftsfallTyp.PROVIDERWECHSEL, GeschaeftsfallEnumType.PV)
                    .put(GeschaeftsfallTyp.VERBUNDLEISTUNG, GeschaeftsfallEnumType.VBL)
                    .build();
    // @formatter:on

    public static AenderungskennzeichenType map(AenderungsKennzeichen aenderungskennzeichen) {
        return mwfToWitaAenderungkennzeichen.get(aenderungskennzeichen);
    }

    public static GeschaeftsfallArtType map(GeschaeftsfallArt geschaeftsfallArt) {
        return mwfToWitaGeschaeftsfallArt.get(geschaeftsfallArt);
    }

    public static GeschaeftsfallEnumType map(GeschaeftsfallTyp geschaeftsfallTyp) {
        return mwfToWitaGeschaeftsfall.get(geschaeftsfallTyp);
    }

    /**
     * Converts the {@link AnsprechpartnerRolle} enum in wita specific values - must not be contained in mnet wita
     * format as it is Telekom specific information subject to changes.
     */
    public static AnsprechpartnerRolleType convertAnsprechpartnerRolle(AnsprechpartnerRolle rolle) {
        if (rolle == AnsprechpartnerRolle.TECHNIK) {
            return AnsprechpartnerRolleType.TECHNIK;
        }
        else if (rolle == AnsprechpartnerRolle.AM) {
            return AnsprechpartnerRolleType.AUFTRAGSMANAGEMENT;
        }
        else {
            throw new RuntimeException("Unknown value for field rolle");
        }
    }


    public static AktionscodeType aktionsCode(AktionsCode input) {
        switch (input) {
            case AENDERUNG:
                return AktionscodeType.A;
            case WEGFALL:
                return AktionscodeType.W;
            case ZUGANG:
                return AktionscodeType.Z;
            default:
                throw new RuntimeException("Unknown actionscode");
        }
    }
}
