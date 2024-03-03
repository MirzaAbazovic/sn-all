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

import de.mnet.wita.message.auftrag.BestandsSuche;
import de.mnet.wita.message.auftrag.geschaeftsfall.Geschaeftsfall;
import de.mnet.wita.validators.BasePresenceValidator;
import de.mnet.wita.validators.geschaeftsfall.BestandsSucheSet.BestandsSucheSetValidator;

@Target({ TYPE })
@Retention(RUNTIME)
@Constraint(validatedBy = BestandsSucheSetValidator.class)
@Documented
public @interface BestandsSucheSet {

    String message() default "";

    boolean mandatory() default true;

    boolean permitted() default true;

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };

    public static class BestandsSucheSetValidator extends BasePresenceValidator implements
            ConstraintValidator<BestandsSucheSet, Geschaeftsfall> {

        @Override
        public boolean isValid(Geschaeftsfall geschaeftsfall, ConstraintValidatorContext context) {
            BestandsSuche bestandsSuche = geschaeftsfall.getAuftragsPosition().getGeschaeftsfallProdukt()
                    .getBestandsSuche();
            return checkPresence(context, bestandsSuche);
        }

        @Override
        public void initialize(BestandsSucheSet constraintAnnotation) {
            permitted = constraintAnnotation.permitted();
            mandatory = constraintAnnotation.mandatory();
            errorMsgIfRequired = "Bestandssuche muss gesetzt sein.";
            errorMsgIfNotAllowed = "Bestandssuche darf nicht gesetzt sein";
        }
    }
}
