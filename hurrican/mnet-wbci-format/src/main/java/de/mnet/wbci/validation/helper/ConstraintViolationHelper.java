/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 14.11.13
 */
package de.mnet.wbci.validation.helper;

import java.util.*;
import javax.validation.*;
import org.springframework.stereotype.Component;

import de.mnet.wbci.model.WbciMessage;

/**
 * Helper for Formating the {@link ConstraintViolation} Sets. Should be not static, to be able to mock this Helper.
 *
 *
 */
@Component
public class ConstraintViolationHelper {

    /**
     * Converts constraint violations, typically detected in an outbound message generated via the GUI or automatically,
     * into a readable error message format.
     *
     * @param constraintViolations
     * @param <M>
     * @return
     */
    public <M extends WbciMessage> String generateWarningMsg(Set<ConstraintViolation<M>> constraintViolations) {
        String suffix = "Soll die %s trotzdem verarbeitet werden?";
        return convertToWarningMsg(constraintViolations, suffix);
    }

    /**
     * Converts constraint violations, originating from an inbound message (i.e. sent from EKP to M-Net), into a
     * readable error message format. This is typically used when the list of violarions are added as a Bemerkung.
     *
     * @param constraintViolations
     * @param <M>
     * @return
     */
    public <M extends WbciMessage> String generateWarningForInboundMsg(Set<ConstraintViolation<M>> constraintViolations) {
        return convertToWarningMsg(constraintViolations, null);
    }

    private <M extends WbciMessage> String convertToWarningMsg(Set<ConstraintViolation<M>> constraintViolations, String suffix) {
        Enum type = constraintViolations.iterator().next().getRootBean().getTyp();
        String prefix = String.format(
                "Achtung, die %s enthält Warnungen. Die folgenden Warnungen sind aufgetreten:%n", type.toString());
        return violationsToString(prefix, constraintViolations, suffix == null ? "" : String.format(suffix, type.toString()));
    }

    /**
     * Converts constraint violations, typically detected in an outbound message generated via the GUI or automatically,
     * into a readable error message format.
     *
     * @param constraintViolations
     * @param <M>
     * @return
     */
    public <M extends WbciMessage> String generateErrorMsg(Set<ConstraintViolation<M>> constraintViolations) {
        final String errorMsgPrefix = "Die %s ist nicht vollständig. Die folgenden Fehler müssen zuerst korrigiert werden:%n";
        return convertToErrorMsg(errorMsgPrefix, constraintViolations);
    }

    /**
     * Converts constraint violations, originating from an inbound message (i.e. sent from EKP to M-Net), into a
     * readable error message format. This is typically used when the geschaeftsfall is marked as a klaerfall and the
     * list of violarions are added as a Bemerkung.
     *
     * @param constraintViolations
     * @param <M>
     * @return
     */
    public <M extends WbciMessage> String generateErrorMsgForInboundMsg(Set<ConstraintViolation<M>> constraintViolations) {
        final String errorMsgPrefix = "Die folgenden Fehler in der eingehenden %s müssen analysiert werden:%n";
        return convertToErrorMsg(errorMsgPrefix, constraintViolations);
    }

    private <M extends WbciMessage> String convertToErrorMsg(String prefix, Set<ConstraintViolation<M>> constraintViolations) {
        String beanType = constraintViolations.iterator().next().getRootBean().getTyp().toString();
        return violationsToString(String.format(prefix, beanType), constraintViolations, "");
    }

    public <M extends WbciMessage> String violationsToString(String prefix, Set<ConstraintViolation<M>> violations,
            String suffix) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(prefix);
        for (ConstraintViolation<M> violation : violations) {
            String property = getLastPropertyInPathOrClass(violation);
            stringBuilder.append(String.format("- %s: %s!%n", property, violation.getMessage()));
        }
        stringBuilder.append(suffix);
        return stringBuilder.toString();
    }

    private <M extends WbciMessage> String getLastPropertyInPathOrClass(ConstraintViolation<M> violation) {
        String property = getLastPropertyInPath(violation.getPropertyPath());
        if (property != null) {
            return property;
        }
        return violation.getRootBeanClass().getSimpleName();
    }

    private String getLastPropertyInPath(Path path) {
        Iterator<Path.Node> iterator = path.iterator();
        Path.Node node = null;
        while (iterator.hasNext()) {
            node = iterator.next();
        }
        return node.getName();
    }

}
