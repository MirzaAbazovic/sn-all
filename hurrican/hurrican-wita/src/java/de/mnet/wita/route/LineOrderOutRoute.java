/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 02.10.2014
 */
package de.mnet.wita.route;

import org.apache.camel.LoggingLevel;
import org.apache.camel.spring.SpringRouteBuilder;
import org.springframework.beans.factory.annotation.Autowired;

import de.mnet.common.exceptions.HurricanExceptionLogErrorHandler;
import de.mnet.common.route.ExtractExchangeOptionsProcessor;
import de.mnet.common.route.helper.RouteConfigHelper;
import de.mnet.wbci.model.MessageProcessingMetadata;
import de.mnet.wita.route.processor.lineorder.out.ConvertWitaToCdmProcessor;
import de.mnet.wita.route.processor.lineorder.out.CreateSendLogProcessor;
import de.mnet.wita.route.processor.lineorder.out.EvaluateCdmVersionProcessor;
import de.mnet.wita.route.processor.lineorder.out.SetStatusTransferredProcessor;
import de.mnet.wita.route.processor.lineorder.out.WitaIoArchiveOutProcessor;

/**
 *
 */
public class LineOrderOutRoute extends SpringRouteBuilder implements WitaCamelConstants {

    @Autowired
    protected RouteConfigHelper routeConfigHelper;
    @Autowired
    protected ConvertWitaToCdmProcessor convertWitaToCdmProcessor;
    @Autowired
    protected EvaluateCdmVersionProcessor evaluateCdmVersionProcessor;
    @Autowired
    protected HurricanExceptionLogErrorHandler hurricanExceptionLogErrorHandler;
    @Autowired
    private ExtractExchangeOptionsProcessor extractExchangeOptionsProcessor;
    @Autowired
    private WitaIoArchiveOutProcessor witaIoArchiveOutProcessor;
    @Autowired
    private SetStatusTransferredProcessor setStatusTransferredProcessor;
    @Autowired
    private CreateSendLogProcessor createSendLogProcessor;

    @Override
    public void configure() throws Exception {
        onException(Throwable.class).bean(hurricanExceptionLogErrorHandler);
        addLineOrderRoutes();
    }

    /**
     * Builds Camel out route for sending line order service requests to Atlas ESB with additional business
     * logic such as IO archive entries.
     * <p/>
     * If the outgoing message is the result of a communication error (e.g. ABBM Message to carriers that have sent an
     * invalid Request or Meldung to M-Net, like Duplicate Requests, TV sent by donating carrier, etc), the WBCI
     * database can be hold clean and consistent, by not persist the invalid inbound message from the carrier. Also the
     * corresponding outbound message shouldn't be persisted.
     * <p/>
     * When invoking this route, the caller can additionally determine if a record of the outbound message is a response
     * : <ul> <li>to an invalid request, set {@link MessageProcessingMetadata#isPostProcessMessage()}<br> <b>=>
     * postprocessing will be skipped</b> </br></li> <li>to a duplicated VA request, set {@link
     * MessageProcessingMetadata#isResponseToDuplicateVaRequest}<br> <b>=> only IO archive entry will be skipped</b>
     * </br></li> </ul>
     */
    private void addLineOrderRoutes() {
        // @formatter:off
        from("direct:atlasLineOrderOut")
            .transacted("ems.required")
            .inOnly(String.format("%s:queue:atlasCdmQueue", routeConfigHelper.getAtlasOutComponent()))
            .end()
            .log(LoggingLevel.INFO, "Message successfully sent to Atlas ESB")
        .end();

        from("direct:lineOrderService")
            .transacted("db.required")
            .routeId(WITA_LINE_ORDER_OUT_ROUTE)
            .process(extractExchangeOptionsProcessor)
            // Destination-URL ermitteln, da versions-abhaengig
            .process(evaluateCdmVersionProcessor)
            // convert the Hurrican WITA request to CDM format
            .process(convertWitaToCdmProcessor)
            // send CDM format to Atlas
            .to("direct:atlasLineOrderOut")
            // update CbVorgang Status
            .process(setStatusTransferredProcessor)
            // create send log
            .process(createSendLogProcessor)
            .process(witaIoArchiveOutProcessor)
            .end()
            .log(LoggingLevel.INFO, "Message processing complete")
        .end();
        // @formatter:on
    }

}
