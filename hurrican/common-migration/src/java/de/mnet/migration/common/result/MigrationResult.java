/**
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 27.08.2009 18:21:09
 */
package de.mnet.migration.common.result;


import java.time.*;

/**
 * Resultat einer Migration: haelt die Zahl der angesehenen Datensaetze ({@code counter}), die der migrierten
 * Datensaetze ({@code migrated}), die der uebersprungenen Datensaetze ({@code skipped}) und die der Datensaetze, bei
 * denen ein Fehler aufgetreten ist ({@code error}).
 * <p/>
 * {@code migrated} + {@code skipped} + {@code errors} sollte gleich {@code counter} sein, sonst hat man einen Fehler
 * gemacht.
 *
 *
 */
public class MigrationResult {

    /**
     * Der Primary-Key in der Tabelle Migration-Result
     */
    private Long id;

    /**
     * Erfolg einer Migration
     */
    private MigrationStatus status = MigrationStatus.RUNNING;

    private String migrationName;
    private LocalDateTime start;
    private LocalDateTime end;

    private int counter = 0;
    private int migrated = 0;
    private int warnings = 0; // migrated but with warnings
    private int infos = 0;    // migrated but with warnings, which are accepted by departments
    private int skipped = 0;
    private int badData = 0;  // skipped due to bad data quality
    private int errors = 0;

    public MigrationResult(String migrationName) {
        this.migrationName = migrationName;
    }

    public MigrationResult(String migrationName, MigrationStatus status, int counter, int migrated, int infos, int warnings, int skipped, int badData, int errors) {
        this.migrationName = migrationName;
        this.status = status;
        this.counter = counter;
        this.migrated = migrated;
        this.infos = infos;
        this.warnings = warnings;
        this.skipped = skipped;
        this.badData = badData;
        this.errors = errors;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder("MigrationResult [Status: ");
        stringBuilder.append(status.toString());
        stringBuilder.append(", Time: ");
        if ((end != null) && (start != null)) {
            stringBuilder.append(String.format("%.3f s", ((double) Duration.between(start, end).toMillis()) / 1000));
        }
        else {
            stringBuilder.append(String.format("?"));
        }
        stringBuilder.append(", Counter: ");
        stringBuilder.append(counter);
        stringBuilder.append(", Migrated: ");
        stringBuilder.append(migrated);
        stringBuilder.append(", Skipped: ");
        stringBuilder.append(skipped);
        stringBuilder.append(", Infos: ");
        stringBuilder.append(infos);
        stringBuilder.append(", Warnings: ");
        stringBuilder.append(warnings);
        stringBuilder.append(", BadData: ");
        stringBuilder.append(badData);
        stringBuilder.append(", Errors: ");
        stringBuilder.append(errors);
        stringBuilder.append("]");
        return stringBuilder.toString();
    }

    /**
     * Merges this {@link MigrationResult} with the given {@link MigrationResult}. Does not produce a new {@link
     * MigrationResult}, returns the current instance.
     *
     * @param toMerge The {@link MigrationResult} to merge into the current {@link MigrationResult}.
     * @return The current {@link MigrationResult} instance, which is now merged with the given {@link MigrationResult}
     * instance.
     */
    public MigrationResult mergeWith(MigrationResult toMerge) {
        if (MigrationStatus.ERROR.equals(toMerge.getStatus())) {
            setStatus(MigrationStatus.ERROR);
        }
        setBadData(getBadData() + toMerge.getBadData());
        setMigrated(getMigrated() + toMerge.getMigrated());
        setSkipped(getSkipped() + toMerge.getSkipped());
        setInfos(getInfos() + toMerge.getInfos());
        setWarnings(getWarnings() + toMerge.getWarnings());
        setErrors(getErrors() + toMerge.getErrors());
        setCounter(getCounter() + toMerge.getCounter());
        return this;
    }

    public void reset() {
        setBadData(0);
        setMigrated(0);
        setSkipped(0);
        setInfos(0);
        setWarnings(0);
        setErrors(0);
        setCounter(0);
    }

    public void addTransformationResult(TransformationResult transformationResult) {
        incCounter();
        switch (transformationResult.getTranformationStatus()) {
            case OK:
                incMigrated();
                break;
            case WARNING:
                incWarnings();
                incMigrated();
                break;
            case INFO:
                incInfos();
                incMigrated();
                break;
            case SKIPPED:
                incSkipped();
                break;
            case BAD_DATA:
                incBadData();
                break;
            case ERROR:
                incErrors();
                setStatus(MigrationStatus.ERROR);
                break;
            default:
                throw new RuntimeException("Result code not handled in switch");
        }
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((migrationName == null) ? 0 : migrationName.hashCode());
        result = prime * result + ((status == null) ? 0 : status.hashCode());
        result = prime * result + badData;
        result = prime * result + counter;
        result = prime * result + errors;
        result = prime * result + migrated;
        result = prime * result + infos;
        result = prime * result + skipped;
        result = prime * result + warnings;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) { return true; }
        if (obj == null) { return false; }
        if (getClass() != obj.getClass()) { return false; }
        MigrationResult other = (MigrationResult) obj;
        if (badData != other.badData) { return false; }
        if (counter != other.counter) { return false; }
        if (errors != other.errors) { return false; }
        if (migrated != other.migrated) { return false; }
        if (skipped != other.skipped) { return false; }
        if (infos != other.infos) { return false; }
        if (warnings != other.warnings) { return false; }
        if (id == null) { if (other.id != null) { return false; } }
        else if (!id.equals(other.id)) { return false; }
        if (migrationName == null) { if (other.migrationName != null) { return false; } }
        else if (!migrationName.equals(other.migrationName)) { return false; }
        if (status == null) { if (other.status != null) { return false; } }
        else if (!status.equals(other.status)) { return false; }
        return true;
    }


    public MigrationStatus getStatus() {
        return status;
    }

    public void setStatus(MigrationStatus status) {
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getSkipped() {
        return skipped;
    }

    public void incSkipped() {
        this.skipped++;
    }

    public void setSkipped(int skipped) {
        this.skipped = skipped;
    }

    public int getBadData() {
        return badData;
    }

    public void incBadData() {
        this.badData++;
    }

    public void setBadData(int badData) {
        this.badData = badData;
    }

    public int getErrors() {
        return errors;
    }

    public void incErrors() {
        this.errors++;
    }

    public void setErrors(int errors) {
        if (errors > 0) {
            status = MigrationStatus.ERROR;
        }
        this.errors = errors;
    }

    public int getMigrated() {
        return migrated;
    }

    public void incMigrated() {
        this.migrated++;
    }

    public void setMigrated(int migrated) {
        this.migrated = migrated;
    }

    public int getCounter() {
        return counter;
    }

    public void incCounter() {
        this.counter++;
    }

    public void setCounter(int counter) {
        this.counter = counter;
    }

    public int getWarnings() {
        return warnings;
    }

    public void incWarnings() {
        this.warnings++;
    }

    public void setWarnings(int warnings) {
        this.warnings = warnings;
    }

    public int getInfos() {
        return infos;
    }

    public void incInfos() {
        this.infos++;
    }

    public void setInfos(int infos) {
        this.infos = infos;
    }

    public void setStart(LocalDateTime start) {
        this.start = start;
    }

    public LocalDateTime getStart() {
        return start;
    }

    public void setEnd(LocalDateTime end) {
        this.end = end;
    }

    public LocalDateTime getEnd() {
        return end;
    }

    public void setMigrationName(String migrationName) {
        this.migrationName = migrationName;
    }

    public String getMigrationName() {
        return migrationName;
    }
}
