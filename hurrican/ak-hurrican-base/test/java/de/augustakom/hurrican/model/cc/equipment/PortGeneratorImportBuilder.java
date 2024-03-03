/**
 * Copyright (c) 2010 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 06.07.2010 14:16:51
 */
package de.augustakom.hurrican.model.cc.equipment;

import de.augustakom.common.model.EntityBuilder;

/**
 * EntityBuilder für {@link PortGeneratorImport} Objekte.
 */
@SuppressWarnings("unused")
public class PortGeneratorImportBuilder extends EntityBuilder<PortGeneratorImportBuilder, PortGeneratorImport> {

    private String hwEqn = null;
    private String v5Port = null;
    private String rangVerteilerIn = null;
    private String rangBuchtIn = null;
    private String rangLeiste1In = null;
    private String rangStift1In = null;
    private String rangLeiste2In = null;
    private String rangStift2In = null;
    private String rangVerteilerOut = null;
    private String rangBuchtOut = null;
    private String rangLeiste1Out = null;
    private String rangStift1Out = null;
    private String rangLeiste2Out = null;
    private String rangStift2Out = null;

    private boolean portAlreadyExists = false;

    public PortGeneratorImportBuilder withHwEqn(String hwEqn) {
        this.hwEqn = hwEqn;
        return this;
    }

    public PortGeneratorImportBuilder withV5Port(String v5Port) {
        this.v5Port = v5Port;
        return this;
    }

    public PortGeneratorImportBuilder withRangVerteilerIn(String rangVerteilerIn) {
        this.rangVerteilerIn = rangVerteilerIn;
        return this;
    }

    public PortGeneratorImportBuilder withRangLeiste1In(String rangLeiste1In) {
        this.rangLeiste1In = rangLeiste1In;
        return this;
    }

    public PortGeneratorImportBuilder withRangStift1In(String rangStift1In) {
        this.rangStift1In = rangStift1In;
        return this;
    }

    public PortGeneratorImportBuilder withRangLeiste2In(String rangLeiste2In) {
        this.rangLeiste2In = rangLeiste2In;
        return this;
    }

    public PortGeneratorImportBuilder withRangStift2In(String rangStift2In) {
        this.rangStift2In = rangStift2In;
        return this;
    }

    public PortGeneratorImportBuilder withRangVerteilerOut(String rangVerteilerOut) {
        this.rangVerteilerOut = rangVerteilerOut;
        return this;
    }

    public PortGeneratorImportBuilder withRangLeiste1Out(String rangLeiste1Out) {
        this.rangLeiste1Out = rangLeiste1Out;
        return this;
    }

    public PortGeneratorImportBuilder withRangStift1Out(String rangStift1Out) {
        this.rangStift1Out = rangStift1Out;
        return this;
    }

    public PortGeneratorImportBuilder withRangLeiste2Out(String rangLeiste2Out) {
        this.rangLeiste2Out = rangLeiste2Out;
        return this;
    }

    public PortGeneratorImportBuilder withRangStift2Out(String rangStift2Out) {
        this.rangStift2Out = rangStift2Out;
        return this;
    }

    public PortGeneratorImportBuilder withRangBuchtIn(String rangBuchtIn) {
        this.rangBuchtIn = rangBuchtIn;
        return this;
    }

    public PortGeneratorImportBuilder withRangBuchtOut(String rangBuchtOut) {
        this.rangBuchtOut = rangBuchtOut;
        return this;
    }
}
