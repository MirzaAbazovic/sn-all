/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 08.08.13
 */
package de.mnet.wbci.marshal.v1.request;

import static org.mockito.Mockito.*;

import java.util.*;
import org.mockito.Mock;

import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.CarrierIdentifikatorType;
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.EKPType;
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.ObjectFactory;
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.PersonOderFirmaType;
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.ProjektIDType;
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.StandortType;
import de.mnet.wbci.marshal.v1.entities.CarrierIdentifikatorMarshaller;
import de.mnet.wbci.marshal.v1.entities.EKPGeschaeftsfallMarshaller;
import de.mnet.wbci.marshal.v1.entities.PersonOderFirmaMarshaller;
import de.mnet.wbci.marshal.v1.entities.ProjektMarshaller;
import de.mnet.wbci.marshal.v1.entities.StandortMarshaller;
import de.mnet.wbci.marshal.v1.entities.WeitereAnschlussInhaberMarshaller;
import de.mnet.wbci.model.WbciGeschaeftsfall;
import de.mnet.wbci.model.WbciGeschaeftsfallKue;

/**
 *
 */
public abstract class AbstactAnfrageMarshallerMock {

    protected static ObjectFactory V1_OBJECT_FACTORY = new ObjectFactory();

    protected CarrierIdentifikatorType aspectedAbsender;
    protected CarrierIdentifikatorType aspectedEmpfaenger;
    protected EKPType aspectedEkp;
    protected List<PersonOderFirmaType> expectedPersonOderFirmaList;
    protected PersonOderFirmaType expectedPersonOderFirma;
    protected ProjektIDType expectedProjekt;
    protected StandortType aspectedStandort;
    @Mock
    private EKPGeschaeftsfallMarshaller ekpMarshallerMock;
    @Mock
    private CarrierIdentifikatorMarshaller carrierIdentifikatorMarshallerMock;
    @Mock
    private WeitereAnschlussInhaberMarshaller weitereAnschlussInhaberMarshallerMock;
    @Mock
    private PersonOderFirmaMarshaller personOderFirmaMarshallerMock;
    @Mock
    private ProjektMarshaller projektMarshallerMock;
    @Mock
    protected StandortMarshaller standortMarshallerMock;

    protected <GF extends WbciGeschaeftsfall> void initMockHandling(GF wbciGeschaeftsfall) {

        aspectedAbsender = V1_OBJECT_FACTORY.createCarrierIdentifikatorType();
        when(carrierIdentifikatorMarshallerMock.apply(wbciGeschaeftsfall.getAbsender())).thenReturn(
                aspectedAbsender);

        aspectedEmpfaenger = V1_OBJECT_FACTORY.createCarrierIdentifikatorType();
        when(carrierIdentifikatorMarshallerMock.apply(wbciGeschaeftsfall.getAbgebenderEKP())).thenReturn(
                aspectedEmpfaenger);

        aspectedEkp = V1_OBJECT_FACTORY.createEKPType();
        when(ekpMarshallerMock.apply(wbciGeschaeftsfall)).thenReturn(aspectedEkp);

    }

    protected <GF extends WbciGeschaeftsfall> void initMockHandlingVorabstimmung(GF wbciGeschaeftsfall) {
        initMockHandling(wbciGeschaeftsfall);
        expectedPersonOderFirmaList = new ArrayList<>();
        when(weitereAnschlussInhaberMarshallerMock.apply(wbciGeschaeftsfall.getWeitereAnschlussinhaber())).thenReturn(
                expectedPersonOderFirmaList);

        expectedPersonOderFirma = V1_OBJECT_FACTORY.createPersonOderFirmaType();
        when(personOderFirmaMarshallerMock.apply(wbciGeschaeftsfall.getEndkunde())).thenReturn(expectedPersonOderFirma);

        expectedProjekt = V1_OBJECT_FACTORY.createProjektIDType();
        when(projektMarshallerMock.apply(wbciGeschaeftsfall.getProjekt())).thenReturn(expectedProjekt);
    }

    protected <GFKUE extends WbciGeschaeftsfallKue> void initMockHandlingAbstractKuedingungGeschaeftsfall(
            GFKUE wbciGeschaeftsfall) {
        initMockHandlingVorabstimmung(wbciGeschaeftsfall);

        aspectedStandort = V1_OBJECT_FACTORY.createStandortType();
        when(standortMarshallerMock.apply(wbciGeschaeftsfall.getStandort())).thenReturn(aspectedStandort);
    }

}
