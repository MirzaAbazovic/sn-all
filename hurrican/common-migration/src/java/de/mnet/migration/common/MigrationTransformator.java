/**
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 31.08.2009 12:11:04
 */
package de.mnet.migration.common;

import java.lang.reflect.*;
import java.time.*;
import java.util.*;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;

import de.mnet.migration.common.result.SourceIdList;
import de.mnet.migration.common.result.SourceTargetId;
import de.mnet.migration.common.result.TargetIdList;
import de.mnet.migration.common.result.TransformationResult;
import de.mnet.migration.common.result.TransformationStatus;
import de.mnet.migration.common.util.Messages;
import de.mnet.migration.common.util.ReflectionUtil;

/**
 * Abstrakte Klasse fuer die Migrations-Transformatoren. Sie laden einen oder mehrere Datensaetze aus den uebergebenen
 * SqlRowSets, transformieren diese Daten in Hurrican-Domaenenobjekte, koennen noch diverse Checks durchfuehren und
 * speichern die Domaenenobjekte ueber Hurrican-Services in der Datenbank.
 *
 * @param <T> Typ des zu transformierenden Datensatzes, also aus der Quelle
 *
 */
public abstract class MigrationTransformator<T> {
    private static final Logger LOGGER = Logger.getLogger(MigrationTransformator.class);

    /**
     * Standard-Classification: 0
     */
    public static final Long CLASS_DEFAULT = 0L;
    public static final LocalDateTime NAVI_0_DATE = LocalDateTime.of(1753, 1, 2, 0, 0, 0, 0);
    public static final LocalDateTime TAIFUN_0_DATE = LocalDateTime.of(1900, 1, 1, 0, 0, 0, 0);
    public static final LocalDateTime TAIFUN_1990_DATE = LocalDateTime.of(1990, 1, 1, 0, 0, 0, 0);
    public static final LocalDateTime TAIFUN_1800_DATE = LocalDateTime.of(1800, 1, 1, 0, 0, 0, 0);
    public static final LocalDateTime TAIFUN_INFINITY_DATE = LocalDateTime.of(9999, 12, 31, 0, 0, 0, 0);

    public static final SourceIdList EMPTY_SOURCE_ID_LIST = new SourceIdList();
    public static final TargetIdList EMPTY_TARGET_ID_LIST = new TargetIdList();

    @Value("${migration.userw}")
    public String migrationUserW;

    @Value("${migration.rec.source}")
    public String migrationRecSource;

    private boolean readResultFromDatabase = false;
    private Long migResultId;

    /**
     * Transformiert einen Datensatz
     */
    public abstract TransformationResult transform(T row);

    public boolean shouldReadResultFromDatabase() {
        return readResultFromDatabase;
    }

    public void setReadResultFromDatabase(boolean readResultFromDatabase) {
        this.readResultFromDatabase = readResultFromDatabase;
    }

    public void setMigResultId(Long migResultId) {
        this.migResultId = migResultId;
    }

    public Long getMigResultId() {
        return migResultId;
    }

    /**
     * Shortcut to create a list of no source ids
     */
    public static SourceIdList noSource() {
        return EMPTY_SOURCE_ID_LIST;
    }


    /**
     * Shortcut to create a list of one source id
     */
    public static SourceIdList source(Object id) {
        if (id instanceof SourceTargetId) {
            return new SourceIdList((SourceTargetId) id);
        }
        return new SourceIdList(id("", id));
    }

    /**
     * Shortcut to create a list of no target ids
     */
    public static TargetIdList noTarget() {
        return new TargetIdList();
    }


    /**
     * Shortcut to create a list of one target id
     */
    public static TargetIdList target(Object id) {
        if (id instanceof SourceTargetId) {
            return new TargetIdList((SourceTargetId) id);
        }
        return new TargetIdList(id("", id));
    }


    /**
     * Shortcut to create a SourceIdList class
     */
    public static SourceIdList sources(SourceTargetId... ids) {
        if (ids.length == 0) {
            return EMPTY_SOURCE_ID_LIST;
        }
        return new SourceIdList(ids);
    }

    /**
     * Shortcut to create a SourceIdList class
     */
    public static SourceIdList sources(List<SourceTargetId> ids) {
        if (ids.isEmpty()) {
            return EMPTY_SOURCE_ID_LIST;
        }
        return new SourceIdList(ids);
    }


    /**
     * Shortcut to create a TargetIdList class
     */
    public static TargetIdList targets(SourceTargetId... ids) {
        if (ids.length == 0) {
            return EMPTY_TARGET_ID_LIST;
        }
        return new TargetIdList(ids);
    }

    /**
     * Shortcut to create a TargetIdList class
     */
    public static TargetIdList targets(List<SourceTargetId> ids) {
        if (ids.isEmpty()) {
            return EMPTY_TARGET_ID_LIST;
        }
        return new TargetIdList(ids);
    }


