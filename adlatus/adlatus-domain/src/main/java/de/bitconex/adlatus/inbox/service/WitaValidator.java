package de.bitconex.adlatus.inbox.service;

import de.telekom.wholesale.oss.v15.envelope.AnnehmenAuftragRequestType;
import de.telekom.wholesale.oss.v15.envelope.AnnehmenMeldungRequestType;

/**
 * Validation of outgoing and incoming WITA messages (it is used before it is sent to DTAG and when it is received from DTAG to validate the soap message, i.e. its content)
 */
public interface WitaValidator {

    /**
     * Validate {@link  AnnehmenAuftragRequestType}.
     *
     * @param request {@link AnnehmenAuftragRequestType}
     */
    void validate(AnnehmenAuftragRequestType request);

    /**
     * Validate {@link  AnnehmenMeldungRequestType}.
     *
     * @param request {@link AnnehmenMeldungRequestType}
     */
    void validate(AnnehmenMeldungRequestType request);
}
