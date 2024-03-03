/**
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 24.09.2009 10:57:28
 */
package de.augustakom.hurrican.model.cc;

import de.augustakom.common.model.DontCreateBuilder;
import de.augustakom.common.model.UseBuilder;
import de.augustakom.hurrican.model.cc.hardware.HWBaugruppe;
import de.augustakom.hurrican.model.cc.hardware.HWBaugruppenTyp;

/**
 * EntityBuilder for HWBaugruppe objects
 *
 *
 */
@SuppressWarnings("unused")
public class HWBaugruppeBuilder extends AbstractCCIDModelBuilder<HWBaugruppeBuilder, HWBaugruppe> {
    @UseBuilder(HWDslamBuilder.class)
    private HWRackBuilder<?, ?> rackBuilder;
    @DontCreateBuilder
    private HWSubrackBuilder subrackBuilder;
    private HWBaugruppenTypBuilder hwBaugruppenTypBuilder;
    private String modNumber = "20-37";
    private Boolean eingebaut;

    private HWBaugruppenTyp hwBaugruppenTyp;

    @Override
    protected void beforeBuild() {
        if (subrackBuilder != null) {
            subrackBuilder.withRackBuilder(rackBuilder);
        }
    }

    public String getModNumber() {
        return this.modNumber;
    }

    public HWBaugruppeBuilder withHwBaugruppenTyp(HWBaugruppenTyp hwBaugruppenTyp) {
        this.hwBaugruppenTyp = hwBaugruppenTyp;
        return this;
    }

    public HWRackBuilder<?, ?> getRackBuilder() {
        return rackBuilder;
    }

    public HWSubrackBuilder getSubrackBuilder() {
        return subrackBuilder;
    }

    public HWBaugruppenTypBuilder getHwBaugruppenTypBuilder() {
        return hwBaugruppenTypBuilder;
    }

    public HWBaugruppeBuilder withBaugruppenTypBuilder(HWBaugruppenTypBuilder hwBaugruppenTypBuilder) {
        this.hwBaugruppenTypBuilder = hwBaugruppenTypBuilder;
        return this;
    }

    public HWBaugruppeBuilder withSubrackBuilder(HWSubrackBuilder subrackBuilder) {
        this.subrackBuilder = subrackBuilder;
        if ((subrackBuilder != null) && (subrackBuilder.getRackBuilder() != null)
                && (subrackBuilder.getRackBuilder() != rackBuilder)) {
            rackBuilder = subrackBuilder.getRackBuilder();
        }
        return this;
    }

    public HWBaugruppeBuilder withModNumber(String modNumber) {
        this.modNumber = modNumber;
        return this;
    }

    public HWBaugruppeBuilder withRackBuilder(HWRackBuilder<?, ?> rackBuilder) {
        this.rackBuilder = rackBuilder;
        if (this.subrackBuilder != null) {
            this.subrackBuilder.withRackBuilder(rackBuilder);
        }
        return this;
    }

    public HWBaugruppeBuilder withEingebaut(Boolean eingebaut) {
        this.eingebaut = eingebaut;
        return this;
    }

}
