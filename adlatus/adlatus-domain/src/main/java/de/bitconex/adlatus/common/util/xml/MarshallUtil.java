package de.bitconex.adlatus.common.util.xml;

import jakarta.xml.bind.*;
import lombok.experimental.UtilityClass;

import javax.xml.namespace.QName;
import javax.xml.transform.stream.StreamSource;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringWriter;

@UtilityClass
public class MarshallUtil {

    @SuppressWarnings("unchecked")
    public static <T> String marshall(T object) throws JAXBException {
        StringWriter writer = new StringWriter();

        JAXBContext jaxbContext = JAXBContext.newInstance(object.getClass());
        Marshaller marshaller = jaxbContext.createMarshaller();

        QName rootElementName = new QName(object.getClass().getSimpleName());
        marshaller.marshal(new JAXBElement<>(rootElementName, (Class<T>) object.getClass(), null, object), writer);

        return writer.toString();
    }

    public static <T> T unmarshall(String value, Class<T> returnType) throws JAXBException {
        JAXBContext jaxbContext = JAXBContext.newInstance(returnType);
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        InputStream inputStream = new ByteArrayInputStream(value.getBytes());
        JAXBElement<T> object = unmarshaller.unmarshal(new StreamSource(inputStream), returnType);
        return object.getValue();
    }
}
