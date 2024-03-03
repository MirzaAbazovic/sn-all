/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 11.10.13
 */
package de.mnet.wbci.acceptance.donating.behavior;

import java.time.*;
import java.util.*;
import org.apache.commons.collections.CollectionUtils;

import de.mnet.wbci.acceptance.AbstractTestBehavior;
import de.mnet.wbci.acceptance.common.builder.RueckmeldungVorabstimmungKftBuilder;
import de.mnet.wbci.acceptance.common.builder.RufnummernportierungEinzelKftBuilder;
import de.mnet.wbci.model.GeschaeftsfallTyp;
import de.mnet.wbci.model.IOType;
import de.mnet.wbci.model.MeldungTyp;
import de.mnet.wbci.model.MeldungsCode;
import de.mnet.wbci.model.RueckmeldungVorabstimmung;
import de.mnet.wbci.model.RufnummerOnkz;
import de.mnet.wbci.model.Rufnummernportierung;
import de.mnet.wbci.model.WbciGeschaeftsfallStatus;
import de.mnet.wbci.model.WbciRequestStatus;
import de.mnet.wbci.model.builder.RueckmeldungVorabstimmungBuilder;
import de.mnet.wbci.model.builder.RufnummerOnkzBuilder;

/**
 *
 */
public class SendRUEMVA_TestBehavior extends AbstractTestBehavior {

    private final MeldungsCode[] expectedMeldungsCodes;
    private final LocalDateTime bestaetigterWechseltermin;
    private RueckmeldungVorabstimmungBuilder builder;
    private List<RufnummerOnkz> einzelrufnummern;
    private boolean automatable = false;
    private Rufnummernportierung rufnummernportieurng;
    private String ruemvaTemplate;

    public SendRUEMVA_TestBehavior(MeldungsCode... expectedMeldungsCodes) {
        this(null, expectedMeldungsCodes);
    }

    public SendRUEMVA_TestBehavior(LocalDateTime bestaetigterWechseltermin, MeldungsCode... expectedMeldungsCodes) {
        this.expectedMeldungsCodes = expectedMeldungsCodes;
        this.bestaetigterWechseltermin = bestaetigterWechseltermin;
        this.einzelrufnummern = new ArrayList<>();
        this.ruemvaTemplate = "RUEMVA";
    }

    public SendRUEMVA_TestBehavior withBuilder(RueckmeldungVorabstimmungBuilder builder) {
        this.builder = builder;
        return this;
    }

    public SendRUEMVA_TestBehavior addEinzelrufnummer(String onkz, String rufnummer, String pkiAbg) {
        this.einzelrufnummern.add(new RufnummerOnkzBuilder()
                .withOnkz(onkz)
                .withRufnummer(rufnummer)
                .withPortierungKennungPKIabg(pkiAbg).build());
        return this;
    }

    public SendRUEMVA_TestBehavior withExplicitGeschaeftsfallTyp(GeschaeftsfallTyp explicitGeschaeftsfallTyp) {
        setExplicitGeschaeftsfallTyp(explicitGeschaeftsfallTyp);
        return this;
    }


    public SendRUEMVA_TestBehavior withAutomatable() {
        this.automatable = true;
        return this;
    }

    @Override
    public void apply() {
        if (builder == null) {
            builder = new RueckmeldungVorabstimmungKftBuilder(getCdmVersion(), getGeschaeftsfallTyp(), IOType.OUT);
        }
        if (CollectionUtils.isNotEmpty(einzelrufnummern)) {
            builder.withRufnummernportierung(new RufnummernportierungEinzelKftBuilder(getCdmVersion())
                    .withRufnummerOnkzs(einzelrufnummern).build());
        }
        if (bestaetigterWechseltermin != null) {
            builder.withWechseltermin(bestaetigterWechseltermin != null ? bestaetigterWechseltermin.toLocalDate() : null);
        }

        if (automatable) {
            hurrican().markGfAsAutomatable();
        }
        if (rufnummernportieurng != null) {
            builder.withRufnummernportierung(rufnummernportieurng);
        }

        RueckmeldungVorabstimmung ruemVa = builder.build();
        hurrican().createWbciMeldung(ruemVa);

        hurrican().assertIoArchiveEntryCreated(IOType.OUT, getGeschaeftsfallTyp(), MeldungTyp.RUEM_VA);
        hurrican().assertKlaerfallStatus(false, null);
        hurrican().assertVaMeldungsCodes(expectedMeldungsCodes);
        hurrican().assertVaRequestStatus(WbciRequestStatus.RUEM_VA_VERSENDET);
        if (getGeschaeftsfallTyp().equals(GeschaeftsfallTyp.VA_RRNP)) {
            hurrican().assertGfStatus(WbciGeschaeftsfallStatus.PASSIVE);
        }
        else {
            hurrican().assertGfStatus(WbciGeschaeftsfallStatus.ACTIVE);
        }
        hurrican().assertWechseltermin(ruemVa.getWechseltermin().atStartOfDay());
        hurrican().assertVaAnswerDeadlineIsSet();
        hurrican().assertVorabstimmungAbgebendSet(true, "positive WBCI-Vorabstimmung - RUEM-VA versendet.*");
        atlas().receiveCarrierChangeUpdate(ruemvaTemplate);
    }

    public SendRUEMVA_TestBehavior withRufnummernportieurng(Rufnummernportierung rufnummernportieurng) {
        this.rufnummernportieurng = rufnummernportieurng;
        return this;
    }

    public SendRUEMVA_TestBehavior withRuemVaTemplate(String ruemvaTemplate) {
        this.ruemvaTemplate = ruemvaTemplate;
        return this;
    }
}
