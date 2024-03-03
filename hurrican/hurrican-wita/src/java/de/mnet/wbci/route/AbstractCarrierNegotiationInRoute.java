/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 27.08.13
 */
package de.mnet.wbci.route;

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
import de.mnet.wbci.route.helper.MessageProcessingMetadataHelper;
import de.mnet.wbci.route.processor.carriernegotiation.in.CreateMessageProcessingMetadataProcessor;
import de.mnet.wbci.route.processor.carriernegotiation.in.CustomerServiceProtocolInProcessor;
import de.mnet.wbci.route.processor.carriernegotiation.in.PostProcessInProcessor;
import de.mnet.wbci.route.processor.carriernegotiation.in.StoreWbciMessageProcessor;
import de.mnet.wbci.route.processor.carriernegotiation.in.WbciIoArchiveInProcessor;

/**
 * Camel-Route um eingehende WBCI Nachrichten zu empfangen und zu verarbeiten. <br> Die Route uebernimmt dann auch das
 * Unmarshalling vom Atlas CDM Format in das Hurrican WBCI Format. <br> <br> <br> <b>Description for the transaction
 * handling in the route:</b><br> After a number of attempts, the final solution for the TX-handling was to create 3
 * Camel Routes to handle the processing of a Inbound WBCI message: <ul> <li>The 1st route (WbciInRoute) starts the JMS
 * Tx (Tx1) on reading of the message from the queue. <li>The 2nd route (WrapRouteInTransaction) starts a new JMS Tx
 * (Tx2) for wrapping any JMS messages sent during the processing of the incoming message (for example automatically
 * sending out an ABBM) <li>The 3rd route (WbciInBizRoute) starts a new DB Tx (Tx3) for wrapping any DB changes. </ul>
 * <p/>
 * If all goes well the transactions are committed in the following order Tx3, Tx2 & Tx1.
 * <p/>
 * If an error occurs then its handled according to where the exception was thrown: <ul> <li>If thrown by a processor in
 * route 3, then the OnError handler in route 3 catches the exception, sets a flag in the exchange to indicate that the
 * error service should handle the error and marks Tx3 for rollback. Route 2 checks on completion of route 3 if an error
 * occurred, detects the Error and rolls back Tx2. Route 1 also detects that an error occurred, generates and sends an
 * error to the error service and commits Tx1. <li>If an error occurs on commit of Tx3, then the OnError handler in
 * route 2 (NOT route 3) catches the exception, sets a flag in the exchange to indicate that the error service should
 * handle the error and marks Tx2 for rollback. Tx3 is automatically rolled-back (as a result of the exception on Tx3
 * commit). Route 1 detects that an error occurred, generates and sends an error to the error service and commits Tx1.
 * <li>If an error occurs on commit of Tx2, then the OnError handler in route 1 (NOT route 2) catches the exception,
 * generates and sends an error to the error service and commits Tx1. Tx2 is automatically rolled-back, however Tx3 is
 * already committed and cannot be rolled back. However due to the likelihood of Tx3 being successfully committed and
 * Tx2 failing being so low, this is acceptable. <li>If an error on commit of Tx1, then the global OnError handler logs
 * the exception and rolls back Tx1, causing the message to be re-delivered and reprocessed later on. </ul>
 * <p/>
 * After a number of attempts this was the only solution that worked. The main problems encountered were: <ul> <li>Camel
 * does not call the Route's OnError handler when a transaction commit fails, even though the transaction is started by
 * the route. To get around this all routes must have a onError handler. <li>the onError handler in a transacted route
 * can either mark the current transaction for rollback or all transactions. However when a route starts 2 transactions
 * (as was the case in the previous solution) then it was not possible to rollback Tx2 and Tx3, but commit Tx1. To get
 * around this only a single transaction can be started per route. </ul>
 */
public abstract class AbstractCarrierNegotiationInRoute extends SpringRouteBuilder implements WbciCamelConstants {

    @Autowired
    protected RouteConfigHelper routeConfigHelper;

    @Autowired
    private WbciIoArchiveInProcessor wbciIoArchiveInProcessor;

    @Autowired
    private PostProcessInProcessor postProcessInProcessor;

    @Autowired
    private StoreWbciMessageProcessor storeWbciMessageProcessor;

    @Autowired
    private GenerateErrorMessageProcessor generateErrorMessageProcessor;

    @Autowired
    private MarshalErrorMessageProcessor marshalErrorMessageProcessor;

    @Autowired
    private HurricanExceptionLogErrorHandler hurricanExceptionLogErrorHandler;

    @Autowired
    private CustomerServiceProtocolInProcessor customerServiceProtocolInProcessor;

    @Autowired
    private CreateMessageProcessingMetadataProcessor createMessageProcessingMetadataProcessor;

    @Autowired
    private MessageProcessingMetadataHelper processingHelper;

    @Override
    public void configure() throws Exception {
        onException(Throwable.class).bean(hurricanExceptionLogErrorHandler);

        addWbciInRoute(getWbciInRouteId(), getWbciInUri(), getWbciInEmsTxUri(), getErrorServiceOutUri());
        addWrapRouteInTransaction(getWbciInEmsTxRouteId(), getWbciInEmsTxUri(), getWbciInBizUri(), "ems.requires_new");
        addWbciInBizRoute(getWbciInBizRouteId(), getWbciInBizUri());
    }

