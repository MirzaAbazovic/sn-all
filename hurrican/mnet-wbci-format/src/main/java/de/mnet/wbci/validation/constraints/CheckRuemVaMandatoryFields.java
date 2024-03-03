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
import java.util.*;

import javax.validation.*;
import javax.validation.constraints.*;

import de.augustakom.common.tools.collections.CollectionTools;
import de.mnet.wbci.model.GeschaeftsfallTyp;
import de.mnet.wbci.model.RueckmeldungVorabstimmung;
import de.mnet.wbci.validation.helper.ValidationHelper;

/**
 * Checks if the fields "Technische Ressource" and "Technologie" have been set correctly according to the GF: {@link
 * GeschaeftsfallTyp#VA_KUE_MRN}, {@link GeschaeftsfallTyp#VA_KUE_ORN} or {@link GeschaeftsfallTyp#VA_RRNP}
 */
@Target(value = TYPE)
@Retention(RUNTIME)
@Constraint(validatedBy = CheckRuemVaMandatoryFields.MandatoryFieldsValidator.class)
@Documented
public @interface CheckRuemVaMandatoryFields {

    String message() default "Die Validierung der Attribute für die Vorabstimmungsantwort (RUEM-VA) beim Geschäftsfall %s ist fehlgeschlagen:%s";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };

    public class MandatoryFieldsValidator implements
            ConstraintValidator<CheckRuemVaMandatoryFields, RueckmeldungVorabstimmung> {

        protected String defaultMessage;
        protected List<String> fieldsToBeNotSet;
        protected List<String> fieldsToBeSet;

        @Override
        public void initialize(CheckRuemVaMandatoryFields constraintAnnotation) {
            this.defaultMessage = constraintAnnotation.message();
        }

        @Override
        public boolean isValid(@NotNull RueckmeldungVorabstimmung ruemVa, ConstraintValidatorContext context) {
            if (ruemVa.getWbciGeschaeftsfall() != null) {
                fieldsToBeSet = new ArrayList<>();
                fieldsToBeNotSet = new ArrayList<>();
                switch (ruemVa.getWbciGeschaeftsfall().getTyp()) {
                    case VA_KUE_MRN:
                        checkTechnologieSet(ruemVa);
                        checkRufnummernportierungSet(ruemVa);
                        break;
                    case VA_KUE_ORN:
                        checkTechnologieSet(ruemVa);
                        checkRufnummernportierungNotSet(ruemVa);
                        break;
                    case VA_RRNP:
                        checkTechnologieNotSet(ruemVa);
                        checkTechnischeRessourceNotSet(ruemVa);
                        checkRufnummernportierungSet(ruemVa);
                        break;
                    default:
                        break;
                }

                if (!fieldsToBeNotSet.isEmpty() || !fieldsToBeSet.isEmpty()) {
                    ValidationHelper.addConstraintViolation(context,
                            String.format(defaultMessage,
                                    ruemVa.getWbciGeschaeftsfall().getTyp(),
                                    getViolatedFieldMessage())
                    );
                    return false;
                }

            }
            return true;
        }

        protected String getViolatedFieldMessage() {
            StringBuilder sb = new StringBuilder();
            if (!fieldsToBeNotSet.isEmpty()) {
                sb.append("\n\t> Die Attribute ")
                        .append(fieldsToBeNotSet)
                        .append(" dürfen nicht gesetzt sein.");
            }
            if (!fieldsToBeSet.isEmpty()) {
                sb.append("\n\t> Die Attribute ")
                        .append(fieldsToBeSet)
                        .append(" sind Pflichtfelder und müssen gesetzt sein.");
            }
            return sb.toString();
        }

        private void checkTechnologieSet(RueckmeldungVorabstimmung ruemVa) {
            if (ruemVa.getTechnologie() == null) {
                fieldsToBeSet.add("Technologie");
            }
        }

        private void checkRufnummernportierungSet(RueckmeldungVorabstimmung ruemVa) {
            if (ruemVa.getRufnummernportierung() == null) {
                fieldsToBeSet.add("Rufnummernportierung");
            }
        }

        private void checkTechnischeRessourceNotSet(RueckmeldungVorabstimmung ruemVa) {
            if (!CollectionTools.isEmpty(ruemVa.getTechnischeRessourcen())) {
                fieldsToBeNotSet.add("technische Ressourcen");
            }
        }

        private void checkTechnologieNotSet(RueckmeldungVorabstimmung ruemVa) {
            if (ruemVa.getTechnologie() != null) {
                fieldsToBeNotSet.add("Technologie");
            }
        }

        private void checkRufnummernportierungNotSet(RueckmeldungVorabstimmung ruemVa) {
            if (ruemVa.getRufnummernportierung() != null) {
                fieldsToBeNotSet.add("Rufnummernportierung");
            }
        }
    }

}
