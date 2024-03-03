/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 10.09.14
 */
package de.mnet.hurrican.acceptance.builder;

import java.time.*;
import java.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.common.tools.lang.Pair;
import de.augustakom.hurrican.dao.cc.CCAuftragDAO;
import de.augustakom.hurrican.model.billing.factory.GeneratedTaifunData;
import de.augustakom.hurrican.model.billing.factory.TaifunDataFactory;
import de.augustakom.hurrican.model.cc.Abteilung;
import de.augustakom.hurrican.model.cc.Auftrag;
import de.augustakom.hurrican.model.cc.Auftrag2TechLeistung;
import de.augustakom.hurrican.model.cc.Auftrag2TechLeistungBuilder;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.BAVerlaufAnlass;
import de.augustakom.hurrican.model.cc.EG;
import de.augustakom.hurrican.model.cc.EG2Auftrag;
import de.augustakom.hurrican.model.cc.EG2AuftragBuilder;
import de.augustakom.hurrican.model.cc.EGBuilder;
import de.augustakom.hurrican.model.cc.EGConfig;
import de.augustakom.hurrican.model.cc.EGConfigBuilder;
import de.augustakom.hurrican.model.cc.IPAddress;
import de.augustakom.hurrican.model.cc.IPAddressBuilder;
import de.augustakom.hurrican.model.cc.IPSecSite2Site;
import de.augustakom.hurrican.model.cc.IPSecSite2SiteBuilder;
import de.augustakom.hurrican.model.cc.Produkt;
import de.augustakom.hurrican.model.cc.TechLeistung;
import de.augustakom.hurrican.model.cc.Verlauf;
import de.augustakom.hurrican.model.cc.VerlaufAbteilung;
import de.augustakom.hurrican.model.cc.VerlaufAbteilungBuilder;
import de.augustakom.hurrican.model.cc.VerlaufBuilder;
import de.augustakom.hurrican.model.cc.hardware.HWOltChild;
import de.mnet.hurrican.acceptance.AbstractHurricanTestBuilder;
import de.mnet.hurrican.ffm.citrus.VariableNames;

/**
 *
 */
@Component
@Scope("prototype")
public class VerlaufTestBuilder {

    @Autowired
    private TaifunDataFactory taifunDataFactory;
    @Autowired
    private CCAuftragDAO ccAuftragDAO;
    @Autowired
    private HurricanAuftragBuilder hurricanAuftragBuilder;
    private AbstractHurricanTestBuilder testBuilder;
    private Date verlAbtRealisierungsdatum;
    private Date baRealisierungsdatum;
    private Long baVerlaufAnlass;
    private Long prodId;
    private Long standortTypRefId;
    private boolean addVpn = false;
    private boolean ffmAbteilung = true;
    private boolean interneArbeit = true;
    private boolean addHousing = false;
    private int countOfDialNumbers = 1;
    private GeneratedTaifunData generatedTaifunData;
    private HWOltChild hwOltChild;
    private boolean addVoip = false;

    public VerlaufTestBuilder configure(final AbstractHurricanTestBuilder testBuilder) {
        this.testBuilder = testBuilder;
        return this;
    }

    public CreatedData buildBauauftrag() {
        assert prodId != null;
        assert standortTypRefId != null;
        if (generatedTaifunData == null) {
            generatedTaifunData = taifunDataFactory.surfAndFonWithDns(countOfDialNumbers).persist();
        }
        testBuilder.exportTaifunDataToVariables(generatedTaifunData);

        Pair<Auftrag, AuftragDaten> auftrag = hurricanAuftragBuilder
                .withHWRack(hwOltChild)
                .buildHurricanAuftrag(prodId, generatedTaifunData.getBillingAuftrag().getKundeNo(),
                        generatedTaifunData.getBillingAuftrag().getAuftragNoOrig(),
                        standortTypRefId,
                        addVpn,
                        addHousing,
                        interneArbeit);

        VerlaufBuilder bauauftragBuilder = new VerlaufBuilder()
                .withAnlass(baVerlaufAnlass != null ? baVerlaufAnlass : BAVerlaufAnlass.NEUSCHALTUNG)
                .withRealisierungstermin(baRealisierungsdatum != null ? baRealisierungsdatum : Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()))
                .withAkt(true)
                .setPersist(false);
        Verlauf bauauftrag = bauauftragBuilder.get();
        bauauftrag.setAuftragId(auftrag.getFirst().getAuftragId());
        ccAuftragDAO.store(bauauftrag);

        testBuilder.variable(VariableNames.REALISIERUNGSTERMIN,
                DateTools.formatDate(bauauftrag.getRealisierungstermin(), DateTools.PATTERN_YEAR_MONTH_DAY));

        if (ffmAbteilung) {
            VerlaufAbteilung bauauftragAbt = new VerlaufAbteilungBuilder()
                    .withRandomId()
                    .withVerlaufBuilder(bauauftragBuilder)
                    .withAbteilungId(Abteilung.FFM)
                    .withRealisierungsdatum(verlAbtRealisierungsdatum)
                    .build();
            ccAuftragDAO.store(bauauftragAbt);
        }

        Long techLeistungIdDownstream = 12L;// = 16.000 Downstream
        Long techLeistungIdUpstream = 13L;// = 256k Upstream
        if (prodId.equals(Produkt.PROD_ID_FTTX_DSL_FON)) {
            techLeistungIdDownstream = 24L; // 50 Mbit Down
            techLeistungIdUpstream = 27L; // 5 Mbit Up
        }
        Auftrag2TechLeistung downstream = new Auftrag2TechLeistungBuilder()
                .withTechLeistungId(techLeistungIdDownstream)
                .withAktivVon(bauauftrag.getRealisierungstermin())
                .withVerlaufIdReal(bauauftrag.getId())
                .setPersist(false).build();
        downstream.setAuftragId(auftrag.getFirst().getAuftragId());
        ccAuftragDAO.store(downstream);

