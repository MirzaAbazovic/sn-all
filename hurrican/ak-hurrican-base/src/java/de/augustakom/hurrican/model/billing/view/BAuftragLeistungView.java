/**
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 09.06.2004 08:57:19
 */
package de.augustakom.hurrican.model.billing.view;


import java.util.*;
import javax.persistence.*;
import com.google.common.base.Predicate;
import org.apache.commons.lang.StringUtils;

import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.common.tools.lang.NumberTools;
import de.augustakom.hurrican.model.billing.AbstractBillingModel;
import de.augustakom.hurrican.model.billing.BAuftrag;
import de.augustakom.hurrican.model.billing.BAuftragPos;
import de.augustakom.hurrican.model.billing.BillingConstants;
import de.augustakom.hurrican.model.billing.Leistung;
import de.augustakom.hurrican.model.billing.OE;
import de.augustakom.hurrican.model.billing.ServiceValue;
import de.augustakom.hurrican.model.shared.iface.ExternLeistungAwareModel;
import de.augustakom.hurrican.model.shared.iface.ExternMiscAwareModel;
import de.augustakom.hurrican.model.shared.iface.ExternProduktAwareModel;
import de.augustakom.hurrican.model.shared.iface.KundenModel;


/**
 * View-Modell, um (Billing-)Auftrags- und Leistungsdaten fuer einen Kunden darzustellen.
 */
