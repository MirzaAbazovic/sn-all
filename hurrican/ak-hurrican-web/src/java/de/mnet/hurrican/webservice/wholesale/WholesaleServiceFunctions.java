/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 01.03.2012 10:47:02
 */
package de.mnet.hurrican.webservice.wholesale;

import static com.google.common.base.Predicates.*;
import static com.google.common.collect.Iterables.*;

import java.util.*;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableSet;
import org.apache.commons.collections.CollectionUtils;

import de.augustakom.hurrican.model.cc.cps.serviceorder.CPSFTTBData;
import de.augustakom.hurrican.model.wholesale.OrderParameters;
import de.augustakom.hurrican.model.wholesale.WholesaleCancelModifyPortRequest;
import de.augustakom.hurrican.model.wholesale.WholesaleCancelModifyPortResponse;
import de.augustakom.hurrican.model.wholesale.WholesaleEkpFrameContract;
import de.augustakom.hurrican.model.wholesale.WholesaleModifyPortRequest;
import de.augustakom.hurrican.model.wholesale.WholesaleModifyPortReservationDateRequest;
import de.augustakom.hurrican.model.wholesale.WholesaleModifyPortReservationDateResponse;
import de.augustakom.hurrican.model.wholesale.WholesaleModifyPortResponse;
import de.augustakom.hurrican.model.wholesale.WholesalePbit;
import de.augustakom.hurrican.model.wholesale.WholesaleProduct;
import de.augustakom.hurrican.model.wholesale.WholesaleProductAttribute;
import de.augustakom.hurrican.model.wholesale.WholesaleProductGroup;
import de.augustakom.hurrican.model.wholesale.WholesaleProductName;
import de.augustakom.hurrican.model.wholesale.WholesaleReleasePortRequest;
import de.augustakom.hurrican.model.wholesale.WholesaleReservePortRequest;
import de.augustakom.hurrican.model.wholesale.WholesaleReservePortResponse;
import de.augustakom.hurrican.model.wholesale.WholesaleVlan;
import de.mnet.hurrican.wholesale.workflow.BuchtLeisteStift;
import de.mnet.hurrican.wholesale.workflow.CancelModifyPortRequest;
import de.mnet.hurrican.wholesale.workflow.CancelModifyPortResponse;
import de.mnet.hurrican.wholesale.workflow.Ekp;
import de.mnet.hurrican.wholesale.workflow.GetOrderParametersResponse;
import de.mnet.hurrican.wholesale.workflow.ModifyPortRequest;
import de.mnet.hurrican.wholesale.workflow.ModifyPortReservationDateRequest;
import de.mnet.hurrican.wholesale.workflow.ModifyPortReservationDateResponse;
import de.mnet.hurrican.wholesale.workflow.ModifyPortResponse;
import de.mnet.hurrican.wholesale.workflow.Pbit;
import de.mnet.hurrican.wholesale.workflow.Product;
import de.mnet.hurrican.wholesale.workflow.ReleasePortRequest;
import de.mnet.hurrican.wholesale.workflow.ReservePortRequest;
import de.mnet.hurrican.wholesale.workflow.ReservePortResponse;
import de.mnet.hurrican.wholesale.workflow.ServiceAttribute;
import de.mnet.hurrican.wholesale.workflow.TechLocation;
import de.mnet.hurrican.wholesale.workflow.TechType;
import de.mnet.hurrican.wholesale.workflow.VLAN;

/**
 * Funktionen um Typen des Webservice (JAXB) zu Typen des Services(POJO) zu konvertieren und umgekehrt. JAXB
 * Pfichtfelder haben keinen not-null check.
 */
