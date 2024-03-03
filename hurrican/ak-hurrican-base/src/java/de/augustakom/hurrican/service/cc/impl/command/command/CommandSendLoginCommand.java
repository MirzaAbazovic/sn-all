package de.augustakom.hurrican.service.cc.impl.command.command;

import namespace.webservice.api.user.InParamLogin;
import namespace.webservice.api.user.LoginDocument;
import namespace.webservice.api.user.LoginInType;
import namespace.webservice.api.user.LoginResponseDocument;
import namespace.webservice.api.user.OutParam;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import de.augustakom.hurrican.model.cc.WebServiceConfig;

/**
 * Command-Klasse, um einen Login am Command-System per WebService durchzufuehren.
 */
public class CommandSendLoginCommand extends AbstractCommandCommand {

    private static final Logger LOGGER = Logger.getLogger(CommandSendLoginCommand.class);

    private String sessionId = null;
    private static final String SESSIONID = "sessionid";

    /**
     * @see de.augustakom.hurrican.service.base.impl.AbstractHurricanServiceCommand#execute()
     */
    @Override
    public Object execute() throws Exception {
        super.execute();
        WebServiceTemplate commandWSTemplate = configureAndGetWSTemplate(WebServiceConfig.WS_CFG_COMMAND);

        LOGGER.info("Calling Command WebService login");
        Object result = commandWSTemplate.marshalSendAndReceive(requestPayload);
        LOGGER.info("WebService call done");
        LOGGER.info("Begin reading WebService call result...");

        if (result instanceof LoginResponseDocument) {
            LoginResponseDocument loginResponseDocument = (LoginResponseDocument) result;
            OutParam outParam = loginResponseDocument.getLoginResponse();
            Node outParamNode = outParam.getDomNode();
            Node firstChild = outParamNode.getFirstChild();
            NodeList nodeList = firstChild.getChildNodes();

            for (int i = 0; i < nodeList.getLength(); i++) {
                Node item = nodeList.item(i);
                String nodeName = item.getNodeName();

                if (StringUtils.equalsIgnoreCase(nodeName, CommandSendLoginCommand.SESSIONID)) {
                    Node valueNode = item.getFirstChild();
                    this.sessionId = valueNode.getNodeValue();
                    break;
                }
            }
        }
        else {
            throw new Exception("Fehler beim Login im Command-System");
        }
        return this.sessionId;
    }

    /**
     * @see de.augustakom.hurrican.service.cc.impl.command.command.AbstractCommandCommand#getPreparedValues()
     */
    @Override
    protected void getPreparedValues() throws Exception {
    }

    /**
     * @see de.augustakom.hurrican.service.cc.impl.command.command.AbstractCommandCommand#validateValues()
     */
    @Override
    protected void validateValues() throws Exception {
    }

    /**
     * @see de.augustakom.hurrican.service.cc.impl.command.command.AbstractCommandCommand#buildRequestPayload()
     */
    protected void buildRequestPayload() throws Exception {
        loadWsConfigTemplate(WebServiceConfig.WS_CFG_COMMAND);
        requestPayload = LoginDocument.Factory.newInstance();
        InParamLogin inParam = ((LoginDocument) requestPayload).addNewLogin();
        LoginInType loginType = inParam.addNewInParam();
        loginType.setUsername(wsCfg.getAppSecurementUser());
        loginType.setPassword(wsCfg.getAppSecurementPassword());
    }
}
