/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 10.10.2011 14:14:16
 */
package de.mnet.wita.model;

import java.time.*;
import java.util.*;

import de.augustakom.common.model.EntityBuilder;
import de.augustakom.common.model.SessionFactoryAware;
import de.mnet.wita.model.TamUserTask.TamBearbeitungsStatus;
import de.mnet.wita.model.UserTask.UserTaskStatus;

@SuppressWarnings("unused")
@SessionFactoryAware("cc.sessionFactory")
public class TamUserTaskBuilder extends EntityBuilder<TamUserTaskBuilder, TamUserTask> {

    public TamBearbeitungsStatus tamBearbeitungsStatus = TamBearbeitungsStatus.OFFEN;
    public UserTaskStatus status = UserTaskStatus.OFFEN;
    public boolean tv60Sent = false;
    public boolean mahnTam = false;
    private Date wiedervorlageAm = null;

    public TamUserTaskBuilder withWiedervorlageAm(Date wiedervorlageAm) {
        this.wiedervorlageAm = wiedervorlageAm;
        return this;
    }

    public TamUserTaskBuilder withWiedervorlageAm(LocalDateTime wiedervorlageAm) {
        final Date dt = wiedervorlageAm != null ? Date.from(wiedervorlageAm.atZone(ZoneId.systemDefault()).toInstant()) : null;
        return this.withWiedervorlageAm(dt);
    }
}
