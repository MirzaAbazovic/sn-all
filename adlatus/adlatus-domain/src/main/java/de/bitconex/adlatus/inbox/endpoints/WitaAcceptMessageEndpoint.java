package de.bitconex.adlatus.inbox.endpoints;

import de.bitconex.adlatus.inbox.service.WitaInboxService;
import de.bitconex.adlatus.wholebuy.provision.service.wita.WitaUtil;
import de.bitconex.adlatus.inbox.service.WitaValidator;
import de.telekom.wholesale.oss.v15.complex.MeldungspositionOhneAttributeType;
import de.telekom.wholesale.oss.v15.complex.MessageTEQType;
import de.telekom.wholesale.oss.v15.envelope.AnnehmenMeldungRequestType;
import de.telekom.wholesale.oss.v15.envelope.AnnehmenMeldungResponseType;
import de.telekom.wholesale.oss.v15.envelope.ObjectFactory;
import de.telekom.wholesale.oss.v15.envelope.WholesaleResponseType;
import jakarta.xml.bind.JAXBElement;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

@Endpoint
public class WitaAcceptMessageEndpoint {

    private static final String NAMESPACE_URI = "http://wholesale.telekom.de/oss/v15/envelope";

    private static final ObjectFactory factory = new ObjectFactory();

    private final WitaInboxService witaInboxService;

    private final WitaValidator witaValidator;

    public WitaAcceptMessageEndpoint(WitaInboxService witaService, WitaValidator witaValidator) {
        this.witaInboxService = witaService;
        this.witaValidator = witaValidator;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "annehmenMeldungRequest")
    @ResponsePayload
    public JAXBElement<AnnehmenMeldungResponseType> acceptMessage(@RequestPayload JAXBElement<AnnehmenMeldungRequestType> request) {
        witaValidator.validate(request.getValue());
        witaInboxService.save(request.getValue());

        return factory.createAnnehmenMeldungResponse(createResponse(request.getValue()));
    }

    private AnnehmenMeldungResponseType createResponse(AnnehmenMeldungRequestType request) {
        AnnehmenMeldungResponseType response = factory.createAnnehmenMeldungResponseType();

        WholesaleResponseType control = new WholesaleResponseType();
        control.setMajorRelease(request.getControl().getMajorRelease());
        control.setMinorRelease(request.getControl().getMinorRelease());
        control.setSignaturId(request.getControl().getSignaturId());
        response.setControl(control);

        String externalOrderId = WitaUtil.extractExternalOrderId(request.getMeldung().getMeldungstyp());
        MessageTEQType receipt = new MessageTEQType();
        receipt.setExterneAuftragsnummer(externalOrderId);
        receipt.setKundenNummer("resolve customer number");

        MessageTEQType.Meldungspositionen positions = new MessageTEQType.Meldungspositionen();
        MeldungspositionOhneAttributeType attribute = new MeldungspositionOhneAttributeType();
        attribute.setMeldungscode("OK");
        attribute.setMeldungstext("OK.");
        positions.getPosition().add(attribute);

        receipt.setMeldungspositionen(positions);
        response.setQuittung(receipt);

        return response;
    }
}
