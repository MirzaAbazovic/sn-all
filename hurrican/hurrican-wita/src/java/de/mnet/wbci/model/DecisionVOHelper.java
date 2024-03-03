/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 13.11.13
 */
package de.mnet.wbci.model;

import static org.apache.commons.lang.StringUtils.*;

import java.time.format.*;
import java.util.*;
import javax.annotation.*;
import javax.validation.constraints.*;
import org.apache.commons.collections.CollectionUtils;

import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.hurrican.model.billing.Rufnummer;
import de.augustakom.hurrican.model.billing.RufnummerPortierungSelection;
import de.augustakom.hurrican.model.shared.iface.AddressModel;
import de.mnet.wbci.exception.InvalidRufnummerPortierungException;
import de.mnet.wbci.model.builder.AbbruchmeldungBuilder;
import de.mnet.wbci.model.builder.DecisionVOBuilder;
import de.mnet.wbci.model.builder.MeldungPositionAbbmRufnummerBuilder;
import de.mnet.wbci.model.builder.MeldungPositionAbbruchmeldungBuilder;
import de.mnet.wbci.model.builder.MeldungPositionAbbruchmeldungTechnRessourceBuilder;
import de.mnet.wbci.model.builder.MeldungPositionRueckmeldungVaBuilder;
import de.mnet.wbci.model.builder.StandortBuilder;
import de.mnet.wbci.model.builder.StrasseBuilder;

/**
 * Hilfsklasse fuer {@link DecisionVO} Objekte.
 */
public class DecisionVOHelper {

    /**
     * Filtert aus einer Collection von {@link DecisionVO} Objekten ein Set an {@link MeldungPositionAbbruchmeldung}en.
     * Aus der übergebenen Collection werden dabei nur die {@link DecisionVO} berücksichtigt, die auch für die ABBM
     * zugelassen sind! Da jeder Meldungscode nur einmal in einer Meldung vorkommen darf, werden diese entsprechend
     * konsolidiert. <br> <br> Sonderfall MeldungsCode.RNG: <br> Alle geschaltenen Einzelrufnummern werden in die
     * Rufnummernliste aufgenommen, wenn mindestens eine passende Rufnummer gefunden wurde. Siehe {@link
     * #extractMeldungPositionAbbmRufnummern(java.util.Collection)}.
     *
     * @param decisionVOs          alle relevanten DecisionVOs
     * @param activeAbbmRufnummern alle geschaltenen Einzelrufnummern
     * @return
     */
    public static Set<MeldungPositionAbbruchmeldung> extractMeldungPositionAbbruchmeldung(
            final @NotNull Collection<DecisionVO> decisionVOs,
            final Set<MeldungPositionAbbmRufnummer> activeAbbmRufnummern) {
        Map<MeldungsCode, MeldungPositionAbbruchmeldungBuilder> abbmBuilderMap = new HashMap<>();
        for (DecisionVO decisionVO : decisionVOs) {
            updateFinalResult(decisionVO, decisionVO.getFinalResult());
            // unvalid MC will be ignored
            if (decisionVO.getFinalMeldungsCode().isValidForMeldungPosTyps(MeldungPositionTyp.ABBM)) {
                MeldungPositionAbbruchmeldungBuilder builder = new MeldungPositionAbbruchmeldungBuilder();
                if (abbmBuilderMap.containsKey(decisionVO.getFinalMeldungsCode())) {
                    builder = abbmBuilderMap.get(decisionVO.getFinalMeldungsCode());
                }

                builder.withMeldungsCode(decisionVO.getFinalMeldungsCode())
                        .withMeldungsText(decisionVO.getFinalMeldungsCode().getStandardText());

                if (MeldungsCode.RNG.equals(decisionVO.getFinalMeldungsCode())) {
                    // bei RNG übergegebenen Rufnummer hizufügen.
                    builder.withRufnummern(activeAbbmRufnummern);
                }
                abbmBuilderMap.put(decisionVO.getFinalMeldungsCode(), builder);
            }
        }

        Set<MeldungPositionAbbruchmeldung> result = new HashSet<>();
        for (MeldungPositionAbbruchmeldungBuilder builder : abbmBuilderMap.values()) {
            result.add(builder.build());
        }
        return result;
    }

