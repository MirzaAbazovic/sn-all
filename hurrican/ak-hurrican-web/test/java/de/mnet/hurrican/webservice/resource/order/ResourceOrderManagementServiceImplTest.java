package de.mnet.hurrican.webservice.resource.order;

import static com.google.common.collect.Iterables.*;
import static de.augustakom.common.tools.lang.RandomTools.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

import java.time.*;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.common.tools.lang.RandomTools;
import de.augustakom.hurrican.model.wholesale.WholesaleModifyPortReservationDateRequest;
import de.augustakom.hurrican.model.wholesale.WholesaleModifyPortReservationDateResponse;
import de.augustakom.hurrican.model.wholesale.WholesaleProductAttribute;
import de.augustakom.hurrican.model.wholesale.WholesaleProductName;
import de.augustakom.hurrican.model.wholesale.WholesaleReleasePortRequest;
import de.augustakom.hurrican.model.wholesale.WholesaleReservePortRequest;
import de.augustakom.hurrican.model.wholesale.WholesaleReservePortResponse;
import de.augustakom.hurrican.service.wholesale.ModifyPortPendingException;
import de.augustakom.hurrican.service.wholesale.TechnischNichtMoeglichException;
import de.augustakom.hurrican.service.wholesale.WholesaleException;
import de.augustakom.hurrican.service.wholesale.WholesaleService;
import de.mnet.esb.cdm.resource.resourceordermanagementnotificationservice.v1.ErrorType;
import de.mnet.esb.cdm.resource.resourceordermanagementnotificationservice.v1.NotificationType;
import de.mnet.esb.cdm.resource.resourceordermanagementnotificationservice.v1.NotifyPortOrderUpdate;
import de.mnet.esb.cdm.resource.resourceordermanagementnotificationservice.v1.ResourceOrderManagementNotificationService;
import de.mnet.esb.cdm.resource.resourceordermanagementservice.v1.Ekp;
import de.mnet.esb.cdm.resource.resourceordermanagementservice.v1.Product;

@Test(groups = BaseTest.UNIT)
public class ResourceOrderManagementServiceImplTest {

    public static final String RESERVE_PORT_RESPONSE = "reservePortResponse";
    public static final String RELEASE_PORT_RESPONSE = "releasePortResponse";

    @Mock
    WholesaleService wholesaleService;

    @Mock
    ResourceOrderManagementNotificationService notificationService;

    @InjectMocks
    @Spy   //notwendig weil getSession() nur mithilfe statischer Methoden implementiert werden kann
            ResourceOrderManagementServiceImpl cut;

    private final String orderId = createString();
    private final String extOrderId = createString();
    private final Ekp ekp = createEkp();
    private final Product.ProductAttributes productAttributes = createProductAttributes();
    private final Product product = createProduct(productAttributes, null);
    private final LocalDate execDate = LocalDate.now();
    private final String lineId = createString();

    @BeforeMethod
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    private String randomProductAttribute() {
        return randomEnumValueOf(WholesaleProductAttribute.class).name();
    }

    private String randomProductName() {
        return randomEnumValueOf(WholesaleProductName.class).productName;
    }

    @Test
    public void testReservePortFailure() throws Exception {
        final WholesaleException wholesaleException = new TechnischNichtMoeglichException(createString());

        when(wholesaleService.reservePort(any(WholesaleReservePortRequest.class)))
                .thenThrow(wholesaleException);
        cut.reservePort(orderId, extOrderId, ekp, null, createLong(),null, product, execDate, null, RandomTools.createString());

        verifyAndAssertErrorNotification(orderId, wholesaleException, RESERVE_PORT_RESPONSE);
    }