    /**
     * This routes consumes JMS messages from the Wbci-In-Queue and forwards them to the {@link #getWbciInEmsTxUri()}
     * route for further processing. On successful completion of the {@link #getWbciInEmsTxUri()} the JMS transaction is
     * committed, thus removing the JMS Message from the queue. On unsuccessful completion the ErrorHandlingService from
     * AtlasESB is invoked with the details of the original JMS message and the error that occurred. If the
     * ErrorHandlingService invocation succeeds then the JMS transaction is committed, otherwise the exception is
     * propagated further, causing the JMS transaction to be rolled-back. (In this case the JMS message still remains in
     * the ATLAS queue, with the re-delivered flag set.)
     */
    public void addWbciInRoute(String routeId, String routeUri, String toRouteUri, String errorServiceUri) {
        // @formatter:off
        from(routeUri)
            .onException(Exception.class)
                .log(LoggingLevel.ERROR, "Exception caught in WBCI-In route. Preparing ErrorHandlingService message. Details: ${exception.stacktrace}")
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
     * This route is responsible for processing the WBCI JMS Messages received from AtlasESB. A new database transaction
     * is started on route entry and committed when the route completes successfully. If an exception is thrown during
     * processing the database is marked for rollback and the {@link ExceptionHelper#ERROR_SERVICE_MESSAGE_KEY}
     * property is set within the exchange.
     */
    private void addWbciInBizRoute(String routeId, String routeUri) {
        // @formatter:off
        from(routeUri)
            .onException(Exception.class)
                .log(LoggingLevel.ERROR, "Exception caught in WBCI-In-Biz route. Preparing ErrorHandlingService message. Details: ${exception.stacktrace}")
                .process(generateErrorMessageProcessor)
                .log(LoggingLevel.INFO, "ErrorMessage generated successfully")
                .markRollbackOnlyLast()
            .end()
            .transacted("db.requires_new")
            .routeId(routeId)
            // add results container for storing the results of the message processing
            .process(createMessageProcessingMetadataProcessor)
            // convert CDM format to Hurrican WBCI Meldung
            .process(getCdmToWbciMeldungProcessor())
            // eingehende Meldung/Requests speichern und verarbeiten (ueber eigene Services)
            .process(storeWbciMessageProcessor)
            .choice()
                .when(processingHelper.isIncomingMessageDuplicateVaRequest())
                    .log(LoggingLevel.INFO, "Skipping recording of VA request in IoArchive since this is a duplicate request")
                .endChoice()
                .otherwise()
                    // create a IoArchive bean from the incoming message and CDM format and send it to history service
                    // bean to store the created IOArchive object
                    .process(wbciIoArchiveInProcessor)
                .endChoice()
            .end()
            .choice()
                .when(processingHelper.isPostProcessMessage())
                    .log(LoggingLevel.INFO, "Post-processing message")
                    .process(postProcessInProcessor)
                    .process(customerServiceProtocolInProcessor)
                .endChoice()
                .otherwise()
                    .log(LoggingLevel.INFO, "Skipping Post-processing of message since the message was not stored")
                .endChoice()
            .end()
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

    protected abstract String getWbciInBizRouteId();

    protected abstract String getWbciInEmsTxRouteId();

    /**
     * Liefert eine Instanz des {@code Processor}s zurueck, der fuer die Umwandlung des CDM-Formats in das Hurrican WBCI
     * Format verwendet werden soll.
     *
     * @return
     */
    protected abstract Processor getCdmToWbciMeldungProcessor();

    /**
     * Definiert eine Id fuer die Route.
     *
     * @return
     */
    protected abstract String getWbciInRouteId();

    /**
     * Liefert die zu verwendende Camel-Komponente fuer die eingehenden Meldungen zurueck (i.d.R. Zugriff auf den Atlas
     * ESB).
     *
     * @return
     */
    protected abstract String getInComponent();

    /**
     * Liefert den Namen / die URL der IN-Komponente zurueck (i.d.R. der Queue-Name des Atlas-ESB, auf der die
     * eingehenden WBCI Nachrichten bereitgestellt werden.)
     *
     * @return
     */
    protected abstract String getCarrierNegotiationServiceIn();

    /**
     * Returns the inbound route parameters such as concurrent consumers.
     */
    protected abstract String getInParameters();

    protected String getWbciInUri() {
        return String.format("%s:%s%s", getInComponent(), getCarrierNegotiationServiceIn(), getInParameters());
    }

    protected String getWbciInBizUri() {
        return String.format("direct:%s", getWbciInBizRouteId());
    }

    protected String getWbciInEmsTxUri() {
        return String.format("direct:%s", getWbciInEmsTxRouteId());
    }

    protected String getErrorServiceOutUri() {
        return String.format("%s:%s", getInComponent(), routeConfigHelper.getErrorHandlingServiceOut());
    }

}
