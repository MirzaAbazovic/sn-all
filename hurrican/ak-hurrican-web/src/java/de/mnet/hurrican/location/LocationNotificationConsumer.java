package de.mnet.hurrican.location;

import javax.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.orm.hibernate4.HibernateOptimisticLockingFailureException;

import de.augustakom.common.tools.lang.Pair;
import de.augustakom.common.tools.messages.AKWarnings;
import de.augustakom.hurrican.annotation.CcTxRequired;
import de.augustakom.hurrican.model.cc.GeoIdLocation;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.location.LocationNotificationHelper;
import de.mnet.esb.cdm.resource.locationnotificationservice.v1.LocationNotificationService;
import de.mnet.esb.cdm.resource.locationnotificationservice.v1.NotifyReplaceLocation;
import de.mnet.esb.cdm.resource.locationnotificationservice.v1.NotifyUpdateLocation;
import de.mnet.hurrican.scheduler.HurricanScheduler;
import de.mnet.hurrican.tools.mail.SendMailErrorHandler;
import de.mnet.hurrican.tools.mail.SendMailWarningHandler;

@CcTxRequired
public class LocationNotificationConsumer implements LocationNotificationService {
    private final static Logger LOGGER = LoggerFactory.getLogger(LocationNotificationConsumer.class);

    private final static int MAX_RETRIES = 5; // Fuenf Ebenen, von country bis building

    @Resource(name = "de.mnet.hurrican.tools.mail.ErrorHandler")
    private SendMailErrorHandler sendMailErrorHandler;

    @Resource(name = "locationNotificationErrorMailMsg")
    private SimpleMailMessage simpleMailErrorMessage;

    @Autowired
    private LocationNotificationHelper locationNotificationHelper;
    @Resource(name = "de.mnet.hurrican.tools.mail.WarningHandler")
    private SendMailWarningHandler sendMailWarningHandler;
    @Resource(name = "locationNotificationWarningMailMsg")
    private SimpleMailMessage simpleMailWarningMessage;

    public LocationNotificationConsumer() {
        LOGGER.info("LocationNotificationConsumer started.");
    }

    @Override
    public void notifyUpdateLocation(NotifyUpdateLocation in) {
        final Pair<String, Long> nameAndId = getNameAndId(in);
        LOGGER.info("consuming update: " + nameAndId);

        try {
            // dispatch des Aufrufs in eine andere Transaktion um Rollback im Fehlerfall zu verhindern und die
            // eingehende Nachricht im EMS zu acknowledgen
            notifyUpdateLocationWrapper(in, nameAndId, new CallUpdateNotificationHelper());
        }
        catch (Exception e) {
            final SimpleMailMessage mailErrorMsg = new SimpleMailMessage(simpleMailErrorMessage);
            mailErrorMsg.setSubject("NotifyUpdateLocation");
            sendMailErrorHandler.handleError(mailErrorMsg, e,
                    String.format("Während der Aktualisierung der Ebene '%s' mit Id '%d' ist ein Fehler aufgetreten.",
                            nameAndId.getFirst(), nameAndId.getSecond())
            );
            LOGGER.error("error while processing notifyUpdateLocation", e);
        }
    }

    @Override
    public void notifyReplaceLocation(final NotifyReplaceLocation in) {
        final Pair<String, Long> nameAndId = getNameAndId(in);
        LOGGER.info("consuming replace: " + nameAndId);

        try {
            // dispatch des Aufrufs in eine andere Transaktion um Rollback im Fehlerfall zu verhindern und die
            // eingehende Nachricht im EMS zu acknowledgen
            notifyUpdateLocationWrapper(in, nameAndId, new CallReplaceNotificationHelper());
        }
        catch (Exception e) {
            final SimpleMailMessage mailErrorMsg = new SimpleMailMessage(simpleMailErrorMessage);
            mailErrorMsg.setSubject("NotifyReplaceLocation");
            sendMailErrorHandler.handleError(mailErrorMsg, e,
                    String.format("Während der Aktualisierung der ersetzten Ebene '%s' mit ID '%d' ist ein Fehler aufgetreten.",
                            nameAndId.getFirst(), nameAndId.getSecond())
            );
            LOGGER.error("exception while processing notifyReplaceLocation", e);
        }
    }

