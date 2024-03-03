/*
* Copyright (c) 2014 - M-net Telekommunikations GmbH
* All rights reserved.
* -------------------------------------------------------
* File created: 17.10.2014
*/
package de.mnet.wita.jmsgroup;

import javax.jms.*;
import org.apache.commons.lang.RandomStringUtils;

/**
 * Can be used for sending messages to JMS queue. Useful for ad-hoc testing.
 */
public class MsgProducer {

    Connection connection = null;
    Session session = null;
    MessageProducer msgProducer = null;
    Destination destination = null;

    private static final String VA_RRNP = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
            + "<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\">\n"
            + "  <SOAP-ENV:Header/>\n"
            + "  <SOAP-ENV:Body>\n"
            + "    <v1:requestCarrierChange xmlns:v1=\"http://www.mnet.de/esb/cdm/SupplierPartner/CarrierNegotiationService/v1\">\n"
            + "      <v1:VA-RRNP>\n"
            + "        <v1:absender>\n"
            + "          <v1:carrierCode>DEU.DTAG</v1:carrierCode>\n"
            + "        </v1:absender>\n"
            + "        <v1:empfaenger>\n"
            + "          <v1:carrierCode>DEU.MNET</v1:carrierCode>\n"
            + "        </v1:empfaenger>\n"
            + "        <v1:version>2</v1:version>\n"
            + "        <v1:endkundenvertragspartner>\n"
            + "          <v1:EKPauf>\n"
            + "            <v1:carrierCode>DEU.DTAG</v1:carrierCode>\n"
            + "          </v1:EKPauf>\n"
            + "          <v1:EKPabg>\n"
            + "            <v1:carrierCode>DEU.MNET</v1:carrierCode>\n"
            + "          </v1:EKPabg>\n"
            + "        </v1:endkundenvertragspartner>\n"
            + "        <v1:vorabstimmungsId>DEU.DTAG.VH%s</v1:vorabstimmungsId>\n"
            + "        <v1:kundenwunschtermin>2015-02-10</v1:kundenwunschtermin>\n"
            + "        <v1:endkunde>\n"
            + "          <v1:person>\n"
            + "            <v1:anrede>1</v1:anrede>\n"
            + "            <v1:vorname>John</v1:vorname>\n"
            + "            <v1:nachname>McFly</v1:nachname>\n"
            + "          </v1:person>\n"
            + "        </v1:endkunde>\n"
            + "        <v1:weitereAnschlussinhaber>\n"
            + "          <v1:person>\n"
            + "            <v1:anrede>2</v1:anrede>\n"
            + "            <v1:vorname>Kate</v1:vorname>\n"
            + "            <v1:nachname>McFly</v1:nachname>\n"
            + "          </v1:person>\n"
            + "        </v1:weitereAnschlussinhaber>\n"
            + "        <v1:projektId>\n"
            + "          <v1:projektkenner>VaRrnpAbg_02</v1:projektkenner>\n"
            + "        </v1:projektId>\n"
            + "        <v1:rufnummernPortierung>\n"
            + "          <v1:anlagenanschluss>\n"
            + "            <v1:onkzDurchwahlAbfragestelle>\n"
            + "              <v1:ONKZ>89</v1:ONKZ>\n"
            + "              <v1:durchwahlnummer>123456</v1:durchwahlnummer>\n"
            + "              <v1:abfragestelle>0</v1:abfragestelle>\n"
            + "            </v1:onkzDurchwahlAbfragestelle>\n"
            + "            <v1:zuPortierenderRufnummernblock>\n"
            + "              <v1:rnrBlockVon>00</v1:rnrBlockVon>\n"
            + "              <v1:rnrBlockBis>99</v1:rnrBlockBis>\n"
            + "            </v1:zuPortierenderRufnummernblock>\n"
            + "          </v1:anlagenanschluss>\n"
            + "          <v1:portierungszeitfenster>ZF3</v1:portierungszeitfenster>\n"
            + "          <v1:portierungskennungPKIauf>D123</v1:portierungskennungPKIauf>\n"
            + "        </v1:rufnummernPortierung>\n"
            + "      </v1:VA-RRNP>\n"
            + "    </v1:requestCarrierChange>\n"
            + "  </SOAP-ENV:Body>\n"
            + "</SOAP-ENV:Envelope>";

    public MsgProducer() {
        try {
            TextMessage msg;
            ConnectionFactory factory = new com.tibco.tibjms.TibjmsConnectionFactory(MsgConsumer.serverUrl);

            connection = factory.createConnection(MsgConsumer.userName, MsgConsumer.password);
            session = connection.createSession(false, javax.jms.Session.AUTO_ACKNOWLEDGE);

            destination = session.createQueue(MsgConsumer.queue);
            msgProducer = session.createProducer(null);
            msgProducer.setDeliveryMode(com.tibco.tibjms.Tibjms.RELIABLE_DELIVERY);
            for (int j = 0; j < 3000; j++) {
                msg = session.createTextMessage();
                msg.setText(String.format(VA_RRNP, RandomStringUtils.randomAlphanumeric(8).toUpperCase()));
                msgProducer.send(destination, msg);

            }
        }
        catch (JMSException e) {
            e.printStackTrace();
            System.exit(-1);
        }
        finally {
            try {
                if (msgProducer != null) {
                    msgProducer.close();
                }
                if (session != null) {
                    session.close();
                }
                if (connection != null) {
                    connection.close();
                }
            }
            catch (JMSException e) {
                e.printStackTrace();
            }
        }

    }

    public static void main(String[] args) {
        new MsgProducer();
    }

}
