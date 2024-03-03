package de.bitconex.adlatus.wholebuy.provision.adapter.catalog;

import de.bitconex.adlatus.common.infrastructure.aspects.RestRequestLogging;
import de.bitconex.tmf.rcm.client.ApiClient;
import de.bitconex.tmf.rcm.client.api.ResourceSpecificationApi;
import de.bitconex.tmf.rcm.model.ResourceSpecification;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RestRequestLogging
public class ResourceCatalogClientServiceImpl implements ResourceCatalogClientService {
    private final ResourceSpecificationApi resourceSpecificationApi;

    public ResourceCatalogClientServiceImpl(@Value("${rcm.api.basepath}") String rcmApiBasePath) {
        ApiClient apiClient = new ApiClient();
        apiClient.setBasePath(rcmApiBasePath);
        resourceSpecificationApi = apiClient.buildClient(ResourceSpecificationApi.class);
    }

    @Override
    public List<ResourceSpecification> getAllResourceSpecifications() {
        return resourceSpecificationApi.listResourceSpecification(null, null, null);
    }
}
