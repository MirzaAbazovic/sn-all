/**
 * Copyright (c) 2010 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 17.06.2010 18:15:47
 */
package de.mnet.migration.common.result;


/**
 * An ID of the source or destination of a transformation. The ID is stored as a string or numeric (long) value. It has
 * a name so that it can be identified and refers to it's transformation result.
 *
 *
 */
public class SourceTargetId {
    private TransformationResult transformationResult;
    private Long numericId;
    private String stringId;
    private String name;
    private Boolean source;


    public SourceTargetId(String name, Long id) {
        this.name = name;
        numericId = id;
    }

    public SourceTargetId(String name, String id) {
        this.name = name;
        stringId = id;
    }

    @Override
    public String toString() {
        return "(" + ((name != null) && (name.length() > 0) ? name + " = " : "") +
                (numericId != null ? numericId.toString() : "") +
                ((numericId != null) && (stringId != null) ? "/" : "") +
                (stringId != null ? stringId : "") +
                ")";
    }


    public Long getNumericId() {
        return numericId;
    }

    public void setNumericId(Long numericId) {
        this.numericId = numericId;
    }

    public String getStringId() {
        return stringId;
    }

    public void setStringId(String stringId) {
        this.stringId = stringId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getSource() {
        return source;
    }

    public void setSource(Boolean source) {
        this.source = source;
    }

    public void setTransformationResult(TransformationResult transformationResult) {
        this.transformationResult = transformationResult;
    }

    public TransformationResult getTransformationResult() {
        return transformationResult;
    }


    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((numericId == null) ? 0 : numericId.hashCode());
        result = prime * result + ((stringId == null) ? 0 : stringId.hashCode());
        result = prime * result + ((source == null) ? 0 : source.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        SourceTargetId other = (SourceTargetId) obj;
        if (name == null) {
            if (other.name != null) {
                return false;
            }
        }
        else if (!name.equals(other.name)) {
            return false;
        }
        if (numericId == null) {
            if (other.numericId != null) {
                return false;
            }
        }
        else if (!numericId.equals(other.numericId)) {
            return false;
        }
        if (source == null) {
            if (other.source != null) {
                return false;
            }
        }
        else if (!source.equals(other.source)) {
            return false;
        }
        if (stringId == null) {
            if (other.stringId != null) {
                return false;
            }
        }
        else if (!stringId.equals(other.stringId)) {
            return false;
        }
        return true;
    }
}
