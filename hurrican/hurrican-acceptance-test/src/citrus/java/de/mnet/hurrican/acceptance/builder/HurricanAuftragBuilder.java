/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 24.11.14
 */
package de.mnet.hurrican.acceptance.builder;

import java.util.*;
import com.consol.citrus.exceptions.CitrusRuntimeException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import de.augustakom.hurrican.dao.cc.CCAuftragDAO;
import de.augustakom.hurrican.model.billing.Rufnummer;
import de.augustakom.hurrican.model.builder.hurrican.AbstractHurricanAuftragBuilder;
import de.augustakom.hurrican.model.cc.Ansprechpartner;
import de.augustakom.hurrican.model.cc.AnsprechpartnerBuilder;
import de.augustakom.hurrican.model.cc.Auftrag;
import de.augustakom.hurrican.model.cc.AuftragBuilder;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.AuftragDatenBuilder;
import de.augustakom.hurrican.model.cc.AuftragStatus;
import de.augustakom.hurrican.model.cc.AuftragTechnik;
import de.augustakom.hurrican.model.cc.AuftragTechnik2Endstelle;
import de.augustakom.hurrican.model.cc.AuftragTechnik2EndstelleBuilder;
import de.augustakom.hurrican.model.cc.AuftragTechnikBuilder;
import de.augustakom.hurrican.model.cc.AuftragVoIPDN;
import de.augustakom.hurrican.model.cc.AuftragVoIPDNBuilder;
import de.augustakom.hurrican.model.cc.CCAddress;
import de.augustakom.hurrican.model.cc.CCAddressBuilder;
import de.augustakom.hurrican.model.cc.Carrier;
import de.augustakom.hurrican.model.cc.CarrierKennung;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.cc.EndstelleBuilder;
import de.augustakom.hurrican.model.cc.GeoId;
import de.augustakom.hurrican.model.cc.HVTGruppe;
import de.augustakom.hurrican.model.cc.HVTGruppeBuilder;
import de.augustakom.hurrican.model.cc.HVTStandort;
import de.augustakom.hurrican.model.cc.HVTStandortBuilder;
import de.augustakom.hurrican.model.cc.Niederlassung;
import de.augustakom.hurrican.model.cc.Produkt;
import de.augustakom.hurrican.model.cc.Reference;
import de.augustakom.hurrican.model.cc.VerbindungsBezeichnung;
import de.augustakom.hurrican.model.cc.VerbindungsBezeichnungBuilder;
import de.augustakom.hurrican.model.cc.VoipDnBlock;
import de.augustakom.hurrican.model.cc.VoipDnPlan;
import de.augustakom.hurrican.model.cc.VoipDnPlanBuilder;
import de.augustakom.hurrican.service.billing.RufnummerService;
import de.augustakom.hurrican.service.cc.CCRufnummernService;

/**
 *
 */
@Component
@Scope("prototype")
public class HurricanAuftragBuilder extends AbstractHurricanAuftragBuilder {

    @Autowired
    private CCRufnummernService hurricanRufnummernService;
    @Autowired
    private RufnummerService billingRufnummernService;

    @Autowired
    public HurricanAuftragBuilder(CCAuftragDAO ccAuftragDAO) {
        super(ccAuftragDAO);
    }

