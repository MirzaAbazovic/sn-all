/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 10.07.2014
 */
package de.augustakom.hurrican.service.elektra;

import java.io.*;

/**
 *
 */
public class ElektraResponseDto implements Serializable {

    public enum ResponseStatus {
        OK,
        ERROR;
    }

    private ResponseStatus status;
    private String modifications;

    public ResponseStatus getStatus() {
        return status;
    }

    public void setStatus(ResponseStatus status) {
        this.status = status;
    }

    public String getModifications() {
        return modifications;
    }

    public void setModifications(String modifications) {
        this.modifications = modifications;
    }

    public boolean isSuccessfull() {
        return ResponseStatus.OK == status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ElektraResponseDto)) {
            return false;
        }

        ElektraResponseDto that = (ElektraResponseDto) o;

        if (modifications != null ? !modifications.equals(that.modifications) : that.modifications != null) {
            return false;
        }
        if (status != that.status) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = status != null ? status.hashCode() : 0;
        result = 31 * result + (modifications != null ? modifications.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ElektraResponseDto{" +
                "status=" + status +
                ", modifications='" + modifications + '\'' +
                '}';
    }

}
