/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 29.09.14
 */
package de.mnet.hurrican.simulator.endpoint;

import com.consol.citrus.context.TestContext;
import com.consol.citrus.context.TestContextFactory;
import com.consol.citrus.exceptions.ActionTimeoutException;
import com.consol.citrus.jms.endpoint.JmsEndpoint;
import com.consol.citrus.jms.endpoint.JmsSyncEndpoint;
import com.consol.citrus.message.Message;
import com.consol.citrus.ws.message.SoapMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;

import de.mnet.hurrican.simulator.exception.SimulatorException;
import de.mnet.hurrican.simulator.handler.SimulatorEndpointAdapter;
import de.mnet.hurrican.simulator.helper.SoapMessageHelper;

/**
 * Simulator endpoint listens on a JMS destination and calls simulator message handling process
 * for incoming messages like it is done with incoming SOAP Http requests.
 *
 *
 */
public class JmsSimulatorEndpoint implements InitializingBean, Runnable, DisposableBean {

    /** Logger */
    private static final Logger LOG = LoggerFactory.getLogger(JmsSimulatorEndpoint.class);

    @Autowired
    private SoapMessageHelper soapMessageHelper;

    @Autowired
    private TestContextFactory testContextFactory;

    /** Jms destination that is constantly listened on */
    private JmsEndpoint jmsEndpoint;

    /** Thread running the server */
    private TaskExecutor taskExecutor = new SimpleAsyncTaskExecutor();

    /** Message handler for incoming simulator request messages */
    private SimulatorEndpointAdapter endpointAdapter;

    /** Running flag */
    private boolean running = false;

    /** Should automatically start on system load */
    private boolean autoStart = true;

    @Override
    public void run() {
        LOG.info(String.format("Simulator endpoint waiting for requests on JMS destination '%s'", jmsEndpoint.getEndpointConfiguration().getDestinationName()));

        while (running) {
            try {
                TestContext context = testContextFactory.getObject();
                Message message = jmsEndpoint.createConsumer().receive(context, jmsEndpoint.getEndpointConfiguration().getTimeout());
                if (message != null) {
                    Message request = new SoapMessage(soapMessageHelper.getSoapBody(message), message.copyHeaders());
                    Message response = endpointAdapter.handleMessage(request);

                    if (response != null && jmsEndpoint instanceof JmsSyncEndpoint) {
                        jmsEndpoint.createProducer().send(soapMessageHelper.createSoapMessage(response), context);
                    }
                }
            } catch (ActionTimeoutException e) {
                // ignore timeout and continue listening for request messages.
                continue;
            } catch (SimulatorException e) {
                LOG.error("Failed to process message", e);
            } catch (Exception e) {
                LOG.error("Unexpected error while processing", e);
            }
        }
    }

    /**
     * Start up runnable in separate thread.
     */
    public void start() {
        running = true;
        taskExecutor.execute(this);
    }

    /**
     * Stop runnable execution.
     */
    public void stop() {
        running = false;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (autoStart) {
            start();
        }
    }

    @Override
    public void destroy() throws Exception {
        stop();
    }

    public void setJmsEndpoint(JmsEndpoint jmsEndpoint) {
        this.jmsEndpoint = jmsEndpoint;
    }

    public void setEndpointAdapter(SimulatorEndpointAdapter simulatorEndpointAdapter) {
        this.endpointAdapter = simulatorEndpointAdapter;
    }

    public void setAutoStart(boolean autoStart) {
        this.autoStart = autoStart;
    }
}
