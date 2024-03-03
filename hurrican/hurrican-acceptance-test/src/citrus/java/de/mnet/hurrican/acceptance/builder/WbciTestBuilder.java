/*

 * Copyright (c) 2017 - M-net Telekommunikations GmbH

 * All rights reserved.

 * -------------------------------------------------------

 * File created: 09.02.2017

 */

package de.mnet.hurrican.acceptance.builder;


import static de.mnet.wbci.model.GeschaeftsfallTyp.*;
import static de.mnet.wbci.model.WbciCdmVersion.*;

import java.time.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import de.mnet.wbci.dao.WbciDao;
import de.mnet.wbci.model.WbciGeschaeftsfall;
import de.mnet.wbci.model.builder.WbciGeschaeftsfallKueMrnTestBuilder;

/**
 * Stores Testdata for WBCIGeschaeftsfall in the database
 * <p>
 * Created by wieran on 09.02.2017.
 */
@Component
@Scope("prototype")
public class WbciTestBuilder {

    @Autowired
    private WbciDao wbciDao;

    private String vaId;
    private LocalDate kundenwunschtermin;

    /**
     * Stores Testdata for WBCIGeschaeftsfall in the database
     *
     * @return the WbciGeschaeftsfall
     */
    public WbciGeschaeftsfall build() {
        String newVaId = "DEU.MNET.V900000002";
        WbciGeschaeftsfall testGeschaeftsfall = new WbciGeschaeftsfallKueMrnTestBuilder()
                .withVorabstimmungsId(vaId != null ? vaId : newVaId)
                .withKundenwunschtermin(kundenwunschtermin)
                .buildValid(V1, VA_KUE_MRN);
        wbciDao.store(testGeschaeftsfall);

        return testGeschaeftsfall;
    }

    public WbciTestBuilder withVorabstimmungsId(String vaId) {
        this.vaId = vaId;
        return this;
    }

    public WbciTestBuilder withKundenwunschtermin(LocalDate wunschtermin) {
        this.kundenwunschtermin = wunschtermin;
        return this;
    }

}
