/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 11.11.2009 11:02:10
 */
package de.augustakom.hurrican.model.cc;

import de.augustakom.common.model.EntityBuilder;
import de.augustakom.hurrican.model.cc.EndstelleConnect.ESEinstellung;
import de.augustakom.hurrican.model.cc.EndstelleConnect.ESRouterInfo;
import de.augustakom.hurrican.model.cc.EndstelleConnect.ESSchnittstelle;


@SuppressWarnings("unused")
public class EndstelleConnectBuilder extends EntityBuilder<EndstelleConnectBuilder, EndstelleConnect> {

    private EndstelleBuilder endstelleBuilder;
    private String gebaeude = "Gebäude 1";
    private String etage = "Stockwerk 3";
    private String raum = "3.45";
    private String schrank = "3.45.18";
    private String uebergabe;
    private String bandbreite;
    private ESSchnittstelle schnittstelle = ESSchnittstelle.G957;
    private ESEinstellung einstellung = null;
    private ESRouterInfo routerinfo = ESRouterInfo.MIETROUTER;
    private String routertyp = "FritzBox";
    private String bemerkung = null;
    private String defaultGateway = "192.168.1.1";

    public EndstelleConnectBuilder withEndstelleBuilder(EndstelleBuilder endstelleBuilder) {
        this.endstelleBuilder = endstelleBuilder;
        return this;
    }

    public EndstelleBuilder getEnstelleBuilder() {
        return endstelleBuilder;
    }

    public EndstelleConnectBuilder withGebaude(String gebaude) {
        this.gebaeude = gebaude;
        return this;
    }

    public EndstelleConnectBuilder withEtage(String etage) {
        this.etage = etage;
        return this;
    }

    public EndstelleConnectBuilder withRaum(String raum) {
        this.raum = raum;
        return this;
    }

    public EndstelleConnectBuilder withSchrank(String schrank) {
        this.schrank = schrank;
        return this;
    }

    public EndstelleConnectBuilder withUebergabe(String uebergabe) {
        this.uebergabe = uebergabe;
        return this;
    }

    public EndstelleConnectBuilder withBandbreite(String bandbreite) {
        this.bandbreite = bandbreite;
        return this;
    }

    public EndstelleConnectBuilder withSchnittstelle(ESSchnittstelle schnittstelle) {
        this.schnittstelle = schnittstelle;
        return this;
    }

    public EndstelleConnectBuilder withEinstellung(ESEinstellung einstellung) {
        this.einstellung = einstellung;
        return this;
    }

    public EndstelleConnectBuilder withRouterInfo(ESRouterInfo routerinfo) {
        this.routerinfo = routerinfo;
        return this;
    }

    public EndstelleConnectBuilder withRouterTyp(String routertyp) {
        this.routertyp = routertyp;
        return this;
    }

    public EndstelleConnectBuilder withBemerkung(String bemerkung) {
        this.bemerkung = bemerkung;
        return this;
    }

    public EndstelleConnectBuilder withDefaultGateway(String defaultGateway) {
        this.defaultGateway = defaultGateway;
        return this;
    }
}
