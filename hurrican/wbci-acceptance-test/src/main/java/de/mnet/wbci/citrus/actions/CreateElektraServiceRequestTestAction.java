/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 12.06.2014
 */
package de.mnet.wbci.citrus.actions;

import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;

import com.consol.citrus.context.TestContext;

import de.mnet.wbci.model.MeldungTyp;
import de.mnet.wbci.model.RequestTyp;
import de.mnet.wbci.service.WbciElektraService;

/**
 *
 */
public class CreateElektraServiceRequestTestAction extends AbstractWbciTestAction {

    private final WbciElektraService wbciElektraService;
    private final RequestTyp requestTyp;
    private final MeldungTyp meldungTyp;
    private String vorabstimmungsId;

    private TaskExecutor taskExecutor = new SimpleAsyncTaskExecutor();

    public CreateElektraServiceRequestTestAction(WbciElektraService wbciElektraService, RequestTyp requestTyp, MeldungTyp meldungTyp) {
        super("createElektraServiceRequest");
        this.wbciElektraService = wbciElektraService;
        this.requestTyp = requestTyp;
        this.meldungTyp = meldungTyp;
    }

    @Override
    public void doExecute(final TestContext context) {
        final String vorabstimmungsId = this.vorabstimmungsId == null ? getVorabstimmungsId(context) : this.vorabstimmungsId;
        taskExecutor.execute(new Runnable() {
            @Override
            public void run() {
                if (requestTyp.equals(RequestTyp.VA) && meldungTyp == null) {
                    wbciElektraService.processRrnp(vorabstimmungsId, null);
                } else if (meldungTyp.equals(MeldungTyp.AKM_TR)) {
                    wbciElektraService.processAkmTr(vorabstimmungsId, null);
                } else if (meldungTyp.equals(MeldungTyp.RUEM_VA)) {
                    wbciElektraService.processRuemVa(vorabstimmungsId, null);
                } else if (requestTyp.equals(RequestTyp.TV) && meldungTyp.equals(MeldungTyp.ERLM)) {
                    wbciElektraService.processTvErlm(vorabstimmungsId, null);
                }
            }
        });
    }

    public CreateElektraServiceRequestTestAction withVorabstimmungsId(String vorabstimmungsId) {
        this.vorabstimmungsId = vorabstimmungsId;
        return this;
    }

}
