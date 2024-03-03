/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 13.11.13
 */
package de.mnet.wbci.service.impl;

import java.time.*;
import java.util.*;
import javax.validation.constraints.*;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import de.augustakom.common.service.holiday.DateCalculationHelper;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.common.tools.lang.Pair;
import de.augustakom.common.tools.lang.StringTools;
import de.augustakom.hurrican.annotation.CcTxRequired;
import de.augustakom.hurrican.model.billing.Adresse;
import de.augustakom.hurrican.model.billing.BAuftrag;
import de.augustakom.hurrican.model.billing.Rufnummer;
import de.mnet.common.tools.DateConverterUtils;
import de.mnet.common.tools.NormalizePattern;
import de.mnet.common.tools.PhoneticCheck;
import de.mnet.wbci.converter.RufnummerConverter;
import de.mnet.wbci.helper.PersonOderFirmaHelper;
import de.mnet.wbci.helper.RufnummerHelper;
import de.mnet.wbci.model.DecisionAttribute;
import de.mnet.wbci.model.DecisionResult;
import de.mnet.wbci.model.DecisionVO;
import de.mnet.wbci.model.DecisionVOwithKuendigungsCheckVO;
import de.mnet.wbci.model.Firma;
import de.mnet.wbci.model.GeschaeftsfallTyp;
import de.mnet.wbci.model.KuendigungsCheckVO;
import de.mnet.wbci.model.MeldungPositionTyp;
import de.mnet.wbci.model.MeldungsCode;
import de.mnet.wbci.model.Person;
import de.mnet.wbci.model.PersonOderFirma;
import de.mnet.wbci.model.Portierungszeitfenster;
import de.mnet.wbci.model.Rufnummernportierung;
import de.mnet.wbci.model.RufnummernportierungAnlage;
import de.mnet.wbci.model.RufnummernportierungAware;
import de.mnet.wbci.model.RufnummernportierungEinzeln;
import de.mnet.wbci.model.RufnummernportierungTyp;
import de.mnet.wbci.model.Standort;
import de.mnet.wbci.model.VorabstimmungsAnfrage;
import de.mnet.wbci.model.WbciGeschaeftsfall;
import de.mnet.wbci.model.WbciGeschaeftsfallKue;
import de.mnet.wbci.model.WbciRequest;
import de.mnet.wbci.model.builder.DecisionVOBuilder;
import de.mnet.wbci.model.builder.DecisionVOwithKuendigungsCheckVOBuilder;
import de.mnet.wbci.service.WbciCommonService;
import de.mnet.wbci.service.WbciDecisionService;
import de.mnet.wbci.service.WbciKuendigungsService;

@CcTxRequired
public class WbciDecisionServiceImpl implements WbciDecisionService {

    private static final Logger LOGGER = Logger.getLogger(WbciDecisionServiceImpl.class);

    @Autowired
    private WbciCommonService wbciCommonService;
    @Autowired
    private WbciKuendigungsService wbciKuendigungsService;

    /**
     * {@inheritDoc  *
     */
    @Override
    public List<DecisionVO> evaluateDecisionData(String vorabstimmungsId) {
        VorabstimmungsAnfrage vaRequest = wbciCommonService.findWbciRequestByType(vorabstimmungsId,
                VorabstimmungsAnfrage.class).get(0);
        WbciGeschaeftsfall wbciGeschaeftsfall = vaRequest.getWbciGeschaeftsfall();

        List<DecisionVO> decisionData = new ArrayList<>();

        Adresse anschlussAdresse = wbciCommonService.getAnschlussAdresse(wbciGeschaeftsfall.getBillingOrderNoOrig());
        if (anschlussAdresse == null) {
            // avoid NullPointer while evaluating decision data and create dummy adresse so all decisions will fail
            anschlussAdresse = new Adresse();
        }

        decisionData.addAll(getKundeDecisionData(wbciGeschaeftsfall.getEndkunde(), anschlussAdresse));

        decisionData.addAll(getWeiterAnschlussInhaberDecisionData(wbciGeschaeftsfall.getWeitereAnschlussinhaber(),
                anschlussAdresse));

        if (wbciGeschaeftsfall instanceof WbciGeschaeftsfallKue) {
            decisionData.addAll(getStandortDecisionData(((WbciGeschaeftsfallKue) wbciGeschaeftsfall).getStandort(),
                    anschlussAdresse));
        }

        decisionData.add(createChangeCarrierDateDecisionData(vaRequest));

        if (StringUtils.hasText(wbciGeschaeftsfall.getStrAenVorabstimmungsId())) {
            final WbciGeschaeftsfall strAenGf = wbciCommonService.findWbciGeschaeftsfall(wbciGeschaeftsfall
                    .getStrAenVorabstimmungsId());
            if (strAenGf.getWechseltermin() != null) {
                decisionData.add(createChangeCarrierDateStrAenDecisionData(Date.from(strAenGf.getWechseltermin().atStartOfDay(ZoneId.systemDefault()).toInstant())));
            }
        }

        decisionData.add(createContractEndDecisionData(wbciGeschaeftsfall.getBillingOrderNoOrig()));

        if (wbciGeschaeftsfall instanceof RufnummernportierungAware) {
            final RufnummernportierungAware rnpAwareGeschaeftsfall = (RufnummernportierungAware) wbciGeschaeftsfall;
            if (Portierungszeitfenster.ZF3 == rnpAwareGeschaeftsfall.getRufnummernportierung()
                    .getPortierungszeitfenster()) {
                decisionData.add(createPortierungszeitfensterZF3DecisionData());
            }
        }

        decisionData.addAll(getRufnummernDecisionData(wbciGeschaeftsfall, wbciGeschaeftsfall.getBillingOrderNoOrig()));

        return decisionData;
    }

