/*
* Copyright (c) 2014 - M-net Telekommunikations GmbH
* All rights reserved.
* -------------------------------------------------------
* File created: 17.10.2014
*/
package de.mnet.wita.jmsgroup;


import java.util.*;
import javax.jms.*;

public class MsgConsumer implements ExceptionListener, MessageListener {

    public static final String queue = "mnet.Hurrican.op.cdm.dst.v1.CarrierNegotiationService";
    public static final String serverUrl = "tcp://localhost:7222,tcp://localhost:7223";
    public static final String userName = "hurrican";
    public static final String password = "";

    /*-----------------------------------------------------------------------
     * Variables
     *----------------------------------------------------------------------*/
    Connection connection = null;
    Session session = null;
    MessageConsumer msgConsumer = null;
    Destination destination = null;
    final String consumerId;

    public MsgConsumer(String consumerId) {
        this.consumerId = consumerId;
        System.err.println("Starting consumer " + consumerId);
        try {
            ConnectionFactory factory = new com.tibco.tibjms.TibjmsConnectionFactory(serverUrl);

            /* create the connection */
            connection = factory.createConnection(userName, password);

            /* create the session */
            session = connection.createSession(false, javax.jms.Session.AUTO_ACKNOWLEDGE);

            /* set the exception listener */
            connection.setExceptionListener(this);

            /* create the destination */
            destination = session.createQueue(queue);

            /* create the consumer */
            msgConsumer = session.createConsumer(destination);

            /* set the message listener */
            msgConsumer.setMessageListener(this);

            /* start the connection */
            connection.start();

            // Note: when message callback is used, the session
            // creates the dispatcher thread which is not a daemon
            // thread by default. Thus we can quit this method however
            // the application will keep running. It is possible to
            // specify that all session dispatchers are daemon threads.
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*---------------------------------------------------------------------
     * onException
     *---------------------------------------------------------------------*/
    public void onException(JMSException e) {
        /* print the connection exception status */
        System.err.println("CONNECTION EXCEPTION: " + e.getMessage());
    }

    /*---------------------------------------------------------------------
     * onMessage
     *---------------------------------------------------------------------*/
    public void onMessage(Message msg) {
        try {
            System.err.println(String.format("%s : %s", consumerId, ((TextMessage)msg).getText()));
            Thread.sleep(randomInterval());
        }
        catch (Exception e) {
            System.err.println("Unexpected exception in the message callback!");
            e.printStackTrace();
            System.exit(-1);
        }
    }

    public static int randomInterval() {
        Random rand = new Random();
        int randomNum = rand.nextInt((1000 - 100) + 1) + 100;
        return randomNum;
    }

    public static void main(String[] args) {
        new MsgConsumer("" + System.currentTimeMillis());
    }
}
