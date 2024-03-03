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
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;

import de.mnet.wita.message.auftrag.Auftragsposition;
import de.mnet.wita.message.auftrag.Schaltangaben;
import de.mnet.wita.message.auftrag.SchaltungKupfer;
import de.mnet.wita.message.auftrag.geschaeftsfall.Geschaeftsfall;
import de.mnet.wita.message.common.Uebertragungsverfahren;
import de.mnet.wita.validators.AuftragspositionValidForLmae.AuftragspositionForLmaeValidator;

/**
 * Validator Annotation fuer {@link Schaltangaben}
 */
@Target({ METHOD, FIELD, ANNOTATION_TYPE, TYPE })
@Retention(RUNTIME)
@Constraint(validatedBy = AuftragspositionForLmaeValidator.class)
@Documented
public @interface AuftragspositionValidForLmae {

    String message() default "{validator.auftragsposition.defined}";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };

    public class AuftragspositionForLmaeValidator implements
            ConstraintValidator<AuftragspositionValidForLmae, Geschaeftsfall> {

        public static final String ALTE_UEBERTRAGUNGSVERFAHREN_BEI_96X_MUESSEN_EINDEUTIG_GESETZT_SEIN = "Alte Übertragungsverfahren bei 96x müssen eindeutig gesetzt sein.";
        public static final String AUFTRAG_ENTHAELT_KEINE_AENDERUNG_AM_UEBERTRAGUNGSVERFAHREN = "Auftrag enthält keine Änderung am Übertragungsverfahren.";
        public static final String ALTE_SCHALTANGABEN_MUESSEN_UEBERTRAGUNGSVERFAHREN_GESETZT_HABEN = "Alte Schaltangaben müssen Übertragungsverfahren gesetzt haben.";
        public static final String FALSCHE_ANZAHL_ALTER_SCHALTANGABEN = "Alte Schaltangaben müssen genau eine Schaltung Kupfer (oder zwei für 96x) gesetzt haben.";
        public static final String ALTE_SCHALTANGABEN_MUESSEN_GESETZT_SEIN = "Alte Schaltangaben müssen gesetzt sein.";
        public static final String NEUE_SCHALTANGABEN_MUESSEN_UEBERTRAGUNGSVERFAHREN_GESETZT_HABEN = "Neue Schaltangaben müssen Übertragungsverfahren gesetzt haben. N01 ist kein gültiges Übertragungsverfahren für LMAE.";
        public static final String FALSCHE_ANZAHL_SCHALTANGABEN = "Neue Schaltangaben müssen genau eine Schaltung Kupfer (oder zwei fuer 96x) gesetzt haben.";
        public static final String NEUE_SCHALTANGABEN_MUESSEN_GESETZT_SEIN = "Neue Schaltangaben müssen gesetzt sein.";
        public static final String NEUE_UEBERTRAGUNGSVERFAHREN_BEI_96X_MUESSEN_EINDEUTIG_GESETZT_SEIN = "Neue Übertragungsverfahren bei 96x müssen eindeutig gesetzt sein";

        @Override
        public void initialize(AuftragspositionValidForLmae constraintAnnotation) {
            // nothing to do
        }

        @Override
        public boolean isValid(Geschaeftsfall geschaeftsfall, ConstraintValidatorContext context) {
            return isValid(geschaeftsfall.getAuftragsPosition(), context);
        }

        public boolean isValid(Auftragsposition auftragsposition, ConstraintValidatorContext context) {
            // GF-Produkt and Auftragsposition are never null
            Schaltangaben oldSchaltangaben = auftragsposition.getGeschaeftsfallProdukt().getSchaltangaben();
            if (oldSchaltangaben == null) {
                return addConstraintViolation(context, ALTE_SCHALTANGABEN_MUESSEN_GESETZT_SEIN);
            }

            int requiredSchaltangabenCount = auftragsposition.getProduktBezeichner().getRequiredSchaltangabenCount();

            if ((oldSchaltangaben.getSchaltungKupfer() == null)
                    || (oldSchaltangaben.getSchaltungKupfer().size() != requiredSchaltangabenCount)) {
                return addConstraintViolation(context, FALSCHE_ANZAHL_ALTER_SCHALTANGABEN);
            }

            Set<Uebertragungsverfahren> oldUebertragungsverfahren = Sets.newHashSet();
            for (SchaltungKupfer oldSchaltung : oldSchaltangaben.getSchaltungKupfer()) {
                if (oldSchaltung.getUebertragungsverfahren() == null) {
                    return addConstraintViolation(context,
                            ALTE_SCHALTANGABEN_MUESSEN_UEBERTRAGUNGSVERFAHREN_GESETZT_HABEN);
                }
                oldUebertragungsverfahren.add(oldSchaltung.getUebertragungsverfahren());
            }
            if (oldUebertragungsverfahren.size() > 1) {
                return addConstraintViolation(context,
                        ALTE_UEBERTRAGUNGSVERFAHREN_BEI_96X_MUESSEN_EINDEUTIG_GESETZT_SEIN);
            }

            // UnterpositionSet for LMAE guarantees that this causes no nullpointerexception
            Schaltangaben schaltangaben = auftragsposition.getPosition().getGeschaeftsfallProdukt().getSchaltangaben();
            if (schaltangaben == null) {
                return addConstraintViolation(context, NEUE_SCHALTANGABEN_MUESSEN_GESETZT_SEIN);
            }

            if ((schaltangaben.getSchaltungKupfer() == null)
                    || (schaltangaben.getSchaltungKupfer().size() != requiredSchaltangabenCount)) {
                return addConstraintViolation(context, FALSCHE_ANZAHL_SCHALTANGABEN);
            }

            Set<Uebertragungsverfahren> newUebertragungsverfahren = Sets.newHashSet();
            for (SchaltungKupfer newSchaltung : schaltangaben.getSchaltungKupfer()) {
                if (newSchaltung.getUebertragungsverfahren() == null) {
                    return addConstraintViolation(context,
                            NEUE_SCHALTANGABEN_MUESSEN_UEBERTRAGUNGSVERFAHREN_GESETZT_HABEN);
                }
                newUebertragungsverfahren.add(newSchaltung.getUebertragungsverfahren());
            }
            if (newUebertragungsverfahren.size() > 1) {
                return addConstraintViolation(context,
                        NEUE_UEBERTRAGUNGSVERFAHREN_BEI_96X_MUESSEN_EINDEUTIG_GESETZT_SEIN);
            }

            if (Iterables.getOnlyElement(oldUebertragungsverfahren).equals(
                    Iterables.getOnlyElement(newUebertragungsverfahren))) {
                return addConstraintViolation(context, AUFTRAG_ENTHAELT_KEINE_AENDERUNG_AM_UEBERTRAGUNGSVERFAHREN);
            }

            return true;
        }
    }
}