    /**
     * {@inheritDoc  *
     */
    @Override
    public List<DecisionVO> getDecisionVOsForMeldungPositionTyp(@NotNull Collection<DecisionVO> decisionVOs,
            @NotNull MeldungPositionTyp meldungPositionTyp) {
        List<DecisionVO> result = new ArrayList<>();
        for (DecisionVO decisionVO : decisionVOs) {
            if (decisionVO.getFinalMeldungsCode() != null &&
                    decisionVO.getFinalMeldungsCode().isValidForMeldungPosTyps(meldungPositionTyp)) {
                result.add(decisionVO);
            }
        }

        return result;
    }

    /**
     * {@inheritDoc  *
     */
    @Override
    public List<DecisionVO> evaluateRufnummernDecisionData(Rufnummernportierung rnp,
            List<Rufnummer> taifunRufnummern, boolean acceptMissingRufnummerInTaifun) {
        return rnp != null ? createDecisionDataForDn(rnp, taifunRufnummern, acceptMissingRufnummerInTaifun)
                : Collections.<DecisionVO>emptyList();
    }

    private List<DecisionVO> getRufnummernDecisionData(WbciGeschaeftsfall wbciGeschaeftsfall,
            Long billingOrderNoOrig) {
        if (wbciGeschaeftsfall instanceof RufnummernportierungAware) {
            Rufnummernportierung rnp = ((RufnummernportierungAware) wbciGeschaeftsfall).getRufnummernportierung();
            Collection<Rufnummer> assignedDns = wbciCommonService.getRNs(billingOrderNoOrig);

            return createDecisionDataForDn(rnp, assignedDns, true);
        }

        return Collections.<DecisionVO>emptyList();
    }

