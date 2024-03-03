/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 14.05.2014
 */
package de.augustakom.hurrican.model.cc.cps.serviceorder.accessdevice;

import com.google.common.collect.Sets;
import de.augustakom.common.BaseTest;
import de.augustakom.hurrican.dao.cc.DSLAMProfileDAO;
import de.augustakom.hurrican.model.cc.*;
import de.augustakom.hurrican.model.cc.cps.serviceorder.CPSServiceOrderData;
import de.augustakom.hurrican.model.cc.fttx.EqVlanBuilder;
import de.augustakom.hurrican.service.AbstractHurricanBaseServiceTest;
import de.augustakom.hurrican.service.cc.CPSGetDataService;
import de.augustakom.hurrican.service.cc.impl.command.cps.AbstractCPSCommand;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.oxm.xstream.XStreamMarshaller;
import org.testng.annotations.Test;

import javax.inject.Inject;
import java.util.Date;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@Test(groups = {BaseTest.SERVICE})
public class CpsAccessDeviceTest extends AbstractHurricanBaseServiceTest {


    @Inject
    CPSGetDataService cut;

    @Autowired
    DSLAMProfileDAO dslamProfileDAO;

    /**
     * Testet die Erzeugung der ACCESS_DEVICE-Struktur mit Ftth-Daten und DSLAM-Profil
     *
     * @throws Exception
     */
    @SuppressFBWarnings("NP_NONNULL_PARAM_VIOLATION")
    @Test
    public void testAccesDeviceFtth() throws Exception {
        final boolean isDSLAM_Profile = true;
        final AuftragBuilder auftragBuilder = prepareAuftragBuilder4Test(isDSLAM_Profile);
        CpsAccessDevice result = cut.getFtthData(auftragBuilder.get().getAuftragId(), new Date(), true, "portId");
        CPSServiceOrderData soData = new CPSServiceOrderData();
        soData.setAccessDevice(result);
        XStreamMarshaller xmlMarshaller = new XStreamMarshaller();
        xmlMarshaller.setAnnotatedClasses(CPSServiceOrderData.class);
        String xml = AbstractCPSCommand.transformSOData2XML(soData, xmlMarshaller);
        assertThat(xml, equalTo(getExpectedXML(isDSLAM_Profile)));
    }

    /**
     * Testet die Erzeugung der ACCESS_DEVICE-Struktur mit Fttb-Daten und DSLAM-Profil
     *
     * @throws Exception
     */
    public void testAccesDeviceFttb() throws Exception {
        final boolean isDSLAM_Profile = false;
        final AuftragBuilder auftragBuilder = prepareAuftragBuilder4Test(isDSLAM_Profile);
        final Date zeitstempel = new Date();
        prepareProfileAuftragBuilder(auftragBuilder.get().getAuftragId(), zeitstempel);
        CpsAccessDevice result = cut.getFttbData(auftragBuilder.get().getAuftragId(), "portId", zeitstempel);
        CPSServiceOrderData soData = new CPSServiceOrderData();
        soData.setAccessDevice(result);
        XStreamMarshaller xmlMarshaller = new XStreamMarshaller();
        xmlMarshaller.setAnnotatedClasses(CPSServiceOrderData.class);
        String xml = AbstractCPSCommand.transformSOData2XML(soData, xmlMarshaller);
        assertThat(xml, equalTo(getExpectedXML(isDSLAM_Profile)));
    }

