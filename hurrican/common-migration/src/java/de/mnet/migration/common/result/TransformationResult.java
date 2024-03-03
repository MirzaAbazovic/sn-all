/**
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 31.08.2009 11:45:12
 */
package de.mnet.migration.common.result;

import java.util.*;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.SystemUtils;
import org.apache.commons.lang.exception.ExceptionUtils;

import de.mnet.migration.common.util.CollectionUtil;


/**
 * Das Resultat eines Transformation-Schrittes (Daten lesen, transformieren, speichern). Die verschiedenen Felder
 * koennen {@code null} bleiben.
 * <p/>
 * Es gibt Factory-Methoden, die das TransformationResult je nach Status des Migrationsschrittes (z.Z. OK, ERROR, ...)
 * mit den wahrscheinlich vorhandenen und sinnvollen Werten initialisieren.
 *
 *
 */
public class TransformationResult {
    private static final String SEPARATOR = " ::: ";
    private TransformationStatus transformationStatus = TransformationStatus.UNKNOWN;
    private Long classification = null;
    private String classificationString = null;
    private SourceIdList sourceValues = null;
    private TargetIdList targetValues = null;
    private List<String> infoText = null;
    private List<Exception> exceptions = null;


    public TransformationResult(TransformationStatus transformationStatus, SourceIdList sourceValues,
            TargetIdList targetValues, String infoText, Long classification, String classificationString, Exception exception) {
        this.transformationStatus = transformationStatus;
        this.sourceValues = sourceValues;
        this.targetValues = targetValues;
        this.infoText = CollectionUtil.list(infoText);
        this.classification = classification;
        this.classificationString = classificationString;
        this.exceptions = CollectionUtil.list(exception);
    }


    public TransformationResult(TransformationStatus transformationStatus, SourceIdList sourceValues,
            TargetIdList targetValues, List<String> infoText, Long classification, String classificationString, List<Exception> exception) {
        this.transformationStatus = transformationStatus;
        this.sourceValues = sourceValues;
        this.targetValues = targetValues;
        this.infoText = infoText;
        this.classification = classification;
        this.classificationString = classificationString;
        this.exceptions = exception;
    }


    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append(transformationStatus.toString());
        s.append(" (");
        s.append(getInfoText());
        s.append(") - Source value: ");
        s.append(sourceValues == null ? "<null>" : sourceValues.toString());
        s.append(" - Target value: ");
        s.append(targetValues == null ? "<null>" : targetValues.toString());
        s.append(getStackTrace());
        return s.toString();
    }

    /**
     * @return Status des Transformationsschrittes
     */
    public TransformationStatus getTranformationStatus() {
        return transformationStatus;
    }

    /**
     * @return Liste der Ursprungswerts
     */
    public SourceIdList getSourceValues() {
        return sourceValues;
    }

    /**
     * @return Liste der Zielwerte
     */
    public TargetIdList getTargetValues() {
        return targetValues;
    }

    /**
     * @return Zusaetzliche Informationen
     */
    public String getInfoText() {
        return StringUtils.join(infoText, SEPARATOR);
    }

    /**
     * @return Die Klassifikation (z.B. zur Unterscheidung zwischen verschiedenen Warnings)
     */
    public Long getClassification() {
        return classification;
    }


    /**
     * @return Die Klassifikation als String zum schnelleren Identifizieren
     */
    public String getClassificationString() {
        return classificationString;
    }


    /**
     * @return Geworfene Exceptions
     */
    public String getStackTrace() {
        StringBuilder s = new StringBuilder();
        if ((exceptions != null) && (!exceptions.isEmpty())) {
            for (Exception e : exceptions) {
                if (e != null) {
                    s.append(SystemUtils.LINE_SEPARATOR);
                    s.append("====== Exception ======");
                    s.append(ExceptionUtils.getFullStackTrace(e));
                }
            }
        }
        return s.toString();
    }
}
