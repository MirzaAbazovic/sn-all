/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 15.07.2011 09:36:34
 */
package de.mnet.wita.model.validators;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

import java.lang.annotation.*;
import javax.validation.*;
import org.apache.commons.lang.StringUtils;

import de.mnet.wita.model.WitaCBVorgang;
import de.mnet.wita.model.validators.MontagehinweisValid.MontagehinweisValidator;

@Target({ TYPE })
@Retention(RUNTIME)
@Constraint(validatedBy = MontagehinweisValidator.class)
@Documented
public @interface MontagehinweisValid {

    String message() default "Der Montagehinweis darf bei Wita-Bestellungen maximal 160 Zeichen lang sein.";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };

    public static class MontagehinweisValidator implements ConstraintValidator<MontagehinweisValid, WitaCBVorgang> {

        @Override
        public boolean isValid(WitaCBVorgang witaCbVorgang, ConstraintValidatorContext context) {
            return StringUtils.isBlank(witaCbVorgang.getMontagehinweis())
                    || (witaCbVorgang.getMontagehinweis().length() <= 160);
        }

        @Override
        public void initialize(MontagehinweisValid constraintAnnotation) {
            // not used
        }

    }

}