    private AuftragBuilder prepareAuftragBuilder4Test(boolean isDSLAMProfile) {
        final AuftragBuilder auftragBuilder = getBuilder(AuftragBuilder.class);
        final AuftragDatenBuilder auftragDatenBuilder = getBuilder(AuftragDatenBuilder.class)
                .withAuftragBuilder(auftragBuilder)
                .withProdId(513L);
        final TechLeistungBuilder techLeistungBuilder = getBuilder(TechLeistungBuilder.class)
                .withTyp(TechLeistung.TYP_SIPTRUNK_QOS_PROFILE)
                .withLongValue(66L);
        final Auftrag2TechLeistungBuilder auftrag2TechLeistungBuilder = getBuilder(Auftrag2TechLeistungBuilder.class)
                .withTechleistungBuilder(techLeistungBuilder)
                .withAuftragBuilder(auftragBuilder);
        final HVTGruppeBuilder ontGruppeBuilder = getBuilder(HVTGruppeBuilder.class)
                .withOrtsteil("Ortsteil");
        final HVTStandortBuilder ontStandortBuilder = getBuilder(HVTStandortBuilder.class)
                .withStandortTypRefId(isDSLAMProfile ? HVTStandort.HVT_STANDORT_TYP_FTTH : HVTStandort.HVT_STANDORT_TYP_FTTB)
                .withHvtGruppeBuilder(ontGruppeBuilder);
        final AuftragTechnikBuilder auftragTechnikBuilder = getBuilder(AuftragTechnikBuilder.class)
                .withAuftragBuilder(auftragBuilder);
        final EndstelleBuilder endstelleBuilder = getBuilder(EndstelleBuilder.class)
                .withEndstelle(Endstelle.ENDSTELLEN_TYP_B)
                .withAuftragTechnikBuilder(auftragTechnikBuilder)
                .withHvtStandortBuilder(ontStandortBuilder);
        final HVTTechnikBuilder hvtTechnikBuilder = getBuilder(HVTTechnikBuilder.class)
                .withCpsName("ALCATEL_LUCENT")
                .withHersteller("Alcatel");
        final HWOltBuilder oltBuilder = getBuilder(HWOltBuilder.class)
                .withHwProducerBuilder(hvtTechnikBuilder)
                .withGeraeteBez("OLT-400003");
        final HWOntBuilder ontBuilder = getBuilder(HWOntBuilder.class)
                .withHWRackOltBuilder(oltBuilder)
                .withHvtStandortBuilder(ontStandortBuilder)
                .withSerialNo("A-1-B2-C3-0815")
                .withGeraeteBez("ONT-404453");

        HVTTechnikBuilder technikBuilder1 = getBuilder(HVTTechnikBuilder.class).withId(HVTTechnik.SIEMENS);
        HWBaugruppenTypBuilder abBuilder = getBuilder(HWBaugruppenTypBuilder.class)
                .setAdslValues()
                .withHvtTechnikBuilder(technikBuilder1)
                .withProfileAssignable(!isDSLAMProfile);
        final HWBaugruppeBuilder hwBaugruppeBuilder = getBuilder(HWBaugruppeBuilder.class)
                .withRackBuilder(ontBuilder)
                .withBaugruppenTypBuilder(abBuilder);
        final EquipmentBuilder ontEquipmentBuilder = getBuilder(EquipmentBuilder.class)
                .withBaugruppeBuilder(hwBaugruppeBuilder)
                .withHvtStandortBuilder(ontStandortBuilder);
        final RangierungBuilder rangierungBuilder = getBuilder(RangierungBuilder.class)
                .withEndstelleBuilder(endstelleBuilder)
                .withEqInBuilder(ontEquipmentBuilder);
        final EqVlanBuilder eqVlanBuilder = getBuilder(EqVlanBuilder.class)
                .withEquipmentBuilder(ontEquipmentBuilder);

        auftragDatenBuilder.build();
        auftrag2TechLeistungBuilder.build();
        rangierungBuilder.build();
        eqVlanBuilder.build();
        return auftragBuilder;
    }

    private void prepareProfileAuftragBuilder(Long auftragId, Date gueltigDatum) {
        final Set<ProfileAuftragValue> profileAuftragValueSet = createTestAuftragProfileSet();
        final DSLAMProfileChangeReason changeReason =
                dslamProfileDAO.findById(DSLAMProfileChangeReason.CHANGE_REASON_ID_INIT, DSLAMProfileChangeReason.class);
        getBuilder(ProfileAuftragBuilder.class)
                .withRandomId()
                .withRandomEquipmentId()
                .withAuftragId(auftragId)
                .withBemerkung("TESTPROFILE")
                .withProfileAuftragValues(profileAuftragValueSet)
                .withDSLAMProfileChangeReason(changeReason)
                .withGueltigDaten(new Date(gueltigDatum.getTime()-1000), new Date(gueltigDatum.getTime()+1000))
                .withOtherDefaultValues().build();
    }

