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

import de.mnet.wita.message.auftrag.geschaeftsfall.Geschaeftsfall;
import de.mnet.wita.message.common.LeitungsBezeichnung;
import de.mnet.wita.validators.BasePresenceValidator;
import de.mnet.wita.validators.geschaeftsfall.LeitungsBezeichnungSet.LeitungsBezeichnungSetValidator;

@Target({ TYPE })
@Retention(RUNTIME)
@Constraint(validatedBy = LeitungsBezeichnungSetValidator.class)
@Documented
public @interface LeitungsBezeichnungSet {
    String message() default "";

    boolean mandatory() default true;

    boolean permitted() default true;

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };

    public static class LeitungsBezeichnungSetValidator extends BasePresenceValidator implements
            ConstraintValidator<LeitungsBezeichnungSet, Geschaeftsfall> {

        @Override
        public boolean isValid(Geschaeftsfall geschaeftsfall, ConstraintValidatorContext context) {
            LeitungsBezeichnung leitungsBezeichnung = geschaeftsfall.getAuftragsPosition().getGeschaeftsfallProdukt()
                    .getLeitungsBezeichnung();
            return checkPresence(context, leitungsBezeichnung);
        }

        @Override
        public void initialize(LeitungsBezeichnungSet constraintAnnotation) {
            permitted = constraintAnnotation.permitted();
            mandatory = constraintAnnotation.mandatory();
            errorMsgIfRequired = "Die Leitungsbezeichnung muss gesetzt sein.";
            errorMsgIfNotAllowed = "Die Leitungsbezeichnung darf nicht gesetzt sein.";
        }
    }

}
