package de.bitconex.adlatus.wholebuy.provision.adapter.geoaddress;

import de.bitconex.adlatus.common.infrastructure.aspects.RestRequestLogging;
import de.bitconex.tmf.address.model.GeographicAddress;
import de.bitconex.tmf.client.ApiClient;
import de.bitconex.tmf.client.api.GeographicAddressApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RestRequestLogging
public class GeographicAddressClientServiceImpl implements GeographicAddressClientService {

    @Value("${ga.api.basepath}")
    private String gaApiBasePath;

    @Override
    public GeographicAddress getAddress(String href) {
        var basePath = resolveBasePath(href);
        var id = resolveId(href);
        return getGeographicAddressApi(basePath).retrieveGeographicAddress(id, "");
    }

    public GeographicAddressApi getGeographicAddressApi(String basePath) {
        ApiClient apiClient = new ApiClient();
        apiClient.setBasePath(basePath);

        return apiClient.buildClient(GeographicAddressApi.class);
    }

    private String resolveBasePath(String href) {
        if (!href.startsWith("http"))
            return gaApiBasePath;

        var basePath = href;

        var index = basePath.lastIndexOf("/geographicAddress");
        if (index > 0)
            basePath = basePath.substring(0, index);

        return basePath;
    }

    private String resolveId(String href) {
        href = href.endsWith("/") ? href.substring(0, href.length() - 1) : href;
        return href.substring(href.lastIndexOf("/") + 1);
    }
}
