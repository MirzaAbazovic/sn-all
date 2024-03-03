/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 05.04.2012 16:44:52
 */
package de.augustakom.hurrican.service.cc.fttx.impl;

import static de.augustakom.common.BaseTest.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.testng.Assert.*;

import java.time.*;
import java.util.*;
import javax.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.testng.annotations.Test;

import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.hurrican.dao.cc.fttx.EkpFrameContractDAO;
import de.augustakom.hurrican.model.cc.Auftrag;
import de.augustakom.hurrican.model.cc.AuftragAktion;
import de.augustakom.hurrican.model.cc.AuftragAktion.AktionType;
import de.augustakom.hurrican.model.cc.AuftragAktionBuilder;
import de.augustakom.hurrican.model.cc.AuftragBuilder;
import de.augustakom.hurrican.model.cc.fttx.A10Nsp;
import de.augustakom.hurrican.model.cc.fttx.A10NspBuilder;
import de.augustakom.hurrican.model.cc.fttx.A10NspPort;
import de.augustakom.hurrican.model.cc.fttx.A10NspPortBuilder;
import de.augustakom.hurrican.model.cc.fttx.Auftrag2EkpFrameContract;
import de.augustakom.hurrican.model.cc.fttx.Auftrag2EkpFrameContractBuilder;
import de.augustakom.hurrican.model.cc.fttx.CVlan;
import de.augustakom.hurrican.model.cc.fttx.CVlanBuilder;
import de.augustakom.hurrican.model.cc.fttx.EkpFrameContract;
import de.augustakom.hurrican.model.cc.fttx.EkpFrameContractBuilder;
import de.augustakom.hurrican.service.AbstractHurricanBaseServiceTest;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.cc.fttx.EkpFrameContractService;

@Test(groups = { SERVICE })
public class EkpFrameContractServiceTest extends AbstractHurricanBaseServiceTest {

    @Resource(name = "de.augustakom.hurrican.service.cc.fttx.EkpFrameContractService")
    private EkpFrameContractService sut;
    @Autowired
    private EkpFrameContractDAO ekpFrameContractDAO;

    @Test
    public void findEkpFrameContract() {
        CVlan cvlan = getBuilder(CVlanBuilder.class).setPersist(false).build();
        EkpFrameContract ekpFrameContractExpected = getBuilder(EkpFrameContractBuilder.class).addCVlan(cvlan).build();
        EkpFrameContract ekpFrameContractFound = sut.findEkpFrameContract(ekpFrameContractExpected.getEkpId(), ekpFrameContractExpected.getFrameContractId());
        assertEquals(ekpFrameContractFound.getId(), ekpFrameContractExpected.getId());
        assertNotEmpty(ekpFrameContractFound.getCvlans());
        assertEquals(ekpFrameContractFound.getCvlans().size(), 1);
        assertEquals(ekpFrameContractFound.getCvlans().get(0).getId(), cvlan.getId());
    }


    @Test
    public void assignEkpFrameContract2Auftrag() {
        Auftrag auftrag = getBuilder(AuftragBuilder.class).build();
        EkpFrameContract ekpFrameContract = getBuilder(EkpFrameContractBuilder.class).build();

        LocalDate validAt = LocalDate.now().plusDays(2);
        Auftrag2EkpFrameContract result = sut.assignEkpFrameContract2Auftrag(ekpFrameContract, auftrag, validAt, null);
        assertNotNull(result);
        assertThat(result.getAuftragId(), equalTo(auftrag.getAuftragId()));
        assertThat(result.getEkpFrameContract().getId(), equalTo(ekpFrameContract.getId()));
        assertThat(result.getAssignedFrom(), equalTo(validAt));
    }


