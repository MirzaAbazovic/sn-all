package de.augustakom.hurrican.service.wholesale;

import static de.augustakom.hurrican.service.wholesale.ReservePortRequestMapper.NotificationDetailKey.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import java.time.*;
import java.util.*;
import java.util.stream.*;
import org.testng.annotations.Test;

import de.augustakom.common.tools.lang.Pair;
import de.augustakom.common.tools.lang.RandomTools;
import de.augustakom.hurrican.model.wholesale.WholesaleContactPerson;
import de.augustakom.hurrican.model.wholesale.WholesaleEkpFrameContract;
import de.augustakom.hurrican.model.wholesale.WholesaleProduct;
import de.augustakom.hurrican.model.wholesale.WholesaleProductAttribute;
import de.augustakom.hurrican.model.wholesale.WholesaleReservePortRequest;
import de.augustakom.hurrican.model.wholesale.WholesaleReservePortResponse;
import de.mnet.esb.cdm.resource.resourceordermanagementnotificationservice.v1.ErrorType;
import de.mnet.esb.cdm.resource.resourceordermanagementnotificationservice.v1.NotificationDetailType;
import de.mnet.esb.cdm.resource.resourceordermanagementnotificationservice.v1.NotificationType;
import de.mnet.esb.cdm.resource.resourceordermanagementnotificationservice.v1.NotifyPortOrderUpdate;
import de.mnet.esb.cdm.resource.resourceordermanagementservice.v1.ContactPersonType;
import de.mnet.esb.cdm.resource.resourceordermanagementservice.v1.Ekp;
import de.mnet.esb.cdm.resource.resourceordermanagementservice.v1.Product;
import de.mnet.esb.cdm.resource.resourceordermanagementservice.v1.ReservePort;

public class ReservePortRequestMapperTest {

    private final String ORDER_ID = "ORDER_ID";
    private final String LINE_ID = "LINE_ID";
    private final LocalDate EXECUTION_DATE = LocalDate.now();
    private final String AUFTRAGS_ID = "AUFTRAGS_ID";
    private final String EXT_ORDER_ID = "EXT_ORDER_ID";
    private final long GEO_ID = 99L;
    private final long SESSION_ID = 123456L;

    /**
     * Optimistic test for the method toReservePortResponseNotification (FTTB)
     */
    @Test
    public void testToReservePortResponseNotification() throws Exception {
        final WholesaleReservePortResponse response = createWholesaleReservePortResponse(true);

        final NotifyPortOrderUpdate notifyPortOrderUpdate = ReservePortRequestMapper.toReservePortResponseNotification(response, ORDER_ID);
        final NotificationType notification = notifyPortOrderUpdate.getNotification();
        final List<NotificationDetailType> notificationDetails = notification.getNotificationDetail();

        assertNotifyPortOrderUpdate(notifyPortOrderUpdate);
        assertNotificationType(notification);
        assertNotificationDetails(response, notificationDetails);
    }

    /**
     * Optimistic test for the method toReservePortResponseNotification (FTTH) - no A10NSP, A10NSP Port and SVLAN.
     * Manuelle Portzuweisung should be true.
     */
    @Test
    public void testToReservePortResponseNotificationNoA10nspA10nspPortAndSvlan() throws Exception {
        final WholesaleReservePortResponse response = createWholesaleReservePortResponse(false);

        final NotifyPortOrderUpdate notifyPortOrderUpdate = ReservePortRequestMapper.toReservePortResponseNotification(response, ORDER_ID);
        final NotificationType notification = notifyPortOrderUpdate.getNotification();
        final List<NotificationDetailType> notificationDetails = notification.getNotificationDetail();

        assertNotifyPortOrderUpdate(notifyPortOrderUpdate);
        assertNotificationType(notification);
        assertThat(notificationDetails, hasSize(3));
        final Map<String, String> detailsMap = asMap(notificationDetails);
        assertThat(detailsMap.get(HURRICAN_AUFTRAGS_ID.name()), equalTo(AUFTRAGS_ID));
        assertThat(detailsMap.get(DPO_NOCH_NICHT_VERBAUT.name()), equalTo(String.valueOf(response.isDpoNochNichtVerbaut())));
        assertThat(detailsMap.get(MANUELLE_PORTZUWEISUNG.name()), equalTo(String.valueOf(response.isManuellePortzuweisung())));
    }

    private void assertNotificationDetails(WholesaleReservePortResponse response, List<NotificationDetailType> notificationDetails) {
        assertThat(notificationDetails, hasSize(5));
        final Map<String, String> detailsMap = asMap(notificationDetails);

        assertThat(detailsMap.get(HURRICAN_AUFTRAGS_ID.name()), equalTo(AUFTRAGS_ID));
        assertThat(detailsMap.get(A10NSP.name()), equalTo(response.getA10nsp()));
        assertThat(detailsMap.get(A10NSP_PORT.name()), equalTo(response.getA10nspPort()));
        assertThat(detailsMap.get(SVLAN_EKP.name()), equalTo(response.getSvlanEkp()));
        assertThat(detailsMap.get(DPO_NOCH_NICHT_VERBAUT.name()), equalTo(String.valueOf(response.isDpoNochNichtVerbaut())));
    }