    private Pair<String, Long> getNameAndId(NotifyUpdateLocation in) {
        String name = null;
        Long id = null;
        if (in.getBuilding() != null) {
            name = in.getBuilding().getClass().getSimpleName();
            id = in.getBuilding().getId();
        }
        else if (in.getStreetSection() != null) {
            name = in.getStreetSection().getClass().getSimpleName();
            id = in.getStreetSection().getId();
        }
        else if (in.getDistrict() != null) {
            name = in.getDistrict().getClass().getSimpleName();
            id = in.getDistrict().getId();
        }
        else if (in.getZipCode() != null) {
            name = in.getZipCode().getClass().getSimpleName();
            id = in.getZipCode().getId();
        }
        else if (in.getCity() != null) {
            name = in.getCity().getClass().getSimpleName();
            id = in.getCity().getId();
        }
        else if (in.getCountry() != null) {
            name = in.getCountry().getClass().getSimpleName();
            id = in.getCountry().getId();
        }
        return Pair.create(name, id);
    }

    private Pair<String, Long> getNameAndId(NotifyReplaceLocation in) {
        String name = null;
        Long id = null;
        if (in.getOldBuilding() != null) {
            name = in.getOldBuilding().getClass().getSimpleName();
            id = in.getOldBuilding().getId();
        }
        else if (in.getOldStreetSection() != null) {
            name = in.getOldStreetSection().getClass().getSimpleName();
            id = in.getOldStreetSection().getId();
        }
        else if (in.getOldDistrict() != null) {
            name = in.getOldDistrict().getClass().getSimpleName();
            id = in.getOldDistrict().getId();
        }
        else if (in.getOldZipCode() != null) {
            name = in.getOldZipCode().getClass().getSimpleName();
            id = in.getOldZipCode().getId();
        }
        else if (in.getOldCity() != null) {
            name = in.getOldCity().getClass().getSimpleName();
            id = in.getOldCity().getId();
        }
        else if (in.getOldCountry() != null) {
            name = in.getOldCountry().getClass().getSimpleName();
            id = in.getOldCountry().getId();
        }
        return Pair.create(name, id);
    }

    /**
     * Filtert Exceptions, die auf Grund von Concurrency Problemen entstanden sind, heraus und versucht diese
     * Notifications erneut zu verarbeiten. Nach {@code MAX_RETRIES} gibt Methode auf. Folgende Exceptions werden
     * gefiltert: <ul> <li>HibernateOptimisticLockingFailureException <li>DataIntegrityViolationException, wenn bspw.
     * Unique Constraints zuschlagen, weil die Hirarchie ganz oder teilweise durch einen anderen Endpoint nebenlaeufig
     * und damit nahezu gleichzeitig bereits erstellt wurde. </ul>
     */
    private <T> void notifyUpdateLocationWrapper(T in, Pair<String, Long> nameAndId,
            CallNotificationHelper<T> callNotificationHelper)
            throws FindException, StoreException {
        int retries = 0;
        do {
            try {
                callNotificationHelper.execute(in);
                if (retries > 0) {
                    LOGGER.warn(String.format(
                            "Ebene '%s' mit Id '%d' benoetigte %s Retries zum Aktualisieren/Ersetzen!",
                            nameAndId.getFirst(), nameAndId.getSecond(), retries));
                }
                return;
            }
            catch (HibernateOptimisticLockingFailureException | DataIntegrityViolationException | FindException
                    | StoreException e) {
                if (retries >= MAX_RETRIES) {
                    // Retries exceeded -> escape
                    throw e;
                }
                if (!checkException(e)) {
                    // Non concurrent conflict -> escape
                    throw e;
                }
                // Concurrent conflict -> retry
            }
            retries++;
        }
        while (retries <= MAX_RETRIES);
    }

    private boolean checkException(Throwable exception) {
        if (exception == null) {
            return false;
        }
        return (HibernateOptimisticLockingFailureException.class == exception.getClass())
                || (DataIntegrityViolationException.class == exception.getClass())
                || checkException(exception.getCause());
    }

    private Long getSessionId() {
        return HurricanScheduler.getSessionId();
    }

    private interface CallNotificationHelper<T> {
        void execute(T in) throws FindException, StoreException;
    }

    private class CallUpdateNotificationHelper implements CallNotificationHelper<NotifyUpdateLocation> {

        @Override
        public void execute(NotifyUpdateLocation in) throws FindException, StoreException {
            Pair<GeoIdLocation, AKWarnings> result = locationNotificationHelper.updateLocation(in, false, getSessionId());
            if (result != null && result.getSecond() != null && result.getSecond().isNotEmpty()) {
                simpleMailWarningMessage.setSubject("NotifyUpdateLocation");
                sendMailWarningHandler.handleWarning(simpleMailWarningMessage, result.getSecond(), "");
            }
        }

    }

    private class CallReplaceNotificationHelper implements CallNotificationHelper<NotifyReplaceLocation> {

        @Override
        public void execute(NotifyReplaceLocation in) throws FindException, StoreException {
            locationNotificationHelper.replaceLocation(in, false, getSessionId());
        }

    }
}
