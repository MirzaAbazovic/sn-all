/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 11.10.2004 15:20:04
 */
package de.augustakom.hurrican.service.cc.impl;

import static org.testng.Assert.*;

import java.lang.reflect.*;
import java.util.*;
import org.apache.log4j.Logger;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.common.tools.lang.NumberTools;
import de.augustakom.common.tools.validation.ValidationException;
import de.augustakom.hurrican.model.cc.AddressTypeEnum;
import de.augustakom.hurrican.model.cc.Auftrag;
import de.augustakom.hurrican.model.cc.AuftragBuilder;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.AuftragDatenBuilder;
import de.augustakom.hurrican.model.cc.EG;
import de.augustakom.hurrican.model.cc.EG2Auftrag;
import de.augustakom.hurrican.model.cc.EG2AuftragBuilder;
import de.augustakom.hurrican.model.cc.EGBuilder;
import de.augustakom.hurrican.model.cc.EGConfig;
import de.augustakom.hurrican.model.cc.EGConfigBuilder;
import de.augustakom.hurrican.model.cc.EGType;
import de.augustakom.hurrican.model.cc.EGTypeBuilder;
import de.augustakom.hurrican.model.cc.EndgeraetAcl;
import de.augustakom.hurrican.model.cc.EndgeraetAclBuilder;
import de.augustakom.hurrican.model.cc.EndgeraetIp;
import de.augustakom.hurrican.model.cc.EndgeraetIp.AddressType;
import de.augustakom.hurrican.model.cc.EndgeraetPort;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.cc.EndstelleBuilder;
import de.augustakom.hurrican.model.cc.IPAddress;
import de.augustakom.hurrican.model.cc.IPAddressBuilder;
import de.augustakom.hurrican.model.cc.PortForwarding;
import de.augustakom.hurrican.model.cc.PortForwarding.TransportProtocolType;
import de.augustakom.hurrican.model.cc.Routing;
import de.augustakom.hurrican.model.cc.Schicht2Protokoll;
import de.augustakom.hurrican.model.cc.Zugang;
import de.augustakom.hurrican.model.cc.view.EG2AuftragView;
import de.augustakom.hurrican.model.internet.IntEndgeraet;
import de.augustakom.hurrican.service.AbstractHurricanBaseServiceTest;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.cc.EndgeraeteService;
import de.augustakom.hurrican.service.cc.EndstellenService;

@Test(groups = { BaseTest.SERVICE })
public class EndgeraeteServiceTest extends AbstractHurricanBaseServiceTest {

    private static final Logger LOGGER = Logger.getLogger(EndgeraeteServiceTest.class);

    public void findEG2AuftragById() throws FindException, StoreException {
        EndgeraeteService endgeraeteService = getCCService(EndgeraeteService.class);
        EG2Auftrag expectedEG2Auftrag = getBuilder(EG2AuftragBuilder.class).build();

        EG2Auftrag eg2Auftrag = endgeraeteService.findEG2AuftragById(expectedEG2Auftrag.getId());

        assertNotNull(eg2Auftrag);
        assertNull(eg2Auftrag.getEndstelleId());
    }

    public void importEg() throws StoreException, FindException {
        EndgeraeteService endgeraeteService = getCCService(EndgeraeteService.class);
        Auftrag auftrag = getBuilder(AuftragBuilder.class).build();

        IntEndgeraet eg = new IntEndgeraet();
        eg.setSerialNo("1234abc");
        eg.setVendor("Cisco");
        eg.setType("CISCO871-K921");
        EGType egType = getBuilder(EGTypeBuilder.class)
                .withHersteller(eg.getVendor())
                .withModell(eg.getType())
                .build();

        endgeraeteService.importEg(auftrag.getAuftragId(), 1234L, eg, getSessionId());

        List<EG2Auftrag> eg2as = endgeraeteService.findEGs4Auftrag(auftrag.getAuftragId());
        assertNotNull(eg2as);
        assertEquals(eg2as.size(), 1);

        EG2Auftrag eg2a = eg2as.get(0);
        EGConfig egConfig = endgeraeteService.findEGConfig(eg2a.getId());
        assertEquals(egConfig.getSerialNumber(), "1234abc");
        assertNotNull(egConfig.getEgType(), "Endgerätetyp darf nicht null sein!");
        assertEquals(egConfig.getEgType().getHersteller(), egType.getHersteller());
        assertEquals(egConfig.getEgType().getModell(), egType.getModell());
    }