    protected List<DecisionVO> createDecisionDataForDn(Rufnummernportierung rnp, Collection<Rufnummer> billingDns,
            boolean acceptMissingRufnummerInTaifun) {
        boolean alleRufnummerPortieren = false;
        DecisionAttribute decisionAttribute;
        Set<String> wbciDns;
        if (rnp instanceof RufnummernportierungEinzeln) {
            final RufnummernportierungEinzeln einzeln = (RufnummernportierungEinzeln) rnp;
            wbciDns = RufnummerHelper.convertWbciEinzelrufnummer(einzeln);
            // we have to do this because of possible NPE during Autoboxing!!!
            final Boolean portieren = einzeln.getAlleRufnummernPortieren();
            alleRufnummerPortieren = portieren == null ? false : portieren;
            decisionAttribute = DecisionAttribute.RUFNUMMER;
        }
        else {
            wbciDns = RufnummerHelper.convertWbciRufnummerAnlage((RufnummernportierungAnlage) rnp);
            decisionAttribute = DecisionAttribute.RUFNUMMERN_BLOCK;
        }

        boolean matchingFound = false;
        List<DecisionVO> decisionData = new ArrayList<>();

        if (alleRufnummerPortieren) {
            decisionData.add(createInfoDecision(DecisionAttribute.ALLE_RUFNUMMERN_PORTIEREN, "ja", null));
        }

        for (Rufnummer billingDn : billingDns) {
            final String billingDnAsString = RufnummerConverter.convertBillingDn(billingDn);
            if (wbciDns.contains(billingDnAsString)) {
                // Rufnummer(n) aus VA auch in M-net Auftrag enthalten
                decisionData.add(createDecisionEquals(decisionAttribute, billingDnAsString, billingDn,
                        billingDnAsString, false));
                // remove found number
                wbciDns.remove(billingDnAsString);
                matchingFound = true;
            }
            else {
                // Rufnummer(n) in M-net Auftrag aber nicht in VA

                // Block- oder Einzelrufnummer
                DecisionAttribute decisionAttributeBillingDn =
                        billingDn.isBlock() ? DecisionAttribute.RUFNUMMERN_BLOCK : DecisionAttribute.RUFNUMMER;

                if ((RufnummernportierungTyp.ANLAGE.equals(rnp.getTyp()) && DecisionAttribute.RUFNUMMER
                        .equals(decisionAttributeBillingDn))
                        || (RufnummernportierungTyp.EINZEL.equals(rnp.getTyp()) && DecisionAttribute.RUFNUMMERN_BLOCK
                        .equals(decisionAttributeBillingDn))) {
                    // Die Rufnummmern des unteschiedlichen Typs werden als DecisionResult.INFO mitaufgenommen.
                    decisionData.add(createDecision(decisionAttributeBillingDn, billingDn,
                            billingDnAsString, MeldungsCode.ZWA, DecisionResult.INFO));
                }
                else if (acceptMissingRufnummerInTaifun) {
                    // Werden fehlende Rufnummern akzeptiert, dann werden diese mitaufgenommen:
                    // alleRufnummern ist TRUE => DecisionResult.OK
                    // alleRufnummern ist FALSE => DecisionResult.INFO
                    DecisionResult decisionResult = alleRufnummerPortieren ? DecisionResult.OK : DecisionResult.INFO;
                    decisionData.add(createDecision(decisionAttributeBillingDn, billingDn,
                            billingDnAsString, MeldungsCode.ZWA, decisionResult));
                }
                else {
                    // Werden fehelende Rufnummern nicht akzeptiert, weden sie als DecisionResult 'NICHT_OK'
                    // mitaufgenommen.
                    decisionData.add(createDecision(decisionAttributeBillingDn, billingDn,
                            billingDnAsString, MeldungsCode.ZWA, DecisionResult.NICHT_OK));
                }
            }
        }

        // Rufnummer(n) in VA aber nicht in M-net Auftrag (--> RNG)
        for (String wbciDn : wbciDns) {
            // wenn alleRufnummerPortieren selektiert ist und mindestens eine Rufnummer aus der VA stimmt mit dem
            // M-net-Bestand überein, dann wird für Rufnummer aus der VA, die aber nicht im Bestand sind,
            // kein MeldungsCode eingetragen, so dass es möglich ist eine RuemVA zu verschicken.
            // ==> Vorgehensweise nach WBCI FAQs Nr. 26

            MeldungsCode meldungsCode = alleRufnummerPortieren && matchingFound ? MeldungsCode.ZWA : MeldungsCode.RNG;
            DecisionResult decisionResult = alleRufnummerPortieren && matchingFound ? DecisionResult.OK
                    : DecisionResult.NICHT_OK;
            decisionData.add(new DecisionVOBuilder(decisionAttribute)
                            .withPropertyValue(wbciDn)
                            .withSuggestedMeldungsCode(meldungsCode)
                            .withFinalMeldungsCode(meldungsCode)
                            .withSuggestedResult(decisionResult)
                            .withFinalResult(decisionResult)
                            .build()
            );
        }
        return decisionData;
    }

