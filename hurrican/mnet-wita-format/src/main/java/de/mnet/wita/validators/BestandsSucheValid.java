/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 15.07.2011 09:36:34
 */
package de.mnet.wita.validators;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

import java.lang.annotation.*;
import javax.validation.*;

import de.mnet.wita.message.auftrag.BestandsSuche;
import de.mnet.wita.message.auftrag.geschaeftsfall.Geschaeftsfall;
import de.mnet.wita.validators.BestandsSucheValid.BestandsSucheSetValidator;

@Target({ TYPE })
@Retention(RUNTIME)
@Constraint(validatedBy = BestandsSucheSetValidator.class)
@Documented
public @interface BestandsSucheValid {

    String message() default "Bestandssuche muss korrekt gesetzt sein.";

    /**
     * distinguishes between 'bestandssuche' (= {@code false}) and 'bestandssuche (erweitert)' (= {@code true})
     */
    boolean erweitert() default false;

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };

    public static class BestandsSucheSetValidator implements ConstraintValidator<BestandsSucheValid, Geschaeftsfall> {

        private boolean erweitert;

        @Override
        public void initialize(BestandsSucheValid constraintAnnotation) {
            erweitert = constraintAnnotation.erweitert();
        }

        @Override
        public boolean isValid(Geschaeftsfall geschaeftsfall, ConstraintValidatorContext context) {
            BestandsSuche bestandsSuche = geschaeftsfall.getAuftragsPosition().getGeschaeftsfallProdukt()
                    .getBestandsSuche();
            if (bestandsSuche == null) {
                return true;
            }
            if (!erweitert && !isEinzelanschlussValid(bestandsSuche)) {
                return false;
            }
            return isEinzelanschlussValid(bestandsSuche) ^ isAnlagenanschlussValid(bestandsSuche);
        }

        private boolean isEinzelanschlussValid(BestandsSuche bestandsSuche) {
            // @formatter:off
            return (bestandsSuche.getOnkz() != null)
                && (bestandsSuche.getRufnummer() != null)
                && (bestandsSuche.getAnlagenAbfrageStelle() == null)
                && (bestandsSuche.getAnlagenDurchwahl() == null)
                && (bestandsSuche.getAnlagenOnkz() == null);
            // @formatter:on
        }

        private boolean isAnlagenanschlussValid(BestandsSuche bestandsSuche) {
            // @formatter:off
            return (bestandsSuche.getOnkz() == null)
                && (bestandsSuche.getRufnummer() == null)
                && (bestandsSuche.getAnlagenAbfrageStelle() != null)
                && (bestandsSuche.getAnlagenDurchwahl() != null)
                && (bestandsSuche.getAnlagenOnkz() != null);
            // @formatter:on
        }
    }

}
