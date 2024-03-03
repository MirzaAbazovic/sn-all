/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 11.12.13
 */
package de.augustakom.hurrican.gui.tools.wbci;

import java.time.*;
import java.util.*;
import org.springframework.util.CollectionUtils;

import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.hurrican.gui.tools.wbci.helper.MeldungServiceHelper;
import de.mnet.wbci.model.Antwortfrist;
import de.mnet.wbci.model.StornoAnfrage;
import de.mnet.wita.model.WitaCBVorgang;

/**
 * Dialog sending new ERLM meldung for a StronoAnfrage.
 *
 *
 */
public class ErlmStornoDialog extends AbstractErlmDialog<StornoAnfrage> {
    private static final long serialVersionUID = -1055440375056594865L;

    private static final String RESOURCE = "de/augustakom/hurrican/gui/tools/wbci/resources/ErlmStornoDialog.xml";
    private static final String AUFNEHMEND_HINWEISTEXT = "Bitte achten sie darauf, dass spätestens %s Arbeitstag(e) vor dem Wechseltermin (%s) eine neue VA vom anderen Carrier erwartet wird.";
    private static final String ABGEBEND_HINWEISTEXT = "Bitte achten Sie darauf, dass spätestens %s Arbeitstag(e) vor dem Wechseltermin (%s) eine neue VA verschickt werden sollte.";
    private static final String WITA_EXIST_FOR_VA = "Zu der WBCI Anfrage '%s' existiert ein WITA Vorgang. Bitte prüfen und ggf. stornieren.";

    public ErlmStornoDialog(StornoAnfrage stornoAnfrage, Antwortfrist antwortfrist) {
        super(RESOURCE, stornoAnfrage);
        final String formattedWT = (stornoAnfrage.getWbciGeschaeftsfall().getWechseltermin() != null)
                ? DateTools.formatDate(Date.from(stornoAnfrage.getWbciGeschaeftsfall().getWechseltermin().atStartOfDay(ZoneId.systemDefault()).toInstant()), DateTools.PATTERN_DAY_MONTH_YEAR)
                : "nicht definiert";

        switch (stornoAnfrage.getTyp()) {
            //TODO use in REL-16 correct configured antwortfristen
            case STR_AEN_ABG:
                setAdditionalInfoTxt(String.format(ABGEBEND_HINWEISTEXT, antwortfrist.getFristInTagen(), formattedWT));
                break;
            case STR_AEN_AUF:
                setAdditionalInfoTxt(String.format(AUFNEHMEND_HINWEISTEXT, antwortfrist.getFristInTagen(), formattedWT));
                break;
            case STR_AUFH_AUF:
                final List<WitaCBVorgang> witaCbVorgaenge =
                        getWbciWitaServiceFacade().findWitaCbVorgaenge(stornoAnfrage.getVorabstimmungsId());
                if (!CollectionUtils.isEmpty(witaCbVorgaenge)) {
                    setAdditionalInfoTxt(String.format(WITA_EXIST_FOR_VA, stornoAnfrage.getVorabstimmungsId()), true);
                }
                break;
            default:
                setAdditionalInfoTxt(null);
        }
    }

    @Override
    protected void onSave() {
        String vaId = wbciRequest.getVorabstimmungsId();
        String aenderungsId = wbciRequest.getAenderungsId();
        MeldungServiceHelper.createStornoErledigtmeldung(getWbciMeldungService(),
                vaId,
                aenderungsId,
                wbciRequest.getTyp());
    }

}
