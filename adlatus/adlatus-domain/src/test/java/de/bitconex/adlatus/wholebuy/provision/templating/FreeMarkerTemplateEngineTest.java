package de.bitconex.adlatus.wholebuy.provision.templating;

import de.bitconex.adlatus.common.infrastructure.exception.GenerateMessageException;
import de.bitconex.adlatus.common.templating.FreeMarkerTemplateEngine;
import de.bitconex.adlatus.wholebuy.provision.dto.LocationDTO;
import de.bitconex.adlatus.wholebuy.provision.dto.PersonDTO;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class FreeMarkerTemplateEngineTest {

    @Test
    void process_ExecutesWithoutExceptions_ReturnsExpectedString() throws IOException, TemplateException {
        FreeMarkerTemplateEngine engine = new FreeMarkerTemplateEngine();
        var dataModel = getDataModel();


        String result = engine.process(dataModel, "wita-create-order.ftlh");

        assertThat(result).contains("<majorRelease>15</majorRelease>");
        assertThat(result).contains("<minorRelease>1</minorRelease>");
        assertThat(result).contains("<kundennummer>123456789</kundennummer>");
        assertThat(result).contains("<leistungsnummer>987654321</leistungsnummer>");
        assertThat(result).contains("<externeAuftragsnummer>123456789</externeAuftragsnummer>");
        assertThat(result).contains("<vorname>Max</vorname>");
        assertThat(result).contains("<nachname>Mustermann</nachname>");
        assertThat(result).contains("<bezeichner>product</bezeichner>");
        assertThat(result).contains("<produkt xsi:type=\"ns2:yourProductType\">");
        assertThat(result).contains("<geschaeftsfallProdukt xsi:type=\"ns2:yourOrderType\">");

    }

    @Test
    void process_ThrowsIOExceptionWhileGettingTemplate_ThrowsGenerateMessageException() throws IOException {

        Configuration cfg = mock(Configuration.class);
        when(cfg.getTemplate(anyString())).thenThrow(IOException.class);
        FreeMarkerTemplateEngine engine = new FreeMarkerTemplateEngine(cfg);


        assertThrows(GenerateMessageException.class, () -> engine.process(new Object(), "templateName"));
    }

    @Test
    void process_ThrowsTemplateExceptionWhileProcessingTemplate_ThrowsGenerateMessageException() throws IOException, TemplateException {

        Configuration cfg = mock(Configuration.class);
        Template template = mock(Template.class);
        doThrow(TemplateException.class).when(template).process(any(), any());
        when(cfg.getTemplate(anyString())).thenReturn(template);
        FreeMarkerTemplateEngine engine = new FreeMarkerTemplateEngine(cfg);


        assertThrows(GenerateMessageException.class, () -> engine.process(new Object(), "templateName"));
    }

    HashMap<String, Object> getDataModel() {
        var dataModel = new HashMap<String, Object>();
        var resourceOrder = new HashMap<String, Object>();
        var agreement = new HashMap<String, String>();
        var messageInfo = new HashMap<String, Object>();
        // todo: create custom builder for this, or something similar..
        messageInfo.put("timestamp", OffsetDateTime.now());

        agreement.put("majorInterfaceVersion", "15");
        agreement.put("minorInterfaceVersion", "1");

        agreement.put("customerNumber", "123456789");
        agreement.put("agreementNumber", "987654321");

        agreement.put("productId", "product");

        agreement.put("productType", "yourProductType");
        agreement.put("businessCase", "yourBusinessCase");
        agreement.put("orderType", "yourOrderType");

        PersonDTO orderManagementContactPerson = PersonDTO.builder()
                .firstName("Max")
                .lastName("Mustermann")
                .email("ordermanag@mail.com")
                .phone("0123456789")
                .salutation(1)
                .build();

        resourceOrder.put("orderManagementContactPerson", orderManagementContactPerson);

        PersonDTO endCustomer = PersonDTO.builder()
                .firstName("Max")
                .lastName("Mustermann")
                .salutation(1)
                .build();

        resourceOrder.put("endCustomer", endCustomer);

        PersonDTO technicianContact = PersonDTO.builder()
                .firstName("Max")
                .lastName("Mustermann")
                .email("testmail@tset.com")
                .phone("0123456789")
                .salutation(1).build();

        // add endCustomerLocation
        LocationDTO endCustomerLocation = LocationDTO.builder()
                .street("street")
                .houseNumber("houseNumber")
                .zipCode("zipCode")
                .city("city")
                .country(LocationDTO.Country.DE)
                .build();

        resourceOrder.put("endCustomerLocation", endCustomerLocation);

        resourceOrder.put("technicianContact", technicianContact);

        resourceOrder.put("additionalServices", List.of("additionalServices"));
        resourceOrder.put("id", "123456789");

        resourceOrder.put("requestedCompletionDate", new Date(2022, 11, 1));

        resourceOrder.put("requestedCompletionTime", 3);

        dataModel.put("messageInfo", messageInfo);
        dataModel.put("order", resourceOrder);
        dataModel.put("agreement", agreement);

        return dataModel;
    }
}