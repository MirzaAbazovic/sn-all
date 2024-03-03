package de.bitconex.adlatus.wholebuy.provision.adapter.agreement;

import de.bitconex.adlatus.common.infrastructure.aspects.RestRequestLogging;
import de.bitconex.tmf.agreement.client.ApiClient;
import de.bitconex.tmf.agreement.client.api.AgreementApi;
import de.bitconex.tmf.agreement.model.Agreement;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RestRequestLogging
public class AgreementClientServiceImpl implements AgreementClientService {
    private final AgreementApi agreementApi;

    public AgreementClientServiceImpl(@Value("${agreement.api.basepath}") String agreementApiBasePath) {
        ApiClient apiClient = new ApiClient();
        apiClient.setBasePath(agreementApiBasePath);
        agreementApi = apiClient.buildClient(AgreementApi.class);
    }

    @Override
    public List<Agreement> getAllAgreements() {
        return agreementApi.listAgreement(null, null, null);
    }
}
