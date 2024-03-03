package de.bitconex.adlatus.wholebuy.provision.service.wita;

import de.telekom.wholesale.oss.v15.envelope.AnnehmenMeldungRequestType;

public interface WitaMessageHandler {
    void handle(String orderId, AnnehmenMeldungRequestType request, String witaInboxId);
}