public class WholesaleServiceFunctions {
    public final static Function<ReservePortRequest, WholesaleReservePortRequest> toWholesaleReservePortRequest = new Function<ReservePortRequest, WholesaleReservePortRequest>() {
        @Override
        public WholesaleReservePortRequest apply(ReservePortRequest in) {
            WholesaleReservePortRequest out = new WholesaleReservePortRequest();
            out.setDesiredExecutionDate(in.getDesiredExecutionDate());
            out.setExtOrderId(in.getExtOrderId());
            if (in.getReseller() != null) {
                out.setEkpFrameContract(toWholesaleEkpFrameContract.apply(in.getReseller()));
            }
            else {
                out.setEkpFrameContract(toWholesaleEkpFrameContract.apply(in.getEkp()));
            }
            out.setGeoId(in.getGeoId());
            out.setProduct(toWholesaleProduct.apply(in.getProduct()));
            return out;
        }
    };

    public final static Function<ReleasePortRequest, WholesaleReleasePortRequest> toWholesaleReleasePortRequest = in -> {
        WholesaleReleasePortRequest out = new WholesaleReleasePortRequest();
        out.setLineId(in.getLineId());
        return out;
    };

    private final static Function<Ekp, WholesaleEkpFrameContract> toWholesaleEkpFrameContract = new Function<Ekp, WholesaleEkpFrameContract>() {
        @Override
        public WholesaleEkpFrameContract apply(Ekp input) {
            WholesaleEkpFrameContract out = new WholesaleEkpFrameContract();
            out.setEkpId(input.getId());
            out.setEkpFrameContractId(input.getFrameContractId());
            return out;
        }
    };

    public final static Function<ModifyPortRequest, WholesaleModifyPortRequest> toWholesaleModifyPortRequest = new Function<ModifyPortRequest, WholesaleModifyPortRequest>() {
        @Override
        public WholesaleModifyPortRequest apply(ModifyPortRequest in) {
            WholesaleModifyPortRequest out = new WholesaleModifyPortRequest();
            out.setLineId(in.getLineId());
            if (in.getReseller() != null) {
                setEkpData(in.getReseller(), out);
            }
            else {
                setEkpData(in.getEkp(), out);
            }
            out.setProduct(toWholesaleProduct.apply(in.getProduct()));
            out.setDesiredExecutionDate(in.getDesiredExecutionDate());
            out.setChangeOfPortAllowed(in.isChangeOfPortAllowed());
            return out;
        }

        private void setEkpData(Ekp in, WholesaleModifyPortRequest out) {
            out.setEkpId(in.getId());
            out.setEkpContractId(in.getFrameContractId());
        }
    };

    public final static Function<Product, WholesaleProduct> toWholesaleProduct = new Function<Product, WholesaleProduct>() {
        @Override
        public WholesaleProduct apply(Product in) {
            WholesaleProduct out = new WholesaleProduct();
            WholesaleProductGroup group = WholesaleProductGroup.valueOf(in.getProductGroup().name());
            out.setName(WholesaleProductName.valueOf(in.getProductName().name()));
            if (group != out.getGroup()) {
                throw new ProductGroupDoesNotMatchException(group, out.getGroup());
            }
            if ((in.getProductAttributes() != null) && !in.getProductAttributes().getServiceAttribute().isEmpty()) {
                ImmutableSet.Builder<WholesaleProductAttribute> attributes = ImmutableSet.builder();
                attributes.addAll(filter(
                        transform(in.getProductAttributes().getServiceAttribute(), toWholesaleAttribute),
                        notNull()));
                out.setAttributes(attributes.build());
            }
            return out;
        }
    };

    public final static Function<ServiceAttribute, WholesaleProductAttribute> toWholesaleAttribute = new Function<ServiceAttribute, WholesaleProductAttribute>() {
        @Override
        public WholesaleProductAttribute apply(ServiceAttribute in) {
            try {
                WholesaleProductAttribute attribute = WholesaleProductAttribute.valueOf(in.name());
                return attribute;
            }
            catch (IllegalArgumentException e) {
                return null;
            }
        }
    };

