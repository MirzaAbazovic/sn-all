/**
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 27.08.2009 13:34:08
 */
package de.augustakom.hurrican.service.cc.impl.helper;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyShort;
import static org.mockito.Mockito.*;

import java.util.*;
import org.apache.commons.lang.SystemUtils;
import org.apache.log4j.Logger;
import org.mockito.ArgumentCaptor;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.common.tools.net.AKMailException;
import de.augustakom.hurrican.model.billing.RufnummerBuilder;
import de.augustakom.hurrican.model.cc.AbteilungBuilder;
import de.augustakom.hurrican.model.cc.AuftragBuilder;
import de.augustakom.hurrican.model.cc.Lock;
import de.augustakom.hurrican.model.cc.LockBuilder;
import de.augustakom.hurrican.model.cc.NiederlassungBuilder;
import de.augustakom.hurrican.model.cc.ReferenceBuilder;
import de.augustakom.hurrican.model.cc.SperreInfoBuilder;
import de.augustakom.hurrican.model.cc.VerbindungsBezeichnungBuilder;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.billing.RufnummerService;
import de.augustakom.hurrican.service.cc.PhysikService;
import de.augustakom.hurrican.service.cc.ReferenceService;
import de.augustakom.hurrican.service.cc.SperreVerteilungService;


/**
 *
 */
@Test(groups = BaseTest.UNIT)
public class SendMailHelperTest extends BaseTest {
    private static final Logger LOGGER = Logger.getLogger(SendMailHelperTest.class);

    private SendMailHelper sendMailHelper = null;
    private MailSender mailSender = null;
    private PhysikService physikService = null;
    private RufnummerService rufnummerService = null;
    private ReferenceService referenceService = null;

    private SperreVerteilungService sperreVerteilungService = null;

    /**
     * Initialize the tests
     */
    @BeforeMethod
    public void prepareTest() {
        // Mock the services
        mailSender = mock(MailSender.class);
        physikService = mock(PhysikService.class);
        rufnummerService = mock(RufnummerService.class);
        referenceService = mock(ReferenceService.class);
        sperreVerteilungService = mock(SperreVerteilungService.class);
        sendMailHelper = new SendMailHelper();
        sendMailHelper.setMailSender(mailSender);
        sendMailHelper.setPhysikService(physikService);
        sendMailHelper.setReferenceService(referenceService);
        sendMailHelper.setRufnummerService(rufnummerService);
        sendMailHelper.setSperreVerteilungService(sperreVerteilungService);
    }


    public void testSendMail4Lock() throws StoreException, AKMailException, FindException {
        AuftragBuilder auftragBuilder = new AuftragBuilder().withRandomId();
        Long auftragId = auftragBuilder.get().getId();
        Long auftragNoOrig = new Long(15);
        Long lockId = new Long(123456);
        AbteilungBuilder abteilungBuilder = new AbteilungBuilder()
                .withNiederlassungBuilder(new NiederlassungBuilder());
        when(physikService.findVerbindungsBezeichnungByAuftragIdTx(auftragId))
                .thenReturn(new VerbindungsBezeichnungBuilder().withVbz("TestVBZ").build());
        when(rufnummerService.findByParam(anyShort(), any(Object[].class)))
                .thenReturn(Arrays.asList(new RufnummerBuilder()
                                .withOnKz("999")
                                .withDnBase("67676767")
                                .withDirectDial("5").build(),
                        new RufnummerBuilder()
                                .withOnKz("555")
                                .withDnBase("333333")
                                .withDirectDial(null).build()
                ));
        when(referenceService.findReference(Lock.REF_ID_LOCK_MODE_FULL))
                .thenReturn(new ReferenceBuilder().withStrValue("Vollsperre").build());
        when(sperreVerteilungService.findSperreInfos(Boolean.TRUE, abteilungBuilder.getId()))
                .thenReturn(Arrays.asList(
                        new SperreInfoBuilder()
                                .withAbteilungBuilder(abteilungBuilder)
                                .withEmail("Test1@Test.de").build(),
                        new SperreInfoBuilder()
                                .withAbteilungBuilder(abteilungBuilder)
                                .withEmail("Test2@Test.de").build()
                ));
        Lock lock = new LockBuilder()
                .withCreatedFrom("TestUser")
                .withId(lockId)
                .withAuftragBuilder(auftragBuilder)
                .withAuftragNoOrig(new Long(auftragNoOrig))
                .withLockModeRefId(Lock.REF_ID_LOCK_MODE_FULL)
                .build();
        sendMailHelper.sendMail4Lock(lock, new HashSet<Long>(Arrays.asList(abteilungBuilder.getId())));
        ArgumentCaptor<SimpleMailMessage> mailMessageCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender).send(mailMessageCaptor.capture());
        LOGGER.info(SystemUtils.LINE_SEPARATOR + mailMessageCaptor.getValue().getText());
        Assert.assertTrue(mailMessageCaptor.getValue().getText().contains("999/67676767-5"));
        Assert.assertTrue(mailMessageCaptor.getValue().getText().contains("555/333333"));
        Assert.assertTrue(mailMessageCaptor.getValue().getText().contains("TestVBZ"));
        Assert.assertTrue(mailMessageCaptor.getValue().getText().contains("Vollsperre"));
        Assert.assertTrue(mailMessageCaptor.getValue().getSubject().contains(lockId.toString()));
        Assert.assertTrue(mailMessageCaptor.getValue().getFrom().contains("Hurrican"));
        Assert.assertEquals(mailMessageCaptor.getValue().getTo(),
                new String[] { "Test1@Test.de", "Test2@Test.de" });
        verify(physikService).findVerbindungsBezeichnungByAuftragIdTx(auftragId);
        verify(physikService, times(1)).findVerbindungsBezeichnungByAuftragIdTx(anyLong());
        verify(sperreVerteilungService).findSperreInfos(Boolean.TRUE, abteilungBuilder.getId());
        verify(sperreVerteilungService, times(1)).findSperreInfos(any(Boolean.class), any(Long.class));
        verify(referenceService).findReference(Lock.REF_ID_LOCK_MODE_FULL);
        verify(sperreVerteilungService).findSperreInfos(Boolean.TRUE, abteilungBuilder.getId());
    }
}
