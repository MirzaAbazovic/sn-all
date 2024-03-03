/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 03.06.2011 12:04:23
 */
package de.mnet.wita.validators;

import static de.mnet.wita.validators.ValidationUtils.*;
import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

import java.lang.annotation.*;
import java.util.*;
import javax.validation.*;

import de.mnet.wita.message.auftrag.Auftragsposition;
import de.mnet.wita.message.auftrag.Schaltangaben;
import de.mnet.wita.message.auftrag.SchaltungKupfer;
import de.mnet.wita.message.auftrag.SchaltungKvzTal;
import de.mnet.wita.message.common.Uebertragungsverfahren;
import de.mnet.wita.validators.SchaltangabenValid.SchaltangabenValidator;

/**
 * Validator Annotation fuer {@link Schaltangaben}
 */
@Target({ METHOD, FIELD, ANNOTATION_TYPE, TYPE })
@Retention(RUNTIME)
@Constraint(validatedBy = SchaltangabenValidator.class)
@Documented
public @interface SchaltangabenValid {

    String message() default "{validator.schaltangaben.defined}";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };

    public class SchaltangabenValidator implements ConstraintValidator<SchaltangabenValid, Auftragsposition> {

        @Override
        public void initialize(SchaltangabenValid constraintAnnotation) {
            // nothing to do
        }

        @Override
        public boolean isValid(Auftragsposition auftragsposition, ConstraintValidatorContext context) {
            // both auftragsposition and geschaeftsfallProdukt must be set for every Anfrage/Request
            Schaltangaben schaltangaben = auftragsposition.getGeschaeftsfallProdukt().getSchaltangaben();
            if (schaltangaben == null) {
                return true;
            }

            int kupferCount = sizeOf(schaltangaben.getSchaltungKupfer());
            int kvzCount = sizeOf(schaltangaben.getSchaltungKvzTal());
            int glasCount = 0; // Pruefung auf SchaltungGlas aufnehmen, wenn definiert
            int isisCount = 0; // Pruefung auf SchaltungIsis aufnehmen, wenn definiert

            if (auftragsposition.getProduktBezeichner() == null) {
                return addConstraintViolation(context,
                        "Produktbezeichner ist nicht definiert; Anzahl notwendiger Schaltangaben dadurch nicht ermittelbar!");
            }
            int requiredCount = auftragsposition.getProduktBezeichner().getRequiredSchaltangabenCount();
            int actualCount = kupferCount + kvzCount + glasCount + isisCount;

            return checkIfExactlyOneTypeOfSchaltangabenSet(context, kupferCount, kvzCount, glasCount, isisCount)
                    && checkIfRequiredSchaltangabenCountIsFulfilled(context, requiredCount, actualCount)
                    && checkIfSameUetv(context, schaltangaben);
        }

        int sizeOf(List<?> data) {
            return (data == null) ? 0 : data.size();
        }

        private boolean checkIfExactlyOneTypeOfSchaltangabenSet(ConstraintValidatorContext context, int kupferCount,
                int kvzCount, int glasCount, int isisCount) {
            if (!isExactlyOneUnequalToZero(kupferCount, kvzCount, glasCount, isisCount)) {
                return addConstraintViolation(context,
                        "Schaltangaben enthalten verschiedene Leitungsarten (Hvt-, Kvz-, Glas-Tal, ...)");
            }
            return true;
        }

        private boolean checkIfRequiredSchaltangabenCountIsFulfilled(ConstraintValidatorContext context,
                int requiredSchaltangabenCount, int actualSchaltangabenCount) {
            if (requiredSchaltangabenCount != actualSchaltangabenCount) {
                return addConstraintViolation(context, String.format(
                        "%d Leitung(en) benötigt, aber %d Leitung(en) gefunden.", requiredSchaltangabenCount,
                        actualSchaltangabenCount));
            }
            return true;
        }

        boolean checkIfSameUetv(ConstraintValidatorContext context, Schaltangaben schaltangaben) {
            Set<Uebertragungsverfahren> uetvs = new HashSet<Uebertragungsverfahren>();

            if (schaltangaben.getSchaltungKupfer() != null) {
                for (SchaltungKupfer schaltungKupfer : schaltangaben.getSchaltungKupfer()) {
                    uetvs.add(schaltungKupfer.getUebertragungsverfahren());
                }
            }
            else if (schaltangaben.getSchaltungKvzTal() != null) {
                for (SchaltungKvzTal schaltungKvz : schaltangaben.getSchaltungKvzTal()) {
                    uetvs.add(schaltungKvz.getUebertragungsverfahren());
                }
            }

            if (uetvs.size() > 1) {
                return addConstraintViolation(context,
                        "Unterschiedliche Übertragungsverfahren gefunden: " + uetvs.toString());
            }
            return true;
        }

        /**
         * Ueberprueft, ob ganz genau ein Element aus {@code values} eine Wert ungleich '0' besitzt.
         */
        boolean isExactlyOneUnequalToZero(int... values) {
            boolean valueIsSet = false;
            for (int value : values) {
                if (value > 0) {
                    if (valueIsSet) {
                        return false;
                    }
                    valueIsSet = true;
                }
            }
            return valueIsSet;
        }
    }
}
