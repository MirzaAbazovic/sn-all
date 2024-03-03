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

import de.mnet.wita.message.auftrag.StandortKollokation;
import de.mnet.wita.message.auftrag.geschaeftsfall.Geschaeftsfall;
import de.mnet.wita.validators.BasePresenceValidator;
import de.mnet.wita.validators.geschaeftsfall.StandortKollokationSet.StandortKollokationSetValidator;

@Target({ TYPE })
@Retention(RUNTIME)
@Constraint(validatedBy = StandortKollokationSetValidator.class)
@Documented
public @interface StandortKollokationSet {
    String message() default "";

    boolean mandatory() default true;

    boolean permitted() default true;

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };

    public static class StandortKollokationSetValidator extends BasePresenceValidator implements
            ConstraintValidator<StandortKollokationSet, Geschaeftsfall> {

        @Override
        public boolean isValid(Geschaeftsfall geschaeftsfall, ConstraintValidatorContext context) {
            StandortKollokation standortKollokation = geschaeftsfall.getAuftragsPosition().getGeschaeftsfallProdukt()
                    .getStandortKollokation();
            return checkPresence(context, standortKollokation);
        }

        @Override
        public void initialize(StandortKollokationSet constraintAnnotation) {
            permitted = constraintAnnotation.permitted();
            mandatory = constraintAnnotation.mandatory();
            errorMsgIfRequired = "Der Kollokationsstandort muss gesetzt sein.";
            errorMsgIfNotAllowed = "Der Kollokationsstandort darf nicht gesetzt sein.";
        }
    }

}
