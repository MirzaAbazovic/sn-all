/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 28.02.2012 16:02:50
 */
package de.augustakom.hurrican.model.wholesale;

import static de.augustakom.hurrican.model.cc.Produkt.*;
import static de.augustakom.hurrican.model.wholesale.WholesaleProductGroup.*;

import java.util.*;
import java.util.stream.*;

import de.augustakom.hurrican.model.cc.TechLeistung.ExterneLeistung;

public enum WholesaleProductName {
    FTTB_50("FttB BSA 50/10", FTTB_BSA, PROD_ID_WHOLESALE_FTTX, ExterneLeistung.KOMBI_50_10),
    FTTH_50("FttH BSA 50/10", FTTH_BSA, PROD_ID_WHOLESALE_FTTX, ExterneLeistung.KOMBI_50_10),
    FTTB_100("FttB BSA 100/40", FTTB_BSA, PROD_ID_WHOLESALE_FTTX, ExterneLeistung.KOMBI_100_40),
    FTTH_100("FttH BSA 100/40", FTTH_BSA, PROD_ID_WHOLESALE_FTTX, ExterneLeistung.KOMBI_100_40),
    FTTH_300("FttH BSA 300/50", FTTH_BSA, PROD_ID_WHOLESALE_FTTX, ExterneLeistung.KOMBI_300_50);

    public final WholesaleProductGroup productGroup;
    public final Long hurricanProduktId;
    public final ExterneLeistung extLeistung;
    public final String productName;

    WholesaleProductName(String productName, WholesaleProductGroup productGroup, Long hurricanProduktId,
            ExterneLeistung extLeistung) {
        this.productName = productName;
        this.productGroup = productGroup;
        this.hurricanProduktId = hurricanProduktId;
        this.extLeistung = extLeistung;
    }

    public static Optional<WholesaleProductName> of(final String productName) {
        return Stream.of(WholesaleProductName.values())
                .filter(e -> e.productName.equals(productName))
                .findFirst();
    }

    public static Optional<WholesaleProductName> ofProdId(final Long productId) {
        return Stream.of(WholesaleProductName.values())
                .filter(e -> e.hurricanProduktId.equals(productId))
                .findFirst();
    }

    public static boolean existsWithProductId(Long prodId) {
        for (WholesaleProductName product : WholesaleProductName.values()) {
            if (product.hurricanProduktId.equals(prodId)) {
                return true;
            }
        }
        return false;
    }

}