    /**
     * Filtert aus einer Liste von {@link DecisionVO} Objekten die daraus resultierenden {@link
     * MeldungPositionRueckmeldungVa}s. Die uebergebene Liste muss dabei schon auf die RUEM-VA MeldungsCodes gefiltert
     * sein! <br> <br> Sonderfall MeldungsCode.ZWA bzw. MeldungsCode.NAT: <br> Sofern ein MeldungsCode NAT gesetzt ist,
     * wird dieser als Position zurueck gemeldet; sonst ZWA.
     * <p/>
     * (Die weiteren moeglichen MeldungsCodes - z.B. ADA - koennen sowohl bei ZWA als auch bei NAT vorkommen.)
     *
     * @param decisionVOs
     * @return
     */
    public static Set<MeldungPositionRueckmeldungVa> extractMeldungPositionRueckmeldungVa(
            @NotNull Collection<DecisionVO> decisionVOs) {

        Map<MeldungsCode, MeldungPositionRueckmeldungVaBuilder> ruemVaBuilderMap = new HashMap<>();

        for (DecisionVO decisionVO : consolidateZwaAndNatMeldungsCodes(decisionVOs)) {
            // unvalid MC will be ignored
            if (decisionVO.getFinalMeldungsCode().isValidForMeldungPosTyps(MeldungPositionTyp.RUEM_VA)) {

                MeldungPositionRueckmeldungVaBuilder builder = new MeldungPositionRueckmeldungVaBuilder();
                if (ruemVaBuilderMap.containsKey(decisionVO.getFinalMeldungsCode())) {
                    builder = ruemVaBuilderMap.get(decisionVO.getFinalMeldungsCode());
                }

                builder.withMeldungsCode(decisionVO.getFinalMeldungsCode())
                        .withMeldungsText(decisionVO.getFinalMeldungsCode().getStandardText());

                // On ADA-Meldungscodes add the hurrican address
                if (decisionVO.getFinalMeldungsCode().isADACode()
                        && decisionVO.getControlObject() instanceof AddressModel) {
                    builder.withStandortAbweichend(createStandort(
                            decisionVO.getFinalMeldungsCode(),
                            (AddressModel) decisionVO.getControlObject()));
                }
                ruemVaBuilderMap.put(decisionVO.getFinalMeldungsCode(), builder);
            }
        }

        Set<MeldungPositionRueckmeldungVa> result = new HashSet<>();
        for (MeldungPositionRueckmeldungVaBuilder builder : ruemVaBuilderMap.values()) {
            result.add(builder.build());
        }
        return result;
    }

    public static Standort createStandort(MeldungsCode code, AddressModel addressModel) {
        StandortBuilder standortBuilder = new StandortBuilder();
        switch (code) {
            case ADAORT:
                standortBuilder.withOrt(addressModel.getOrt());
                break;
            case ADAPLZ:
                standortBuilder.withPostleitzahl(addressModel.getPlz());
                break;
            case ADASTR:
                standortBuilder.withStrasse(new StrasseBuilder().withStrassenname(addressModel.getStrasse()).build());
                break;
            case ADAHSNR:
                standortBuilder.withStrasse(new StrasseBuilder().withHausnummer(addressModel.getNummer()).build());
                break;
            default:
                break;
        }

        return standortBuilder.build();
    }

    /**
     * Consolidates the {@link DecisionVO} objekts, that only one {@link MeldungsCode#ZWA} or one {@link
     * MeldungsCode#NAT} is in the Collection. All {@link MeldungsCode#ZWA} which are not used for the {@link
     * DecisionAttribute#KUNDENWUNSCHTERMIN} will be removed.
     *
     * @param decisionVOs an Collection of {@link DecisionVO}s
     * @return a consolidated Collection of {@link DecisionVO}s
     */
    public static Collection<DecisionVO> consolidateZwaAndNatMeldungsCodes(@NotNull Collection<DecisionVO> decisionVOs) {
        List<DecisionVO> result = new ArrayList<>();
        for (DecisionVO decisionVO : decisionVOs) {
            updateFinalResult(decisionVO, decisionVO.getFinalResult());
            // invalid MC will be ignored
            if (!MeldungsCode.ZWA.equals(decisionVO.getFinalMeldungsCode())
                    && !MeldungsCode.NAT.equals(decisionVO.getFinalMeldungsCode())) {
                result.add(decisionVO);
            }
            else if (DecisionAttribute.KUNDENWUNSCHTERMIN.equals(decisionVO.getAttribute())) {
                result.add(decisionVO);
            }
        }
        return result;
    }

