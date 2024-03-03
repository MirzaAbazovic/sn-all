package de.mnet.hurrican.atlas.simulator.archive;

/**
 *
 */
public enum DocumentArchiveOperation {

    ARCHIVE_DOCUMENT("archiveDocument", "/DocumentArchiveService/archiveDocument"),
    GET_DOCUMENT("getDocument", "/DocumentArchiveService/getDocument"),
    SEARCH_DOCUMENT("searchDocument", "/DocumentArchiveService/searchDocument");

    private final String name;
    private final String soapAction;

    DocumentArchiveOperation(String name, String soapAction) {
        this.name = name;
        this.soapAction = soapAction;
    }

    public String getName() {
        return name;
    }

    public String getSoapAction() {
        return soapAction;
    }

    @Override
    public String toString() {
        return soapAction;
    }
}