    private Map<String, String> asMap(List<NotificationDetailType> notificationDetails) {
        return notificationDetails.stream()
                .collect(Collectors.toMap(
                        NotificationDetailType::getKey,
                        NotificationDetailType::getValue
                ));
    }

    private void assertNotificationType(NotificationType notification) {
        assertThat(notification.getLineId(), is(LINE_ID));
        assertThat(notification.getExecutionDate(), is(EXECUTION_DATE));
    }

    private void assertNotifyPortOrderUpdate(NotifyPortOrderUpdate notifyPortOrderUpdate) {
        assertThat(notifyPortOrderUpdate.getError(), nullValue(ErrorType.class));
        assertThat(notifyPortOrderUpdate.getOrderId(), is(ORDER_ID));
        assertThat(notifyPortOrderUpdate.getNotificationType(), is("reservePortResponse"));
    }

    private WholesaleReservePortResponse createWholesaleReservePortResponse(boolean isFttb) {
        WholesaleReservePortResponse response = new WholesaleReservePortResponse();
        response.setLineId(LINE_ID);
        response.setExecutionDate(EXECUTION_DATE);
        response.setHurricanAuftragId(AUFTRAGS_ID);
        if (isFttb) {
            response.setA10nsp(RandomTools.createString());
            response.setA10nspPort(RandomTools.createString());
            response.setSvlanEkp(RandomTools.createString());
        }
        else {
            response.setManuellePortzuweisung(true);
        }
        response.setDpoNochNichtVerbaut(true);
        return response;
    }

    /**
     * Optimistic test for the method toReservePortErrorNotification
     */
    @Test
    public void testToReservePortErrorNotification() throws Exception {
        final LineIdNotFoundException e = new LineIdNotFoundException(LINE_ID);

        final NotifyPortOrderUpdate notifyPortOrderUpdate = ReservePortRequestMapper.toReservePortErrorNotification(e, ORDER_ID);
        final ErrorType error = notifyPortOrderUpdate.getError();

        assertReservePortErrorNotification(notifyPortOrderUpdate);
        assertErrorType(e, error);
    }

    private void assertErrorType(LineIdNotFoundException e, ErrorType error) {
        assertThat(error.getErrorCode(), is(e.fehler.code));
        assertThat(error.getErrorDescription(), is(e.getFehlerBeschreibung()));
    }

    private void assertReservePortErrorNotification(NotifyPortOrderUpdate notifyPortOrderUpdate) {
        assertThat(notifyPortOrderUpdate.getNotificationType(), is("reservePortResponse"));
        assertThat(notifyPortOrderUpdate.getOrderId(), is(ORDER_ID));
        assertThat(notifyPortOrderUpdate.getNotification(), nullValue());
    }

    /**
     * Optimistic test for the method toWholesaleRequest
     */
    @Test
    public void testToWholesaleRequest() throws Exception {
        final Ekp ekp = ekp();
        final Product product = product();
        final String connectionUnitLocation = RandomTools.createString();
        final ReservePort.ContactPersons contactPersons = contactPersons();

        final WholesaleReservePortRequest wholesaleReservePortRequest = ReservePortRequestMapper
                .toWholesaleReservePortRequest(ORDER_ID, EXT_ORDER_ID, ekp, null, GEO_ID, contactPersons, product, EXECUTION_DATE, SESSION_ID, null, connectionUnitLocation);
        final WholesaleProduct wholesaleProduct = wholesaleReservePortRequest.getProduct();
        final Set<WholesaleProductAttribute> attributes = wholesaleProduct.getAttributes();
        final WholesaleEkpFrameContract ekpFrameContract = wholesaleReservePortRequest.getEkpFrameContract();

        assertWholesaleReservePortRequest(wholesaleReservePortRequest);
        assertWholesaleProduct(product, wholesaleProduct);
        assertWholesaleProductAttributes(attributes);
        assertEkpFrameContract(ekp, ekpFrameContract);
        assertContactPersons(contactPersons, wholesaleReservePortRequest.getContactPersons());
        assertThat(wholesaleReservePortRequest.getLageTaeOnt(), equalTo(connectionUnitLocation));
    }

