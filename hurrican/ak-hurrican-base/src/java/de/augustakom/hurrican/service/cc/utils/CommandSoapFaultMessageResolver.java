/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 24.06.2009 15:47:27
 */
package de.augustakom.hurrican.service.cc.utils;

import java.io.*;
import java.lang.reflect.*;
import java.util.*;
import javax.xml.transform.dom.*;
import de.fnt.command.api.common.CmdSoapException;
import de.fnt.command.custom.api.services.hurricanweb.ProductionReleaseOfMDUErrorType;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.SystemUtils;
import org.springframework.ws.WebServiceMessage;
import org.springframework.ws.client.WebServiceFaultException;
import org.springframework.ws.soap.SoapBody;
import org.springframework.ws.soap.SoapFaultDetail;
import org.springframework.ws.soap.SoapFaultDetailElement;
import org.springframework.ws.soap.SoapMessage;
import org.springframework.ws.soap.client.core.SoapFaultMessageResolver;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


/**
 * FaultMessageResolver, um Command Fault-Messages in eine WebServiceFaultException umzuwandeln. <br>
 *
 *
 */
public class CommandSoapFaultMessageResolver extends SoapFaultMessageResolver {
    final static String UNKNOWN_ERROR = "Unbekannter Fehler!";

    /**
     * @see org.springframework.ws.soap.client.core.SoapFaultMessageResolver#resolveFault(org.springframework.ws.WebServiceMessage)
     */
    @Override
    public void resolveFault(WebServiceMessage message) throws IOException {
        SoapMessage soapMessage = (SoapMessage) message;
        SoapBody soapBody = soapMessage.getSoapBody();
        SoapFaultDetail detail = soapBody.getFault().getFaultDetail();

        if (detail != null) {
            new CommandExceptionBuilder(detail);
        }
    }

    /**
     * Klasse zum Auslesen der Exception von Command
     */
    class CommandExceptionExtractor {
        private final static String NEWINSTANCE = "newInstance";
        private final static String CMD_EXCEPTION = "ns1:cmdException";
        private final static String EXCEPTION_NAME = "ns2:exceptionName";
        private final static String HOSTNAME = "ns3:hostname";
        private final static String STANDORT = "standort";
        private final static String MDU = "mdu";
        private final static String AUFTRAGSNUMMER_HURRICAN = "auftragsnummerHurrican";
        private final static String ERRORMSG = "errorMsg";
        private final static String TYPE = "xsi:type";
        private final static String NS = "xmlns:ns1";
        private final static String ERRORTYPE = "errorType";
        private final static String ERRORCODE = "errorCode";
        private final static String ERRORMESSAGE = "errorMessage";
        private final static String ERRORMESSAGELONG = "errorMessageLong";
        private SoapFaultDetail detail;
        private String type;
        private String ns;
        private String exceptionName;
        private String hostName;
        private Map<String, String> childMap = new HashMap<>();

        public CommandExceptionExtractor(SoapFaultDetail detail) throws CommandWebServiceFaultException {
            this.detail = detail;

            if (detail == null) {
                throw new CommandWebServiceFaultException("Es muss ein detail angegeben werden");
            }
            extract();
        }

        /**
         * Ermittelt die Details
         */
        @SuppressWarnings("unchecked")
        protected void extract() throws CommandWebServiceFaultException {
            Iterator<SoapFaultDetailElement> detailIterator = detail.getDetailEntries();

            while (detailIterator.hasNext()) {
                SoapFaultDetailElement fault = detailIterator.next();
                DOMSource domSource = (DOMSource) fault.getSource();
                Node detailNode = domSource.getNode();

                if (detailNode.getNodeName().equals(CommandExceptionExtractor.CMD_EXCEPTION)) {
                    getExceptionDetails(detailNode);
                }

                if (detailNode.getNodeName().equals(CommandExceptionExtractor.EXCEPTION_NAME)) {
                    exceptionName = getValue(detailNode);
                }

                if (detailNode.getNodeName().equals(CommandExceptionExtractor.HOSTNAME)) {
                    hostName = getValue(detailNode);
                }
            }
        }