    public final static Function<WholesaleReservePortResponse, ReservePortResponse> toReservePortResponse = new Function<WholesaleReservePortResponse, ReservePortResponse>() {
        @Override
        public ReservePortResponse apply(WholesaleReservePortResponse in) {
            ReservePortResponse out = new ReservePortResponse();
            out.setLineId(in.getLineId());
            out.setExecutionDate(in.getExecutionDate());
            return out;
        }
    };

    public final static Function<WholesaleModifyPortResponse, ModifyPortResponse> toModifyPortResponse = new Function<WholesaleModifyPortResponse, ModifyPortResponse>() {
        @Override
        public ModifyPortResponse apply(WholesaleModifyPortResponse in) {
            ModifyPortResponse out = new ModifyPortResponse();
            out.setLineId(in.getLineId());
            out.setExecutionDate(in.getExecutionDate());
            out.setPortChanged(in.isPortChanged());
            return out;
        }
    };

    public final static Function<ModifyPortReservationDateRequest, WholesaleModifyPortReservationDateRequest> toWholesaleModifyPortReservationDateRequest = new Function<ModifyPortReservationDateRequest, WholesaleModifyPortReservationDateRequest>() {
        @Override
        public WholesaleModifyPortReservationDateRequest apply(ModifyPortReservationDateRequest in) {
            WholesaleModifyPortReservationDateRequest request = new WholesaleModifyPortReservationDateRequest();
            request.setLineId(in.getLineId());
            request.setDesiredExecutionDate(in.getDesiredExecutionDate());
            return request;
        }
    };

    public final static Function<WholesaleModifyPortReservationDateResponse, ModifyPortReservationDateResponse> toModifyPortReservationDateResponse = new Function<WholesaleModifyPortReservationDateResponse, ModifyPortReservationDateResponse>() {
        @Override
        public ModifyPortReservationDateResponse apply(WholesaleModifyPortReservationDateResponse in) {
            ModifyPortReservationDateResponse response = new ModifyPortReservationDateResponse();
            response.setExecutionDate(in.getExecutionDate());
            return response;
        }
    };


    public final static Function<CancelModifyPortRequest, WholesaleCancelModifyPortRequest> toWholesaleCancelModifyPortRequest = new Function<CancelModifyPortRequest, WholesaleCancelModifyPortRequest>() {
        @Override
        public WholesaleCancelModifyPortRequest apply(CancelModifyPortRequest in) {
            WholesaleCancelModifyPortRequest request = new WholesaleCancelModifyPortRequest();
            request.setLineId(in.getLineId());
            return request;
        }
    };

    public final static Function<WholesaleCancelModifyPortResponse, CancelModifyPortResponse> toCancelModifyPortResponse = new Function<WholesaleCancelModifyPortResponse, CancelModifyPortResponse>() {
        @Override
        public CancelModifyPortResponse apply(WholesaleCancelModifyPortResponse in) {
            CancelModifyPortResponse response = new CancelModifyPortResponse();
            response.setPreviousLineId(in.getPreviousLineId());
            return response;
        }
    };