    public void testSaveWithEndstelle() throws FindException, StoreException {
        EndgeraeteService endgeraeteService = getCCService(EndgeraeteService.class);
        EndstelleBuilder endstelleBuilder = getBuilder(EndstelleBuilder.class);
        EG2Auftrag expectedEG2Auftrag = getBuilder(EG2AuftragBuilder.class).withEndstelleBuilder(endstelleBuilder)
                .build();

        EG2Auftrag eg2Auftrag = endgeraeteService.findEG2AuftragById(expectedEG2Auftrag.getId());

        EndstellenService endstellenService = getCCService(EndstellenService.class);
        assertNotNull(eg2Auftrag);
        assertNotNull(eg2Auftrag.getEndstelleId());
        Endstelle es = endstellenService.findEndstelle(eg2Auftrag.getEndstelleId());
        assertNotNull(es);
    }

    public void testAddIpToEG2Auftrag() throws FindException, StoreException, ValidationException {
        EndgeraeteService endgeraeteService = getCCService(EndgeraeteService.class);
        EG2Auftrag expectedEG2Auftrag = getBuilder(EG2AuftragBuilder.class).build();
        EG2Auftrag foundEg2Auftrag = endgeraeteService.findEG2AuftragById(expectedEG2Auftrag.getId());
        assertNotNull(foundEg2Auftrag);
        assertEmpty(foundEg2Auftrag.getEndgeraetIps(), "Ips duerfen nicht vorhanden sein");

        IPAddress ipAddress = getBuilder(IPAddressBuilder.class)
                .withAddressType(AddressTypeEnum.IPV4)
                .withAddress("192.168.1.1")
                .build();
        EndgeraetIp endgeraetIp = new EndgeraetIp();
        endgeraetIp.setAddressType(AddressType.LAN.name());
        endgeraetIp.setIpAddressRef(ipAddress);
        foundEg2Auftrag.addEndgeraetIp(endgeraetIp);

        endgeraeteService.saveEG2Auftrag(foundEg2Auftrag, getSessionId());

        foundEg2Auftrag = endgeraeteService.findEG2AuftragById(expectedEG2Auftrag.getId());

        assertNotEmpty(foundEg2Auftrag.getEndgeraetIps(), "Das Endgeraet hat immer noch keine IPs");
        assertEquals(foundEg2Auftrag.getEndgeraetIps().size(), 1);
        assertEquals(foundEg2Auftrag.getEndgeraetIps().iterator().next(), endgeraetIp,
                "Die IP wurde nicht zu dem Endgeraet hinzugefuegt");
    }

    public void testDeleteIpFromEG2Auftrag() throws FindException, StoreException, ValidationException {
        EndgeraeteService endgeraeteService = getCCService(EndgeraeteService.class);

        IPAddress ipAddress = getBuilder(IPAddressBuilder.class)
                .withAddressType(AddressTypeEnum.IPV4)
                .withAddress("192.168.1.1")
                .build();

        EndgeraetIp endgeraetIp = new EndgeraetIp();
        endgeraetIp.setAddressType(AddressType.LAN.name());
        endgeraetIp.setIpAddressRef(ipAddress);
        Set<EndgeraetIp> endgeraetIps = new HashSet<>();
        endgeraetIps.add(endgeraetIp);
        EG2Auftrag expectedEG2Auftrag = getBuilder(EG2AuftragBuilder.class).withEndgeraetIps(endgeraetIps).build();

        EG2Auftrag foundEg2Auftrag = endgeraeteService.findEG2AuftragById(expectedEG2Auftrag.getId());
        assertNotNull(foundEg2Auftrag);
        assertNotEmpty(foundEg2Auftrag.getEndgeraetIps(), "Ips muessen vorhanden sein");

        foundEg2Auftrag.removeEndgeraetIp(endgeraetIp);
        endgeraeteService.saveEG2Auftrag(foundEg2Auftrag, getSessionId());

        foundEg2Auftrag = endgeraeteService.findEG2AuftragById(expectedEG2Auftrag.getId());

        assertEmpty(foundEg2Auftrag.getEndgeraetIps(), "Das Endgeraet hat immer noch IPs");
    }

    public void testDeleteEG2Auftrag() throws Exception {
        EndgeraeteService endgeraeteService = getCCService(EndgeraeteService.class);

        IPAddress ipAddress = getBuilder(IPAddressBuilder.class)
                .withAddressType(AddressTypeEnum.IPV4)
                .withAddress("192.168.1.1")
                .build();

        EndgeraetIp endgeraetIp = new EndgeraetIp();
        endgeraetIp.setAddressType(AddressType.LAN.name());
        endgeraetIp.setIpAddressRef(ipAddress);
        Set<EndgeraetIp> endgeraetIps = new HashSet<>();
        endgeraetIps.add(endgeraetIp);
        EG2Auftrag expectedEG2Auftrag = getBuilder(EG2AuftragBuilder.class).withEndgeraetIps(endgeraetIps).build();

        EG2Auftrag foundEg2Auftrag = endgeraeteService.findEG2AuftragById(expectedEG2Auftrag.getId());
        assertNotNull(foundEg2Auftrag);
        assertNotEmpty(foundEg2Auftrag.getEndgeraetIps(), "Ips muessen vorhanden sein");

        endgeraeteService.deleteEG2Auftrag(foundEg2Auftrag.getId(), 1L);
        flushAndClear();
        assertNull(endgeraeteService.findEG2AuftragById(foundEg2Auftrag.getId()));
    }

