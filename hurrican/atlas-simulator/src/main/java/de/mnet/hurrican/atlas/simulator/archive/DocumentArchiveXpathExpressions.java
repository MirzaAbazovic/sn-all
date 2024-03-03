package de.mnet.hurrican.atlas.simulator.archive;

/**
 *
 */
public enum DocumentArchiveXpathExpressions {

    DOCUMENT_ID("//*[local-name() = 'documentId']");

    /**
     * XPath expression
     */
    private final String xpath;

    /**
     * Constructor using xpath expression.
     *
     * @param xpath
     */
    DocumentArchiveXpathExpressions(String xpath) {
        this.xpath = xpath;
    }

    /**
     * Gets the xpath expression.
     *
     * @return
     */
    public String getXpath() {
        return xpath;
    }
}
