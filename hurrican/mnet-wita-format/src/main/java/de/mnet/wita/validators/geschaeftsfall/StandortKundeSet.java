/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 15.07.2011 09:36:34
 */
package de.mnet.wita.validators.geschaeftsfall;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

import java.lang.annotation.*;
import javax.validation.*;

import de.mnet.wita.message.auftrag.GeschaeftsfallProdukt;
import de.mnet.wita.message.auftrag.geschaeftsfall.Geschaeftsfall;
import de.mnet.wita.validators.BasePresenceValidator;
import de.mnet.wita.validators.geschaeftsfall.StandortKundeSet.StandortKundeSetValidator;

@Target({ TYPE })
@Retention(RUNTIME)
@Constraint(validatedBy = StandortKundeSetValidator.class)
@Documented
public @interface StandortKundeSet {
    String message() default "";

    boolean mandatory() default true;

    boolean permitted() default true;

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };

    public static class StandortKundeSetValidator extends BasePresenceValidator implements
            ConstraintValidator<StandortKundeSet, Geschaeftsfall> {

        @Override
        public boolean isValid(Geschaeftsfall geschaeftsfall, ConstraintValidatorContext context) {
            GeschaeftsfallProdukt gfProdukt = geschaeftsfall.getAuftragsPosition().getGeschaeftsfallProdukt();
            return checkPresence(context, gfProdukt.getStandortKunde());
        }

        @Override
        public void initialize(StandortKundeSet constraintAnnotation) {
            permitted = constraintAnnotation.permitted();
            mandatory = constraintAnnotation.mandatory();
            errorMsgIfRequired = "Der Kundenstandort muss gesetzt sein.";
            errorMsgIfNotAllowed = "Der Kundenstandort darf nicht gesetzt sein.";
        }
    }

}
