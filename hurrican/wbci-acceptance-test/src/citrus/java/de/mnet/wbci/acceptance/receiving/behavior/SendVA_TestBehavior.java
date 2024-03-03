/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 25.09.13
 */
package de.mnet.wbci.acceptance.receiving.behavior;

import java.util.*;

import de.augustakom.hurrican.model.billing.Rufnummer;
import de.augustakom.hurrican.model.billing.factory.GeneratedTaifunData;
import de.mnet.wbci.acceptance.AbstractTestBehavior;
import de.mnet.wbci.acceptance.common.builder.WbciGeschaeftsfallKueMrnKftBuilder;
import de.mnet.wbci.acceptance.common.builder.WbciGeschaeftsfallKueOrnKftBuilder;
import de.mnet.wbci.acceptance.common.builder.WbciGeschaeftsfallRrnpKftBuilder;
import de.mnet.wbci.citrus.VariableNames;
import de.mnet.wbci.model.Anrede;
import de.mnet.wbci.model.CarrierCode;
import de.mnet.wbci.model.GeschaeftsfallTyp;
import de.mnet.wbci.model.IOType;
import de.mnet.wbci.model.PersonOderFirma;
import de.mnet.wbci.model.Portierungszeitfenster;
import de.mnet.wbci.model.RequestTyp;
import de.mnet.wbci.model.RufnummerOnkz;
import de.mnet.wbci.model.Rufnummernportierung;
import de.mnet.wbci.model.RufnummernportierungAware;
import de.mnet.wbci.model.Standort;
import de.mnet.wbci.model.WbciGeschaeftsfall;
import de.mnet.wbci.model.WbciGeschaeftsfallKue;
import de.mnet.wbci.model.WbciGeschaeftsfallStatus;
import de.mnet.wbci.model.WbciRequestStatus;
import de.mnet.wbci.model.builder.PersonBuilder;
import de.mnet.wbci.model.builder.RufnummerOnkzTestBuilder;
import de.mnet.wbci.model.builder.RufnummernportierungEinzelnBuilder;
import de.mnet.wbci.model.builder.StandortBuilder;
import de.mnet.wbci.model.builder.StrasseBuilder;
import de.mnet.wbci.model.builder.WbciGeschaeftsfallBuilder;

/**
 * Performs basic test steps for sending a VA, when M-Net is the receiving carrier.
 * <p/>
 * <pre>
 *      AtlasESB                        Hurrican (receiving carrier)
 *                          <-          VA_KUEMRN / VA_KUEORN / VA_RRNP
 * </pre>
 *
 *
 */
public class SendVA_TestBehavior extends AbstractTestBehavior {

    private final WbciGeschaeftsfallBuilder wbciGeschaeftsfallBuilder;
    protected GeneratedTaifunData generatedTaifunData;
    private boolean autoprocessing = false;
    private boolean vaLinkedToStrAenGf = false;
    private CarrierCode abgebenderEKP = CarrierCode.DTAG;
    private Long userId = null;
    private Rufnummernportierung rufnummernportierung;
    private Long auftragId;
    private Long billingOrderNoOrig;

    private PersonOderFirma endkunde;
    private List<PersonOderFirma> weitereAnschlussinhaber;
    private boolean dontUseWeitereAnschlussinhaber = false;
    private Standort standort;

    public SendVA_TestBehavior() {
        this(null);
    }

    public SendVA_TestBehavior(WbciGeschaeftsfallBuilder wbciGeschaeftsfallBuilder) {
        this.wbciGeschaeftsfallBuilder = wbciGeschaeftsfallBuilder;
    }

    public SendVA_TestBehavior withEndkunde(PersonOderFirma endkunde) {
        this.endkunde = endkunde;
        return this;
    }

    public SendVA_TestBehavior withWeitereAnschlussinhaber(List<PersonOderFirma> weitereAnschlussinhaber) {
        this.weitereAnschlussinhaber = weitereAnschlussinhaber;
        return this;
    }

    public SendVA_TestBehavior withStandort(Standort standort) {
        this.standort = standort;
        return this;
    }

