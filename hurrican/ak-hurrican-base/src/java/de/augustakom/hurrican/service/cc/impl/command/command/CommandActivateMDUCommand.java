package de.augustakom.hurrican.service.cc.impl.command.command;

import java.util.*;
import de.fnt.command.custom.api.services.hurricanweb.InParamProductionReleaseOfMDUInType;
import de.fnt.command.custom.api.services.hurricanweb.ProductionReleaseOfMDUDocument;
import de.fnt.command.custom.api.services.hurricanweb.ProductionReleaseOfMDUErrorDocument;
import de.fnt.command.custom.api.services.hurricanweb.ProductionReleaseOfMDUErrorType;
import de.fnt.command.custom.api.services.hurricanweb.ProductionReleaseOfMDUInType;
import de.fnt.command.custom.api.services.hurricanweb.ProductionReleaseOfMDUResponseDocument;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.ws.client.core.WebServiceTemplate;

import de.augustakom.hurrican.model.cc.WebServiceConfig;

/**
 * Command-Klasse, um eine MDU per WebService in Command zu aktivieren.
 */
public class CommandActivateMDUCommand extends AbstractCommandCommand {

    private static final Logger LOGGER = Logger.getLogger(CommandActivateMDUCommand.class);

    public static final String KEY_SESSION_ID = "sessionId";
    public static final String KEY_MDU = "mdu";
    public static final String KEY_DATUM = "datum";

    private String sessionId = null;
    private String mdu = null;
    private Date datum = null;


    /**
     * @see de.augustakom.hurrican.service.base.impl.AbstractHurricanServiceCommand#execute()
     */
    @Override
    public Object execute() throws Exception {
        super.execute();
        WebServiceTemplate commandWSTemplate = configureAndGetWSTemplate(WebServiceConfig.WS_CFG_COMMAND_HURRICAN_API);
        LOGGER.info("Calling Command WebService");
        Object result = commandWSTemplate.marshalSendAndReceive(requestPayload);
        LOGGER.info("WebService call done");

        LOGGER.info("Begin reading WebService call result...");

        if (result instanceof ProductionReleaseOfMDUResponseDocument) {
            return null;
        }
        else if (result instanceof ProductionReleaseOfMDUErrorDocument) {
            ProductionReleaseOfMDUErrorDocument response = (ProductionReleaseOfMDUErrorDocument) result;
            ProductionReleaseOfMDUErrorType productionReleaseOfMDUErrorType = response.getProductionReleaseOfMDUError();
            String mdu = productionReleaseOfMDUErrorType.getMdu();
            String errorMessage = productionReleaseOfMDUErrorType.getErrorMsg();
            throw new Exception("Fehler für MDU: " + mdu + " " + errorMessage);
        }
        else {
            throw new Exception("Fehler für MDU: " + mdu);
        }
    }

    /**
     * @see de.augustakom.hurrican.service.cc.impl.command.command.AbstractCommandCommand#getPreparedValues()
     */
    @Override
    protected void getPreparedValues() throws Exception {
        this.sessionId = (String) getPreparedValue(CommandActivateMDUCommand.KEY_SESSION_ID);
        this.mdu = (String) getPreparedValue(CommandActivateMDUCommand.KEY_MDU);
        this.datum = (Date) getPreparedValue(CommandActivateMDUCommand.KEY_DATUM);
    }

    /**
     * @see de.augustakom.hurrican.service.cc.impl.command.command.AbstractCommandCommand#validateValues()
     */
    @Override
    protected void validateValues() throws Exception {
        if (StringUtils.isEmpty(this.sessionId)) {
            throw new IllegalArgumentException("Es muss eine Session-ID angegeben werden.");
        }

        if (StringUtils.isEmpty(this.mdu)) {
            throw new IllegalArgumentException("Es muss eine MDU angegeben werden.");
        }

        if (datum == null) {
            throw new IllegalArgumentException("Es muss ein Datum angegeben werden.");
        }
    }

    /**
     * @see de.augustakom.hurrican.service.cc.impl.command.command.AbstractCommandCommand#buildRequestPayload()
     */
    @Override
    protected void buildRequestPayload() throws Exception {
        requestPayload = ProductionReleaseOfMDUDocument.Factory.newInstance();
        InParamProductionReleaseOfMDUInType inParamProductionReleaseOfMDUInType = ((ProductionReleaseOfMDUDocument) requestPayload).addNewProductionReleaseOfMDU();
        ProductionReleaseOfMDUInType productionReleaseOfMDUInType = inParamProductionReleaseOfMDUInType.addNewInParam();

        productionReleaseOfMDUInType.setSessionId(sessionId);
        productionReleaseOfMDUInType.setMdu(mdu);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(datum.getTime());
        productionReleaseOfMDUInType.setDatum(calendar);
    }
}
