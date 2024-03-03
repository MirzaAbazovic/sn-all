/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 13.11.13
 */
package de.mnet.wbci.model;

import java.util.*;
import javax.validation.constraints.*;

import de.mnet.wbci.model.builder.RufnummerOnkzBuilder;
import de.mnet.wbci.model.builder.RufnummernblockBuilder;
import de.mnet.wbci.model.builder.RufnummernportierungAnlageBuilder;
import de.mnet.wbci.model.builder.RufnummernportierungEinzelnBuilder;

/**
 * Helper class for the {@link de.mnet.wbci.model.RufnummernportierungVO} objects.
 */
public class RufnummernportierungVOHelper {

    /**
     * converter for all Rufnummern with the type {@link Rufnummernportierung}.
     *
     * @param rufnummernportierungVOs    VO objects of the final data
     * @param originalWbciGeschaeftsfall original {@link WbciGeschaeftsfall} from the incoming {@link
     *                                   VorabstimmungsAnfrage}.
     * @return for the outgoing {@link RueckmeldungVorabstimmung} a filled:
     * <p/>
     * - a filled {@link RufnummernportierungAnlage} for Block-Rufnummern <br/> - a filled {@link
     * RufnummernportierungEinzeln} for Einzelrufnummern.
     */
    public static Rufnummernportierung convertToRuemVaRufnummerportierung(
            @NotNull Collection<RufnummernportierungVO> rufnummernportierungVOs,
            @NotNull RufnummernportierungAware originalWbciGeschaeftsfall) {

        Rufnummernportierung rufnummernportierung = originalWbciGeschaeftsfall.getRufnummernportierung();

        if (rufnummernportierung instanceof RufnummernportierungEinzeln) {
            return convertToRufnummerportierungEinzel(rufnummernportierungVOs,
                    (RufnummernportierungEinzeln) rufnummernportierung);
        }
        else if (rufnummernportierung instanceof RufnummernportierungAnlage) {
            return convertToRufnummerportierungAnlage(rufnummernportierungVOs,
                    (RufnummernportierungAnlage) rufnummernportierung);
        }
        return null;
    }

    /**
     * specific implementation for the {@link RufnummernportierungEinzeln} objects of the method {@link
     * #convertToRuemVaRufnummerportierung(java.util.Collection, RufnummernportierungAware)}
     */
    private static RufnummernportierungEinzeln convertToRufnummerportierungEinzel(
            @NotNull Collection<RufnummernportierungVO> rufnummernportierungVOs,
            @NotNull RufnummernportierungEinzeln originalRufnummernportierungEinzeln) {

        RufnummernportierungEinzelnBuilder rnpBuilder = new RufnummernportierungEinzelnBuilder()
                .withAlleRufnummernPortieren(originalRufnummernportierungEinzeln.getAlleRufnummernPortieren());

        rnpBuilder.withPortierungszeitfenster(originalRufnummernportierungEinzeln.getPortierungszeitfenster())
                .withPortierungskennungPKIauf(originalRufnummernportierungEinzeln.getPortierungskennungPKIauf());

        for (RufnummernportierungVO rufnummernportierungVO : rufnummernportierungVOs) {
            if (!rufnummernportierungVO.isBlock()) {
                rnpBuilder.addRufnummer(new RufnummerOnkzBuilder()
                        .withRufnummer(rufnummernportierungVO.getDnBase())
                        .withOnkz(rufnummernportierungVO.getOnkz())
                        .withPortierungKennungPKIabg(rufnummernportierungVO.getPkiAbg())
                        .build());
            }
        }

        return rnpBuilder.build();
    }


    /**
     * specific implementation for the {@link RufnummernportierungAnlage} objects of the method {@link
     * #convertToRuemVaRufnummerportierung(java.util.Collection, RufnummernportierungAware)}
     */
    private static RufnummernportierungAnlage convertToRufnummerportierungAnlage(
            @NotNull Collection<RufnummernportierungVO> rufnummernportierungVOs,
            @NotNull RufnummernportierungAnlage originalRufnummernportierungAnlage) {

        RufnummernportierungAnlageBuilder rnpBuilder = new RufnummernportierungAnlageBuilder();

        rnpBuilder.withPortierungszeitfenster(originalRufnummernportierungAnlage.getPortierungszeitfenster())
                .withPortierungskennungPKIauf(originalRufnummernportierungAnlage.getPortierungskennungPKIauf());

        for (RufnummernportierungVO rufnummernportierungVO : rufnummernportierungVOs) {
            if (rufnummernportierungVO.isBlock()) {
                rnpBuilder.withOnkz(rufnummernportierungVO.getOnkz())
                        .withDurchwahlnummer(rufnummernportierungVO.getDnBase())
                        .withAbfragestelle(rufnummernportierungVO.getDirectDial())
                        .addRufnummernblock(new RufnummernblockBuilder()
                                .withRnrBlockVon(rufnummernportierungVO.getBlockFrom())
                                .withRnrBlockBis(rufnummernportierungVO.getBlockTo())
                                .withPkiAbg(rufnummernportierungVO.getPkiAbg())
                                .build());
            }
        }

        return rnpBuilder.build();
    }
}