    /**
     * Used for indicating whether the VA request is created as a result of a previous WBCI Preagreement being cancelled
     * via a STR-AEN request. <br /> When this flag is set to true the preagreement Id from the previous Preagreement is
     * read and used for setting the StrAenVorabstimmungsId property in the new Preagreement. <br /> An extra check is
     * done to ensure that the status of the StrAenVa is set to {@link WbciGeschaeftsfallStatus#COMPLETE}
     *
     * @param vaLinkedToStrAenGf
     * @return
     */
    public SendVA_TestBehavior withVaLinkedToStrAenGf(boolean vaLinkedToStrAenGf) {
        this.vaLinkedToStrAenGf = vaLinkedToStrAenGf;
        return this;
    }

    public SendVA_TestBehavior withAbgebenderEKP(CarrierCode abgebenderEKP) {
        this.abgebenderEKP = abgebenderEKP;
        return this;
    }

    public SendVA_TestBehavior withAutoprocessing(boolean autoprocessing) {
        this.autoprocessing = autoprocessing;
        return this;
    }

    public SendVA_TestBehavior withUserId(Long userId) {
        this.userId = userId;
        return this;
    }

    public SendVA_TestBehavior withRufnummernportierung(Rufnummernportierung rufnummernportierung) {
        this.rufnummernportierung = rufnummernportierung;
        return this;
    }

    public SendVA_TestBehavior withGeneratedTaifunData(final GeneratedTaifunData generatedTaifunData) {
        this.generatedTaifunData = generatedTaifunData;

        withEndkunde(new PersonBuilder()
                .withNachname(generatedTaifunData.getAddress().getName())
                .withVorname(generatedTaifunData.getAddress().getVorname())
                .withAnrede(Anrede.HERR)
                .build());

        if (generatedTaifunData.getAddress().getName2() == null) {
            dontUseWeitereAnschlussinhaber = true;
        }
        else {
            List<PersonOderFirma> weitere = new ArrayList<>();
            weitere.add(new PersonBuilder()
                    .withNachname(generatedTaifunData.getAddress().getName2())
                    .withVorname(generatedTaifunData.getAddress().getVorname2())
                    .withAnrede(Anrede.HERR)
                    .build());
            withWeitereAnschlussinhaber(weitere);
        }

        withStandort(new StandortBuilder()
                        .withOrt(generatedTaifunData.getAddress().getOrt())
                        .withPostleitzahl(generatedTaifunData.getAddress().getPlz())
                        .withStrasse(new StrasseBuilder()
                                .withStrassenname(generatedTaifunData.getAddress().getStrasse())
                                .withHausnummer(generatedTaifunData.getAddress().getNummer())
                                .build())
                        .build());

        List<RufnummerOnkz> rufnummern = new ArrayList<>();
        for (Rufnummer dn : generatedTaifunData.getDialNumbers()) {
            rufnummern.add(new RufnummerOnkzTestBuilder()
                    .withOnkz(dn.getOnKzWithoutLeadingZeros())
                    .withRufnummer(dn.getDnBase())
                    .build());
        }
        withRufnummernportierung(new RufnummernportierungEinzelnBuilder()
                .withAlleRufnummernPortieren(true)
                .withRufnummerOnkzs(rufnummern)
                .withPortierungszeitfenster(Portierungszeitfenster.ZF2)
                .build());

        return this;
    }

