/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 02.02.2012 17:19:38
 */
package de.mnet.wita.ticketing.converter;

import static com.google.common.collect.Lists.*;

import java.util.*;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;

import de.augustakom.hurrican.dao.cc.CBVorgangDAO;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.tal.CBVorgang;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.cc.CarrierService;
import de.mnet.esb.cdm.customer.customerservice.v1.AddCommunication;
import de.mnet.wita.common.converter.AbstractConverterFunction;
import de.mnet.wita.message.MwfEntity;
import de.mnet.wita.message.meldung.Meldung;
import de.mnet.wita.message.meldung.position.MeldungsPosition;
import de.mnet.wita.model.AkmPvUserTask;
import de.mnet.wita.model.WitaCBVorgang;

public abstract class AbstractMwfBsiProtokollConverter<T extends MwfEntity> extends
        AbstractConverterFunction<MwfEntity, T, AddCommunication> {

    public static final String BSI_PROTOKOLL_EINTRAG_KONTAKT_ART_UID_WITA = "WITA";
    public static final String BSI_PROTOKOLL_EINTRAG_KONTAKT_GRUND_UID_WITA = "WITA-Meldung";

    /**
     * {0} wird für anbieterwechsel46TKG verwendet. Wenn nicht relevant wird ein Leerstring eingefügt.
     *
     * @see #getAnbieterwechsel46TKG(String)
     */
    public static final String WITA_REFERENZ_NR_TEMPLATE = "({0}WITA-Referenz-Nr.: {1})";

    @Autowired
    private CBVorgangDAO cbVorgangDAO;

    @Autowired
    private CarrierService carrierService;

    protected String formatMeldungspositionen(Set<? extends MeldungsPosition> positionen) {
        List<String> meldungsTexte = newArrayList();
        for (MeldungsPosition meldungsPosition : positionen) {
            meldungsTexte.add(meldungsPosition.getMeldungsText());
        }
        return Joiner.on(", ").join(meldungsTexte);
    }

    protected static AddCommunication createWitaProtokollEintrag() {
        AddCommunication protokollEintrag = new AddCommunication();
        protokollEintrag.setReason(BSI_PROTOKOLL_EINTRAG_KONTAKT_GRUND_UID_WITA);
        protokollEintrag.setType(BSI_PROTOKOLL_EINTRAG_KONTAKT_ART_UID_WITA);
        return protokollEintrag;
    }

    /**
     * Converter implementation provides Hurrican auftragId for
     * given MwfEntity.
     * @param mwfEntity
     * @return
     */
    public abstract Long findHurricanAuftragId(T mwfEntity);

    /**
     * Es wird der erste gefundene aktive Auftrag zurückgegeben, falls vorhanden. Ansonsten der erste gefundene
     * inaktive. Zuletzt wird einfach die {@code auftragId} auf dem {@link CBVorgang} zurückgegeben, falls nicht {@code
     * null}
     *
     * @throws @throws RuntimeException falls kein aktiver Auftrag zur {@code externeAuftragsnummer} existiert oder keine oder
     *                          mehrere {@link AuftragDaten} zum {@link AkmPvUserTask} der zu protokollierenden
     *                          PV-{@link Meldung} zugeordnet
     */
    protected Long findHurricanAuftragIdViaCbVorgang(String extAuftragsnummer) {
        CBVorgang cbVorgang = cbVorgangDAO.findCBVorgangByCarrierRefNr(extAuftragsnummer);
        try {
            List<Long> auftraegeInactive = Lists.newArrayList();
            List<AuftragDaten> auftragDaten4CB = carrierService.findAuftragDaten4CB(cbVorgang.getCbId());
            for (AuftragDaten auftragDaten : auftragDaten4CB) {
                if (auftragDaten.isAuftragActive() || (auftragDaten.isInKuendigung() && !auftragDaten.isCancelled())) {
                    return auftragDaten.getAuftragId();
                }
                auftraegeInactive.add(auftragDaten.getAuftragId());
            }
            if (!auftraegeInactive.isEmpty()) {
                return auftraegeInactive.get(0);
            }
            if (cbVorgang.getAuftragId() != null) { // vor allem für REX-MK
                return cbVorgang.getAuftragId();
            }
            throw new RuntimeException("Keinen Auftrag zur externen Auftragsnummer " + extAuftragsnummer + " gefunden!");
        }
        catch (FindException e) {
            throw new RuntimeException("Kein Auftrag zu Carrierbestellung gefunden", e);
        }
    }

    protected String getAnbieterwechsel46TKG(String extAuftragsnummer) {
        CBVorgang cbVorgang = cbVorgangDAO.findCBVorgangByCarrierRefNr(extAuftragsnummer);
        if ((cbVorgang != null) && cbVorgang.getAnbieterwechselTkg46()) {
            return WitaCBVorgang.ANBIETERWECHSEL_46TKG + " ";
        }
        return "";
    }
}