    /**
     * Create a new SourceTargetId instance, to be used in {@link #source(SourceTargetId...)} or {@link
     * #target(SourceTargetId...)} methods.
     *
     * @param name The name of this ID
     * @param It's value
     */
    public static SourceTargetId id(String name, Object id) {
        if (id == null) {
            return new SourceTargetId(name, (Long) null);
        }
        else if (id instanceof Long) {
            return new SourceTargetId(name, (Long) id);
        }
        else if (id instanceof Integer) {
            return new SourceTargetId(name, ((Integer) id).longValue());
        }
        else {
            return new SourceTargetId(name, id.toString());
        }
    }


    /**
     * @param sourceValues      Liste der Ursprungswerte
     * @param destinationValues Liste der Zielwerte
     * @param infoText          Zusaetzliche Informationen
     */
    public static TransformationResult ok(SourceIdList sourceValues,
            TargetIdList destinationValues, String infoText) {
        return new TransformationResult(TransformationStatus.OK, sourceValues,
                destinationValues, infoText, CLASS_DEFAULT, "", null);
    }

    /**
     * @param sourceValues Liste der Ursprungswerte
     * @param infoText     Zusaetzliche Informationen
     */
    public static TransformationResult warning(SourceIdList sourceValues,
            TargetIdList destinationValues, String infoText, Long classification, String classificationString) {
        return new TransformationResult(TransformationStatus.WARNING,
                sourceValues, destinationValues, infoText, classification, classificationString, null);
    }

    /**
     * @param sourceValues Liste der Ursprungswerte
     * @param infoText     Zusaetzliche Informationen
     * @param exception    (optional) Exception die aufgetreten ist
     */
    public static TransformationResult warning(SourceIdList sourceValues,
            TargetIdList destinationValues, String infoText, Long classification, String classificationString, Exception exception) {
        return new TransformationResult(TransformationStatus.WARNING,
                sourceValues, destinationValues, infoText, classification, classificationString, exception);
    }

    /**
     * @param sourceValues Liste der Ursprungswerte
     * @param infoText     Zusaetzliche Informationen
     */
    public static TransformationResult info(SourceIdList sourceValues,
            TargetIdList destinationValues, String infoText, Long classification, String classificationString) {
        return new TransformationResult(TransformationStatus.INFO,
                sourceValues, destinationValues, infoText, classification, classificationString, null);
    }

    /**
     * @param sourceValues Liste der Ursprungswerte
     * @param infoText     Zusaetzliche Informationen
     * @param exception    (optional) Exception die aufgetreten ist
     */
    public static TransformationResult info(SourceIdList sourceValues,
            TargetIdList destinationValues, String infoText, Long classification, String classificationString, Exception exception) {
        return new TransformationResult(TransformationStatus.INFO,
                sourceValues, destinationValues, infoText, classification, classificationString, exception);
    }

    /**
     * @param sourceValues Liste der Ursprungswerte
     * @param infoText     Zusaetzliche Informationen
     */
    public static TransformationResult skipped(SourceIdList sourceValues, String infoText, Long classification, String classificationString) {
        return new TransformationResult(TransformationStatus.SKIPPED,
                sourceValues, null, infoText, classification, classificationString, null);
    }

    /**
     * @param sourceValues Liste der Ursprungswerte
     * @param infoText     Zusaetzliche Informationen
     * @param exception    (optional) Exception die aufgetreten ist
     */
    public static TransformationResult badData(SourceIdList sourceValues,
            String infoText, Long classification, String classificationString, Exception exception) {
        return new TransformationResult(TransformationStatus.BAD_DATA,
                sourceValues, null, infoText, classification, classificationString, exception);
    }

    /**
     * @param sourceValues Liste der Ursprungswerte
     * @param infoText     Zusaetzliche Informationen
     */
    public static TransformationResult error(SourceIdList sourceValues,
            String infoText, Long classification, String classificationString, Exception exception) {
        return new TransformationResult(TransformationStatus.ERROR,
                sourceValues, null, infoText, classification, classificationString, exception);
    }


    /**
     * Checks if the child defines a field of type {@link Messages} and, if so, returns the Messages object. If multiple
     * are declared, returns the first object found.
     *
     * @return a messages object, or {@code null} if none was defined
     */
    public <V extends Messages> V getMessagesIfDefined() {
        List<Field> fields = ReflectionUtil.getAllFields(MigrationTransformator.class, this.getClass());
        for (Field field : fields) {
            if (Messages.class.isAssignableFrom(field.getType())) {
                field.setAccessible(true);
                try {
                    @SuppressWarnings("unchecked")
                    V result = (V) field.get(this);
                    return result;
                }
                catch (Exception e) {
                    LOGGER.warn("getMessagesIfDefined() - Could not get Messages object from field " +
                            field.toString() + " of class " + this.getClass().getName());
                }
            }
        }
        return null;
    }
}