    @Override
    public void apply() {
        WbciGeschaeftsfall wbciGeschaeftsfall = buildWbciGeschaeftsfall(getGeschaeftsfallTyp());

        writeToVariables(generatedTaifunData);

        wbciGeschaeftsfall.setAutomatable(autoprocessing);

        if (vaLinkedToStrAenGf) {
            hurrican().enrichGeschaeftsfallWithStrAenPreagreementId(wbciGeschaeftsfall);
        }
        if (standort != null && wbciGeschaeftsfall instanceof WbciGeschaeftsfallKue) {
            ((WbciGeschaeftsfallKue) wbciGeschaeftsfall).setStandort(standort);
        }
        if (endkunde != null) {
            wbciGeschaeftsfall.setEndkunde(endkunde);
        }

        if (dontUseWeitereAnschlussinhaber) {
            wbciGeschaeftsfall.setWeitereAnschlussinhaber(null);
        }
        else if (weitereAnschlussinhaber != null && !weitereAnschlussinhaber.isEmpty()) {
            wbciGeschaeftsfall.setWeitereAnschlussinhaber(weitereAnschlussinhaber);
        }

        if (rufnummernportierung != null && wbciGeschaeftsfall instanceof RufnummernportierungAware) {
            ((RufnummernportierungAware) wbciGeschaeftsfall).setRufnummernportierung(rufnummernportierung);
        }
        if (billingOrderNoOrig != null) {
            wbciGeschaeftsfall.setBillingOrderNoOrig(billingOrderNoOrig);
        }
        if (auftragId != null) {
            wbciGeschaeftsfall.setAuftragId(auftragId);
        }
        wbciGeschaeftsfall.setUserId(userId);

        hurrican().createWbciVorgang(wbciGeschaeftsfall);
        hurrican().assertVaRequestScheduled(false);
        hurrican().assertWbciOutgoingRequestCreated();
        hurrican().assertIoArchiveEntryCreated(IOType.OUT, getGeschaeftsfallTyp());
        hurrican().assertKlaerfallStatus(false, null);
        hurrican().assertWbciBaseRequestMetaDataSet(IOType.OUT, WbciGeschaeftsfallStatus.ACTIVE);
        hurrican().assertVaRequestStatus(WbciRequestStatus.VA_VERSENDET);
        hurrican().assertKundenwunschtermin(VariableNames.REQUESTED_CUSTOMER_DATE, RequestTyp.VA);
        hurrican().assertGfStatus(WbciGeschaeftsfallStatus.ACTIVE);

        if (vaLinkedToStrAenGf) {
            hurrican().assertLinkedStrAenGfStatus(WbciGeschaeftsfallStatus.COMPLETE);
        }
        hurrican().assertKundenwunschtermin(wbciGeschaeftsfall.getKundenwunschtermin(), RequestTyp.VA);
        hurrican().assertWechselterminIsNull();
        hurrican().assertAutomatable(autoprocessing);
        hurrican().assertVaAnswerDeadlineIsSet();
        atlas().receiveCarrierChangeRequest(retrieveMessageTemplate(getGeschaeftsfallTyp()));
    }

    private WbciGeschaeftsfall buildWbciGeschaeftsfall(GeschaeftsfallTyp geschaeftsfallTyp) {

        if (wbciGeschaeftsfallBuilder != null) {
            return (WbciGeschaeftsfall) wbciGeschaeftsfallBuilder.withVorabstimmungsId(getPreAgreementId()).build();
        }
        switch (geschaeftsfallTyp) {
            case VA_KUE_MRN:
                return new WbciGeschaeftsfallKueMrnKftBuilder(getCdmVersion())
                        .withAbgebenderEKP(abgebenderEKP)
                        .withVorabstimmungsId(getPreAgreementId())
                        .build();
            case VA_KUE_ORN:
                return new WbciGeschaeftsfallKueOrnKftBuilder(getCdmVersion())
                        .withAbgebenderEKP(abgebenderEKP)
                        .withVorabstimmungsId(getPreAgreementId())
                        .build();
            case VA_RRNP:
                return new WbciGeschaeftsfallRrnpKftBuilder(getCdmVersion())
                        .withAbgebenderEKP(abgebenderEKP)
                        .withVorabstimmungsId(getPreAgreementId())
                        .build();
            default:
                throw new IllegalArgumentException(String.format("Unsupported GeschaeftsfallTyp '%s'",
                        geschaeftsfallTyp));
        }
    }

    private String retrieveMessageTemplate(GeschaeftsfallTyp geschaeftsfallTyp) {
        switch (geschaeftsfallTyp) {
            case VA_KUE_MRN:
                return "VA_KUEMRN";
            case VA_KUE_ORN:
                return "VA_KUEORN";
            case VA_RRNP:
                return "VA_RRNP";
            default:
                throw new IllegalArgumentException(String.format("Unsupported GeschaeftsfallTyp '%s'",
                        geschaeftsfallTyp));
        }
    }

    public SendVA_TestBehavior withRealTaifunAndHurricanAuftrag(Long billingOrderNoOrig, Long auftragId) {
        this.billingOrderNoOrig = billingOrderNoOrig;
        this.auftragId = auftragId;
        return this;
    }
}
