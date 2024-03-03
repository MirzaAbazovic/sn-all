package de.augustakom.hurrican.service.cc.impl.command.command;

import de.fnt.command.api.common.CmdInfo;
import namespace.webservice.api.user.InParamLogout;
import namespace.webservice.api.user.LogoutDocument;
import namespace.webservice.api.user.LogoutInType;
import namespace.webservice.api.user.LogoutResponseDocument;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.ws.client.core.WebServiceTemplate;

import de.augustakom.hurrican.model.cc.WebServiceConfig;

/**
 * Command-Klasse, um einen Logout am Command-System per WebService durchzufuehren.
 */
public class CommandSendLogoutCommand extends AbstractCommandCommand {

    private static final Logger LOGGER = Logger.getLogger(CommandSendLogoutCommand.class);

    public static final String KEY_SESSION_ID = "sessionId";

    private String sessionId = null;

    /**
     * @see de.augustakom.hurrican.service.base.impl.AbstractHurricanServiceCommand#execute()
     */
    @Override
    public Object execute() throws Exception {
        super.execute();

        LOGGER.info("Calling Command WebService logout");
        WebServiceTemplate commandWSTemplate = configureAndGetWSTemplate(WebServiceConfig.WS_CFG_COMMAND);
        Object result = commandWSTemplate.marshalSendAndReceive(requestPayload);
        LOGGER.info("WebService call done");

        LOGGER.info("Begin reading WebService call result...");

        if (result instanceof LogoutResponseDocument) {
            LogoutResponseDocument responseDocument = (LogoutResponseDocument) result;
            CmdInfo cmdInfo = responseDocument.getLogoutResponse();
            cmdInfo.getExitstatus();
            cmdInfo.getInfoMessage();
        }
        return null;
    }

    /**
     * @see de.augustakom.hurrican.service.cc.impl.command.command.AbstractCommandCommand#getPreparedValues()
     */
    @Override
    protected void getPreparedValues() throws Exception {
        sessionId = (String) getPreparedValue(KEY_SESSION_ID);
    }

    /**
     * @see de.augustakom.hurrican.service.cc.impl.command.command.AbstractCommandCommand#validateValues()
     */
    @Override
    protected void validateValues() throws Exception {
        if (StringUtils.isEmpty(sessionId)) {
            throw new Exception("Es muss eine SessionId angegeben werden.");
        }
    }

    /**
     * @see de.augustakom.hurrican.service.cc.impl.command.command.AbstractCommandCommand#buildRequestPayload()
     */
    protected void buildRequestPayload() throws Exception {
        requestPayload = LogoutDocument.Factory.newInstance();
        InParamLogout inParam = ((LogoutDocument) requestPayload).addNewLogout();
        LogoutInType logoutInType = inParam.addNewInParam();
        logoutInType.setSessionId(sessionId);
    }
}
