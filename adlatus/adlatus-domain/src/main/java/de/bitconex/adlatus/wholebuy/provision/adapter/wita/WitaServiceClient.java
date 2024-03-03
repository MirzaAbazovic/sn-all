package de.bitconex.adlatus.wholebuy.provision.adapter.wita;

import de.telekom.wholesale.oss.v15.envelope.AnnehmenAuftragRequestType;
import de.telekom.wholesale.oss.v15.envelope.AnnehmenAuftragResponseType;
import de.telekom.wholesale.oss.v15.wholesale.WholesalePort;
import de.telekom.wholesale.oss.v15.wholesale.WholesaleService;
import jakarta.xml.ws.BindingProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class WitaServiceClient {

    private final WholesaleService wholesaleService;

    public WitaServiceClient() {
        wholesaleService = new WholesaleService();
    }

    public AnnehmenAuftragResponseType sendOrder(AnnehmenAuftragRequestType request, String endpoint) {
        WholesalePort wholesalePort = wholesaleService.getWholesale();
        BindingProvider bindingProvider = (BindingProvider) wholesalePort;

        if (endpoint != null) {
            bindingProvider.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, endpoint);
        }

        log.info("Call WITA on {} with {}", bindingProvider.getRequestContext().get(BindingProvider.ENDPOINT_ADDRESS_PROPERTY), request);
        AnnehmenAuftragResponseType response = wholesalePort.annehmenAuftrag(request);
        log.info("Response from WITA: {}", response);

        return response;
    }
}