    private List<DecisionVO> getKundeDecisionData(PersonOderFirma personOderFirma, Adresse anschlussAdresse) {
        List<DecisionVO> decisionData = new ArrayList<>();

        if (personOderFirma instanceof Person) {
            Person person = (Person) personOderFirma;

            decisionData.add(createDecisionEquals(DecisionAttribute.NACHNAME,
                    person.getNachname(),
                    anschlussAdresse.getName(),
                    true));

            decisionData.add(createDecisionEquals(DecisionAttribute.VORNAME,
                    person.getVorname(),
                    anschlussAdresse.getVorname(),
                    true));
        }
        else if (personOderFirma instanceof Firma) {
            decisionData.add(createDecisionEquals(DecisionAttribute.FIRMEN_NAME,
                    ((Firma) personOderFirma).getFirmenname(),
                    StringTools.join(new String[] { anschlussAdresse.getName(), anschlussAdresse.getVorname() }, " ",
                            true),
                    true
            ));

            final String firmennamenZusatz = ((Firma) personOderFirma).getFirmennamenZusatz();
            if (StringUtils.hasText(firmennamenZusatz)) {
                decisionData.add(createManualDecision(DecisionAttribute.FIRMENZUSATZ, firmennamenZusatz));
            }
            else {
                decisionData.add(new DecisionVOBuilder(DecisionAttribute.FIRMENZUSATZ)
                        .withPropertyValue(firmennamenZusatz)
                        .withSuggestedMeldungsCode(null)
                        .withFinalMeldungsCode(null)
                        .build());
            }
        }

        return decisionData;
    }

    private List<DecisionVO> getWeiterAnschlussInhaberDecisionData(List<PersonOderFirma> weitereAnschlussInhaber,
            Adresse anschlussAdresse) {
        List<DecisionVO> decisionData = new ArrayList<>();

        String mnetName2 = anschlussAdresse.getName2();
        String mnetVorname2 = anschlussAdresse.getVorname2();

        Pair<List<PersonOderFirma>, List<PersonOderFirma>> pair = PersonOderFirmaHelper.divideInMatchingAndNotMatching(
                weitereAnschlussInhaber, mnetName2, mnetVorname2);

        // matchende Personen/Firmen werden aufgelistet
        for (PersonOderFirma personOderFirma : pair.getFirst()) {
            if (personOderFirma instanceof Person) {
                decisionData.add(createDecisionEquals(DecisionAttribute.NACHNAME_WAI,
                        ((Person) personOderFirma).getNachname(), mnetName2, true));
                decisionData.add(createDecisionEquals(DecisionAttribute.VORNAME_WAI,
                        ((Person) personOderFirma).getVorname(), mnetVorname2, true));
            }
            else if (personOderFirma instanceof Firma) {
                decisionData.add(createDecisionEquals(DecisionAttribute.FIRMEN_NAME_WAI,
                        ((Firma) personOderFirma).getFirmenname(), mnetName2, true));
                decisionData.add(createDecisionEquals(DecisionAttribute.FIRMENZUSATZ_WAI,
                        ((Firma) personOderFirma).getFirmennamenZusatz(), mnetVorname2, true));
            }
        }

        // nicht matchende Eintraege aus der WBCI VA werden aufgelistet
        for (PersonOderFirma personOderFirma : pair.getSecond()) {
            if (personOderFirma instanceof Person) {
                decisionData.add(createManualDecision(DecisionAttribute.NACHNAME_WAI,
                        ((Person) personOderFirma).getNachname()));
                decisionData.add(createManualDecision(DecisionAttribute.VORNAME_WAI,
                        ((Person) personOderFirma).getVorname()));
            }
            else if (personOderFirma instanceof Firma) {
                decisionData.add(createManualDecision(DecisionAttribute.FIRMEN_NAME_WAI,
                        ((Firma) personOderFirma).getFirmenname()));
                decisionData.add(createManualDecision(DecisionAttribute.FIRMENZUSATZ_WAI,
                        ((Firma) personOderFirma).getFirmennamenZusatz()));
            }
        }

        // weiterer Anschlussinhaber in M-net Auftrag definiert, aber nicht in VA angegeben
        if (pair.getFirst().isEmpty() && (mnetName2 != null || mnetVorname2 != null)) {
            DecisionAttribute daNameOderFirma = (anschlussAdresse.isBusinessAddress())
                    ? DecisionAttribute.FIRMEN_NAME_WAI : DecisionAttribute.NACHNAME_WAI;
            DecisionAttribute daVornameOderFirmenzusatz = (anschlussAdresse.isBusinessAddress())
                    ? DecisionAttribute.FIRMENZUSATZ_WAI : DecisionAttribute.VORNAME_WAI;

            decisionData.add(createDecision(daNameOderFirma, null, mnetName2,
                    daNameOderFirma.getNegativeMeldungsCode(), DecisionResult.NICHT_OK));
            decisionData.add(createDecision(daVornameOderFirmenzusatz, null, mnetVorname2,
                    daVornameOderFirmenzusatz.getNegativeMeldungsCode(), DecisionResult.NICHT_OK));
        }

        return decisionData;
    }