    /**
     * Testet die Validierung von Endgeraet-Ips
     */
    @Test(expectedExceptions = ValidationException.class)
    public void testEndgeraetIpValidation() throws FindException, StoreException, ValidationException {
        EndgeraeteService endgeraeteService = getCCService(EndgeraeteService.class);

        IPAddress ipAddress = getBuilder(IPAddressBuilder.class)
                .withAddressType(AddressTypeEnum.IPV4)
                .withAddress("192.168.1.1")
                .build();

        EndgeraetIp endgeraetIp = new EndgeraetIp();
        Set<EndgeraetIp> endgeraetIps = new HashSet<>();
        endgeraetIp.setAddressType(AddressType.LAN);
        endgeraetIp.setIpAddressRef(ipAddress);
        endgeraetIps.add(endgeraetIp);

        EG2Auftrag eg2Auftrag = getBuilder(EG2AuftragBuilder.class).withEndgeraetIps(endgeraetIps).build();
        eg2Auftrag.getEndgeraetIps().iterator().next().setIpAddressRef(null);

        endgeraeteService.saveEG2Auftrag(eg2Auftrag, getSessionId());
    }

    /**
     * Testet das Hinzufuegen einer statischen Route zu einem EG2Auftrag-Objekt.
     */
    public void testAddRoutingToEgAuf() throws FindException {
        EndgeraeteService endgeraeteService = getCCService(EndgeraeteService.class);
        IPAddress ipAddress = getBuilder(IPAddressBuilder.class)
                .withAddressType(AddressTypeEnum.IPV4)
                .withAddress("192.168.0.1")
                .build();
        Routing routing = new Routing();
        Set<Routing> routings = new HashSet<>();
        routing.setBemerkung("Bemerkung 123");
        routing.setDestinationAdressRef(ipAddress);
        routing.setNextHop("192.168.0.2");
        routings.add(routing);
        EG2Auftrag expectedEG2Auftrag = getBuilder(EG2AuftragBuilder.class).withRoutings(routings).build();

        EG2Auftrag eg2Auftrag = endgeraeteService.findEG2AuftragById(expectedEG2Auftrag.getId());

        assertNotNull(eg2Auftrag);
        assertEquals(eg2Auftrag.getRoutings().iterator().next(), routing);
    }

    /**
     * Testet die Validierung der statischen Routen.
     */
    @Test(expectedExceptions = ValidationException.class)
    public void testRoutingValidation() throws FindException, StoreException, ValidationException {
        EndgeraeteService endgeraeteService = getCCService(EndgeraeteService.class);
        IPAddress ipAddress = getBuilder(IPAddressBuilder.class)
                .withAddressType(AddressTypeEnum.IPV4)
                .withAddress("192.168.0.1")
                .build();
        Routing routing = new Routing();
        Set<Routing> routings = new HashSet<>();
        routing.setDestinationAdressRef(ipAddress);
        routing.setNextHop("192.168.0.2");
        routings.add(routing);
        EG2Auftrag expectedEG2Auftrag = getBuilder(EG2AuftragBuilder.class).withRoutings(routings).build();
        expectedEG2Auftrag.getRoutings().iterator().next().setDestinationAdressRef(null);
        endgeraeteService.saveEG2Auftrag(expectedEG2Auftrag, getSessionId());
    }

    public void testFindAllEndgeraetAcls() throws FindException {
        EndgeraeteService endgeraeteService = getCCService(EndgeraeteService.class);
        EndgeraetAcl endgeraetAcl = getBuilder(EndgeraetAclBuilder.class).build();

        List<EndgeraetAcl> endgeraetAcls = endgeraeteService.findAllEndgeraetAcls();

        LOGGER.debug("Number of Acls: " + endgeraetAcls.size());
        assertNotEmpty(endgeraetAcls, "Keine Endgeraet ACL gefunden");
        assertTrue(endgeraetAcls.contains(endgeraetAcl));
    }