@Entity
public class BAuftragLeistungView extends AbstractBillingModel implements KundenModel,
        ExternProduktAwareModel, ExternLeistungAwareModel, ExternMiscAwareModel {

    private Long itemNo = null;
    private Long itemNoOrig = null;
    private Long kundeNo = null;
    private String atyp = null;
    private Integer astatus = null;
    private Long auftragNo = null;
    private Long auftragNoOrig = null;
    private String oldAuftragNoOrig = null;
    private Long menge = null;
    private Integer bundleOrderNo = null;
    private Long leistungNo = null;
    private Long leistungNoOrig = null;
    private String serviceValueParam = null;
    private Long externMiscNo = null;
    private Long externProduktNo = null;
    private Long externLeistungNo = null;
    private Long oeNo = null;
    private Long oeNoOrig = null;
    private String oeName = null;
    public static final String LEISTUNG_NAME = "leistungName";
    private String leistungName = null;
    private String auftragHistStatus = null;
    private Date auftragGueltigVon = null;
    private Date auftragGueltigBis = null;
    public static final String POS_GUELTIG_VON = "auftragPosGueltigVon";
    private Date auftragPosGueltigVon = null;
    public static final String POS_GUELTIG_BIS = "auftragPosGueltigBis";
    private Date auftragPosGueltigBis = null;
    private String auftragPosParameter = null;
    private Date auftragPosChargedUntil = null;
    private Long rechInfoNoOrig = null;
    private String freeText;

    /**
     * Predicate, um nicht-gekuendigte Positionen zu ermitteln
     */
    public static Predicate<BAuftragLeistungView> filterNotCancelled() {
        return new Predicate<BAuftragLeistungView>() {
            @Override
            public boolean apply(BAuftragLeistungView input) {
                return input.isActiveAuftragpos();
            }
        };
    }

    /**
     * Predicate, um gekuendigte Positionen zu ermitteln
     */
    public static Predicate<BAuftragLeistungView> filterCancelled() {
        return new Predicate<BAuftragLeistungView>() {
            @Override
            public boolean apply(BAuftragLeistungView input) {
                return input.getAuftragPosGueltigBis() != null
                        && DateTools.isDateBefore(input.getAuftragPosGueltigBis(), DateTools.getBillingEndDate());
            }
        };
    }

    /**
     * Predicate, um die gekuendigten Positionen mit einem bestimmten Kuendigungsdatum zu ermitteln
     */
    public static Predicate<BAuftragLeistungView> filterCancelled(final Date cancelDate) {
        return new Predicate<BAuftragLeistungView>() {
            @Override
            public boolean apply(BAuftragLeistungView input) {
                return DateTools.isDateEqual(input.getAuftragPosGueltigBis(), cancelDate);
            }
        };
    }

    /**
     * Predicate, um die Positionen mit einer bestimmten Leistungsnummer zu ermitteln.
     */
    public static Predicate<BAuftragLeistungView> filterByServiceNoOrig(final Long serviceNoOrig) {
        return new Predicate<BAuftragLeistungView>() {
            @Override
            public boolean apply(BAuftragLeistungView input) {
                return NumberTools.equal(input.getLeistungNoOrig(), serviceNoOrig);
            }
        };
    }

    @Transient
    public String displayAsText() {
        if (getAuftragPosGueltigBis() != null) {
            // Leistungsname (von: dd.MM.yyyy; bis: dd.MM.yyyy)
            return String.format("%s (von %2$td.%2$tm.%2$tY; bis %3$td.%3$tm.%3$tY)",
                    getLeistungName(), getAuftragPosGueltigVon(), getAuftragPosGueltigBis());
        }
        return String.format("%s (von %2$td.%2$tm.%2$tY)",
                getLeistungName(), getAuftragPosGueltigVon());
    }

    @Transient
    public boolean isActiveAuftragpos() {
        return ((auftragPosGueltigBis == null) || DateTools.isDateEqual(auftragPosGueltigBis,
                DateTools.getBillingEndDate()));
    }

    @Transient
    public boolean isActiveAt(Date toCheck) {
        Date to = (auftragPosGueltigBis != null) ? auftragPosGueltigBis : DateTools.getBillingEndDate();
        return DateTools.isDateBetween(toCheck, auftragPosGueltigVon, to);
    }

    /**
     * @return true wenn es sich um eine einmalige Leistung handelt
     */
    @Transient
    public boolean isEinmalig() {
        return DateTools.isDateEqual(auftragPosGueltigVon, auftragPosGueltigBis);
    }

    /**
     * Ermittelt die Menge an Leistungen einer Auftragsposition unabhängig davon ob eine Werteliste hinterlegt ist oder
     * nicht Bsp.: Werteliste = "50", Menge = 2 => 100 Werteliste = "" oder null, Menge = 2 => 2 Werteliste = "asdf",
     * Menge = 2 => 2 Menge = null => 0
     *
     * @return die Anzahl an tatsaechlichen Leistungen je Auftragsposition
     */
    public Long calculateLeistungsMenge() {
        long menge = (this.getMenge() != null) ? this.getMenge() : 0;
        final boolean leistungMitWerteliste = StringUtils.isNotEmpty(this.getServiceValueParam());
        if (leistungMitWerteliste) {
            Long wertelisteWert = null;
            try {
                wertelisteWert = Long.valueOf(this.getServiceValueParam());
            }
            //Werteliste muss nicht zwingend eine Zahl beinhalten
            catch (NumberFormatException e) {
                wertelisteWert = 1L;
            }
            menge = menge * wertelisteWert;
        }
        return menge;
    }


    @Deprecated
    public BAuftragLeistungView() {
    }

    /**
     * Konstruktor mit Angabe der Objekte, aus denen die View erstellt werden kann.
     *
     * @param auftrag
     * @param oe
     * @param ap
     * @param leistung
     */
    public BAuftragLeistungView(BAuftrag auftrag, OE oe, BAuftragPos ap, Leistung leistung) {
        if ((auftrag == null) || (ap == null) || (leistung == null) || (oe == null)) {
            throw new IllegalArgumentException(
                    "Nicht alle notwendigen Auftrags- Positions- od. Leistungsdaten angegeben!");
        }
        init(auftrag, oe, ap, leistung, null);
    }


    public static BAuftragLeistungView createBAuftragLeistungView(BAuftragPos pos, Leistung l) {
        BAuftrag ba = new BAuftrag();
        OE oe = new OE();
        BAuftragLeistungView balv = new BAuftragLeistungView(ba, oe, pos, l);
        balv.setMenge(1L);
        return balv;
    }

    /**
     * Konstruktor mit Angabe der Objekte, aus denen die View erstellt werden kann.
     *
     * @param auftrag
     * @param oe
     * @param ap
     * @param leistung
     * @param sv
     */
    public BAuftragLeistungView(BAuftrag auftrag, OE oe, BAuftragPos ap, Leistung leistung, ServiceValue sv) {
        if ((auftrag == null) || (ap == null) || (leistung == null) || (oe == null)) {
            throw new IllegalArgumentException(
                    "Nicht alle notwendigen Auftrags- Positions- od. Leistungsdaten angegeben!");
        }
        init(auftrag, oe, ap, leistung, sv);
    }

    private void init(BAuftrag auftrag, OE oe, BAuftragPos ap, Leistung leistung, ServiceValue sv) {
        setKundeNo(auftrag.getKundeNo());
        setAtyp(auftrag.getAtyp());
        setAstatus(auftrag.getAstatus());
        setAuftragNo(auftrag.getAuftragNo());
        setAuftragNoOrig(auftrag.getAuftragNoOrig());
        setOldAuftragNoOrig(auftrag.getOldAuftragNoOrig());
        setBundleOrderNo(auftrag.getBundleOrderNo());
        setAuftragGueltigVon(auftrag.getGueltigVon());
        setAuftragGueltigBis(auftrag.getGueltigBis());
        setAuftragHistStatus(auftrag.getHistStatus());
        setRechInfoNoOrig(auftrag.getRechInfoNoOrig());
        setItemNo(ap.getItemNo());
        setItemNoOrig(ap.getItemNoOrig());
        setMenge(ap.getMenge());
        setAuftragPosGueltigVon(ap.getChargeFrom());
        setAuftragPosGueltigBis(ap.getChargeTo());
        setAuftragPosChargedUntil(ap.getChargedUntil());
        setLeistungNoOrig(ap.getLeistungNoOrig());
        setAuftragPosParameter(ap.getParameter());
        setFreeText(ap.getFreeText());
        setOeNo(oe.getOeNo());
        setOeNoOrig(oe.getOeNoOrig());
        setOeName(oe.getName());
        if (leistung != null) {
            setLeistungNo(leistung.getLeistungNo());
            setLeistungName(leistung.getName());
            setExternProduktNo(leistung.getExternProduktNo());
            setExternMiscNo(leistung.getExternMiscNo());
            setExternLeistungNo(leistung.getExternLeistungNo());

            if (sv != null) {
                setExternProduktNo((sv.getExternProduktNo() != null) ? sv.getExternProduktNo() : getExternProduktNo());
                setExternMiscNo((sv.getExternMiscNo() != null) ? sv.getExternMiscNo() : getExternMiscNo());
                setExternLeistungNo((sv.getExternLeistungNo() != null) ? sv.getExternLeistungNo() : getExternLeistungNo());
            }
        }
    }

    /**
     * Prueft, ob die referenzierte Auftragsposition gekuendigt ist oder in der Zukunft gekuendigt wird. Dies ist dann
     * der Fall, wenn einer der folgenden Faelle zutrifft: <br> <ul> <li>Gueltig-Bis von Auftragsposition ist nicht
     * 'null' UND kleiner '31.12.9999' <li>Gueltig-Bis von Auftragsposition ist 'null' UND Auftragstyp='KUEND' UND
     * Auftragsstatus in (1,2) </ul>
     *
     * @return
     *
     */
    @Transient
    public boolean isAuftragPosInKuendigung() {
        if ((getAuftragPosGueltigBis() != null) &&
                DateTools.isDateBefore(getAuftragPosGueltigBis(), DateTools.getBillingEndDate())) {
            return true;
        }
        else if ((getAuftragPosGueltigBis() == null) &&
                StringUtils.equals(getAtyp(), BillingConstants.ATYP_KUEND) &&
                NumberTools.isIn(getAstatus(), new Number[] { BAuftrag.STATUS_EINGEGANGEN, BAuftrag.STATUS_ERFASST })) {
            return true;
        }

        return false;
    }

    @Id
    @Column(name = "ITEM_NO")
    public Long getItemNo() {
        return this.itemNo;
    }

    public void setItemNo(Long itemNo) {
        this.itemNo = itemNo;
    }

    @Column(name = "ITEM__NO")
    public Long getItemNoOrig() {
        return this.itemNoOrig;
    }

    public void setItemNoOrig(Long itemNoOrig) {
        this.itemNoOrig = itemNoOrig;
    }

    @Column(name = "ATYP")
    public String getAtyp() {
        return atyp;
    }

    public void setAtyp(String atyp) {
        this.atyp = atyp;
    }

    @Column(name = "ASTATUS")
    public Integer getAstatus() {
        return this.astatus;
    }

    public void setAstatus(Integer astatus) {
        this.astatus = astatus;
    }

    @Column(name = "AUFTRAG__NO")
    public Long getAuftragNoOrig() {
        return auftragNoOrig;
    }

    public void setAuftragNoOrig(Long auftragNoOrig) {
        this.auftragNoOrig = auftragNoOrig;
    }

    @Column(name = "AUFTRAG_NO")
    public Long getAuftragNo() {
        return this.auftragNo;
    }

    public void setAuftragNo(Long auftragNo) {
        this.auftragNo = auftragNo;
    }

    @Column(name = "BUNDLE_AUFTRAG__NO")
    public Integer getBundleOrderNo() {
        return bundleOrderNo;
    }

    public void setBundleOrderNo(Integer buendelOrderNo) {
        this.bundleOrderNo = buendelOrderNo;
    }

    @Column(name = "FREE_TEXT")
    public String getFreeText() {
        return freeText;
    }

    public void setFreeText(String freeText) {
        this.freeText = freeText;
    }

    @Override
    @Column(name = "CUST_NO")
    public Long getKundeNo() {
        return kundeNo;
    }

    @Override
    public void setKundeNo(Long kundeNo) {
        this.kundeNo = kundeNo;
    }

    @Column(name = "QUANTITY")
    public Long getMenge() {
        return menge;
    }

    public void setMenge(Long menge) {
        this.menge = menge;
    }

    @Column(name = "VALID_TO")
    public Date getAuftragGueltigBis() {
        return auftragGueltigBis;
    }

    public void setAuftragGueltigBis(Date auftragGueltigBis) {
        this.auftragGueltigBis = auftragGueltigBis;
    }

    @Column(name = "VALID_FROM")
    public Date getAuftragGueltigVon() {
        return auftragGueltigVon;
    }

    public void setAuftragGueltigVon(Date auftragGueltigVon) {
        this.auftragGueltigVon = auftragGueltigVon;
    }

    @Column(name = "HIST_STATUS")
    public String getAuftragHistStatus() {
        return auftragHistStatus;
    }

    public void setAuftragHistStatus(String auftragsHistStatus) {
        this.auftragHistStatus = auftragsHistStatus;
    }

    /**
     * Gibt die OE__NO des zugehoerigen Produkts zurueck.
     *
     * @return Returns the oeNoOrig.
     */
    @Column(name = "OE_NO")
    public Long getOeNo() {
        return this.oeNo;
    }

    public void setOeNo(Long oeNo) {
        this.oeNo = oeNo;
    }

    @Column(name = "OE__NO")
    public Long getOeNoOrig() {
        return this.oeNoOrig;
    }

    public void setOeNoOrig(Long oeNoOrig) {
        this.oeNoOrig = oeNoOrig;
    }

    @Column(name = "OE_NAME")
    public String getOeName() {
        return this.oeName;
    }

    public void setOeName(String oeName) {
        this.oeName = oeName;
    }

    @Column(name = "LEISTUNG_NAME")
    public String getLeistungName() {
        return this.leistungName;
    }

    public void setLeistungName(String leistungName) {
        this.leistungName = leistungName;
    }

    @Column(name = "CHARGE_TO")
    public Date getAuftragPosGueltigBis() {
        return this.auftragPosGueltigBis;
    }

    public void setAuftragPosGueltigBis(Date auftragPosGueltigBis) {
        this.auftragPosGueltigBis = auftragPosGueltigBis;
    }

    @Column(name = "CHARGE_FROM")
    public Date getAuftragPosGueltigVon() {
        return this.auftragPosGueltigVon;
    }

    public void setAuftragPosGueltigVon(Date auftragPosGueltigVon) {
        this.auftragPosGueltigVon = auftragPosGueltigVon;
    }

    @Override
    @Column(name = "EXT_MISC__NO")
    public Long getExternMiscNo() {
        return this.externMiscNo;
    }

    public void setExternMiscNo(Long externMiscNo) {
        this.externMiscNo = externMiscNo;
    }

    @Override
    @Column(name = "EXT_PRODUKT__NO")
    public Long getExternProduktNo() {
        return this.externProduktNo;
    }

    public void setExternProduktNo(Long externProduktNo) {
        this.externProduktNo = externProduktNo;
    }

    @Override
    @Column(name = "EXT_LEISTUNG__NO")
    public Long getExternLeistungNo() {
        return this.externLeistungNo;
    }

    public void setExternLeistungNo(Long externLeistungNo) {
        this.externLeistungNo = externLeistungNo;
    }

    @Column(name = "PARAMETER")
    public String getAuftragPosParameter() {
        return this.auftragPosParameter;
    }

    public void setAuftragPosParameter(String auftragPosParameter) {
        this.auftragPosParameter = auftragPosParameter;
    }

    @Column(name = "BILL_SPEC_NO")
    public Long getRechInfoNoOrig() {
        return this.rechInfoNoOrig;
    }

    public void setRechInfoNoOrig(Long rechInfoNoOrig) {
        this.rechInfoNoOrig = rechInfoNoOrig;
    }

    @Column(name = "LEISTUNG_NO")
    public Long getLeistungNo() {
        return this.leistungNo;
    }

    public void setLeistungNo(Long leistungNo) {
        this.leistungNo = leistungNo;
    }

    @Column(name = "LEISTUNG__NO")
    public Long getLeistungNoOrig() {
        return this.leistungNoOrig;
    }

    public void setLeistungNoOrig(Long leistungNoOrig) {
        this.leistungNoOrig = leistungNoOrig;
    }

    @Column(name = "OLD_SERVICE__NO")
    public String getOldAuftragNoOrig() {
        return this.oldAuftragNoOrig;
    }

    public void setOldAuftragNoOrig(String oldAuftragNoOrig) {
        this.oldAuftragNoOrig = oldAuftragNoOrig;
    }

    @Column(name = "SERVICE_VALUE")
    public String getServiceValueParam() {
        return this.serviceValueParam;
    }

    public void setServiceValueParam(String serviceValueParam) {
        this.serviceValueParam = serviceValueParam;
    }

    @Column(name = "CHARGED_UNTIL")
    public Date getAuftragPosChargedUntil() {
        return auftragPosChargedUntil;
    }

    public void setAuftragPosChargedUntil(Date auftragPosChargedUntil) {
        this.auftragPosChargedUntil = auftragPosChargedUntil;
    }

}
