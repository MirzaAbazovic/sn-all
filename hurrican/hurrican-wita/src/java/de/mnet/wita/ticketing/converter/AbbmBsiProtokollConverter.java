/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 06.02.2012 13:20:53
 */
package de.mnet.wita.ticketing.converter;

import java.text.*;
import com.google.common.base.Preconditions;

import de.mnet.esb.cdm.customer.customerservice.v1.AddCommunication;
import de.mnet.wita.message.meldung.AbbruchMeldung;

/**
 * konvertiert eine ABBM in einen BSI-Protokolleintrag
 */
public class AbbmBsiProtokollConverter extends BaseMeldungBsiProtokollConverter<AbbruchMeldung> {

    private static final String STANDARD_ABBRUCH = String.format(
            "Abbruchmeldung empfangen %s - Auftrag wird abgebrochen - Gründe: {2}", WITA_REFERENZ_NR_TEMPLATE);
    private static final String TV_ABBRUCH = String.format(
            "Abbruchmeldung empfangen %s - Terminverschiebung wurde abgelehnt - Gründe: {2}",
            WITA_REFERENZ_NR_TEMPLATE);
    private static final String STORNO_ABBRUCH = String.format(
            "Abbruchmeldung empfangen %s - Stornierung wurde abgelehnt - Gründe: {2}", WITA_REFERENZ_NR_TEMPLATE);

    @Override
    public AddCommunication apply(AbbruchMeldung abbm) {
        Preconditions.checkNotNull(abbm);
        AddCommunication protokollEintrag = createMeldungProtokollEintrag(abbm);

        String useTemplate;
        switch (abbm.getAenderungsKennzeichen()) {
            case TERMINVERSCHIEBUNG:
                useTemplate = TV_ABBRUCH;
                break;
            case STORNO:
                useTemplate = STORNO_ABBRUCH;
                break;
            default:
                useTemplate = STANDARD_ABBRUCH;
                break;
        }

        protokollEintrag.setNotes(MessageFormat.format(useTemplate,
                getAnbieterwechsel46TKG(abbm.getExterneAuftragsnummer()),
                abbm.getExterneAuftragsnummer(),
                formatMeldungspositionen(abbm.getMeldungsPositionen())));
        return protokollEintrag;
    }

}