    public void testFindEndgeraetAclByName() throws FindException {
        EndgeraeteService endgeraeteService = getCCService(EndgeraeteService.class);
        EndgeraetAcl endgeraetAcl = getBuilder(EndgeraetAclBuilder.class).build();

        EndgeraetAcl foundEndgeraetAcl = endgeraeteService.findEndgeraetAclByName(endgeraetAcl.getName());

        assertEquals(foundEndgeraetAcl, endgeraetAcl, "EndgeraetAcl wurde nicht gefunden");
    }

    public void testAddEndgeraetAclToEGConfig() throws FindException, StoreException, ValidationException {
        EndgeraeteService endgeraeteService = getCCService(EndgeraeteService.class);
        EndgeraetAcl endgeraetAcl = getBuilder(EndgeraetAclBuilder.class).build();
        EGConfig egConfig = getBuilder(EGConfigBuilder.class).build();

        EGConfig foundEgConfig = endgeraeteService.findEGConfig(egConfig.getEg2AuftragId());
        foundEgConfig.addEndgeraetAcl(endgeraetAcl);
        endgeraeteService.saveEGConfig(foundEgConfig, getSessionId());

        endgeraeteService.findEGConfig(egConfig.getEg2AuftragId());
        assertNotEmpty(egConfig.getEndgeraetAcls(), "Keine Acls zu Config zugeordnet");
        assertTrue(egConfig.getEndgeraetAcls().contains(endgeraetAcl));
    }

    public void testAddEndgeraetTwiceAclToEGConfig() throws FindException, StoreException, ValidationException {
        EndgeraeteService endgeraeteService = getCCService(EndgeraeteService.class);
        EndgeraetAcl endgeraetAcl = getBuilder(EndgeraetAclBuilder.class).build();
        EndgeraetAcl endgeraetAclTransient = getBuilder(EndgeraetAclBuilder.class).setPersist(false).build();

        EGConfig egConfig = getBuilder(EGConfigBuilder.class).build();

        EGConfig foundEgConfig = endgeraeteService.findEGConfig(egConfig.getEg2AuftragId());
        foundEgConfig.addEndgeraetAcl(endgeraetAcl);
        foundEgConfig.addEndgeraetAcl(endgeraetAclTransient);
        endgeraeteService.saveEGConfig(foundEgConfig, getSessionId());

        endgeraeteService.findEGConfig(egConfig.getEg2AuftragId());
        assertNotEmpty(egConfig.getEndgeraetAcls(), "Keine Acls zu Config zugeordnet");
        assertTrue(egConfig.getEndgeraetAcls().contains(endgeraetAcl));
        assertEquals(egConfig.getEndgeraetAcls().size(), 1,
                "Adding the same Acl twice to a set creates more than one value");
    }

    public void testRemoveEndgeraetAclFromEGConfig() throws FindException, StoreException, ValidationException {
        EndgeraeteService endgeraeteService = getCCService(EndgeraeteService.class);
        EndgeraetAcl endgeraetAcl = getBuilder(EndgeraetAclBuilder.class).build();
        EGConfig egConfig = getBuilder(EGConfigBuilder.class).withEndgeraetAcl(endgeraetAcl).build();

        EGConfig foundEgConfig = endgeraeteService.findEGConfig(egConfig.getEg2AuftragId());
        assertNotEmpty(foundEgConfig.getEndgeraetAcls(), "Keine Acls zu Config zugeordnet");

        foundEgConfig.removeEndgeraetAcl(endgeraetAcl);
        endgeraeteService.saveEGConfig(foundEgConfig, getSessionId());

        endgeraeteService.findEGConfig(egConfig.getEg2AuftragId());
        assertEmpty(egConfig.getEndgeraetAcls(), "Acl zu Config wurde nicht geloescht");
        EndgeraetAcl foundEndgeraetAcl = endgeraeteService.findEndgeraetAclByName(endgeraetAcl.getName());
        assertEquals(foundEndgeraetAcl, endgeraetAcl, "Acl wurde geloescht!");
    }

    public void testSaveEndgeraetAcl() throws StoreException, FindException {
        EndgeraeteService endgeraeteService = getCCService(EndgeraeteService.class);
        EndgeraetAcl endgeraetAcl = getBuilder(EndgeraetAclBuilder.class).setPersist(false).build();
        endgeraeteService.saveEndgeraetAcl(endgeraetAcl);
        EndgeraetAcl foundAcl = endgeraeteService.findEndgeraetAclByName(endgeraetAcl.getName());
        assertEquals(foundAcl, endgeraetAcl);
    }