    private void assertContactPersons(ReservePort.ContactPersons contactPersons, List<WholesaleContactPerson> mappedContactPersons) {
        assertThat(mappedContactPersons, hasSize(1));
        WholesaleContactPerson wholesaleContactPerson = mappedContactPersons.get(0);
        ContactPersonType contactPersonType = contactPersons.getContactPerson().get(0);
        assertThat(wholesaleContactPerson.getSalutation(), equalTo(contactPersonType.getSalutation()));
        assertThat(wholesaleContactPerson.getEmailAddress(), equalTo(contactPersonType.getEmailAddress()));
        assertThat(wholesaleContactPerson.getFaxNumber(), equalTo(contactPersonType.getFaxNumber()));
        assertThat(wholesaleContactPerson.getFirstName(), equalTo(contactPersonType.getFirstName()));
        assertThat(wholesaleContactPerson.getLastName(), equalTo(contactPersonType.getLastName()));
        assertThat(wholesaleContactPerson.getMobilePhoneNumber(), equalTo(contactPersonType.getMobilePhoneNumber()));
        assertThat(wholesaleContactPerson.getPhoneNumber(), equalTo(contactPersonType.getPhoneNumber()));
        assertThat(wholesaleContactPerson.getRole(), equalTo(contactPersonType.getRole()));
    }

    private void assertEkpFrameContract(Ekp ekp, WholesaleEkpFrameContract ekpFrameContract) {
        assertThat(ekpFrameContract.getEkpFrameContractId(), is(ekp.getFrameContractId()));
        assertThat(ekpFrameContract.getEkpId(), is(ekp.getId()));
    }

    private void assertWholesaleProductAttributes(Set<WholesaleProductAttribute> attributes) {
        assertThat(attributes, hasSize(1));
        assertThat(attributes.toArray()[0], is(WholesaleProductAttribute.TP));
    }

    private void assertWholesaleProduct(Product product, WholesaleProduct wholesaleProduct) {
        assertThat(wholesaleProduct.getName().productName, is(product.getProductName()));
        assertThat(wholesaleProduct.getGroup(), is(wholesaleProduct.getName().productGroup));
    }

    private void assertWholesaleReservePortRequest(WholesaleReservePortRequest wholesaleReservePortRequest) {
        assertThat(wholesaleReservePortRequest.getGeoId(), is(GEO_ID));
        assertThat(wholesaleReservePortRequest.getOrderId(), is(ORDER_ID));
        assertThat(wholesaleReservePortRequest.getDesiredExecutionDate(), is(EXECUTION_DATE));
        assertThat(wholesaleReservePortRequest.getExtOrderId(), is(EXT_ORDER_ID));
        assertThat(wholesaleReservePortRequest.getSessionId(), is(SESSION_ID));
    }

    private Product product() {
        Product product = new Product();
        product.setProductName("FttB BSA 50/10");
        product.setProductGroup("FTTB_BSA");
        Product.ProductAttributes productAttributes = new Product.ProductAttributes();
        final String SERVICE_ATTR = "TP";
        productAttributes.getServiceAttribute().add(SERVICE_ATTR);
        product.setProductAttributes(productAttributes);
        return product;
    }

    private Ekp ekp() {
        Ekp ekp = new Ekp();
        ekp.setFrameContractId("FRAME_CONTRACT_ID");
        ekp.setId("EKP_ID");
        return ekp;
    }

    private ReservePort.ContactPersons contactPersons() {
        ReservePort.ContactPersons contactPersons = new ReservePort.ContactPersons();
        ContactPersonType contactPerson = new ContactPersonType();
        contactPerson.setFirstName("FIRSTNAME");
        contactPerson.setLastName("LASTNAME");
        contactPerson.setFaxNumber("FAX_NUMBER");
        contactPerson.setMobilePhoneNumber("MOBILE_PHONE_NUMBER");
        contactPerson.setEmailAddress("EMAIL_ADDRESS");
        contactPerson.setSalutation("SALUTATION");
        contactPerson.setRole("ENDKUNDE");
        contactPersons.getContactPerson().add(contactPerson);
        return contactPersons;
    }

    @Test
    public void testTBauauftragChangedSuccessNotification() throws Exception {
        NotifyPortOrderUpdate notifyPortOrderUpdate = ReservePortRequestMapper.toBauauftragChangedSuccessNotification(ORDER_ID);

        assertThat(notifyPortOrderUpdate.getOrderId(), is(ORDER_ID));
        assertThat(notifyPortOrderUpdate.getNotificationType(), is("bauauftragChangedResponse"));
    }


    @Test
    public void testTBauauftragChangedErrorNotification() throws Exception {
        Pair<Long, String> tCode = Pair.create(1307L, "Sonstiges");
        NotifyPortOrderUpdate notifyPortOrderUpdate = ReservePortRequestMapper.toBauauftragChangedErrorNotification(ORDER_ID, tCode);

        assertThat(notifyPortOrderUpdate.getOrderId(), is(ORDER_ID));
        assertThat(notifyPortOrderUpdate.getNotificationType(), is("bauauftragChangedResponse"));
        assertThat(notifyPortOrderUpdate.getError().getErrorCode(), is("1307"));
        assertThat(notifyPortOrderUpdate.getError().getErrorDescription(), is("Sonstiges"));
    }

}