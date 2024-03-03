/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 29.06.2011 15:56:51
 */
package de.mnet.wita.message.builder;

import java.lang.reflect.*;
import java.time.*;
import java.util.*;

import de.mnet.wita.WitaCdmVersion;
import de.mnet.wita.message.GeschaeftsfallTyp;
import de.mnet.wita.message.MnetWitaRequest;
import de.mnet.wita.message.auftrag.AuftragsKenner;
import de.mnet.wita.message.auftrag.Kunde;
import de.mnet.wita.message.auftrag.Projekt;
import de.mnet.wita.message.auftrag.geschaeftsfall.Geschaeftsfall;
import de.mnet.wita.message.builder.auftrag.geschaeftsfall.GeschaeftsfallBuilder;

public abstract class MnetWitaRequestBuilder<T extends MnetWitaRequest> {

    private Long id;
    private String externeAuftragsnummer = "123";
    private String vertragsnummer = "1234567890";
    private Long cbVorgangId = Long.MAX_VALUE;
    final WitaCdmVersion witaCdmVersion;
    private AuftragsKenner auftragsKenner;
    private Kunde besteller;
    private Projekt projekt;
    private Boolean requestWurdeStorniert = false;
    private LocalDateTime sentAt;
    private LocalDateTime earliestSendDate;
    private LocalDateTime mwfCreationDate;
    private final GeschaeftsfallBuilder geschaeftsfallBuilder;

    public MnetWitaRequestBuilder(GeschaeftsfallTyp geschaeftsfallTyp) {
        this(geschaeftsfallTyp, WitaCdmVersion.getDefault());
    }

    MnetWitaRequestBuilder(GeschaeftsfallTyp geschaeftsfallTyp, WitaCdmVersion witaCdmVersion) {
        this.geschaeftsfallBuilder = new GeschaeftsfallBuilder(geschaeftsfallTyp, witaCdmVersion);
        this.witaCdmVersion = witaCdmVersion;
    }

    MnetWitaRequestBuilder(GeschaeftsfallBuilder geschaeftsfallBuilder) {
        this.geschaeftsfallBuilder = geschaeftsfallBuilder;
        this.witaCdmVersion = WitaCdmVersion.getDefault();
    }

    public MnetWitaRequestBuilder<T> withId(Long id) {
        this.id = id;
        return this;
    }

    public MnetWitaRequestBuilder<T> withExterneAuftragsnummer(String externeAuftragsnummer) {
        this.externeAuftragsnummer = externeAuftragsnummer;
        return this;
    }

    public MnetWitaRequestBuilder<T> withVertragsnummer(String vertragsnummer) {
        this.vertragsnummer = vertragsnummer;
        return this;
    }

    public MnetWitaRequestBuilder<T> withBesteller(Kunde besteller) {
        this.besteller = besteller;
        return this;
    }

    public MnetWitaRequestBuilder<T> withAuftragsKenner(Long auftragsKlammer, Integer anzahlAuftraege) {
        this.auftragsKenner = new AuftragsKenner(auftragsKlammer, anzahlAuftraege);
        return this;
    }

    public MnetWitaRequestBuilder<T> withSentAt(LocalDateTime sentAt) {
        this.sentAt = sentAt;
        return this;
    }

    public MnetWitaRequestBuilder<T> withEarliestSendDate(LocalDateTime earliestSendDate) {
        this.earliestSendDate = earliestSendDate;
        return this;
    }

    public MnetWitaRequestBuilder<T> withMwfCreationDate(LocalDateTime mwfCreationDate) {
        this.mwfCreationDate = mwfCreationDate;
        return this;
    }

    public MnetWitaRequestBuilder<T> withRequestWurdeStorniert(Boolean requestWurdeStorniert) {
        this.requestWurdeStorniert = requestWurdeStorniert;
        return this;
    }

    public MnetWitaRequestBuilder<T> withProjekt(String projektKenner) {
        this.projekt = new Projekt(projektKenner);
        return this;
    }

    public MnetWitaRequestBuilder<T> withCbVorgangId(Long cbVorgangId) {
        this.cbVorgangId = cbVorgangId;
        return this;
    }

    public abstract T buildValid();

    public T buildWithoutKunde() {
        return createRequest();
    }

    public T buildWithoutDate() {
        return createRequest();
    }

    public T buildWithoutGeschaeftsfall() {
        T request = createRequest();
        request.setKunde(new Kunde());
        request.setCdmVersion(witaCdmVersion);
        return request;
    }

    T buildValidRequest() {
        T request = createRequest();
        request.setId(id);
        request.setExterneAuftragsnummer(externeAuftragsnummer);
        request.setKunde(createKunde());
        request.setBesteller(besteller);
        request.setAuftragsKenner(auftragsKenner);
        request.setProjekt(projekt);
        request.setRequestWurdeStorniert(requestWurdeStorniert);
        request.setSentAt(sentAt != null ? Date.from(sentAt.atZone(ZoneId.systemDefault()).toInstant()) : null);
        request.setEarliestSendDate(earliestSendDate != null ? Date.from(earliestSendDate.atZone(ZoneId.systemDefault()).toInstant()) : null);
        if (mwfCreationDate != null) {
            request.setMwfCreationDate(Date.from(mwfCreationDate.atZone(ZoneId.systemDefault()).toInstant()));
        }

        String ansprechpartnerNachname = "Huber";
        Geschaeftsfall geschaeftsfall = geschaeftsfallBuilder.withAnsprechpartnerNachname(
                ansprechpartnerNachname).withVertragsnummer(vertragsnummer).buildValid();
        request.setGeschaeftsfall(geschaeftsfall);
        request.setCbVorgangId(cbVorgangId);
        request.setCdmVersion(witaCdmVersion);

        return request;
    }

    @SuppressWarnings("unchecked")
    private T createRequest() {
        try {
            return ((Class<T>) ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments()[0])
                    .newInstance();
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Kunde createKunde() {
        Kunde kunde = new Kunde();
        kunde.setKundennummer("1234567890");
        kunde.setLeistungsnummer("1234567890");
        return kunde;
    }

    public GeschaeftsfallTyp getGeschaeftsfallTyp() {
        return geschaeftsfallBuilder.getGeschaeftsfallTyp();
    }

}
