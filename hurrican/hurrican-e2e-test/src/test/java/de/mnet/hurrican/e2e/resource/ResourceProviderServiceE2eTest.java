/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 09.05.2014
 */
package de.mnet.hurrican.e2e.resource;

import static de.augustakom.common.BaseTest.*;
import static org.testng.Assert.*;

import java.io.*;
import java.util.*;
import javax.annotation.*;
import javax.inject.*;
import javax.xml.bind.*;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.hurrican.model.cc.Equipment;
import de.augustakom.hurrican.model.cc.HVTStandort;
import de.augustakom.hurrican.model.cc.hardware.HWBaugruppe;
import de.augustakom.hurrican.model.cc.hardware.HWOlt;
import de.augustakom.hurrican.model.cc.hardware.HWOnt;
import de.augustakom.hurrican.model.cc.hardware.HWRack;
import de.augustakom.hurrican.service.cc.HVTService;
import de.augustakom.hurrican.service.cc.HWService;
import de.augustakom.hurrican.service.cc.RangierungsService;
import de.mnet.hurrican.adm.resourceinventoryservice.v1.Resource;
import de.mnet.hurrican.adm.resourceinventoryservice.v1.ResourceCharacteristic;
import de.mnet.hurrican.adm.resourceinventoryservice.v1.UpdateResource;
import de.mnet.hurrican.adm.resourceinventoryservice.v1.UpdateResourceResponse;
import de.mnet.hurrican.e2e.common.BaseHurricanE2ETest;
import de.mnet.hurrican.e2e.common.StandortDataBuilder;

@Test(groups = E2E)
public class ResourceProviderServiceE2eTest extends BaseHurricanE2ETest {

    @Inject
    protected WebServiceTemplate resourceInventoryWebServiceTemplate;

    @Inject
    protected HVTService hvtService;

    @Inject
    protected HWService hwService;

    @Inject
    protected RangierungsService rangierungsService;

    @Inject
    protected Provider<StandortDataBuilder> standortDataBuilderProvider;

    protected StandortDataBuilder.StandortData standortData;


    @BeforeMethod(groups = BaseTest.E2E)
    protected void initData() throws Exception {
        standortData = standortDataBuilderProvider.get()
                .withStandortTypeRefId(HVTStandort.HVT_STANDORT_TYP_FTTH)
                .withOltChildRackType(HWRack.RACK_TYPE_ONT)
                .getStandortData();
    }

    private Resource createResourceFromXML(String name) throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(UpdateResource.class);
        Unmarshaller unmarshaller = context.createUnmarshaller();
        InputStream inputStream = getClass().getResourceAsStream(name);

