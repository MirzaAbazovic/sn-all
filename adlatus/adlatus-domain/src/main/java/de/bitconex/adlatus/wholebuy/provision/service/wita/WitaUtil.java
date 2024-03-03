package de.bitconex.adlatus.wholebuy.provision.service.wita;

import de.bitconex.adlatus.wholebuy.provision.workflow.variables.Variables;
import de.telekom.wholesale.oss.v15.complex.AnsprechpartnerType;
import de.telekom.wholesale.oss.v15.ftth.MeldungsattributeFTTHABMType;
import de.telekom.wholesale.oss.v15.message.*;
import de.telekom.wholesale.oss.v15.ng.fttb.MeldungsattributeFTTBABMType;
import de.telekom.wholesale.oss.v15.xdsl.MeldungsattributeXDSLABMType;
import lombok.experimental.UtilityClass;

import java.util.Map;
import java.util.function.Function;

@UtilityClass
public class WitaUtil {
    private final static Map<String, Function<MeldungstypAbstractType, Object>> variablesMap = Map.of(
        Variables.EXTERNAL_ORDER_NUMBER.getVariableName(), WitaUtil::extractExternalOrderId,
        Variables.CONTACT_PERSON.getVariableName(), (message) -> WitaUtil.extractContactPerson((MeldungstypQEBType) message),
        Variables.LINE_ID.getVariableName(), (message) -> WitaUtil.extractLineId((MeldungstypABMType) message),
        Variables.BINDING_DELIVERY_DATE.getVariableName(), (message) -> WitaUtil.extractBindingDeliveryDate((MeldungstypABMType) message),
        Variables.PAYMENT_DATE.getVariableName(), (message) -> WitaUtil.extractPaymentDate((MeldungstypENTMType) message),
        Variables.COMPLETION_DATE.getVariableName(), (message) -> WitaUtil.extractCompletionDate((MeldungstypERLMType) message)
    );

    public static Object extractVariable(MeldungstypAbstractType message, String variableName) {
        return variablesMap.get(variableName).apply(message);
    }

    public static String extractExternalOrderId(MeldungstypAbstractType message) {
        return switch (message) {
            case MeldungstypQEBType messageQEB -> messageQEB.getMeldungsattribute().getExterneAuftragsnummer();
            case MeldungstypABMType messageABM -> messageABM.getMeldungsattribute().getExterneAuftragsnummer();
            case MeldungstypENTMType messageENTM -> messageENTM.getMeldungsattribute().getExterneAuftragsnummer();
            case MeldungstypERLMType messageERLM -> messageERLM.getMeldungsattribute().getExterneAuftragsnummer();
            case null, default -> throw new IllegalStateException("Invalid WITA Message to extract external order id");
        };
    }

    public static String extractLineId(MeldungstypABMType messageABM) {
        return switch (messageABM.getMeldungsattribute()) {
            case MeldungsattributeXDSLABMType messageXDSLABM -> messageXDSLABM.getLineID();
            case MeldungsattributeFTTBABMType messageFTTABM -> messageFTTABM.getLineID();
            case MeldungsattributeFTTHABMType messageFTTHABM -> messageFTTHABM.getLineID();
            case null, default ->
                throw new IllegalStateException("Unexpected value: " + messageABM.getMeldungsattribute());
        };
    }

    public static AnsprechpartnerType extractContactPerson(MeldungstypQEBType messageQEB) {
        MeldungspositionsattributeQEBType positionAttribute = (MeldungspositionsattributeQEBType) messageQEB.getMeldungspositionen().getPosition().get(0).getPositionsattribute();
        return positionAttribute.getAnsprechpartnerTelekom();
    }

    public static String extractBindingDeliveryDate(MeldungstypABMType message) {
        return message.getMeldungsattribute().getVerbindlicherLiefertermin().toString();
    }

    public static String extractPaymentDate(MeldungstypENTMType message) {
        return message.getMeldungsattribute().getEntgelttermin().toString();
    }

    public static String extractCompletionDate(MeldungstypERLMType message) {
        return message.getMeldungsattribute().getErledigungstermin().toString();
    }
}