    private Set<ProfileAuftragValue> createTestAuftragProfileSet() {
        Set<ProfileAuftragValue> profileAuftragValueSet = Sets.newHashSet();
        ProfileAuftragValue profileAuftragValue = new ProfileAuftragValue("Rate Up", "30");
        profileAuftragValueSet.add(profileAuftragValue);
        profileAuftragValue = new ProfileAuftragValue("Rate Down", "30");
        profileAuftragValueSet.add(profileAuftragValue);
        profileAuftragValue = new ProfileAuftragValue("UPBO", "5");
        profileAuftragValueSet.add(profileAuftragValue);
        profileAuftragValue = new ProfileAuftragValue("DPBO", "20");
        profileAuftragValueSet.add(profileAuftragValue);
        profileAuftragValue = new ProfileAuftragValue("RFI", "5");
        profileAuftragValueSet.add(profileAuftragValue);
        profileAuftragValue = new ProfileAuftragValue("noise margin", "10");
        profileAuftragValueSet.add(profileAuftragValue);
        profileAuftragValue = new ProfileAuftragValue("INP Delay", "5");
        profileAuftragValueSet.add(profileAuftragValue);
        profileAuftragValue = new ProfileAuftragValue("Virtual Noise", "5");
        profileAuftragValueSet.add(profileAuftragValue);
        profileAuftragValue = new ProfileAuftragValue("G.fast Line Spectrum", "5");
        profileAuftragValueSet.add(profileAuftragValue);
        profileAuftragValue = new ProfileAuftragValue("INM", "5");
        profileAuftragValueSet.add(profileAuftragValue);
        profileAuftragValue = new ProfileAuftragValue("SOS", "5");
        profileAuftragValueSet.add(profileAuftragValue);
        profileAuftragValue = new ProfileAuftragValue("Traffic Table Upstream", "5");
        profileAuftragValueSet.add(profileAuftragValue);
        profileAuftragValue = new ProfileAuftragValue("Traffic Table Downstream", "10");
        profileAuftragValueSet.add(profileAuftragValue);

        return profileAuftragValueSet;
    }
    private String getExpectedXML(boolean isDSLAM_Profile) {
        return "<SERVICE_ORDER>\n"
                + "  <ACCESS_DEVICE>\n"
                + "    <ITEM>\n"
                + "      <DEACTIVATE>0</DEACTIVATE>\n"
                + "      <NETWORK_DEVICE>\n"
                + "        <TYPE>OLT</TYPE>\n"
                + "        <MANUFACTURER>ALCATEL_LUCENT</MANUFACTURER>\n"
                + "        <NAME>OLT-400003</NAME>\n"
                + "        <PORT>00-01-02-03-04</PORT>\n"
                + "        <PORT_TYPE>GPON</PORT_TYPE>\n"
                + "      </NETWORK_DEVICE>\n"
                + "      <ENDPOINT_DEVICE>\n"
                + "        <TYPE>ONT</TYPE>\n"
                + "        <HARDWARE_MODEL>O-123-T</HARDWARE_MODEL>\n"
                + "        <NAME>ONT-404453</NAME>\n"
                + "        <PORT>1-2-3-4</PORT>\n"
                + "        <PORT_TYPE>ADSL</PORT_TYPE>\n"
                + "        <TECH_ID>Ortsteil</TECH_ID>\n"
                + "        <SERIAL_NO>A-1-B2-C3-0815</SERIAL_NO>\n"
                + "        <LOCATION_TYPE>" + (isDSLAM_Profile ? "FTTH" : "FTTB") + "</LOCATION_TYPE>\n"
                + (isDSLAM_Profile ? getUpDownStreamSnipplet() : "")
                + "        <PORT_NAME>portId</PORT_NAME>\n"
                + (isDSLAM_Profile ? "" : getAuftragProfilSnipplet())
                + "      </ENDPOINT_DEVICE>\n"
                + "      <LAYER2_CONFIG>\n"
                + (isDSLAM_Profile ? getPBitSnipplet() : "")
                + "        <VLAN>\n"
                + "          <SERVICE>HSI</SERVICE>\n"
                + "          <TYPE>UNICAST</TYPE>\n"
                + "          <CVLAN>40</CVLAN>\n"
                + "          <SVLAN>102</SVLAN>\n"
                + "          <SVLAN_BACKBONE>101</SVLAN_BACKBONE>\n"
                + "        </VLAN>\n"
                + "      </LAYER2_CONFIG>\n"
                + "    </ITEM>\n"
                + "  </ACCESS_DEVICE>\n"
                + "</SERVICE_ORDER>";
    }

    private String getPBitSnipplet() {
        return "        <PBIT>\n"
                + "          <SERVICE>VOIP</SERVICE>\n"
                + "          <LIMIT>16500</LIMIT>\n"
                + "        </PBIT>\n";

    }
    private String getUpDownStreamSnipplet() {
        return  "        <DOWNSTREAM>25000</DOWNSTREAM>\n"
                + "        <UPSTREAM>2500</UPSTREAM>\n";
    }

    private String getAuftragProfilSnipplet() {
        return "        <DataRateUp>30</DataRateUp>\n"
                + "        <DataRateDown>30</DataRateDown>\n"
                + "        <PowerBackOffUp>5</PowerBackOffUp>\n"
                + "        <PowerBackOffDown>20</PowerBackOffDown>\n"
                + "        <RadioFrequencyInterference>5</RadioFrequencyInterference>\n"
                + "        <NoiseMargin>10</NoiseMargin>\n"
                + "        <ImpulseNoiseProtectionDelay>5</ImpulseNoiseProtectionDelay>\n"
                + "        <VirtualNoise>5</VirtualNoise>\n"
                + "        <Spectrum>5</Spectrum>\n"
                + "        <ImpulseNoiseMonitoring>5</ImpulseNoiseMonitoring>\n"
                + "        <Sos>5</Sos>\n"
                + "        <TrafficTableUp>5</TrafficTableUp>\n"
                + "        <TrafficTableDown>10</TrafficTableDown>\n";
    }
}
