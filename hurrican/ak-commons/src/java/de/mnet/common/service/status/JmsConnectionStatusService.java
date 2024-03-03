/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 14.09.2011 09:14:38
 */
package de.mnet.common.service.status;

import java.util.concurrent.*;
import javax.jms.*;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.jms.connection.ConnectionFactoryUtils;

/**
 * Prueft die Verbindung zum JMS-Provider bzw. der ActiveMq.
 */
public class JmsConnectionStatusService implements ApplicationStatusService {

    private static final Logger LOGGER = Logger.getLogger(JmsConnectionStatusService.class);

    private String jmsFactoryName;
    private int timeoutInSeconds = 2;

    @Autowired
    private ApplicationContext applicationContext;

    @Override
    public ApplicationStatusResult getStatus() {
        final ApplicationStatusResult result = new ApplicationStatusResult();

        Callable<Void> checkConnection = new CheckConnectionTask();

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<Void> connectionCheck = executor.submit(checkConnection);
        try {
            connectionCheck.get(timeoutInSeconds, TimeUnit.SECONDS);
        }
        catch (TimeoutException e) {
            String errorString = String.format("Error obtaining JMS Connection - Timeout of %d seconds reached",
                    timeoutInSeconds);
            LOGGER.error(errorString, e);
            result.addError(errorString);
        }
        catch (Exception e) {
            LOGGER.error("Error obtaining JMS Connection", e);
            result.addError("Error obtaining JMS Connection: " + e.getMessage());
        }
        finally {
            executor.shutdown();
        }

        return result;
    }

    @Override
    public String getStatusName() {
        return "Connection to Jms-Factory " + jmsFactoryName;
    }

    public void setJmsFactoryName(String jmsFactoryName) {
        this.jmsFactoryName = jmsFactoryName;
    }

    public void setTimeoutInSeconds(int timeoutInSeconds) {
        this.timeoutInSeconds = timeoutInSeconds;
    }

    private class CheckConnectionTask implements Callable<Void> {
        Connection connection = null;
        ConnectionFactory connectionFactory = null;

        @Override
        public Void call() throws Exception {
            try {
                connectionFactory = applicationContext.getBean(jmsFactoryName, ConnectionFactory.class);
                connection = connectionFactory.createConnection();
                Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
                session.close();
            }
            finally {
                ConnectionFactoryUtils.releaseConnection(connection, connectionFactory, true);
            }
            return null;
        }
    }

}


