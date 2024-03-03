package de.bitconex.adlatus.wholebuy.provision.adapter.agreement;

import de.bitconex.tmf.agreement.model.Agreement;

import java.util.List;

/**
 * Used for API Calls to Agreement Management
 */
public interface AgreementClientService {

    /**
     * Get all list Agreement.
     * @return list of {@link  Agreement}
     */
    List<Agreement> getAllAgreements();
}