    /**
     * updates the {@link DecisionVO} due to the chosen decision.
     *
     * @param decisionVO     {@link DecisionVO} which should be updated
     * @param decisionResult <ul> <li>{@link DecisionResult#OK} => {@link DecisionVO#propertyValue} will be set</li>
     *                       <li>{@link DecisionResult#NICHT_OK} => {@link DecisionVO#controlValue} will be set</li>
     *                       <li>{@link DecisionResult#NICHT_OK} && {@link DecisionVO#finalResult} == {@link
     *                       MeldungsCode#RNG} => {@link DecisionVO#propertyValue} will be set</li> <li>{@link
     *                       DecisionResult#MANUELL} => no value will be set</li> </ul>
     */
    public static void updateFinalResult(DecisionVO decisionVO, DecisionResult decisionResult) {
        decisionVO.setFinalResult(decisionResult);
        if (decisionResult.equals(DecisionResult.OK)) {
            decisionVO.setFinalValue(decisionVO.getPropertyValue());
        }
        /***
         * this is necessary, to give the right values back at
         * {@link #extractMeldungPositionAbbruchmeldung(java.util.Collection, java.util.Set)}
         */
        else if (decisionResult.equals(DecisionResult.NICHT_OK)
                && MeldungsCode.RNG.equals(decisionVO.getFinalMeldungsCode())) {
            decisionVO.setFinalValue(decisionVO.getPropertyValue());
        }
        else if (DecisionAttribute.PORTIERUNGSZEITFENSTER == decisionVO.getAttribute()) {
            decisionVO.setFinalValue(decisionVO.getPropertyValue());
        }
        else if (decisionResult.equals(DecisionResult.NICHT_OK) || decisionResult.equals(DecisionResult.ABWEICHEND)) {
            decisionVO.setFinalValue(decisionVO.getControlValue());
        }
    }

    /**
     * @return the first {@link DecisionVO} which represents the {@link DecisionAttribute#KUNDENWUNSCHTERMIN}
     */
    public static DecisionVO findKundenwunschterminVo(@NotNull Collection<DecisionVO> decisionVOs) {
        for (DecisionVO vo : decisionVOs) {
            if (DecisionAttribute.KUNDENWUNSCHTERMIN.equals(vo.getAttribute())) {
                return vo;
            }
        }
        return null;
    }

