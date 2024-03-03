/**
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 24.08.2009 15:12:37
 */
package de.augustakom.hurrican.model.cc;

import de.augustakom.common.model.ReferencedEntityId;
import de.augustakom.common.service.iface.IServiceObject;
import de.augustakom.hurrican.model.cc.hardware.HWSwitch;

/**
 *
 */
@SuppressWarnings("unused")
public class ProduktBuilder extends AbstractCCIDModelBuilder<ProduktBuilder, Produkt> implements IServiceObject {

    @ReferencedEntityId("produktGruppeId")
    private ProduktGruppeBuilder produktGruppeBuilder;
    private Long produktGruppeId;
    private String produktNr = randomInt(Integer.MAX_VALUE / 2, Integer.MAX_VALUE).toString();
    private String anschlussart;
    private String productNamePattern;
    private Integer leitungsart;
    private Short aktionsId = 0;
    private Integer minDnCount = 0;
    private Integer maxDnCount = 0;
    private Boolean dnBlock;
    private Long dnTyp;
    private Boolean brauchtDN4Account;
    private Boolean auftragserstellung;
    private Boolean leitungsNrAnlegen;
    private Boolean buendelProdukt;
    private Boolean buendelBillingHauptauftrag = Boolean.FALSE;
    private Boolean elVerlauf;
    private Integer endstellenTyp = Produkt.ES_TYP_A_UND_B;
    private String beschreibung = "TestProdukt";
    private String accountVorsatz;
    private Integer liNr;
    private Boolean vpnPhysik;
    private Boolean projektierung;
    private Boolean isParent = Boolean.FALSE;
    private Boolean checkChild = Boolean.FALSE;
    private Boolean isCombiProdukt;
    private Boolean autoProductChange;
    private Boolean exportKdpM;
    private Boolean createKdpAccountReport;
    private Boolean exportAKProdukt;
    private Long verteilungDurch;
    private Boolean baRuecklaeufer;
    private Boolean brauchtVpiVci;
    private Long projektierungChainId;
    private Long verlaufChainId;
    private Long verlaufCancelChainId;
    private Boolean createAPAddress;
    private Boolean baTerminVerschieben;
    private Boolean assignIad;
    private Boolean cpsProvisioning;
    private String cpsProductName;
    private Boolean cpsDSLProduct;
    private String cpsAccountType;
    private Boolean cpsMultiDraht;
    private String vbzKindOfUseProduct;
    private String vbzKindOfUseType;
    private Boolean isVierDraht = Boolean.FALSE;
    private Boolean cpsIPDefault = Boolean.FALSE;
    private Long ipPool;
    private Integer ipNetmaskSizeV4;
    private Integer ipNetmaskSizeV6;
    private Reference ipPurposeV4;
    private Boolean ipPurposeV4Editable;
    private Boolean automationPossible = Boolean.FALSE;
    private HWSwitch hwSwitch;
    private GeoIdSource geoIdSource = GeoIdSource.HVT;
    private SdslNdraht sdslNdraht;
    private Boolean autoHvtZuordnung = Boolean.TRUE;
    private Boolean smsVersand = Boolean.TRUE;
    private Long erstellStatusId;
    private Long kuendigungStatusId;
    private boolean sendStatusUpdates = true;

    public ProduktBuilder withSdslNdraht(final SdslNdraht sdslNdraht) {
        this.sdslNdraht = sdslNdraht;
        return this;
    }

    @Override
    protected void initialize() {
        id = getLongId();
    }

    public ProduktBuilder withBATerminVerschieben(final Boolean baTerminVerschieben) {
        this.baTerminVerschieben = baTerminVerschieben;
        return this;
    }

    public ProduktBuilder withGeoIdSource(final GeoIdSource geoIdSource) {
        this.geoIdSource = geoIdSource;
        return this;
    }

    public ProduktBuilder withIpPurposeV4(Reference ipPurposeV4) {
        this.ipPurposeV4 = ipPurposeV4;
        return this;
    }

    public ProduktBuilder withIpPurposeV4Editable(Boolean ipPurposeV4Editable) {
        this.ipPurposeV4Editable = ipPurposeV4Editable;
        return this;
    }

    public ProduktBuilder withCpsProvisioning(Boolean cpsProvisioning) {
        this.cpsProvisioning = cpsProvisioning;
        return this;
    }

    public ProduktBuilder withMinDnCount(Integer minDnCount) {
        this.minDnCount = minDnCount;
        return this;
    }

    public ProduktBuilder withMaxDnCount(Integer maxDnCount) {
        this.maxDnCount = maxDnCount;
        return this;
    }

    public ProduktBuilder withDNBlock(Boolean dnBlock) {
        this.dnBlock = dnBlock;
        return this;
    }

    public ProduktBuilder withDnTyp(Long dnTyp) {
        this.dnTyp = dnTyp;
        return this;
    }

