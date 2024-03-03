package de.bitconex.adlatus.wholebuy.provision.service.wita;

import de.telekom.wholesale.oss.v15.message.*;
import org.springframework.stereotype.Component;

@Component
public class WitaMessageHandlerResolver {
    private final ABMHandler abmHandler;
    private final QEBHandler qebHandler;
    private final ERLMHandler erlmHandler;
    private final ENTMHandler entmHandler;

    public WitaMessageHandlerResolver(ABMHandler abmHandler, QEBHandler qebHandler, ERLMHandler erlmHandler, ENTMHandler entmHandler) {
        this.abmHandler = abmHandler;
        this.qebHandler = qebHandler;
        this.erlmHandler = erlmHandler;
        this.entmHandler = entmHandler;
    }

    public WitaMessageHandler getMessageHandler(MeldungstypAbstractType messageType) {
        return switch (messageType) {
            case MeldungstypQEBType ignored -> qebHandler;
            case MeldungstypABMType ignored -> abmHandler;
            case MeldungstypERLMType ignored -> erlmHandler;
            case MeldungstypENTMType ignored -> entmHandler;
            default -> throw new IllegalArgumentException("Unknown message type: " + messageType);
        };
    }
}