    public final static Function<OrderParameters, GetOrderParametersResponse> toGetOrderParametersResponse = new Function<OrderParameters, GetOrderParametersResponse>() {
        @Override
        public GetOrderParametersResponse apply(OrderParameters in) {
            GetOrderParametersResponse out = new GetOrderParametersResponse();
            CPSFTTBData fttbData = in.getFttbData();
            if (fttbData != null) {
                out.setManufacturer(fttbData.getManufacturer());
                out.setGponPort(fttbData.getGponPort());
                out.setPortId(fttbData.getPortId());
                out.setMduType(fttbData.getMduTyp());
                out.setMduName(fttbData.getMduGeraeteBezeichnung());
                out.setMduSerialNumber(fttbData.getSerialNo());
                out.setMduLocation(fttbData.getMduStandort());
                out.setOltName(fttbData.getOltGeraeteBezeichnung());
                out.setBgPort(fttbData.getBaugruppenPort());
                out.setPortType(fttbData.getPortTyp());
            }

            // Hvt-Standort
            if (in.getTechLocationHvtGruppe() != null) {
                TechLocation techLocation = new TechLocation();
                techLocation.setName(in.getTechLocationHvtGruppe().getOrtsteil());
                techLocation.setStreet(in.getTechLocationHvtGruppe().getStrasse());
                techLocation.setHousenum(in.getTechLocationHvtGruppe().getHausNr());
                techLocation.setZipcode(in.getTechLocationHvtGruppe().getPlz());
                techLocation.setCity(in.getTechLocationHvtGruppe().getOrt());
                out.setTechLocation(techLocation);
                out.setOnkz(in.getTechLocationHvtGruppe().getOnkz());
            }

            if (fttbData != null) {
                out.setUpStream(Integer.parseInt(fttbData.getUpstream()));
                out.setDownStream(Integer.parseInt(fttbData.getDownstream()));

                // Bucht - Leiste - Stift
                BuchtLeisteStift buchtLeisteStift = new BuchtLeisteStift();
                buchtLeisteStift.setBucht(fttbData.getBucht());
                buchtLeisteStift.setLeiste(fttbData.getLeiste());
                buchtLeisteStift.setStift(fttbData.getStift());
                out.setBuchtLeisteStift(buchtLeisteStift);
            }
            out.setAsb(in.getAsb());
            out.setTechType(toTechType.apply(in.getTechType()));
            out.setTargetMargin(in.getTargetMargin());
            out.setVdslProfile(in.getVdslProfile());
            // Die A10NSP kommt aus dem technischen Rahmenvertrag
            out.setA10Nsp(in.getA10nsp());
            out.setA10NspPort(in.getA10nspPort());

            // Pbits
            out.getPbit().addAll(toPbits.apply(in.getPbit()));
            // VLANs
            out.getVlans().addAll(toVLANs.apply(in.getVlans()));

            return out;
        }
    };

    public final static Function<OrderParameters.WholesaleTechType, TechType> toTechType = new Function<OrderParameters.WholesaleTechType, TechType>() {
        @Override
        public TechType apply(OrderParameters.WholesaleTechType in) {
            return (in != null) ? TechType.valueOf(in.name()) : null;
        }
    };

    public final static Function<List<WholesalePbit>, List<Pbit>> toPbits = new Function<List<WholesalePbit>, List<Pbit>>() {
        @Override
        public List<Pbit> apply(List<WholesalePbit> in) {
            List<Pbit> out = new ArrayList<Pbit>();
            if (CollectionUtils.isNotEmpty(in)) {
                for (WholesalePbit wholesalePbit : in) {
                    Pbit pbit = new Pbit();
                    pbit.setLimit(wholesalePbit.getLimit());
                    pbit.setService(wholesalePbit.getService());
                    out.add(pbit);
                }
            }
            return out;
        }
    };

    public final static Function<List<WholesaleVlan>, List<VLAN>> toVLANs = new Function<List<WholesaleVlan>, List<VLAN>>() {
        @Override
        public List<VLAN> apply(List<WholesaleVlan> in) {
            List<VLAN> out = new ArrayList<VLAN>();
            if (CollectionUtils.isNotEmpty(in)) {
                for (WholesaleVlan wholesaleVlan : in) {
                    VLAN vlan = new VLAN();
                    vlan.setSvlanBackbone(wholesaleVlan.getSvlanBackbone());
                    vlan.setService(wholesaleVlan.getService());
                    vlan.setCvlan(wholesaleVlan.getCvlan());
                    vlan.setSvlan(wholesaleVlan.getSvlan());
                    vlan.setType(wholesaleVlan.getType());
                    out.add(vlan);
                }
            }
            return out;
        }
    };
}


