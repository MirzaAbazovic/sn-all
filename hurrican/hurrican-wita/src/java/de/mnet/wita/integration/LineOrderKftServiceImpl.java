/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 20.10.2014
 */
package de.mnet.wita.integration;

import javax.annotation.*;
import javax.jms.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import de.mnet.common.webservice.tools.AtlasEsbConstants;
import de.mnet.wita.WitaCdmVersion;
import de.mnet.wita.service.WitaConfigService;

/**
 * Versuchen KFT-Service wieder zu entfernen und in Citrus-Test zu integerieren, wenn die WITA-Accpetancetest umgestellt
 * sind.
 *
 *
 */
public class LineOrderKftServiceImpl implements LineOrderKftService {
    private static final Logger LOGGER = LoggerFactory.getLogger(LineOrderKftService.class);

    @Resource(name = "atlasConnectionFactory")
    private ConnectionFactory atlasConnectionFactory;
    @Value("${atlas.lineorderservice.out.queue}")
    private String lineOrderServiceOutQueue;
    @Autowired
    private WitaConfigService witaConfigService;

    @Override
    public void sendXmlString(String msg, String soapAction) throws JMSException {
        WitaCdmVersion defaultWitaCdmVersion = witaConfigService.getDefaultWitaVersion();
        String queue = String.format(lineOrderServiceOutQueue, defaultWitaCdmVersion.getVersion());

        Connection connection = atlasConnectionFactory.createConnection();

            /* create the session */
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

        Destination destination = session.createQueue(queue);

            /* create the producer */
        MessageProducer msgProducer = session.createProducer(null);

                      /* create text message */
        TextMessage textMessage = session.createTextMessage();

                    /* set message text */
        textMessage.setText(msg);
        textMessage.setStringProperty(AtlasEsbConstants.SOAPACTION_TIBCO, soapAction);

                    /* publish message */
        LOGGER.info("Try to send raw XML message to ATLAS queue '{}':\n{}", queue, textMessage.getText());
        msgProducer.send(destination, textMessage);

            /* close the connection */
        connection.close();
    }
}