    /**
     * @return checks if {@link DecisionVO} is in the collection, which represents the {@link
     * DecisionAttribute#ALLE_RUFNUMMERN_PORTIEREN}
     */
    public static boolean isAlleRufnummernPortieren(@NotNull Collection<DecisionVO> decisionVOs) {
        for (DecisionVO decisionVO : decisionVOs) {
            if (DecisionAttribute.ALLE_RUFNUMMERN_PORTIEREN.equals(decisionVO.getAttribute())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Extracts of a {@link DecisionVO} collection all active Einzelrufnummern objects as a new collection of {@link
     * de.mnet.wbci.model.MeldungPositionAbbmRufnummer}s. This method must be called, before all {@link
     * de.mnet.wbci.model.MeldungsCode#ZWA} will be removed.
     * <p/>
     * The Rufnummern objects will be selected in consideration of the WBCI FAQs Nr. 26 & Nr.27. Furthermore the
     * collection only contains Einzelrufnummern, no Anlangenanschluss-Rufnummern, if there is at least one matching
     * number. Else a empty Set will be returned. For details take look at JIRA-Ticket WITA-1711.
     *
     * @param decisionVOs decision data
     * @return a {@link Collection} of {@link RufnummernportierungVO}s
     */
    public static Set<MeldungPositionAbbmRufnummer> extractMeldungPositionAbbmRufnummern(
            final @NotNull Collection<DecisionVO> decisionVOs) {
        Set<MeldungPositionAbbmRufnummer> result = new HashSet<>();
        if (containsMatchingEinzelrufnummer(decisionVOs)) {
            for (Rufnummer rufnummer : extractActiveRufnummern(decisionVOs, true)) {
                result.add(new MeldungPositionAbbmRufnummerBuilder()
                        .withRufnummer(rufnummer.getOnKz() + rufnummer.getDnBase())
                        .build());
            }
        }
        return result;
    }

    private static boolean containsMatchingEinzelrufnummer(Collection<DecisionVO> decisionVOs) {
        for (DecisionVO vo : decisionVOs) {
            if (DecisionAttribute.RUFNUMMER.equals(vo.getAttribute())
                    && vo.getFinalResult().equals(DecisionResult.OK)
                    && vo.getControlValue().equals(vo.getPropertyValue())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Extracts of a {@link DecisionVO} collection all active Rufnummernportierung objects as a new collection of {@link
     * RufnummernportierungVO}s. The valid Rufnummernportierung objects will be selected in consideration of the WBCI
     * FAQs Nr. 26.
     *
     * @param decisionVOs decision data
     * @return a {@link Collection} of {@link RufnummernportierungVO}s
     */
    public static Collection<RufnummernportierungVO> extractActiveRufnummernportierung(
            Collection<DecisionVO> decisionVOs) {
        List<RufnummernportierungVO> result = new ArrayList<>();
        for (Rufnummer rufnummer : extractActiveRufnummern(decisionVOs, false)) {
            RufnummernportierungVO rpVO = new RufnummernportierungVO();
            rpVO.setOnkz(rufnummer.getOnKz());
            rpVO.setDnBase(rufnummer.getDnBase());
            rpVO.setDirectDial(rufnummer.getDirectDial());
            rpVO.setBlockFrom(rufnummer.getRangeFrom());
            rpVO.setBlockTo(rufnummer.getRangeTo());
            rpVO.setPkiAbg(rufnummer.getActCarrierPortKennung());
            rpVO.setPkiAuf(rufnummer.getFutureCarrierPortKennung());
            result.add(rpVO);
        }
        return result;
    }

    private static Set<Rufnummer> extractActiveRufnummern(Collection<DecisionVO> decisionVOs, boolean addAllTaifunDns) {
        Set<Rufnummer> result = new HashSet<>();

        final boolean alleRufnummernPortieren = isAlleRufnummernPortieren(decisionVOs);
        for (DecisionVO decisionVO : decisionVOs) {
            if ((DecisionAttribute.RUFNUMMER.equals(decisionVO.getAttribute()) || DecisionAttribute.RUFNUMMERN_BLOCK
                    .equals(decisionVO.getAttribute()))
                    &&
                    (addAllTaifunDns || !DecisionResult.INFO.equals(decisionVO.getSuggestedResult()))) {

                final Rufnummer rufnummer = (Rufnummer) decisionVO.getControlObject();
                if (rufnummer != null && (alleRufnummernPortieren || MeldungsCode.ZWA.equals(decisionVO.getFinalMeldungsCode()))) {
                    result.add(rufnummer);
                }
            }
        }
        return result;
    }

    /**
     * Creates the default {@link DecisionVO} objects for the {@link AbbruchmeldungTerminverschiebung}.
     *
     * @param terminverschiebungsAnfrage valid {@link TerminverschiebungsAnfrage} with attached {@link
     *                                   WbciGeschaeftsfall}.
     * @return a {@link DecisionVO} object for AbbmTvDialog
     */
    public static List<DecisionVO> createAbbmTvDecisionVo(TerminverschiebungsAnfrage terminverschiebungsAnfrage) {
        String dateValueControl = terminverschiebungsAnfrage.getTvTermin().format(DateTimeFormatter.ofPattern(DateTools.PATTERN_DAY_MONTH_YEAR));
        String dateValueProp = terminverschiebungsAnfrage.getWbciGeschaeftsfall().getWechseltermin().format(DateTimeFormatter.ofPattern(DateTools.PATTERN_DAY_MONTH_YEAR));

        return Arrays.asList(new DecisionVOBuilder(DecisionAttribute.KUNDENWUNSCHTERMIN)
                .withPropertyValue(dateValueProp)
                .withControlValue(dateValueControl)
                .withSuggestedResult(DecisionResult.NICHT_OK)
                .withFinalMeldungsCode(MeldungsCode.TV_ABG)
                .withFinalResult(DecisionResult.NICHT_OK)
                .build());
    }

    /**
     * Creates the default {@link DecisionVO} objects for the {@link AbbruchmeldungStornoAuf} or {@link
     * AbbruchmeldungStornoAen}.
     *
     * @return a {@link DecisionVO} object for AbbmStornoDialog
     */
    public static List<DecisionVO> createAbbmStornoDecisionVo() {
        return Arrays.asList(
                new DecisionVOBuilder(null)
                        .withSuggestedResult(DecisionResult.NICHT_OK)
                        .withFinalMeldungsCode(MeldungsCode.STORNO_ABG)
                        .withFinalResult(DecisionResult.NICHT_OK)
                        .build()
        );
    }

    /**
     * Creates a default {@link de.mnet.wbci.model.builder.AbbruchmeldungBuilder} based on the assigned {@link
     * de.mnet.wbci.model.DecisionVO}s.
     *
     * @param decisionVOs          Collection of {@link de.mnet.wbci.model.DecisionVO}s
     * @param reason               possible additional reason for the ABBM
     * @param wbciGeschaeftsfall   the original {@link de.mnet.wbci.model.WbciGeschaeftsfall}
     * @param activeAbbmRufnummern all active Rufnummern, which should be considered in an ABBM.
     * @return {@link de.mnet.wbci.model.builder.AbbruchmeldungBuilder}.
     */
    public static AbbruchmeldungBuilder createAbbmBuilderFromDecisionVo(
            @NotNull Collection<DecisionVO> decisionVOs, @Nullable String reason,
            WbciGeschaeftsfall wbciGeschaeftsfall,
            Set<MeldungPositionAbbmRufnummer> activeAbbmRufnummern) {

        AbbruchmeldungBuilder abbruchmeldungBuilder = new AbbruchmeldungBuilder()
                .withAbsender(CarrierCode.MNET)
                .withWbciGeschaeftsfall(wbciGeschaeftsfall)
                .withIoType(IOType.OUT);

        abbruchmeldungBuilder.addMeldungPositionen(
                DecisionVOHelper.extractMeldungPositionAbbruchmeldung(decisionVOs, activeAbbmRufnummern));

        /**
         * if a special reason should mentioned or a predefined Meldungcode won't matched for the ABBM, the user can
         * insert this in the tfSonstiges field. If this happens, a {@link de.mnet.wbci.model.MeldungsCode#SONST} will
         * be added and the field {@link de.mnet.wbci.model.Abbruchmeldung#begruendung} will be set.
         */
        if (isNotBlank(reason)) {
            abbruchmeldungBuilder.withBegruendung(reason);
        }
        return abbruchmeldungBuilder;
    }

    /**
     * Creates the default {@link de.mnet.wbci.model.DecisionVO} list for the { {@link
     * de.mnet.wbci.model.AbbruchmeldungTechnRessource} .
     *
     * @return a {@link DecisionVO} object for the AbbmTrDialog
     */
    public static List<DecisionVO> createAbbmTrDecisionVo() {
        return Arrays.asList(new DecisionVOBuilder(DecisionAttribute.TECHNOLOGIE)
                .withSuggestedResult(DecisionResult.NICHT_OK)
                .withFinalMeldungsCode(MeldungsCode.UETN_NM)
                .withFinalResult(DecisionResult.NICHT_OK)
                .build());
    }

    /**
     * Extracts from an Collection of {@link DecisionVO}s a valid Set of {@link MeldungPositionAbbruchmeldungTechnRessource}.
     *
     * @param decisionVOs an Collection of {@link DecisionVO}s
     * @return a Set of {@link MeldungPositionAbbruchmeldungTechnRessource}.
     */
    public static Set<MeldungPositionAbbruchmeldungTechnRessource> extractMeldungPositionAbbmTr(
            @NotNull Collection<DecisionVO> decisionVOs) {
        Set<MeldungPositionAbbruchmeldungTechnRessource> result = new HashSet<>();
        for (DecisionVO vo : decisionVOs) {
            if (vo.getFinalMeldungsCode().isValidForMeldungPosTyps(MeldungPositionTyp.ABBM_TR)) {
                result.add(new MeldungPositionAbbruchmeldungTechnRessourceBuilder()
                        .withMeldungsCode(vo.getFinalMeldungsCode())
                        .withMeldungsText(vo.getFinalMeldungsCode().getStandardText())
                        .build());
            }
        }
        return result;
    }

    /**
     * Updates a collection of {@link RufnummerPortierungSelection} according to the assigned {@link DecisionVO}s. If
     * some Rufnummer have NOT the {@link DecisionResult#OK} or couldn't found in the rufnummer portierung collection,
     * an {@link InvalidRufnummerPortierungException} will be thrown.
     *
     * @param rufnummerPortierungSelections a prefilled collection of {@link RufnummerPortierungSelection}
     * @param decisionVOs                   with decision attributes of typ {@link DecisionAttribute#RUFNUMMER} or
     *                                      {@link DecisionAttribute#RUFNUMMERN_BLOCK}.
     * @param wbciVorabstimmungsId          the VorabstimmungsId for a possible exception
     * @return collection with selected rufnummern.
     */
    public static Collection<RufnummerPortierungSelection> updateRufnummerPortierungSelection(
            Collection<RufnummerPortierungSelection> rufnummerPortierungSelections, List<DecisionVO> decisionVOs,
            String wbciVorabstimmungsId) {

        if (CollectionUtils.isNotEmpty(decisionVOs) && CollectionUtils.isNotEmpty(rufnummerPortierungSelections)) {
            for (DecisionVO vo : decisionVOs) {
                if (DecisionAttribute.RUFNUMMER.equals(vo.getAttribute())
                        || DecisionAttribute.RUFNUMMERN_BLOCK.equals(vo.getAttribute())) {

                    // if the Rufnummer of the VA is also in the Portierungselection
                    if (vo.getFinalResult().equals(DecisionResult.OK)
                            && vo.getControlObject() instanceof Rufnummer) {
                        // if the rufnummerPortierungSelection couldn't be updated, a new exception will be thrown.
                        if (!updateSelection(rufnummerPortierungSelections, (Rufnummer) vo.getControlObject(), true)) {
                            throw new InvalidRufnummerPortierungException(wbciVorabstimmungsId);
                        }
                    }
                    // if an Rufnummer of the VA is not included in the Portierungselection
                    else if (vo.getFinalMeldungsCode().equals(MeldungsCode.RNG)) {
                        throw new InvalidRufnummerPortierungException(wbciVorabstimmungsId);
                    }
                }
            }
            return rufnummerPortierungSelections;
        }
        throw new InvalidRufnummerPortierungException(wbciVorabstimmungsId);
    }

    private static boolean updateSelection(Collection<RufnummerPortierungSelection> rufnummerPortierungSelections,
            Rufnummer rufnummer, boolean selected) {
        for (RufnummerPortierungSelection rps : rufnummerPortierungSelections) {
            if (rps.getRufnummer().isRufnummerEqual(rufnummer)) {
                rps.setSelected(selected);
                return true;
            }
        }
        return false;
    }

    /**
     * Checks whether a 'Portierungszeitfenster ZF3' DecisionVO is present within the provided list of DecisionVOs.
     *
     * @param decisionVOs
     * @return
     */
    public static boolean containsPortierungszeitfenserZF3(List<DecisionVO> decisionVOs) {
        for (DecisionVO decisionVO : decisionVOs) {
            if (DecisionAttribute.PORTIERUNGSZEITFENSTER == decisionVO.getAttribute()
                    && Portierungszeitfenster.ZF3.name().equals(decisionVO.getPropertyValue())) {
                return true;
            }
        }
        return false;
    }

}