    public ProduktBuilder withAnschlussart(String anschlussart) {
        this.anschlussart = anschlussart;
        return this;
    }

    public ProduktBuilder withCpsDSLProduct(Boolean cpsDSLProduct) {
        this.cpsDSLProduct = cpsDSLProduct;
        return this;
    }

    public ProduktBuilder withCpsAccountType(String cpsAccountType) {
        this.cpsAccountType = cpsAccountType;
        return this;
    }

    public ProduktBuilder withLiNr(Integer liNr) {
        this.liNr = liNr;
        return this;
    }

    public ProduktBuilder withCpsProductName(String cpsProductName) {
        this.cpsProductName = cpsProductName;
        return this;
    }

    public ProduktBuilder withVbzKindOfUseProduct(String vbzKindOfUseProduct) {
        this.vbzKindOfUseProduct = vbzKindOfUseProduct;
        return this;
    }

    public ProduktBuilder withVbzKindOfUseType(String vbzKindOfUseType) {
        this.vbzKindOfUseType = vbzKindOfUseType;
        return this;
    }

    public ProduktBuilder withLeitungsNrAnlegen(Boolean leitungsNrAnlegen) {
        this.leitungsNrAnlegen = leitungsNrAnlegen;
        return this;
    }

    public ProduktBuilder withProduktGruppeBuilder(ProduktGruppeBuilder produktGruppeBuilder) {
        this.produktGruppeBuilder = produktGruppeBuilder;
        return this;
    }

    public ProduktBuilder withIsVierDraht(Boolean isVierDraht) {
        this.isVierDraht = isVierDraht;
        return this;
    }

    public ProduktBuilder withCreateAPAddress(Boolean createAPAddress) {
        this.createAPAddress = createAPAddress;
        return this;
    }

    public ProduktBuilder withElVerlauf(boolean elVerlauf) {
        this.elVerlauf = elVerlauf;
        return this;
    }

    public ProduktBuilder withIsParent(boolean isParent) {
        this.isParent = isParent;
        return this;
    }

    public ProduktBuilder withCpsMultiDraht(Boolean cpsMultiDraht) {
        this.cpsMultiDraht = cpsMultiDraht;
        return this;
    }

    public ProduktBuilder withCpsIPDefault(Boolean cpsIPDefault) {
        this.cpsIPDefault = cpsIPDefault;
        return this;
    }

    public ProduktBuilder withProduktGruppeId(Long produktGruppeId) {
        this.produktGruppeId = produktGruppeId;
        return this;
    }

    public ProduktBuilder withIpPool(Long ipPool) {
        this.ipPool = ipPool;
        return this;
    }

    public ProduktBuilder withEndstellenTyp(Integer eTyp) {
        this.endstellenTyp = eTyp;
        return this;
    }

    public ProduktBuilder withAuftragserstellung(Boolean auftragserstellung) {
        this.auftragserstellung = auftragserstellung;
        return this;
    }

    public ProduktBuilder withBuendelProdukt(Boolean buendelProdukt) {
        this.buendelProdukt = buendelProdukt;
        return this;
    }

    public ProduktBuilder withBuendelBillingHauptauftrag(Boolean buendelBillingHauptauftrag) {
        this.buendelBillingHauptauftrag = buendelBillingHauptauftrag;
        return this;
    }

    public ProduktBuilder withIpNetmaskSizeV4(Integer ipNetmaskSizeV4) {
        this.ipNetmaskSizeV4 = ipNetmaskSizeV4;
        return this;
    }

    public ProduktBuilder withIpNetmaskSizeV6(Integer ipNetmaskSizeV6) {
        this.ipNetmaskSizeV6 = ipNetmaskSizeV6;
        return this;
    }

    public ProduktBuilder withAutomationPossible() {
        this.automationPossible = Boolean.TRUE;
        return this;
    }

    public ProduktBuilder withHwSwitch(HWSwitch hwSwitch) {
        this.hwSwitch = hwSwitch;
        return this;
    }

    public ProduktBuilder withAutoHvtZuordnung(Boolean autoHvtZuordnung) {
        this.autoHvtZuordnung = autoHvtZuordnung;
        return this;
    }

    public ProduktBuilder withSmsVersand(Boolean smsVersand) {
        this.smsVersand = smsVersand;
        return this;
    }

    public ProduktBuilder withErstellStatusId(Long erstellStatusId) {
        this.erstellStatusId = erstellStatusId;
        return this;
    }

    public ProduktBuilder withKuendigungStatusId(Long kuendigungStatusId) {
        this.kuendigungStatusId = kuendigungStatusId;
        return this;
    }

    public ProduktBuilder withSendStatusUpdates(boolean sendStatusUpdates) {
        this.sendStatusUpdates = sendStatusUpdates;
        return this;
    }

    public ProduktBuilder withProductNamePattern(String productNamePattern) {
        this.productNamePattern = productNamePattern;
        return this;
    }
}
