/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 06.10.2014
 */
package de.mnet.wita.route;

import static de.mnet.common.route.helper.ExceptionHelper.*;

import org.apache.camel.LoggingLevel;
import org.apache.camel.Processor;
import org.apache.camel.spring.SpringRouteBuilder;
import org.springframework.beans.factory.annotation.Autowired;

import de.mnet.common.errorhandling.processor.GenerateErrorMessageProcessor;
import de.mnet.common.errorhandling.processor.MarshalErrorMessageProcessor;
import de.mnet.common.exceptions.HurricanExceptionLogErrorHandler;
import de.mnet.common.route.helper.ExceptionHelper;
import de.mnet.common.route.helper.RouteConfigHelper;
import de.mnet.wita.route.processor.lineorder.in.StoreWitaMessageInProcessor;
import de.mnet.wita.route.processor.lineorder.in.WitaIoArchiveInProcessor;

public abstract class AbstractLineOrderInRoute extends SpringRouteBuilder implements WitaCamelConstants {

    @Autowired
    protected RouteConfigHelper routeConfigHelper;

    @Autowired
    private WitaIoArchiveInProcessor ioArchiveInProcessor;

    @Autowired
    private StoreWitaMessageInProcessor storeWitaInProcessor;

    @Autowired
    private GenerateErrorMessageProcessor generateErrorMessageProcessor;

    @Autowired
    private MarshalErrorMessageProcessor marshalErrorMessageProcessor;

    @Autowired
    private HurricanExceptionLogErrorHandler hurricanExceptionLogErrorHandler;

    @Override
    public void configure() throws Exception {
        onException(Throwable.class).bean(hurricanExceptionLogErrorHandler);
        addLineOrderInRoute(getLineOrderInRouteId(), getLineOrderInUri(), getLineOrderInEmsTxUri(), getErrorServiceOutUri());
        addWrapRouteInTransaction(getLineOrderInEmsTxRouteId(), getLineOrderInEmsTxUri(), getLineOrderInBizUri(), "ems.requires_new");
        addLineOrderInBizRoute(getLineOrderInBizRouteId(), getLineOrderInBizUri());
    }

    /**
     * This routes consumes JMS messages from the LineOrder-In-Queue and forwards them to the {@link
     * #getLineOrderInEmsTxUri()} route for further processing. On successful completion of the {@link
     * #getLineOrderInEmsTxUri()} the JMS transaction is committed, thus removing the JMS Message from the queue. On
     * unsuccessful completion the ErrorHandlingService from AtlasESB is invoked with the details of the original JMS
     * message and the error that occurred. If the ErrorHandlingService invocation succeeds then the JMS transaction is
     * committed, otherwise the exception is propagated further, causing the JMS transaction to be rolled-back. (In this
     * case the JMS message still remains in the ATLAS queue, with the re-delivered flag set.)
     */

    public void addLineOrderInRoute(String routeId, String routeUri, String toRouteUri, String errorServiceUri) {
        // @formatter:off
        from(routeUri)
            .onException(Exception.class)
                .log(LoggingLevel.ERROR, "Exception caught in LineOrder-In route. Preparing ErrorHandlingService message. Details: ${exception.stacktrace}")
                .process(generateErrorMessageProcessor)
                .process(marshalErrorMessageProcessor)
                .inOnly(errorServiceUri)
                .log(LoggingLevel.INFO, "ErrorHandlingService message processed successfully")
                .handled(true)
            .end()
            .transacted("ems.required")
            .routeId(routeId)
            .log(LoggingLevel.INFO, "AtlasESB message received")
            .to(toRouteUri)
            .choice()
                .when(property(ERROR_SERVICE_MESSAGE_KEY).isNotNull())
                    .log(LoggingLevel.INFO, String.format("Detected %s key in exchange", ERROR_SERVICE_MESSAGE_KEY))
                    .process(marshalErrorMessageProcessor)
                    .inOnly(errorServiceUri)
                    .log(LoggingLevel.INFO, "ErrorHandlingService message processed successfully")
                .endChoice()
                .otherwise()
                    .log(LoggingLevel.INFO, "AtlasESB message processed successfully")
                .endChoice()
            .end()
            .end()
        ;
        // @formatter:on
    }

