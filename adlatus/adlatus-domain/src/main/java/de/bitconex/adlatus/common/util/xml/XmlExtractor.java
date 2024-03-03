package de.bitconex.adlatus.common.util.xml;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import de.bitconex.adlatus.wholebuy.provision.dto.enums.XmlExtractorEnum;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import java.io.StringReader;
import javax.xml.xpath.XPathConstants;

@Service
public class XmlExtractor {


    // todo: couldnt this be - getProperty(xml, propertyName) ? then this can be renamed to XmlUtils in common module
    // the only wita relevant part is XmlExtractorEnum
    public String getMeldungstypType(String xmlString) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new InputSource(new StringReader(xmlString)));

            XPathFactory xPathfactory = XPathFactory.newInstance();
            XPath xpath = xPathfactory.newXPath();

            String meldungstypType = (String) xpath.compile(XmlExtractorEnum.MELDUNGSTYP_TYPE.getExpression()).evaluate(doc, XPathConstants.STRING);
            return meldungstypType.split(":")[1];

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getZeitstempel(String xmlString) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new InputSource(new StringReader(xmlString)));

            XPathFactory xPathfactory = XPathFactory.newInstance();
            XPath xpath = xPathfactory.newXPath();

            String zeitstempel = (String) xpath.compile(XmlExtractorEnum.ZEITSTEMPEL.getExpression()).evaluate(doc, XPathConstants.STRING);
            return zeitstempel.toString();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getInterfaceVersion(String xmlString) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new InputSource(new StringReader(xmlString)));

            XPathFactory xPathfactory = XPathFactory.newInstance();
            XPath xpath = xPathfactory.newXPath();

            String interfaceVersion = (String) xpath.compile(XmlExtractorEnum.INTERFACE_VERSION.getExpression()).evaluate(doc, XPathConstants.STRING);
            return interfaceVersion;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}