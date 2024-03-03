package de.bitconex.adlatus.wholebuy.provision.adapter.party;

import de.bitconex.tmf.pm.model.Individual;
import de.bitconex.tmf.pm.model.IndividualCreate;
import de.bitconex.tmf.pm.model.Organization;

import java.util.List;

/**
 * We use this class for receiving information for Client or Organization.
 */
public interface PartyClientService {

    /**
     * Receiving information for client.
     *
     * @param href type of String
     * @return {@link  Individual}
     */
    Individual getIndividual(String href);

    /**
     * Receiving information for Organization.
     *
     * @param href type of String
     * @return {@link  Organization}
     */
    Organization getOrganization(String href);

    Individual createIndividual(IndividualCreate individual);

    List<Individual> getIndividuals();
}
