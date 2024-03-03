/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 07.10.2011
 */
package de.augustakom.hurrican.validation.cc;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

import java.lang.annotation.*;
import java.util.*;
import javax.validation.*;
import org.apache.commons.lang.StringUtils;

import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.hurrican.model.cc.CarrierbestellungVormieter;
import de.augustakom.hurrican.validation.cc.CarrierbestellungVormieterValid.CarrierbestellungVormieterValidator;

/**
 * Validator Annotation fuer {@link CarrierbestellungVormieter}
 */
@Target({ TYPE })
@Retention(RUNTIME)
@Constraint(validatedBy = CarrierbestellungVormieterValidator.class)
@Documented
public @interface CarrierbestellungVormieterValid {

    String message() default "{validator.carrierbestellung.vormieter.valid}";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };

    public class CarrierbestellungVormieterValidator implements
            ConstraintValidator<CarrierbestellungVormieterValid, CarrierbestellungVormieter> {

        @Override
        public void initialize(CarrierbestellungVormieterValid constraintAnnotation) {
            // nothing to do
        }

        @Override
        public boolean isValid(CarrierbestellungVormieter vormieter, ConstraintValidatorContext context) {

            boolean isPerson = StringUtils.isNotBlank(vormieter.getVorname())
                    || StringUtils.isNotBlank(vormieter.getNachname());
            boolean isAnschluss = StringUtils.isNotBlank(vormieter.getOnkz())
                    || StringUtils.isNotBlank(vormieter.getRufnummer());
            boolean isUfa = StringUtils.isNotBlank(vormieter.getUfaNummer());

            if (isPerson || isAnschluss || isUfa) {

                List<String> errorMessages = new ArrayList<String>();
                if (isPerson && StringUtils.isBlank(vormieter.getNachname())) {
                    errorMessages.add("Nachname muss ebenfalls gesetzt sein!");
                }

                if (isAnschluss
                        && (StringUtils.isBlank(vormieter.getOnkz()) || StringUtils.isBlank(vormieter.getRufnummer()))) {
                    errorMessages.add("ONKZ und Rufnummer müssen beide gesetzt oder leer sein!");
                }

                if (CollectionTools.isNotEmpty(errorMessages)) {
                    context.disableDefaultConstraintViolation();
                    for (String errorMsg : errorMessages) {
                        context.buildConstraintViolationWithTemplate(errorMsg).addConstraintViolation();
                    }
                    return false;
                }
                return true;
            }

            return false;
        }
    }
}