    /**
     * This route is responsible for processing the LineOrder JMS Messages received from AtlasESB. A new database
     * transaction is started on route entry and committed when the route completes successfully. If an exception is
     * thrown during processing the database is marked for rollback and the {@link ExceptionHelper#ERROR_SERVICE_MESSAGE_KEY}
     * property is set within the exchange.
     */
    private void addLineOrderInBizRoute(String routeId, String routeUri) {
        // @formatter:off
        from(routeUri)
            .onException(Exception.class)
                .log(LoggingLevel.ERROR, "Exception caught in LineOrder-In-Biz route. Preparing ErrorHandlingService message. Details: ${exception.stacktrace}")
                .process(generateErrorMessageProcessor)
                .log(LoggingLevel.INFO, "ErrorMessage generated successfully")
                .markRollbackOnlyLast()
            .end()
            .transacted("db.requires_new")
            .routeId(routeId)
            // convert CDM to Wita Line Order format
            .process(getCdmToLineOrderMeldungProcessor())
            // eingehende Meldung/Requests speichern und verarbeiten
            .process(storeWitaInProcessor)
            // create a IoArchive bean from the incoming message and CDM format and send it to history service
            // bean to store the created IOArchive object
            .process(ioArchiveInProcessor)
            .log(LoggingLevel.INFO, "Message processing complete")
            .end()
        ;
        // @formatter:on
    }

    /**
     * Transactional route, for wrapping and invoking another route within a transactional context. The route checks if
     * the wrapped route completed with errors by checking if the {@link ExceptionHelper#ERROR_SERVICE_MESSAGE_KEY}
     * property is set within the exchange and marks the transaction for roll back when found. If an exception is thrown
     * within the route itself then the transaction is also marked for roll back.
     */
    private void addWrapRouteInTransaction(String routeId, String routeUri, String toRouteUri, String txPolicy) {
        // @formatter:off
        from(routeUri)
            .onException(Exception.class)
                .log(LoggingLevel.ERROR, "Exception caught in route. Preparing ErrorHandlingService message. Details: ${exception.stacktrace}")
                .process(generateErrorMessageProcessor)
                .log(LoggingLevel.INFO, String.format("Marking transaction %s for rollback", txPolicy))
                .markRollbackOnlyLast()
            .end()
            .transacted(txPolicy)
            .routeId(routeId)
            .log(LoggingLevel.INFO, String.format("Invoking %s", toRouteUri))
            .to(toRouteUri)
            .choice()
                .when(property(ERROR_SERVICE_MESSAGE_KEY).isNotNull())
                    .log(LoggingLevel.INFO, String.format(
                            "Detected %s key in exchange after invoking %s. Marking transaction %s for rollback",
                            ERROR_SERVICE_MESSAGE_KEY, toRouteUri, txPolicy))
                    .markRollbackOnlyLast()
                .endChoice()
                .otherwise()
                    .log(LoggingLevel.INFO, String.format("%s completed successfully", toRouteUri))
                .endChoice()
            .end()
        ;
        // @formatter:on
    }

    protected abstract String getLineOrderInBizRouteId();

    protected abstract String getLineOrderInEmsTxRouteId();

    /**
     * Liefert eine Instanz des {@code Processor}s zurueck, der fuer die Umwandlung des CDM-Formats in das WITA Format
     * verwendet werden soll.
     *
     * @return
     */
    protected abstract Processor getCdmToLineOrderMeldungProcessor();

    /**
     * Definiert eine Id fuer die Route.
     *
     * @return
     */
    protected abstract String getLineOrderInRouteId();

    /**
     * Liefert die zu verwendende Camel-Komponente fuer die eingehenden Meldungen zurueck (i.d.R. Zugriff auf den Atlas
     * ESB).
     *
     * @return
     */
    protected abstract String getInComponent();

    /**
     * Liefert den Namen / die URL der IN-Komponente zurueck (i.d.R. der Queue-Name des Atlas-ESB, auf der die
     * eingehenden Line Order Nachrichten bereitgestellt werden.)
     *
     * @return
     */
    protected abstract String getLineOrderIn();

    /**
     * Returns the inbound route parameters such as concurrent consumers.
     */
    protected abstract String getInParameters();

    protected String getLineOrderInUri() {
        return String.format("%s:%s%s", getInComponent(), getLineOrderIn(), getInParameters());
    }

    protected String getLineOrderInBizUri() {
        return String.format("direct:%s", getLineOrderInBizRouteId());
    }

    protected String getLineOrderInEmsTxUri() {
        return String.format("direct:%s", getLineOrderInEmsTxRouteId());
    }

    protected String getErrorServiceOutUri() {
        return String.format("%s:%s", getInComponent(), routeConfigHelper.getErrorHandlingServiceOut());
    }
}
