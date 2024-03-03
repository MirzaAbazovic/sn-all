/**
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 17.09.2009 12:24:53
 */
package de.augustakom.hurrican.model.cc;

import java.util.*;

import de.augustakom.common.tools.lang.DateTools;


/**
 * Entity-Builder fuer IntAccount Objekte
 *
 *
 */
@SuppressWarnings("unused")
public class IntAccountBuilder extends AbstractCCIDModelBuilder<IntAccountBuilder, IntAccount> {
    private String account = "BlaUser";
    private String passwort = "nichtsogeheim";
    private Integer liNr = null;
    private Date gueltigVon = new Date();
    private Date gueltigBis = DateTools.getHurricanEndDate();

    /**
     * @see de.augustakom.common.model.EntityBuilder#initialize()
     */
    @Override
    protected void initialize() {
        super.initialize();
        this.gueltigVon = DateTools.createDate(2009, 0, 1);
        this.gueltigBis = DateTools.getHurricanEndDate();
    }

    public String getAccount() {
        return account;
    }

    public IntAccountBuilder withAccount(String account) {
        this.account = account;
        return this;
    }

    public IntAccountBuilder withRandomAccount() {
        this.account = randomString(10);
        return this;
    }

    public String getPasswort() {
        return passwort;
    }

    public IntAccountBuilder withPasswort(String passwort) {
        this.passwort = passwort;
        return this;
    }

    public IntAccountBuilder withLiNr(Integer liNr) {
        this.liNr = liNr;
        return this;
    }

    public IntAccountBuilder withGueltigBis(Date gueltigBis) {
        this.gueltigBis = gueltigBis;
        return this;
    }

}