    @Test
    public void assignEkpFrameContract2AuftragExpectThatAssignmentAlreadyExists() {
        AuftragBuilder auftragBuilder = getBuilder(AuftragBuilder.class);
        EkpFrameContractBuilder ekpBuilder = getBuilder(EkpFrameContractBuilder.class);

        Auftrag2EkpFrameContract a2ekp = getBuilder(Auftrag2EkpFrameContractBuilder.class)
                .withAuftragBuilder(auftragBuilder)
                .withEkpFrameContractBuilder(ekpBuilder)
                .build();

        Auftrag2EkpFrameContract result = sut.assignEkpFrameContract2Auftrag(ekpBuilder.get(), auftragBuilder.get(), LocalDate.now().plusDays(2), null);
        assertThat(result.getId(), equalTo(a2ekp.getId()));
        assertThat(result.getAssignedFrom(), equalTo(a2ekp.getAssignedFrom()));
        assertThat(Date.from(result.getAssignedTo().atStartOfDay(ZoneId.systemDefault()).toInstant()), equalTo(DateTools.getHurricanEndDate()));
    }


    @Test
    public void assignEkpFrameContract2AuftragWithNewEkpFrameContract() {
        AuftragBuilder auftragBuilder = getBuilder(AuftragBuilder.class);
        EkpFrameContractBuilder ekpBuilder = getBuilder(EkpFrameContractBuilder.class);
        getBuilder(Auftrag2EkpFrameContractBuilder.class)
                .withAuftragBuilder(auftragBuilder)
                .withEkpFrameContractBuilder(ekpBuilder)
                .build();
        EkpFrameContract anotherEkp = getBuilder(EkpFrameContractBuilder.class).build();

        LocalDate validAt = LocalDate.now().plusDays(2);
        AuftragAktion aktion = getBuilder(AuftragAktionBuilder.class)
                .withAction(AktionType.MODIFY_PORT)
                .withDesiredExecutionDate(validAt)
                .withAuftragBuilder(auftragBuilder)
                .build();

        Auftrag2EkpFrameContract result = sut.assignEkpFrameContract2Auftrag(anotherEkp, auftragBuilder.get(), validAt, aktion);
        assertNotNull(result);
        assertThat(result.getEkpFrameContract().getId(), equalTo(anotherEkp.getId()));
        assertThat(result.getAssignedFrom(), equalTo(validAt));
        assertThat(Date.from(result.getAssignedTo().atStartOfDay(ZoneId.systemDefault()).toInstant()), equalTo(DateTools.getHurricanEndDate()));

        Auftrag2EkpFrameContract currentAssignment = sut.findAuftrag2EkpFrameContract(auftragBuilder.get().getId(), LocalDate.now());
        assertNotNull(currentAssignment);
        assertNotNull(currentAssignment.getAuftragAktionsIdRemove());
        Auftrag2EkpFrameContract futureAssignment = sut.findAuftrag2EkpFrameContract(auftragBuilder.get().getId(), validAt);
        assertNotNull(futureAssignment);
        assertNotNull(futureAssignment.getAuftragAktionsIdAdd());
        assertNull(futureAssignment.getAuftragAktionsIdRemove());
    }

    @Test
    public void findAuftrag2EkpFrameContract() {
        AuftragBuilder auftragBuilder = getBuilder(AuftragBuilder.class);
        EkpFrameContractBuilder ekpBuilder = getBuilder(EkpFrameContractBuilder.class);
        Auftrag2EkpFrameContract a2ekp = getBuilder(Auftrag2EkpFrameContractBuilder.class)
                .withAuftragBuilder(auftragBuilder)
                .withEkpFrameContractBuilder(ekpBuilder)
                .build();

        Auftrag2EkpFrameContract result = sut.findAuftrag2EkpFrameContract(auftragBuilder.get().getAuftragId(), LocalDate.now().plusDays(5));
        assertNotNull(result);
        assertThat(result.getId(), equalTo(a2ekp.getId()));
    }