    private void verifyAndAssertErrorNotification(String orderId, WholesaleException wholesaleException, String notificationType) {
        final ArgumentCaptor<NotifyPortOrderUpdate> captor = ArgumentCaptor.forClass(NotifyPortOrderUpdate.class);
        verify(notificationService).notifyPortOrderUpdate(captor.capture());

        final NotifyPortOrderUpdate capturedUpdate = captor.getValue();
        assertThat(capturedUpdate.getNotificationType(), equalTo(notificationType));
        assertThat(capturedUpdate.getOrderId(), equalTo(orderId));
        assertThat(capturedUpdate.getError().getErrorCode(), equalTo(wholesaleException.fehler.code));
        assertThat(capturedUpdate.getError().getErrorDescription(), equalTo(wholesaleException.getFehlerBeschreibung()));
        assertThat(capturedUpdate.getNotification(), nullValue(NotificationType.class));
    }

    @Test
    public void testReservePortSuccessful() throws Exception {
        final WholesaleReservePortResponse wholesaleReservePortResponse = createWholesaleReservePortResponse();
        Product fttb50Product = createProduct(productAttributes, WholesaleProductName.FTTB_50.productName);

        when(wholesaleService.reservePort(any(WholesaleReservePortRequest.class))).thenReturn(wholesaleReservePortResponse);
        cut.reservePort(orderId, extOrderId, ekp, null, createLong(),null,  fttb50Product, execDate, null, RandomTools.createString());

        verifyAndAssertPortReservation(orderId, extOrderId, ekp, productAttributes, fttb50Product, execDate);
        verifyAndAssertNotification(orderId, wholesaleReservePortResponse);
    }

    @Test
    public void testReservePortFtth50() throws Exception {
        final WholesaleReservePortResponse wholesaleReservePortResponse = createWholesaleReservePortResponse();
        Product ftth50Product = createProduct(productAttributes, WholesaleProductName.FTTH_50.productName);
        wholesaleReservePortResponse.setLineId(null);
        wholesaleReservePortResponse.setExecutionDate(null);

        when(wholesaleService.reservePort(any(WholesaleReservePortRequest.class))).thenReturn(wholesaleReservePortResponse);
        cut.reservePort(orderId, extOrderId, ekp, null, createLong(), null, ftth50Product, execDate, null, RandomTools.createString());

        verifyAndAssertPortReservation(orderId, extOrderId, ekp, productAttributes, ftth50Product, execDate);
        verifyAndAssertNotificationFtth(orderId);
    }

    /**
     * Test f&uuml;r {@link ResourceOrderManagementServiceImpl#releasePort(String, String, LocalDate)}.
     * <p>
     * Test f&uuml;r eine fehlerhafte Freigabe.
     */
    @Test
    public void testReleasePortFailure() throws Exception {
        final WholesaleException wholesaleException = new TechnischNichtMoeglichException(createString());

        when(wholesaleService.releasePort(any(WholesaleReleasePortRequest.class))).thenThrow(wholesaleException);
        cut.releasePort(orderId, lineId, LocalDate.now());

        verifyAndAssertErrorNotification(orderId, wholesaleException, RELEASE_PORT_RESPONSE);
    }

    /**
     * Test f&uuml;r {@link ResourceOrderManagementServiceImpl#releasePort(String, String, LocalDate)}.
     * <p>
     * * Test f&uuml;r eine erfolgreiche Freigabe.
     */
    @Test
    public void testReleasePortSuccessful() {
        when(wholesaleService.releasePort(createWholesaleReleasePortRequest())).thenReturn(execDate);

        cut.releasePort(orderId, lineId, LocalDate.now());

        verifyAndAssertPortReleaseNotification(orderId, lineId, execDate);
    }

    @Test
    public void testModifyPortReservationDateSuccess() {
        final WholesaleModifyPortReservationDateResponse result = createWholesaleModifyPortReservationDateResponse();
        when(wholesaleService.modifyPortReservationDate(any(WholesaleModifyPortReservationDateRequest.class))).thenReturn(result);

        cut.modifyPortReservationDate(orderId, lineId, execDate);

        verifyAndAssertModifyPortReservationDate();
        verifyAndAssertModifyPortReservationDateNotifyOrderUpdate();
    }

