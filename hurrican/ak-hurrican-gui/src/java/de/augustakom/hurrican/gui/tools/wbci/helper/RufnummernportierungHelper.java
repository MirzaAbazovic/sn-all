/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 06.03.14
 */
package de.augustakom.hurrican.gui.tools.wbci.helper;

import java.util.*;
import org.apache.commons.lang.StringUtils;

import de.augustakom.common.gui.swing.wizard.AKJWizardFinishVetoException;
import de.augustakom.hurrican.model.billing.RufnummerPortierungSelection;
import de.mnet.wbci.model.Portierungszeitfenster;
import de.mnet.wbci.model.RufnummerOnkz;
import de.mnet.wbci.model.Rufnummernportierung;
import de.mnet.wbci.model.builder.RufnummerOnkzBuilder;
import de.mnet.wbci.model.builder.RufnummernblockBuilder;
import de.mnet.wbci.model.builder.RufnummernportierungAnlageBuilder;
import de.mnet.wbci.model.builder.RufnummernportierungEinzelnBuilder;

/**
 *
 */
public final class RufnummernportierungHelper {

    /**
     * Prevent instantiation.
     */
    private RufnummernportierungHelper() {
    }

    /**
     * Creates proper rufnummerportierung according to rufnummern selections and portierungszeitfenster.
     *
     * @param portierungszeitfenster
     * @param rufnummerPortierungSelections
     * @param alleRufnummernPortieren
     * @return
     * @throws AKJWizardFinishVetoException
     */
    public static Rufnummernportierung getRufnummernportierung(Portierungszeitfenster portierungszeitfenster,
            List<RufnummerPortierungSelection> rufnummerPortierungSelections,
            boolean alleRufnummernPortieren) throws AKJWizardFinishVetoException {
        if (isAnlagenanschluss(rufnummerPortierungSelections)) {
            return getAnlagenanschlussRufnummernportierung(portierungszeitfenster, rufnummerPortierungSelections);
        }
        else {
            return getEinzelanschlussRufnummernportierung(portierungszeitfenster, rufnummerPortierungSelections, alleRufnummernPortieren);
        }
    }

    /**
     * Create proper einzel rufnummer portierung.
     *
     * @param portierungszeitfenster
     * @param rufnummerPortierungSelections
     * @param alleRufnummernPortieren
     * @return
     * @throws AKJWizardFinishVetoException
     */
    private static Rufnummernportierung getEinzelanschlussRufnummernportierung(Portierungszeitfenster portierungszeitfenster,
            List<RufnummerPortierungSelection> rufnummerPortierungSelections,
            boolean alleRufnummernPortieren) {
        RufnummernportierungEinzelnBuilder builder = new RufnummernportierungEinzelnBuilder();

        builder.withAlleRufnummernPortieren(alleRufnummernPortieren);
        builder.withPortierungszeitfenster(portierungszeitfenster);

        for (RufnummerPortierungSelection rufnummerPortierungSelection : rufnummerPortierungSelections) {
            if (rufnummerPortierungSelection.getRufnummer().isBlock()) {
                throw new IllegalStateException("Mischung aus Einzelrufnummer und Rufnummernblock ist nicht erlaubt!");
            }

            RufnummerOnkz rufnummernOnkzBuilder = new RufnummerOnkzBuilder()
                    .withOnkz(rufnummerPortierungSelection.getRufnummer().getOnKz())
                    .withRufnummer(rufnummerPortierungSelection.getRufnummer().getDnBase())
                    .build();
            builder.addRufnummer(rufnummernOnkzBuilder);
        }

        return builder.build();
    }

    /**
     * Create proper anlagenanschluss rufnummer portierung.
     *
     * @param portierungszeitfenster
     * @param rufnummerPortierungSelections
     * @return
     * @throws AKJWizardFinishVetoException
     */
    private static Rufnummernportierung getAnlagenanschlussRufnummernportierung(Portierungszeitfenster portierungszeitfenster,
            List<RufnummerPortierungSelection> rufnummerPortierungSelections) {
        RufnummernportierungAnlageBuilder builder = new RufnummernportierungAnlageBuilder();

        builder.withPortierungszeitfenster(portierungszeitfenster);

        String onkz = null;
        String dnBase = null;
        String durchwahl = null;
        String fehlerText = "Mindestens ein Rufnummernblock hat eine abweichende %s";
        for (RufnummerPortierungSelection rufnummerPortierungSelection : rufnummerPortierungSelections) {
            if (!rufnummerPortierungSelection.getRufnummer().isBlock()) {
                throw new IllegalStateException("Mischung aus Einzelrufnummer und Rufnummernblock ist nicht erlaubt!");
            }

            // these attributes only need to be set once, since these values should be the same for each table entry!
            if (StringUtils.isBlank(onkz)) {
                onkz = rufnummerPortierungSelection.getRufnummer().getOnKz();
                builder.withOnkz(onkz);
                dnBase = rufnummerPortierungSelection.getRufnummer().getDnBase();
                builder.withDurchwahlnummer(dnBase);
                durchwahl = rufnummerPortierungSelection.getRufnummer().getDirectDial();
                builder.withAbfragestelle(durchwahl);
            }
            else {
                // check that next block to add has same onkz, dnBase and direct dial as specified before
                if (!onkz.equals(rufnummerPortierungSelection.getRufnummer().getOnKz())) {
                    throw new IllegalStateException(String.format(fehlerText, "Onkz"));
                }
                else if (!dnBase.equals(rufnummerPortierungSelection.getRufnummer().getDnBase())) {
                    throw new IllegalStateException(String.format(fehlerText, "DN-Base"));
                }
                else if (durchwahl != null && !durchwahl.equals(rufnummerPortierungSelection.getRufnummer().getDirectDial())) {
                    throw new IllegalStateException(String.format(fehlerText, "Direct Dial"));
                }
            }

            builder.addRufnummernblock(
                    new RufnummernblockBuilder()
                            .withRnrBlockVon(rufnummerPortierungSelection.getRufnummer().getRangeFrom())
                            .withRnrBlockBis(rufnummerPortierungSelection.getRufnummer().getRangeTo())
                            .build()
            );

        }
        return builder.build();
    }

    /**
     * Checks to see whether the numbers selected within the table correspond to a number block (range from/to) or to
     * individual telephone numbers.
     *
     * @return true if the selected numbers correspond to a number block, otherwise false
     */
    private static boolean isAnlagenanschluss(List<RufnummerPortierungSelection> rufnummerPortierungSelections) {
        for (RufnummerPortierungSelection rufnummerPortierungSelection : rufnummerPortierungSelections) {
            if (rufnummerPortierungSelection.getRufnummer().isBlock()) {
                return true;
            }
        }
        return false;
    }
}