    @Test(expectedExceptions = IncorrectResultSizeDataAccessException.class)
    public void findAuftrag2EkpFrameContractYieldsExceptionBecauseOfMultipleValid() {
        AuftragBuilder auftragBuilder = getBuilder(AuftragBuilder.class);
        for (int i = 0; i < 2; i++) {
            EkpFrameContractBuilder ekpBuilder = getBuilder(EkpFrameContractBuilder.class);
            getBuilder(Auftrag2EkpFrameContractBuilder.class)
                    .withAuftragBuilder(auftragBuilder)
                    .withEkpFrameContractBuilder(ekpBuilder)
                    .build();
        }

        sut.findAuftrag2EkpFrameContract(auftragBuilder.get().getAuftragId(), LocalDate.now().plusDays(5));
    }

    @Test
    public void createA10NspPort() throws StoreException {
        A10Nsp a10Nsp = getBuilder(A10NspBuilder.class).build();
        A10NspPort result = sut.createA10NspPort(a10Nsp);
        assertNotNull(result);
        assertThat(result.getVbz().getVbz(), startsWith("FE"));
    }

    @Test
    public void hasAuftrag2EkpFrameContract() {
        AuftragBuilder auftragBuilder = getBuilder(AuftragBuilder.class);
        EkpFrameContractBuilder ekpBuilder = getBuilder(EkpFrameContractBuilder.class);
        getBuilder(Auftrag2EkpFrameContractBuilder.class)
                .withAuftragBuilder(auftragBuilder)
                .withEkpFrameContractBuilder(ekpBuilder)
                .build();
        assertThat(sut.hasAuftrag2EkpFrameContract(ekpBuilder.get()), equalTo(Boolean.TRUE));
    }

    @Test
    public void hasAuftrag2EkpFrameContract4A10NspPort() {
        AuftragBuilder auftragBuilder = getBuilder(AuftragBuilder.class);
        A10NspPortBuilder a10NspPortBuilder = getBuilder(A10NspPortBuilder.class);
        EkpFrameContractBuilder ekpBuilder = getBuilder(EkpFrameContractBuilder.class).addA10NspPort(
                a10NspPortBuilder.get(), Boolean.TRUE);
        getBuilder(Auftrag2EkpFrameContractBuilder.class)
                .withAuftragBuilder(auftragBuilder)
                .withEkpFrameContractBuilder(ekpBuilder)
                .build();
        assertThat(sut.hasAuftrag2EkpFrameContract(a10NspPortBuilder.get()), equalTo(Boolean.TRUE));
    }

    @Test
    public void hasAuftrag2EkpFrameContract4A10Nsp() {
        AuftragBuilder auftragBuilder = getBuilder(AuftragBuilder.class);
        A10NspBuilder a10NspBuilder = getBuilder(A10NspBuilder.class);
        A10NspPortBuilder a10NspPortBuilder = getBuilder(A10NspPortBuilder.class).withA10NspBuilder(a10NspBuilder);
        EkpFrameContractBuilder ekpBuilder = getBuilder(EkpFrameContractBuilder.class).addA10NspPort(
                a10NspPortBuilder.get(), Boolean.TRUE);
        getBuilder(Auftrag2EkpFrameContractBuilder.class)
                .withAuftragBuilder(auftragBuilder)
                .withEkpFrameContractBuilder(ekpBuilder)
                .build();
        assertThat(sut.hasAuftrag2EkpFrameContract(a10NspBuilder.get()), equalTo(Boolean.TRUE));
    }

    public void findA10NspPorts() throws FindException {
        A10NspBuilder a10NspBuilder = getBuilder(A10NspBuilder.class);
        getBuilder(A10NspPortBuilder.class).withA10NspBuilder(a10NspBuilder).build();
        List<A10NspPort> a10NspPorts = sut.findA10NspPorts(a10NspBuilder.get());
        assertThat(a10NspPorts, hasSize(1));
        assertThat(a10NspPorts.get(0).getA10Nsp(), equalTo(a10NspBuilder.get()));
    }

}