    public void testDeleteEndgeraetAcl() throws StoreException, FindException {
        EndgeraeteService endgeraeteService = getCCService(EndgeraeteService.class);
        EndgeraetAcl endgeraetAcl = getBuilder(EndgeraetAclBuilder.class).build();
        endgeraeteService.deleteEndgeraetAcl(endgeraetAcl);
        EndgeraetAcl foundAcl = endgeraeteService.findEndgeraetAclByName(endgeraetAcl.getName());
        assertNull(foundAcl);
    }

    /**
     * Testet das Hinzufuegen von Port-Forwardings.
     */
    public void testAddPortForwardingToEGConfig() throws FindException, StoreException, ValidationException {
        EndgeraeteService endgeraeteService = getCCService(EndgeraeteService.class);
        EGConfig egConfig = getBuilder(EGConfigBuilder.class).build();

        EGConfig foundConfig = endgeraeteService.findEGConfig(egConfig.getEg2AuftragId());
        assertNotNull(foundConfig);

        assertEmpty(foundConfig.getPortForwardings(), "Port Forwardings duerfen nicht vorhanden sein");

        IPAddress ipAddressSource = getBuilder(IPAddressBuilder.class)
                .withAddressType(AddressTypeEnum.IPV4)
                .withAddress("192.168.0.1")
                .build();
        IPAddress ipAddressDest = getBuilder(IPAddressBuilder.class)
                .withAddressType(AddressTypeEnum.IPV4)
                .withAddress("192.168.0.2")
                .build();
        PortForwarding newPortForwarding = new PortForwarding();
        newPortForwarding.setSourceIpAddressRef(ipAddressSource);
        newPortForwarding.setSourcePort(80);
        newPortForwarding.setDestIpAddressRef(ipAddressDest);
        newPortForwarding.setDestPort(81);
        newPortForwarding.setActive(Boolean.TRUE);
        newPortForwarding.setBemerkung("bemerkung");
        newPortForwarding.setTransportProtocol(TransportProtocolType.TCP);

        foundConfig.addPortForwarding(newPortForwarding);

        endgeraeteService.saveEGConfig(foundConfig, getSessionId());

        // reload
        foundConfig = endgeraeteService.findEGConfig(egConfig.getEg2AuftragId());
        assertNotEmpty(foundConfig.getPortForwardings(), "Das hinzugefuegte Port Forwarding ist nicht vorhanden.");
    }

    /**
     * Testet das Loeschen von Port-Forwardings.
     */
    public void testDeletePortForwarding() throws FindException, StoreException, ValidationException {
        EndgeraeteService endgeraeteService = getCCService(EndgeraeteService.class);

        IPAddress ipAddressDest = getBuilder(IPAddressBuilder.class)
                .withAddressType(AddressTypeEnum.IPV4)
                .withAddress("192.168.0.1")
                .build();
        PortForwarding pf = new PortForwarding();
        Set<PortForwarding> pfs = new HashSet<>();
        pf.setDestIpAddressRef(ipAddressDest);
        pf.setDestPort(211);
        pf.setActive(Boolean.TRUE);
        pfs.add(pf);

        EGConfig egConfig = getBuilder(EGConfigBuilder.class).withPortForwardings(pfs).build();
        Long eg2AuftragId = egConfig.getEg2AuftragId();

        egConfig = endgeraeteService.findEGConfig(eg2AuftragId);
        assertNotEmpty(egConfig.getPortForwardings(), "Das hinzugefuegte Port Forwarding ist nicht vorhanden.");

        egConfig.removePortForwarding(egConfig.getPortForwardings().iterator().next());
        endgeraeteService.saveEGConfig(egConfig, getSessionId());

        egConfig = endgeraeteService.findEGConfig(eg2AuftragId);
        assertEmpty(egConfig.getPortForwardings(), "Port Forwardings ist nicht entfernt worden");
    }

    /**
     * Testet die Validierung von Port-Forwardings
     */
    @Test(expectedExceptions = ValidationException.class)
    public void testPortForwardingValidation() throws FindException, StoreException, ValidationException {
        EndgeraeteService endgeraeteService = getCCService(EndgeraeteService.class);

        IPAddress ipAddressDest = getBuilder(IPAddressBuilder.class)
                .withAddressType(AddressTypeEnum.IPV4)
                .withAddress("192.118.0.1")
                .build();
        PortForwarding pf = new PortForwarding();
        Set<PortForwarding> pfs = new HashSet<>();
        pf.setDestPort(211);
        pf.setActive(Boolean.TRUE);
        pf.setDestIpAddressRef(ipAddressDest);
        pfs.add(pf);

        EGConfig egConfig = getBuilder(EGConfigBuilder.class).withPortForwardings(pfs).build();
        egConfig.getPortForwardings().iterator().next().setDestIpAddressRef(null);

        endgeraeteService.saveEGConfig(egConfig, getSessionId());
    }