    private List<DecisionVO> getStandortDecisionData(Standort standort, Adresse anschlussAdresse) {
        List<DecisionVO> decisionData = new ArrayList<>();

        decisionData.add(createDecisionEquals(DecisionAttribute.PLZ,
                standort.getPostleitzahl(),
                anschlussAdresse,
                anschlussAdresse.getPlz(),
                false));

        decisionData.add(createDecisionEquals(DecisionAttribute.ORT,
                standort.getOrt(),
                anschlussAdresse,
                anschlussAdresse.getOrt(),
                true, NormalizePattern.CITY_PATTERNS));

        decisionData.add(createDecisionEquals(DecisionAttribute.STRASSENNAME,
                standort.getStrasse().getStrassenname(),
                anschlussAdresse,
                anschlussAdresse.getStrasse(),
                true, NormalizePattern.STREET_PATTERNS));

        decisionData.add(createDecisionEquals(DecisionAttribute.HAUSNUMMER,
                standort.getStrasse().getHausnummer(),
                anschlussAdresse,
                anschlussAdresse.getNummer(),
                false));

        decisionData.add(createInfoDecision(DecisionAttribute.HAUSNUMMERZUSATZ,
                standort.getStrasse().getHausnummernZusatz(),
                anschlussAdresse.getHausnummerZusatz()));

        return decisionData;
    }

    private DecisionVO createDecisionEquals(DecisionAttribute attribute,
            String propertyValue,
            String controlValue,
            boolean usePhoneticCheck,
            NormalizePattern... normalizePatterns) {
        return createDecisionEquals(attribute, propertyValue, null, controlValue, usePhoneticCheck,
                normalizePatterns);
    }

    private DecisionVO createDecisionEquals(DecisionAttribute attribute,
            String propertyValue,
            Object controlObject,
            String controlValue,
            boolean usePhoneticCheck,
            NormalizePattern... normalizePatterns) {
        DecisionVOBuilder decisionVOBuilder = new DecisionVOBuilder(attribute)
                .withPropertyValue(propertyValue)
                .withControlObject(controlObject)
                .withControlValue(controlValue);

        boolean isEquals = (usePhoneticCheck)
                ? new PhoneticCheck(PhoneticCheck.Codec.COLOGNE, true, normalizePatterns).isPhoneticEqual(
                propertyValue, controlValue)
                : propertyValue.equals(controlValue);

        if (!isEquals) {
            decisionVOBuilder
                    .withSuggestedMeldungsCode(attribute.getNegativeMeldungsCode())
                    .withFinalMeldungsCode(attribute.getNegativeMeldungsCode())
                    .withSuggestedResult(DecisionResult.NICHT_OK)
                    .withFinalResult(DecisionResult.NICHT_OK);
        }

        return decisionVOBuilder.build();
    }

    private DecisionVO createDecision(DecisionAttribute attribute,
            Object controlObject,
            String controlValue,
            MeldungsCode meldungsCode,
            DecisionResult decisionResult) {
        return new DecisionVOBuilder(attribute)
                .withControlObject(controlObject)
                .withControlValue(controlValue)
                .withSuggestedMeldungsCode(meldungsCode)
                .withFinalMeldungsCode(meldungsCode)
                .withSuggestedResult(decisionResult)
                .withFinalResult(decisionResult)
                .build();
    }

    private DecisionVO createManualDecision(DecisionAttribute attribute, String propertyValue) {
        return new DecisionVOBuilder(attribute)
                .withPropertyValue(propertyValue)
                .withSuggestedMeldungsCode(null)
                .withFinalMeldungsCode(null)
                .withSuggestedResult(DecisionResult.MANUELL)
                .withFinalResult(DecisionResult.MANUELL)
                .build();
    }

    private DecisionVO createInfoDecision(DecisionAttribute attribute, String propertyValue, String controlValue) {
        return new DecisionVOBuilder(attribute)
                .withPropertyValue(propertyValue)
                .withControlValue(controlValue)
                .withSuggestedResult(DecisionResult.INFO)
                .withFinalResult(DecisionResult.INFO)
                .build();
    }

    private DecisionVO createChangeCarrierDateStrAenDecisionData(Date kundenwunschterminStrAen) {
        String kundenwunschterminAsString = kundenwunschterminStrAen != null
                ? DateTools.formatDate(kundenwunschterminStrAen, DateTools.PATTERN_DAY_MONTH_YEAR)
                : null;

        return createInfoDecision(DecisionAttribute.STR_AEN_WECHSELTERMIN, kundenwunschterminAsString,
                kundenwunschterminAsString);
    }