    @Test
    public void testModifyPortReservationDateFailure() {
        final WholesaleException exception = new ModifyPortPendingException();
        when(wholesaleService.modifyPortReservationDate(any(WholesaleModifyPortReservationDateRequest.class))).thenThrow(exception);

        cut.modifyPortReservationDate(orderId, lineId, execDate);

        verifyAndAssertModifyPortReservationDateErrorNotification(exception);
    }

    private void verifyAndAssertModifyPortReservationDateErrorNotification(WholesaleException exception) {
        final ArgumentCaptor<NotifyPortOrderUpdate> errorNotification = ArgumentCaptor.forClass(NotifyPortOrderUpdate.class);
        verify(notificationService).notifyPortOrderUpdate(errorNotification.capture());
        assertThat(errorNotification.getValue().getNotificationType(), equalTo("modifyPortReservationDateNotification"));
        assertThat(errorNotification.getValue().getError().getErrorCode(), equalTo(exception.fehler.code));
        assertThat(errorNotification.getValue().getError().getErrorDescription(), equalTo(exception.getFehlerBeschreibung()));
    }

    private WholesaleModifyPortReservationDateResponse createWholesaleModifyPortReservationDateResponse() {
        final WholesaleModifyPortReservationDateResponse result = new WholesaleModifyPortReservationDateResponse();
        result.setExecutionDate(execDate);
        result.setLineId(lineId);
        return result;
    }

    private void verifyAndAssertModifyPortReservationDateNotifyOrderUpdate() {
        final ArgumentCaptor<NotifyPortOrderUpdate> update = ArgumentCaptor.forClass(NotifyPortOrderUpdate.class);
        verify(notificationService).notifyPortOrderUpdate(update.capture());
        assertThat(update.getValue().getNotificationType(), equalTo("modifyPortReservationDateNotification"));
        assertThat(update.getValue().getOrderId(), equalTo(orderId));
        assertThat(update.getValue().getNotification().getLineId(), equalTo(lineId));
        assertThat(update.getValue().getNotification().getExecutionDate(), equalTo(execDate));
    }

    private void verifyAndAssertModifyPortReservationDate() {
        final ArgumentCaptor<WholesaleModifyPortReservationDateRequest> req =
                ArgumentCaptor.forClass(WholesaleModifyPortReservationDateRequest.class);
        verify(wholesaleService).modifyPortReservationDate(req.capture());
        assertThat(req.getValue().getDesiredExecutionDate(), equalTo(execDate));
        assertThat(req.getValue().getLineId(), equalTo(lineId));
    }


    private Ekp createEkp() {
        final Ekp ekp = new Ekp();
        ekp.setId(createString());
        ekp.setFrameContractId(createString());
        return ekp;
    }

    private Product.ProductAttributes createProductAttributes() {
        final Product.ProductAttributes productAttributes = new Product.ProductAttributes();
        productAttributes.getServiceAttribute().add(randomProductAttribute());
        return productAttributes;
    }

    private Product createProduct(Product.ProductAttributes productAttributes, String productName) {
        final Product product = new Product();
        String prodName = productName;
        if (prodName == null) {
            prodName = randomProductName();
        }
        product.setProductName(prodName);
        product.setProductAttributes(productAttributes);
        return product;
    }

    private WholesaleReservePortResponse createWholesaleReservePortResponse() {
        final WholesaleReservePortResponse wholesaleReservePortResponse = new WholesaleReservePortResponse();
        wholesaleReservePortResponse.setLineId(createString());
        wholesaleReservePortResponse.setExecutionDate(LocalDate.now());
        return wholesaleReservePortResponse;
    }

    private WholesaleReleasePortRequest createWholesaleReleasePortRequest() {
        WholesaleReleasePortRequest releasePortRequest = new WholesaleReleasePortRequest();
        releasePortRequest.setLineId(lineId);
        releasePortRequest.setSessionId(null);
        return releasePortRequest;
    }