    /**
     * Testet CreateCopy von EGConfig
     */
    public void testEGConfigCreateCopy() throws StoreException, ValidationException, IllegalAccessException,
            InvocationTargetException, NoSuchMethodException {
        EndgeraeteService endgeraeteService = getCCService(EndgeraeteService.class);
        IPAddress ipAddressDest = getBuilder(IPAddressBuilder.class)
                .withAddressType(AddressTypeEnum.IPV4)
                .withAddress("192.118.0.1")
                .build();
        PortForwarding pf = new PortForwarding();
        Set<PortForwarding> pfs = new HashSet<>();
        pf.setDestPort(211);
        pf.setActive(Boolean.TRUE);
        pf.setDestIpAddressRef(ipAddressDest);
        pfs.add(pf);

        EndgeraetAcl acl = getBuilder(EndgeraetAclBuilder.class).build();

        EGConfig egConfig = getBuilder(EGConfigBuilder.class).withSchicht2Protokoll(Schicht2Protokoll.ATM)
                .withPortForwardings(pfs).withEndgeraetAcl(acl).build();
        EG2Auftrag eg2Auftrag = getBuilder(EG2AuftragBuilder.class).build();

        EGConfig copyOfEgConfig = EGConfig.createCopy(egConfig, eg2Auftrag.getId());
        endgeraeteService.saveEGConfig(egConfig, getSessionId());
        endgeraeteService.saveEGConfig(copyOfEgConfig, getSessionId());

        assertEquals(egConfig.getPortForwardings().size(), 1);
        assertEquals(copyOfEgConfig.getPortForwardings().size(), 1);
        PortForwarding portForw = egConfig.getPortForwardings().iterator().next();
        PortForwarding copyOfPortForw = copyOfEgConfig.getPortForwardings().iterator().next();
        assertNotSame(portForw, copyOfPortForw);
        assertNotSame(portForw.getDestIpAddressRef(), copyOfPortForw.getDestIpAddressRef());
        assertEquals(portForw.getDestIpAddressRef().getAddress(), copyOfPortForw.getDestIpAddressRef().getAddress());
        assertNotNull(portForw.getDestIpAddressRef());

        assertEquals(egConfig.getEndgeraetAcls().size(), 1);
        assertEquals(copyOfEgConfig.getEndgeraetAcls().size(), 1);
        EndgeraetAcl oldAcl = egConfig.getEndgeraetAcls().iterator().next();
        EndgeraetAcl newAcl = egConfig.getEndgeraetAcls().iterator().next();
        assertSame(oldAcl, newAcl);
    }

    /**
     * Testet CreateCopy von EG2Auftrag
     */
    public void testEG2AuftragCreateCopy() throws StoreException, ValidationException, IllegalAccessException,
            InvocationTargetException, NoSuchMethodException {
        EndgeraeteService endgeraeteService = getCCService(EndgeraeteService.class);

        IPAddress ipAddress = getBuilder(IPAddressBuilder.class)
                .withAddressType(AddressTypeEnum.IPV4)
                .withAddress("192.168.0.1")
                .build();
        EndgeraetIp endgeraetIp = new EndgeraetIp();
        endgeraetIp.setAddressType(AddressType.LAN.name());
        endgeraetIp.setIpAddressRef(ipAddress);
        Set<EndgeraetIp> endgeraetIps = new HashSet<>();
        endgeraetIps.add(endgeraetIp);

        Routing routing = new Routing();
        Set<Routing> routings = new HashSet<>();
        routing.setBemerkung("Bemerkung 123");
        routing.setDestinationAdressRef(ipAddress);
        routing.setNextHop("192.168.0.2");
        routings.add(routing);

        EG2Auftrag eg2Auftrag = getBuilder(EG2AuftragBuilder.class).withEndgeraetIps(endgeraetIps)
                .withRoutings(routings).build();
        AuftragDaten auftragDaten = getBuilder(AuftragDatenBuilder.class).build();

        EG2Auftrag copyOfEg2Auftrag = EG2Auftrag.createCopy(eg2Auftrag, auftragDaten.getAuftragId());

        endgeraeteService.saveEG2Auftrag(eg2Auftrag, getSessionId());
        endgeraeteService.saveEG2Auftrag(copyOfEg2Auftrag, getSessionId());

        assertEquals(eg2Auftrag.getEndgeraetIps().size(), 1);
        assertEquals(copyOfEg2Auftrag.getEndgeraetIps().size(), 1);
        EndgeraetIp ip = eg2Auftrag.getEndgeraetIps().iterator().next();
        EndgeraetIp copyOfIp = copyOfEg2Auftrag.getEndgeraetIps().iterator().next();
        assertNotNull(ip.getIpAddressRef());
        assertNotSame(ip, copyOfIp);
        assertNotSame(ip.getIpAddressRef(), copyOfIp.getIpAddressRef());
        assertEquals(ip.getIpAddressRef().getAddress(), copyOfIp.getIpAddressRef().getAddress());

        assertEquals(eg2Auftrag.getRoutings().size(), 1);
        assertEquals(copyOfEg2Auftrag.getRoutings().size(), 1);
        Routing oldRouting = eg2Auftrag.getRoutings().iterator().next();
        Routing newRouting = copyOfEg2Auftrag.getRoutings().iterator().next();
        assertNotNull(oldRouting.getDestinationAdressRef());
        assertNotSame(oldRouting, newRouting);
        assertNotSame(oldRouting.getDestinationAdressRef(), newRouting.getDestinationAdressRef());
        assertEquals(oldRouting.getDestinationAdressRef().getAddress(), newRouting.getDestinationAdressRef()
                .getAddress());
    }

