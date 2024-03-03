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
import org.apache.commons.lang.StringUtils;

import de.mnet.wita.message.GeschaeftsfallTyp;
import de.mnet.wita.message.auftrag.geschaeftsfall.Geschaeftsfall;
import de.mnet.wita.message.common.portierung.RufnummernPortierung;
import de.mnet.wita.validators.BasePresenceValidator;
import de.mnet.wita.validators.ValidationUtils;
import de.mnet.wita.validators.geschaeftsfall.RufnummernPortierungSetForAuftrag.RufnummernPortierungSetForAuftragValidator;

@Target({ TYPE })
@Retention(RUNTIME)
@Constraint(validatedBy = RufnummernPortierungSetForAuftragValidator.class)
@Documented
public @interface RufnummernPortierungSetForAuftrag {

    String message() default "";

    boolean mandatory() default true;

    boolean permitted() default true;

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };

    public static class RufnummernPortierungSetForAuftragValidator extends BasePresenceValidator implements
            ConstraintValidator<RufnummernPortierungSetForAuftrag, Geschaeftsfall> {

        @Override
        public boolean isValid(Geschaeftsfall geschaeftsfall, ConstraintValidatorContext context) {
            RufnummernPortierung portierung = geschaeftsfall.getAuftragsPosition().getGeschaeftsfallProdukt()
                    .getRufnummernPortierung();

            if ((portierung != null) && (portierung.getPortierungsKenner() == null)) {
                ValidationUtils.addConstraintViolation(context, "PortierungsKenner muss an der RufnummernPortierung gesetzt sein!");
            }
            if (geschaeftsfall.getGeschaeftsfallTyp().equals(GeschaeftsfallTyp.RUFNUMMERNEXPORT_ANSCHLUSSKUENDIGUNG) && (portierung == null)) {
                ValidationUtils.addConstraintViolation(context, "Keine Rufnummern im Portierungsmodus 'Kommend' gefunden oder das Vorgabedatum stimmt nicht mit dem Datum aus Taifun überein!");
                return false;
            }
            return checkPresence(context, portierung);
        }

        @Override
        public void initialize(RufnummernPortierungSetForAuftrag constraintAnnotation) {
            permitted = constraintAnnotation.permitted();
            mandatory = constraintAnnotation.mandatory();
            String message = constraintAnnotation.message();
            if (StringUtils.isNotBlank(message)) {
                errorMsgIfRequired = message;
                errorMsgIfNotAllowed = message;
            }
            else {
                errorMsgIfRequired = "Die RufnummernPortierung muss gesetzt sein.";
                errorMsgIfNotAllowed = "Die RufnummernPortierung darf nicht gesetzt sein";
            }
        }
    }


}
