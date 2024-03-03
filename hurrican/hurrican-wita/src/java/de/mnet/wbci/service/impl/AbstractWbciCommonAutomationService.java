package de.mnet.wbci.service.impl;

import static de.mnet.wbci.model.AutomationTask.AutomationStatus.*;

import com.google.common.base.Throwables;
import org.springframework.beans.factory.annotation.Autowired;

import de.augustakom.authentication.model.AKUser;
import de.mnet.wbci.exception.WbciAutomationValidationException;
import de.mnet.wbci.model.AutomationTask;
import de.mnet.wbci.model.Meldung;
import de.mnet.wbci.model.WbciGeschaeftsfall;
import de.mnet.wbci.service.WbciGeschaeftsfallService;

/**
 * AbstractWbciCommonAutomationService
 */
public abstract class AbstractWbciCommonAutomationService {

    private static final String NO_ACTIVE_WITA_INSTANCE =
            "Konnte keinen (oder keinen eindeutigen) aktiven WITA Vorgang zur Vorabstimmungs-Id '%s' ermitteln";

    @Autowired
    protected WbciGeschaeftsfallService wbciGeschaeftsfallService;

    /**
     * Handles automation exceptions. If exc is an instance of {@link WbciAutomationValidationException} no stacktrace
     * will be added to the auomation log. These errors are expected errors, so no stack trace is needed.
     *
     * @param meldung  the processed {@link Meldung}
     * @param taskName {@link AutomationTask.TaskName}
     * @param exc      {@link Exception}
     * @param user
     */
    protected void handleAutomationException(Meldung meldung, AutomationTask.TaskName taskName, Exception exc,
            AKUser user) {
        // for expected errors print no stack trace
        if (exc instanceof WbciAutomationValidationException) {
            wbciGeschaeftsfallService.createOrUpdateAutomationTaskNewTx(meldung.getVorabstimmungsId(), meldung,
                    taskName, ERROR, exc.getMessage(), user);
        }
        else {
            wbciGeschaeftsfallService.createOrUpdateAutomationTaskNewTx(meldung.getVorabstimmungsId(), meldung,
                    taskName, ERROR, Throwables.getStackTraceAsString(exc), user);
        }
    }

    /**
     * @see {@link AbstractWbciCommonAutomationService#handleAutomationException(Meldung, AutomationTask.TaskName, Exception, AKUser)}
     * Der AutomationError wird in diesem Fall allerdings nicht mit einer spezifischen Meldung, sondern nur mit dem
     * {@link WbciGeschaeftsfall} verbunden.
     *
     * @param wbciGeschaeftsfall
     * @param taskName
     * @param exc
     * @param user
     */
    protected void handleAutomationException(WbciGeschaeftsfall wbciGeschaeftsfall, AutomationTask.TaskName taskName, Exception exc, AKUser user) {
        if (exc instanceof WbciAutomationValidationException) {
            wbciGeschaeftsfallService.createOrUpdateAutomationTaskNewTx(wbciGeschaeftsfall.getVorabstimmungsId(), null,
                    taskName, ERROR, exc.getMessage(), user);
        }
        else {
            wbciGeschaeftsfallService.createOrUpdateAutomationTaskNewTx(wbciGeschaeftsfall.getVorabstimmungsId(), null,
                    taskName, ERROR, Throwables.getStackTraceAsString(exc), user);
        }
    }

}
