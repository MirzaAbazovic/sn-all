/**
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 19.11.2009 15:57:22
 */
package de.augustakom.hurrican.gui.auftrag.shared;

import java.util.*;

import de.augustakom.common.gui.swing.table.AKReferenceAwareTableModel;
import de.augustakom.common.tools.collections.CollectionMapConverter;
import de.augustakom.hurrican.model.cc.IPSecClient2SiteToken;
import de.augustakom.hurrican.model.cc.Reference;


/**
 *
 */
public class IPSecC2STokenTableModel extends AKReferenceAwareTableModel<IPSecClient2SiteToken> {

    /**
     * @param beanClass
     * @param columnNames
     * @param propertyNames
     * @param classTypes
     */
    public IPSecC2STokenTableModel() {
        super(new String[] {
                        "Techn. Auftragnummer",
                        "Seriennummer",
                        "SAP Bestellung",
                        "Lieferdatum",
                        "Laufzeit in Monaten",
                        "Batterie Ende",
                        "Status",
                        "Batch",
                        "Bemerkung" },
                new String[] {
                        IPSecClient2SiteToken.AUFTRAG_ID,
                        IPSecClient2SiteToken.SERIAL_NUMBER,
                        IPSecClient2SiteToken.SAP_ORDER_ID,
                        IPSecClient2SiteToken.LIEFERDATUM,
                        IPSecClient2SiteToken.LAUFZEIT_IN_MONATEN,
                        IPSecClient2SiteToken.BATTERIE_ENDE,
                        IPSecClient2SiteToken.STATUS_REF_ID,
                        IPSecClient2SiteToken.BATCH,
                        IPSecClient2SiteToken.BEMERKUNG },
                new Class[] { Long.class, String.class, String.class, Date.class, Integer.class, Date.class,
                        String.class, String.class, String.class }
        );
    }

    public void setTokenStatusRefs(List<Reference> tokenStatusRefs) {
        Map<Long, Reference> referenceMap = new HashMap<Long, Reference>();
        CollectionMapConverter.convert2Map(tokenStatusRefs, referenceMap, "getId", null);
        this.addReference(6, referenceMap, "strValue");
    }

}
