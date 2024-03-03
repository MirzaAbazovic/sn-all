/**
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 22.10.2009 17:32:56
 */
package de.mnet.migration.common.util;

import java.util.*;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.SystemUtils;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import de.mnet.migration.common.MigrationTransformator;
import de.mnet.migration.common.result.SourceIdList;
import de.mnet.migration.common.result.TargetIdList;
import de.mnet.migration.common.result.TransformationResult;
import de.mnet.migration.common.result.TransformationStatus;


/**
 * Tools fuer die Migrationen
 *
 *
 */
public class MigrationTools {

    public interface Predicate<T> {
        boolean evaluate(T object);
    }

    /**
     * Ueberprueft ob bei den angebenen Meldungen eine Warnung dabei ist, d.h. eine Meldung die mit "WARN" anfaengt.
     *
     * @param warnMessages
     */
    public static boolean containsWarning(Collection<String> warnMessages) {
        for (String message : warnMessages) {
            if (message.substring(0, 4).equalsIgnoreCase("WARN")) {
                return true;
            }
        }
        return false;
    }

    public static String createStringFromWarnings(Collection<String> warnMessages) {
        return StringUtils.join(warnMessages.iterator(), SystemUtils.LINE_SEPARATOR);
    }


    /**
     * Klassifiziert nur als DEFAULT (0)
     */
    public static TransformationResult transformationOkOrWarning(
            SourceIdList sourceValues,
            TargetIdList destinationValues,
            Collection<String> warnMessages) {
        if (!containsWarning(warnMessages)) {
            return new TransformationResult(TransformationStatus.OK,
                    sourceValues,
                    destinationValues,
                    createStringFromWarnings(warnMessages), MigrationTransformator.CLASS_DEFAULT, "", null);
        }
        else {
            return new TransformationResult(TransformationStatus.WARNING,
                    sourceValues,
                    destinationValues,
                    createStringFromWarnings(warnMessages), MigrationTransformator.CLASS_DEFAULT, "", null);
        }
    }

    public static Long getLongOrNullFromRowSet(SqlRowSet rowSet, String columnName) {
        long retrievedLong = rowSet.getLong(columnName);
        if (rowSet.wasNull()) {
            return null;
        }
        else {
            return Long.valueOf(retrievedLong);
        }
    }

    public static Integer getIntegerOrNullFromRowSet(SqlRowSet rowSet, String columnName) {
        int retrievedInt = rowSet.getInt(columnName);
        if (rowSet.wasNull()) {
            return null;
        }
        else {
            return Integer.valueOf(retrievedInt);
        }
    }

    public static <T> void filter(Collection<T> collection, Predicate<? super T> predicate) {
        Iterator<T> iterator = collection.iterator();
        while (iterator.hasNext()) {
            T object = iterator.next();
            if (!predicate.evaluate(object)) {
                iterator.remove();
            }
        }
    }

    public static boolean willCommit(TransformationStatus transformationStatus) {
        return TransformationStatus.OK.equals(transformationStatus) ||
                TransformationStatus.WARNING.equals(transformationStatus);
    }

    public static String getSimpleExceptionMessage(Throwable e, Class<?> stackTraceClass) {
        StackTraceElement element = null;
        if (e.getStackTrace() != null) {
            for (int i = 0; (i < e.getStackTrace().length) && (element == null); ++i) {
                if (e.getStackTrace()[i].getClassName().equals(stackTraceClass.getName())) {
                    element = e.getStackTrace()[i];
                }
            }
        }
        if (element == null) {
            if ((e.getStackTrace() != null) && (e.getStackTrace().length > 0)) {
                element = e.getStackTrace()[0];
            }
            else if ((e.getCause() != e) && (e.getCause() != null)) {
                return getSimpleExceptionMessage(e.getCause(), stackTraceClass);
            }
            else {
                return e.getClass().getSimpleName() + ":" + e.getMessage();
            }
        }
        return e.getClass().getSimpleName() + "@" + element.getFileName() + "#" + element.getLineNumber() + ": " + e.getMessage();
    }
}
