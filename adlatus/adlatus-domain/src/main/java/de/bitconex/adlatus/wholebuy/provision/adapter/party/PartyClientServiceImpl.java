package de.bitconex.adlatus.wholebuy.provision.adapter.party;

import de.bitconex.adlatus.common.infrastructure.aspects.RestRequestLogging;
import de.bitconex.tmf.pm.client.ApiClient;
import de.bitconex.tmf.pm.client.api.IndividualApi;
import de.bitconex.tmf.pm.client.api.OrganizationApi;
import de.bitconex.tmf.pm.model.Individual;
import de.bitconex.tmf.pm.model.IndividualCreate;
import de.bitconex.tmf.pm.model.Organization;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RestRequestLogging
public class PartyClientServiceImpl implements PartyClientService {
    @Value("${pm.api.basepath}")
    private String pmApiBasePath;

    @Override
    public Individual getIndividual(String href) {
        var basePath = resolveBasePath(href);
        var id = resolveId(href);
        return getIndividualApi(basePath).retrieveIndividual(id, "");
    }

    @Override
    public Organization getOrganization(String href) {
        var basePath = resolveBasePath(href);
        var id = resolveId(href);
        return getOrganizationApi(basePath).retrieveOrganization(id, "");
    }

    @Override
    public Individual createIndividual(IndividualCreate individual) {
        var individualApi = getIndividualApi(pmApiBasePath);
        return individualApi.createIndividual(individual);
    }

    @Override
    public List<Individual> getIndividuals() {
        var individualApi = getIndividualApi(pmApiBasePath);
        return individualApi.listIndividual(null);
    }

    private IndividualApi getIndividualApi(String basePath) {
        ApiClient apiClient = new ApiClient();
        apiClient.setBasePath(basePath);
        return apiClient.buildClient(IndividualApi.class);
    }

    private OrganizationApi getOrganizationApi(String basePath) {
        ApiClient apiClient = new ApiClient();
        apiClient.setBasePath(basePath);
        return apiClient.buildClient(OrganizationApi.class);
    }

    private String resolveBasePath(String href) {
        if (!href.startsWith("http"))
            return pmApiBasePath;

        var basePath = href;

        var individualIndex = basePath.indexOf("/individual");
        if (individualIndex > 0)
            basePath = basePath.substring(0, individualIndex);

        var organizationIndex = basePath.indexOf("/organization");
        if (organizationIndex > 0)
            basePath = basePath.substring(0, organizationIndex);

        return basePath;
    }

    private String resolveId(String href) {
        href = href.endsWith("/") ? href.substring(0, href.length() - 1) : href;
        return href.substring(href.lastIndexOf("/") + 1);
    }
}
