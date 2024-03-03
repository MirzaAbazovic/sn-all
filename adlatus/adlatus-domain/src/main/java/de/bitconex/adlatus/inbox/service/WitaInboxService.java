package de.bitconex.adlatus.inbox.service;

import de.bitconex.adlatus.common.model.WitaProductInbox;
import de.telekom.wholesale.oss.v15.envelope.AnnehmenMeldungRequestType;

import java.util.List;

/**
 * Used for processing WITA message. For more <a href="https://bitconex.atlassian.net/wiki/spaces/ADL/pages/2245099527/Interfaces">visit WITA</a>
 */
public interface WitaInboxService {

    /**
     * Save incoming request but Check all WITA msg where status is correct.
     *
     * @param request {@link AnnehmenMeldungRequestType}
     */
    void save(AnnehmenMeldungRequestType request);

    void save(WitaProductInbox message);

    List<WitaProductInbox> findByExternalOrderId(String id);

    WitaProductInbox findById(String id);
}
