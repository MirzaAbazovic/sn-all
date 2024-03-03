/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 24.10.13
 */
package de.mnet.wbci.validation.constraints;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

import java.lang.annotation.*;
import javax.validation.*;
import javax.validation.constraints.*;

import de.mnet.wbci.model.MeldungPositionRueckmeldungVa;
import de.mnet.wbci.model.MeldungsCode;
import de.mnet.wbci.model.helper.StandortHelper;
import de.mnet.wbci.validation.helper.ValidationHelper;

/**
 * Checks if the {@link MeldungPositionRueckmeldungVa} contains ADA-{@link MeldungsCode}s.
 */
@Target(value = TYPE)
@Retention(RUNTIME)
@Constraint(validatedBy = CheckRuemVaMeldungscodeADA.MeldungscodesValidator.class)
@Documented
public @interface CheckRuemVaMeldungscodeADA {

    String message() default "Die abweichende Adresse ist nicht gültig, da bei Meldungscode %s - %s nur das Attribut '%s' in der abweichenden Adresse gesetzt werden darf";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };

    public class MeldungscodesValidator implements
            ConstraintValidator<CheckRuemVaMeldungscodeADA, MeldungPositionRueckmeldungVa> {

        protected String defaultMessage;

        @Override
        public void initialize(CheckRuemVaMeldungscodeADA constraintAnnotation) {
            this.defaultMessage = constraintAnnotation.message();
        }

        @Override
        public boolean isValid(@NotNull MeldungPositionRueckmeldungVa ruemVaPos, ConstraintValidatorContext context) {
            if (ruemVaPos.getMeldungsCode() != null) {

                MeldungsCode code = ruemVaPos.getMeldungsCode();
                if (!StandortHelper.isStandortValidForMeldungscode(ruemVaPos.getStandortAbweichend(), code)) {
                    ValidationHelper.addConstraintViolation(context, String.format(defaultMessage,
                            code.getCode(),
                            code.getStandardText(),
                            getStandortAttribute(code)
                    ));
                    return false;
                }
            }
            return true;
        }

        private String getStandortAttribute(MeldungsCode code) {
            switch (code) {
                case ADAORT:
                    return "Ort";
                case ADAPLZ:
                    return "PLZ";
                case ADASTR:
                    return "Strassenname";
                case ADAHSNR:
                    return "Hausnummer";
                default:
                    return null;
            }
        }
    }
}
