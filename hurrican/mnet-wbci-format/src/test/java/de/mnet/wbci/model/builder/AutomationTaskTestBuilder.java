/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 14.07.2014
 */
package de.mnet.wbci.model.builder;

import java.time.*;

import de.mnet.wbci.model.AutomationTask;
import de.mnet.wbci.model.GeschaeftsfallTyp;
import de.mnet.wbci.model.Meldung;
import de.mnet.wbci.model.WbciCdmVersion;

/**
 *
 */
public class AutomationTaskTestBuilder extends AutomationTaskBuilder implements WbciTestBuilder<AutomationTask> {

    @Override
    public AutomationTask buildValid(WbciCdmVersion wbciCdmVersion, GeschaeftsfallTyp gfTyp) {
        if (name == null) {
            withName(AutomationTask.TaskName.TAIFUN_NACH_AKMTR_AKTUALISIEREN);
        }
        if (status == null) {
            withStatus(AutomationTask.AutomationStatus.COMPLETED);
        }
        if (createdAt == null) {
            withCreatedAt(LocalDateTime.now());
        }
        if (userName == null) {
            withUserName("user");
        }
        return build();
    }

    public AutomationTaskTestBuilder withStatus(AutomationTask.AutomationStatus status) {
        return (AutomationTaskTestBuilder) super.withStatus(status);
    }

    public AutomationTaskTestBuilder withName(AutomationTask.TaskName name) {
        return (AutomationTaskTestBuilder) super.withName(name);
    }

    public AutomationTaskTestBuilder withExecutionLog(String executionLog) {
        return (AutomationTaskTestBuilder) super.withExecutionLog(executionLog);
    }

    public AutomationTaskTestBuilder withMeldung(Meldung meldung) {
        return (AutomationTaskTestBuilder) super.withMeldung(meldung);
    }

}