        /**
         * @return
         * @throws Exception
         */
        protected Object getExceptionInstance() throws CommandWebServiceFaultException {
            if (null != exceptionName) {
                try {
                    Class<?> clazz = Class.forName(exceptionName);
                    Class<?>[] clazzes = clazz.getClasses();

                    if ((clazzes != null) && (clazzes.length == 1)) {
                        Class<?> factoryClass = clazzes[0];

                        if (factoryClass != null) {
                            Method method = factoryClass.getDeclaredMethod(CommandExceptionExtractor.NEWINSTANCE, (Class<?>[]) null);

                            if (method != null) {
                                return method.invoke(null, (Object[]) null);
                            }
                        }
                    }
                }
                catch (Exception e) {
                    throw new CommandWebServiceFaultException(e.getMessage());
                }
            }

            throw new CommandWebServiceFaultException(UNKNOWN_ERROR);
        }

        /**
         * Ermittelt die Details der Exception
         */
        private void getExceptionDetails(Node detailNode) {
            if (detailNode != null) {
                getAttributes(detailNode);
                getChilds(detailNode);
            }
        }

        /**
         * @param detailNode
         */
        private void getChilds(Node detailNode) {
            NodeList childNodes = detailNode.getChildNodes();

            for (int i = 0; i < childNodes.getLength(); i++) {
                Node child = childNodes.item(i);
                childMap.put(child.getNodeName(), getValue(child));
            }
        }

        /**
         * @param detailNode
         */
        private void getAttributes(Node detailNode) {
            NamedNodeMap attributeMap = detailNode.getAttributes();

            for (int z = 0; z < attributeMap.getLength(); z++) {
                Node attribute = attributeMap.item(z);

                if (StringUtils.equals(attribute.getNodeName(), CommandExceptionExtractor.TYPE)) {
                    type = getValue(attribute);
                }

                if (StringUtils.equals(attribute.getNodeName(), CommandExceptionExtractor.NS)) {
                    ns = getValue(attribute);
                }
            }
        }

        /**
         * get the value
         */
        private String getValue(Node node) {
            String value = null;

            if (node != null) {
                Node valueNode = node.getFirstChild();

                if (valueNode != null) {
                    value = valueNode.getNodeValue();
                }
            }
            return value;
        }

        /**
         * @return the type
         */
        public String getType() {
            return type;
        }

        /**
         * @return the ns
         */
        public String getNs() {
            return ns;
        }

        /**
         * @return the exceptionName
         */
        public String getExceptionName() {
            return exceptionName;
        }

        /**
         * @return the hostName
         */
        public String getHostName() {
            return hostName;
        }

        /**
         * @return the childMap
         */
        public Map<String, String> getChildMap() {
            return childMap;
        }
    }

    /**
     * Klasse zum Erzeugen der Exception
     */
    class CommandExceptionBuilder {
        CommandExceptionExtractor commandExceptionExtractor;

        /**
         * @param detail
         * @throws CommandWebServiceFaultException
         */
        public CommandExceptionBuilder(SoapFaultDetail detail) throws CommandWebServiceFaultException {
            if (detail == null) {
                throw new CommandWebServiceFaultException("Es muss ein SoapFaultDetail angegeben werden.");
            }

            this.commandExceptionExtractor = new CommandExceptionExtractor(detail);

            buildException();
        }