    private void verifyAndAssertNotification(String orderId, WholesaleReservePortResponse wholesaleReservePortResponse) {
        final ArgumentCaptor<NotifyPortOrderUpdate> notifyArgumentCaptor =
                ArgumentCaptor.forClass(NotifyPortOrderUpdate.class);
        verify(notificationService).notifyPortOrderUpdate(notifyArgumentCaptor.capture());
        final NotifyPortOrderUpdate capturedNotify = notifyArgumentCaptor.getValue();

        verifyAndAssertNotificationCommon(orderId, capturedNotify);
        assertThat(capturedNotify.getNotification().getLineId(), equalTo(wholesaleReservePortResponse.getLineId()));
        assertThat(capturedNotify.getNotification().getExecutionDate(), equalTo(wholesaleReservePortResponse.getExecutionDate()));
    }

    private void verifyAndAssertNotificationFtth(String orderId) {
        final ArgumentCaptor<NotifyPortOrderUpdate> notifyArgumentCaptor =
                ArgumentCaptor.forClass(NotifyPortOrderUpdate.class);

        verify(notificationService).notifyPortOrderUpdate(notifyArgumentCaptor.capture());

        final NotifyPortOrderUpdate capturedNotify = notifyArgumentCaptor.getValue();

        verifyAndAssertNotificationCommon(orderId, capturedNotify);
        assertThat(capturedNotify.getNotification().getLineId(), nullValue());
        assertThat(capturedNotify.getNotification().getExecutionDate(), nullValue());
    }

    private void verifyAndAssertNotificationCommon(String orderId, NotifyPortOrderUpdate capturedNotify) {
        assertThat(capturedNotify.getNotificationType(), equalTo(RESERVE_PORT_RESPONSE));
        assertThat(capturedNotify.getOrderId(), equalTo(orderId));
        assertThat(capturedNotify.getError(), nullValue(ErrorType.class));
    }

    private void verifyAndAssertPortReleaseNotification(String orderId, String lineId, LocalDate execDate) {
        final ArgumentCaptor<NotifyPortOrderUpdate> notifyArgumentCaptor =
                ArgumentCaptor.forClass(NotifyPortOrderUpdate.class);
        verify(notificationService).notifyPortOrderUpdate(notifyArgumentCaptor.capture());
        final NotifyPortOrderUpdate capturedNotify = notifyArgumentCaptor.getValue();
        assertThat(capturedNotify.getNotificationType(), equalTo(RELEASE_PORT_RESPONSE));
        assertThat(capturedNotify.getNotification().getLineId(), equalTo(lineId));
        assertThat(capturedNotify.getNotification().getExecutionDate(), equalTo(execDate));
        assertThat(capturedNotify.getOrderId(), equalTo(orderId));
        assertThat(capturedNotify.getError(), nullValue(ErrorType.class));
    }

    private void verifyAndAssertPortReservation(String orderId, String extOrderId, Ekp ekp, Product.ProductAttributes productAttributes, Product product, LocalDate execDate) {
        final ArgumentCaptor<WholesaleReservePortRequest> requestArgumentCaptor =
                ArgumentCaptor.forClass(WholesaleReservePortRequest.class);

        verify(wholesaleService).reservePort(requestArgumentCaptor.capture());
        final WholesaleReservePortRequest capturedRequest = requestArgumentCaptor.getValue();
        assertThat(capturedRequest.getOrderId(), equalTo(orderId));
        assertThat(capturedRequest.getExtOrderId(), equalTo(extOrderId));
        assertThat(capturedRequest.getEkpFrameContract().getEkpId(), equalTo(ekp.getId()));
        assertThat(capturedRequest.getEkpFrameContract().getEkpFrameContractId(), equalTo(ekp.getFrameContractId()));
        assertThat(capturedRequest.getProduct().getName().productName, equalTo(product.getProductName()));
        assertThat(getOnlyElement(capturedRequest.getProduct().getAttributes()).name(),
                equalTo(getOnlyElement(productAttributes.getServiceAttribute())));
        assertThat(capturedRequest.getDesiredExecutionDate(), equalTo(execDate));
    }
}