/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 27.07.2011
 */
package de.mnet.wita.validators.geschaeftsfall;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

import java.lang.annotation.*;
import javax.validation.*;

import de.mnet.wita.message.auftrag.Auftragsposition;
import de.mnet.wita.message.auftrag.Vormieter;
import de.mnet.wita.message.auftrag.geschaeftsfall.Geschaeftsfall;
import de.mnet.wita.validators.BasePresenceValidator;
import de.mnet.wita.validators.geschaeftsfall.VormieterSet.VormieterSetValidator;

@Target({ TYPE })
@Retention(RUNTIME)
@Constraint(validatedBy = VormieterSetValidator.class)
@Documented
public @interface VormieterSet {
    String message() default "";

    boolean mandatory() default true;

    boolean permitted() default true;

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };

    public static class VormieterSetValidator extends BasePresenceValidator implements
            ConstraintValidator<VormieterSet, Geschaeftsfall> {

        @Override
        public void initialize(VormieterSet constraintAnnotation) {
            permitted = constraintAnnotation.permitted();
            mandatory = constraintAnnotation.mandatory();
            errorMsgIfRequired = "Der Vormieter muss gesetzt sein.";
            errorMsgIfNotAllowed = "Der Vormieter darf nicht gesetzt sein";
        }

        @Override
        public boolean isValid(Geschaeftsfall geschaeftsfall, ConstraintValidatorContext context) {
            Vormieter vormieter = geschaeftsfall.getAuftragsPosition().getGeschaeftsfallProdukt().getVormieter();
            return subAuftragspositionIsValid(geschaeftsfall, context) && checkPresence(context, vormieter);
        }

        private boolean subAuftragspositionIsValid(Geschaeftsfall geschaeftsfall, ConstraintValidatorContext context) {
            Auftragsposition subAuftragPosition = geschaeftsfall.getAuftragsPosition().getPosition();
            if (subAuftragPosition != null) {
                Vormieter subVormieter = subAuftragPosition.getGeschaeftsfallProdukt().getVormieter();
                return checkPresence(context, subVormieter);
            }
            return true;
        }
    }

}
