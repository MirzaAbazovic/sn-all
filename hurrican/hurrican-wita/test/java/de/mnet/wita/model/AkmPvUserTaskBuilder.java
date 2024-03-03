/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 16.02.2012 09:32:29
 */
package de.mnet.wita.model;

import java.time.*;
import java.util.*;

import de.augustakom.common.model.EntityBuilder;
import de.augustakom.common.model.SessionFactoryAware;
import de.mnet.wita.model.AkmPvUserTask.AkmPvStatus;
import de.mnet.wita.model.UserTask.UserTaskStatus;

@SuppressWarnings("unused")
@SessionFactoryAware("cc.sessionFactory")
public class AkmPvUserTaskBuilder extends EntityBuilder<AkmPvUserTaskBuilder, AkmPvUserTask> {

    public UserTaskStatus status = UserTaskStatus.OFFEN;
    public AkmPvStatus akmPvStatus = AkmPvStatus.AKM_PV_EMPFANGEN;
    public String externAuftragsNummer = "0";
    public String vertragsnummer = null;
    private Date wiedervorlageAm = null;
    private Set<UserTask2AuftragDaten> userTaskAuftragDaten = null;

    public AkmPvUserTaskBuilder withExterneAuftragsNummer(String extOrderNo) {
        this.externAuftragsNummer = extOrderNo;
        return this;
    }

    public AkmPvUserTaskBuilder withVertragsnummer(String vertragsnummer) {
        this.vertragsnummer = vertragsnummer;
        return this;
    }

    public AkmPvUserTaskBuilder withUsertaskStatus(UserTaskStatus usertaskStatus) {
        this.status = usertaskStatus;
        return this;
    }

    public AkmPvUserTaskBuilder withAkmPvStatus(AkmPvStatus akmPvStatus) {
        this.akmPvStatus = akmPvStatus;
        return this;
    }

    public AkmPvUserTaskBuilder withWiedervorlageAm(Date wiedervorlageAm) {
        this.wiedervorlageAm = wiedervorlageAm;
        return this;
    }

    public AkmPvUserTaskBuilder withWiedervorlageAm(LocalDateTime wiedervorlageAm) {
        final Date dt = wiedervorlageAm != null ? Date.from(wiedervorlageAm.atZone(ZoneId.systemDefault()).toInstant()) : null;
        return this.withWiedervorlageAm(dt);
    }

    public AkmPvUserTaskBuilder withUserTaskAuftragDaten(Set<UserTask2AuftragDaten> userTask2AuftragDaten) {
        this.userTaskAuftragDaten = userTask2AuftragDaten;
        return this;
    }
}