        Auftrag2TechLeistung upstream = new Auftrag2TechLeistungBuilder()
                .withTechLeistungId(techLeistungIdUpstream)
                .withAktivVon(bauauftrag.getRealisierungstermin())
                .withVerlaufIdReal(bauauftrag.getId())
                .setPersist(false).build();
        upstream.setAuftragId(auftrag.getFirst().getAuftragId());
        ccAuftragDAO.store(upstream);

        if (addVoip) {
            Auftrag2TechLeistung voip = new Auftrag2TechLeistungBuilder()
                    .withTechLeistungId(TechLeistung.ID_VOIP_MGA)
                    .withAktivVon(bauauftrag.getRealisierungstermin())
                    .withVerlaufIdReal(bauauftrag.getId())
                    .setPersist(false).build();
            voip.setAuftragId(auftrag.getFirst().getAuftragId());
            ccAuftragDAO.store(voip);
        }

        buildIP(auftrag.getSecond(), ccAuftragDAO);
        buildEG(auftrag.getSecond(), ccAuftragDAO);
        buildIPSecSite2Site(auftrag.getSecond(), ccAuftragDAO);

        testBuilder.exportHurricanDataToVariables(auftrag.getSecond());
        return new CreatedData(bauauftrag, auftrag.getFirst(), auftrag.getSecond());
    }

    private void buildIP(AuftragDaten auftragDaten, CCAuftragDAO ccAuftragDAO) {
        IPAddress ip = new IPAddressBuilder()
                .withBillingOrderNumber(auftragDaten.getAuftragNoOrig())
                .withNetId(Long.MAX_VALUE)
                .setPersist(false).build();
        ccAuftragDAO.store(ip);
    }


    private void buildEG(AuftragDaten auftragDaten, CCAuftragDAO ccAuftragDAO) {
        EG eg = new EGBuilder()
                .withEgName("Hurrican-CPE")
                .setPersist(false).build();
        ccAuftragDAO.store(eg);

        EG2Auftrag eg2Auftrag = new EG2AuftragBuilder()
                .setPersist(false).build();
        eg2Auftrag.setEgId(eg.getId());
        eg2Auftrag.setAuftragId(auftragDaten.getAuftragId());
        ccAuftragDAO.store(eg2Auftrag);

        EGConfig egConfig = new EGConfigBuilder()
                .withSerialNumber("123-456-789")
                .setPersist(false).build();
        egConfig.setEg2AuftragId(eg2Auftrag.getId());
        ccAuftragDAO.store(egConfig);
    }


    private void buildIPSecSite2Site(AuftragDaten auftragDaten, CCAuftragDAO ccAuftragDAO) {
        IPSecSite2Site ipSecSite2Site = new IPSecSite2SiteBuilder()
                .setPersist(false).build();
        ipSecSite2Site.setAuftragId(auftragDaten.getAuftragId());
        ccAuftragDAO.store(ipSecSite2Site);
    }

    public VerlaufTestBuilder withVerlaufAbteilungRealisierungsdatum(Date verlAbtRealisierungsdatum) {
        this.verlAbtRealisierungsdatum = verlAbtRealisierungsdatum;
        return this;
    }

    public VerlaufTestBuilder withBauauftragRealisierungsdatum(Date baRealisierungsdatum) {
        this.baRealisierungsdatum = baRealisierungsdatum ;
        return this;
    }

    public VerlaufTestBuilder withBaVerlaufAnlass(Long baVerlaufAnlass) {
        this.baVerlaufAnlass = baVerlaufAnlass;
        return this;
    }

    public VerlaufTestBuilder withProdId(Long prodId) {
        this.prodId = prodId;
        return this;
    }

    public VerlaufTestBuilder withStandortTypRefId(Long standortTypRefId) {
        this.standortTypRefId = standortTypRefId;
        return this;
    }

    public VerlaufTestBuilder withInterneArbeit(boolean interneArbeit) {
        this.interneArbeit = interneArbeit;
        return this;
    }

    public VerlaufTestBuilder withVpn(boolean addVpn) {
        this.addVpn = addVpn;
        return this;
    }

    public VerlaufTestBuilder withFfmAbteilung(boolean ffmAbteilung) {
        this.ffmAbteilung = ffmAbteilung;
        return this;
    }

    public VerlaufTestBuilder withHousing(boolean housing) {
        this.addHousing = housing;
        return this;
    }

    public VerlaufTestBuilder withCountOfDialNumbers(int countOfDialNumbers) {
        this.countOfDialNumbers = countOfDialNumbers;
        return this;
    }

    public VerlaufTestBuilder withGeneratedTaifunData(GeneratedTaifunData generatedTaifunData) {
        this.generatedTaifunData = generatedTaifunData;
        return this;
    }

    public VerlaufTestBuilder withHwOltChild(HWOltChild hwOltChild) {
        this.hwOltChild = hwOltChild;
        return this;
    }

    public VerlaufTestBuilder withVoIP(boolean addVoip) {
        this.addVoip = addVoip;
        return this;
    }

    public class CreatedData {
        public final Verlauf verlauf;
        public final Auftrag auftrag;
        public final AuftragDaten auftragDaten;

        public CreatedData(Verlauf verlauf, Auftrag auftrag, AuftragDaten auftragDaten) {
            this.verlauf = verlauf;
            this.auftrag = auftrag;
            this.auftragDaten = auftragDaten;
        }
    }

}