    /**
     * @param billingOrderNoOrig Billing-Auftrags Nummer
     * @return
     */
    public AuftragDaten buildAuftragFttxTelefonie(Long billingOrderNoOrig, Long kundeNo, Long dnNoOrig) {
        AuftragBuilder auftragBuilder = new AuftragBuilder()
                .withKundeNo(kundeNo)
                .setPersist(false);
        Auftrag auftrag = ccAuftragDAO.store(auftragBuilder.build());

        AuftragDaten auftragDaten = new AuftragDatenBuilder()
                .withProdId(Produkt.PROD_ID_FTTX_TELEFONIE)
                .withStatusId(AuftragStatus.TECHNISCHE_REALISIERUNG)
                .withAuftragNoOrig(billingOrderNoOrig)
                .setPersist(false)
                .build();
        auftragDaten.setAuftragId(auftrag.getAuftragId());
        ccAuftragDAO.store(auftragDaten);

        AuftragTechnik2Endstelle at2Es = new AuftragTechnik2EndstelleBuilder().setPersist(false).build();
        ccAuftragDAO.store(at2Es);

        VerbindungsBezeichnung vbz = new VerbindungsBezeichnungBuilder()
                .withKindOfUseProduct("P")
                .withKindOfUseType("P")
                .withRandomUniqueCode()
                .setPersist(false).build();
        ccAuftragDAO.store(vbz);

        AuftragTechnik auftragTechnik = new AuftragTechnikBuilder()
                .setPersist(false)
                .build();
        auftragTechnik.setAuftragId(auftrag.getAuftragId());
        auftragTechnik.setNiederlassungId(Niederlassung.ID_MUENCHEN);
        auftragTechnik.setAuftragTechnik2EndstelleId(at2Es.getId());
        auftragTechnik.setVbzId(vbz.getId());
        auftragTechnik.setAuftragTechnik2EndstelleId(at2Es.getId());
        ccAuftragDAO.store(auftragTechnik);

        HVTGruppe hvtGruppe = new HVTGruppeBuilder().setPersist(false).build();
        ccAuftragDAO.store(hvtGruppe);

        HVTStandort hvtStandort = new HVTStandortBuilder()
                .withCarrierId(Carrier.ID_DTAG)
                .withCpsProvisioning(true)
                .withStandortTypRefId(HVTStandort.HVT_STANDORT_TYP_FTTB)
                .withCarrierKennungId(CarrierKennung.ID_MNET_MUENCHEN)
                .setPersist(false).build();
        hvtStandort.setHvtGruppeId(hvtGruppe.getId());
        ccAuftragDAO.store(hvtStandort);

        CCAddress address = new CCAddressBuilder()
                .setPersist(false).build();
        address.setHandy("+49 176 123456");
        address.setTelefon("+49 89 555222");
        ccAuftragDAO.store(address);

        Ansprechpartner ansprechpartner = new AnsprechpartnerBuilder()
                .withType(Ansprechpartner.Typ.ENDSTELLE_B)
                .setPersist(false).build();
        ansprechpartner.setAddress(address);
        ansprechpartner.setAuftragId(auftrag.getId());
        ccAuftragDAO.store(ansprechpartner);

        GeoIdBuilder.GeoIdBuilderResult geoIdBuilderResult = new GeoIdBuilder().build(ccAuftragDAO);
        GeoId geoId = geoIdBuilderResult.geoId;

        Endstelle endstelle = new EndstelleBuilder()
                .withEndstelleTyp(Endstelle.ENDSTELLEN_TYP_B)
                .setPersist(false).build();
        endstelle.setEndstelleGruppeId(at2Es.getId());
        endstelle.setHvtIdStandort(hvtStandort.getId());
        endstelle.setAddressId(address.getId());
        endstelle.setGeoId(geoId.getId());
        ccAuftragDAO.store(endstelle);

        createVoip(dnNoOrig, auftrag.getAuftragId());

        return auftragDaten;
    }

    public void createVoip(Long dnNoOrig, Long auftragId) {
        VoipDnPlan voipDnPlan = new VoipDnPlanBuilder()
                .withGueltigAb(new Date())
                .withVoipDnBlocks(Collections.singletonList(new VoipDnBlock("0", "10", true)))
                .withSipLogin("login")
                .setPersist(false).build();

        Reference reference = ccAuftragDAO.findById(Reference.REF_ID_SIP_DOMAIN_MAXI_M_CALL, Reference.class);
        AuftragVoIPDN auftragVoIPDN = new AuftragVoIPDNBuilder()
                .withDnNoOrig(dnNoOrig)
                .withSipDomain(reference)
                .withRufnummernplaene(Collections.singletonList(voipDnPlan))
                .setPersist(false).build();
        auftragVoIPDN.setAuftragId(auftragId);
        ccAuftragDAO.store(auftragVoIPDN);
        createDefaultLeistungen4Dn(dnNoOrig, auftragId, voipDnPlan.getGueltigAb());
    }

    /**
     * Creates for the assigned {@link Rufnummer#dnNoOrig}  the default {@link de.augustakom.hurrican.model.cc.dn.Leistung4Dn}.
     *
     * @param dnNoOrig  {@link Rufnummer#dnNoOrig}
     * @param auftragId {@link Auftrag#getAuftragId()}
     * @param realDate  planned realDate
     */
    public void createDefaultLeistungen4Dn(Long dnNoOrig, Long auftragId, Date realDate) {
        try {
            final Rufnummer lastRN = billingRufnummernService.findLastRN(dnNoOrig);
            hurricanRufnummernService.attachDefaultLeistungen2DN(lastRN, auftragId, realDate, null);
        }
        catch (Exception e) {
            throw new CitrusRuntimeException("unexpected error during the creation of the default Leistungen for the DN with dnNoOrig=" + dnNoOrig, e);
        }

    }

}
