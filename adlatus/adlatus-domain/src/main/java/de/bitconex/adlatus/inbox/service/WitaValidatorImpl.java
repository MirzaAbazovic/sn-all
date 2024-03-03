package de.bitconex.adlatus.inbox.service;

import de.bitconex.adlatus.wholebuy.provision.adapter.wita.exception.WitaMessageException;
import de.telekom.wholesale.oss.v15.envelope.AnnehmenAuftragRequestType;
import de.telekom.wholesale.oss.v15.envelope.AnnehmenMeldungRequestType;
import de.telekom.wholesale.oss.v15.envelope.WholesaleRequestType;
import de.telekom.wholesale.oss.v15.order.AuftragType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class WitaValidatorImpl implements WitaValidator {

    @Override
    public void validate(AnnehmenAuftragRequestType request) {
        validateVersion(request.getControl());
        validateAuftrag(request.getAuftrag());
    }

    @Override
    public void validate(AnnehmenMeldungRequestType request) {
        validateVersion(request.getControl());
    }

    private void validateVersion(WholesaleRequestType requestType) {
        if (requestType.getMajorRelease().isEmpty() || requestType.getMinorRelease().isEmpty()) {
            throw new WitaMessageException("Missing major or minor version.");
        }
    }

    private void validateAuftrag(AuftragType auftragType) {
        if (auftragType.getExterneAuftragsnummer().isEmpty()) {
            throw new WitaMessageException("Missing externeAuftragsnummer.");
        }
    }
}