    /**
     * Test fuer die Methode {@link EndgeraeteService#findValidEG(Long, Long)}
     */
    @Test(enabled = false)
    public void testFindValidEGLongInteger() {
        try {
            EndgeraeteService service = getCCService(EndgeraeteService.class);
            List<EG> result = service.findValidEG(50017L, 421L);
            assertNotEmpty(result, "Keine EGs zu Leistung__NO gefunden!");

            LOGGER.debug("EGs:");
            for (EG eg : result) {
                LOGGER.debug(eg.getEgName());
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            fail(e.getMessage());
        }
    }

    /**
     * Test fuer die Methode {@link EndgeraeteService#findDefaultEGs4Order(Long)}
     */
    @Test(enabled = false)
    public void testFindDefaultEGs4Order() {
        try {
            EndgeraeteService service = getCCService(EndgeraeteService.class);
            List<EG> result = service.findDefaultEGs4Order(205049L);
            assertNotEmpty(result, "Keine EGs gefunden, die dem Auftrag zugeordnet werden sollten.");

            LOGGER.debug("EGs to add:");
            for (EG eg : result) {
                LOGGER.debug("  " + eg.getEgName());
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            fail(e.getMessage());
        }
    }

    @Test(enabled = false)
    public void testFindZugaenge4Auftrag() {
        try {
            Long aId = 79619L;
            EndgeraeteService es = getCCService(EndgeraeteService.class);

            List<Zugang> result = es.findZugaenge4Auftrag(aId);
            assertNotEmpty(result, "Keine Zugangsdaten zu Auftrag " + aId + " gefunden!");
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            fail(e.getMessage());
        }
    }

    public void testFindEG2AuftragViews() {
        try {
            EndgeraeteService endgeraeteService = getCCService(EndgeraeteService.class);
            EG2Auftrag expectedEG2Auftrag = getBuilder(EG2AuftragBuilder.class)
                    .withAuftragBuilder(getBuilder(AuftragBuilder.class)).withEgBuilder(getBuilder(EGBuilder.class))
                    .build();

            List<EG2AuftragView> result = endgeraeteService.findEG2AuftragViews(expectedEG2Auftrag.getAuftragId());
            assertNotEmpty(result, "keine Endgeraete zum Auftrag gefunden!");
            assertEquals(result.size(), 1);
            assertEquals(result.get(0).getAuftragId(), expectedEG2Auftrag.getAuftragId());
            assertEquals(result.get(0).getEgId(), expectedEG2Auftrag.getEgId());
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            fail(e.getMessage());
        }
    }

    public void testGetDistinctListOfModelsByManufacturer() {
        try {
            String hersteller = "CISCO@testGetDistinctListOfModelsByManufacturer";
            String modell1 = "CIS2345";
            String modell2 = "CIS3456";
            EndgeraeteService endgeraeteService = getCCService(EndgeraeteService.class);
            EGType egType = getBuilder(EGTypeBuilder.class)
                    .withHersteller(hersteller)
                    .withModell(modell1)
                    .build();
            getBuilder(EGTypeBuilder.class)
                    .withHersteller(hersteller)
                    .withModell(modell2)
                    .build();

            List<String> result = endgeraeteService.getDistinctListOfModelsByManufacturer(egType.getHersteller());
            assertNotEmpty(result, "keine Endgeraete zum Auftrag gefunden!");
            assertEquals(result.size(), 2, "Resultset sollte zwei Strings enthalten!");
            assertFalse(result.get(0).equals(result.get(1)), "Modelle sollten unterschiedlich sein!");
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            fail(e.getMessage());
        }
    }

    public void testGetDistinctListOfEGManucaturer() {
        try {
            EndgeraeteService endgeraeteService = getCCService(EndgeraeteService.class);
            getBuilder(EGTypeBuilder.class).withHersteller("CISCO").withModell("CIS2345").build();

            List<String> result = endgeraeteService.getDistinctListOfEGManufacturer();
            assertNotEmpty(result, "keine Endgeraete zum Auftrag gefunden!");
            if (result.size() < 1) {
                fail("zu wenig Endgeraetetypen");
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            fail(e.getMessage());
        }
    }

    public void testGetDistinctListOfEGModels() {
        try {
            EndgeraeteService endgeraeteService = getCCService(EndgeraeteService.class);
            getBuilder(EGTypeBuilder.class).withHersteller("CISCO").withModell("CIS2345").build();

            List<String> result = endgeraeteService.getDistinctListOfEGModels();
            assertNotEmpty(result, "keine Endgeraete zum Auftrag gefunden!");
            if (result.size() < 1) {
                fail("zu wenig Endgeraetetypen");
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            fail(e.getMessage());
        }
    }

    public void testSaveEGType() throws StoreException {
        EndgeraeteService endgeraeteService = getCCService(EndgeraeteService.class);
        EGType egType = new EGType();
        egType.setHersteller("Hersteller");
        egType.setModell("Modell");
        endgeraeteService.saveEGType(egType, -1L);

        assertTrue(egType.getId() != null, "EGType nicht persistiert!");
    }

    public void testFindAllEGTypes() throws StoreException {
        EndgeraeteService endgeraeteService = getCCService(EndgeraeteService.class);
        EGType egType = new EGType();
        egType.setHersteller("Hersteller");
        egType.setModell("Modell");

        List<EGType> resultFirst = endgeraeteService.findAllEGTypes();
        endgeraeteService.saveEGType(egType, -1L);
        List<EGType> resultSecond = endgeraeteService.findAllEGTypes();

        assertNotNull(resultSecond, "Resultset null!");
        assertTrue(!resultSecond.isEmpty(), "Es sollte mindestens ein EGTyp ermittelbar sein!");
        assertTrue(((resultSecond.size() - ((resultFirst != null) ? resultFirst.size() : 0)) == 1),
                "Die zweite Liste sollte um "
                        + "eins groesser als die erste Liste sein!"
        );
    }

    public void testFindPossibleEGs4EGType() throws FindException {
        EndgeraeteService endgeraeteService = getCCService(EndgeraeteService.class);
        EGType egType = getBuilder(EGTypeBuilder.class).build();

        List<EG> resultFirst = endgeraeteService.findPossibleEGs4EGType(egType.getId());
        getBuilder(EGBuilder.class).build();
        List<EG> resultSecond = endgeraeteService.findPossibleEGs4EGType(egType.getId());

        assertNotNull(resultSecond, "Resultset null!");
        assertTrue(!resultSecond.isEmpty(), "Es sollte mindestens eine EG für den neu angelegten EGTyp existieren!");
        assertTrue(((resultSecond.size() - ((resultFirst != null) ? resultFirst.size() : 0)) == 1),
                "Die zweite Liste sollte um "
                        + "eins groesser als die erste Liste sein!"
        );
    }

    public void testFindEGTypeByHerstellerAndModell() {
        try {
            EndgeraeteService endgeraeteService = getCCService(EndgeraeteService.class);
            EGType egType = getBuilder(EGTypeBuilder.class).withHersteller("CISCO").withModell("CIS2345").build();

            egType = endgeraeteService.findEGTypeByHerstellerAndModell("CISCO", "CIS2345");
            assertNotNull(egType, "Engerätetyp konnte nicht ermittelt werden!");
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            fail(e.getMessage());
        }
    }

    public void testFindDefaultEndgeraetPorts4Count() throws FindException {
        EndgeraeteService endgeraeteService = getCCService(EndgeraeteService.class);

        Map<Integer, EndgeraetPort> result = endgeraeteService.findDefaultEndgeraetPorts4Count(3);
        assertNotNull(result);
        assertTrue(result.size() == 3);
        assertTrue(NumberTools.equal(result.get(1).getNumber(), 1));
        assertTrue(NumberTools.equal(result.get(2).getNumber(), 2));
        assertTrue(NumberTools.equal(result.get(3).getNumber(), 3));
    }

    public void testGetMaxDefaultEndgeraetPorts() throws FindException {
        EndgeraeteService endgeraeteService = getCCService(EndgeraeteService.class);
        Integer result = endgeraeteService.getMaxDefaultEndgeraetPorts();
        assertNotNull(result);
        assertTrue(NumberTools.equal(result, 8)); // Standardmaessig sind bis zu 8 Ports erlaubt
    }

}
