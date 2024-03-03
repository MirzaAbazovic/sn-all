/*
 * Copyright (c) 2007 - M-net Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 27.07.2007 08:00:47
 */
package de.augustakom.hurrican.service.cc.impl.command.tal;

import java.util.*;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import de.augustakom.common.tools.lang.StringTools;
import de.augustakom.hurrican.model.cc.tal.CBVorgang;
import de.augustakom.hurrican.model.exmodules.tal.TALSegment;
import de.augustakom.hurrican.service.base.exceptions.HurricanServiceCommandException;


@Deprecated
/**
 * Klasse ist zu entfernen, da nur fuer ESAA-Schnittstelle relevant, die mittlerweile deaktiviert ist!
 *
 * Erstellt das Segment B017 Sonstiges fuer die el TAL-Schnittstelle
 *
 */
public class GetDataB017Command extends AbstractTALDataCommand {

    private static final Logger LOGGER = Logger.getLogger(GetDataB017Command.class);

    /**
     * @see de.augustakom.hurrican.service.base.impl.AbstractHurricanServiceCommand#execute()
     */
    @Override
    public Object execute() throws Exception {
        try {
            CBVorgang cbVorgang = getCBVorgang();
            TALSegment segment = new TALSegment();
            List<TALSegment> result = new ArrayList<TALSegment>();

            if (StringUtils.isNotBlank(cbVorgang.getMontagehinweis())) {
                segment.setSegmentName(TALSegment.SEGMENT_NAME_B017); // Bemerkung Mnet
                verifyMandatory(
                        StringTools.replaceChars(cbVorgang.getMontagehinweis(), invalidITEXChars),
                        segment, "B017_2: Bemerkung ist leer");

                result.add(segment);
                return result;
            }
            else {
                return null;
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new HurricanServiceCommandException("Fehler beim ermitteln von Segment B017: " + e.getMessage(), e);
        }
    }
}


