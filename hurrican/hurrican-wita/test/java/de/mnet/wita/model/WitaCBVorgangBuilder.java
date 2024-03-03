/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 15.07.2011 14:15:28
 */
package de.mnet.wita.model;

import java.util.*;

import de.augustakom.common.model.SessionFactoryAware;
import de.augustakom.hurrican.model.cc.AbstractCBVorgangBuilder;
import de.augustakom.hurrican.model.cc.WitaCBVorgangAnlage;
import de.mnet.wita.message.GeschaeftsfallTyp;
import de.mnet.wita.message.auftrag.Kundenwunschtermin.Zeitfenster;
import de.mnet.wita.message.meldung.position.AenderungsKennzeichen;
import de.mnet.wita.model.WitaCBVorgang.AbmState;

@SuppressWarnings("unused")
@SessionFactoryAware("cc.sessionFactory")
public class WitaCBVorgangBuilder extends AbstractCBVorgangBuilder<WitaCBVorgangBuilder, WitaCBVorgang> {

    public GeschaeftsfallTyp witaGeschaeftsfallTyp = GeschaeftsfallTyp.BEREITSTELLUNG;
    public AenderungsKennzeichen aenderungsKennzeichen = AenderungsKennzeichen.STANDARD;
    public AenderungsKennzeichen letztesGesendetesAenderungsKennzeichen = AenderungsKennzeichen.STANDARD;
    public AbmState abmState = AbmState.NO_ABM;
    public Zeitfenster realisierungsZeitfenster;
    public TamUserTaskBuilder tamUserTaskBuilder;
    public Set<Long> rufnummerIds = new HashSet<>();
    public Long auftragsKlammer;
    private boolean requestOnUnsentRequest = false;
    private List<WitaCBVorgangAnlage> anlagen;
    private String vorabstimmungsId;
    private Long cbVorgangRefId;

    public WitaCBVorgangBuilder withWitaGeschaeftsfallTyp(GeschaeftsfallTyp witaGeschaeftsfallTyp) {
        this.witaGeschaeftsfallTyp = witaGeschaeftsfallTyp;
        return this;
    }

    public WitaCBVorgangBuilder withAenderungsKennzeichen(AenderungsKennzeichen aenderungsKennzeichen) {
        this.aenderungsKennzeichen = aenderungsKennzeichen;
        return this;
    }

    public WitaCBVorgangBuilder withRealisierungsZeitfenster(Zeitfenster witaZeitfenster) {
        this.realisierungsZeitfenster = witaZeitfenster;
        return this;
    }

    public WitaCBVorgangBuilder withTamUserTask(TamUserTaskBuilder tamUserTaskBuilder) {
        this.tamUserTaskBuilder = tamUserTaskBuilder;
        return this;
    }

    public WitaCBVorgangBuilder withRufnummerIds(Set<Long> rufnummerIds) {
        this.rufnummerIds = rufnummerIds;
        return this;
    }

    public WitaCBVorgangBuilder withAuftragsKlammer(Long auftragsKlammer) {
        this.auftragsKlammer = auftragsKlammer;
        return this;
    }

    public WitaCBVorgangBuilder withAnlagen(List<WitaCBVorgangAnlage> anlagen) {
        this.anlagen = anlagen;
        return this;
    }

    public WitaCBVorgangBuilder withVorabstimmungsId(String vorabstimmungsId) {
        this.vorabstimmungsId = vorabstimmungsId;
        return this;
    }

    public WitaCBVorgangBuilder withCbVorgangRefId(Long cbVorgangRefId) {
        this.cbVorgangRefId = cbVorgangRefId;
        return this;
    }
}
