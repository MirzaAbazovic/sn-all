/*
 * Copyright (c) 2016 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 06.05.2016
 */
package de.mnet.wita.unmarshal.v2;

import java.util.*;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v2.AngabenZurLeitungType;
import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v2.AnlageMitTypType;
import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v2.AnschlussType;
import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v2.MeldungsattributeAKMPVType;
import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v2.MeldungsattributeTALAKMPVType;
import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v2.MeldungstypAKMPVType;
import de.mnet.wita.message.meldung.AnkuendigungsMeldungPv;
import de.mnet.wita.message.meldung.position.Leitung;

@SuppressWarnings("Duplicates")
@Component
public class AkmPvUnmarshallerV2 extends AbstractUnmarshallerFunctionV2<MeldungstypAKMPVType> {

    @Override
    public AnkuendigungsMeldungPv unmarshal(MeldungstypAKMPVType in) {
        AnkuendigungsMeldungPv out = new AnkuendigungsMeldungPv();

        mapMeldungAttributes(in, out);
        mapMeldungPositions(in, out);

        return out;
    }

    private void mapMeldungAttributes(MeldungstypAKMPVType in, AnkuendigungsMeldungPv out) {
        MeldungstypAKMPVType.Meldungsattribute inMeldungAttributes = in.getMeldungsattribute();

        out.setKundenNummer(inMeldungAttributes.getKundennummer());
        out.setVertragsNummer(inMeldungAttributes.getVertragsnummer());

        out.setKundenNummer(inMeldungAttributes.getKundennummer());
        out.setVertragsNummer(inMeldungAttributes.getVertragsnummer());
        out.setLeitung(unmarshalLeitung(inMeldungAttributes.getTAL()));
        out.setAufnehmenderProvider(unmarshalAufnehmenderProvider(inMeldungAttributes.getAufnehmenderProvider()));
        out.setEndkunde(unmarshalStandortPerson(inMeldungAttributes.getEndkunde()));

        mapTAL(inMeldungAttributes.getTAL(), out);

        out.getAnlagen().addAll(unmarshalAnlagen(getAnlagen(inMeldungAttributes.getAnlagen())));

    }

    private void mapMeldungPositions(MeldungstypAKMPVType in, AnkuendigungsMeldungPv out) {
        out.getMeldungsPositionen().addAll(unmarshalMeldungspositionen(in.getMeldungspositionen().getPosition()));
    }

    private Leitung unmarshalLeitung(MeldungsattributeTALAKMPVType inTal) {
        if (inTal == null || inTal.getLeitung() == null) {
            return null;
        }

        AngabenZurLeitungType inLeitung = inTal.getLeitung();
        Leitung outLeitung = new Leitung();
        outLeitung.setLeitungsBezeichnung(unmarshalLeitungsbezeichnung(inLeitung.getLeitungsbezeichnung()));
        outLeitung.setSchleifenWiderstand(inLeitung.getSchleifenwiderstand());
        return outLeitung;
    }

    private void mapTAL(MeldungsattributeTALAKMPVType in, AnkuendigungsMeldungPv out) {

        if (in != null) {

            if (StringUtils.isNotBlank(in.getVorabstimmungsID())) {
                out.setVorabstimmungsId(in.getVorabstimmungsID());
            }

            AnschlussType anschluss = in.getAnschluss();
            if (anschluss != null) {
                out.setAnschlussOnkz(anschluss.getONKZ());
                out.setAnschlussRufnummer(anschluss.getRufnummer());
            }

            // WITAX: rnrPortierung im MeldungsattributeTALAKMPVType wurde in WITA 9 entfernt
            out.setRufnummernPortierung(null);
        }
    }

    private List<AnlageMitTypType> getAnlagen(MeldungsattributeAKMPVType.Anlagen anlagen) {
        if (anlagen != null) {
            return anlagen.getAnlage();
        }
        return null;
    }
}