    private DecisionVO createContractEndDecisionData(Long billingOrderNoOrig) {
        String vertragsende = null;
        try {
            final BAuftrag billingAuftrag = wbciKuendigungsService.getCheckedBillingAuftrag(billingOrderNoOrig);
            vertragsende = DateTools.formatDate(billingAuftrag.getAktuellesVertragsende(), DateTools.PATTERN_DAY_MONTH_YEAR);
        }
        catch (Exception e) {
            // Auftrag nicht gefunden -> Exception nur loggen
            LOGGER.error(e);
        }

        return createInfoDecision(DecisionAttribute.AKT_VERTRAGSENDE, null, vertragsende);
    }

    private DecisionVO createPortierungszeitfensterZF3DecisionData() {
        return new DecisionVOBuilder(DecisionAttribute.PORTIERUNGSZEITFENSTER)
                .withPropertyValue(Portierungszeitfenster.ZF3.name())
                .withSuggestedMeldungsCode(MeldungsCode.SONST)
                .withFinalMeldungsCode(MeldungsCode.SONST)
                .withSuggestedResult(DecisionResult.NICHT_OK)
                .withFinalResult(DecisionResult.NICHT_OK)
                .build();
    }

    protected DecisionVOwithKuendigungsCheckVO createChangeCarrierDateDecisionData(WbciRequest wbciRequest) {
        DecisionVOwithKuendigungsCheckVOBuilder decisionVOBuilder = new DecisionVOwithKuendigungsCheckVOBuilder(
                DecisionAttribute.KUNDENWUNSCHTERMIN);
        Date earliestCarrierChangeDate = null;
        Date kundenwunschtermin = Date.from(wbciRequest.getWbciGeschaeftsfall().getKundenwunschtermin().atStartOfDay(ZoneId.systemDefault()).toInstant());

        KuendigungsCheckVO kuendigungsCheckVO = wbciKuendigungsService.doKuendigungsCheck(
                wbciRequest.getWbciGeschaeftsfall().getBillingOrderNoOrig(),
                DateConverterUtils.asLocalDateTime(wbciRequest.getCreationDate()));

        if (GeschaeftsfallTyp.VA_RRNP.equals(wbciRequest.getWbciGeschaeftsfall().getTyp())) {
            // in the case of RRNP the earliest change date is always one working day from today
            // independent of the contract end date.
            earliestCarrierChangeDate = Date.from(DateCalculationHelper.getDateInWorkingDaysFromNow(1).toLocalDate().atStartOfDay(ZoneId.systemDefault()).toInstant());
        }
        else {
            if (!(kuendigungsCheckVO.getKuendigungsstatus() != null
                    && kuendigungsCheckVO.getKuendigungsstatus().isAskSales())
                    && kuendigungsCheckVO.getCalculatedEarliestCancelDate() != null) {
                earliestCarrierChangeDate = Date.from(kuendigungsCheckVO.getCalculatedEarliestCancelDate().atZone(ZoneId.systemDefault()).toInstant());
            }
        }

        String carrierChangeDateAsString = earliestCarrierChangeDate != null
                ? DateTools.formatDate(earliestCarrierChangeDate, DateTools.PATTERN_DAY_MONTH_YEAR)
                : null;
        String kundenwunschterminAsString = kundenwunschtermin != null
                ? DateTools.formatDate(kundenwunschtermin, DateTools.PATTERN_DAY_MONTH_YEAR)
                : null;

        decisionVOBuilder
                .withKuendigungsCheckVO(kuendigungsCheckVO)
                .withPropertyValue(kundenwunschterminAsString)
                .withControlObject(earliestCarrierChangeDate)
                .withControlValue(carrierChangeDateAsString);

        if (earliestCarrierChangeDate == null ||
                (kundenwunschtermin != null && kundenwunschtermin.before(earliestCarrierChangeDate))) {
            decisionVOBuilder
                    .withSuggestedMeldungsCode(MeldungsCode.NAT)
                    .withFinalMeldungsCode(MeldungsCode.NAT)
                    .withSuggestedResult(DecisionResult.ABWEICHEND)
                    .withFinalResult(DecisionResult.ABWEICHEND);
        }

        return decisionVOBuilder.build();
    }
}
