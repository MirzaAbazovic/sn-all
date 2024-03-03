package de.mnet.hurrican.atlas.simulator.wita;

import de.mnet.hurrican.simulator.helper.LocaliseXPath;

/**
 * Basic enumeration of XPath expressions used in simulator.
 */
public enum WitaLineOrderXpathExpressions {

    USE_CASE("//*[local-name() = 'nachname']"),
    EXTERNAL_ORDER_ID("//*[local-name() = 'externeAuftragsnummer']"),
    NOTIFICATION_CONTRACT_NUMMER("//*[local-name() = 'vertragsnummer']"),
    IS_KUENDIGUNG("boolean(//*[local-name() = 'KUE-KD'])"),
    IS_BEREITSTELLUNG("boolean(//*[local-name() = 'NEU'])"),
    REQUESTED_CUSTOMER_DATE("//*[local-name() = 'geschaeftsfall']/*/*[local-name() = 'termine']/*[local-name() = 'kundenwunschtermin']/*[local-name() = 'datum']"),
    REQUESTED_CUSTOMER_CHANGE_DATE(LocaliseXPath.localise("//order/geschaeftsfallAenderung/kundenwunschtermin")),
    PRODUCT_IDENTIFIER("//*[local-name() = 'geschaeftsfall']/*/*[local-name() = 'auftragsposition']/*[local-name() = 'produkt']/*[local-name() = 'bezeichner']"),
    HAS_THIRD_PARTY_SALESMAN("boolean://*[local-name() = 'order']/*[local-name() = 'besteller']"),
    THIRD_PARTY_SALESMAN_CUSTOMER_ID("//*[local-name() = 'order']/*[local-name() = 'besteller']/*[local-name() = 'kundennummer']"),
    CUSTOMER_ID("//*[local-name() = 'order']/*[local-name() = 'kunde']/*[local-name() = 'kundennummer']"),
    PRE_AGREEMENT_ID(LocaliseXPath.localise("//geschaeftsfall/*/auftragsposition/geschaeftsfallProdukt/TAL/vorabstimmungsID")),
    MELDUNG_XSI_TYPE("parent(//meldungsattribute)/local-name()");

    /**
     * XPath expression for LineOrder interface.
     */
    private final String xpath;

    WitaLineOrderXpathExpressions(String xpath) {
        this.xpath = xpath;
    }

    /**
     * Gets the XPath expression related to either the wita version or the cdm version. In case wita version is set
     * method returns the wita message xpath expression otherwise the method will return the Atlas cdm version
     * expression.
     *
     * @param interfaceVersion
     * @return
     */
    public String getXpath(WitaLineOrderServiceVersion interfaceVersion) {
        if (interfaceVersion != null) {
            return xpath;
        }

        return null;
    }
}
