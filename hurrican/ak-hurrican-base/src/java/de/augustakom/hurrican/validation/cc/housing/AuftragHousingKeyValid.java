/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 18.07.2011 13:16:24
 */
package de.augustakom.hurrican.validation.cc.housing;

import static java.lang.annotation.RetentionPolicy.*;

import java.lang.annotation.*;
import javax.validation.*;

import de.augustakom.hurrican.model.cc.AuftragHousingKey;
import de.augustakom.hurrican.validation.cc.housing.AuftragHousingKeyValid.AuftragHousingKeyValidator;


@Retention(RUNTIME)
@Target({ ElementType.TYPE })
@Constraint(validatedBy = AuftragHousingKeyValidator.class)
@Documented
public @interface AuftragHousingKeyValid {

    String message() default "Es muss eine Transponder-ID oder(!) eine Transponder-Gruppe gesetzt sein.";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };

    public static class AuftragHousingKeyValidator implements
            ConstraintValidator<AuftragHousingKeyValid, AuftragHousingKey> {

        @Override
        public boolean isValid(AuftragHousingKey auftragHousingKey, ConstraintValidatorContext context) {
            if (auftragHousingKey == null) {
                return true;
            }

            if ((auftragHousingKey.getTransponder() == null) && (auftragHousingKey.getTransponderGroup() == null)) {
                return false;
            }
            else if ((auftragHousingKey.getTransponder() != null) && (auftragHousingKey.getTransponderGroup() != null)) {
                return false;
            }
            return true;
        }

        @Override
        public void initialize(AuftragHousingKeyValid constraintAnnotation) {
            // nothing to do
        }

    }

}