        /**
         * @throws CommandWebServiceFaultException
         */
        private void buildException() throws CommandWebServiceFaultException {
            Object exceptionInstance = commandExceptionExtractor.getExceptionInstance();

            if (exceptionInstance instanceof ProductionReleaseOfMDUErrorType) {
                ((ProductionReleaseOfMDUErrorType) exceptionInstance).setMdu(commandExceptionExtractor.getChildMap().get(CommandExceptionExtractor.MDU));
                ((ProductionReleaseOfMDUErrorType) exceptionInstance).setErrorMsg(commandExceptionExtractor.getChildMap().get(CommandExceptionExtractor.ERRORMSG));
                throw new CommandProductionReleaseOfMDUWebServiceFaultException((ProductionReleaseOfMDUErrorType) exceptionInstance, commandExceptionExtractor.getHostName());
            }
            else if (exceptionInstance instanceof CmdSoapException) {
                ((CmdSoapException) exceptionInstance).setErrorType(commandExceptionExtractor.getChildMap().get(CommandExceptionExtractor.ERRORTYPE));
                ((CmdSoapException) exceptionInstance).setErrorCode(Integer.parseInt(commandExceptionExtractor.getChildMap().get(CommandExceptionExtractor.ERRORCODE)));
                ((CmdSoapException) exceptionInstance).setErrorMessage(commandExceptionExtractor.getChildMap().get(CommandExceptionExtractor.ERRORMESSAGE));
                ((CmdSoapException) exceptionInstance).setErrorMessageLong(commandExceptionExtractor.getChildMap().get(CommandExceptionExtractor.ERRORMESSAGELONG));
                throw new CommandCmdSoapExceptionWebServiceFaultException((CmdSoapException) exceptionInstance, commandExceptionExtractor.getHostName());
            }
            else {
                throw new CommandWebServiceFaultException(UNKNOWN_ERROR + " Host: "
                        + commandExceptionExtractor.getHostName());
            }
        }
    }

    /**
     * Klasse fuer Exceptions von Command
     */
    class CommandWebServiceFaultException extends WebServiceFaultException {
        final static String HOSTNAME = "host";
        private static final long serialVersionUID = -1801737696454267193L;

        /**
         * @param msg
         */
        public CommandWebServiceFaultException(String msg) {
            super(msg);
        }
    }

    /**
     * Klasse fuer Exceptions von Command (ProductionReleaseOfMDU)
     *
     *
     */
    class CommandProductionReleaseOfMDUWebServiceFaultException extends CommandWebServiceFaultException {
        private static final long serialVersionUID = 4127871154151102900L;

        public CommandProductionReleaseOfMDUWebServiceFaultException(ProductionReleaseOfMDUErrorType productionReleaseOfMDUErrorType, String hostname) {
            super(new StringBuilder(CommandExceptionExtractor.MDU).append(" : ")
                    .append(productionReleaseOfMDUErrorType.getMdu())
                    .append(SystemUtils.LINE_SEPARATOR)
                    .append(CommandExceptionExtractor.ERRORMSG).append(" : ")
                    .append(productionReleaseOfMDUErrorType.getErrorMsg())
                    .append(SystemUtils.LINE_SEPARATOR)
                    .append(HOSTNAME).append((" : "))
                    .append(hostname)
                    .toString());
        }
    }

    /**
     * Klasse fuer Exceptions von Command (CmdSoapException)
     *
     *
     */
    class CommandCmdSoapExceptionWebServiceFaultException extends CommandWebServiceFaultException {
        private static final long serialVersionUID = -6138333046503882075L;

        /**
         * @param cmdSoapException
         */
        public CommandCmdSoapExceptionWebServiceFaultException(CmdSoapException cmdSoapException, String hostname) {
            super(new StringBuilder(CommandExceptionExtractor.ERRORCODE).append(" : ")
                    .append(cmdSoapException.getErrorCode()).append(SystemUtils.LINE_SEPARATOR)
                    .append(CommandExceptionExtractor.ERRORTYPE).append(" : ")
                    .append(cmdSoapException.getErrorType()).append(SystemUtils.LINE_SEPARATOR)
                    .append(CommandExceptionExtractor.ERRORMESSAGE).append(" : ")
                    .append(cmdSoapException.getErrorMessage()).append(SystemUtils.LINE_SEPARATOR)
                    .append(CommandExceptionExtractor.ERRORMESSAGELONG).append(" : ")
                    .append(cmdSoapException.getErrorMessageLong())
                    .append(HOSTNAME).append((" : "))
                    .append(hostname)
                    .toString());
        }
    }
}
