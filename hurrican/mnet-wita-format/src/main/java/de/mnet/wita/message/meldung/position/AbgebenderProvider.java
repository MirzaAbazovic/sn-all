/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 11.08.2011 13:21:44
 */
package de.mnet.wita.message.meldung.position;

import javax.persistence.*;
import javax.validation.constraints.*;

import de.mnet.wita.RuemPvAntwortCode;
import de.mnet.wita.message.MwfEntity;
import de.mnet.wita.validators.groups.Workflow;

@edu.umd.cs.findbugs.annotations.SuppressWarnings(value = "EQ_DOESNT_OVERRIDE_EQUALS", justification = "equals in super class just compares id therefore no overriding necessary")
@Entity
@Table(name = "T_MWF_ABGEBENDERPROVIDER")
@SequenceGenerator(name = "SEQ_GEN", sequenceName = "S_T_MWF_ABGEBENDERPROVIDER_0", allocationSize = 1)
public class AbgebenderProvider extends MwfEntity {

    private static final long serialVersionUID = 3994463001294677265L;

    private RuemPvAntwortCode antwortCode;
    private String antwortText;
    private String providerName;
    boolean zustimmungProviderWechsel;

    @Size(min = 1, max = 60)
    @Column(name = "PROVIDER_NAME")
    public String getProviderName() {
        return providerName;
    }

    public void setProviderName(String providerName) {
        this.providerName = providerName;
    }

    @NotNull(groups = Workflow.class)
    @Column(name = "ZUSTIMMUNG_PROVIDER_WECHSEL")
    public boolean isZustimmungProviderWechsel() {
        return zustimmungProviderWechsel;
    }

    public void setZustimmungProviderWechsel(boolean zustimmungProviderWechsel) {
        this.zustimmungProviderWechsel = zustimmungProviderWechsel;
    }

    @Size(min = 1, max = 255)
    @Column(name = "ANTWORT_TEXT")
    public String getAntwortText() {
        return antwortText;
    }

    public void setAntwortText(String antwortText) {
        this.antwortText = antwortText;
    }

    @Enumerated(EnumType.STRING)
    @Column(name = "ANTWORT_CODE")
    public RuemPvAntwortCode getAntwortCode() {
        return antwortCode;
    }

    public void setAntwortCode(RuemPvAntwortCode antwortCode) {
        this.antwortCode = antwortCode;
    }

    @Override
    public String toString() {
        return "AbgebenderProvider [antwortCode=" + antwortCode + ", antwortText="
                + antwortText + ", providerName=" + providerName + ", zustimmungProviderWechsel="
                + zustimmungProviderWechsel + "]";
    }

}
