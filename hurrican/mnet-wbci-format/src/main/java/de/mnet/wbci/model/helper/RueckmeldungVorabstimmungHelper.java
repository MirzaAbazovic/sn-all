/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 27.08.13
 */
package de.mnet.wbci.model.helper;

import java.util.*;
import org.apache.commons.lang.StringUtils;

import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.common.tools.lang.StringTools;
import de.mnet.wbci.model.MeldungPositionRueckmeldungVa;
import de.mnet.wbci.model.RueckmeldungVorabstimmung;
import de.mnet.wbci.model.Standort;
import de.mnet.wbci.model.TechnischeRessource;

/**
 * Helper class for the {@link de.mnet.wbci.model.RueckmeldungVorabstimmung}
 */
public final class RueckmeldungVorabstimmungHelper {

    private RueckmeldungVorabstimmungHelper() {
    }

    /**
     * Checks if the RUEM-VA contains the assigned WBCI Line-IDs
     *
     * @return true or false
     */
    public static boolean isLineIdPresent(RueckmeldungVorabstimmung ruemVa, String lineId) {
        if (ruemVa.getTechnischeRessourcen() != null) {
            for (TechnischeRessource tr : ruemVa.getTechnischeRessourcen()) {
                if (lineId.equals(tr.getLineId())) {
                    return true;
                }
            }
        }
        return false;
    }


    /**
     * Checks if the RUEM-VA contains the assigned WITA-Vertragsnummer
     *
     * @return true or false
     */
    public static boolean isWitaVertragsnummerPresent(RueckmeldungVorabstimmung ruemVa, String vertragsnummer) {
        if (ruemVa.getTechnischeRessourcen() != null) {
            for (TechnischeRessource tr : ruemVa.getTechnischeRessourcen()) {
                if (vertragsnummer.equals(tr.getVertragsnummer())) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Creates a formatted String form the techn. Ressourcen with all WITA Vertragsnummern and WBCI LineIDs.
     *
     * @return {@link java.lang.String}
     */
    public static String extractWitaVtrNrsAndLineIds(RueckmeldungVorabstimmung ruemVa) {
        StringBuilder result = new StringBuilder();

        String witaVtrNrs = extractVtrNrsFromTechnischeRessourcen(ruemVa);
        if (StringUtils.isNotEmpty(witaVtrNrs)) {
            result.append(String.format("WITA Vertragsnummern:%n%s", witaVtrNrs));
        }

        String lineIds = extractLineIdsFromTechnischeRessourcen(ruemVa);
        if (StringUtils.isNotEmpty(lineIds)) {
            if (result.length() > 0) {
                result.append(System.lineSeparator());
            }
            result.append(String.format("WBCI Line-IDs:%n%s", lineIds));
        }
        return result.toString();
    }

    /**
     * Creates a {@link java.util.List} of Strings form the techn. Ressourcen with all WITA Vertragsnummern and WBCI
     * LineIDs.
     *
     * @return {@link java.util.List} of {@link java.lang.String}s
     */
    public static List<String> getWitaVtrNrsAndLineIds(RueckmeldungVorabstimmung ruemVa) {
        List<String> result = new ArrayList<>();
        if (ruemVa.getTechnischeRessourcen() != null) {
            for (TechnischeRessource techRes : ruemVa.getTechnischeRessourcen()) {
                CollectionTools.addIfNotBlank(result, techRes.getLineId());
                CollectionTools.addIfNotBlank(result, techRes.getVertragsnummer());
            }
        }
        Collections.sort(result);
        return result;
    }

    /**
     * Creates a {@link java.util.List} of Strings form the techn. Ressourcen with all WITA Vertragsnummern.
     *
     * @return {@link java.util.List} of {@link java.lang.String}s
     */
    public static List<String> getWitaVtrNrs(RueckmeldungVorabstimmung ruemVa) {
        List<String> result = new ArrayList<>();
        if (ruemVa.getTechnischeRessourcen() != null) {
            for (TechnischeRessource techRes : ruemVa.getTechnischeRessourcen()) {
                CollectionTools.addIfNotBlank(result, techRes.getVertragsnummer());
            }
        }
        Collections.sort(result);
        return result;
    }

    private static String extractVtrNrsFromTechnischeRessourcen(RueckmeldungVorabstimmung ruemVa) {
        StringBuilder vtrNrs = new StringBuilder();
        if (ruemVa.getTechnischeRessourcen() != null) {
            for (TechnischeRessource techRes : ruemVa.getTechnischeRessourcen()) {
                if (techRes.getVertragsnummer() != null) {
                    if (vtrNrs.length() > 0) {
                        vtrNrs.append(System.lineSeparator());
                    }
                    vtrNrs.append(techRes.getVertragsnummer());
                }
            }
        }
        return vtrNrs.toString();
    }

    private static String extractLineIdsFromTechnischeRessourcen(RueckmeldungVorabstimmung ruemVa) {
        StringBuilder lineIds = new StringBuilder();
        if (ruemVa.getTechnischeRessourcen() != null) {
            for (TechnischeRessource techRes : ruemVa.getTechnischeRessourcen()) {
                if (techRes.getLineId() != null) {
                    if (lineIds.length() > 0) {
                        lineIds.append(System.lineSeparator());
                    }
                    lineIds.append(techRes.getLineId());
                }
            }
        }
        return lineIds.toString();
    }


    /**
     * Creates a formatted String form the ADA response codes.
     *
     * @return {@link java.lang.String}
     */
    public static String extractAdaInfos(RueckmeldungVorabstimmung ruemVa) {
        StringBuilder result = new StringBuilder();

        for (MeldungPositionRueckmeldungVa meldungPos : ruemVa.getMeldungsPositionen()) {
            if (meldungPos.getMeldungsCode().isADACode() && meldungPos.getStandortAbweichend() != null) {
                String responseValue = null;
                Standort adaStandort = meldungPos.getStandortAbweichend();

                switch (meldungPos.getMeldungsCode()) {
                    case ADAPLZ:
                        responseValue = adaStandort.getPostleitzahl();
                        break;
                    case ADAORT:
                        responseValue = adaStandort.getOrt();
                        break;
                    case ADASTR:
                        responseValue = (adaStandort.getStrasse() != null)
                                ? adaStandort.getStrasse().getStrassenname() : null;
                        break;
                    case ADAHSNR:
                        responseValue = (adaStandort.getStrasse() != null)
                                ? StringTools.join(new String[] {
                                    adaStandort.getStrasse().getHausnummer(),
                                    adaStandort.getStrasse().getHausnummernZusatz() },
                                    " ", true)
                                : null;
                        break;
                    default:
                        break;
                }

                result.append(String.format("%s: %s%n",
                        meldungPos.getMeldungsCode().name(),
                        responseValue));
            }
        }

        return result.toString();
    }

}
