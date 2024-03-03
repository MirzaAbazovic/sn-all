/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 27.07.2011
 */
package de.mnet.wita.validators;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

import java.lang.annotation.*;
import java.util.*;
import javax.validation.*;
import org.apache.commons.lang.StringUtils;

import de.mnet.wita.message.auftrag.Vormieter;
import de.mnet.wita.validators.VormieterValid.VormieterValidator;

/**
 * Validator Annotation fuer {@link Vormieter}
 */
@Target({ TYPE })
@Retention(RUNTIME)
@Constraint(validatedBy = VormieterValidator.class)
@Documented
public @interface VormieterValid {

    String DEFAULT_MESSAGE = "{validator.vormieter.valid}";

    String message() default DEFAULT_MESSAGE;

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };

    class VormieterValidator implements ConstraintValidator<VormieterValid, Vormieter> {

        @Override
        public void initialize(VormieterValid constraintAnnotation) {
            // nothing to do
        }

        @Override
        public boolean isValid(Vormieter vormieter, ConstraintValidatorContext context) {
            List<String> errorMessages = new ArrayList<String>();

            boolean isPerson = StringUtils.isNotBlank(vormieter.getVorname())
                    || StringUtils.isNotBlank(vormieter.getNachname());
            boolean isAnschluss = StringUtils.isNotBlank(vormieter.getOnkz())
                    || StringUtils.isNotBlank(vormieter.getRufnummer());
            boolean isUfa = StringUtils.isNotBlank(vormieter.getUfaNummer());

            if (isPerson || isAnschluss || isUfa) {
                if (isPerson && StringUtils.isBlank(vormieter.getNachname())) {
                    errorMessages.add("Nachname muss ebenfalls gesetzt sein!");
                }

                if (isAnschluss
                        && (StringUtils.isBlank(vormieter.getOnkz()) || StringUtils.isBlank(vormieter.getRufnummer()))) {
                    errorMessages.add("ONKZ und Rufnummer müssen beide gesetzt oder leer sein!");
                }
            }
            else {
                errorMessages.add("Vormieter muss entweder Personen-, Anschluss- oder UFA-Informationen enthalten!");
            }

            if (!errorMessages.isEmpty()) {
                context.disableDefaultConstraintViolation();
                for (String errorMsg : errorMessages) {
                    context.buildConstraintViolationWithTemplate(errorMsg).addConstraintViolation();
                }
                return false;
            }
            return true;
        }
    }
}