        return ((UpdateResource) unmarshaller.unmarshal(inputStream)).getResource().get(0);
    }


    private class CreateOnt {

        private String oltBezeichnung;
        private Resource resource;

        public UpdateResourceResponse createOnt() throws Exception {
            Calendar cal = new GregorianCalendar();
            String randomTestNumber = "-" + Long.toString(cal.getTimeInMillis());

            final UpdateResource request = new UpdateResource();
            resource = createResourceFromXML("/resource/request/ResourceProviderServiceTestOntValid.xml");

            oltBezeichnung = modifyResourceForRepeatableTests(randomTestNumber, resource);

            request.getResource().add(resource);

            return (UpdateResourceResponse) resourceInventoryWebServiceTemplate.marshalSendAndReceive(request);
        }

        public UpdateResourceResponse createOntWithDifferentType() throws Exception {
            addOrModifyCharacteristic(resource, "modellnummer", "HG8242");

            final UpdateResource request = new UpdateResource();
            request.getResource().add(resource);

            return (UpdateResourceResponse) resourceInventoryWebServiceTemplate.marshalSendAndReceive(request);
        }

        public HWOnt findCreatedOnt() throws Exception {
            HWRack oltrack = hwService.findActiveRackByBezeichnung(getOltBezeichnung());
            List<HWOnt> ontList = hwService.findHWOltChildByOlt(oltrack.getId(), HWOnt.class);
            assertNotNull(ontList);

            HWOnt createdOnt = null;
            for (HWOnt ont : ontList) {
                if (ont.getGeraeteBez().equals(getResource().getName())) {
                    createdOnt = ont;
                }
            }
            return createdOnt;
        }

        private String modifyResourceForRepeatableTests(String randomTestNumber, Resource resource) {
            String oltBezeichnung = null;
            resource.setName(resource.getName() + randomTestNumber);
            for (ResourceCharacteristic resChar : resource.getCharacteristic()) {
                if ("olt".equals(resChar.getName().toLowerCase())) {
                    resChar.getValue().remove(0);
                    resChar.getValue().add(standortData.hwOlt.getGeraeteBez());
                    oltBezeichnung = resChar.getValue().get(0);
                }
                if ("standort".equals(resChar.getName().toLowerCase())) {
                    resChar.getValue().remove(0);
                    resChar.getValue().add(standortData.oltChildGruppe.getOrtsteil());
                }
            }
            return oltBezeichnung;
        }

        public String getOltBezeichnung() {
            return oltBezeichnung;
        }

        public Resource getResource() {
            return resource;
        }

    }

    private class CreatePort {
        private Resource resource;

        public UpdateResourceResponse createPort(HWOnt hwOnt) throws Exception {
            final UpdateResource request = new UpdateResource();
            resource = createResourceFromXML("/resource/request/ResourceProviderServiceTestOntPort.xml");
            resource.setId(hwOnt.getGeraeteBez() + "_" + resource.getName());
            resource.getParentResource().setId(hwOnt.getGeraeteBez());

            request.getResource().add(resource);
            return (UpdateResourceResponse) resourceInventoryWebServiceTemplate.marshalSendAndReceive(request);
        }

        public Resource getResource() {
            return resource;
        }
    }

    @Test(enabled = false)
    public void updateResource() throws Exception {
        createOntPort(createOnt());
    }

    @Test(enabled = false)
    public void updateExistingOnt() throws Exception {
        final CreateOnt createOnt = new CreateOnt();
        createOnt.createOnt();
        assertNull(createOnt.findCreatedOnt().getSerialNo());

        final String serialNo = "123-456";
        Resource resource = createOnt.getResource();
        addOrModifyCharacteristic(resource, "seriennummer", serialNo);

        final UpdateResource request = new UpdateResource();
        request.getResource().add(resource);
        resourceInventoryWebServiceTemplate.marshalSendAndReceive(request);

        assertEquals(createOnt.findCreatedOnt().getSerialNo(), serialNo);
    }

    @Test(enabled = false)
    public void deleteExistingOntAndCreateDifferentType() throws Exception {
        final CreateOnt createOnt = new CreateOnt();
        createOnt.createOnt();
        assertNull(createOnt.findCreatedOnt().getSerialNo());

        Resource resource = createOnt.getResource();
        addOrModifyCharacteristic(resource, "installationsstatus", "GELOESCHT");

        final UpdateResource request = new UpdateResource();
        request.getResource().add(resource);
        resourceInventoryWebServiceTemplate.marshalSendAndReceive(request);

        assertTrue(DateTools.isDateBeforeOrEqual(createOnt.findCreatedOnt().getGueltigBis(), new Date()));

        addOrModifyCharacteristic(resource, "installationsstatus", "IST");
        createOnt.createOntWithDifferentType();
        assertTrue(DateTools.isDateEqual(createOnt.findCreatedOnt().getGueltigBis(),
                DateTools.getHurricanEndDate()));
    }

    private HWOnt createOnt() throws Exception {
        CreateOnt createOnt = new CreateOnt();
        createOnt.createOnt();
        HWOnt createdOnt = createOnt.findCreatedOnt();

        verifyResult(standortData.oltChildStandort, standortData.hwOlt, createOnt.getResource(), createdOnt);
        return createdOnt;
    }

    private void verifyResult(HVTStandort hvtStandort, HWOlt olt, Resource resource, HWOnt hwOnt) {
        assertEquals(hwOnt.getGeraeteBez(), resource.getName());
        for (ResourceCharacteristic resChar : resource.getCharacteristic()) {
            switch (resChar.getName().toLowerCase()) {
                case "hersteller":
                    assertNotNull(extractString(resChar.getValue(), 0));
                    break;
                case "seriennummer":
                    assertEquals(hwOnt.getSerialNo(), extractString(resChar.getValue(), 0));
                    break;
                case "modellnummer":
                    assertEquals(hwOnt.getOntType(), extractString(resChar.getValue(), 0));
                    break;
                case "olt":
                    assertNotNull(extractString(resChar.getValue(), 0));
                    assertEquals(hwOnt.getOltRackId(), olt.getId());
                    break;
                case "oltrack":
                    assertEquals(hwOnt.getOltFrame(), extractString(resChar.getValue(), 0));
                    break;
                case "oltsubrack":
                    assertEquals(hwOnt.getOltSubrack(), extractString(resChar.getValue(), 0));
                    break;
                case "oltslot":
                    assertEquals(hwOnt.getOltSlot(), extractString(resChar.getValue(), 0));
                    break;
                case "oltport":
                    assertEquals(hwOnt.getOltGPONPort(), extractString(resChar.getValue(), 0));
                    break;
                case "oltgponid":
                    assertEquals(hwOnt.getOltGPONId(), extractString(resChar.getValue(), 0));
                    break;
                case "standort":
                    assertEquals(hwOnt.getHvtIdStandort(), hvtStandort.getId());
                    break;
                case "raumbezeichung":
                    assertNotNull(hwOnt.getHvtRaumId());
                    break;
            }
        }
    }

    private void createOntPort(HWOnt hwOnt) throws Exception {
        CreatePort createPort = new CreatePort();
        createPort.createPort(hwOnt);

        List<HWBaugruppe> hwBaugruppen = hwService.findBaugruppen4Rack(hwOnt.getId());
        assertNotNull(hwBaugruppen);
        assertEquals(hwBaugruppen.size(), 1);
        HWBaugruppe ethBaugruppe = hwBaugruppen.get(0);
        assertEquals(ethBaugruppe.getModNumber(), "0-1");

        List<Equipment> equipments = rangierungsService.findEquipments4HWBaugruppe(ethBaugruppe.getId());
        assertNotNull(equipments);
        assertEquals(equipments.size(), 1);
        Equipment portCreated = equipments.get(0);
        assertEquals(createPort.getResource().getName(), portCreated.getHwEQN());
        assertEquals(extractString(extractValues(createPort.getResource(), "schnittstelle"), 0),
                portCreated.getHwSchnittstelle());
    }

    private void addOrModifyCharacteristic(final Resource resource, final String name, final String value) {
        boolean modified = false;
        for (ResourceCharacteristic resChar : resource.getCharacteristic()) {
            if (name.toLowerCase().equals(resChar.getName().toLowerCase())) {
                resChar.getValue().remove(0);
                resChar.getValue().add(value);
                modified = true;
            }
        }
        if (!modified) {
            final List<ResourceCharacteristic> list = resource.getCharacteristic();
            ResourceCharacteristic newChar = new ResourceCharacteristic();
            newChar.setName(name);
            newChar.getValue().add(value);
            list.add(newChar);
        }
    }

    @Nullable
    String extractString(List<String> value, int index) {
        if (value == null || index >= value.size()) {
            return null;
        }
        return value.get(index);
    }

    @Nullable
    Long extractLong(List<String> value, int index) {
        if (value == null || index >= value.size()) {
            return null;
        }
        return Long.valueOf(value.get(index));
    }

    @Nullable
    List<String> extractValues(Resource resource, String name) {
        if (resource == null || resource.getCharacteristic() == null || name == null) {
            return null;
        }
        for (ResourceCharacteristic resourceCharacteristic : resource.getCharacteristic()) {
            if (name.equalsIgnoreCase(resourceCharacteristic.getName())) {
                return resourceCharacteristic.getValue();
            }
        }
        return null;
    }

}
