/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 24.09.2014
 */
package de.mnet.wita.marshal.v2;

import java.util.*;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import de.mnet.esb.cdm.supplierpartner.lineorderservice.v2.AbgebenderProviderType;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v2.AngabenZurLeitungType;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v2.AnschlussType;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v2.BoolDecisionType;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v2.LeitungsbezeichnungType;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v2.MeldungstypRUEMPVType;
import de.mnet.wita.RuemPvAntwortCode;
import de.mnet.wita.message.common.LeitungsBezeichnung;
import de.mnet.wita.message.meldung.RueckMeldungPv;
import de.mnet.wita.message.meldung.position.AbgebenderProvider;
import de.mnet.wita.message.meldung.position.Leitung;
import de.mnet.wita.message.meldung.position.MeldungsPosition;

@SuppressWarnings("Duplicates")
@Component
public class RueckMeldungPvMarshallerV2 extends MeldungMarshallerV2<RueckMeldungPv, MeldungstypRUEMPVType> {

    @Override
    protected MeldungstypRUEMPVType createMeldungstyp(RueckMeldungPv input) {
        MeldungstypRUEMPVType ruemPv = new MeldungstypRUEMPVType();
        ruemPv.setMeldungsattribute(createMeldungsattribute(input));
        ruemPv.setMeldungspositionen(createMeldungspositionen(input));
        return ruemPv;
    }

    private MeldungstypRUEMPVType.Meldungsattribute createMeldungsattribute(RueckMeldungPv input) {

        MeldungstypRUEMPVType.Meldungsattribute.TAL talruempvType = new MeldungstypRUEMPVType.Meldungsattribute.TAL();
        talruempvType.setAnschluss(createAnschluss(input));
        talruempvType.setLeitung(createLeitung(input));
        talruempvType.setVorabstimmungsID(input.getVorabstimmungsId());

        MeldungstypRUEMPVType.Meldungsattribute meldungsattribute = new MeldungstypRUEMPVType.Meldungsattribute();
        meldungsattribute.setKundennummer(input.getKundenNummer());
        meldungsattribute.setKundennummerBesteller(input.getKundennummerBesteller());
        meldungsattribute.setVertragsnummer(input.getVertragsNummer());
        meldungsattribute.setAbgebenderProvider(createAbgebenderProvider(input));
        meldungsattribute.setTAL(talruempvType);

        return meldungsattribute;
    }

    private AnschlussType createAnschluss(RueckMeldungPv input) {
        if (StringUtils.isBlank(input.getAnschlussOnkz()) || StringUtils.isBlank(input.getAnschlussRufnummer())) {
            return null;
        }

        AnschlussType anschlussType = new AnschlussType();
        anschlussType.setONKZ(input.getAnschlussOnkz());
        anschlussType.setRufnummer(input.getAnschlussRufnummer());
        return anschlussType;
    }

    private MeldungstypRUEMPVType.Meldungspositionen createMeldungspositionen(RueckMeldungPv input) {
        MeldungstypRUEMPVType.Meldungspositionen positionen = new MeldungstypRUEMPVType.Meldungspositionen();
        Set<MeldungsPosition> meldungspositionen = input.getMeldungsPositionen();
        for (MeldungsPosition position : meldungspositionen) {
            positionen.getPosition().add(createMeldungsPosition(position));
        }
        return positionen;
    }

    private AbgebenderProviderType createAbgebenderProvider(RueckMeldungPv input) {
        AbgebenderProviderType providerType = new AbgebenderProviderType();
        AbgebenderProvider abgebenderProvider = input.getAbgebenderProvider();
        RuemPvAntwortCode antwortCode = abgebenderProvider.getAntwortCode();
        if (antwortCode != null) {
            providerType.setAntwortcode(antwortCode.antwortCode);
        }
        providerType.setAntworttext(abgebenderProvider.getAntwortText());
        providerType.setProvidername(abgebenderProvider.getProviderName());
        providerType.setZustimmungProviderwechsel(abgebenderProvider.isZustimmungProviderWechsel() ? BoolDecisionType.J
                : BoolDecisionType.N);
        return providerType;
    }

    private AngabenZurLeitungType createLeitung(RueckMeldungPv input) {
        Leitung leitung = input.getLeitung();
        if (leitung == null) {
            return null;
        }
        AngabenZurLeitungType leitungType = new AngabenZurLeitungType();
        leitungType.setLeitungsbezeichnung(createLeitungsbezeichnung(leitung));

        // Schleifenwiederstand must not be set in WITA 4.0 ! (Even if the xsd allows it)
        return leitungType;
    }


    private LeitungsbezeichnungType createLeitungsbezeichnung(Leitung leitung) {
        if (leitung.getLeitungsBezeichnung() == null) {
            return null;
        }
        LeitungsBezeichnung leitungsBezeichnung = leitung.getLeitungsBezeichnung();

        LeitungsbezeichnungType leitungsbezeichnungType = new LeitungsbezeichnungType();
        leitungsbezeichnungType.setLeitungsschluesselzahl(leitungsBezeichnung.getLeitungsSchluesselZahl());
        leitungsbezeichnungType.setOnkzA(leitungsBezeichnung.getOnkzKunde());
        leitungsbezeichnungType.setOnkzB(leitungsBezeichnung.getOnkzKollokation());
        leitungsbezeichnungType.setOrdnungsnummer(leitungsBezeichnung.getOrdnungsNummer());
        return leitungsbezeichnungType;
    }

}
