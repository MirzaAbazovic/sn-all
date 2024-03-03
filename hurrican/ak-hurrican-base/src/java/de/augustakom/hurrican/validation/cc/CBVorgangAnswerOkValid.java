/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 15.07.2011 09:36:34
 */
package de.augustakom.hurrican.validation.cc;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

import java.lang.annotation.*;
import javax.validation.*;
import org.apache.commons.lang.StringUtils;

import de.augustakom.common.tools.lang.BooleanTools;
import de.augustakom.common.tools.lang.NumberTools;
import de.augustakom.hurrican.model.cc.Carrier;
import de.augustakom.hurrican.model.cc.tal.CBVorgang;
import de.augustakom.hurrican.validation.cc.CBVorgangAnswerOkValid.CBVorgangAnswerOkValidator;

@Target({ TYPE })
@Retention(RUNTIME)
@Constraint(validatedBy = CBVorgangAnswerOkValidator.class)
@Documented
public @interface CBVorgangAnswerOkValid {

    String message() default "Bei positiven Rueckmeldungen muss das Bereitstellungsdatum und (abhaengig vom Carrier) die Leitungsbezeichnung gesetzt sein!";

    boolean present() default true;

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };

    public static class CBVorgangAnswerOkValidator implements ConstraintValidator<CBVorgangAnswerOkValid, CBVorgang> {

        @Override
        public boolean isValid(CBVorgang cbVorgang, ConstraintValidatorContext context) {
            if (CBVorgangValidationHelper.isAnswerState(cbVorgang)
                    && !cbVorgang.isStorno()
                    && BooleanTools.nullToFalse(cbVorgang.getReturnOk())
                    && CBVorgang.BESTELL_TYPEN.contains(cbVorgang.getTyp())) {

                if (cbVorgang.getReturnRealDate() == null) {
                    return false;
                }

                // Bei Bestellungen an andere Carrier (ausser Akom/Mnet/NefKom) muss bei
                // einer positivien Rueckmeldung eine LBZ angegeben sein!
                if (!Carrier.isMNetCarrier(cbVorgang.getCarrierId()) && StringUtils.isBlank(cbVorgang.getReturnLBZ())) {
                    return false;
                }

            }
            return true;
        }

        @Override
        public void initialize(CBVorgangAnswerOkValid constraintAnnotation) {
        }

    }

}
