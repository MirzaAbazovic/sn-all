/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 25.09.2014
 */
package de.mnet.wita.model.builder.cdm.lineordernotificationservice.v2;

import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v2.AenderungskennzeichenType;
import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v2.AuftragstypType;
import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v2.GeschaeftsfallArtType;
import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v2.GeschaeftsfallEnumType;
import de.mnet.wita.model.builder.cdm.lineordernotificationservice.LineOrderNotificationTypeBuilder;

/**
 *
 */
public class AuftragstypTypeBuilder implements LineOrderNotificationTypeBuilder<AuftragstypType> {

    private GeschaeftsfallEnumType geschaeftsfall;
    private AenderungskennzeichenType aenderungsKennzeichen;
    private GeschaeftsfallArtType geschaeftsfallart;

    @Override
    public AuftragstypType build() {
        AuftragstypType auftragstypType = new AuftragstypType();
        auftragstypType.setAenderungsKennzeichen(aenderungsKennzeichen);
        auftragstypType.setGeschaeftsfall(geschaeftsfall);
        auftragstypType.setGeschaeftsfallart(geschaeftsfallart);
        return auftragstypType;
    }

    public AuftragstypTypeBuilder withGeschaeftsfall(GeschaeftsfallEnumType geschaeftsfall) {
        this.geschaeftsfall = geschaeftsfall;
        return this;
    }

    public AuftragstypTypeBuilder withAenderungsKennzeichen(AenderungskennzeichenType aenderungsKennzeichen) {
        this.aenderungsKennzeichen = aenderungsKennzeichen;
        return this;
    }

    public AuftragstypTypeBuilder withGeschaeftsfallart(GeschaeftsfallArtType geschaeftsfallart) {
        this.geschaeftsfallart = geschaeftsfallart;
        return this;
    }

}
