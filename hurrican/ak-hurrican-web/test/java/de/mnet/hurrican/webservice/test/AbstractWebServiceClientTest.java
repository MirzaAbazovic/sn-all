/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 18.03.2009 11:44:40
 */
package de.mnet.hurrican.webservice.test;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Abstrakte Klasse fuer WebService-Clients Tests.
 *
 *
 */
public abstract class AbstractWebServiceClientTest {

    protected static final String DEFAULT_BEAN_ID_WS_TEMPLATE = "ws.template";

    private static final Logger LOGGER = Logger.getLogger(AbstractWebServiceClientTest.class);

    /**
     * Bean-ID im Application-Context, ueber die das WebService-Template definiert ist. Diese kann jedoch ueberschrieben
     * werden
     */
    private String wsTemplateBeanId = DEFAULT_BEAN_ID_WS_TEMPLATE;

    protected WebServiceTemplate wsTemplate;

    /**
     * Methode, um den WebService-Test durchzufuehren. <br> Die Methode ruft <code>customSendAndReceive</code> auf, in
     * der die eigentliche Test-Logik enthalten ist.
     *
     *
     */
    @Test
    public void test() {
        try {
            ApplicationContext appCtx =
                    new ClassPathXmlApplicationContext(getApplicationCtxConfigFile());

            wsTemplate = appCtx.getBean(wsTemplateBeanId, WebServiceTemplate.class);

            if (getDefaultURI() != null) {
                wsTemplate.setDefaultUri(getDefaultURI());
            }

            // Default-URI setzen, falls von ableitender Klasse definiert
            if (StringUtils.isBlank(getURISuffix())) {
                throw new Exception("Suffix for WebService URI not defined");
            }

            wsTemplate.setDefaultUri(wsTemplate.getDefaultUri() + "/" + getURISuffix());

            if (StringUtils.isBlank(wsTemplate.getDefaultUri())) {
                throw new Exception("Default-URI for WebService is not defined!");
            }

            customSendAndReceive(wsTemplate);
        }
        catch (Exception e) {
            Assert.fail(e.getMessage());
        }
    }

    /**
     * Gibt das (Spring) Konfig-File an, ueber den der Application-Context definiert ist. <br> Default:
     * ClientApplicationContext.xml <br> Kann von den Clients ueberschrieben werden, um ein anderes Konfig-File zu
     * verwenden. <br> <br> In dem Application-Kontext wird eine Bean vom Typ <code>org.springframework.ws.client.core.WebServiceTemplate</code>
     * mit dem Name <code>ws.template</code> erwartet. <br> Das Template ist mit den notwendigen Parametern (z.B.
     * Un-/Marshaller) zu konfigurieren.
     *
     * @return Dateiname des zu verwendenen Konfig-Files fuer den Spring Application-Context
     *
     */
    protected String getApplicationCtxConfigFile() {
        return "de/mnet/hurrican/webservice/test/DefaultClientApplicationContext.xml";
    }

    /**
     * Muss vom Client ueberschrieben werden, um den Suffix der WebService URI zu definieren. <br> Bsp.:  <br> gesamte
     * URI: http://localhost:8080/hurricanweb/loadtest <br> Suffix:      loadtest
     *
     * @return
     *
     */
    protected abstract String getURISuffix();

    /**
     * Kann vom Client ueberschrieben werden, um die Default-URI fuer den WebService zu ueberschreiben, z.B.:
     * http://localhost:8080/hurricanweb
     *
     * @return
     *
     */
    protected String getDefaultURI() {
        return null;
    }

    /**
     * Methode, die von den Clients implementiert werden soll, um einen Web-Service aufzurufen.
     *
     * @param wsTemplate WebService-Template, ueber das der WebService aufgerufen werden kann.
     *
     */
    protected abstract void customSendAndReceive(WebServiceTemplate wsTemplate) throws Exception;

    /**
     * Setter zum ueberschreiben der BeanId, die als Webservice-Template verwendet wird
     */
    public void setWsTemplateBeanId(String wsTemplateBeanId) {
        this.wsTemplateBeanId = wsTemplateBeanId;
    }
}
